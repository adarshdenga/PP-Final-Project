package symbol;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.FailedPredicateException;
import org.antlr.v4.runtime.InputMismatchException;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import antlr.langBaseListener;
import antlr.langLexer;
import antlr.langParser;
import antlr.langParser.ArrdeclContext;
import antlr.langParser.BinassignContext;
import antlr.langParser.BracketexprContext;
import antlr.langParser.BrackettermContext;
import antlr.langParser.DoubleexprbinContext;
import antlr.langParser.DoublelogicbinContext;
import antlr.langParser.ExprContext;
import antlr.langParser.ExprtermContext;
import antlr.langParser.FloopContext;
import antlr.langParser.FuncContext;
import antlr.langParser.FuncargContext;
import antlr.langParser.FunccallContext;
import antlr.langParser.IncContext;
import antlr.langParser.NoreturnContext;
import antlr.langParser.NotexprContext;
import antlr.langParser.OrderingexprContext;
import antlr.langParser.ProgContext;
import antlr.langParser.RegassignContext;
import antlr.langParser.ReturnValContext;
import antlr.langParser.SinglecondContext;
import antlr.langParser.TermfunccallContext;
import antlr.langParser.TermidentContext;
import antlr.langParser.TermvalContext;
import antlr.langParser.UninitdeclContext;
import antlr.langParser.ValboolContext;
import antlr.langParser.ValintContext;
import antlr.langParser.VardeclContext;
import antlr.langParser.WithreturnContext;
import antlr.langParser.WloopContext;

/**
 * Performs elaboration stage of the compilation process. 
 * This includes type checking as well as scope checking (i.e ensuring variables
 * are declared before assigned a value, making sure variables are not reused)
 */

public class LangElab extends langBaseListener {
    private SymbolTable symbolTable;
    private Set<String> errorset;
    private ParseTreeProperty<String> types = new ParseTreeProperty<>();
    private Map<String, List<String>> functionToArgTypes = new HashMap<>();
    private ParseTree tree;
    
    /** 
     * Begins the elaboration phase
     * @param chars stream of chars to process (i.e the file)
     * @return Set list of errors from the elaboration phase
     */
    public Set<String> process(CharStream chars) {
        Lexer lexer = new langLexer(chars);
        TokenStream tokens = new CommonTokenStream(lexer);
        langParser parser = new langParser(tokens);
        parser.setErrorHandler(new MyStrat());
        this.tree = parser.prog();
        new ParseTreeWalker().walk(this, tree);
        return this.errorset;
    }

    public ParseTree getTree() {
        return tree;
    }
    
    /** 
     * Initialization of the data structures to hold scopes, i.e start a symbol table
     * for scopes and open a scope.
     * @param ctx context for this listener method
     */
    @Override
    public void enterProg(ProgContext ctx) {
        this.symbolTable = new SymbolTable();
        this.errorset = new HashSet<>();

        for (FuncContext func : ctx.func()) {
            if (func instanceof NoreturnContext) {
                // store argument definitions
                NoreturnContext function = ((NoreturnContext) func);
                this.functionToArgTypes.put(function.IDENTIFIER(0).getText(), new ArrayList<>());
                for (int i = 1; i < function.IDENTIFIER().size(); i++) {
                    // store the type of each argument of the function definition,
                    // to type check arguments used in a function call.
                    this.functionToArgTypes.get(function.IDENTIFIER(0).getText()).add(function.type(i - 1).getText());
                }
            } else {
                WithreturnContext function = ((WithreturnContext) func);
                this.functionToArgTypes.put(function.IDENTIFIER(0).getText(), new ArrayList<>());
                for (int i = 1; i < function.IDENTIFIER().size(); i++) {
                    // store the type of each argument of the function definition,
                    // to type check arguments used in a function call.
                    this.functionToArgTypes.get(function.IDENTIFIER(0).getText()).add(function.type(i).getText());
                }
            }
        }
    }

