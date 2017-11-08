import antlr.WaccParser.Int_literContext;
import antlr.WaccParserBaseVisitor;

public class AssignmentOutOfBoundsChecker extends WaccParserBaseVisitor<Boolean> {
  VisitorErrorHandler visitorErrorHandler = new VisitorErrorHandler();

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
    return null;
  }
}
