import Utils.*;
import antlr.WaccParser;
import antlr.WaccParser.*;
import antlr.WaccParserBaseVisitor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.antlr.v4.runtime.tree.TerminalNode;

public class SemanticChecker extends WaccParserBaseVisitor<Type> {

  public VisitorErrorHandler visitorErrorHandler = new VisitorErrorHandler();
  private final SymbolNode globalSymbolNode = new SymbolNode(new HashMap<>(), null, new ArrayList<>());
  private SymbolNode symbolNode = globalSymbolNode;
  private Map<String, Function> functionList = new HashMap<>();
  private Type intType = new BaseType(WaccParser.INT);
  private Type charType = new BaseType(WaccParser.CHAR);
  private Type boolType = new BaseType(WaccParser.BOOL);
  private Type stringType = new BaseType(WaccParser.STRING);

  public SymbolNode getGlobalSymbolNode() {
    return globalSymbolNode;
  }

  public Map<String, Function> getFunctionList() {
    return functionList;
  }

  public boolean typeChecker(Type type1, Type type2) {
//    System.out.println("type checking");
//    if (type1 == null) {
//      System.out.println("Type 1 null");
//    }
//    if (type2 == null) {
//      System.out.println("Type 2 null");
//  }
    return type1.isValidType() && type2.isValidType() && type1.equals(type2);
  }

  @Override
  public Type visitProg(ProgContext ctx) {
    for (FuncContext funcContext : ctx.func()) {
      ArrayList<String> identList = new ArrayList<>();
      List<Type> typeList = new ArrayList<>();

      Type returnType = visit(funcContext.type());

      if (functionList.containsKey(funcContext.ident().getText())) {
        visitorErrorHandler.redefineError(funcContext, funcContext.ident().getText());
      }

      if (funcContext.param_list() != null) {
        for (ParamContext paramContext : funcContext.param_list().param()) {
          String ident = paramContext.ident().getText();
          Type type = visit(paramContext.type());
          identList.add(ident);
          typeList.add(type);
        }
      }
      functionList
          .put(funcContext.ident().getText(), new Function(returnType, identList, typeList));

    }
    for (FuncContext funcContext : ctx.func()) {
      visit(funcContext);
    }
    visit(ctx.stat());
    return null;
  }

  public Type visitFunc(FuncContext ctx) {
//    System.out.println("visiting func");

    Type expected = visit(ctx.type());

    symbolNode = symbolNode.enterScope(symbolNode);

    if (ctx.param_list() != null) {
      for (ParamContext paramContext : ctx.param_list().param()) {
        String ident = paramContext.ident().getText();
        Type type = visit(paramContext.type());
        symbolNode.insert(ident, type);
      }
    }

    Type returnType = visit(ctx.stat());

    if (!typeChecker(expected, returnType)) {
      visitorErrorHandler
          .incompatibleTypeError(ctx, ctx.ident().getText(), expected,
              returnType);
    }
    functionList.get(ctx.ident().getText()).setSymbolNode(symbolNode);
    symbolNode = symbolNode.exitScope();

    return expected;
  }

  @Override
  public Type visitDeclareAndAssignStat(DeclareAndAssignStatContext ctx) {
//    System.out.println("Visiting declare and assign");
    Type expected = visitType(ctx.type());
    Type actual = visit(ctx.assign_rhs());
    String identName = ctx.ident().getText();
    if (!typeChecker(expected, actual)) {
      visitorErrorHandler
          .incompatibleTypeError(ctx, ctx.assign_rhs().getText(), expected,
              actual);
    } else if (symbolNode.getParent() != null) {
//      symbolNode.getParentSymbolTable().printTable();
      if (symbolNode.contain(identName) && !symbolNode.getParent()
          .contain(identName)) {
        visitorErrorHandler.redefineError(ctx, identName);
      } else {
        symbolNode.insert(ctx.ident().getText(), expected);
      }
    } else if (symbolNode.getParent() == null) {
      if (symbolNode.contain(identName)) {
        visitorErrorHandler.redefineError(ctx, identName);
      } else {
        symbolNode.insert(ctx.ident().getText(), expected);
      }
    }
    return null;
  }