    /** 
     * Tree listener method for entering a function definition. Ensures that for the main method, there
     * are no arguments (we won't support them in our language). In the symbol table, we should be in
     * the outermost scope, which contains function definitions. Ensures a function name isn't already in use.
     * Then opens a scope for this function, and adds all the arguments of the function to the new scope 
     * @param ctx context for this listener method
     */
    @Override
    public void enterNoreturn(NoreturnContext ctx) {
        // if main method, ensure no arguments
        if (ctx.IDENTIFIER(0).getText().toLowerCase().equals("main") && ctx.IDENTIFIER().size() > 1) {
            // new error if it has arguments
            errorset.add(String.format("Error on line %s, pos %s: main method cannot have arguments!.",
            ctx.IDENTIFIER(0).getSymbol().getLine(), ctx.IDENTIFIER(0).getSymbol().getCharPositionInLine()
            ));
        }
        // has a function with the same name already been declared?
        if (!symbolTable.inUse(ctx.IDENTIFIER(0).getText())) {
            // not in use so add this function to the outermost scope. The return type
            // is void (since this is a no return function)
            symbolTable.add(ctx.IDENTIFIER(0).getText(), "void");
            // open new scope for the function body
            symbolTable.openScope();
            // add all the arguments of the function to the new scope
            for (int i = 1; i < ctx.IDENTIFIER().size(); i++) {
                TerminalNode ident = ctx.IDENTIFIER(i);
                // ensure the variable name isn't in use
                if (!symbolTable.inUse(ident.getText())) {
                    symbolTable.add(ident.getText(), ctx.type(i - 1).getText());
                } else {
                    // throw error if variable redeclared
                    errorset.add(String.format("Error on line %s, pos %s: identifier %s is already in use.",
                            ident.getSymbol().getLine(), ident.getSymbol().getCharPositionInLine(),
                            ident.getSymbol().getText()));
                }
            }
        } else {
            // yes this function name is in use, add a new error for it
            errorset.add(String.format("Error on line %s, pos %s: function identifier %s is already in use.",
                    ctx.IDENTIFIER(0).getSymbol().getLine(), ctx.IDENTIFIER(0).getSymbol().getCharPositionInLine(),
                    ctx.IDENTIFIER(0).getSymbol().getText()));
        }
    }

    
    /** 
     * Exiting a function definition, close the scope
     * @param ctx context for this listener method
     */
    @Override
    public void exitNoreturn(NoreturnContext ctx) {
        symbolTable.closeScope();
    }

    
    /** Entering a function definition which has a return type. Performs the same tasks as 
     * {@link #enterNoreturn(NoreturnContext) enterNoreturn}. Ensure that a main method cannot have a 
     * return type. Add the function definition to the outermost scope with its expected return type.
     * @param ctx context for this listener method
     */
    @Override
    public void enterWithreturn(WithreturnContext ctx) {
        // main method can't have a return type, throw an error for this
        if (ctx.IDENTIFIER(0).getText().toLowerCase().equals("main")) {
            errorset.add(String.format("Error on line %s, pos %s: main method must be of return type void!.",
            ctx.IDENTIFIER(0).getSymbol().getLine(), ctx.IDENTIFIER(0).getSymbol().getCharPositionInLine()
            ));
        }
        // main method can't have arguments, throw error for this
        if (ctx.IDENTIFIER(0).getText().toLowerCase().equals("main") && ctx.IDENTIFIER().size() > 1) {
            errorset.add(String.format("Error on line %s, pos %s: main method cannot have arguments!.",
            ctx.IDENTIFIER(0).getSymbol().getLine(), ctx.IDENTIFIER(0).getSymbol().getCharPositionInLine()
            ));
        }
        // ensure function name isn't in use
        if (!symbolTable.inUse(ctx.IDENTIFIER(0).getText())) {
            // if not in use, add to symbol table
            symbolTable.add(ctx.IDENTIFIER(0).getText(), ctx.type(0).getText());
            // open scope for function body
            symbolTable.openScope();
            // add all the arguments to the function's scope
            for (int i = 1; i < ctx.IDENTIFIER().size(); i++) {
                TerminalNode ident  = ctx.IDENTIFIER(i);
                // ensure variable name isn't reused in the current scope
                if (!symbolTable.inUse(ident.getText())) {
                    symbolTable.add(ident.getText(), ctx.type(i).getText());
                } else {
                    // throw error if it is reused
                    errorset.add(String.format("Error on line %s, pos %s: identifier %s is already in use.",
                        ident.getSymbol().getLine(), ident.getSymbol().getCharPositionInLine(),
                        ident.getSymbol().getText()));
                }
            }
        } else {
            // throw error if function name reused
            errorset.add(String.format("Error on line %s, pos %s: function identifier %s is already in use.",
            ctx.IDENTIFIER(0).getSymbol().getLine(), ctx.IDENTIFIER(0).getSymbol().getCharPositionInLine(),
            ctx.IDENTIFIER(0).getSymbol().getText()));
       }
        
    }

    
    /** Ensure that the type of the value that is returned matches the return type of the function,
     * then close the function's scope
     * @param ctx context for this listener method
     */
    public void exitWithreturn(WithreturnContext ctx) {
        symbolTable.closeScope();
        // ensure return value's type matches return type of function
        if (!symbolTable.contains(ctx.IDENTIFIER(0).getText(), types.get(ctx.returnVal()))) {
            // if not, throw error
            errorset.add(String.format("Error on line %s, pos %s: return value of %s's type (%s) doesn't match function definition return type %s.",
                ctx.IDENTIFIER(0).getSymbol().getLine(), ctx.IDENTIFIER(0).getSymbol().getCharPositionInLine(),
                ctx.IDENTIFIER(0).getText(), types.get(ctx.returnVal()), ctx.type(0).getText()));
        }
       
    }

