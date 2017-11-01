import antlr.BasicLexer;
import antlr.BasicParser;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class Main {

  public static void main(String[] args) throws Exception{

    String filename = args[0];
    File file = new File(filename);
    InputStream fileStream = new FileInputStream(file);

    ANTLRInputStream input = new ANTLRInputStream(fileStream);

    BasicLexer lexer = new BasicLexer(input);

    CommonTokenStream tokens = new CommonTokenStream(lexer);

    BasicParser parser = new BasicParser(tokens);

    ParseTree tree = parser.prog();
    System.exit(100);
    System.out.println(tree.toStringTree(parser));
  }
}