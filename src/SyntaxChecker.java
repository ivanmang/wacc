import antlr.WaccParser.FuncContext;
import antlr.WaccParserBaseVisitor;

public class SyntaxChecker extends WaccParserBaseVisitor<Type> {

  private VisitorErrorHandler visitorErrorHandler = new VisitorErrorHandler();

  @Override
  public Type visitFunc(FuncContext ctx) {
    if(ctx.func_stat() == null) {
      visitorErrorHandler.functionNoReturnError(ctx, ctx.ident().getText());
    } else {
      if(ctx.func_stat().RETURN() == null) {
        visitorErrorHandler.functionNoReturnError(ctx, ctx.ident().getText());
      }
    }
    return null;
  }
}
