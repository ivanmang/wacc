import antlr.WaccLexer;
import antlr.WaccParser;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class Main {

  public static void main(String[] args) throws Exception {

    String filename = args[0];
    File file = new File(filename);
    InputStream fileStream = new FileInputStream(file);

    //create a CharStream that reads from file stream
    ANTLRInputStream input = new ANTLRInputStream(fileStream);

    //create a lexer that feeds off of input CharStream
    WaccLexer lexer = new WaccLexer(input);

    //create a buffer of tokens pulled from the lexer
    CommonTokenStream tokens = new CommonTokenStream(lexer);

    //create a parser that feeds off the tokens buffer
    WaccParser parser = new WaccParser(tokens);

    //set a error handler for parser
    //parser.setErrorHandler(new ParserErrorHandler());

    //begin parsing at prog rule
    ParseTree tree = parser.prog();

    //show AST in GUI
//    JFrame frame = new JFrame("Antlr AST");
//    JPanel panel = new JPanel();
//    TreeViewer viewr = new TreeViewer(Arrays.asList(
//        parser.getRuleNames()),tree);
//    viewr.setScale(1);//scale a little
//    panel.add(viewr);
//    frame.add(panel);
//    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//    frame.setSize(1000,1000);
//    frame.setVisible(true);

    SemanticChecker checker = new SemanticChecker();
    checker.visit(tree);

    //print LISP-style tree
//    System.out.println(tree.toStringTree(parser));

    //System.out.println("====");
    //MyVisitor visitor = new MyVisitor();
    //visitor.visit(tree);
    //System.out.println("====");
  }
}