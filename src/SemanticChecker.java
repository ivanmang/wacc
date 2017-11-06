import antlr.WaccParser;
import antlr.WaccParser.Arg_listContext;
import antlr.WaccParser.Array_typeContext;
import antlr.WaccParser.AssignStatContext;
import antlr.WaccParser.Assign_lhsContext;
import antlr.WaccParser.Base_typeContext;
import antlr.WaccParser.BeginStatContext;
import antlr.WaccParser.DeclareAndAssignStatContext;
import antlr.WaccParser.ExitStatContext;
import antlr.WaccParser.ExprContext;
import antlr.WaccParser.FreeStatContext;
import antlr.WaccParser.IfStatContext;
import antlr.WaccParser.Pair_elem_typeContext;
import antlr.WaccParser.Pair_typeContext;
import antlr.WaccParser.PrintStatContext;
import antlr.WaccParser.PrintlnStatContext;
import antlr.WaccParser.ReadStatContext;
import antlr.WaccParser.TypeContext;
import antlr.WaccParser.WhileStatContext;
import antlr.WaccParserBaseVisitor;

public class SemanticChecker extends WaccParserBaseVisitor<Type> {

  private SymbolTable symbolTable = new SymbolTable(null, null);

  public VisitorErrorHandler visitorErrorHandler = new VisitorErrorHandler();

  public SymbolTable getSymbolTable() {
    return symbolTable;
  }

  private Type intType = new BaseType(WaccParser.INT);
  private Type charType = new BaseType(WaccParser.CHAR);
  private Type boolType = new BaseType(WaccParser.BOOL);
  private Type stringType = new BaseType(WaccParser.STRING);

  public boolean typeChecker(Type type1, Type type2) {
    return type1.isValidType() && type2.isValidType() && type1.equals(type2);
  }

  @Override
  public Type visitDeclareAndAssignStat(DeclareAndAssignStatContext ctx) {
    System.out.println("CHEKING EXPECTED");
    Type expected = visitType(ctx.type());
    System.out.println("CHECKING ACTUAL");
    Type actual = visit(ctx.assign_rhs());
    String identName = ctx.ident().IDENT().getText();
    if (!typeChecker(expected, actual)) {
      visitorErrorHandler
          .incompatibleTypeError(ctx, ctx.ident().IDENT().getText(), expected, actual);
    } else if (symbolTable.contain(identName)) {
      visitorErrorHandler.variableRedefineError(ctx, identName);
    } else {
      symbolTable.insert(ctx.ident().getText(), expected);
    }
    return null;
  }

  @Override
  public Type visitAssignStat(AssignStatContext ctx) {
    Type expected = visit(ctx.assign_lhs());
    Type actual = visit(ctx.assign_rhs());
    if (!typeChecker(expected, actual)) {
      visitorErrorHandler
          .incompatibleTypeError(ctx, ctx.assign_rhs().getTokens(0).toString(), expected, actual);
    }
    return null;
  }

  @Override
  public Type visitReadStat(ReadStatContext ctx) {
    Type actual = visit(ctx.assign_lhs());
    if (!typeChecker(charType, actual) && !typeChecker(intType, actual)) {
      visitorErrorHandler.incompatibleTypeError(ctx, actual);
    }
    return null;
  }

  @Override
  public Type visitFreeStat(FreeStatContext ctx) {
    Type actual = visit(ctx.expr());
    if (actual.getID() != ID.Pair) {
      visitorErrorHandler.incompatibleTypeError(ctx, actual);
    }
    return null;
  }

  @Override
  public Type visitExitStat(ExitStatContext ctx) {
    Type actual = visit(ctx.expr());
    if (!typeChecker(intType, actual)) {
      visitorErrorHandler.incompatibleTypeError(ctx, actual);
    }
    return null;
  }

  @Override
  public Type visitPrintStat(PrintStatContext ctx) {
    Type actual = visit(ctx.expr());
    if (!actual.isValidType()) {
      visitorErrorHandler.incompatibleTypeError(ctx, actual);
    }
    return null;
  }

  @Override
  public Type visitPrintlnStat(PrintlnStatContext ctx) {
    Type actual = visit(ctx.expr());
    if (!actual.isValidType()) {
      visitorErrorHandler.incompatibleTypeError(ctx, actual);
    }
    return null;
  }


