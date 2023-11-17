package ca.concordia.soen;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jface.text.Document;

import java.io.*;
import java.util.*;

public class MetricsCalculator {
    	
	public static void main(String[] args) throws IOException {
	    if (args.length == 0) {
	        System.out.println("Usage: java MetricsCalculator <source_folder>");
	        return;
	    }

	    String sourceFolder = args[0];

	    List<File> javaFiles = getJavaFiles(new File(sourceFolder));
	    if (javaFiles.isEmpty()) {
	        System.out.println("No Java files found in " + sourceFolder);
	        return;
	    }
	    // Create CSV writer
        BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\Admin\\eclipse-workspace\\Assignment\\src\\Assignment\\src\\Files\\FlowMetrics.csv"));
        writer.write("File,Try Quantity,Try LOC, Try SLOC, Catch Quantity, Catch LOC, Catch SLOC, Specific Strategy %, Subsumption Strategy %, recoverable exceptions, unrecoverable exceptions\n");
        
        //writer.write("File,Specific Strategy %, Subsumption Strategy %, recoverable exceptions, unrecoverable exceptions\n");
	    // Calculate metrics for each Java file
	    int totalTryQuantity = 0;
	    int totalTryLOC=0;
	    int totalTrySLOC = 0;
	    
	    int totalCatchSize = 0;
	    int totalCatchLOC = 0;
	    int totalCatchSLOC = 0;
	    int totalSpecificCatchP = 0;
	    int totalSubsumptionCatchP = 0;
	    
	   
	    for (File javaFile : javaFiles) {
	        String sourceCode = readFile(javaFile.getAbsolutePath());

	        ASTParser parser = ASTParser.newParser(AST.JLS14);
	        parser.setSource(sourceCode.toCharArray());
	        parser.setKind(ASTParser.K_COMPILATION_UNIT);
	        final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
	        

	        // Calculate metrics for this Java file
	        double tryQuantity = countTries(cu);
	        int tryLOC=countTryLOC(cu);
	        int trySLOC = countTrySLOC(cu);
	        int catchSize = countCatch(cu);
	        int catchLOC = countCatchLOC(cu);
	        int catchSLOC=countCatchSLOC(cu);
	        double specificHandlerP = specificHandlingStrategy(cu);
	        double subsumptionHandlerP = subsumptionHandlingStrategy(cu);
	        int recoverableExceptions = recoverableException(cu);
	        int unrecoverableExceptions = unrecoverableException(cu);

	        // Accumulate metrics for all Java files
	      /*  totalTryQuantity += tryQuantity;
	        totalTryLOC += tryLOC;
	        totalTrySLOC += trySLOC;
	        totalCatchSize +=catchSize;
	        totalCatchLOC += catchLOC;
	        totalCatchSLOC += catchSLOC;*/
	        
	        
	        // Write metrics to CSV
       
            if(tryQuantity!=0)
	        {
            	if(tryQuantity>= subsumptionHandlerP)
            	writer.write(javaFile.getAbsolutePath() + "," + (int)tryQuantity + "," + tryLOC + "," + trySLOC + ","+ catchSize +"," +catchLOC + "," + catchSLOC + "," + (specificHandlerP/tryQuantity) *100 + "," + (subsumptionHandlerP/tryQuantity)*100+","+recoverableExceptions+","+unrecoverableExceptions +"," +"\n");
            	else
            		writer.write(javaFile.getAbsolutePath() + "," + (int)tryQuantity + "," + tryLOC + "," + trySLOC + ","+ catchSize +"," +catchLOC + "," + catchSLOC + "," + (specificHandlerP/tryQuantity) *100 + "," + 100+","+recoverableExceptions+","+unrecoverableExceptions+ "," + "\n");
	        
	        }
	        else
	        {
	        	writer.write(javaFile.getAbsolutePath() + "," + (int)tryQuantity + "," + tryLOC + "," + trySLOC + ","+ catchSize +"," +catchLOC + "," + catchSLOC + "," + 0 + "," + 0+","+recoverableExceptions+","+unrecoverableExceptions+ ","+"\n");

	        }
            
	        // Print metrics for this Java file
	        System.out.println("Metrics for " + javaFile.getAbsolutePath());
	        System.out.println("Try Quantity: " + (int)tryQuantity);
	        System.out.println("Try LOC: " + tryLOC);
	        System.out.println("Try SLOC: " + trySLOC);
	        System.out.println("Catch Size: " + catchSize);
	        System.out.println("Catch LOC: " + catchLOC); 
	        System.out.println("Catch SLOC: " + catchSLOC);
	        
	        if(tryQuantity!=0)
	        {
	        	System.out.println("Specific Handling Strategy %: " + (specificHandlerP/tryQuantity) *100); 
		        System.out.println("Subsumption Handling Strategy %: " + (subsumptionHandlerP/tryQuantity)*100); 
	        
	        }
	        else
	        {
	        	System.out.println("Specific Handling Strategy %: " + 0); 
		        System.out.println("Subsumption Handling Strategy %: " +0);
	        }
	        
	        System.out.println("Recoverable Exceptions: "+ recoverableExceptions);	      
	        System.out.println("UnRecoverable Exceptions: "+ unrecoverableExceptions);
	        System.out.println();
	        
	        //Close CSV writer
	        //writer.close();
	    }

	    // Print total metrics for all Java files
	    /*System.out.println("Total Metrics for " + javaFiles.size() + " Java files");
	    System.out.println("Try Quantity: " + totalTryQuantity);
	    System.out.println("Try LOC: " + totalTryLOC);
	    System.out.println("Try SLOC: " + totalTrySLOC);
	    System.out.println("Catch Size: " + totalCatchSize);*/
	    //Close CSV writer
        writer.close();
	}

	

