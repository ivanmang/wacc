import antlr.WaccParser.FuncContext;
import antlr.WaccParser.Func_statContext;
import antlr.WaccParserBaseVisitor;
import org.antlr.v4.runtime.tree.ParseTree;

public class SyntaxChecker extends WaccParserBaseVisitor<Boolean> {

  private VisitorErrorHandler visitorErrorHandler = new VisitorErrorHandler();

  @Override
  public Boolean visitFunc(FuncContext ctx) {
    boolean hvReturn = false;
    for(ParseTree tree : ctx.func_stat().children) {
      if(tree instanceof Func_statContext) {
        Func_statContext func_statContext = (Func_statContext) tree;
        if(func_statContext.RETURN() != null) {
          hvReturn = true;
          break;
        }
      }
    }

    if(!hvReturn) {
      visitorErrorHandler.functionNoReturnError(ctx, ctx.ident().getText());
    }
    return hvReturn;
  }
}