  @Override
  public Type visitIfStat(IfStatContext ctx) {
    Type condition = visit(ctx.expr());
    if (!typeChecker(boolType, condition)) {
      visitorErrorHandler.incompatibleTypeError(ctx, condition);
    }
    symbolTable = symbolTable.enterScope(symbolTable);
    Type fstat = visit(ctx.stat(0));
    symbolTable = symbolTable.exitScope(symbolTable);

    symbolTable = symbolTable.enterScope(symbolTable);
    Type sstat = visit(ctx.stat(1));
    symbolTable = symbolTable.exitScope(symbolTable);

    if (sstat == null) {//only have if then , no else
      symbolTable = symbolTable.enterScope(symbolTable);
      Type stat = visit(ctx.stat(0));
      symbolTable = symbolTable.exitScope(symbolTable);
      return stat;

    }
    return typeChecker(fstat, sstat) ? fstat : null;
  }

  @Override
  public Type visitWhileStat(WhileStatContext ctx) {
    Type conditon = visit(ctx.expr());
    if (!typeChecker(boolType, conditon)) {
      visitorErrorHandler.incompatibleTypeError(ctx, conditon);
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
    if (ctx.array_elem() != null) {
      return visit(ctx.array_elem());
    } else if (ctx.ident() != null) {
      return visit(ctx.ident());
    } else if (ctx.pair_elem() != null) {
      return visit(ctx.pair_elem());
    }
    return null;
  }


  @Override
  public Type visitArg_list(Arg_listContext ctx) {
    for (int i = 0; i < ctx.expr().size(); i++) {
      Type argType = visit(ctx.expr(i));
      //check type match with function para
    }
    return null;
  }

  @Override
  public Type visitBase_type(Base_typeContext ctx) {
    if(ctx.BOOL() != null) {
      return new BaseType(WaccParser.BOOL);
    } else if(ctx.CHAR() != null) {
      return new BaseType(WaccParser.CHAR);
    } else if(ctx.INT() != null) {
      return new BaseType(WaccParser.INT);
    } else if(ctx.STRING() != null) {
      return new BaseType(WaccParser.STRING);
    }
    System.out.println("VISIT BASE TYPE ERROR");
    return null;
  }

  @Override
  public Type visitArray_type(Array_typeContext ctx) {
    if(ctx.array_type().array_type() != null){
      return new ArrayType(visit(ctx.array_type()));
    } else if(ctx.base_type() != null){
      return new ArrayType(visit(ctx.base_type()));
    } else if(ctx.pair_type() != null) {
      return new ArrayType(visit(ctx.pair_type()));
    }
    System.out.println("VISIT ARRAY TYPE ERROR");
    return null;
  }

  @Override
  public Type visitPair_type(Pair_typeContext ctx) {
    Type fst = visit(ctx.pair_elem_type(0));
    Type snd = visit(ctx.pair_elem_type(1));
    return new PairType(fst, snd);
  }

  @Override
  public Type visitPair_elem_type(Pair_elem_typeContext ctx) {
    if(ctx.PAIR() != null) {
      return new PairType();
    } else if(ctx.array_type() != null) {
      return visit(ctx.array_type());
    } else if(ctx.base_type() != null){
      return visit(ctx.base_type());
    }
    System.out.println("VISIT PAIR ELEM TYPE ERROR");
    return null;
  }

  @Override
  public Type visitExpr(ExprContext ctx) {
    if(ctx.int_liter()!= null){
      return visit(ctx.int_liter());
    }else if(ctx.bool_liter() != null){
      return visit(ctx.bool_liter());
    }else if(ctx.array_elem() != null){
      return visit(ctx.array_elem());
    }else if(ctx.binary_oper() != null){
      return visit(ctx.binary_oper());
    }else if(ctx.pair_liter() != null){
      return  visit(ctx.pair_liter());
    }else if(ctx.unary_oper()!= null){
      return visit(ctx.unary_oper());
    }else if(ctx.ident()!= null){
      return visit(ctx.ident());
    }else if(ctx.CHAR_LIT()!= null){
      return new BaseType(WaccParser.CHAR);
    }else if(ctx.CHARACTER_LIT()!= null){
      return new BaseType(WaccParser.CHAR);
    }else if(ctx.binary_oper()!= null){
      return visit(ctx.binary_oper());
    }else if(ctx.OPEN_PARENTHESES()!= null){
      return visit(ctx.expr(0));
    }
    return null;
  }


}
