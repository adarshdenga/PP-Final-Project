package compiler;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import antlr.langBaseVisitor;
import antlr.langParser.BinassignContext;
import antlr.langParser.BracketexprContext;
import antlr.langParser.BrackettermContext;
import antlr.langParser.DoubleexprbinContext;
import antlr.langParser.DoublelogicbinContext;
import antlr.langParser.ExprtermContext;
import antlr.langParser.FloopContext;
import antlr.langParser.FuncContext;
import antlr.langParser.FunccallContext;
import antlr.langParser.IfstatContext;
import antlr.langParser.IncContext;
import antlr.langParser.NoreturnContext;
import antlr.langParser.NotexprContext;
import antlr.langParser.OrderingexprContext;
import antlr.langParser.RegassignContext;
import antlr.langParser.ReturnValContext;
import antlr.langParser.StatContext;
import antlr.langParser.TermfunccallContext;
import antlr.langParser.TermidentContext;
import antlr.langParser.TermvalContext;
import antlr.langParser.UninitdeclContext;
import antlr.langParser.ValboolContext;
import antlr.langParser.ValintContext;
import antlr.langParser.VardeclContext;
import antlr.langParser.WithreturnContext;
import antlr.langParser.WloopContext;
import antlr.langParser.SinglecondContext;
import antlr.langParser.ForkfuncstatContext;
import antlr.langParser.JoinstatContext;
import model.Addr;
import model.Instr;
import model.Label;
import model.Op;
import model.OpCode;
import model.Operand;
import model.Operator;
import model.Program;
import model.Reg;
import model.Target;
import model.Addr.AddrEnum;
import model.Operator.OperatorType;
import model.Target.TargetEnum;
import symbol.LangElab;

public class Compiler extends langBaseVisitor {
    private Map<String, FuncContext> funcnamestodefs;
    private Map<String, List<String>> funcnamestoargs;

    private ParseTreeProperty<Reg> intermediateRegs;

    private Program program;
    private Map<Integer, Map<String, Addr>> regularMemory;
    private Map<Integer, Map<String, Addr>> sharedMemory;

    private int lastmemory = 10;
    private Set<Integer> usedregs;

    public static int currentSprockell = 0;
    public static List<Instr> concurrencyInstrs = new ArrayList<>();

    public Compiler(Map<String, FuncContext> funcnamestodefs, Map<String, List<String>> funcnamestoargs) {
        this.funcnamestoargs = funcnamestoargs;
        this.funcnamestodefs = funcnamestodefs;

        this.program = new Program();
        this.regularMemory = new HashMap<>();
        this.sharedMemory = new HashMap<>();
        this.intermediateRegs = new ParseTreeProperty<>();

        this.usedregs = new HashSet<>();
    }

    public String compile() {
        this.regularMemory.put(this.regularMemory.size(), new HashMap<>());
        this.sharedMemory.put(this.sharedMemory.size(), new HashMap<>());

        // Emit the instruction with regSprID
        Reg sprID = new Reg ("regSprID", 1);
        emit(OpCode.Branch, sprID, new Target(TargetEnum.Rel, 0));

        visit(funcnamestodefs.get("main"));

        emit(OpCode.EndProg);
        program.getInstr().get(program.size() - 1).setLabel(new Label("f_e"));

        concurrencyInstrs.addAll(program.getInstr());
        
        addAddresses();

        return this.program.toString();
    }

    @Override
    public Object visitTermfunccall(TermfunccallContext ctx) {
        visit(ctx.funccall());
        Reg reg = this.intermediateRegs.get(ctx.funccall());
        this.intermediateRegs.put(ctx, reg);
        return null;
    }

