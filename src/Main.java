import antlr.WaccLexer;
import antlr.WaccParser;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.gui.TreeViewer;

public class Main {

  public static void main(String[] args) throws Exception{

    String filename = args[0];
    File file = new File(filename);
    InputStream fileStream = new FileInputStream(file);

    ANTLRInputStream input = new ANTLRInputStream(fileStream);

    WaccLexer lexer = new WaccLexer(input);

    CommonTokenStream tokens = new CommonTokenStream(lexer);

    WaccParser parser = new WaccParser(tokens);
    //parser.setErrorHandler(new ParserErrorHandler());

    ParseTree tree = parser.prog();
    System.out.println(tree.toStringTree(parser));

    //show AST in GUI
    JFrame frame = new JFrame("Antlr AST");
    JPanel panel = new JPanel();
    TreeViewer viewr = new TreeViewer(Arrays.asList(
        parser.getRuleNames()),tree);
    viewr.setScale(0.8);//scale a little
    panel.add(viewr);
    frame.add(panel);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(1000,1000);
    frame.setVisible(true);
  }
}