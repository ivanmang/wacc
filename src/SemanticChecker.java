import antlr.WaccParser;
import antlr.WaccParser.Arg_listContext;
import antlr.WaccParser.Array_elemContext;
import antlr.WaccParser.Array_literContext;
import antlr.WaccParser.Array_typeContext;
import antlr.WaccParser.AssignStatContext;
import antlr.WaccParser.Assign_lhsContext;
import antlr.WaccParser.Assign_rhsContext;
import antlr.WaccParser.Base_typeContext;
import antlr.WaccParser.BeginStatContext;
import antlr.WaccParser.Binary_operContext;
import antlr.WaccParser.DeclareAndAssignStatContext;
import antlr.WaccParser.ExitStatContext;
import antlr.WaccParser.ExprContext;
import antlr.WaccParser.FreeStatContext;
import antlr.WaccParser.FuncContext;
import antlr.WaccParser.Function_callContext;
import antlr.WaccParser.IdentContext;
import antlr.WaccParser.IfStatContext;
import antlr.WaccParser.New_pairContext;
import antlr.WaccParser.Pair_elem_typeContext;
import antlr.WaccParser.Pair_literContext;
import antlr.WaccParser.Pair_typeContext;
import antlr.WaccParser.ParamContext;
import antlr.WaccParser.PrintStatContext;
import antlr.WaccParser.PrintlnStatContext;
import antlr.WaccParser.ReadStatContext;
import antlr.WaccParser.TypeContext;
import antlr.WaccParser.Unary_operContext;
import antlr.WaccParser.WhileStatContext;
import antlr.WaccParserBaseVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;

public class SemanticChecker extends WaccParserBaseVisitor<Type> {

  public VisitorErrorHandler visitorErrorHandler = new VisitorErrorHandler();
  private SymbolTable symbolTable = new SymbolTable(null, null);
  private Type intType = new BaseType(WaccParser.INT);
  private Type charType = new BaseType(WaccParser.CHAR);
  private Type boolType = new BaseType(WaccParser.BOOL);
  private Type stringType = new BaseType(WaccParser.STRING);

  public SymbolTable getSymbolTable() {
    return symbolTable;
  }

  public boolean typeChecker(Type type1, Type type2) {
    System.out.println("type checking");
    if (type1 == null) {
      System.out.println("Type 1 null");
    }
    if (type2 == null) {
      System.out.println("Type 2 null");
    }
    return type1.isValidType() && type2.isValidType() && type1.equals(type2);
  }

  @Override
  public Type visitDeclareAndAssignStat(DeclareAndAssignStatContext ctx) {
    System.out.println("CHEKING EXPECTED");
    Type expected = visitType(ctx.type());
    System.out.println("CHECKING ACTUAL");
    Type actual = visit(ctx.assign_rhs());
    System.out.println(actual);
    String identName = ctx.ident().IDENT().getText();
    if (!typeChecker(expected, actual)) {
      visitorErrorHandler
          .incompatibleTypeError(ctx, ctx.ident().IDENT().getText(), expected,
              actual);
    } else if (symbolTable.contain(identName)) {
      visitorErrorHandler.variableRedefineError(ctx, identName);
    } else {
      symbolTable.insert(ctx.ident().getText(), expected);
    }
    return null;
  }

  @Override
  public Type visitAssignStat(AssignStatContext ctx) {
    System.out.println("Visiting Assign Stat");
    Type expected = visit(ctx.assign_lhs());
    Type actual = visit(ctx.assign_rhs());
    if (!typeChecker(expected, actual)) {
      visitorErrorHandler
          .incompatibleTypeError(ctx, ctx.assign_rhs().getTokens(0).toString(),
              expected, actual);
    }
    return null;
  }

  @Override
  public Type visitReadStat(ReadStatContext ctx) {
    System.out.println("Visiting Read Stat");
    Type actual = visit(ctx.assign_lhs());
    if (!typeChecker(charType, actual) && !typeChecker(intType, actual)) {
      visitorErrorHandler.incompatibleTypeError(ctx, actual);
    }
    return null;
  }

  @Override
  public Type visitFreeStat(FreeStatContext ctx) {
    System.out.println("Visiting Free stat");
    Type actual = visit(ctx.expr());
    if (actual.getID() != ID.Pair) {
      visitorErrorHandler.incompatibleTypeError(ctx, actual);
    }
    return null;
  }

  @Override
  public Type visitExitStat(ExitStatContext ctx) {
    System.out.println("Visiting exit stat");
    Type actual = visit(ctx.expr());
    if (!typeChecker(intType, actual)) {
      visitorErrorHandler.incompatibleTypeError(ctx, actual);
    }
    return null;
  }

  @Override
  public Type visitPrintStat(PrintStatContext ctx) {
    System.out.println("Visiting print stat");
    Type actual = visit(ctx.expr());
    if (!actual.isValidType()) {
      visitorErrorHandler.incompatibleTypeError(ctx, actual);
    }
    return null;
  }

