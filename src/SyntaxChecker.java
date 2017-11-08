import antlr.WaccParser.ExitStatContext;
import antlr.WaccParser.FuncContext;
import antlr.WaccParser.Func_statContext;
import antlr.WaccParserBaseVisitor;
import org.antlr.v4.runtime.tree.ParseTree;

public class SyntaxChecker extends WaccParserBaseVisitor<Boolean> {

  private VisitorErrorHandler visitorErrorHandler = new VisitorErrorHandler();

  @Override
  public Boolean visitFunc(FuncContext ctx) {
    boolean hvReturnOrExit = false;
    System.out.println(ctx.func_stat().getChildCount());
    for(ParseTree tree : ctx.func_stat().children) {
      System.out.println(tree.getText());
      if(tree instanceof Func_statContext) {
        System.out.println("1");
        Func_statContext func_statContext = (Func_statContext) tree;
        System.out.println(func_statContext.getText());
        if(func_statContext.RETURN() != null) {
          hvReturnOrExit = true;
          break;
        }
      }
      if(tree instanceof ExitStatContext) {
        hvReturnOrExit = true;
        break;
      }
    }

    if(!hvReturnOrExit) {
      visitorErrorHandler.functionNoReturnError(ctx, ctx.ident().getText());
    }
    return hvReturnOrExit;
  }
}
