package flowmetrics;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class NestedTryDetector {

    public static void main(String[] args) throws IOException, CoreException {
        if (args.length < 1) {
            System.out.println("Please provide a path to a folder as an argument.");
            return;
        }

        String folderPath = args[0];
        File folder = new File(folderPath);
        if (!folder.isDirectory()) {
            System.out.println("The specified path is not a folder.");
            return;
        }

        List<File> javaFiles = collectJavaFiles(folder);
        for (File javaFile : javaFiles) {
            processJavaFile(javaFile);
        }
    }

    private static List<File> collectJavaFiles(File folder) {
        List<File> javaFiles = new ArrayList<>();
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                javaFiles.addAll(collectJavaFiles(file));
            } else if (file.getName().endsWith(".java")) {
                javaFiles.add(file);
            }
        }
        return javaFiles;
    }
    
    
    private static void processJavaFile(File javaFile) throws IOException, CoreException {
        String source = Files.readString(Path.of(javaFile.getPath()));

        ASTParser parser = ASTParser.newParser(AST.JLS14);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setSource(source.toCharArray());

        CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);

        List<TryStatement> nestedTryStatements = findNestedTryStatements(compilationUnit);

        if (!nestedTryStatements.isEmpty()) {
            System.out.println("Processing file " + javaFile.getPath() + ":");
            double percentageHandled = 0;
            FileWriter writer = new FileWriter(new File("C:\\Users\\Admin\\Downloads\\ActionsNestedTry.csv"), true);
            writer.write("File, Possible Exceptions, Handled Exceptions, Percentage Handled\n");
            for (TryStatement tryStatement : nestedTryStatements) {
                System.out.println("Nested try found at line " + compilationUnit.getLineNumber(tryStatement.getStartPosition()));
                List<ASTNode> possibleExceptions = getPossibleExceptions(tryStatement);
                int numPossibleExceptions = possibleExceptions.size();
                int numHandledExceptions = tryStatement.catchClauses().size();
                int numUnhandledExceptions = numPossibleExceptions - numHandledExceptions;
                if (numPossibleExceptions != 0)
                    percentageHandled = (double) numHandledExceptions / numPossibleExceptions * 100.0;
                else if (numPossibleExceptions == 0)
                	percentageHandled = (double) 0 * 100.0;
                System.out.println("Number of possible exceptions: " + numPossibleExceptions);
                System.out.println("Number of handled exceptions: " + numHandledExceptions);
                System.out.println("Number of unhandled exceptions: " + numUnhandledExceptions);
                System.out.println("Percentage of handled exceptions: " + percentageHandled + "%");
                System.out.println();
                String str=javaFile.getPath().substring(49);
                writer.write(str + "," + numPossibleExceptions + "," + numHandledExceptions + "," + percentageHandled + "\n");
                writer.flush();
            }
            writer.close();
        }
    }



    private static List<TryStatement> findNestedTryStatements(CompilationUnit compilationUnit) {
        List<TryStatement> nestedTryStatements = new ArrayList<>();
        compilationUnit.accept(new ASTVisitor() {
            private List<TryStatement> enclosingTryStatements = new ArrayList<>();

            @Override
            public boolean visit(TryStatement node) {
                if (!enclosingTryStatements.isEmpty()) {
                    nestedTryStatements.add(node);
                }
                enclosingTryStatements.add(node);
                return true;
            }


            @Override
            public void endVisit(TryStatement node) {
                enclosingTryStatements.remove(enclosingTryStatements.size() - 1);
            }
        });
        return nestedTryStatements;
    }
    
    private static List<ASTNode> getPossibleExceptions(TryStatement tryStatement) {
        List<ASTNode> possibleExceptions = new ArrayList<>();
        for (Object o : tryStatement.catchClauses()) {
            CatchClause catchClause = (CatchClause) o;
            SingleVariableDeclaration exception = catchClause.getException();
            if (exception != null) {
                Type exceptionType = exception.getType();
                possibleExceptions.add(exceptionType);
            }
        }
        return possibleExceptions;
    }


}