    /** Listener method for variable declaration. Ensure that the variable name isn't reused (in the current scope), and that
     * the value that is being assigned matches the type of the variable (using synthesized type checking). Also, add the 
     * declaration to the ParseTreeProperty for types, so that we can do synthesized type checking higher up in the tree.
     * @param ctx context for this listener method
     */
    @Override
    public void exitVardecl(VardeclContext ctx) {
        // ensure variable name isn't in use in current scope
        if (!symbolTable.inUse(ctx.IDENTIFIER().getText())) {
            // ensure the type of the value matches the type of the variable
            if (types.get(ctx.expr()).equals(ctx.type().getText())) {
                // all checks passed, add variable to the scope
                symbolTable.add(ctx.IDENTIFIER().getText(), ctx.type().getText());
                // add type of this declaration to the ParseTreeProperty, for use in synthesized type checking
                types.put(ctx, ctx.type().getText());
            } else {
                // type mismatch, throw error
                errorset.add(String.format("Error on line %s, pos %s: expr %s's type (%s) doesn't match variable's type %s.",
                        ctx.IDENTIFIER().getSymbol().getLine(), ctx.IDENTIFIER().getSymbol().getCharPositionInLine(),
                        ctx.expr().getText(), types.get(ctx.expr()), ctx.type().getText()));
            }
        } else {
            // variable name already in use, throw error
            errorset.add(String.format("Error on line %s, pos %s: identifier %s is already in use.",
                    ctx.IDENTIFIER().getSymbol().getLine(), ctx.IDENTIFIER().getSymbol().getCharPositionInLine(),
                    ctx.IDENTIFIER().getSymbol().getText()));
        }
    }

    
    /** Listener method for array declaration. Similar to {@link #exitVardecl(VardeclContext) exitVardecl},
     * but also ensures every value inside the array is of the same type the array is declared to be
     * @param ctx context for this listener method
     */
    @Override
    public void exitArrdecl(ArrdeclContext ctx) {
        // ensure variable name isn't already in use
        if (!symbolTable.inUse(ctx.IDENTIFIER().getText())) {
            // extract the inner type of the array (i.e Arr[int] -> int)
            String innerType = ctx.type().getText().substring(4, ctx.type().getText().length()-1);
            // ensure each value in the array matches the type of the array
            for (ExprContext expr : ctx.expr()) {
                if (types.get(expr).equals(innerType)) {
                    symbolTable.add(ctx.IDENTIFIER().getText(), ctx.type().getText());
                } else {
                    // type mismatch of value in array throw error.
                    errorset.add(String.format("Error on line %s, pos %s: expr %s (%s) is not a %s. All expressions in %s must be of type %s.",
                            ctx.IDENTIFIER().getSymbol().getLine(),
                            ctx.IDENTIFIER().getSymbol().getCharPositionInLine(), expr.getText(), types.get(expr),
                            innerType, ctx.type().getText(), innerType));
                }
            }
            // add array variable name to symbol table
            symbolTable.add(ctx.IDENTIFIER().getText(), ctx.type().getText());
            // add type of this declaration to the ParseTreeProperty, for use in synthesized type checking
            types.put(ctx, ctx.type().getText());
        } else {
            // variable name reused, throw error
            errorset.add(String.format("Error on line %s, pos %s: identifier %s is already in use.",
                    ctx.IDENTIFIER().getSymbol().getLine(), ctx.IDENTIFIER().getSymbol().getCharPositionInLine(),
                    ctx.IDENTIFIER().getSymbol().getText()));
        }
    }

    
    /** 
     * Create variable without assigning it a value at the current point in time. Otherwise similar to 
     * {@link #exitVardecl(VardeclContext) exitVardecl}, we only don't check for the types of values, since
     * there is no value
     * @param ctx context for this listener method

     */
    @Override
    public void exitUninitdecl(UninitdeclContext ctx) {
        // check if variable name in use
        if (!symbolTable.inUse(ctx.IDENTIFIER().getText())) {
            // no, add it to symbol table
            symbolTable.add(ctx.IDENTIFIER().getText(), ctx.type().getText());
            // add type of this declaration to the ParseTreeProperty, for use in synthesized type checking
            types.put(ctx, ctx.type().getText());
        } else {
            // variable name reused, throw error
            errorset.add(String.format("Error on line %s, pos %s: identifier %s is already in use.",
                    ctx.IDENTIFIER().getSymbol().getLine(), ctx.IDENTIFIER().getSymbol().getCharPositionInLine(),
                    ctx.IDENTIFIER().getSymbol().getText()));
        }
    }

    
    /** 
     * Type check when assigning a value to a variable that has already been declared (i.e x = 3 or y = 1+1).
     * A variable may have been declared in the current scope or a lexically enclosing one.
     * This listener will ensure that the variable name has been declared previously, and also type checks
     * the value against the declared type of the variable (using synthesized type checking)
     * @param ctx context for this listener method
     */
    @Override
    public void exitRegassign(RegassignContext ctx) {
        // ensure variable declared (either in the current scope or a lexically enclosing one) before assigning value
        if (symbolTable.varDeclared(ctx.IDENTIFIER().getText())) {
            // ensure the type of the value on the right hand side matches the variable's declared type
            // using synthesized type checking (types is a ParseTreeProperty which maps a Node to its type)
            if (!symbolTable.getType(ctx.IDENTIFIER().getText()).equals(types.get(ctx.expr()))) {
                // throw error for type mismatch
                errorset.add(String.format("Error on line %s, pos %s: variable %s's value doesn't match expected type.",
                        ctx.IDENTIFIER().getSymbol().getLine(), ctx.IDENTIFIER().getSymbol().getCharPositionInLine(),
                        ctx.IDENTIFIER().getSymbol().getText()));
            } else {
                // add type of this assignment to the ParseTreeProperty, for use in synthesized type checking
                types.put(ctx, types.get(ctx.expr()));
            }
        } else {
            // throw error for variable assignment before declaration
            errorset.add(String.format("Error on line %s, pos %s: variable %s's assignment before declaration.",
                    ctx.IDENTIFIER().getSymbol().getLine(), ctx.IDENTIFIER().getSymbol().getCharPositionInLine(),
                    ctx.IDENTIFIER().getSymbol().getText()));
        }
    }

    
    /** 
     * Similar to {@link #exitRegassign(RegassignContext) exitRegassign}, except for expressions
     * such as x += 3;
     * @param ctx context for this listener method
     */
    @Override
    public void exitBinassign(BinassignContext ctx) {
        // ensure variable declared (either in the current scope or a lexically enclosing one) before assigning value
        if (symbolTable.varDeclared(ctx.IDENTIFIER().getText())) {
            // bin assigns should only work with type ints. ensure this variable is of type int
            if (symbolTable.getType(ctx.IDENTIFIER().getText()).equals("int")) {
                // ensure the value on the right hand side is of type int
                if (!types.get(ctx.expr()).equals("int")) {
                    // can't do, for example x += bool when x is declared as an int. 
                    // throw error for type mismatch
                    errorset.add(String.format(
                            "Error on line %s, pos %s: unexpected assignment, type of %s doesn't match variable %s",
                            ctx.IDENTIFIER().getSymbol().getLine(),
                            ctx.IDENTIFIER().getSymbol().getCharPositionInLine(), ctx.expr().getText(),
                            ctx.IDENTIFIER().getSymbol().getText()));

                } else {
                    // add type of this assignment to the ParseTreeProperty, for use in synthesized type checking
                    types.put(ctx, "int");
                }
            } else {
                // throw error if variable isn't of type int
                errorset.add(
                        String.format("Error on line %s, pos %s: unexpected assignment, variable %s isn't an integer!",
                                ctx.IDENTIFIER().getSymbol().getLine(),
                                ctx.IDENTIFIER().getSymbol().getCharPositionInLine(), ctx.IDENTIFIER().getText()));
            }
        } else {
            // throw error for variable assignment before declarartion
            errorset.add(String.format("Error on line %s, pos %s: variable %s's assignment before declaration.",
                    ctx.IDENTIFIER().getSymbol().getLine(), ctx.IDENTIFIER().getSymbol().getCharPositionInLine(),
                    ctx.IDENTIFIER().getSymbol().getText()));
        }
    }

    
    /** Type check for variable unary operations, such as x++, x--, x**
     * @param ctx context for this listener method
     */
    @Override
    public void exitInc(IncContext ctx) {
        // ensure variable declaration before assignment
        if (!symbolTable.getType(ctx.IDENTIFIER().getText()).equals("int")) {
            // error if variable not declared as int
            errorset.add(
            String.format("Error on line %s, pos %s: unexpected assignment, variable %s isn't an integer!",
                    ctx.IDENTIFIER().getSymbol().getLine(),
                    ctx.IDENTIFIER().getSymbol().getCharPositionInLine(), ctx.IDENTIFIER().getText()));
            }
        
    }

    
    /** Type check for condition of an if-else statement. Ensure that the expression contained is of type boolean
     * from the ParseTreeProperty types (synthesized type checking)
     * @param ctx context for this listener method
     */
    
