import antlr.WaccParser.BeginStatContext;
import antlr.WaccParser.ExitStatContext;
import antlr.WaccParser.FuncContext;
import antlr.WaccParser.Func_statContext;
import antlr.WaccParser.IfStatContext;
import antlr.WaccParser.Int_literContext;
import antlr.WaccParser.Stat_helperContext;
import antlr.WaccParserBaseVisitor;
import org.antlr.v4.runtime.tree.ParseTree;

public class SyntaxChecker extends WaccParserBaseVisitor<Boolean> {

  private VisitorErrorHandler visitorErrorHandler = new VisitorErrorHandler();

  @Override
  public Boolean visitFunc(FuncContext ctx) {
    System.out.println(visit(ctx.func_stat()));
    if(!visit(ctx.func_stat())) {
      visitorErrorHandler.functionNoReturnError(ctx, ctx.ident().getText());
    }
    return true;
  }

  @Override
  public Boolean visitFunc_stat(Func_statContext ctx) {
    if(ctx.RETURN() != null) {
      return true;
    } else if(ctx.stat_helper() != null) {
      if(ctx.stat_helper() instanceof ExitStatContext) {
        return true;
      } else if(ctx.stat_helper() instanceof IfStatContext) {
        IfStatContext ifStatContext = (IfStatContext) ctx.stat_helper();
        System.out.println("SIZE = " + ifStatContext.stat().size());
        if(ifStatContext.stat().size() == 1) {
          return visit(ifStatContext.stat(0));
        } else if (ifStatContext.stat().size() == 2){
          return visit(ifStatContext.stat(0)) && visit(ifStatContext.stat(1));
        }
      } else if(ctx.func_stat() != null) {
        return visit(ctx.func_stat());
      }
    }
    return false;
  }

  @Override
  public Boolean visitInt_liter(Int_literContext ctx) {
    System.out.println(ctx.getChild(0));
    long cur = Long.parseLong(ctx.getChild(0).getText());
    System.out.println("cur = " + cur);
    if(ctx.int_sign() != null){
      //negative number
      if(cur < VisitorErrorHandler.INT_MIN_VALUE){
        visitorErrorHandler.intValueOutofBoundsError(ctx);
      }
      return true;
    }else{
      if(cur > VisitorErrorHandler.INT_MAX_VALUE){
        visitorErrorHandler.intValueOutofBoundsError(ctx);
      }
      return true;
    }
  }
}


