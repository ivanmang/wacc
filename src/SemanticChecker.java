import antlr.WaccParser;
import antlr.WaccParser.*;
import antlr.WaccParserBaseVisitor;

public class SemanticChecker extends WaccParserBaseVisitor<Type>{

  private SymbolTable symbolTable = new SymbolTable(null,null);

  public VisitorErrorHandler visitorErrorHandler = new VisitorErrorHandler();

  public SymbolTable getSymbolTable() {
    return symbolTable;
  }

  public boolean typeChecker(Object type, Type type1){
      Type checkType = (Type) type;
      return checkType.isValidType() && checkType.equals(type1);
  }

  @Override
  public Type visitDeclareAndAssignStat(DeclareAndAssignStatContext ctx) {
    Type expected = visit(ctx.type());
    Type actual = visit(ctx.assign_rhs());
    String identName = ctx.ident().IDENT().getText();
    if(!typeChecker(expected,actual)){
      visitorErrorHandler.incompatibleTypeError(ctx,ctx.ident().IDENT().getText(),expected,actual);
    }else if(symbolTable.contain(identName)){
      visitorErrorHandler.variableRedefineError(ctx,identName);
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
      visitorErrorHandler.incompatibleTypeError(ctx,ctx.assign_rhs().getTokens(0).toString(),expected,actual);
    }
    return null;
  }

  @Override
  public Type visitReadStat(ReadStatContext ctx) {
    Type actual = visit(ctx.assign_lhs());
    if(!typeChecker(WaccParser.CHAR,actual) && !typeChecker(WaccParser.INT, actual)){
      visitorErrorHandler.incompatibleTypeError(ctx,actual);
    }
    return null;
  }

  @Override
  public Type visitFreeStat(FreeStatContext ctx) {
    Type actual = visit(ctx.expr());
    if(!typeChecker(WaccParser.PAIR,actual)){
      visitorErrorHandler.incompatibleTypeError(ctx,actual);
    }
    return null;
  }

  @Override
  public Type visitExitStat(ExitStatContext ctx) {
    Type actual = visit(ctx.expr());
    if(!typeChecker(WaccParser.INT,actual)){
      visitorErrorHandler.incompatibleTypeError(ctx,actual);
    }
    return null;
  }

  @Override
  public Type visitPrintStat(PrintStatContext ctx) {
    Type actual = visit(ctx.expr());
    if(!actual.isValidType()){
      visitorErrorHandler.incompatibleTypeError(ctx,actual);
    }
    return null;
  }

  @Override
  public Type visitPrintlnStat(PrintlnStatContext ctx) {
    Type actual = visit(ctx.expr());
    if(!actual.isValidType()){
      visitorErrorHandler.incompatibleTypeError(ctx,actual);
    }
    return null;
  }


  @Override
  public Type visitIfStat(IfStatContext ctx) {
    Type condition = visit(ctx.expr());
    if (!typeChecker(WaccParser.BOOL, condition)) {
      visitorErrorHandler.incompatibleTypeError(ctx,condition);
    }
    symbolTable = symbolTable.enterScope(symbolTable);
    Type fstat = visit(ctx.stat(0));
    symbolTable = symbolTable.exitScope(symbolTable);

    symbolTable = symbolTable.enterScope(symbolTable);
    Type sstat = visit(ctx.stat(1));
    symbolTable = symbolTable.exitScope(symbolTable);

    if(sstat == null){//only have if then , no else
      symbolTable = symbolTable.enterScope(symbolTable);
      Type stat = visit(ctx.stat(0));
      symbolTable = symbolTable.exitScope(symbolTable);
      return stat;

    }
    return typeChecker(fstat,sstat) ? fstat : null;
  }

  @Override
  public Type visitWhileStat(WhileStatContext ctx) {
    Type conditon = visit(ctx.expr());
    if(!typeChecker(WaccParser.BOOL,conditon)){
      visitorErrorHandler.incompatibleTypeError(ctx,conditon);
    }
    symbolTable = symbolTable.enterScope(symbolTable);
    Type stat = visit(ctx.stat());
    symbolTable = symbolTable.exitScope(symbolTable);
    return stat;
  }

  @Override
  public Type visitBeginStat(BeginStatContext ctx) {
    symbolTable = symbolTable.enterScope(symbolTable);
    Type stat = visit(ctx.stat());
    symbolTable = symbolTable.exitScope(symbolTable);
    return stat;
  }

  @Override
  public Type visitAssign_lhs(Assign_lhsContext ctx) {
    if(ctx.array_elem() != null){
      return visit(ctx.array_elem());
    }else if(ctx.ident() != null){
      return visit(ctx.ident());
    }else if(ctx.pair_elem() != null){
      return visit(ctx.pair_elem());
    }
    return null;
  }


  @Override
  public Type visitArg_list(Arg_listContext ctx) {
    for(int i =0 ; i<ctx.expr().size();i++){
      Type argType = visit(ctx.expr(i));
      //check type match with function para
    }
    return null;
  }


}
