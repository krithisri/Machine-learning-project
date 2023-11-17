package flowmetrics;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.ThrowStatement;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ThrowStatement;

class LogAndThrow_Flow1 {

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println("Please provide a folder path as input.");
            return;
        }

        File folder = new File(args[0]);
        if (!folder.exists()) {
            System.err.println("The specified folder does not exist.");
            return;
        }

        if (!folder.isDirectory()) {
            System.err.println("The specified path is not a folder.");
            return;
        }

        LogAndThrow_Flow visitor = new LogAndThrow_Flow();
        analyzeFiles(folder, visitor);

    }
    

    private static void analyzeFiles(File folder, LogAndThrow_Flow visitor) throws IOException {
    	BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\Admin\\eclipse-workspace\\Assignment\\src\\Assignment\\src\\Files\\Final\\FlowAP\\Flow_LogAndThrowAntiPatterns.csv"));
        writer.write("File,Total Catch Handlers, Total Log and Throw Handlers, Percentage Log and Throw Handlers\n");
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                analyzeFiles(file, visitor);
                
                double percentage = visitor.getPercentageOfCatchClausesAffected();
                System.out.println(file.getAbsolutePath());
                System.out.println("Total number of catch handlers: " + visitor.getTotalCatchCount());
                System.out.println("Number of catch handlers using log and throw anti-pattern: " + visitor.getLogAndThrowCount());
                System.out.println("Percentage of catch handlers affected by log and throw anti-pattern: " + percentage + "%");
                writer.write(file.getAbsolutePath()+","+visitor.getTotalCatchCount()+","+visitor.getLogAndThrowCount()+","+percentage+"\n");
                visitor.resetCounts();
                
            } else if (file.getName().endsWith(".java")) {
                visitor.analyzeCode(file.getAbsolutePath());
            }
        }
        writer.close();
    }
}


public class LogAndThrow_Flow extends ASTVisitor {
    private int totalCatchClauses;
    private int logAndThrowCount;

    private String filePath;

    public LogAndThrow_Flow() {
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

   /* @Override
    public boolean visit(CatchClause node) {
        totalCatchClauses++;

        Block block = node.getBody();
        if (block != null) {
            for (Object stmtObj : block.statements()) {
                Statement stmt = (Statement) stmtObj;
                if (stmt instanceof ThrowStatement) {
                    ThrowStatement throwStmt = (ThrowStatement) stmt;
                    if (throwStmt.getExpression() instanceof MethodInvocation) {
                        MethodInvocation mi = (MethodInvocation) throwStmt.getExpression();
                        if (mi.getName().getIdentifier().toLowerCase().contains("log")) {
                            logAndThrowCount++;
                            break;
                        }
                    }
                }
            }
        }

        return super.visit(node);
    }*/
    
    
    public boolean visit(MethodInvocation methodInvocation) {
	    
	    return true; 
	}
  @Override
  
  public boolean visit(CatchClause clause) {
    boolean hasLogging = false;
    boolean hasThrowing = false;
    totalCatchClauses++;
    int lineNumber = ((CompilationUnit) clause.getRoot()).getLineNumber(clause.getStartPosition());
    for (Object statement : clause.getBody().statements()) {
      if (statement instanceof ExpressionStatement) {
        ExpressionStatement exprStmt = (ExpressionStatement) statement;
        if (exprStmt.getExpression() instanceof MethodInvocation) {
          MethodInvocation methodInvocation = (MethodInvocation) exprStmt.getExpression();
          if (methodInvocation.getName().getIdentifier().equals("log") || 
        		  methodInvocation.getName().getIdentifier().equals("logp") || 
        		  methodInvocation.getName().getIdentifier().equals("logrb") || 
        		  methodInvocation.getName().getIdentifier().equals("error") || 
        		  methodInvocation.getName().getIdentifier().equals("warning") || 
        		  methodInvocation.getName().getIdentifier().equals("info") || 
        		  methodInvocation.getName().getIdentifier().equals("debug") || 
        		  methodInvocation.getName().getIdentifier().equals("fatal") || 
        		  methodInvocation.getName().getIdentifier().equals("trace") || 
        		  methodInvocation.getName().getIdentifier().equals("setLevel") || 
        	  methodInvocation.getName().getIdentifier().equals("printStackTrace")) {
            hasLogging = true;
          }
        }
      } else if (statement instanceof ThrowStatement) {
        hasThrowing = true;
      }
    }
    if (hasLogging && hasThrowing) {
    	logAndThrowCount++;
    	//System.out.println("A log and throw anti-pattern is detected in a file, whose file path is: " + filename);
      //System.out.println("The anti-pattern is detected in a catch block at this line number: " + lineNumber);
      //System.out.println("The catch clause \n" + clause.toString() + " is having the log and throw anti pattern.");
      
      //System.out.println();
      
      //System.out.println("Total Number of over-catch try: "+LogandThrowVisitor.try_count);
    }
    
    return true;
  }

    public int getTotalCatchCount() {
        return totalCatchClauses;
    }

    public int getLogAndThrowCount() {
        return logAndThrowCount;
    }

    public double getPercentageOfCatchClausesAffected() {
        if (totalCatchClauses == 0) {
            return 0;
        } else {
            return ((double) logAndThrowCount / totalCatchClauses) * 100;
        }
    }

    public void resetCounts() {
        totalCatchClauses = 0;
        logAndThrowCount = 0;
    }
}
