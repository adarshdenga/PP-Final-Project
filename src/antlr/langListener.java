// Generated from /home/shady/Git/pp-final-project/src/antlr/lang.g4 by ANTLR 4.7.1
package antlr;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link langParser}.
 */
public interface langListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link langParser#prog}.
	 * @param ctx the parse tree
	 */
	void enterProg(langParser.ProgContext ctx);
	/**
	 * Exit a parse tree produced by {@link langParser#prog}.
	 * @param ctx the parse tree
	 */
	void exitProg(langParser.ProgContext ctx);
	/**
	 * Enter a parse tree produced by the {@code declstat}
	 * labeled alternative in {@link langParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterDeclstat(langParser.DeclstatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code declstat}
	 * labeled alternative in {@link langParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitDeclstat(langParser.DeclstatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code assgnstat}
	 * labeled alternative in {@link langParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterAssgnstat(langParser.AssgnstatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code assgnstat}
	 * labeled alternative in {@link langParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitAssgnstat(langParser.AssgnstatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ifsat}
	 * labeled alternative in {@link langParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterIfsat(langParser.IfsatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ifsat}
	 * labeled alternative in {@link langParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitIfsat(langParser.IfsatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code floopstat}
	 * labeled alternative in {@link langParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterFloopstat(langParser.FloopstatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code floopstat}
	 * labeled alternative in {@link langParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitFloopstat(langParser.FloopstatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code wloopstat}
	 * labeled alternative in {@link langParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterWloopstat(langParser.WloopstatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code wloopstat}
	 * labeled alternative in {@link langParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitWloopstat(langParser.WloopstatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code incstat}
	 * labeled alternative in {@link langParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterIncstat(langParser.IncstatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code incstat}
	 * labeled alternative in {@link langParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitIncstat(langParser.IncstatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code funcstat}
	 * labeled alternative in {@link langParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterFuncstat(langParser.FuncstatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code funcstat}
	 * labeled alternative in {@link langParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitFuncstat(langParser.FuncstatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code forkfuncstat}
	 * labeled alternative in {@link langParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterForkfuncstat(langParser.ForkfuncstatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code forkfuncstat}
	 * labeled alternative in {@link langParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitForkfuncstat(langParser.ForkfuncstatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code joinstat}
	 * labeled alternative in {@link langParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterJoinstat(langParser.JoinstatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code joinstat}
	 * labeled alternative in {@link langParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitJoinstat(langParser.JoinstatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code vardecl}
	 * labeled alternative in {@link langParser#decl}.
	 * @param ctx the parse tree
	 */
	void enterVardecl(langParser.VardeclContext ctx);
	/**
	 * Exit a parse tree produced by the {@code vardecl}
	 * labeled alternative in {@link langParser#decl}.
	 * @param ctx the parse tree
	 */
	void exitVardecl(langParser.VardeclContext ctx);
	/**
	 * Enter a parse tree produced by the {@code arrdecl}
	 * labeled alternative in {@link langParser#decl}.
	 * @param ctx the parse tree
	 */
	void enterArrdecl(langParser.ArrdeclContext ctx);
	/**
	 * Exit a parse tree produced by the {@code arrdecl}
	 * labeled alternative in {@link langParser#decl}.
	 * @param ctx the parse tree
	 */
	void exitArrdecl(langParser.ArrdeclContext ctx);
	/**
	 * Enter a parse tree produced by the {@code uninitdecl}
	 * labeled alternative in {@link langParser#decl}.
	 * @param ctx the parse tree
	 */
	void enterUninitdecl(langParser.UninitdeclContext ctx);
	/**
	 * Exit a parse tree produced by the {@code uninitdecl}
	 * labeled alternative in {@link langParser#decl}.
	 * @param ctx the parse tree
	 */
	void exitUninitdecl(langParser.UninitdeclContext ctx);
	/**
	 * Enter a parse tree produced by the {@code regassign}
	 * labeled alternative in {@link langParser#assgn}.
	 * @param ctx the parse tree
	 */
	void enterRegassign(langParser.RegassignContext ctx);
	/**
	 * Exit a parse tree produced by the {@code regassign}
	 * labeled alternative in {@link langParser#assgn}.
	 * @param ctx the parse tree
	 */
	void exitRegassign(langParser.RegassignContext ctx);
	/**
	 * Enter a parse tree produced by the {@code binassign}
	 * labeled alternative in {@link langParser#assgn}.
	 * @param ctx the parse tree
	 */
	void enterBinassign(langParser.BinassignContext ctx);
	/**
	 * Exit a parse tree produced by the {@code binassign}
	 * labeled alternative in {@link langParser#assgn}.
	 * @param ctx the parse tree
	 */
	void exitBinassign(langParser.BinassignContext ctx);
	/**
	 * Enter a parse tree produced by the {@code notexpr}
	 * labeled alternative in {@link langParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterNotexpr(langParser.NotexprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code notexpr}
	 * labeled alternative in {@link langParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitNotexpr(langParser.NotexprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code bracketexpr}
	 * labeled alternative in {@link langParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterBracketexpr(langParser.BracketexprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code bracketexpr}
	 * labeled alternative in {@link langParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitBracketexpr(langParser.BracketexprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code doubleexprbin}
	 * labeled alternative in {@link langParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterDoubleexprbin(langParser.DoubleexprbinContext ctx);
	/**
	 * Exit a parse tree produced by the {@code doubleexprbin}
	 * labeled alternative in {@link langParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitDoubleexprbin(langParser.DoubleexprbinContext ctx);
	/**
	 * Enter a parse tree produced by the {@code orderingexpr}
	 * labeled alternative in {@link langParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterOrderingexpr(langParser.OrderingexprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code orderingexpr}
	 * labeled alternative in {@link langParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitOrderingexpr(langParser.OrderingexprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code exprterm}
	 * labeled alternative in {@link langParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExprterm(langParser.ExprtermContext ctx);
	/**
	 * Exit a parse tree produced by the {@code exprterm}
	 * labeled alternative in {@link langParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExprterm(langParser.ExprtermContext ctx);
	/**
	 * Enter a parse tree produced by the {@code doublelogicbin}
	 * labeled alternative in {@link langParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterDoublelogicbin(langParser.DoublelogicbinContext ctx);
	/**
	 * Exit a parse tree produced by the {@code doublelogicbin}
	 * labeled alternative in {@link langParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitDoublelogicbin(langParser.DoublelogicbinContext ctx);
	/**
	 * Enter a parse tree produced by {@link langParser#inc}.
	 * @param ctx the parse tree
	 */
	void enterInc(langParser.IncContext ctx);
	/**
	 * Exit a parse tree produced by {@link langParser#inc}.
	 * @param ctx the parse tree
	 */
	void exitInc(langParser.IncContext ctx);
	/**
	 * Enter a parse tree produced by {@link langParser#returnVal}.
	 * @param ctx the parse tree
	 */
	void enterReturnVal(langParser.ReturnValContext ctx);
	/**
	 * Exit a parse tree produced by {@link langParser#returnVal}.
	 * @param ctx the parse tree
	 */
	void exitReturnVal(langParser.ReturnValContext ctx);
	/**
	 * Enter a parse tree produced by the {@code bracketterm}
	 * labeled alternative in {@link langParser#term}.
	 * @param ctx the parse tree
	 */
	void enterBracketterm(langParser.BrackettermContext ctx);
	/**
	 * Exit a parse tree produced by the {@code bracketterm}
	 * labeled alternative in {@link langParser#term}.
	 * @param ctx the parse tree
	 */
	void exitBracketterm(langParser.BrackettermContext ctx);
	/**
	 * Enter a parse tree produced by the {@code termval}
	 * labeled alternative in {@link langParser#term}.
	 * @param ctx the parse tree
	 */
	void enterTermval(langParser.TermvalContext ctx);
	/**
	 * Exit a parse tree produced by the {@code termval}
	 * labeled alternative in {@link langParser#term}.
	 * @param ctx the parse tree
	 */
	void exitTermval(langParser.TermvalContext ctx);
	/**
	 * Enter a parse tree produced by the {@code termident}
	 * labeled alternative in {@link langParser#term}.
	 * @param ctx the parse tree
	 */
	void enterTermident(langParser.TermidentContext ctx);
	/**
	 * Exit a parse tree produced by the {@code termident}
	 * labeled alternative in {@link langParser#term}.
	 * @param ctx the parse tree
	 */
	void exitTermident(langParser.TermidentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code termfunccall}
	 * labeled alternative in {@link langParser#term}.
	 * @param ctx the parse tree
	 */
	void enterTermfunccall(langParser.TermfunccallContext ctx);
	/**
	 * Exit a parse tree produced by the {@code termfunccall}
	 * labeled alternative in {@link langParser#term}.
	 * @param ctx the parse tree
	 */
	void exitTermfunccall(langParser.TermfunccallContext ctx);
	/**
	 * Enter a parse tree produced by {@link langParser#floop}.
	 * @param ctx the parse tree
	 */
	void enterFloop(langParser.FloopContext ctx);
	/**
	 * Exit a parse tree produced by {@link langParser#floop}.
	 * @param ctx the parse tree
	 */
	void exitFloop(langParser.FloopContext ctx);
	/**
	 * Enter a parse tree produced by {@link langParser#wloop}.
	 * @param ctx the parse tree
	 */
	void enterWloop(langParser.WloopContext ctx);
	/**
	 * Exit a parse tree produced by {@link langParser#wloop}.
	 * @param ctx the parse tree
	 */
	void exitWloop(langParser.WloopContext ctx);
	/**
	 * Enter a parse tree produced by {@link langParser#ifstat}.
	 * @param ctx the parse tree
	 */
	void enterIfstat(langParser.IfstatContext ctx);
	/**
	 * Exit a parse tree produced by {@link langParser#ifstat}.
	 * @param ctx the parse tree
	 */
	void exitIfstat(langParser.IfstatContext ctx);
	/**
	 * Enter a parse tree produced by {@link langParser#ifbody}.
	 * @param ctx the parse tree
	 */
	void enterIfbody(langParser.IfbodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link langParser#ifbody}.
	 * @param ctx the parse tree
	 */
	void exitIfbody(langParser.IfbodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link langParser#elsebody}.
	 * @param ctx the parse tree
	 */
	void enterElsebody(langParser.ElsebodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link langParser#elsebody}.
	 * @param ctx the parse tree
	 */
	void exitElsebody(langParser.ElsebodyContext ctx);
	/**
	 * Enter a parse tree produced by the {@code singlecond}
	 * labeled alternative in {@link langParser#cond}.
	 * @param ctx the parse tree
	 */
	void enterSinglecond(langParser.SinglecondContext ctx);
	/**
	 * Exit a parse tree produced by the {@code singlecond}
	 * labeled alternative in {@link langParser#cond}.
	 * @param ctx the parse tree
	 */
	void exitSinglecond(langParser.SinglecondContext ctx);
	/**
	 * Enter a parse tree produced by the {@code noreturn}
	 * labeled alternative in {@link langParser#func}.
	 * @param ctx the parse tree
	 */
	void enterNoreturn(langParser.NoreturnContext ctx);
	/**
	 * Exit a parse tree produced by the {@code noreturn}
	 * labeled alternative in {@link langParser#func}.
	 * @param ctx the parse tree
	 */
	void exitNoreturn(langParser.NoreturnContext ctx);
	/**
	 * Enter a parse tree produced by the {@code withreturn}
	 * labeled alternative in {@link langParser#func}.
	 * @param ctx the parse tree
	 */
	void enterWithreturn(langParser.WithreturnContext ctx);
	/**
	 * Exit a parse tree produced by the {@code withreturn}
	 * labeled alternative in {@link langParser#func}.
	 * @param ctx the parse tree
	 */
	void exitWithreturn(langParser.WithreturnContext ctx);
	/**
	 * Enter a parse tree produced by {@link langParser#funccall}.
	 * @param ctx the parse tree
	 */
	void enterFunccall(langParser.FunccallContext ctx);
	/**
	 * Exit a parse tree produced by {@link langParser#funccall}.
	 * @param ctx the parse tree
	 */
	void exitFunccall(langParser.FunccallContext ctx);
	/**
	 * Enter a parse tree produced by {@link langParser#funcarg}.
	 * @param ctx the parse tree
	 */
	void enterFuncarg(langParser.FuncargContext ctx);
	/**
	 * Exit a parse tree produced by {@link langParser#funcarg}.
	 * @param ctx the parse tree
	 */
	void exitFuncarg(langParser.FuncargContext ctx);
	/**
	 * Enter a parse tree produced by the {@code valint}
	 * labeled alternative in {@link langParser#value}.
	 * @param ctx the parse tree
	 */
	void enterValint(langParser.ValintContext ctx);
	/**
	 * Exit a parse tree produced by the {@code valint}
	 * labeled alternative in {@link langParser#value}.
	 * @param ctx the parse tree
	 */
	void exitValint(langParser.ValintContext ctx);
	/**
	 * Enter a parse tree produced by the {@code valbool}
	 * labeled alternative in {@link langParser#value}.
	 * @param ctx the parse tree
	 */
	void enterValbool(langParser.ValboolContext ctx);
	/**
	 * Exit a parse tree produced by the {@code valbool}
	 * labeled alternative in {@link langParser#value}.
	 * @param ctx the parse tree
	 */
	void exitValbool(langParser.ValboolContext ctx);
	/**
	 * Enter a parse tree produced by {@link langParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(langParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link langParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(langParser.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link langParser#binaryop}.
	 * @param ctx the parse tree
	 */
	void enterBinaryop(langParser.BinaryopContext ctx);
	/**
	 * Exit a parse tree produced by {@link langParser#binaryop}.
	 * @param ctx the parse tree
	 */
	void exitBinaryop(langParser.BinaryopContext ctx);
	/**
	 * Enter a parse tree produced by {@link langParser#unaryop}.
	 * @param ctx the parse tree
	 */
	void enterUnaryop(langParser.UnaryopContext ctx);
	/**
	 * Exit a parse tree produced by {@link langParser#unaryop}.
	 * @param ctx the parse tree
	 */
	void exitUnaryop(langParser.UnaryopContext ctx);
	/**
	 * Enter a parse tree produced by {@link langParser#binassgn}.
	 * @param ctx the parse tree
	 */
	void enterBinassgn(langParser.BinassgnContext ctx);
	/**
	 * Exit a parse tree produced by {@link langParser#binassgn}.
	 * @param ctx the parse tree
	 */
	void exitBinassgn(langParser.BinassgnContext ctx);
	/**
	 * Enter a parse tree produced by {@link langParser#logicalop}.
	 * @param ctx the parse tree
	 */
	void enterLogicalop(langParser.LogicalopContext ctx);
	/**
	 * Exit a parse tree produced by {@link langParser#logicalop}.
	 * @param ctx the parse tree
	 */
	void exitLogicalop(langParser.LogicalopContext ctx);
	/**
	 * Enter a parse tree produced by {@link langParser#ordering}.
	 * @param ctx the parse tree
	 */
	void enterOrdering(langParser.OrderingContext ctx);
	/**
	 * Exit a parse tree produced by {@link langParser#ordering}.
	 * @param ctx the parse tree
	 */
	void exitOrdering(langParser.OrderingContext ctx);
}