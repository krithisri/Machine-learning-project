package ca.concordia.soen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TryStatement;

public class CatchNested {
    public static void main(String[] args) throws IOException {
        // Parse the Java files in a folder
    	float percentage=0;
        String folderPath = "C:\\Users\\Admin\\Downloads\\kafka-trunk"; // Change this to the folder you want to analyze
        List<File> files = listFilesInFolder(folderPath, ".java");
        // Create the CSV file and write the header row
        FileWriter writer = new FileWriter(new File("C:\\Users\\Admin\\Downloads\\FlowNested.csv"));
        writer.write("File,Total methods,Methods with nested try, Percentage of methods with Nested try\n");
        for (File file : files) {
            String source = readFile(file.getPath());
            CompilationUnit cu = parseCompilationUnit(source);
            int totalMethods = 0;
            int methodsWithNestedTry = 0;
            for (Object obj : cu.types()) {
                if (obj instanceof org.eclipse.jdt.core.dom.TypeDeclaration) {
                    org.eclipse.jdt.core.dom.TypeDeclaration type = (org.eclipse.jdt.core.dom.TypeDeclaration) obj;
                    for (MethodDeclaration method : type.getMethods()) {
                        totalMethods++;
                        if (hasNestedTry(method)) {
                            methodsWithNestedTry++;
                        }
                    }
                }
            }

            /* Print the results for this file
            System.out.println("File: " + file.getName());
            System.out.println("Total number of methods: " + totalMethods);
            System.out.println("Number of methods with nested try anti-pattern: " + methodsWithNestedTry);
            System.out.println();*/
            if(totalMethods!=0)
            percentage =  ((float) methodsWithNestedTry / (float) totalMethods) * 100;

            // Write the results for this file to the CSV file
            String str=file.getPath().substring(49);
            writer.write(str + "," + totalMethods + "," + methodsWithNestedTry + ","+ percentage+ "\n");
        }
        // Close the CSV file
        writer.close();
    }

    private static String readFile(String fileName) throws IOException {
        byte[] bytes = Files.readAllBytes(new File(fileName).toPath());
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private static List<File> listFilesInFolder(String folderPath, String extension) {
        List<File> files = new ArrayList<>();
        File folder = new File(folderPath);
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                files.addAll(listFilesInFolder(file.getPath(), extension));
            } else if (file.getName().endsWith(extension)) {
                files.add(file);
            }
        }
        return files;
    }

    private static CompilationUnit parseCompilationUnit(String source) {
        ASTParser parser = ASTParser.newParser(AST.JLS14);
        parser.setSource(source.toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        return (CompilationUnit) parser.createAST(null);
    }

    private static boolean hasNestedTry(MethodDeclaration method) {
        List<TryStatement> tryStatements = new ArrayList<>();
        method.accept(new TryStatementVisitor(tryStatements));
        for (TryStatement tryStatement : tryStatements) {
            if (hasNestedTry(tryStatement)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasNestedTry(TryStatement tryStatement) {
        List<TryStatement> nestedTryStatements = new ArrayList<>();
        tryStatement.accept(new TryStatementVisitor(nestedTryStatements));
        return nestedTryStatements.size() > 1;
    }

    private static class TryStatementVisitor extends org.eclipse.jdt.core.dom.ASTVisitor {
        private final List<TryStatement> tryStatements;

        public TryStatementVisitor(List<TryStatement> tryStatements) {
            this.tryStatements = tryStatements;
        }

        @Override
        public boolean visit(TryStatement node) {
            tryStatements.add(node);
            return super.visit(node);
        }
    }
}