    @Override
    public void exitSinglecond(SinglecondContext ctx) {
        // ensure the expression is of type boolean using the ParseTreeProperty mapping Nodes to types
        if (!types.get(ctx.expr()).equals("bool")) {
            errorset.add(
                // error if condition isn't a boolean expression
                String.format("Error! %s couldn't be evaluated to a valid condition of type bool.", ctx.getText()));
        }
    }

    // @Override
    // public void exitChainedcond(ChainedcondContext ctx) {
    //         for (CondContext cond : ctx.cond()) {
    //             if (!types.get(cond).equals("bool")) {
    //                 errorset.add(String.format("Error! %s couldn't be evaluated to a valid condition of type bool.",
    //                     ctx.getText()));
    //             }
    //         }
    //     } else {
    //         errorset.add(String.format("Error! %s couldn't be evaluated to a valid condition of type bool.",
    //         ctx.getText()));
    //     }
    // }

    /**
     * Opens a new scope for the for loop
     * @param ctx context for this listener method
     */

    @Override
    public void enterFloop(FloopContext ctx) {
        symbolTable.openScope();
    }

    /**
     * Opens a new scope for the while loop
     * @param ctx context for this listener method
     */

    @Override
    public void enterWloop(WloopContext ctx) {
        symbolTable.openScope();
    }

