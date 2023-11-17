package flowmetrics;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jdt.core.dom.*;

public class ExceptionAnalyzer {
    private static final String FOLDER_PATH = "C:\\Users\\Admin\\Downloads\\kafka-3.2";
    private static final String CSV_FILE_PATH = "C:\\Users\\Admin\\Downloads\\NestedTry.csv";

    public static void main(String[] args) throws IOException {
        File folder = new File(FOLDER_PATH);
        String[] extensions = { "java" };
        List<File> files = getFiles(folder, extensions);
        double percentage=0.0;
        FileWriter csvWriter = new FileWriter(CSV_FILE_PATH);
        csvWriter.append("File,Total Exceptions,Handled Exceptions,Percentage of Handled Exceptions\n");

        for (File file : files) {
            String source = readFile(file);

            ASTParser parser = ASTParser.newParser(AST.JLS11);
            parser.setKind(ASTParser.K_COMPILATION_UNIT);
            parser.setSource(source.toCharArray());

            CompilationUnit cu = (CompilationUnit) parser.createAST(null);

            ExceptionVisitor visitor = new ExceptionVisitor();
            cu.accept(visitor);

            int total = visitor.getTotal();
            int handled = visitor.getHandled();
            if (total > 0) {
                percentage = handled * 100.0 / total;
            }

            csvWriter.append(file.getName() + "," + total + "," + handled + "," + String.format("%.2f%%", percentage) + "\n");
        }

        csvWriter.flush();
        csvWriter.close();
    }

    private static String readFile(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        try {
            String line = reader.readLine();
            while (line != null) {
                sb.append(line).append("\n");
                line = reader.readLine();
            }
        } finally {
            reader.close();
        }
        return sb.toString();
    }

    private static List<File> getFiles(File folder, String[] extensions) {
        List<File> files = new ArrayList<>();
        File[] listFiles = folder.listFiles();
        if (listFiles != null) {
            for (File file : listFiles) {
                if (file.isDirectory()) {
                    files.addAll(getFiles(file, extensions));
                } else {
                    for (String extension : extensions) {
                        if (file.getName().endsWith("." + extension)) {
                            files.add(file);
                            break;
                        }
                    }
                }
            }
        }
        return files;
    }
}

class ExceptionVisitor extends ASTVisitor {
    private int total = 0;
    private int handled = 0;

    @Override
    public boolean visit(TryStatement node) {
        total++;

        boolean isHandled = false;
        List<CatchClause> catchClauses = node.catchClauses();
        for (CatchClause catchClause : catchClauses) {
            if (catchClause.getBody().statements().size() > 0) {
                isHandled = true;
                break;
            }
        }

        if (isHandled) {
            handled++;
        }

        return true;
    }

    public int getTotal() {
        return total;
    }

    public int getHandled() {
        return handled;
    }
}
