package ca.concordia.soen;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jface.text.Document;

public class CodeAnalyzer {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java CodeAnalyzer <directory>");
            return;
        }
        Path directoryPath = Paths.get(args[0]);
        Files.walkFileTree(directoryPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                if (path.toString().endsWith(".java")) {
                    // Process the file
                    System.out.println("Processing file: " + path);
                    Document document = new Document(Files.readString(path));

                    // Parse the code and generate the AST
                    ASTParser parser = ASTParser.newParser(AST.JLS12);
                    parser.setSource(document.get().toCharArray());
                    CompilationUnit cu = (CompilationUnit) parser.createAST(null);

                    // Create a visitor to analyze the AST
                    NestedTryVisitori visitor = new NestedTryVisitori(cu, path.toString());
                    cu.accept(visitor);

                    // Print the results
                    System.out.println("Total number of methods: " + visitor.getMethodCount());
                    System.out.println("Number of methods with nested try statements: " + visitor.getTryCount());
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }
}

class NestedTryVisitori extends ASTVisitor {
    private int methodCount;
    private int tryCount;
    private CompilationUnit cu;
    private String filePath;

    public NestedTryVisitori(CompilationUnit cu, String filePath) {
        this.cu = cu;
        this.filePath = filePath;
    }

    @Override
    public boolean visit(MethodDeclaration node) {
        methodCount++;
        return super.visit(node);
    }

    @Override
    public boolean visit(TryStatement node) {
        ASTNode parent = node.getParent();
        while (parent != null) {
            if (parent.getNodeType() == ASTNode.TRY_STATEMENT) {
                System.out.println("Nested try detected at line " + cu.getLineNumber(node.getStartPosition()) +
                        " in file " + filePath);
                tryCount++;
                break;

            }
            parent = parent.getParent();
        }
        return super.visit(node);
    }

    public int getMethodCount() {
        return methodCount;
    }

    public int getTryCount() {
        return tryCount;
    }
}

