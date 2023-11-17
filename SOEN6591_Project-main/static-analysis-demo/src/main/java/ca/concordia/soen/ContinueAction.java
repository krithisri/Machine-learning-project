package ca.concordia.soen;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.core.runtime.CoreException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ContinueAction{

    public static void main(String[] args) throws IOException, CoreException {
    	int num=0;
        double percentage=0;
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
        int numTryBlocks = 0;
        int numTryBlocksWithHandledExceptions = 0;
        FileWriter writer = new FileWriter(new File("C:\\Users\\Admin\\Downloads\\HandledTryBlocksWithContinue.csv"));
        writer.write("File, Possible Exceptions, Percentage Possible Exceptions\n");
        for (File javaFile : javaFiles) {
            int[] stats = processJavaFile(javaFile);
            numTryBlocks += stats[0];
            numTryBlocksWithHandledExceptions += stats[1];
            if(numTryBlocks!=0)
                percentage = 100.0 * numTryBlocksWithHandledExceptions / numTryBlocks;
            System.out.println("Total try blocks: " + numTryBlocks);
            System.out.println("Try blocks with handled exceptions: " + numTryBlocksWithHandledExceptions);
            System.out.printf("Percentage of try blocks with handled exceptions: %.2f\n", percentage);
            String str=javaFile.getPath();
            writer.write(str +"," + numTryBlocks+","+percentage + "\n");
            writer.flush();
            numTryBlocksWithHandledExceptions=0;
            numTryBlocks=0;
        }
        writer.close();
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
    
    private static int[] processJavaFile(File javaFile) throws IOException, CoreException {
        String source = Files.readString(Path.of(javaFile.getPath()));
         int num=0;
        ASTParser parser = ASTParser.newParser(AST.JLS14);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setSource(source.toCharArray());

        CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);

        List<TryStatement> tryBlocks = findTryBlocks(compilationUnit);

        int numTryBlocks = tryBlocks.size();
        int numTryBlocksWithHandledExceptions = 0;
        int numHandledExceptions = 0;

        if (!tryBlocks.isEmpty()) {
            for (TryStatement tryBlock : tryBlocks) {
                List<CatchClause> catchClauses = (List<CatchClause>) tryBlock.catchClauses();
                for (CatchClause catchClause : catchClauses) {
                    Block catchBlock = catchClause.getBody();
                    List<Statement> statements = (List<Statement>) catchBlock.statements();
                    for (Statement statement : statements) {
                        if (statement instanceof ContinueStatement) {
                            List<ASTNode> handledExceptions = getHandledExceptions(catchClause);
                            if (!handledExceptions.isEmpty()) {
                                numTryBlocksWithHandledExceptions++;
                                numHandledExceptions += handledExceptions.size();
                            }
                        }
                    }
                }
            }
        }

        return new int[] { numTryBlocks,numTryBlocksWithHandledExceptions };
    }

    private static List<TryStatement> findTryBlocks(CompilationUnit compilationUnit) {
        TryBlockVisitor tryBlockVisitor = new TryBlockVisitor();
        compilationUnit.accept(tryBlockVisitor);
        return tryBlockVisitor.getTryBlocks();
    }

    private static List<ASTNode> getHandledExceptions(CatchClause catchClause) {
        List<ASTNode> handledExceptions = new ArrayList<>();
        SingleVariableDeclaration exceptionDeclaration = catchClause.getException();
        if (exceptionDeclaration != null) {
        	Type exceptionType = exceptionDeclaration.getType();
        	handledExceptions.add(exceptionType);
        	System.out.println("size"+handledExceptions);
        }
        return handledExceptions;
    }


    private static class TryBlockVisitor extends ASTVisitor {
        private final List<TryStatement> tryBlocks = new ArrayList<>();

        @Override
        public boolean visit(TryStatement tryStatement) {
        	
            tryBlocks.add(tryStatement);
            return super.visit(tryStatement);
        }

        public List<TryStatement> getTryBlocks() {
            return tryBlocks;
        }
        
    }
}