    /**
     * Closes scope for the while loop when exiting
     * @param ctx context for this listener method
     */
    @Override
    public void exitWloop(WloopContext ctx) {
        symbolTable.openScope();
    }

    /**
     * Type check for for loop declaration; all we need to do is ensure there is a valid counter
     * declaration of type int, i.e for (i = 0 :: int) ...
     * Done using synthesized type checking. Also, close the scope for the for loop.
     * @param ctx context for this listener method
     */

    @Override
    public void exitFloop(FloopContext ctx) {
        if (!types.get(ctx.decl()).equals("int")) {
            errorset.add(String.format("Error! %s couldn't be evaluated to a valid for loop counter declaration of type int.",
            ctx.getText()));
        }
        symbolTable.closeScope();
    }

    
    /** 
     * Use synthesized type of the child term to set the type of this expression, to be used 
     * later for synthesized type checking
     * @param ctx context for this listener method
     */

     @Override
    public void exitExprterm(ExprtermContext ctx) {
        types.put(ctx, types.get(ctx.term()));
    }

    /** 
     * Use synthesized type of the child expression to ensure the expression is a bool, 
     * then set the type of this expression to bool (for example !(false)). To be used 
     * later for synthesized type checking
     * @param ctx context for this listener method
     */

    @Override
    public void exitNotexpr(NotexprContext ctx) {
         // ensure the expression is of type boolean using the ParseTreeProperty mapping Nodes to types
         if (!types.get(ctx.expr()).equals("bool")) {
            errorset.add(
                // error if expr isn't a boolean expression
                String.format("Error! %s couldn't be evaluated to a valid expression of type bool.", ctx.getText()));
        }
        types.put(ctx, "bool");
    }

