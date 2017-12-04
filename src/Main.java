import antlr.WaccLexer;
import antlr.WaccParser;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.antlr.v4.gui.TreeViewer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class Main {

  public static void main(String[] args) throws Exception {

    String inputFilePath = "";

    if(args.length == 0) {
      System.out.println("Please enter a file path:");
      Scanner s = new Scanner(System.in);
      inputFilePath = s.next();
    } else {
      inputFilePath = args[0];
    }

    File inputFile = new File(inputFilePath);
    String inputFileName = inputFile.getName();

    //create a CharStream that reads from inputFile stream
    CharStream input = CharStreams.fromFileName(inputFilePath);

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

    //show AST in GUI
    JFrame frame = new JFrame("Antlr AST");
    JPanel panel = new JPanel();
    TreeViewer viewr = new TreeViewer(Arrays.asList(
        parser.getRuleNames()),tree);
    viewr.setScale(1.5);//scale a little
    panel.add(viewr);
    frame.add(panel);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(200,200);
    frame.setVisible(true);

    //Checking if integer assignments is out of bounds
    AssignmentOutOfBoundsChecker assignmentOutOfBoundsChecker = new AssignmentOutOfBoundsChecker();
    assignmentOutOfBoundsChecker.visit(tree);

    //Checking if the functions have return statement
    FunctionReturnChecker functionReturnChecker = new FunctionReturnChecker();
    functionReturnChecker.visit(tree);

    //Checking for semantic errors
    SemanticChecker checker = new SemanticChecker();
    checker.visit(tree);

    for(String ident : checker.getFunctionList().keySet()) {
      System.out.println("Symbol table for " + ident);
      checker.getFunctionList().get(ident).getSymbolTable().printTable();
      System.out.println("Finished");
    }

    System.out.println("---Printing symbol table---");
    checker.getGlobalSymbolTable().printTable();
    System.out.println("---Finished---");

    CodeGenerator gen = new CodeGenerator(checker.getGlobalSymbolTable(), checker.getFunctionList());
    gen.visit(tree);
    System.out.println(gen.generateCode());


    String outputFileName = inputFileName.substring(0, inputFileName.lastIndexOf('.')) + ".s";
    String program = gen.generateCode();
    FileWriter writer = null;
    try {
      writer = new FileWriter(outputFileName);
      writer.write(program);
      writer.flush();
    } catch (IOException ex) {
      System.out.println("Error writing inputFile");
    } finally {
      try{
        writer.close();
      } catch (IOException ex) {
        System.out.println("Error closing inputFile");
      }
    }
  }
}