    @Override
    public Object visitFunccall(FunccallContext ctx) {
        String functionname = ctx.IDENTIFIER().getText();
        // First, open a new scope for the function
        sharedMemory.put(sharedMemory.size(), new HashMap<>());
        regularMemory.put(regularMemory.size(), new HashMap<>());
        
        if (funcnamestoargs.get(functionname) == null) {
            System.out.println("Error! Undefined function call " + functionname);
            System.exit(0);
        }        
        for (int i = 0; i < funcnamestoargs.get(functionname).size(); i++) {
            if (ctx.funcarg(i).value() != null) {
                visit(ctx.funcarg(i).value());
                String varname = funcnamestoargs.get(functionname).get(i);
                Addr addr = new Addr(AddrEnum.DirAddr, lastmemory);
                storeVarAddress(false, varname, addr , true);
                Reg reg = intermediateRegs.get(ctx.funcarg(i).value());
                storeToMemory(false, reg, addr);
                lastmemory+= 1;
            } else {
                String varname = funcnamestoargs.get(functionname).get(i);
                Addr addr = findVarAddress(false, ctx.funcarg(i).IDENTIFIER().getText());
                storeVarAddress(false, varname, addr , true);
            }
        }

        if (funcnamestodefs.get(functionname) instanceof NoreturnContext) {
            visit ((NoreturnContext) funcnamestodefs.get(functionname));
        } else {
            visit((WithreturnContext) funcnamestodefs.get(functionname));
        }
        intermediateRegs.put(ctx, intermediateRegs.get(funcnamestodefs.get(functionname)));
        
        sharedMemory.remove(sharedMemory.size()-1);
        regularMemory.remove(regularMemory.size()-1);

        return null;
    }

    @Override
    public Object visitNoreturn(NoreturnContext ctx) {
        for (StatContext stat : ctx.stat()) {
            visit(stat);
        }
        return null;
    }

    @Override
    public Object visitWithreturn(WithreturnContext ctx) {
        for (StatContext stat : ctx.stat()) {
            visit(stat);
        }
        visit(ctx.returnVal());
        this.intermediateRegs.put(ctx, intermediateRegs.get(ctx.returnVal()));
        return null;
    }

    @Override
    public Object visitReturnVal(ReturnValContext ctx) {
        visit(ctx.expr());
        this.intermediateRegs.put(ctx, intermediateRegs.get(ctx.expr()));
        return null;
    }


    @Override
    public Object visitValbool(ValboolContext ctx) {
        Addr immval = null;
        if (ctx.BOOLEAN().getText().equals("False")) {
            immval = new Addr(AddrEnum.ImmValue, 0);
        } else {
            immval = new Addr(AddrEnum.ImmValue, 1);
        }
        Reg reg = loadFromMemory(false, immval);
        this.intermediateRegs.put(ctx, reg);
        return null;
    }

    @Override
    public Object visitValint(ValintContext ctx) {
        Addr immval = new Addr(AddrEnum.ImmValue, Integer.parseInt(ctx.INTEGER().getText()));
        Reg reg = loadFromMemory(false, immval);
        this.intermediateRegs.put(ctx, reg);
        return null;
    }

    @Override
    public Object visitBracketterm(BrackettermContext ctx) {
        visit(ctx.term());
        this.intermediateRegs.put(ctx, this.intermediateRegs.get(ctx.term()));
        return null;
    }

    @Override
    public Object visitTermval(TermvalContext ctx) {
        visit(ctx.value());
        this.intermediateRegs.put(ctx, this.intermediateRegs.get(ctx.value()));
        return null;
    }

    @Override
    public Object visitTermident(TermidentContext ctx) {
        if (this.memoryContainsVar(sharedMemory, ctx.IDENTIFIER().getText())) {
            Addr addr = findVarAddress(true, ctx.IDENTIFIER().getText());
            Reg reg = loadFromMemory(true, addr);
            this.intermediateRegs.put(ctx, reg);
        } else {
            Addr addr = findVarAddress(false, ctx.IDENTIFIER().getText());
            Reg reg = loadFromMemory(false, addr);
            this.intermediateRegs.put(ctx, reg);
        }
        return null;
    }