    // @Override
    // public void exitExprbin(ExprbinContext ctx) {
    //     if (!(types.get(ctx.expr()).equals("bool") && types.get(ctx.term()).equals("bool"))) {
    //         if (!types.get(ctx.term()).equals(types.get(ctx.expr()))) {
    //             errorset.add(String.format("Error! Type mismatch in logical expression %s (%s is %s while %s is %s).",
    //             ctx.getText(), ctx.term().getText(), types.get(ctx.term()), ctx.expr().getText(), types.get(ctx.expr())));
    
    //         }
    //         types.put(ctx, types.get(ctx.term()));
    //     } else {
    //         errorset.add(String.format("Error! %s binary expressions can't have bools! In expression %s.",
    //             ctx.expr().getText(), ctx.getText()));
    //     }
    // }

    // @Override
    // public void exitLogicbin(LogicbinContext ctx) {
    //     // System.out.println((ctx.expr().getText()));
    //     if ((types.get(ctx.expr()).equals("bool") && types.get(ctx.term()).equals("bool"))) {
    //         if (!types.get(ctx.term()).equals(types.get(ctx.expr()))) {
    //             errorset.add(String.format("Error! Type mismatch in binary expression %s (%s is %s while %s is %s).",
    //             ctx.getText(), ctx.term().getText(), types.get(ctx.term()), ctx.expr().getText(), types.get(ctx.expr())));
    
    //         }
    //         types.put(ctx, types.get(ctx.term()));
    //     } else {
    //         errorset.add(String.format("Error! %s logical operations can't have ints! In expression %s.",
    //             ctx.expr().getText(), ctx.getText()));
    //     }
    // }

    /** 
     * Use synthesized type of the child value to set the type of this term in the ParseTreeProperty, to be used 
     * later for synthesized type checking
     * @param ctx context for this listener method
     */

    @Override
    public void exitTermval(TermvalContext ctx) {
        types.put(ctx, types.get(ctx.value()));
    }

    
    /** 
     * This is for something such as "x", where x is a variable. First find the type of the 
     * variable identifed by x, then we set the type of this term to that type in the ParseTreeProperty, to be used 
     * later for synthesized type checking 
     * @param ctx context for this listener method
     */
    @Override
    public void exitTermident(TermidentContext ctx) {
        types.put(ctx, symbolTable.getType(ctx.IDENTIFIER().getText()));
    }

    
    /** 
     * Use synthesized type of the child funccall to set the type of this term in the ParseTreeProperty, to be used 
     * later for synthesized type checking
     * @param ctx context for this listener method
     */

    @Override
    public void exitTermfunccall(TermfunccallContext ctx) {
        types.put(ctx, types.get(ctx.funccall()));
    }

    
     /** 
     * Use synthesized type of the child int to set the type of this value in the ParseTreeProperty, to be used 
     * later for synthesized type checking
     * @param ctx context for this listener method
     */