  @Override
  public Type visitAssignStat(AssignStatContext ctx) {
//    System.out.println("Visiting Assign Stat");
    Type expected = visit(ctx.assign_lhs());
    Type actual = visit(ctx.assign_rhs());
    System.out.println(expected + " " + actual);
    if (functionList.containsKey(ctx.assign_lhs().getText())) {
      visitorErrorHandler.variableNotDefinedInScopeError(ctx, ctx.assign_lhs().getText());
    }
    if (!typeChecker(expected, actual)) {
      visitorErrorHandler
          .incompatibleTypeError(ctx, ctx.assign_rhs().getTokens(0).toString(),
              expected, actual);
    }
    return null;
  }

  @Override
  public Type visitReadStat(ReadStatContext ctx) {
//    System.out.println("Visiting Read Stat");
    Type actual = visit(ctx.assign_lhs());
    if (!typeChecker(charType, actual) && !typeChecker(intType, actual)) {
      visitorErrorHandler.incompatibleTypeError(ctx, actual);
    }
    return null;
  }

  @Override
  public Type visitFreeStat(FreeStatContext ctx) {
//    System.out.println("Visiting Free stat");
    Type actual = visit(ctx.expr());
    if (actual.getID() != ID.Pair) {
      visitorErrorHandler.incompatibleTypeError(ctx, actual);
    }
    return null;
  }

  @Override
  public Type visitExitStat(ExitStatContext ctx) {
//    System.out.println("Visiting exit stat");
    Type actual = visit(ctx.expr());
    if (!typeChecker(intType, actual)) {
      visitorErrorHandler.incompatibleTypeError(ctx, actual);
    }
    return new AllType();
  }

  @Override
  public Type visitPrintStat(PrintStatContext ctx) {
//    System.out.println("Visiting print stat");
    Type actual = visit(ctx.expr());
    if (!actual.isValidType()) {
      visitorErrorHandler.incompatibleTypeError(ctx, actual);
    }
    return null;
  }

  @Override
  public Type visitPrintlnStat(PrintlnStatContext ctx) {
//    System.out.println("visiting println stat");
    Type actual = visit(ctx.expr());
    if (!actual.isValidType()) {
      visitorErrorHandler.incompatibleTypeError(ctx, actual);
    }
    return null;
  }


  @Override
  public Type visitIfStat(IfStatContext ctx) {
//    System.out.println("Visiting if stat");
    Type condition = visit(ctx.expr());
    if (!typeChecker(boolType, condition)) {
      visitorErrorHandler.incompatibleTypeError(ctx, condition);
    }
    Type stat;
    if (ctx.stat(1) != null) { //have if, then, else
      symbolNode = symbolNode.enterScope(symbolNode);
      stat = visit(ctx.stat(0));
      symbolNode = symbolNode.exitScope();

      symbolNode = symbolNode.enterScope(symbolNode);
      Type sstat = visit(ctx.stat(1));
      symbolNode = symbolNode.exitScope();
    } else { //only have if then , no else
      symbolNode = symbolNode.enterScope(symbolNode);
      stat = visit(ctx.stat(0));
      symbolNode = symbolNode.exitScope();
    }
    return stat;

  }

  @Override
  public Type visitWhileStat(WhileStatContext ctx) {
//    System.out.println("visiting while stat");
    symbolNode = symbolNode.enterScope(symbolNode);
    Type conditon = visit(ctx.expr());
    if (!typeChecker(boolType, conditon)) {
      visitorErrorHandler.incompatibleTypeError(ctx, ctx.expr().getText(), boolType, conditon);
    }
    Type stat = visit(ctx.stat());
    symbolNode = symbolNode.exitScope();
    return stat;
  }