    @Override
    public Object visitInc(IncContext ctx) {
        Addr addr = null;
        boolean shared = this.memoryContainsVar(sharedMemory, ctx.IDENTIFIER().getText());
        addr = this.findVarAddress(shared, ctx.IDENTIFIER().getText());
        Reg reg = loadFromMemory(shared, addr);

        switch (ctx.unaryop().getText()) {
            case "++": {
                emit(OpCode.Compute, new Operator(OperatorType.Incr), reg, reg, reg);
                break;
            }
            case "--": {
                emit(OpCode.Compute, new Operator(OperatorType.Decr), reg, reg, reg);
                break;
            }
            case "**": {
                emit(OpCode.Compute, new Operator(OperatorType.Mul), reg, reg, reg);
                break;
            }
        }

        storeToMemory(shared, reg, addr);

        releaseReg(reg);
        return null;
    }

    @Override
    public Object visitBracketexpr(BracketexprContext ctx) {
        visit(ctx.expr());
        this.intermediateRegs.put(ctx, intermediateRegs.get(ctx.expr()));
        return null;
    }

    @Override
    public Object visitNotexpr(NotexprContext ctx) {
        visit(ctx.expr());

        Reg reg1 = this.intermediateRegs.get(ctx.expr());
        Addr immval = new Addr(AddrEnum.ImmValue, 1);
        Reg reg2 = loadFromMemory(false, immval);
        emit(OpCode.Compute, new Operator(OperatorType.Xor), reg1, reg2, reg1);

        releaseReg(reg2);

        return null;
    }

    @Override
    public Object visitExprterm(ExprtermContext ctx) {
        visit(ctx.term());
        this.intermediateRegs.put(ctx, intermediateRegs.get(ctx.term()));
        return null;
    }

    @Override
    public Object visitDoubleexprbin(DoubleexprbinContext ctx) {
        visit(ctx.expr(0));
        visit(ctx.expr(1));

        Reg reg1 = this.intermediateRegs.get(ctx.expr(0));
        Reg reg2 = this.intermediateRegs.get(ctx.expr(1));

        switch (ctx.binaryop().getText()) {
            case "+": {
                emit(OpCode.Compute, new Operator(OperatorType.Add), reg1, reg2, reg1);
                break;
            }
            case "-": {
                emit(OpCode.Compute, new Operator(OperatorType.Sub), reg1, reg2, reg1);
                break;
            }
            case "*": {
                emit(OpCode.Compute, new Operator(OperatorType.Mul), reg1, reg2, reg1);
                break;
            }
        }
        this.intermediateRegs.put(ctx, reg1);
        releaseReg(reg2);

        return null;
    }

    @Override
    public Object visitDoublelogicbin(DoublelogicbinContext ctx) {
        visit(ctx.expr(0));
        visit(ctx.expr(1));

        Reg reg1 = this.intermediateRegs.get(ctx.expr(0));
        Reg reg2 = this.intermediateRegs.get(ctx.expr(1));


        switch (ctx.logicalop().getText()) {
            case "and": {
                emit(OpCode.Compute, new Operator(OperatorType.And), reg1, reg2, reg1);
                break;
            }
            case "or": {
                emit(OpCode.Compute, new Operator(OperatorType.Or), reg1, reg2, reg1);
                break;
            }
        }

        this.intermediateRegs.put(ctx, reg1);
        releaseReg(reg2);

        return null;
    }