    @Override
    public void exitValint(ValintContext ctx) {
        types.put(ctx, "int");
    }

    
     /** 
     * Use synthesized type of the child bool to set the type of this value in the ParseTreeProperty, to be used 
     * later for synthesized type checking
     * @param ctx context for this listener method
     */
    @Override
    public void exitValbool(ValboolContext ctx) {
        types.put(ctx, "bool");
    }

    
     /** 
     * Use synthesized type of the child value to set the type of this term in the ParseTreeProperty, to be used 
     * later for synthesized type checking
     * @param ctx context for this listener method
     */
    
    @Override
    public void exitFunccall(FunccallContext ctx) {
        String functionName = ctx.IDENTIFIER().getText();
        if (functionToArgTypes.containsKey(functionName)) {
            // ensure that that the number of arguments is the same of the definition.
            if (ctx.funcarg().size() > 0) {
                // System.out.println(ctx.getText());
                // System.out.println(functionToArgTypes.get(functionName));
                if (functionToArgTypes.get(functionName).size() == (ctx.funcarg().size())) {
                    for (int i = 0; i < ctx.funcarg().size(); i++) {
                        if (!types.get(ctx.funcarg(i)).equals(functionToArgTypes.get(functionName).get(i))) {
                            // error for mismatched arg types
                            errorset.add(String.format("Error in call %s! Expected function arg at position %s of type %s, but found type %s instead.", ctx.getText(),
                            i, functionToArgTypes.get(functionName).get(i), ctx.funcarg().get(i)));
                        }
                    }
                } else {
                    errorset.add(
                        // error for mismatched number of args
                        String.format("Error! function %s expects %s arguments but %s were provided in call %s.", functionName,
                        functionToArgTypes.get(functionName).size(), ctx.funcarg().size(), ctx.getText()));
        
                }
            }
        } else {
            errorset.add(
                // error for mismatched number of args
                String.format("Error! Undefined function %s.", functionName));
        
        }

        types.put(ctx, symbolTable.getType(ctx.IDENTIFIER().getText()));
    }
    
    @Override
    public void exitFuncarg(FuncargContext ctx) {
        if (ctx.IDENTIFIER() != null) {
            types.put(ctx, symbolTable.getType(ctx.IDENTIFIER().getText()));
        } else {
            types.put(ctx, types.get(ctx.value()));
        }
    }
    
    /** 
     * Use synthesized type of the child expression to set the type of this term in the ParseTreeProperty, to be used 
     * later for synthesized type checking (if we do return x where x is an int, the type of this should be int)
     * @param ctx context for this listener method
     */
    @Override
    public void exitReturnVal(ReturnValContext ctx) {
        types.put(ctx, types.get(ctx.expr()));
    }

    
    /** 
     * Use synthesized type of the child expression to set the type of this term in the ParseTreeProperty, to be used 
     * later for synthesized type checking (this is an expression like (x + y))
     * @param ctx context for this listener method
     */
    @Override
    public void exitBracketexpr(BracketexprContext ctx) {
        types.put(ctx, types.get(ctx.expr()));
    }

    
    /** 
     * Use synthesized type of the child term to set the type of this term in the ParseTreeProperty, to be used 
     * later for synthesized type checking
     * @param ctx context for this listener method
     */
    @Override
    public void exitBracketterm(BrackettermContext ctx) {
        types.put(ctx, types.get(ctx.term()));

    }    

    
    /** 
     * @param ctx context for this listener method

     */
    @Override
    public void exitDoubleexprbin(DoubleexprbinContext ctx) {
        if (!(types.get(ctx.expr(1)).equals("bool") && types.get(ctx.expr(0)).equals("bool"))) {
            if (!types.get(ctx.expr(0)).equals(types.get(ctx.expr(1)))) {
                errorset.add(String.format("Error! Type mismatch in binary expression %s (%s is %s while %s is %s).",
                ctx.getText(), ctx.expr(0).getText(), types.get(ctx.expr(0)), ctx.expr(1).getText(), types.get(ctx.expr(1))));
    
            }
            types.put(ctx, types.get(ctx.expr(0)));
        } else {
            errorset.add(String.format("Error! %s logical operations can't have ints! In expression %s.",
                ctx.expr(1).getText(), ctx.getText()));
        }
    }

    
    /** 
     * @param ctx context for this listener method

     */
    public void exitDoublelogicbin(DoublelogicbinContext ctx) {
        if ((types.get(ctx.expr(1)).equals("bool") && types.get(ctx.expr(0)).equals("bool"))) {
            if (!types.get(ctx.expr(0)).equals(types.get(ctx.expr(1)))) {
                errorset.add(String.format("Error! Type mismatch in logical expression %s (%s is %s while %s is %s).",
                ctx.getText(), ctx.expr(0).getText(), types.get(ctx.expr(0)), ctx.expr(1).getText(), types.get(ctx.expr(1))));
    
            }
            types.put(ctx, types.get(ctx.expr(0)));
        } else {
            errorset.add(String.format("Error! %s binary expressions can't have bools! In expression %s.",
                ctx.expr(1).getText(), ctx.getText()));
        }
    }

    
    /** 
     * @param ctx context for this listener method

     */
    public void exitOrderingexpr(OrderingexprContext ctx) {
        if (!types.get(ctx.expr(0)).equals(types.get(ctx.expr(1)))) {
            errorset.add(String.format("Error! Type mismatch in logical expression %s (%s is %s while %s is %s).",
            ctx.getText(), ctx.expr(0).getText(), types.get(ctx.expr(0)), ctx.expr(1).getText(), types.get(ctx.expr(1))));

        }
        if (!(ctx.ordering().getText().equals("==") || ctx.ordering().getText().equals("!="))) {
            if (!types.get(ctx.expr(0)).equals("int") && !types.get(ctx.expr(1)).equals("int")) {
                errorset.add(String.format("Error! You can only do == and != with non integer expressions. in logical expression %s (%s is %s and %s is %s)",
                    ctx.getText(), ctx.expr(0).getText(), types.get(ctx.expr(0)), ctx.expr(1).getText(), types.get(ctx.expr(1))));
    
            }
        }
        types.put(ctx, "bool");
    }