  @Override
  public Type visitDoWhileStat(DoWhileStatContext ctx) {
    symbolTable = symbolTable.enterScope(symbolTable);
    Type conditon = visit(ctx.expr());
    if (!typeChecker(boolType, conditon)) {
      visitorErrorHandler.incompatibleTypeError(ctx, ctx.expr().getText(), boolType, conditon);
    }
    symbolTable = symbolTable.exitScope(symbolTable);
    return visit(ctx.stat());
  }

  @Override
  public Type visitForStat(ForStatContext ctx) {
    symbolTable = symbolTable.enterScope(symbolTable);
    visit(ctx.init_stat());

    Type cond = visit(ctx.expr());
    if (!typeChecker(cond, boolType)) {
      visitorErrorHandler.incompatibleTypeError(ctx, ctx.expr().getText(), boolType, cond);
    }
    visit(ctx.stat(0));

    return null;
  }

  @Override
  public Type visitInitAssignStat(InitAssignStatContext ctx) {
    Type expected = visit(ctx.assign_lhs());
    Type actual = visit(ctx.assign_rhs());
    System.out.println(expected + " " + actual);
    if (functionList.containsKey(ctx.assign_lhs().getText())) {
      visitorErrorHandler.variableNotDefinedInScopeError(ctx, ctx.assign_lhs().getText());
    }
    if (!typeChecker(expected, actual)) {
      visitorErrorHandler
          .incompatibleTypeError(ctx, ctx.assign_rhs().getTokens(0).toString(),
              expected, actual);
    }
    return null;
  }

  @Override
  public Type visitBeginStat(BeginStatContext ctx) {
//    System.out.println("visiting begin stat");
    symbolNode = symbolNode.enterScope(symbolNode);
    Type stat = visit(ctx.stat());
    symbolNode = symbolNode.exitScope();
    return stat;
  }

  @Override
  public Type visitAssign_lhs(Assign_lhsContext ctx) {
//    System.out.println("visiting assign lhs");
    return visitChildren(ctx);
  }


  @Override
  public Type visitArg_list(Arg_listContext ctx) {
//    System.out.println("visiting arg list");
    Function_callContext parent = (Function_callContext) ctx.getParent();
    Function curFunc = functionList.get(parent.ident().getText());
    for (int i = 0; i < ctx.expr().size(); i++) {
      Type actualType = visit(ctx.expr(i));
      Type expectedType = curFunc.getType(i);
      if (!typeChecker(expectedType, actualType)) {
        visitorErrorHandler
            .incompatibleTypeError(ctx, ctx.expr(i).getText(), expectedType,
                actualType);
      }
    }
    return null;
  }

  @Override
  public Type visitBase_type(Base_typeContext ctx) {
//    System.out.println("visiting base type");
    if (ctx.BOOL() != null) {
      return new BaseType(WaccParser.BOOL);
    } else if (ctx.CHAR() != null) {
      return new BaseType(WaccParser.CHAR);
    } else if (ctx.INT() != null) {
      return new BaseType(WaccParser.INT);
    } else if (ctx.STRING() != null) {
      return new BaseType(WaccParser.STRING);
    }
//    System.out.println("VISIT BASE TYPE ERROR");
    return null;
  }

  @Override
  public Type visitArray_type(Array_typeContext ctx) {
//    System.out.println("visiting array type");
    if (ctx.array_type() != null) {
      return new ArrayType(visit(ctx.array_type()));
    } else if (ctx.base_type() != null) {
      return new ArrayType(visit(ctx.base_type()));
    } else if (ctx.pair_type() != null) {
      return new ArrayType(visit(ctx.pair_type()));
    }
//    System.out.println("VISIT ARRAY TYPE ERROR");
    return null;
  }

