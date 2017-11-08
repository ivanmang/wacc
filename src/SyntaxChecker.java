import antlr.WaccParser.Int_literContext;
import antlr.WaccParserBaseVisitor;

public class SyntaxChecker extends WaccParserBaseVisitor<Boolean> {

  private VisitorErrorHandler visitorErrorHandler = new VisitorErrorHandler();

//  @Override
//  public Boolean visitFunc(FuncContext ctx) {
//    System.out.println("visiting function context");
//    System.out.println(visit(ctx.func_stat()));
//    if(!visit(ctx.func_stat())) {
//      visitorErrorHandler.functionNoReturnError(ctx, ctx.ident().getText());
//    }
//    return true;
//  }
//
//  @Override
//  public Boolean visitFunc_stat(Func_statContext ctx) {
//    System.out.println("visiting function statement");
//    if(ctx.RETURN() != null) {
//      return true;
//    } else if(ctx.stat_helper() != null) {
//      if(ctx.stat_helper() instanceof ExitStatContext) {
//        return true;
//      } else if(ctx.stat_helper() instanceof IfStatContext) {
//        IfStatContext ifStatContext = (IfStatContext) ctx.stat_helper();
//        System.out.println("SIZE = " + ifStatContext.stat().size());
//        if(ifStatContext.stat().size() == 1) {
//          return visit(ifStatContext.stat(0));
//        } else if (ifStatContext.stat().size() == 2){
//          return visit(ifStatContext.stat(0)) && visit(ifStatContext.stat(1));
//        }
//      } else if(ctx.func_stat() != null) {
//        return visit(ctx.func_stat());
//      }
//    }
//    return false;
//  }

  @Override
  public Boolean visitInt_liter(Int_literContext ctx) {
    long cur = 0;
    if(ctx.getChildCount() == 1) {
      cur = Long.parseLong(ctx.getChild(0).getText());
      System.out.println("Count = 1, cur = " + cur);
      if(cur >= VisitorErrorHandler.INT_MAX_VALUE){
        visitorErrorHandler.intValueOutofBoundsError(ctx);
      }
    } else if(ctx.getChildCount() == 2){
      cur = Long.parseLong(ctx.getChild(1).getText());
      System.out.println("Count = 2, cur = " + cur);
      if(ctx.int_sign().MINUS() != null){
        //negative number
        if((0-cur) < VisitorErrorHandler.INT_MIN_VALUE){
          visitorErrorHandler.intValueOutofBoundsError(ctx);
        }
      }else if(ctx.int_sign().PLUS() != null){
        if(cur > VisitorErrorHandler.INT_MAX_VALUE){
          visitorErrorHandler.intValueOutofBoundsError(ctx);
        }
      }
    }
    System.out.println("END THIS PLS");
    return null;
  }
}