    @Override
    public Object visitOrderingexpr(OrderingexprContext ctx) {
        visit(ctx.expr(0));
        visit(ctx.expr(1));

        Reg reg1 = this.intermediateRegs.get(ctx.expr(0));
        Reg reg2 = this.intermediateRegs.get(ctx.expr(1));


        switch (ctx.ordering().getText()) {
            case ">": {
                emit(OpCode.Compute, new Operator(OperatorType.Gt), reg1, reg2, reg1);
                break;
            }
            case "<": {
                emit(OpCode.Compute, new Operator(OperatorType.Lt), reg1, reg2, reg1);
                break;
            }
            case "==": {
                emit(OpCode.Compute, new Operator(OperatorType.Equal), reg1, reg2, reg1);
                break;
            }
            case "<=": {
                emit(OpCode.Compute, new Operator(OperatorType.LtE), reg1, reg2, reg1);
                break;
            }
            case ">=": {
                emit(OpCode.Compute, new Operator(OperatorType.GtE), reg1, reg2, reg1);
                break;
            }
            case "!=": {
                emit(OpCode.Compute, new Operator(OperatorType.NEq), reg1, reg2, reg1);
                break;
            }
        }

        this.intermediateRegs.put(ctx, reg1);

        releaseReg(reg2);

        return null;
    }

    @Override
    public Object visitRegassign(RegassignContext ctx) {
        visit(ctx.expr());

        boolean shared = memoryContainsVar(sharedMemory, ctx.IDENTIFIER().getText());

        Addr addr =  findVarAddress(shared, ctx.IDENTIFIER().getText());
        Reg reg = this.intermediateRegs.get(ctx.expr());
        storeToMemory(shared, reg, addr);

        return null;
    }

    @Override
    public Object visitBinassign(BinassignContext ctx) {
        visit(ctx.expr());
        boolean shared = this.memoryContainsVar(sharedMemory, ctx.IDENTIFIER().getText());
        Addr addr = this.findVarAddress(shared, ctx.IDENTIFIER().getText());
        Reg reg1 = loadFromMemory(shared, addr);
        Reg reg2 = intermediateRegs.get(ctx.expr());
        switch (ctx.binassgn().getText()) {
            case "+=": {
                emit(OpCode.Compute, new Operator(OperatorType.Add), reg1, reg2, reg1);
                break;
            }
            case "-=": {
                emit(OpCode.Compute, new Operator(OperatorType.Add), reg1, reg2, reg1);
                break;
            }
            case "*=": {
                emit(OpCode.Compute, new Operator(OperatorType.Mul), reg1, reg2, reg1);
                break;
            }
        }

        storeToMemory(shared, reg1, addr);

        releaseReg(reg1);
        releaseReg(reg2);
        return null;
    }

    @Override
    public Object visitVardecl(VardeclContext ctx) {
        visit(ctx.expr());
        boolean shared = ctx.getText().substring(3, 9).equals("shared");
        Reg reg = intermediateRegs.get(ctx.expr());
        Addr addr = new Addr(AddrEnum.DirAddr, lastmemory);
        storeVarAddress(shared, ctx.IDENTIFIER().getText(), addr, true);
        storeToMemory(shared, reg, addr);
        lastmemory+= 1;
        releaseReg(reg);

        return null;
    }

    @Override
    public Object visitUninitdecl(UninitdeclContext ctx) {
        boolean shared = ctx.getText().substring(3, 9).equals("shared");

        String type = ctx.type().getText();
        int val = 0; // default value 0 for ints, true for bools

        if (!type.equals("bool") && !type.equals("int")) {
            System.out.println("Unsupported type " + type);
        }

        Addr immval = new Addr(AddrEnum.ImmValue, val);
        Reg reg = loadFromMemory(false, immval);
        Addr valaddr = new Addr(AddrEnum.DirAddr, lastmemory);
        storeVarAddress(shared, ctx.IDENTIFIER().getText(), valaddr, true);
        storeToMemory(shared, reg, valaddr);

        lastmemory += 1;
        releaseReg(reg);

        return null;
    }

    public Object visitSinglecond(SinglecondContext ctx) {
        visit(ctx.expr());
        intermediateRegs.put(ctx, intermediateRegs.get(ctx.expr()));
        return null;
    }

