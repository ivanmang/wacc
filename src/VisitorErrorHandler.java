import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

public class VisitorErrorHandler {

  public static final int SYNTAX_ERROR_CODE = 100;
  public static final int SEMANTIC_ERROR_CODE = 200;
  public static final int INT_MAX_VALUE = (int) Math.pow(2, 31) - 1;
  public static final int INT_MIN_VALUE = (int) -Math.pow(2, 31);

  public void incompatibleTypeError(ParseTree ctx, Type type) {
    throwError(ctx, "Incompatible type " + type, SEMANTIC_ERROR_CODE);
  }

  public void incompatibleTypeError(ParseTree ctx, String token, Type expectedType,
      Type actualType) {
    throwError(ctx,
        "Incompatible type at " + token + " (expected: " + expectedType
            + ", actual: " + actualType,
        SEMANTIC_ERROR_CODE);
  }

  public void variableNotDefinedInScopeError(ParseTree ctx, String token) {
    throwError(ctx, "Variable " + token + " is not defined in this scope", SEMANTIC_ERROR_CODE);
  }

  public void redefineError(ParseTree ctx, String token) {
    throwError(ctx, "\"" + token + "\"" + " is already defined in this scope",
        SEMANTIC_ERROR_CODE);
  }

  public void incorrectNumberOfParametersError(ParseTree ctx, String funcToken, int expected,
      int actual) {
    throwError(ctx,
        "Incorrect number of parameters for " + funcToken + " (expected: " + expected + ", actual: "
            + actual + ")", SEMANTIC_ERROR_CODE);
  }

  public void functionNoReturnError(ParseTree ctx, String funcToken) {
    throwError(ctx, "Function " + funcToken + " is not ended with a return or an exit statement.",
        SYNTAX_ERROR_CODE);
  }

  public void cantReturnFromGlobalScope(ParseTree ctx) {
    throwError(ctx, "Cannot return from the global scope.", SEMANTIC_ERROR_CODE);
  }

  public void intValueOutofBoundsError(ParseTree ctx) {
    throwError(ctx,
        "Integer out of bounds error, expected between " + INT_MIN_VALUE + " and " + INT_MAX_VALUE
            + ".", SYNTAX_ERROR_CODE);
  }

  private String getErrorType(int errorCode) {
    switch (errorCode) {
      case SYNTAX_ERROR_CODE:
        return "Syntactic Error";
      case SEMANTIC_ERROR_CODE:
        return "Semantic Error";
    }
    return "Wrong error code";
  }

  public String getLineandPos(ParseTree ctx) {
    int line = 0;
    int pos = 0;
    if (ctx instanceof ParserRuleContext) {
      ParserRuleContext ctxParse = (ParserRuleContext) ctx;
      line = ctxParse.getStart().getLine();
      pos = ctxParse.getStart().getCharPositionInLine();
    } else if (ctx instanceof TerminalNode) {
      TerminalNode ctxNode = (TerminalNode) ctx;
      line = ctxNode.getSymbol().getLine();
      pos = ctxNode.getSymbol().getCharPositionInLine();
    }
    return "line " + line + ":" + pos;
  }

  private void throwError(ParseTree ctx, String errorMsg, int errorCode) {
    System.err.println(getErrorType(errorCode) + " at " + getLineandPos(ctx) + " -- " + errorMsg);
    System.exit(errorCode);
  }

}
