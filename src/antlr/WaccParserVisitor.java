// Generated from ./WaccParser.g4 by ANTLR 4.7
package antlr;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link WaccParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface WaccParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link WaccParser#prog}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProg(WaccParser.ProgContext ctx);
	/**
	 * Visit a parse tree produced by {@link WaccParser#func}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunc(WaccParser.FuncContext ctx);
	/**
	 * Visit a parse tree produced by {@link WaccParser#param_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam_list(WaccParser.Param_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link WaccParser#param}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam(WaccParser.ParamContext ctx);
	/**
	 * Visit a parse tree produced by the {@code readStat}
	 * labeled alternative in {@link WaccParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReadStat(WaccParser.ReadStatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ifStat}
	 * labeled alternative in {@link WaccParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStat(WaccParser.IfStatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code printStat}
	 * labeled alternative in {@link WaccParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrintStat(WaccParser.PrintStatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code printlnStat}
	 * labeled alternative in {@link WaccParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrintlnStat(WaccParser.PrintlnStatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code exitStat}
	 * labeled alternative in {@link WaccParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExitStat(WaccParser.ExitStatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code freeStat}
	 * labeled alternative in {@link WaccParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFreeStat(WaccParser.FreeStatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code declareAndAssignStat}
	 * labeled alternative in {@link WaccParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclareAndAssignStat(WaccParser.DeclareAndAssignStatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code beginStat}
	 * labeled alternative in {@link WaccParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBeginStat(WaccParser.BeginStatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code skipStat}
	 * labeled alternative in {@link WaccParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSkipStat(WaccParser.SkipStatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code returnStat}
	 * labeled alternative in {@link WaccParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnStat(WaccParser.ReturnStatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code assignStat}
	 * labeled alternative in {@link WaccParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignStat(WaccParser.AssignStatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code statToStat}
	 * labeled alternative in {@link WaccParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatToStat(WaccParser.StatToStatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code whileStat}
	 * labeled alternative in {@link WaccParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhileStat(WaccParser.WhileStatContext ctx);
	/**
	 * Visit a parse tree produced by {@link WaccParser#assign_lhs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssign_lhs(WaccParser.Assign_lhsContext ctx);
	/**
	 * Visit a parse tree produced by {@link WaccParser#assign_rhs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssign_rhs(WaccParser.Assign_rhsContext ctx);
	/**
	 * Visit a parse tree produced by {@link WaccParser#new_pair}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNew_pair(WaccParser.New_pairContext ctx);
	/**
	 * Visit a parse tree produced by {@link WaccParser#function_call}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction_call(WaccParser.Function_callContext ctx);
	/**
	 * Visit a parse tree produced by {@link WaccParser#arg_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArg_list(WaccParser.Arg_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link WaccParser#pair_elem}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPair_elem(WaccParser.Pair_elemContext ctx);
	/**
	 * Visit a parse tree produced by {@link WaccParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(WaccParser.TypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link WaccParser#base_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBase_type(WaccParser.Base_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link WaccParser#array_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArray_type(WaccParser.Array_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link WaccParser#pair_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPair_type(WaccParser.Pair_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link WaccParser#pair_elem_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPair_elem_type(WaccParser.Pair_elem_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link WaccParser#int_liter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInt_liter(WaccParser.Int_literContext ctx);
	/**
	 * Visit a parse tree produced by {@link WaccParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(WaccParser.ExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link WaccParser#unary_oper}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnary_oper(WaccParser.Unary_operContext ctx);
	/**
	 * Visit a parse tree produced by {@link WaccParser#binary_oper_mul}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinary_oper_mul(WaccParser.Binary_oper_mulContext ctx);
	/**
	 * Visit a parse tree produced by {@link WaccParser#binary_oper_plus}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinary_oper_plus(WaccParser.Binary_oper_plusContext ctx);
	/**
	 * Visit a parse tree produced by {@link WaccParser#binary_oper_eql}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinary_oper_eql(WaccParser.Binary_oper_eqlContext ctx);
	/**
	 * Visit a parse tree produced by {@link WaccParser#binary_oper_and_or}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinary_oper_and_or(WaccParser.Binary_oper_and_orContext ctx);
	/**
	 * Visit a parse tree produced by {@link WaccParser#ident}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdent(WaccParser.IdentContext ctx);
	/**
	 * Visit a parse tree produced by {@link WaccParser#array_elem}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArray_elem(WaccParser.Array_elemContext ctx);
	/**
	 * Visit a parse tree produced by {@link WaccParser#int_sign}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInt_sign(WaccParser.Int_signContext ctx);
	/**
	 * Visit a parse tree produced by {@link WaccParser#bool_liter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBool_liter(WaccParser.Bool_literContext ctx);
	/**
	 * Visit a parse tree produced by {@link WaccParser#array_liter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArray_liter(WaccParser.Array_literContext ctx);
	/**
	 * Visit a parse tree produced by {@link WaccParser#pair_liter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPair_liter(WaccParser.Pair_literContext ctx);
}