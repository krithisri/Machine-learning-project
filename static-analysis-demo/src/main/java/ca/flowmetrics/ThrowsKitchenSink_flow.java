package flowmetrics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

class throwskitchensink {

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

        ThrowsKitchenSink_flow visitor = new ThrowsKitchenSink_flow();
        analyzeFiles(folder, visitor);
    }

    private static void analyzeFiles(File folder, ThrowsKitchenSink_flow visitor) throws IOException {
    	BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\Admin\\eclipse-workspace\\Assignment\\src\\Assignment\\src\\Files\\Final\\FlowAP\\Flow_ThrowKitchenAntiPatterns.csv"));
        writer.write("File,Total Handlers, Total Effected Handlers, Percentage of Effected Handlers\n");
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
            	
            	
                analyzeFiles(file, visitor);
                double percentage = visitor.getPercentageOfHandlersAffected();
                System.out.println(file.getAbsolutePath());
                System.out.println("Total number of catch handlers: " + visitor.getTotalCount());
                System.out.println("Number of nested catch handlers: " + visitor.getKitchenSinkCount());
                System.out.println("Percentage of catch handlers affected by nested try anti-pattern: " + percentage + "%");
                writer.write(file.getAbsolutePath()+"\\"+file.getName()+","+visitor.getTotalCount()+","+visitor.getKitchenSinkCount()+","+percentage+"\n");
                visitor.resetCounts();
                
                
                
                
            } else if (file.isDirectory()==false) {
            		visitor.analyzeCode(file.getAbsolutePath());
            		
            }
        }
        writer.close();
    }
}


class ThrowsKitchenSink_flow extends ASTVisitor {
	
    private int totalHandlers;
    private int kitchensinkclause;

    private String filePath;

    public ThrowsKitchenSink_flow() {
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
	public boolean visit(MethodDeclaration methodDeclaration) {
    	totalHandlers++;
		List<Type> throwDecList = methodDeclaration.thrownExceptionTypes();
		if (throwDecList.size() > 1) {
			HashSet<String> throwDecSet = new HashSet<String>();
			for (Type exceptionType: throwDecList) {
				throwDecSet.add(exceptionType.toString());
			}
			Block block = methodDeclaration.getBody();

			if (block != null) {

				List<Statement> statements = block.statements();

				for (int i = 0; i < statements.size(); i++) {
					if (statements.get(i).getNodeType() == Statement.IF_STATEMENT) {
						IfStatement ifStatement = (IfStatement) statements.get(i);
						Statement thenStatement = ifStatement.getThenStatement();
						Statement elseStatement = ifStatement.getElseStatement();
						findThrows(elseStatement, throwDecSet);
						findThrows(thenStatement, throwDecSet);

					}
					else if (statements.get(i).getNodeType() == Statement.SWITCH_STATEMENT) {
						SwitchStatement switchStatement = (SwitchStatement) statements.get(i);
						List<Statement> list = switchStatement.statements();
						for (int j = 0; j < list.size(); j++) {
							findThrows(list.get(j), throwDecSet);
						}
					}
				}

				if (!throwDecSet.isEmpty()) {
					CompilationUnit cu = ((CompilationUnit) methodDeclaration.getRoot());
					int lineNum = cu.getLineNumber(methodDeclaration.getStartPosition());
					kitchensinkclause++;
					//System.out.println("Throw Kitchen Sink Found at: File Path: "+this.path+" Line: "+lineNum+" Method name: "+methodDeclaration.getName());
				}
			}
		}

		return true;
	}
	public static void findThrows(Statement statement, HashSet<String> throwDecSet) {
		if (statement != null) {
			if (statement.getNodeType() == Statement.VARIABLE_DECLARATION_STATEMENT) {
				VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) statement;

				if (throwDecSet.contains(variableDeclarationStatement.getType().toString())) {
					throwDecSet.remove(variableDeclarationStatement.getType().toString());
				}
			}
			else if (statement.getNodeType() == Statement.THROW_STATEMENT) {
				ThrowStatement thenThrow = (ThrowStatement) statement;
				Expression throwExpression = thenThrow.getExpression();
				if (throwExpression.getNodeType() == Expression.CLASS_INSTANCE_CREATION) {
					ClassInstanceCreation variableDeclarationExpression = (ClassInstanceCreation) throwExpression;
					if (throwDecSet.contains(variableDeclarationExpression.getType().toString())) {
						throwDecSet.remove(variableDeclarationExpression.getType().toString());
					}
				}

			}
			else if (statement.getNodeType() == Statement.BLOCK) {
				Block block = (Block) statement;
				List<Statement> statements = block.statements();
				for (int i = 0; i < statements.size(); i++) {
				     findThrows(statements.get(i), throwDecSet);
				}
			}
		}
	}


    public int getKitchenSinkCount() {
        return kitchensinkclause;
    }

    public int getTotalCount() {
        return totalHandlers;
    }

    public double getPercentageOfHandlersAffected() {
        if (totalHandlers == 0) {
            return 0;
        } else {
            return ((double) kitchensinkclause / totalHandlers) * 100;
        }
    }
    public void resetCounts() {
    	kitchensinkclause=0;
    	totalHandlers=0;
    }
    
}