  @Override
  public Type visitPrintlnStat(PrintlnStatContext ctx) {
    System.out.println("visiting println stat");
    Type actual = visit(ctx.expr());
    if (!actual.isValidType()) {
      visitorErrorHandler.incompatibleTypeError(ctx, actual);
    }
    return null;
  }


  @Override
  public Type visitIfStat(IfStatContext ctx) {
    System.out.println("Visiting if stat");
    Type condition = visit(ctx.expr());
    if (!typeChecker(boolType, condition)) {
      visitorErrorHandler.incompatibleTypeError(ctx, condition);
    }

    if (ctx.stat(1) != null) { //have if, then, else
      symbolTable = symbolTable.enterScope(symbolTable);
      Type fstat = visit(ctx.stat(0));
      symbolTable = symbolTable.exitScope(symbolTable);

      symbolTable = symbolTable.enterScope(symbolTable);
      Type sstat = visit(ctx.stat(1));
      symbolTable = symbolTable.exitScope(symbolTable);
      return typeChecker(fstat, sstat) ? fstat : null;

    } else { //only have if then , no else
      symbolTable = symbolTable.enterScope(symbolTable);
      Type stat = visit(ctx.stat(0));
      symbolTable = symbolTable.exitScope(symbolTable);
      return stat;
    }


  }

  @Override
  public Type visitWhileStat(WhileStatContext ctx) {
    System.out.println("visiting while stat");
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
    System.out.println("visiting begin stat");
    symbolTable = symbolTable.enterScope(symbolTable);
    Type stat = visit(ctx.stat());
    symbolTable = symbolTable.exitScope(symbolTable);
    return stat;
  }

  @Override
  public Type visitAssign_lhs(Assign_lhsContext ctx) {
    System.out.println("visiting assign lhs");
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
    System.out.println("visiting arg list");
    for (int i = 0; i < ctx.expr().size(); i++) {
      Type argType = visit(ctx.expr(i));
      //check type match with function para
      //TODO
    }
    return null;
  }

  @Override
  public Type visitBase_type(Base_typeContext ctx) {
    System.out.println("visiting base type");
    if (ctx.BOOL() != null) {
      return new BaseType(WaccParser.BOOL);
    } else if (ctx.CHAR() != null) {
      return new BaseType(WaccParser.CHAR);
    } else if (ctx.INT() != null) {
      return new BaseType(WaccParser.INT);
    } else if (ctx.STRING() != null) {
      return new BaseType(WaccParser.STRING);
    }
    System.out.println("VISIT BASE TYPE ERROR");
    return null;
  }

  @Override
  public Type visitArray_type(Array_typeContext ctx) {
    System.out.println("visiting array type");
    if (ctx.array_type().array_type() != null) {
      return new ArrayType(visit(ctx.array_type()));
    } else if (ctx.base_type() != null) {
      return new ArrayType(visit(ctx.base_type()));
    } else if (ctx.pair_type() != null) {
      return new ArrayType(visit(ctx.pair_type()));
    }
    System.out.println("VISIT ARRAY TYPE ERROR");
    return null;
  }

  @Override
  public Type visitPair_type(Pair_typeContext ctx) {
    System.out.println("visiting pair type");
    Type fst = visit(ctx.pair_elem_type(0));
    Type snd = visit(ctx.pair_elem_type(1));
    return new PairType(fst, snd);
  }

  @Override
  public Type visitPair_elem_type(Pair_elem_typeContext ctx) {
    System.out.println("visiting pair elem type");
    if (ctx.PAIR() != null) {
      return new PairType();
    } else if (ctx.array_type() != null) {
      return visit(ctx.array_type());
    } else if (ctx.base_type() != null) {
      return visit(ctx.base_type());
    }
    System.out.println("VISIT PAIR ELEM TYPE ERROR");
    return null;
  }

  @Override
  public Type visitExpr(ExprContext ctx) {
    System.out.println("visiting expr");
    if (ctx.int_liter() != null) {
      return intType;
    } else if (ctx.bool_liter() != null) {
      return boolType;
    } else if (ctx.array_elem() != null) {
      return visit(ctx.array_elem());
    } else if (ctx.binary_oper() != null) {
      return visit(ctx.binary_oper());
    } else if (ctx.pair_liter() != null) {
      return new PairType();
    } else if (ctx.unary_oper() != null) {
      return visit(ctx.unary_oper());
    } else if (ctx.ident() != null) {
      return visit(ctx.ident());
    } else if (ctx.CHAR_LIT() != null) {
      return charType;
    } else if (ctx.CHARACTER_LIT() != null) {
      return charType;
    } else if (ctx.binary_oper() != null) {
      return visit(ctx.binary_oper());
    } else if (ctx.OPEN_PARENTHESES() != null) {
      return visit(ctx.expr(0));
    }
    System.out.println("FAILED");
    return null;
  }