    @Override
    public Object visitIfstat(IfstatContext ctx){

        // Line just befpore the if starts
        int preIf = program.size() -1;

        // open scope
        sharedMemory.put(sharedMemory.size(), new HashMap<>());
        regularMemory.put(regularMemory.size(), new HashMap<>());

        // Add the body of the if to the prog
        visit(ctx.ifbody());
    
        // close scope
        sharedMemory.remove(sharedMemory.size()-1);
        regularMemory.remove(regularMemory.size()-1);

        // Line the if block ends at
        int postIf = program.size() - 1;
        // Size of the if block
        int ifSize = postIf - preIf;

        // Save the if block as a stack of instructions
        Deque<Instr> ifbl = new ArrayDeque<>();
        for (int i = 0; i < ifSize; i++) {
            ifbl.push(program.removeInstruction());
        }
        
        if(ctx.elsebody() == null){
            // There isn't an else

            visit(ctx.cond());
            Reg reg = intermediateRegs.get(ctx.cond());

            // not the condition so we branch if it's false
            Addr immval = new Addr(AddrEnum.ImmValue, 1);
            Reg reg2 = loadFromMemory(false, immval);
            emit(OpCode.Compute, new Operator(OperatorType.Xor), reg, reg2, reg);

            releaseReg(reg2);

            
            // Emit the instruction for the condition
            emit(OpCode.Branch, reg, new Target(Target.TargetEnum.Rel, ifSize + 1));

            releaseReg(reg);

            // Add instructions back
            for (int i = 0; i < ifSize; i++) {
                program.addInstr(ifbl.pop());
            }

        } else {
            
            // There is an else

            // Line just before the start of the else block
            int preElse = program.size() - 1;

            // open Else scope
            sharedMemory.put(sharedMemory.size(), new HashMap<>());
            regularMemory.put(regularMemory.size(), new HashMap<>());

            // Add the body of the else to the prog
            visit(ctx.elsebody());
        
            // close Else scope
            sharedMemory.remove(sharedMemory.size()-1);
            regularMemory.remove(regularMemory.size()-1);

            // Line the else block ends at
            int postElse = program.size() - 1;
            // Size of the else block
            int elseSize = postElse - preElse;

            // Save the else block as a stack of instructions
            Deque<Instr> elbl = new ArrayDeque<>();
            for (int i = 0; i < elseSize; i++) {
                elbl.push(program.removeInstruction());
            }

            // Assembling the instructions
            
            // Condition and Jump
            visit(ctx.cond());
            Reg reg = intermediateRegs.get(ctx.cond());
            // not the condition so we branch if it's false
            Addr immval = new Addr(AddrEnum.ImmValue, 1);
            Reg reg2 = loadFromMemory(false, immval);
            emit(OpCode.Compute, new Operator(OperatorType.Xor), reg, reg2, reg);

            releaseReg(reg2);

            // Emit the instruction for the condition, and +2 since we need the jump after the if to outside the block
            emit(OpCode.Branch, reg, new Target(Target.TargetEnum.Rel, ifSize + 2));

            releaseReg(reg);

            // If block
            // Add if body back
            for (int i = 0; i < ifSize; i++) {
                program.addInstr(ifbl.pop());
            }

            // Jump from if statement to outside the block
            emit(OpCode.Jump, new Target(Target.TargetEnum.Rel, elseSize + 1));

            // Add else body back
            for (int i = 0; i < elseSize; i++) {
                program.addInstr(elbl.pop());
            }
        }

        return null;
    }

