import antlr.WaccParser.AssignStatContext;
import antlr.WaccParser.DeclareAndAssignStatContext;
import antlr.WaccParser.ExitStatContext;
import antlr.WaccParser.FreeStatContext;
import antlr.WaccParser.FuncContext;
import antlr.WaccParser.IfStatContext;
import antlr.WaccParser.PrintStatContext;
import antlr.WaccParser.PrintlnStatContext;
import antlr.WaccParser.ReadStatContext;
import antlr.WaccParser.ReturnStatContext;
import antlr.WaccParser.SkipStatContext;
import antlr.WaccParser.StatToStatContext;
import antlr.WaccParserBaseVisitor;

public class FunctionReturnChecker extends WaccParserBaseVisitor<Boolean> {

  private VisitorErrorHandler visitorErrorHandler = new VisitorErrorHandler();

  @Override
  public Boolean visitFunc(FuncContext ctx) {
    System.out.println("visiting function");
    if (ctx.stat() instanceof IfStatContext) {
      System.out.println("Checking if");
      IfStatContext ifStatContext = (IfStatContext) ctx.stat();
      System.out.println(ifStatContext.stat(0).getText());
      System.out.println(ifStatContext.stat(1).getText());
      if (!(visit(ifStatContext.stat(0)) && visit(ifStatContext.stat(1)))) {
        visitorErrorHandler.functionNoReturnError(ctx, ctx.ident().getText());
      }
    } else {
      if (!visit(ctx.stat())) {
        visitorErrorHandler.functionNoReturnError(ctx, ctx.ident().getText());
      }
    }
    return true;
  }

  @Override
  public Boolean visitAssignStat(AssignStatContext ctx) {
    return false;
  }

  @Override
  public Boolean visitDeclareAndAssignStat(DeclareAndAssignStatContext ctx) {
    return false;
  }

  @Override
  public Boolean visitFreeStat(FreeStatContext ctx) {
    return false;
  }

  @Override
  public Boolean visitPrintlnStat(PrintlnStatContext ctx) {
    return false;
  }

  @Override
  public Boolean visitPrintStat(PrintStatContext ctx) {
    return false;
  }

  @Override
  public Boolean visitSkipStat(SkipStatContext ctx) {
    System.out.println("visiting skip statement");
    return false;
  }

  @Override
  public Boolean visitReadStat(ReadStatContext ctx) {
    return false;
  }

  @Override
  public Boolean visitReturnStat(ReturnStatContext ctx) {
    System.out.println("visiting return stat");
    return true;
  }

  @Override
  public Boolean visitIfStat(IfStatContext ctx) {
    System.out.println("visiting if stat");
    return visit(ctx.stat(0)) && visit(ctx.stat(1));
  }

  @Override
  public Boolean visitExitStat(ExitStatContext ctx) {
    return true;
  }

  @Override
  public Boolean visitStatToStat(StatToStatContext ctx) {
    System.out.println("visiting statement to statement");
    if (ctx.stat(0) instanceof ReturnStatContext) {
      return false;
    }
    if (ctx.stat(1) instanceof IfStatContext) {
      IfStatContext ifStatContext = (IfStatContext) ctx.stat(1);
      return visit(ifStatContext.stat(0)) && visit(ifStatContext.stat(1));
    }
    return visit(ctx.stat(1));
  }
}