    class MyStrat extends DefaultErrorStrategy {
        @Override
	    public void reportError(Parser recognizer, RecognitionException e) {
            // if we've already reported an error and have not matched a token
            // yet successfully, don't report any errors.
            if (inErrorRecoveryMode(recognizer)) {
    //			System.err.print("[SPURIOUS] ");
                return; // don't report spurious errors
            }
            beginErrorCondition(recognizer);
            if ( e instanceof NoViableAltException ) {
                reportNoViableAlternative(recognizer, (NoViableAltException) e);
            }
            else if ( e instanceof InputMismatchException ) {
                reportInputMismatch(recognizer, (InputMismatchException)e);
            }
            else if ( e instanceof FailedPredicateException ) {
                reportFailedPredicate(recognizer, (FailedPredicateException)e);
            }
            else {
                System.err.println("unknown recognition error type: "+e.getClass().getName());
                recognizer.notifyErrorListeners(e.getOffendingToken(), e.getMessage(), e);
            }
            System.exit(0);
        }
    }
    
    /** 
     * @param args filename
     */
    public static void main(String[] args) {
        System.out.println("Starting elaboration process for file sampleprog.aapfl: type checking, scope checking...");
        LangElab listener = new LangElab();
        System.out.println("\u001B[33mParser errors, if any: \u001B[0m");
        try {
            Set<String> errors = listener.process(CharStreams.fromFileName(System.getProperty("user.dir") + "/src/antlr/sampleprog.aapfl"));
            System.out.println(errors.size() > 0 ? "\u001B[33mErrors found (" + errors.size() + ")...\u001B[0m" : "\u001B[32mDone!\u001B[0m");
            for (String error : errors) {
                System.err.println("\u001B[31m" + error + "\u001B[0m");
            }
            assert(errors.size() != 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Starting elaboration process for file failprog.aapfl: type checking, scope checking...");
        listener = new LangElab();
        try {
            Set<String> errors = listener.process(CharStreams.fromFileName(System.getProperty("user.dir") + "/src/antlr/failprog.aapfl"));
            System.out.println(errors.size() > 0 ? "\u001B[33mErrors found (" + errors.size() + ")...\u001B[0m" : "\u001B[32mDone!\u001B[0m");
            for (String error : errors) {
                System.err.println("\u001B[31m" + error + "\u001B[0m");
            }
            assert(errors.size() != 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
