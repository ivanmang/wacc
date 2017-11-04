import antlr.WaccParser;
import antlr.WaccParser.*;
import antlr.WaccParserBaseVisitor;

public class SemanticChecker extends WaccParserBaseVisitor<Type>{


  SymbolTable symbolTable = new SymbolTable();

  public boolean typeChecker(Type type, Type type1){
    return type.equals(type1);
  }

  @Override
  public Type visitDeclareAndAssignStat(DeclareAndAssignStatContext ctx) {
    Type expected = visit(ctx.type());
    Type actual = visit(ctx.assign_rhs());
    if(!typeChecker(expected,actual)){
      //type mismatch
    }else if(symbolTable.contain(ctx.ident().getText())){
      //variable redeclare
    }else{
      symbolTable.insert(ctx.ident().getText(),expected);
    }
    return null;
  }

  @Override
  public Type visitAssignStat(AssignStatContext ctx) {
    Type expected = visit(ctx.assign_lhs());
    Type actual = visit(ctx.assign_rhs());
    if(!typeChecker(expected,actual)){
      //type mismatch
    }
    return null;
  }

  @Override
  public Type visitReadStat(ReadStatContext ctx) {
    Type actual = visit(ctx.assign_lhs());
    if(!typeChecker(WaccParser.CHAR,actual) && !typeChecker(WaccParser.INT, actual)){
      //type mismatch
    }
    return null;
  }

  @Override
  public Type visitFreeStat(FreeStatContext ctx) {
    Type actual = visit(ctx.expr());
    if(!typeChecker(WaccParser.PAIR,actual)){
      //type mismatch
    }
    return null;
  }

  @Override
  public Type visitExitStat(ExitStatContext ctx) {
    Type actual = visit(ctx.expr());
    if(!typeChecker(WaccParser.INT,actual)){
      //type mismatch
    }
    return null;
  }

  @Override
  public Type visitPrintStat(PrintStatContext ctx) {
    Type actual = visit(ctx.expr());
    if(!actual.isValidType()){
      //invalid type can't be print
    }
    return null;
  }

  @Override
  public Type visitPrintlnStat(PrintlnStatContext ctx) {
    Type actual = visit(ctx.expr());
    if(!actual.isValidType()){
      //invalid type can't be print
    }
    return null;
  }


  @Override
  public Type visitIfStat(IfStatContext ctx) {
    Type condition = visit(ctx.expr());
    if (!typeChecker(WaccParser.BOOL, condition)) {
      //type mismatch
    }
    //new scope
    Type fstat = visit(ctx.stat(0));
    //end scope

    //new scope
    Type sstat = visit(ctx.stat(1));
    //end scope

    if(sstat == null){//only have if then , no else
      //new scope
      Type stat = visit(ctx.stat(0));
      //end scope
      return stat;

    }
    return null; //return stat conditional branch
  }

  @Override
  public Type visitWhileStat(WhileStatContext ctx) {
    Type conditon = visit(ctx.expr());
    if(!typeChecker(WaccParser.BOOL,conditon)){
      //type mismatch
    }
    //new scope
    Type stat = visit(ctx.stat());
    //end scope
    return stat;
  }

  @Override
  public Type visitBeginStat(BeginStatContext ctx) {
    //new scope
    Type stat = visit(ctx.stat());
    //end scope
    return stat;
  }
}
