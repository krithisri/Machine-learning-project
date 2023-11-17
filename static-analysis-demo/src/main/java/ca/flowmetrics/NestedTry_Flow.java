package flowmetrics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Stack;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jface.text.Document;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;



class NestedTryVisitor extends ASTVisitor {
    private int totalTryBlocks;
    private int nestedTryBlocks;
    private int totalCatchHandlers;
    private int nestedCatchHandlers;

    private String filePath;

    public NestedTryVisitor() {
    }

    public void analyzeCode(String filePath) {
        this.filePath = filePath;

        try {
            String code = readFile(filePath);
            CompilationUnit cu = parse(code);
            cu.accept(this);
        } catch (IOException e) {
            System.err.println("Error reading file " + filePath + ": " + e.getMessage());
        }
    }

    private CompilationUnit parse(String code) {
        ASTParser parser = ASTParser.newParser(AST.JLS16);
        parser.setSource(code.toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);

        return (CompilationUnit) parser.createAST(null);
    }

    private String readFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readString(path);
    }

    @Override
    public boolean visit(TryStatement node) {
        totalTryBlocks++;

        ASTNode parent = node.getParent();
        while (parent != null) {
            if (parent.getNodeType() == ASTNode.TRY_STATEMENT) {
                //System.out.println("Nested try detected at line " + node.getStartPosition().getLineNumber() +
                  //      " in file " + filePath);
                //System.out.println(node);
                nestedTryBlocks++;
                break;
            }
            parent = parent.getParent();
        }

        return super.visit(node);
    }
    
    @Override
    public boolean visit(CatchClause node) {
        totalCatchHandlers++;

        ASTNode parent = node.getParent();
        while (parent != null) {
            if (parent.getNodeType() == ASTNode.TRY_STATEMENT) {
                TryStatement tryStatement = (TryStatement) parent;
                if (tryStatement.catchClauses().size() > 1) {
                    nestedCatchHandlers++;
                }
                break;
            }
            parent = parent.getParent();
        }

        return super.visit(node);
    }

    public int getNestedTryCount() {
        return nestedTryBlocks;
    }

    public int getTotalTryCount() {
        return totalTryBlocks;
    }

    public int getNestedCatchCount() {
        return nestedCatchHandlers;
    }

    public int getTotalCatchCount() {
        return totalCatchHandlers;
    }

    public double getPercentageOfHandlersAffected() {
        if (totalCatchHandlers == 0) {
            return 0;
        } else {
            return ((double) nestedCatchHandlers / totalCatchHandlers) * 100;
        }
    }
    public void resetCounts() {
        totalTryBlocks = 0;
        nestedTryBlocks = 0;
        totalCatchHandlers = 0;
        nestedCatchHandlers = 0;
    }
    
}

