package ca.concordia.soen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jface.text.Document;


public class StaticAnalysisTool {
    public static void main(String[] args) {
        Map<String, Integer> logAndThrowMap = new HashMap<>();
        Map<String, Integer> nestedTryMap = new HashMap<>();
        Map<String, Integer> destructiveWrappingMap = new HashMap<>();
        Map<String, Integer> throwsKitchenSinkMap = new HashMap<>();

        Stack<File> filestack = new Stack<>();
        filestack.push(new File(args[0]));
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

                LogandThrowVisitor_1 logAndThrowVisitor = new LogandThrowVisitor_1(cu, file.getAbsolutePath());
                NestedTryVisitor nestedTryVisitor = new NestedTryVisitor(cu, file.getAbsolutePath());
                DestructiveWrappingVisitor1 destructiveWrappingVisitor = new DestructiveWrappingVisitor1(file.getAbsolutePath());
                ThrowsKitchenSinkFinder throwsKitchenSinkFinder = new ThrowsKitchenSinkFinder(path);

                cu.accept(logAndThrowVisitor);
                cu.accept(nestedTryVisitor);
                cu.accept(destructiveWrappingVisitor);
                cu.accept(throwsKitchenSinkFinder);

                int nestedTryCount = nestedTryVisitor.getCount();
                int logAndThrowCount = logAndThrowVisitor.getCount();
                int destructiveWrappingCount = destructiveWrappingVisitor.getCount();
                int throwsKitchenSinkCount = throwsKitchenSinkFinder.getCount();

                logAndThrowMap.put(file.getAbsolutePath(), logAndThrowCount);
                nestedTryMap.put(file.getAbsolutePath(), nestedTryCount);
                destructiveWrappingMap.put(file.getAbsolutePath(), destructiveWrappingCount);
                throwsKitchenSinkMap.put(file.getAbsolutePath(), throwsKitchenSinkCount);
            }
        }
     // Write results to CSV file
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\Admin\\eclipse-workspace\\Assignment\\src\\Assignment\\src\\Files\\Final\\AntiPatterns\\Antipatters_logs.csv"));
            writer.write("File,Log and Throw,Nested Try,Destructive Wrapping,Throws Kitchen Sink\n");
            for (String file : logAndThrowMap.keySet()) {
                int logAndThrowCount = logAndThrowMap.get(file);
                int nestedTryCount = nestedTryMap.get(file);
                int destructiveWrappingCount = destructiveWrappingMap.get(file);
                int throwsKitchenSinkCount = throwsKitchenSinkMap.get(file);
                String fname=file.substring(45);
                writer.write(fname+ "," + logAndThrowCount + "," + nestedTryCount + "," + destructiveWrappingCount + "," + throwsKitchenSinkCount + "\n");
            }
            writer.close();
        } catch (IOException e) {
            System.err.println("Error writing results to CSV file: " + e.getMessage());
        }
    }
}
