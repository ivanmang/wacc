import antlr.WaccLexer;
import antlr.WaccParser;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class Main {

  public static void main(String[] args) throws Exception {

    String filename = args[0];

    File file = new File(filename);
    InputStream fileStream = new FileInputStream(file);
    //create a CharStream that reads from file stream
    CharStream input = CharStreams.fromFileName(filename);

    //create a lexer that feeds off of input CharStream
    WaccLexer lexer = new WaccLexer(input);

    //create a buffer of tokens pulled from the lexer
    CommonTokenStream tokens = new CommonTokenStream(lexer);

    //a parser that feeds off the tokens buffer
    WaccParser parser = new WaccParser(tokens);

    //set a error handler for parser
    parser.setErrorHandler(new ParserErrorHandler());

    //begin parsing at prog rule
    ParseTree tree = parser.prog();

    AssignmentOutOfBoundsChecker assignmentOutOfBoundsChecker = new AssignmentOutOfBoundsChecker();
    assignmentOutOfBoundsChecker.visit(tree);

    FunctionReturnChecker functionReturnChecker = new FunctionReturnChecker();
    functionReturnChecker.visit(tree);

    SemanticChecker checker = new SemanticChecker();
    checker.visit(tree);
  }
}