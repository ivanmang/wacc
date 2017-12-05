import Utils.ArrayType;
import Utils.BaseType;
import Utils.PairType;
import Utils.SymbolNode;
import Utils.SymbolTable;
import Utils.Type;
import antlr.WaccParser;
import antlr.WaccParser.Array_elemContext;
import antlr.WaccParser.Binary_oper_and_orContext;
import antlr.WaccParser.Binary_oper_eqlContext;
import antlr.WaccParser.Binary_oper_mulContext;
import antlr.WaccParser.Binary_oper_plusContext;
import antlr.WaccParser.ExprContext;
import antlr.WaccParser.IdentContext;
import antlr.WaccParser.Pair_elemContext;
import antlr.WaccParser.Unary_operContext;
import antlr.WaccParserBaseVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;

public class GetTypeFromExpr extends WaccParserBaseVisitor<Type> {

  private Type intType = new BaseType(WaccParser.INT);
  private Type charType = new BaseType(WaccParser.CHAR);
  private Type boolType = new BaseType(WaccParser.BOOL);
  private Type stringType = new BaseType(WaccParser.STRING);
  private SymbolNode symbolNode;

  public void setSymbolNode(SymbolNode symbolNode) {
    this.symbolNode = symbolNode;
  }

  public Type visitExpr(ExprContext ctx, SymbolNode symbolNode) {
    setSymbolNode(symbolNode);
    if (ctx.int_liter() != null) {
      return intType;
    } else if (ctx.bool_liter() != null) {
      return boolType;
    } else if (ctx.array_elem() != null) {
      return visitArray_elem(ctx.array_elem(), symbolNode);
    } else if (ctx.binary_oper_and_or() != null) {
      return visit(ctx.binary_oper_and_or());
    } else if (ctx.binary_oper_eql() != null) {
      return visit(ctx.binary_oper_eql());
    } else if (ctx.binary_oper_mul() != null) {
      return visit(ctx.binary_oper_mul());
    } else if (ctx.binary_oper_plus() != null) {
      return visit(ctx.binary_oper_plus());
    } else if (ctx.pair_liter() != null) {
      return new PairType();
    } else if (ctx.unary_oper() != null) {
      return visit(ctx.unary_oper());
    } else if (ctx.ident() != null) {
      return visit(ctx.ident());
    } else if (ctx.CHAR_LIT() != null) {
      return charType;
    } else if (ctx.CHARACTER_LIT() != null) {
      return stringType;
    } else if (ctx.OPEN_PARENTHESES() != null) {
      return visit(ctx.expr(0));
    }
    return null;
  }


  public Type visitArray_elem(Array_elemContext ctx, SymbolNode symbolNode) {
//    System.out.println("visiting array elem");
    System.out.println("checking");
    String ident = ctx.ident().getText();
    Type array = symbolNode.lookupAll(ident);
//    System.out.println(array);
    System.out.println("checking");
    if (array instanceof ArrayType) {
      ArrayType arrayBase = (ArrayType) array;
      return arrayBase.getElementType();
    } else if (array instanceof BaseType) {
      return charType;
    }
    System.out.println("failed");
    return null;
  }


  @Override
  public Type visitBinary_oper_and_or(Binary_oper_and_orContext ctx) {
//    System.out.println("visiting binary and or");
    int op = ((TerminalNode) ctx.getChild(0)).getSymbol().getType();
    if (op == WaccParser.AND || op == WaccParser.OR) {
      return boolType;
    }
    return null;
  }

  @Override
  public Type visitBinary_oper_eql(Binary_oper_eqlContext ctx) {
//    System.out.println("visiting binary equal");
    int op = ((TerminalNode) ctx.getChild(0)).getSymbol().getType();
    if (op == WaccParser.GT || op == WaccParser.LT || op == WaccParser.GET
        || op == WaccParser.LET) {
      return boolType;
    }

    if (op == WaccParser.EQL || op == WaccParser.NEQL) {
      return boolType;
    }
    return null;
  }

  @Override
  public Type visitBinary_oper_mul(Binary_oper_mulContext ctx) {
//    System.out.println("visiting binary op mul");

    int op = ((TerminalNode) ctx.getChild(0)).getSymbol().getType();
    if (op == WaccParser.MUL || op == WaccParser.DIV || op == WaccParser.MOD) {
      return intType;
    }
    return null;
  }

  @Override
  public Type visitBinary_oper_plus(Binary_oper_plusContext ctx) {
//    System.out.println("visiting binary plus");

    int op = ((TerminalNode) ctx.getChild(0)).getSymbol().getType();
    if (op == WaccParser.PLUS || op == WaccParser.MINUS) {

      return intType;
    }
    return null;
  }

  @Override
  public Type visitUnary_oper(Unary_operContext ctx) {
//    System.out.println("visiting unray op");
    int op = ((TerminalNode) ctx.getChild(0)).getSymbol().getType();
    if (op == WaccParser.NOT) {
      return boolType;
    }

    if (op == WaccParser.MINUS) {
      return intType;
    }

    if (op == WaccParser.LEN) {
      return intType;
    }

    if (op == WaccParser.ORD) {
      return intType;
    }

    if (op == WaccParser.CHR) {
      return charType;
    }
    return null;
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

  @Override
  public Type visitIdent(IdentContext ctx) {
    String ident = ctx.getText();
    return symbolNode.lookupAll(ident);
  }
}