  @Override
  public Type visitPair_type(Pair_typeContext ctx) {
//    System.out.println("visiting pair type");
    Type fst = visit(ctx.pair_elem_type(0));
    Type snd = visit(ctx.pair_elem_type(1));
    return new PairType(fst, snd);
  }

  @Override
  public Type visitPair_elem_type(Pair_elem_typeContext ctx) {
//    System.out.println("visiting pair elem type");
    if (ctx.PAIR() != null) {
      return new PairType();
    } else if (ctx.array_type() != null) {
      return visit(ctx.array_type());
    } else if (ctx.base_type() != null) {
      return visit(ctx.base_type());
    }
//    System.out.println("VISIT PAIR ELEM TYPE ERROR");
    return null;
  }

  @Override
  public Type visitExpr(ExprContext ctx) {
//    System.out.println("visiting expr");
    if (ctx.int_liter() != null) {
      return intType;
    } else if (ctx.bool_liter() != null) {
      return boolType;
    } else if (ctx.CHAR_LIT() != null) {
      return charType;
    } else if (ctx.CHARACTER_LIT() != null) {
      return stringType;
    } else if (ctx.pair_liter() != null) {
      return new PairType();
    } else if (ctx.OPEN_PARENTHESES() != null) {
      return visit(ctx.expr(0));
    } else if (ctx.array_elem() != null) {
      return visit(ctx.array_elem());
    } else if (ctx.binary_oper_and_or() != null) {
      return visit(ctx.binary_oper_and_or());
    } else if (ctx.binary_oper_eql() != null) {
      return visit(ctx.binary_oper_eql());
    } else if (ctx.binary_oper_mul() != null) {
      return visit(ctx.binary_oper_mul());
    } else if (ctx.binary_oper_plus() != null) {
      return visit(ctx.binary_oper_plus());
    } else if (ctx.unary_oper() != null) {
      return visit(ctx.unary_oper());
    } else if (ctx.ident() != null) {
      return visit(ctx.ident());
    } else if (ctx.side_effect() != null) {
      return visit(ctx.side_effect());
    }
    return null;
  }

  @Override
  public Type visitSide_effect(Side_effectContext ctx) {
    Type actual = visit(ctx.ident());
    if (ctx.INC() != null || ctx.DEC() != null || ctx.INCNUM() != null || ctx.DECNUM() != null) {
      if (!typeChecker(actual, charType) && !typeChecker(actual, intType)) {
        visitorErrorHandler
            .incompatibleTypeError(ctx, ctx.ident().getText(), intType, charType, actual);
      }
    }
    return actual;
  }


  @Override
  public Type visitAssign_rhs(Assign_rhsContext ctx) {
//    System.out.println("visiting assign rhs");
    return visitChildren(ctx);
  }

  @Override
  public Type visitFunction_call(Function_callContext ctx) {
//    System.out.println("visiting function call");
    String ident = ctx.ident().getText();
    Function curFunc = functionList.get(ident);
    int expectedSize = curFunc.getParamSize();
    int actualSize = 0;
    if (ctx.arg_list() != null) {
      actualSize = ctx.arg_list().expr().size();
    }
    if (expectedSize != actualSize) {
      visitorErrorHandler
          .incorrectNumberOfParametersError(ctx, ident, expectedSize,
              actualSize);
    }
    if (ctx.arg_list() != null) {
      visit(ctx.arg_list());
    }
    return functionList.get(ident).getReturnType();
  }

  @Override
  public Type visitReturnStat(ReturnStatContext ctx) {
    if (symbolNode.getParent() == null) {
      visitorErrorHandler.cantReturnFromGlobalScope(ctx);
    }
    return visit(ctx.expr());
  }

