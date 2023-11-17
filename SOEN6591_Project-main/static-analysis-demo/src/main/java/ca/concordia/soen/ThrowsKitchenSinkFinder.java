package ca.concordia.soen;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;


class ThrowsKitchenSinkFinder extends ASTVisitor {

	Path path;
	int try_count;
	public ThrowsKitchenSinkFinder(Path path) {
		this.path = path;
	}

	@Override
	public boolean visit(MethodDeclaration methodDeclaration) {
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
						ThrowsKitchenSinkFinder.findThrows(elseStatement, throwDecSet);
						ThrowsKitchenSinkFinder.findThrows(thenStatement, throwDecSet);

					}
					else if (statements.get(i).getNodeType() == Statement.SWITCH_STATEMENT) {
						SwitchStatement switchStatement = (SwitchStatement) statements.get(i);
						List<Statement> list = switchStatement.statements();
						for (int j = 0; j < list.size(); j++) {
							ThrowsKitchenSinkFinder.findThrows(list.get(j), throwDecSet);
						}
					}
				}

				if (!throwDecSet.isEmpty()) {
					CompilationUnit cu = ((CompilationUnit) methodDeclaration.getRoot());
					int lineNum = cu.getLineNumber(methodDeclaration.getStartPosition());
					try_count++;
					System.out.println("Throw Kitchen Sink Found at: File Path: "+this.path+" Line: "+lineNum+" Method name: "+methodDeclaration.getName());
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
					ThrowsKitchenSinkFinder.findThrows(statements.get(i), throwDecSet);
				}
			}
		}
	}
	
	 public int getCount() {
	        return try_count;
	    }

}