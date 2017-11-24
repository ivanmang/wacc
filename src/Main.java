import antlr.WaccLexer;
import antlr.WaccParser;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class Main {

  public static void main(String[] args) throws Exception {

    String filename = args[0];

    File file = new File(filename);
    String name = file.getName();

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

    //Checking if integer assignments is out of bounds
    AssignmentOutOfBoundsChecker assignmentOutOfBoundsChecker = new AssignmentOutOfBoundsChecker();
    assignmentOutOfBoundsChecker.visit(tree);

    //Checking if the functions have return statement
    FunctionReturnChecker functionReturnChecker = new FunctionReturnChecker();
    functionReturnChecker.visit(tree);

    //Checking for semantic errors
    SemanticChecker checker = new SemanticChecker();
    checker.visit(tree);
//    System.out.println("---Printing symbol table---");
//    checker.getGlobalSymbolTable().printTable();
//    System.out.println("---Finished---");

    CodeGenerator gen = new CodeGenerator(checker.getGlobalSymbolTable(), checker.getFunctionList());
    gen.visit(tree);
    System.out.println(gen.generateCode());


    String outputFileName = name.substring(0, name.lastIndexOf('.')) + ".s";
    String program = gen.generateCode();
    FileWriter writer = null;
    try {
      writer = new FileWriter(outputFileName);
      writer.write(program);
      writer.flush();
    } catch (IOException ex) {
      System.out.println("Error writing file");
    } finally {
      try{
        writer.close();
      } catch (IOException ex) {
        System.out.println("Error closing file");
      }
    }
  }
}