	private static List<File> getJavaFiles(File folder) {
	    List<File> javaFiles = new ArrayList<>();
	    File[] files = folder.listFiles();
	    for (File file : files) {
	        if (file.isDirectory()) {
	            javaFiles.addAll(getJavaFiles(file));
	        } else if (file.getName().endsWith(".java")) {
	            javaFiles.add(file);
	        }
	    }
	    return javaFiles;
	}
	
	

    private static String readFile(String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }
        br.close();
        return sb.toString();
    }

    private static int countTries(CompilationUnit cu) {
        int count = 0;
        TryVisitor visitor = new TryVisitor();
        cu.accept(visitor);
        count = visitor.getCount();
        return count;
    }
    
    
    private static int countTryLOC(CompilationUnit cu) {
        int count = 0;
        TryLOCVisitor visitor = new TryLOCVisitor();
        cu.accept(visitor);
        count = visitor.getLocInsideTry();
        return count;
    }
    
    private static int countTrySLOC(CompilationUnit cu) {
        int count = 0;
        TrySLOCVisitor visitor = new TrySLOCVisitor();
        cu.accept(visitor);
        count = visitor.getSLocInsideTry();
        return count;
    }

    private static int countCatch(CompilationUnit cu) {
        int count = 0;
        CatchVisitor visitor = new CatchVisitor();
        cu.accept(visitor);
        count = visitor.getCatchCount();
        return count;
    }
    
    private static int countCatchLOC(CompilationUnit cu) {
        int count = 0;
        CatchLOCVisitor visitor = new CatchLOCVisitor();
        cu.accept(visitor);
        count = visitor.getLocInsideCatch();
        return count;
    }
    
    private static int countCatchSLOC(CompilationUnit cu) {
        int count = 0;
        CatchSLOCVisitor visitor = new CatchSLOCVisitor();
        cu.accept(visitor);
        count = visitor.getSLocInsideCatch();
        return count;
    }
    
    private static int specificHandlingStrategy(CompilationUnit cu) {
    	
        int count = 0;
        SpecificStrategyVisitor visitor = new SpecificStrategyVisitor();
        cu.accept(visitor);
        count = visitor.getSpecificStrategyP();
        return count;
    }
    
    private static int subsumptionHandlingStrategy(CompilationUnit cu) {
        int count = 0;      
        SpecificStrategyVisitor visitor = new SpecificStrategyVisitor();
        cu.accept(visitor);
        count = visitor.getSubsumptionCatchCount();
        return count;
    }
    
    private static int recoverableException(CompilationUnit cu) {
    	
    	RecoverableExceptionVisitor visitor = new RecoverableExceptionVisitor();
        cu.accept(visitor);
        return visitor.getRecoverableCount();
	}

	private static int unrecoverableException(CompilationUnit cu) {
		
		UnRecoverableExceptionVisitor visitor = new UnRecoverableExceptionVisitor();
	    cu.accept(visitor);
	    return visitor.getUnrecoverableCount();
		
	}
    
    
    //Try
    private static class TryVisitor extends ASTVisitor {
        private int count = 0;
        public boolean visit(TryStatement node) {
            count++;
            return super.visit(node);
        }
        public int getCount() {
            return count;
        }
    }
    //Try LOC
    private static class TryLOCVisitor extends ASTVisitor {
    	
    	private int tryCount = 0;
        private int locInsideTry = 0;

        @Override
        public boolean visit(TryStatement node) {
            tryCount++;
            locInsideTry += countLinesOfCode(node.getBody().toString());
            return true;
        }

        private int countLinesOfCode(String code) {
            Document doc = new Document(code);
            int lineCount = doc.getNumberOfLines();
            int commentCount = countOccurrences(code, "//") + countOccurrences(code, "/") + countOccurrences(code, "/");
            return lineCount - commentCount;
        }

        private int countOccurrences(String str, String substr) {
            return str.split(substr, -1).length - 1;
        }
        public int getTryCount() {
            return tryCount;
        }

        public int getLocInsideTry() {
            return locInsideTry;
        }
    }
    //Try SLOC
    private static class TrySLOCVisitor extends ASTVisitor {
        private int count = 0;
        public boolean visit(TryStatement node) {
            int startLine = node.getStartPosition();
            int endLine = startLine + node.getLength();
            String rootString = node.getRoot().toString();
            if (startLine < 0 || endLine > rootString.length()) {
                return super.visit(node);
            }
            String code = rootString.substring(startLine, endLine);
            //String code = node.getRoot().toString().substring(startLine, endLine);
            int lines = code.split("\n").length;
            int nonEmptyLines = 0;
            for (String line : code.split("\n")) {
                if (!line.trim().isEmpty()) {
                    nonEmptyLines++;
                }
            }
            count += nonEmptyLines;
            return super.visit(node);
        }
        public int getSLocInsideTry() {
            return count;
        }
    }
    
    //Catch Quantity
    private static class CatchVisitor extends ASTVisitor {
        private int count = 0;
        public boolean visit(CatchClause node) {
            count++;
            return super.visit(node);
        }
        public int getCatchCount() {
            return count;
        }
    }
    
  //Catch LOC
    private static class CatchLOCVisitor extends ASTVisitor {
    	
    	private int catchCount = 0;
        private int locInsideCatch = 0;

        @Override
        public boolean visit(CatchClause node) {
            catchCount++;
            locInsideCatch += countLinesOfCode(node.getBody().toString());
            return true;
        }

        private int countLinesOfCode(String code) {
            Document doc = new Document(code);
            int lineCount = doc.getNumberOfLines();
            int commentCount = countOccurrences(code, "//") + countOccurrences(code, "/") + countOccurrences(code, "/");
            return lineCount - commentCount;
        }

        private int countOccurrences(String str, String substr) {
            return str.split(substr, -1).length - 1;
        }
        public int getCatchCount() {
            return catchCount;
        }

        public int getLocInsideCatch() {
            return locInsideCatch;
        }
    }
    //Catch SLOC
    private static class CatchSLOCVisitor extends ASTVisitor {
        private int count = 0;
        public boolean visit(CatchClause node) {
            int startLine = node.getStartPosition();
            int endLine = startLine + node.getLength();
            String rootString = node.getRoot().toString();
            if (startLine < 0 || endLine > rootString.length()) {
                return super.visit(node);
            }
            String code = rootString.substring(startLine, endLine);
            //String code = node.getRoot().toString().substring(startLine, endLine);
            //int lines = code.split("\n").length;
            int nonEmptyLines = 0;
            for (String line : code.split("\n")) {
                if (!line.trim().isEmpty()) {
                    nonEmptyLines++;
                }
            }
            count += nonEmptyLines;
            return super.visit(node);
        }
        public int getSLocInsideCatch() {
            return count;
        }
    }
    
    // Strategy counter   
    private static class SpecificStrategyVisitor extends ASTVisitor {
    	
         int specificCatchCount = 0;
         int subsumptionCatchCount = 0;

        public boolean visit(CatchClause node) {
        	
            ITypeBinding catchType = node.getException().getType().resolveBinding();
            ITypeBinding thrownType = null;

            // Get the type of the exception thrown in the try block
            if (node.getParent() instanceof TryStatement) {
                TryStatement tryStatement = (TryStatement) node.getParent();
                for (Object obj : tryStatement.getBody().statements()) {
                    if (obj instanceof ThrowStatement) {
                        ThrowStatement throwStatement = (ThrowStatement) obj;
                        thrownType = throwStatement.getExpression().resolveTypeBinding();
                        break; // stop searching after the first throw statement is found
                    }
                }
            }

            // Check if the catch parameter type is the same as the thrown exception type
            if (catchType != null && thrownType != null && catchType.isEqualTo(thrownType)) {
            	System.out.println("ct: "+catchType);
            	System.out.println("tt: "+thrownType);
                specificCatchCount++;
            } else {
                subsumptionCatchCount++;
            }

            return super.visit(node);
        }

        public int getSpecificStrategyP() {
            return specificCatchCount;
        }

        public int getSubsumptionCatchCount() {
            return subsumptionCatchCount;
        }
    }

    // Recoverable and Unrecoverable exceptions
    
    private static class RecoverableExceptionVisitor extends ASTVisitor {
        private int recoverableCount = 0;
        private int unrecoverableCount = 0;
        
        public boolean visit(TryStatement node) {
            List<CatchClause> catchClauses = node.catchClauses();
            boolean hasRecoverableException = false;
            for (CatchClause catchClause : catchClauses) {
                Type catchType = catchClause.getException().getType();
                if (catchType != null && catchType.resolveBinding() != null && catchType.resolveBinding().isSubTypeCompatible(
                        node.getAST().resolveWellKnownType("java.lang.Exception"))) {
                    recoverableCount++;
                    hasRecoverableException = true;
                } else {
                    unrecoverableCount++;
                }
            }
            // If there is no catch block that catches Exception, increment the unrecoverable count
            if (!hasRecoverableException) {
                unrecoverableCount++;
                //recoverableCount = 0;
            }
            return super.visit(node);
        }
        
        public int getRecoverableCount() {
            return recoverableCount;
        }
        
        public int getUnrecoverableCount() {
            return unrecoverableCount;
        }
    }

    private static class UnRecoverableExceptionVisitor extends ASTVisitor {
        private int recoverableCount = 0;
        private int unrecoverableCount = 0;
        
        public boolean visit(TryStatement node) {
            List<CatchClause> catchClauses = node.catchClauses();
            boolean hasRecoverableException = false;
            for (CatchClause catchClause : catchClauses) {
                Type catchType = catchClause.getException().getType();
                if (catchType != null) {
                    ITypeBinding binding = catchType.resolveBinding();
                    if (binding != null && binding.isSubTypeCompatible(node.getAST().resolveWellKnownType("java.lang.Exception"))) {
                        recoverableCount++;
                        hasRecoverableException = true;
                    } else {
                        unrecoverableCount++;
                    }
                } else {
                    // If the catch type is null, check if it has a binding
                    IVariableBinding variableBinding = catchClause.getException().resolveBinding();
                    if (variableBinding != null) {
                        ITypeBinding binding = variableBinding.getType();
                        if (binding != null && binding.isSubTypeCompatible(node.getAST().resolveWellKnownType("java.lang.Exception"))) {
                            recoverableCount++;
                            hasRecoverableException = true;
                        } else {
                            unrecoverableCount++;
                        }
                    } else {
                        // If the catch type and its binding are null, count it as unrecoverable
                        unrecoverableCount++;
                    }
                }
            }
            // If there is no catch block that catches Exception, increment the unrecoverable count
            if (!hasRecoverableException) {
                unrecoverableCount++;
            }
            return super.visit(node);
        }

        public int getRecoverableCount() {
            return recoverableCount;
        }
        
        public int getUnrecoverableCount() {
            return unrecoverableCount;
        }
    }
}