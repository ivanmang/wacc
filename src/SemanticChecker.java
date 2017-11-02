import antlr.WaccParser.Arg_listContext;
import antlr.WaccParser.Array_elemContext;
import antlr.WaccParser.Array_literContext;
import antlr.WaccParser.Array_typeContext;
import antlr.WaccParser.Assign_lhsContext;
import antlr.WaccParser.Assign_rhsContext;
import antlr.WaccParser.Base_typeContext;
import antlr.WaccParser.Binary_operContext;
import antlr.WaccParser.Bool_literContext;
import antlr.WaccParser.ExprContext;
import antlr.WaccParser.FuncContext;
import antlr.WaccParser.IdentContext;
import antlr.WaccParser.Int_literContext;
import antlr.WaccParser.Int_signContext;
import antlr.WaccParser.Pair_elemContext;
import antlr.WaccParser.Pair_elem_typeContext;
import antlr.WaccParser.Pair_literContext;
import antlr.WaccParser.Pair_typeContext;
import antlr.WaccParser.ParamContext;
import antlr.WaccParser.Param_listContext;
import antlr.WaccParser.ProgContext;
import antlr.WaccParser.StatContext;
import antlr.WaccParser.Stat_helperContext;
import antlr.WaccParser.TypeContext;
import antlr.WaccParser.Unary_operContext;
import antlr.WaccParserBaseVisitor;

public class SemanticChecker extends WaccParserBaseVisitor<Type>{

  @Override
  public Type visitInt_liter(Int_literContext ctx) {
    return super.visitInt_liter(ctx);
  }

  @Override
  public Type visitArg_list(Arg_listContext ctx) {
    return super.visitArg_list(ctx);
  }

  @Override
  public Type visitArray_elem(Array_elemContext ctx) {
    return super.visitArray_elem(ctx);
  }

  @Override
  public Type visitArray_liter(Array_literContext ctx) {
    return super.visitArray_liter(ctx);
  }

  @Override
  public Type visitArray_type(Array_typeContext ctx) {
    return super.visitArray_type(ctx);
  }

  @Override
  public Type visitAssign_lhs(Assign_lhsContext ctx) {
    return super.visitAssign_lhs(ctx);
  }

  @Override
  public Type visitAssign_rhs(Assign_rhsContext ctx) {
    return super.visitAssign_rhs(ctx);
  }

  @Override
  public Type visitBase_type(Base_typeContext ctx) {
    return super.visitBase_type(ctx);
  }

  @Override
  public Type visitBinary_oper(Binary_operContext ctx) {
    return super.visitBinary_oper(ctx);
  }

  @Override
  public Type visitBool_liter(Bool_literContext ctx) {
    return super.visitBool_liter(ctx);
  }

  @Override
  public Type visitExpr(ExprContext ctx) {
    return super.visitExpr(ctx);
  }

  @Override
  public Type visitFunc(FuncContext ctx) {
    return super.visitFunc(ctx);
  }

  @Override
  public Type visitIdent(IdentContext ctx) {
    return super.visitIdent(ctx);
  }

  @Override
  public Type visitInt_sign(Int_signContext ctx) {
    return super.visitInt_sign(ctx);
  }

  @Override
  public Type visitPair_elem(Pair_elemContext ctx) {
    return super.visitPair_elem(ctx);
  }

  @Override
  public Type visitPair_elem_type(Pair_elem_typeContext ctx) {
    return super.visitPair_elem_type(ctx);
  }

  @Override
  public Type visitPair_liter(Pair_literContext ctx) {
    return super.visitPair_liter(ctx);
  }

  @Override
  public Type visitPair_type(Pair_typeContext ctx) {
    return super.visitPair_type(ctx);
  }

  @Override
  public Type visitParam(ParamContext ctx) {
    return super.visitParam(ctx);
  }

  @Override
  public Type visitParam_list(Param_listContext ctx) {
    return super.visitParam_list(ctx);
  }

  @Override
  public Type visitProg(ProgContext ctx) {
    return super.visitProg(ctx);
  }

  @Override
  public Type visitStat(StatContext ctx) {
    return super.visitStat(ctx);
  }

  @Override
  public Type visitStat_helper(Stat_helperContext ctx) {
    return super.visitStat_helper(ctx);
  }

  @Override
  public Type visitType(TypeContext ctx) {
    return super.visitType(ctx);
  }

  @Override
  public Type visitUnary_oper(Unary_operContext ctx) {
    return super.visitUnary_oper(ctx);
  }
  
}
