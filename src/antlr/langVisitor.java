// Generated from /home/shady/Git/pp-final-project/src/antlr/lang.g4 by ANTLR 4.7.1
package antlr;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link langParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface langVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link langParser#prog}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProg(langParser.ProgContext ctx);
	/**
	 * Visit a parse tree produced by the {@code declstat}
	 * labeled alternative in {@link langParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclstat(langParser.DeclstatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code assgnstat}
	 * labeled alternative in {@link langParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssgnstat(langParser.AssgnstatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ifsat}
	 * labeled alternative in {@link langParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfsat(langParser.IfsatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code floopstat}
	 * labeled alternative in {@link langParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFloopstat(langParser.FloopstatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code wloopstat}
	 * labeled alternative in {@link langParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWloopstat(langParser.WloopstatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code incstat}
	 * labeled alternative in {@link langParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIncstat(langParser.IncstatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code funcstat}
	 * labeled alternative in {@link langParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncstat(langParser.FuncstatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code forkfuncstat}
	 * labeled alternative in {@link langParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForkfuncstat(langParser.ForkfuncstatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code joinstat}
	 * labeled alternative in {@link langParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJoinstat(langParser.JoinstatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code vardecl}
	 * labeled alternative in {@link langParser#decl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVardecl(langParser.VardeclContext ctx);
	/**
	 * Visit a parse tree produced by the {@code arrdecl}
	 * labeled alternative in {@link langParser#decl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrdecl(langParser.ArrdeclContext ctx);
	/**
	 * Visit a parse tree produced by the {@code uninitdecl}
	 * labeled alternative in {@link langParser#decl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUninitdecl(langParser.UninitdeclContext ctx);
	/**
	 * Visit a parse tree produced by the {@code regassign}
	 * labeled alternative in {@link langParser#assgn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRegassign(langParser.RegassignContext ctx);
	/**
	 * Visit a parse tree produced by the {@code binassign}
	 * labeled alternative in {@link langParser#assgn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinassign(langParser.BinassignContext ctx);
	/**
	 * Visit a parse tree produced by the {@code notexpr}
	 * labeled alternative in {@link langParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNotexpr(langParser.NotexprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code bracketexpr}
	 * labeled alternative in {@link langParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBracketexpr(langParser.BracketexprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code doubleexprbin}
	 * labeled alternative in {@link langParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDoubleexprbin(langParser.DoubleexprbinContext ctx);
	/**
	 * Visit a parse tree produced by the {@code orderingexpr}
	 * labeled alternative in {@link langParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrderingexpr(langParser.OrderingexprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code exprterm}
	 * labeled alternative in {@link langParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprterm(langParser.ExprtermContext ctx);
	/**
	 * Visit a parse tree produced by the {@code doublelogicbin}
	 * labeled alternative in {@link langParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDoublelogicbin(langParser.DoublelogicbinContext ctx);
	/**
	 * Visit a parse tree produced by {@link langParser#inc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInc(langParser.IncContext ctx);
	/**
	 * Visit a parse tree produced by {@link langParser#returnVal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnVal(langParser.ReturnValContext ctx);
	/**
	 * Visit a parse tree produced by the {@code bracketterm}
	 * labeled alternative in {@link langParser#term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBracketterm(langParser.BrackettermContext ctx);
	/**
	 * Visit a parse tree produced by the {@code termval}
	 * labeled alternative in {@link langParser#term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTermval(langParser.TermvalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code termident}
	 * labeled alternative in {@link langParser#term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTermident(langParser.TermidentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code termfunccall}
	 * labeled alternative in {@link langParser#term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTermfunccall(langParser.TermfunccallContext ctx);
	/**
	 * Visit a parse tree produced by {@link langParser#floop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFloop(langParser.FloopContext ctx);
	/**
	 * Visit a parse tree produced by {@link langParser#wloop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWloop(langParser.WloopContext ctx);
	/**
	 * Visit a parse tree produced by {@link langParser#ifstat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfstat(langParser.IfstatContext ctx);
	/**
	 * Visit a parse tree produced by {@link langParser#ifbody}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfbody(langParser.IfbodyContext ctx);
	/**
	 * Visit a parse tree produced by {@link langParser#elsebody}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElsebody(langParser.ElsebodyContext ctx);
	/**
	 * Visit a parse tree produced by the {@code singlecond}
	 * labeled alternative in {@link langParser#cond}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSinglecond(langParser.SinglecondContext ctx);
	/**
	 * Visit a parse tree produced by the {@code noreturn}
	 * labeled alternative in {@link langParser#func}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNoreturn(langParser.NoreturnContext ctx);
	/**
	 * Visit a parse tree produced by the {@code withreturn}
	 * labeled alternative in {@link langParser#func}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWithreturn(langParser.WithreturnContext ctx);
	/**
	 * Visit a parse tree produced by {@link langParser#funccall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunccall(langParser.FunccallContext ctx);
	/**
	 * Visit a parse tree produced by {@link langParser#funcarg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncarg(langParser.FuncargContext ctx);
	/**
	 * Visit a parse tree produced by the {@code valint}
	 * labeled alternative in {@link langParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValint(langParser.ValintContext ctx);
	/**
	 * Visit a parse tree produced by the {@code valbool}
	 * labeled alternative in {@link langParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValbool(langParser.ValboolContext ctx);
	/**
	 * Visit a parse tree produced by {@link langParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(langParser.TypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link langParser#binaryop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinaryop(langParser.BinaryopContext ctx);
	/**
	 * Visit a parse tree produced by {@link langParser#unaryop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryop(langParser.UnaryopContext ctx);
	/**
	 * Visit a parse tree produced by {@link langParser#binassgn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinassgn(langParser.BinassgnContext ctx);
	/**
	 * Visit a parse tree produced by {@link langParser#logicalop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalop(langParser.LogicalopContext ctx);
	/**
	 * Visit a parse tree produced by {@link langParser#ordering}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrdering(langParser.OrderingContext ctx);
}