    @Override
    public Object visitWloop(WloopContext ctx) {
        // Line just before while block
        int preWhile = program.size() - 1;

        // Add the body of the while block
        sharedMemory.put(sharedMemory.size(), new HashMap<>());
        regularMemory.put(regularMemory.size(), new HashMap<>());
        for (StatContext stat : ctx.stat()) {
            visit(stat);
        }
        sharedMemory.remove(sharedMemory.size()-1);
        regularMemory.remove(regularMemory.size()-1);

        // Line just at the end of the while block
        int postWhile = program.size() -1;
        // Get the number of lines for the while block
        int whileSize = postWhile - preWhile;


        // Save the While block as a stack of instructions
        Deque<Instr> whbl = new ArrayDeque<>();
        for (int i = 0; i < whileSize; i++) {
            whbl.push(program.removeInstruction());
        }

        // Evaluate the condition
        // Get the value of the condition
        visit(ctx.cond());
        Reg reg = intermediateRegs.get(ctx.cond());
        
        // not the condition so we branch if it's false
        Addr immval = new Addr(AddrEnum.ImmValue, 1);
        Reg reg2 = loadFromMemory(false, immval);
        emit(OpCode.Compute, new Operator(OperatorType.Xor), reg, reg2, reg);

        releaseReg(reg2);

        // Emit the instruction for the condition
        emit(OpCode.Branch, reg, new Target(Target.TargetEnum.Rel, whileSize + 2));

        releaseReg(reg);

        // Add instructions back
        for (int i = 0; i < whileSize; i++) {
            program.addInstr(whbl.pop());
        }
        int something = program.size();
        System.out.println(something);
        emit(OpCode.Jump, new Target(Target.TargetEnum.Rel, (preWhile - something) + 1));

        return null;
    }

    @Override
    public Object visitFloop(FloopContext ctx) {
        sharedMemory.put(sharedMemory.size(), new HashMap<>());
        regularMemory.put(regularMemory.size(), new HashMap<>());

        visit(ctx.decl());

        // Line just before for loop body
        int preFor = program.size() - 1;

        // Add the body of the for loop
        for (StatContext stat : ctx.stat()) {
            visit(stat);
        }

        // Line just at the end of the for loop body
        int postFor = program.size() -1;
        
        // calculate size of the for loop body
        int forSize = postFor - preFor;

        // Save the For loop block as a stack of instructions
        Deque<Instr> forblock = new ArrayDeque<>();
        for (int i = 0; i < forSize; i++) {
            forblock.push(program.removeInstruction());
        }

        // line number before condition
        int precond = program.size() -1;
        // Evaluate the condition
        visit(ctx.cond());
        // Get condition value
        Reg reg = intermediateRegs.get(ctx.cond());
        
        // not the condition so we branch if it's false
        Addr immval = new Addr(AddrEnum.ImmValue, 1);
        Reg reg2 = loadFromMemory(false, immval);
        emit(OpCode.Compute, new Operator(OperatorType.Xor), reg, reg2, reg);

        releaseReg(reg2);

        // line number before the increment instructions
        int incbefore = program.size() -1;

        // evaluate increment instructions, depending on what kind of increment it is
        if (ctx.inc() != null) {
            visit(ctx.inc());
        } else {
            visit(ctx.assgn());
        }
        // line number after the increment instructions
        int incafter = program.size() -1;
        // calculate size of the increment instructions
        int incsize = incafter - incbefore;

        // remove the increment instructions to readd after the for loop body (to increment before next iteration)
        Deque<Instr> incbl = new ArrayDeque<>();
        for (int i = 0; i < incsize; i++) {
            incbl.push(program.removeInstruction());
        }

        // Emit the instruction for jumping, when the condition evaluates to false
        emit(OpCode.Branch, reg, new Target(Target.TargetEnum.Rel, forSize + incsize + 2));
        releaseReg(reg);
        
        // line number for after the condition
        int postcond = program.size() -1;
        // calcualte size of condition instructions
        int condsize = postcond - precond;

        // Add instructions back for the for loop body
        for (int i = 0; i < forSize; i++) {
            program.addInstr(forblock.pop());
        }

        // Add instructions back for the increment step
        for (int i = 0; i < incsize; i++) {
            program.addInstr(incbl.pop());
        }

        // Figure out where the condition is so we know where to jump back
        int wheretojumpback = forSize + condsize + incsize ;
        // jump to condition before next iteration
        emit(OpCode.Jump, new Target(Target.TargetEnum.Rel, -1 * (wheretojumpback)));
        
        return null;
    }

// -----------------------------------------------------------------------------------------------------------

