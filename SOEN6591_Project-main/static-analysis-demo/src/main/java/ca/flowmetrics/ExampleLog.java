

	package flowmetrics;

	import java.io.BufferedWriter;
	import java.io.File;
	import java.io.FileWriter;
	import java.io.IOException;
	import java.nio.file.Files;
	import java.nio.file.Path;
	import java.util.Stack;
	import java.util.stream.Collectors;

	import org.eclipse.jdt.core.dom.AST;
	import org.eclipse.jdt.core.dom.ASTParser;
	import org.eclipse.jdt.core.dom.CompilationUnit;
	import org.eclipse.jface.text.Document;

	public class ExampleLog{
		
		public static void main(String[] args) throws IOException {

	        Stack<File> filestack = new Stack<>();
	        filestack.push(new File(args[0]));
	        BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\Admin\\eclipse-workspace\\Assignment\\src\\Assignment\\src\\Files\\Final\\FlowAP\\Flow_LogAndThrowAntiPatterns.csv"));
	        writer.write("File,Total Catch Handlers, Total Log and Throw Handlers, Percentage Log and Throw Handlers\n");
	        while (!filestack.isEmpty()) {
	            File file = filestack.pop();
	            if (file.isDirectory()) {
	                File[] files = file.listFiles();
	                if (files != null) {
	                    for (File f : files) {
	                        filestack.push(f);
	                    }
	                }
	            } else if (file.isFile() && file.getName().endsWith(".java")) {
	                String source;
	                Path path = file.toPath();
	                ASTParser parser = ASTParser.newParser(AST.getJLSLatest());
	                parser.setResolveBindings(true);
	                try {
	                    source = Files.lines(path).collect(Collectors.joining("\n"));
	                } catch (IOException e) {
	                    System.err.println(e);
	                    continue;
	                }

	                parser.setSource(new Document(source).get().toCharArray());
	                CompilationUnit cu = (CompilationUnit) parser.createAST(null);
	                
	                LogAndThrow_Flow visitor = new LogAndThrow_Flow();
	                
	                cu.accept(visitor);
	             
	      
	                double percentage = visitor.getPercentageOfCatchClausesAffected();
	                System.out.println(file.getAbsolutePath());
	                System.out.println("Total number of catch handlers: " + visitor.getTotalCatchCount());
	                System.out.println("Number of catch handlers using log and throw anti-pattern: " + visitor.getLogAndThrowCount());
	                System.out.println("Percentage of catch handlers affected by log and throw anti-pattern: " + percentage + "%");
	                String str=file.getAbsolutePath().substring(45);
	                writer.write(str+","+visitor.getTotalCatchCount()+","+visitor.getLogAndThrowCount()+","+percentage+"\n");
	                visitor.resetCounts();
	            }
	        }
	        writer.close();
	    }

	}

