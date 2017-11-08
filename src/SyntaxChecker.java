import antlr.WaccParser.BeginStatContext;
import antlr.WaccParser.ExitStatContext;
import antlr.WaccParser.FuncContext;
import antlr.WaccParser.Func_statContext;
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
      } else if(ctx.func_stat() != null) {
        return visit(ctx.func_stat());
      }
    }
    return false;
  }
}