    @Override public Object visitForkfuncstat(ForkfuncstatContext ctx){

        currentSprockell = currentSprockell + 1;

        Reg reg1 = getEmptyReg();
        emit(OpCode.Load, new Addr(AddrEnum.ImmValue, program.size()-1), reg1);
        program.getInstr().get(program.size() - 1).setLabel(new Label ("f_c" + currentSprockell + ""));
        emit(OpCode.WriteInstr, reg1,new Addr(AddrEnum.DirAddr, currentSprockell));
        releaseReg(reg1);

        int prefork = program.size() - 1;

        visit(ctx.funccall());

        int postfork = program.size() -1;
        int forksize = postfork - prefork;

        Deque<Instr> forkinstrs = new ArrayDeque<>();
        for(int i  = 0; i < forksize; i++){
            forkinstrs.push(program.removeInstruction());
        }

        emit(OpCode.Jump, new Target(TargetEnum.Rel, forksize + 2));

        for(int i  = 0; i < forksize; i++){
            program.addInstr(forkinstrs.pop());
        }

        Instr jmp = new Op (OpCode.Jump, new Target (TargetEnum.Abs, currentSprockell));
        jmp.setLabel(new Label("f_e" + currentSprockell +""));
        program.addInstr(jmp);

        return null;
    } 

    public Object visitJoinstat (JoinstatContext ctx){

        Reg reg = loadFromMemory(true, new Addr(AddrEnum.DirAddr, 0));
        emit (OpCode.Branch, reg, new Target(TargetEnum.Rel, 2));
        emit (OpCode.Jump, new Target(TargetEnum.Rel, -1));

        return null;
    }

    public Addr findVarAddress(boolean shared, String var) {
        if (shared) {
            for (int i = sharedMemory.size() - 1; i >= 0; i--) {
                if (sharedMemory.get(i).containsKey(var)) {
                    return sharedMemory.get(i).get(var);
                }
            }
        } else {
            for (int i = regularMemory.size() - 1; i >= 0; i--) {
                if (regularMemory.get(i).containsKey(var)) {
                    return regularMemory.get(i).get(var);
                }
            }
        }
        return null;
    }

    public void storeVarAddress(boolean shared, String var, Addr addr, boolean newDecl) {
        if (shared) {
            if (!newDecl) {
                for (int i = sharedMemory.size() - 1; i >= 0; i++) {
                    if (sharedMemory.get(i).containsKey(var)) {
                        sharedMemory.get(i).put(var, addr);
                    }
                }
            } else {
                this.sharedMemory.get(this.sharedMemory.size() - 1).put(var, addr);
            }
        } else {
            if (!newDecl) {
                for (int i = regularMemory.size() - 1; i >= 0; i--) {
                    if (regularMemory.get(i).containsKey(var)) {
                        regularMemory.get(i).put(var, addr);
                    }
                }
            } else {
                this.regularMemory.get(this.regularMemory.size() - 1).put(var, addr);
            }
        }

    }

    public boolean memoryContainsVar(Map<Integer, Map<String, Addr>> mem, String var) {
        for (int i = mem.size() - 1; i > -1; i--) {
            if (mem.get(i).containsKey(var)) {
                return true;
            }
        }
        return false;
    }

    public void storeToMemory(boolean shared, Reg reg, Addr addr) {
        if (shared) {
            emit(OpCode.WriteInstr, reg, addr);
        } else {
            emit(OpCode.Store, reg, addr);
        }
    }

    public Reg loadFromMemory(boolean shared, Addr addr) {
        Reg reg = getEmptyReg();
        if (shared) {
            emit(OpCode.ReadInstr, addr);
            emit(OpCode.Receive, reg);
        } else {
            emit(OpCode.Load, addr, reg);
        }
        return reg;
    }

