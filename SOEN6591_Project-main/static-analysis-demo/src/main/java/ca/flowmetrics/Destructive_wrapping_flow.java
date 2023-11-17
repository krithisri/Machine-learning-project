package flowmetrics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;

public class Destructive_wrapping_flow {
	

	    public static void main(String[] args) throws IOException {
	        if (args.length == 0) {
	            System.err.println("Please provide a folder path as input.");
	            return;
	        }

	        File folder = new File(args[0]);
	        System.out.println(args[0]);
	        
	        if (!folder.exists()) {
	            System.err.println("The specified folder does not exist.");
	            return;
	        }

	        if (!folder.isDirectory()) {
	            System.err.println("The specified path is not a folder.");
	            return;
	        }

	        DestructiveWrapping visitor = new DestructiveWrapping();
	        analyzeFiles(folder, visitor);

	        /*double percentage = visitor.getPercentageOfHandlersAffected();

	        System.out.println("Total number of try blocks: " + visitor.getTotalTryCount());
	        System.out.println("Number of nested try blocks: " + visitor.getNestedTryCount());
	        System.out.println("Total number of catch handlers: " + visitor.getTotalCatchCount());
	        System.out.println("Number of nested catch handlers: " + visitor.getNestedCatchCount());
	        System.out.println("Percentage of catch handlers affected by nested try anti-pattern: " + percentage + "%");*/
	    }

	    private static void analyzeFiles(File folder, DestructiveWrapping visitor) throws IOException {
	    	BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\Admin\\eclipse-workspace\\Assignment\\src\\Assignment\\src\\Files\\Final\\FlowAP\\Flow_CatchDWAntiPatterns.csv"));
	        writer.write("File,Total Catch Handlers, Total DW Catch Handlers, Percentage DW Catch Handlers\n");
	        for (File file : folder.listFiles()) {
	            if (file.isDirectory()) {
	            	
	            	
	                analyzeFiles(file, visitor);
	                double percentage = visitor.getPercentageOfHandlersAffected();
	                System.out.println(file.getAbsolutePath());
	                System.out.println("Total number of try blocks: " + visitor.getTotalTryCount());
	                System.out.println("Number of DW try blocks: " + visitor.getNestedTryCount());
	                System.out.println("Total number of catch handlers: " + visitor.getTotalCatchCount());
	                System.out.println("Number of DW catch handlers: " + visitor.getDestCatchCount());
	                System.out.println("Percentage of catch handlers affected by destructive wrapping anti-pattern: " + percentage + "%");
	                writer.write(file.getAbsolutePath()+"\\"+file.getName()+","+visitor.getTotalCatchCount()+","+visitor.getNestedTryCount()+","+percentage+"\n");
	                visitor.resetCounts();
	                
	                
	                
	                
	            } else if (file.isDirectory()==false) {
	            	 	//System.out.println(file.getAbsolutePath());
	            		visitor.analyzeCode(file.getAbsolutePath());
	            		
	            }
	        }
	        writer.close();
	    }
	}

class DestructiveWrapping extends ASTVisitor {
    private int totalTryBlocks;
    private int nestedTryBlocks;
    private int totalCatchHandlers;
    private int nestedDestcatchHandlers;

    private String filePath;

    public DestructiveWrapping() {
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
    public boolean visit(CatchClause node) {
    	totalCatchHandlers++;
        String caughtTypeName = node.getException().getType().toString();

        List<Statement> statements = node.getBody().statements();
        for (Statement statement : statements) {
            if (statement instanceof ThrowStatement) {
                Expression expression = ((ThrowStatement) statement).getExpression();
                String thrownTypeName = expression.toString();
                if (thrownTypeName.contains("(")) {
                    thrownTypeName = thrownTypeName.substring(4,thrownTypeName.indexOf("("));
                }
                else
                	break;
                if(!caughtTypeName.equals(thrownTypeName)) {
                    System.out.println("Destructive wrapping detected in file: " + filePath);
                    System.out.println("Line number: " + ((CompilationUnit)node.getRoot()).getLineNumber(expression.getStartPosition()));
                    System.out.println("Caught exception type: " + caughtTypeName);    
                    System.out.println("Thrown exception type: " + thrownTypeName);
                    nestedDestcatchHandlers++;
                }
                break;
            }
        }
        return true;
    }

    public int getNestedTryCount() {
        return nestedTryBlocks;
    }

    public int getTotalTryCount() {
        return totalTryBlocks;
    }

    public int getDestCatchCount() {
        return nestedDestcatchHandlers;
    }

    public int getTotalCatchCount() {
        return totalCatchHandlers;
    }

    public double getPercentageOfHandlersAffected() {
        if (totalCatchHandlers == 0) {
            return 0;
        } else {
            return ((double) nestedDestcatchHandlers / totalCatchHandlers) * 100;
        }
    }
    public void resetCounts() {
        totalTryBlocks = 0;
        nestedTryBlocks = 0;
        totalCatchHandlers = 0;
        nestedDestcatchHandlers = 0;
    }
    
}
