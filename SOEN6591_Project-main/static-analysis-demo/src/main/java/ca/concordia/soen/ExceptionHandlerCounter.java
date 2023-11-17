package ca.concordia.soen;
import java.io.File;
import java.io.IOException;
import org.eclipse.jdt.core.dom.*;

public class ExceptionHandlerCounter {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java ExceptionHandlerAnalyzer <folder_path>");
            System.exit(1);
        }
        String folderPath = args[0];
        File folder = new File(folderPath);
        if (!folder.isDirectory()) {
            System.err.println(folderPath + " is not a directory.");
            System.exit(1);
        }

        File[] files = folder.listFiles();
        if (files == null) {
            System.err.println("Failed to list files in " + folderPath);
            System.exit(1);
        }

        ASTParser parser = ASTParser.newParser(AST.JLS11);
        ExceptionHandlerVisitor visitor = new ExceptionHandlerVisitor();

        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".java")) {
                String source = readFile(file.getAbsolutePath());
                parser.setSource(source.toCharArray());
                CompilationUnit cu = (CompilationUnit) parser.createAST(null);
                cu.accept(visitor);
            }
        }
    }

    private static String readFile(String filePath) {
        String content = "";
        try {
            content = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(filePath)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("ANUSHA");
        return content;
    }
}

class ExceptionHandlerVisitor extends ASTVisitor {

    @Override
    public boolean visit(CatchClause node) {
    	
        SingleVariableDeclaration catchParam = node.getException();
        ITypeBinding catchType = catchParam.getType().resolveBinding();
        ITypeBinding exceptionType = node.getException().getType().resolveBinding().getSuperclass();
        System.out.println("ANUSHA");
        if (catchType.isEqualTo(exceptionType)) {
            // Specific strategy detected
            System.out.println("Specific strategy detected for exception: " + catchType.getName());
        } else {
            // Subsumption strategy detected
            System.out.println("Subsumption strategy detected for exception: " + catchType.getName());
        }

        return super.visit(node);
    }
}