    private Reg getEmptyReg() {
        for (int i = 1; i < 6; i++) {
            if (!usedregs.contains(i)) {
                usedregs.add(i);
                String name = "reg" + Character.toString((char) (64 + i));
                return new Reg(name, i);
            }
        }
        System.out.println("Ran out of regs :(");
        return null;
    }

    private void releaseReg(Reg reg) {
        this.usedregs.remove(reg.getId());
    }

    private void emit(OpCode opCode, Operand... args) {
        Op result = new Op(opCode, args);
        program.addInstr(result);
    }

    public List<Instr> addAddresses (){

        int end = 0;
        boolean forked = false;

        for(int i =0; i<concurrencyInstrs.size(); i++){
            if(concurrencyInstrs.get(i) .getLabel()!= null && concurrencyInstrs.get(i).getLabel().getValue().equals("f_e")){
                end = i;
                forked = true;
            } else if (i == concurrencyInstrs.size() - 1 && ((concurrencyInstrs.get(i) .getLabel()!= null && !(concurrencyInstrs.get(i).getLabel().getValue().equals("f_e"))) || concurrencyInstrs.get(i) .getLabel()== null)){
                end = -1;
                forked = false;
            }
        }

        if(forked){
            for(int i = 0; i<concurrencyInstrs.size(); i++){
                if(concurrencyInstrs.get(i) .getLabel()!= null && concurrencyInstrs.get(i).getLabel().getValue().startsWith("f_e") && concurrencyInstrs.get(i).getLabel().getValue()!= "f_e"){
                    Instr instr = new Op (OpCode.Jump, new Target(TargetEnum.Abs, end));
                    concurrencyInstrs.set(i, instr);
                }
            }
        }

        for(int i = 0; i < concurrencyInstrs.size(); i++){
            if(concurrencyInstrs.get(i).getLabel()!=null && concurrencyInstrs.get(i).getLabel().getValue().startsWith("f_c")){
                Reg reg1 = getEmptyReg();
                Instr newinstr = new Op (OpCode.Load, new Addr(AddrEnum.ImmValue, i + 3), reg1);
                concurrencyInstrs.set(i, newinstr);
                releaseReg(reg1);
            }
        }

        return null;
    }  

    public static void main(String[] args) throws FileNotFoundException {
        String FILE_NAME = "simpleprog";
        System.out.println("Starting elaboration process for file "+ FILE_NAME +".aapfl: type checking, scope checking...");
        LangElab listener = new LangElab();
        System.out.println("\u001B[33mParser errors, if any: \u001B[0m");
        Set<String> errors;
        try {
            errors = listener.process(CharStreams.fromFileName(System.getProperty("user.dir") + "/src/antlr/"+ FILE_NAME +".aapfl"));
            System.out.println(errors.size() > 0 ? "\u001B[33mErrors found (" + errors.size() + ")...\u001B[0m" : "\u001B[32mDone!\u001B[0m");
            for (String error : errors) {
                System.err.println("\u001B[31m" + error + "\u001B[0m");
            }
            if (errors.size() > 0) {
                System.out.println("Terminating compiler!");
                System.exit(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        ParseTree tree = listener.getTree();
        FirstPass fp = new FirstPass();
        fp.process(tree);
        Map<String, List<String>> funcnamestoargs = fp.getFuncnamestoargs(); 
        Map<String, FuncContext> funcnamestodefs = fp.getFuncnamestodefs();

        if (!funcnamestodefs.containsKey("main")) {
            System.out.println("No main method was defined! Exiting.");
            System.exit(0);
        }

        Compiler compiler = new Compiler(funcnamestodefs, funcnamestoargs);
        String compiled = compiler.compile();
        try (PrintStream out = new PrintStream(new FileOutputStream(System.getProperty("user.dir") + "/src/sprockell/src/" + FILE_NAME + ".hs") )) {
            out.print(compiled);
        }
        System.out.println(compiled);

        for(Instr i : concurrencyInstrs){
            System.out.println(i);
        }
    }
}