  @Override
  public Type visitArray_elem(Array_elemContext ctx) {
//    System.out.println("visiting array elem");
    for (ExprContext exprContext : ctx.expr()) {
      Type typeInArray = visit(exprContext);
      if (!typeChecker(intType, typeInArray)) {
        visitorErrorHandler
            .incompatibleTypeError(ctx, typeInArray.toString(), intType,
                typeInArray);
      }
    }
    Type array = visit(ctx.ident());
    if (array instanceof ArrayType) {
      ArrayType arrayBase = (ArrayType) array;
      return arrayBase.getElementType();
    } else if (array instanceof BaseType) {
      return charType;
    }
    return null;
  }

  @Override
  public Type visitArray_liter(Array_literContext ctx) {
//    System.out.println("visiting array liter");
    if (ctx.expr() != null && ctx.expr().size() > 0) {
      Type first_elem_type = visit(ctx.expr(0));
      for (ExprContext exprContext : ctx.expr()) {
        Type typeInArray = visit(exprContext);
        if (!typeChecker(first_elem_type, typeInArray)) {
          visitorErrorHandler
              .incompatibleTypeError(ctx, typeInArray.toString(), first_elem_type,
                  typeInArray);
        }
      }
      return new ArrayType(first_elem_type);
    } else {
      return new AllType();
    }
  }


  @Override
  public Type visitNew_pair(New_pairContext ctx) {
//    System.out.println("visiting new pair");
    Type fst_type = visit(ctx.expr(0));
    Type snd_type = visit(ctx.expr(1));
    return new PairType(fst_type, snd_type);
  }

  @Override
  public Type visitIdent(IdentContext ctx) {
//    System.out.println("visiting ident");
    String ident = ctx.IDENT().getText();
    if (!symbolNode.contain(ident)) {
      visitorErrorHandler.variableNotDefinedInScopeError(ctx, ident);
    }
    return symbolNode.lookupAll(ident);
  }

  @Override
  public Type visitPair_liter(Pair_literContext ctx) {
//    System.out.println("visiting pair liter");
    return new PairType();
  }

  @Override
  public Type visitParam(ParamContext ctx) {
//    System.out.println("visiting param");
    return visitType(ctx.type());
  }

  @Override
  public Type visitBinary_oper_mul(Binary_oper_mulContext ctx) {
//    System.out.println("visiting binary op mul");
    ExprContext parent = (ExprContext) ctx.getParent();
    Type t1 = visit(parent.expr(0));
    Type t2 = visit(parent.expr(1));
    String firstString = parent.expr(0).getText();
    String secondString = parent.expr(1).getText();
    int op = ((TerminalNode) ctx.getChild(0)).getSymbol().getType();
    if (op == WaccParser.MUL || op == WaccParser.DIV || op == WaccParser.MOD) {
      if (!typeChecker(intType, t1)) {
        visitorErrorHandler
            .incompatibleTypeError(ctx, firstString, intType, t1);
      }
      if (!typeChecker(intType, t2)) {
        visitorErrorHandler
            .incompatibleTypeError(ctx, secondString, intType, t2);
      }
      return intType;
    }
    return null;
  }

  @Override
  public Type visitBinary_oper_and_or(Binary_oper_and_orContext ctx) {
//    System.out.println("visiting binary and or");
    ExprContext parent = (ExprContext) ctx.getParent();
    Type t1 = visit(parent.expr(0));
    Type t2 = visit(parent.expr(1));
    String firstString = parent.expr(0).getText();
    String secondString = parent.expr(1).getText();
    int op = ((TerminalNode) ctx.getChild(0)).getSymbol().getType();
    if (op == WaccParser.AND || op == WaccParser.OR) {
      if (!typeChecker(boolType, t1)) {
        visitorErrorHandler
            .incompatibleTypeError(ctx, firstString, boolType, t1);
      }
      if (!typeChecker(boolType, t2)) {
        visitorErrorHandler
            .incompatibleTypeError(ctx, secondString, boolType, t2);
      }
      return boolType;
    }
    return null;
  }

