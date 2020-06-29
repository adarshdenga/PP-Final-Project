package compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import antlr.langBaseListener;
import antlr.langParser.NoreturnContext;
import antlr.langParser.WithreturnContext;
import antlr.langParser.FuncContext;

public class FirstPass extends langBaseListener{
    private Map<String, List<String>> funcnamestoargs;
    private Map<String, FuncContext> funcnamestodefs;
    public void process(ParseTree tree) {
        this.funcnamestoargs = new HashMap<>();
        this.funcnamestodefs = new HashMap<>();
        new ParseTreeWalker().walk(this, tree);
    }

    public void exitNoreturn(NoreturnContext ctx) {
        List<String> args = new ArrayList<>();
        for (int i = 1; i < ctx.IDENTIFIER().size(); i++) {
            args.add(ctx.IDENTIFIER(i).getText());
        }
        funcnamestoargs.put(ctx.IDENTIFIER(0).getText(), args);    
        funcnamestodefs.put(ctx.IDENTIFIER(0).getText(), ctx);
    }

    public void exitWithreturn(WithreturnContext ctx) {
        List<String> args = new ArrayList<>();
        for (int i = 1; i < ctx.IDENTIFIER().size(); i++) {
            args.add(ctx.IDENTIFIER(i).getText());
        }
        funcnamestoargs.put(ctx.IDENTIFIER(0).getText(), args);    
        funcnamestodefs.put(ctx.IDENTIFIER(0).getText(), ctx);
    }

    public Map<String, List<String>> getFuncnamestoargs() {
        return funcnamestoargs;
    }

    public Map<String, FuncContext> getFuncnamestodefs() {
        return funcnamestodefs;
    }
}