import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;

public class ParserErrorHandler extends DefaultErrorStrategy {

  private static final int SYNTAX_ERROR_CODE = 100;

  @Override
  public void recover(Parser recognizer, RecognitionException e) {
    System.exit(SYNTAX_ERROR_CODE);
  }

  @Override
  public Token recoverInline(Parser recognizer) {
    System.exit(SYNTAX_ERROR_CODE);
    return null;
  }

  @Override
  public void sync(Parser recognizer) {}
}