  @Override
  public Type visitBinary_oper_eql(Binary_oper_eqlContext ctx) {
//    System.out.println("visiting binary equal");
    ExprContext parent = (ExprContext) ctx.getParent();
    Type t1 = visit(parent.expr(0));
    Type t2 = visit(parent.expr(1));
    String firstString = parent.expr(0).getText();
    String secondString = parent.expr(1).getText();
    int op = ((TerminalNode) ctx.getChild(0)).getSymbol().getType();
    if (op == WaccParser.GT || op == WaccParser.LT || op == WaccParser.GET
        || op == WaccParser.LET) {
      if (!typeChecker(t1, t2)) {
        visitorErrorHandler.incompatibleTypeError(ctx, firstString, t1, t2);
      }
      if (t1 instanceof ArrayType || t1 instanceof PairType) {
        visitorErrorHandler.incompatibleTypeError(ctx, t1);
      }
      if (t2 instanceof ArrayType || t2 instanceof PairType) {
        visitorErrorHandler.incompatibleTypeError(ctx, t2);
      }
      return boolType;
    }

    if (op == WaccParser.EQL || op == WaccParser.NEQL) {
      return boolType;
    }
    return null;
  }

  @Override
  public Type visitBinary_oper_plus(Binary_oper_plusContext ctx) {
//    System.out.println("visiting binary plus");
    ExprContext parent = (ExprContext) ctx.getParent();
    Type t1 = visit(parent.expr(0));
    Type t2 = visit(parent.expr(1));
    String firstString = parent.expr(0).getText();
    String secondString = parent.expr(1).getText();
    int op = ((TerminalNode) ctx.getChild(0)).getSymbol().getType();
    if (op == WaccParser.PLUS || op == WaccParser.MINUS) {
      if (!typeChecker(intType, t1)) {
        visitorErrorHandler
            .incompatibleTypeError(ctx, firstString, intType, t1);
      }
      if (!typeChecker(intType, t2)) {
        visitorErrorHandler
            .incompatibleTypeError(ctx, secondString, intType, t2);
      }
      return intType;
    }
    return null;
  }

  @Override
  public Type visitUnary_oper(Unary_operContext ctx) {
//    System.out.println("visiting unray op");
    ExprContext parent = (ExprContext) ctx.getParent();
    Type type = visit(parent.expr(0));
    int op = ((TerminalNode) ctx.getChild(0)).getSymbol().getType();
    if (op == WaccParser.NOT) {
      if (!typeChecker(boolType, type)) {
        visitorErrorHandler
            .incompatibleTypeError(ctx, type.toString(), boolType, type);
      }
      return boolType;
    }

    if (op == WaccParser.MINUS) {
      if (!typeChecker(intType, type)) {
        visitorErrorHandler
            .incompatibleTypeError(ctx, type.toString(), intType, type);
      }
      return intType;
    }

    if (op == WaccParser.LEN) {
      if (type.getID() != ID.Array) {
        visitorErrorHandler
            .incompatibleTypeError(ctx, type.toString(), new ArrayType(), type);
      }
      return intType;
    }

    if (op == WaccParser.ORD) {
      if (!typeChecker(charType, type)) {
        visitorErrorHandler
            .incompatibleTypeError(ctx, type.toString(), charType, type);
      }
      return intType;
    }

    if (op == WaccParser.CHR) {
      if (!typeChecker(intType, type)) {
        visitorErrorHandler
            .incompatibleTypeError(ctx, type.toString(), intType, type);
      }
      return charType;
    }
    return null;
  }

  @Override
  public Type visitType(TypeContext ctx) {
//    System.out.println("visiting type");
    return visit(ctx.getChild(0));
  }

  @Override
  public Type visitPair_elem(Pair_elemContext ctx) {
    PairType pair = (PairType) visit(ctx.expr());
    if (ctx.FST() != null) {
      return pair.getFst();
    } else if (ctx.SND() != null) {
      return pair.getSnd();
    }
    return null;
  }
}