  @Override
  public Type visitAssign_rhs(Assign_rhsContext ctx) {
    System.out.println("visiting assign rhs");
    return visitChildren(ctx);
  }

  @Override
  public Type visitFunction_call(Function_callContext ctx) {
    System.out.println("visiting function call");
    return symbolTable.lookupAll(ctx.ident().toString());
  }

  @Override
  public Type visitFunc(FuncContext ctx) {
    System.out.println("visiting func");
    Type expected = visit(ctx.type());
    symbolTable = symbolTable.enterScope(symbolTable);
    if (symbolTable.lookupAll(ctx.ident().toString()) != null) {
      //TODO:IF
    }
    symbolTable.insert(ctx.ident().toString(), expected);
    return expected;
  }

  @Override
  public Type visitArray_elem(Array_elemContext ctx) {
    System.out.println("visiting array elem");
    for (ExprContext exprContext : ctx.expr()) {
      Type typeInArray = visit(exprContext);
      if (!typeChecker(intType, typeInArray)) {
        visitorErrorHandler
            .incompatibleTypeError(ctx, typeInArray.toString(), intType,
                typeInArray);
      }
    }
    return visit(ctx.ident());
  }

  @Override
  public Type visitArray_liter(Array_literContext ctx) {
    System.out.println("visiting array liter");
    Type first_elem_type = visit(ctx.expr(0));
    for (ExprContext exprContext : ctx.expr()) {
      Type typeInArray = visit(exprContext);
      if (!typeChecker(first_elem_type, typeInArray)) {
        visitorErrorHandler
            .incompatibleTypeError(ctx, typeInArray.toString(), first_elem_type,
                typeInArray);
      }
    }
    return first_elem_type;
  }

  @Override
  public Type visitNew_pair(New_pairContext ctx) {
    System.out.println("visiting new pair");
    Type fst_type = visit(ctx.expr(0));
    Type snd_type = visit(ctx.expr(1));
    return new PairType(fst_type, snd_type);
  }

  @Override
  public Type visitIdent(IdentContext ctx) {
    System.out.println("visiting ident");
    String ident = ctx.IDENT().getText();
    if (!symbolTable.contain(ident)) {
      visitorErrorHandler.variableNotDefinedInScopeError(ctx, ident);
    }
    return symbolTable.lookupAll(ident);
  }

  @Override
  public Type visitPair_liter(Pair_literContext ctx) {
    System.out.println("visiting pair liter");
    return new PairType();
  }

  /*
    @Override
    public Type visitProg(ProgContext ctx) {
      for(FuncContext funcContext : ctx.func()){
        if(symbolTable.contain(funcContext.getText())){
          //function redeclare
        }
        symbolTable.insert(funcContext.getText(),visit(funcContext.type()));
        visit(funcContext.param_list());
      }
      return visitChildren(ctx);
    }
   */
  @Override
  public Type visitParam(ParamContext ctx) {
    System.out.println("visiting param");
    return visitType(ctx.type());
  }

  @Override
  public Type visitBinary_oper(Binary_operContext ctx) {
    System.out.println("visiting binary op");
    ExprContext parent = (ExprContext) ctx.getParent();
    Type t1 = visit(parent.expr(0));
    Type t2 = visit(parent.expr(1));
    int op = ((TerminalNode) ctx.getChild(0)).getSymbol().getType();
    if (op == WaccParser.PLUS || op == WaccParser.MINUS || op == WaccParser.MUL
        || op == WaccParser.DIV) {
      if (!typeChecker(intType, t1)) {
        visitorErrorHandler
            .incompatibleTypeError(ctx, t1.toString(), intType, t1);
      }
      if (!typeChecker(intType, t2)) {
        visitorErrorHandler
            .incompatibleTypeError(ctx, t2.toString(), intType, t2);
      }
      return intType;
    }

    if (op == WaccParser.GT || op == WaccParser.LT || op == WaccParser.GET
        || op == WaccParser.LET) {
      if (!typeChecker(t1, t2)) {
        visitorErrorHandler.incompatibleTypeError(ctx, t1.toString(), t1, t2);
      }
      return boolType;
    }

    if (op == WaccParser.EQL || op == WaccParser.NEQL) {
      return boolType;
    }

    if (op == WaccParser.AND || op == WaccParser.OR) {
      if (!typeChecker(boolType, t1)) {
        visitorErrorHandler
            .incompatibleTypeError(ctx, t1.toString(), boolType, t1);
      }
      if (!typeChecker(boolType, t2)) {
        visitorErrorHandler
            .incompatibleTypeError(ctx, t2.toString(), boolType, t2);
      }
      return boolType;
    }

    return null;
  }

  @Override
  public Type visitUnary_oper(Unary_operContext ctx) {
    System.out.println("visiting unray op");
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
    System.out.println("visiting type");
    return visit(ctx.getChild(0));
  }
}