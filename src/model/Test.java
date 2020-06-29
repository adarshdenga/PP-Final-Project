// package pp.finalproject.antlr;

// import org.antlr.v4.runtime.*;
// import org.antlr.v4.runtime.misc.NotNull;
// import org.antlr.v4.runtime.tree.ParseTree;
// import org.antlr.v4.runtime.tree.ParseTreeProperty;
// import org.antlr.v4.runtime.tree.ParseTreeWalker;
// import pp.finalproject.Compiler.BuildType;
// import pp.finalproject.Util;
// import pp.finalproject.model.*;
// import pp.finalproject.typecheck.TypeChecker;

// import java.io.File;
// import java.io.FileReader;
// import java.io.IOException;
// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.List;

// public class SprockelBuilder extends GrammarBaseListener {
//     private ParseTreeProperty<Operand> operands = new ParseTreeProperty<>();
//     private ParseTreeProperty<String> variables = new ParseTreeProperty<>();
//     private ParseTreeProperty<Reg> resultRegisters = new ParseTreeProperty<>();

//     //Saves a list of of objects to be assigned to an array (integer[] test = {1, 2, 3})
//     private ParseTreeProperty<List<Object>> arrayAssign = new ParseTreeProperty<>();

//     //Save current number of program lines at a node, needed to loop back in a while
//     private ParseTreeProperty<Integer> programLines = new ParseTreeProperty<>();

//     //Saves the location of branch & jump instructions needed for if statements. The values for this need to be filled in after the if statement
//     private ParseTreeProperty<Op> operators = new ParseTreeProperty<>();

//     //Hashmaps that hold the locations of variables in memory
//     private HashMap<String, MemAddr> sharedMemory = new HashMap<>();
//     private HashMap<String, MemAddr> memory = new HashMap<>();

//     private Program program = new Program();

//     //List that holds the number of registers in use
//     private List<Integer> registersInUse = new ArrayList<>();

//     /**
//      * Compiles a sprockell program from input code and saves it as a haskell file.
//      *
//      * @param file Input source code
//      */
//     public File build(File file, BuildType buildType, int instances) {
//         try {
//             CharStream chars = new ANTLRInputStream(new FileReader(file));
//             Lexer lexer = new GrammarLexer(chars);
//             lexer.removeErrorListeners();

//             TokenStream tokens = new CommonTokenStream(lexer);

//             GrammarParser parser = new GrammarParser(tokens);
//             ParseTree tree = parser.program();

//             if (parser.getNumberOfSyntaxErrors() != 0)
//                 return null;

//             TypeChecker typeChecker = new TypeChecker();
//             typeChecker.check(tree);
//             if (!typeChecker.getErrors().isEmpty()) {
//                 System.out.println("Program contains errors:");
//                 typeChecker.getErrors().forEach(System.out::println);
//                 return null;
//             }

//             ParseTreeWalker walker = new ParseTreeWalker();
//             walker.walk(this, tree);
//         } catch (IOException e) {
//             e.printStackTrace();
//         }

//         //Add debug output
//         if (buildType.equals(BuildType.DEBUG)) {
//             System.out.println("Created program:");
//             System.out.println(program.toString());
//             Util.addDebugOutput(program, memory);
//         } else {
//             program.addOp(new Op(OpCode.NOP));
//             program.addOp(new Op(OpCode.ENDPROG));
//         }
//         return Util.saveProgram(program, file.getName(), instances, buildType);
//     }

//     @Override
//     public void exitProgram(@NotNull GrammarParser.ProgramContext ctx) {
//         //Enable following line if debug output is not used
//         //emit(OpCode.ENDPROG);
//         super.exitProgram(ctx);
//     }

//     @Override
//     public void exitDeclStat(@NotNull GrammarParser.DeclStatContext ctx) {
//         Reg reg = null;
//         GrammarParser.TypeContext type = ctx.target().type();

//         if (type != null) {
//             Operand operand = null;
//             if (type.INT() != null) {
//                 operand = new Num();
//             } else if (type.BOOL() != null) {
//                 operand = new Bool();
//             }
//             reg = getEmptyRegister();

//             saveToReg(operand, reg);

//             saveToMemory(ctx.ID().getText(), reg, ctx.SHARED() != null);
//         } else {
//             GrammarParser.TypeContext arrayType = ctx.target().arraytype().type();
//             GrammarParser.ExprContext expr = ctx.target().arraytype().expr();

//             int arraySize = 16;
//             if (ctx.target().arraytype().expr() != null) {
//                 if (operands.get(expr) != null) {
//                     arraySize = ((Num) operands.get(expr)).getValue();
//                 }
//             }

//             for (int i = 0; i < arraySize; i++) {
//                 Reg tempReg = getEmptyRegister();
//                 if (arrayType.BOOL() != null) {
//                     saveToReg(new Bool(), tempReg);
//                 } else {
//                     saveToReg(new Num(), tempReg);
//                 }
//                 saveToMemory(ctx.ID().getText() + i, tempReg, ctx.SHARED() != null);
//             }
//         }

//         super.exitDeclStat(ctx);
//     }

//     @Override
//     public void exitDeclAssignStat(@NotNull GrammarParser.DeclAssignStatContext ctx) {
//         //When assigning an array
//         if (arrayAssign.get(ctx.expr()) != null) {
//             List<Object> arrayContents = arrayAssign.get(ctx.expr());
//             if (ctx.expr() instanceof GrammarParser.ArrayAssignExprContext) {
//                 int i = 0;
//                 for (Object object : arrayContents) {
//                     if (object instanceof Reg) {
//                         saveToMemory(ctx.ID().getText() + i, (Reg) object, ctx.SHARED() != null);
//                     } else if (object instanceof Operand) {
//                         Reg reg = getEmptyRegister();
//                         saveToReg((Operand) object, reg);
//                         saveToMemory(ctx.ID().getText() + i, reg, ctx.SHARED() != null);
//                     } else if (object instanceof String) {
//                         Reg reg = loadFromMemory((String) object, ctx.SHARED() != null);
//                         saveToMemory(ctx.ID().getText() + i, reg, ctx.SHARED() != null);
//                     }
//                     i++;
//                 }
//             }
//             return;
//         }

//         Reg reg = null;
//         if (operands.get(ctx.expr()) != null) {
//             reg = getEmptyRegister();
//             saveToReg(operands.get(ctx.expr()), reg);
//         } else if (resultRegisters.get(ctx.expr()) != null) {
//             reg = resultRegisters.get(ctx.expr());
//         } else if (variables.get(ctx.expr()) != null) {
//             reg = loadFromMemory(variables.get(ctx.expr()), ctx.SHARED() != null);
//         }


//         saveToMemory(ctx.ID().getText(), reg, ctx.SHARED() != null);
//         super.exitDeclAssignStat(ctx);
//     }

//     @Override
//     public void exitArrayAssignExpr(@NotNull GrammarParser.ArrayAssignExprContext ctx) {
//         List<Object> objects = new ArrayList<>();
//         for (int i = 0; i < ctx.expr().size(); i++) {
//             if (operands.get(ctx.expr(i)) != null) {
//                 objects.add(operands.get(ctx.expr(i)));
//             } else if (resultRegisters.get(ctx.expr(i)) != null) {
//                 objects.add(resultRegisters.get(ctx.expr(i)));
//             } else if (variables.get(ctx.expr(i)) != null) {
//                 objects.add(variables.get(ctx.expr(i)));
//             }
//         }
//         arrayAssign.put(ctx, objects);
//         super.exitArrayAssignExpr(ctx);
//     }

//     @Override
//     public void exitAssignStat(@NotNull GrammarParser.AssignStatContext ctx) {
//         boolean shared = sharedMemory.containsKey(ctx.ID().getText());
//         Reg reg = null;
//         if (operands.get(ctx.expr()) != null) {
//             reg = getEmptyRegister();
//             saveToReg(operands.get(ctx.expr()), reg);
//         } else if (resultRegisters.get(ctx.expr()) != null) {
//             reg = resultRegisters.get(ctx.expr());
//         } else if (variables.get(ctx.expr()) != null) {
//             reg = loadFromMemory(variables.get(ctx.expr()), shared);
//         }
//         saveToMemory(ctx.ID().getText(), reg, shared);
//         super.exitAssignStat(ctx);
//     }

//     @Override
//     public void exitArrayAssignStat(@NotNull GrammarParser.ArrayAssignStatContext ctx) {
//         String id = ctx.ID().getText() + "0";
//         boolean shared = sharedMemory.containsKey(id);

//         //Load proper array value from memory
//         Reg startAddressReg = getEmptyRegister();
//         if (shared) {
//             MemAddr startAddress = sharedMemory.get(id);
//             saveToReg(new Num(startAddress.getAddress()), startAddressReg);
//         } else {
//             MemAddr startAddress = memory.get(id);
//             saveToReg(new Num(startAddress.getAddress()), startAddressReg);
//         }

//         Reg arrayIndex = null;
//         if (operands.get(ctx.expr(0)) != null) {
//             arrayIndex = getEmptyRegister();
//             saveToReg(operands.get(ctx.expr(0)), arrayIndex);
//         } else if (resultRegisters.get(ctx.expr(0)) != null) {
//             arrayIndex = resultRegisters.get(ctx.expr(0));
//         } else if (variables.get(ctx.expr(0)) != null) {
//             arrayIndex = loadFromMemory(variables.get(ctx.expr(0)), sharedMemory.containsKey(variables.get(ctx.expr(0))));
//         }

//         emit(OpCode.COMPUTE, new Operator(Operator.OperatorType.ADD), startAddressReg, arrayIndex, arrayIndex);
//         releaseReg(startAddressReg);

//         Reg reg = null;
//         if (operands.get(ctx.expr(1)) != null) {
//             reg = getEmptyRegister();
//             saveToReg(operands.get(ctx.expr(1)), reg);
//         } else if (resultRegisters.get(ctx.expr(1)) != null) {
//             reg = resultRegisters.get(ctx.expr(1));
//         } else if (variables.get(ctx.expr(1)) != null) {
//             reg = loadFromMemory(variables.get(ctx.expr(1)), shared);
//         }

//         saveArraySlotToMemory(arrayIndex, reg, shared);
//         releaseReg(reg);
//         releaseReg(arrayIndex);

//         super.exitArrayAssignStat(ctx);
//     }

//     @Override
//     public void exitTarget(@NotNull GrammarParser.TargetContext ctx) {
//         super.exitTarget(ctx);
//     }

//     @Override
//     public void exitArraytype(@NotNull GrammarParser.ArraytypeContext ctx) {
//         super.exitArraytype(ctx);
//     }

//     @Override
//     public void exitParExpr(@NotNull GrammarParser.ParExprContext ctx) {
//         if (operands.get(ctx.expr()) != null) {
//             operands.put(ctx, operands.get(ctx.expr()));
//         } else if (variables.get(ctx.expr()) != null) {
//             variables.put(ctx, variables.get(ctx.expr()));
//         } else if (resultRegisters.get(ctx.expr()) != null) {
//             resultRegisters.put(ctx, resultRegisters.get(ctx.expr()));
//         }
//         super.exitParExpr(ctx);
//     }

//     @Override
//     public void exitArrayExpr(@NotNull GrammarParser.ArrayExprContext ctx) {
//         String id = ctx.ID().getText() + "0";
//         boolean shared = sharedMemory.containsKey(id);

//         //Reg startAddress = loadFromMemory(id, shared);
//         Reg startAddressReg = getEmptyRegister();
//         if (shared) {
//             MemAddr startAddress = sharedMemory.get(id);
//             saveToReg(new Num(startAddress.getAddress()), startAddressReg);
//         } else {
//             MemAddr startAddress = memory.get(id);
//             saveToReg(new Num(startAddress.getAddress()), startAddressReg);
//         }

//         Reg arrayIndex = null;
//         if (operands.get(ctx.expr()) != null) {
//             arrayIndex = getEmptyRegister();
//             saveToReg(operands.get(ctx.expr()), arrayIndex);
//         } else if (resultRegisters.get(ctx.expr()) != null) {
//             arrayIndex = resultRegisters.get(ctx.expr());
//         } else if (variables.get(ctx.expr()) != null) {
//             arrayIndex = loadFromMemory(variables.get(ctx.expr()), shared);
//         }

//         emit(OpCode.COMPUTE, new Operator(Operator.OperatorType.ADD), startAddressReg, arrayIndex, arrayIndex);
//         releaseReg(startAddressReg);
//         //Reg outputReg = loadFromMemory(id, shared);
//         Reg outputReg = loadArraySlotFromMemory(arrayIndex, shared);
//         releaseReg(arrayIndex);
//         resultRegisters.put(ctx, outputReg);

//         //emit(OpCode.CONST, )
//         super.exitArrayExpr(ctx);
//     }

//     /* If statement */

//     @Override
//     public void exitIfcompare(@NotNull GrammarParser.IfcompareContext ctx) {
//         Reg reg = null;
//         if (operands.get(ctx.expr()) != null) {
//             reg = getEmptyRegister();
//             saveToReg(operands.get(ctx.expr()), reg);
//         } else if (resultRegisters.get(ctx.expr()) != null) {
//             reg = resultRegisters.get(ctx.expr());
//         } else if (variables.get(ctx.expr()) != null) {
//             boolean shared = sharedMemory.containsKey(variables.get(ctx.expr()));
//             reg = loadFromMemory(variables.get(ctx.expr()), shared);
//         }
//         emit(OpCode.BRANCH, reg, new Target(Target.TargetType.REL, 2));
//         releaseReg(reg);
//         super.exitIfcompare(ctx);
//     }

//     @Override
//     public void enterIfbody(@NotNull GrammarParser.IfbodyContext ctx) {
//         Op jump = emit(OpCode.JUMP);

//         programLines.put(ctx, program.opCount());
//         operators.put(ctx, jump);
//         super.enterIfbody(ctx);
//     }

//     @Override
//     public void exitIfStat(@NotNull GrammarParser.IfStatContext ctx) {
//         Op jump = operators.get(ctx.ifbody());
//         int opLines = programLines.get(ctx.ifbody());

//         int jumpLines = program.opCount() - opLines;
//         jump.setArgs(new Target(Target.TargetType.REL, jumpLines + 1));
//         super.exitIfStat(ctx);
//     }

//     /* While statement */

//     @Override
//     public void exitWhilecompare(@NotNull GrammarParser.WhilecompareContext ctx) {
//         Reg reg = null;
//         if (operands.get(ctx.expr()) != null) {
//             reg = getEmptyRegister();
//             saveToReg(operands.get(ctx.expr()), reg);
//         } else if (resultRegisters.get(ctx.expr()) != null) {
//             reg = resultRegisters.get(ctx.expr());
//         } else if (variables.get(ctx.expr()) != null) {
//             boolean shared = sharedMemory.containsKey(variables.get(ctx.expr()));
//             reg = loadFromMemory(variables.get(ctx.expr()), shared);
//         }
//         emit(OpCode.BRANCH, reg, new Target(Target.TargetType.REL, 2));
//         releaseReg(reg);
//         super.exitWhilecompare(ctx);
//     }

//     @Override
//     public void enterWhilebody(@NotNull GrammarParser.WhilebodyContext ctx) {
//         Op jump = emit(OpCode.JUMP);

//         programLines.put(ctx, program.opCount());
//         operators.put(ctx, jump);
//         super.enterWhilebody(ctx);
//     }

//     @Override
//     public void enterWhileStat(@NotNull GrammarParser.WhileStatContext ctx) {
//         programLines.put(ctx, program.opCount());
//         super.enterWhileStat(ctx);
//     }

//     @Override
//     public void exitWhileStat(@NotNull GrammarParser.WhileStatContext ctx) {
//         Op jump = operators.get(ctx.whilebody());
//         int opLines = programLines.get(ctx.whilebody());

//         //Jump to skip while if compare was false
//         int jumpLines = program.opCount() - opLines;
//         jump.setArgs(new Target(Target.TargetType.REL, jumpLines + 2));

//         //Jump to loop back to compare
//         int loopLines = program.opCount() - programLines.get(ctx);
//         emit(OpCode.JUMP, new Target(Target.TargetType.REL, -(loopLines)));
//         super.exitWhileStat(ctx);
//     }

//     /* Concurreny */
//     @Override
//     public void enterSynchronizedbody(@NotNull GrammarParser.SynchronizedbodyContext ctx) {
//         //Attempt to get lock
//         emit(OpCode.TESTANDSET, new MemAddr(0));
//         Reg receiveReg = getEmptyRegister();
//         emit(OpCode.RECEIVE, receiveReg);
//         emit(OpCode.BRANCH, receiveReg, new Target(Target.TargetType.REL, 2));
//         emit(OpCode.JUMP, new Target(Target.TargetType.REL, -3));
//         releaseReg(receiveReg);
//         super.enterSynchronizedbody(ctx);
//     }

//     @Override
//     public void exitSynchronizedbody(@NotNull GrammarParser.SynchronizedbodyContext ctx) {
//         //Release lock
//         emit(OpCode.WRITE, new Reg("Zero"), new MemAddr(0));
//         super.exitSynchronizedbody(ctx);
//     }

//     /* Expressions */
//     @Override
//     public void exitTimesDivideExpr(@NotNull GrammarParser.TimesDivideExprContext ctx) {
//         Reg reg1 = loadToReg(ctx.expr(0));
//         Reg reg2 = loadToReg(ctx.expr(1));
//         if (ctx.TIMES() != null) {
//             emit(OpCode.COMPUTE, new Operator(Operator.OperatorType.MUL), reg1, reg2, reg1);
//         } else if (ctx.DIVIDE() != null) {
//             emit(OpCode.COMPUTE, new Operator(Operator.OperatorType.DIV), reg1, reg2, reg1);
//         }
//         resultRegisters.put(ctx, reg1);
//         releaseReg(reg2);
//         super.exitTimesDivideExpr(ctx);
//     }

//     @Override
//     public void exitPlusMinusExpr(@NotNull GrammarParser.PlusMinusExprContext ctx) {
//         Reg reg1 = loadToReg(ctx.expr(0));
//         Reg reg2 = loadToReg(ctx.expr(1));

//         if (ctx.PLUS() != null) {
//             emit(OpCode.COMPUTE, new Operator(Operator.OperatorType.ADD), reg1, reg2, reg1);
//         } else if (ctx.MINUS() != null) {
//             emit(OpCode.COMPUTE, new Operator(Operator.OperatorType.SUB), reg1, reg2, reg1);
//         }
//         resultRegisters.put(ctx, reg1);
//         releaseReg(reg2);
//         super.exitPlusMinusExpr(ctx);
//     }

//     @Override
//     public void exitOrExpr(@NotNull GrammarParser.OrExprContext ctx) {
//         Reg reg1 = loadToReg(ctx.expr(0));
//         Reg reg2 = loadToReg(ctx.expr(1));
//         emit(OpCode.COMPUTE, new Operator(Operator.OperatorType.OR), reg1, reg2, reg1);
//         resultRegisters.put(ctx, reg1);
//         releaseReg(reg2);
//         super.exitOrExpr(ctx);
//     }

//     @Override
//     public void exitMinusExpr(@NotNull GrammarParser.MinusExprContext ctx) {
//         Reg reg1 = loadToReg(ctx.expr());
//         emit(OpCode.COMPUTE, new Operator(Operator.OperatorType.SUB), new Reg("Zero"), reg1, reg1);
//         resultRegisters.put(ctx, reg1);
//         super.exitMinusExpr(ctx);
//     }

//     @Override
//     public void exitConstExpr(@NotNull GrammarParser.ConstExprContext ctx) {
//         if (ctx.NUM() != null) {
//             operands.put(ctx, new Num(Integer.parseInt(ctx.NUM().getText())));
//         } else if (ctx.TRUE() != null) {
//             operands.put(ctx, new Bool(true));
//         } else if (ctx.FALSE() != null) {
//             operands.put(ctx, new Bool(false));
//         }
//         super.exitConstExpr(ctx);
//     }

//     @Override
//     public void exitSpidExpr(@NotNull GrammarParser.SpidExprContext ctx) {
//         resultRegisters.put(ctx, new Reg("SPID"));
//         super.exitSpidExpr(ctx);
//     }

//     @Override
//     public void exitIdExpr(@NotNull GrammarParser.IdExprContext ctx) {
//         variables.put(ctx, ctx.getText());
//         //registers.put(ctx.getText(), getEmptyRegister());
//         super.exitIdExpr(ctx);
//     }

//     @Override
//     public void exitCmpExpr(@NotNull GrammarParser.CmpExprContext ctx) {
//         //LT | GT | LTE| | GTE | EQUAL | NOTEQUAL
//         Reg reg1 = loadToReg(ctx.expr(0));
//         Reg reg2 = loadToReg(ctx.expr(1));
//         if (ctx.LT() != null) {
//             emit(OpCode.COMPUTE, new Operator(Operator.OperatorType.LT), reg1, reg2, reg1);
//         } else if (ctx.GT() != null) {
//             emit(OpCode.COMPUTE, new Operator(Operator.OperatorType.GT), reg1, reg2, reg1);
//         } else if (ctx.LTE() != null) {
//             emit(OpCode.COMPUTE, new Operator(Operator.OperatorType.LTE), reg1, reg2, reg1);
//         } else if (ctx.GTE() != null) {
//             emit(OpCode.COMPUTE, new Operator(Operator.OperatorType.GTE), reg1, reg2, reg1);
//         } else if (ctx.EQUAL() != null) {
//             emit(OpCode.COMPUTE, new Operator(Operator.OperatorType.EQUAL), reg1, reg2, reg1);
//         }
//         //Reg1 is still used since it contains the return value. Reg2 can already be released
//         releaseReg(reg2);
//         //else if (ctx.NOTEQUAL() != null) {
//         //    emit(OpCode.COMPUTE, new Operator(Operator.OperatorType.NOTEQUAL), reg1, reg2, reg1);
//         //}

//         //Store register that computed value is saved to
//         resultRegisters.put(ctx, reg1);
//         super.exitCmpExpr(ctx);
//     }

//     @Override
//     public void exitAndExpr(@NotNull GrammarParser.AndExprContext ctx) {
//         Reg reg1 = loadToReg(ctx.expr(0));
//         Reg reg2 = loadToReg(ctx.expr(1));
//         emit(OpCode.COMPUTE, new Operator(Operator.OperatorType.AND), reg1, reg2, reg1);
//         resultRegisters.put(ctx, reg1);
//         releaseReg(reg2);
//         super.exitAndExpr(ctx);
//     }

//     @Override
//     public void exitType(@NotNull GrammarParser.TypeContext ctx) {
//         super.exitType(ctx);
//     }

//     @Override
//     public void exitOutStat(@NotNull GrammarParser.OutStatContext ctx) {
//         Reg reg = loadToReg(ctx.expr());
//         emit(OpCode.WRITE, reg, new MemAddr(-2));
//         releaseReg(reg);
//         super.exitOutStat(ctx);
//     }

//     /**
//      * Loads a value from given ExprContext into a register.
//      * This value can be obtained from several places via the symbol table.
//      *
//      * @param expr context to load from
//      * @return register with value from expression
//      */
//     private Reg loadToReg(GrammarParser.ExprContext expr) {
//         if (resultRegisters.get(expr) != null) {
//             return resultRegisters.get(expr);
//         }
//         //If it is a named variable, load value from memory
//         if (expr instanceof GrammarParser.IdExprContext) {
//             GrammarParser.IdExprContext ctx = (GrammarParser.IdExprContext) expr;
//             Reg reg = loadFromMemory(ctx.ID().getText(), sharedMemory.containsKey(((GrammarParser.IdExprContext) expr).ID().getText()));
//             return reg;
//         } else if (operands.get(expr) != null) {
//             //Create a register to return value in
//             Reg reg = getEmptyRegister();
//             saveToReg(operands.get(expr), reg);
//             return reg;
//         } else {
//             //Create a register to return value in
//             Reg reg = getEmptyRegister();
//             emit(OpCode.POP, reg);
//             return reg;
//         }
//     }

//     /**
//      * Saves the operand to a register
//      *
//      * @param operand Operand to save
//      * @param reg     Register to save to
//      */
//     private void saveToReg(Operand operand, Reg reg) {
//         emit(OpCode.CONST, operand, reg);
//     }

//     /**
//      * Adds the variable name and memory address to the context tree
//      * Saves the register to an empty memory address
//      *
//      * @param name Variable name
//      * @param reg  Register to save
//      */
//     private void saveToMemory(String name, Reg reg, boolean shared) {
//         if (shared) {
//             MemAddr addr;
//             if (sharedMemory.containsKey(name)) {
//                 addr = sharedMemory.get(name);
//                 emit(OpCode.WRITE, reg, addr);
//             } else {
//                 addr = new MemAddr(sharedMemory.size() + 1);
//                 sharedMemory.put(name, addr);
//                 //Branch to declare if it is sprockel id 0
//                 //Wait until until it is defined otherwise
//                 emit(OpCode.BRANCH, new Reg("SPID"), new Target(Target.TargetType.REL, 3));
//                 emit(OpCode.WRITE, reg, addr);
//                 emit(OpCode.JUMP, new Target(Target.TargetType.REL, 3));
//                 emit(OpCode.READ, addr);
//                 emit(OpCode.RECEIVE, reg);
//             }
//         } else {
//             MemAddr addr;
//             if (memory.containsKey(name)) {
//                 //Overwrite memory location, variable stays in memory so no need to add it again
//                 addr = memory.get(name);
//             } else {
//                 addr = new MemAddr(memory.size());
//                 memory.put(name, addr);
//             }
//             emit(OpCode.STORE, reg, addr);
//         }
//         releaseReg(reg);
//     }

//     /**
//      * Replaces the value of an array slot.
//      * This action is not thread-safe, as it is assumed that expression is synchronized by code block
//      *
//      * @param address Register containing the memory address to save to
//      * @param value   Value to save
//      * @param shared  Indicator wheter to save to shared or local memory
//      */
//     private void saveArraySlotToMemory(Reg address, Reg value, boolean shared) {
//         MemAddr addr = new MemAddr(address);
//         if (shared) {
//             emit(OpCode.WRITE, value, addr);
//         } else {
//             emit(OpCode.STORE, value, addr);
//         }
//         releaseReg(value);
//         releaseReg(address);
//     }

//     /**
//      * Loads a variable from memory.
//      *
//      * @param name   Variable name
//      * @param shared Load from shared memory or not
//      * @return Reg with variable value
//      */
//     private Reg loadFromMemory(String name, boolean shared) {
//         if (shared) {
//             MemAddr addr = sharedMemory.get(name);
//             Reg reg = getEmptyRegister();
//             emit(OpCode.READ, addr);
//             emit(OpCode.RECEIVE, reg);
//             return reg;
//         } else {
//             MemAddr addr = memory.get(name);
//             Reg reg = getEmptyRegister();
//             emit(OpCode.LOAD, addr, reg);
//             return reg;
//         }
//     }

//     /**
//      * Loads a value from an arrayslot
//      *
//      * @param address Register containing the values location
//      * @param shared  Indicator wheter to use shared or local memory
//      * @return returns the value encapsulated in this register
//      */
//     private Reg loadArraySlotFromMemory(Reg address, boolean shared) {
//         MemAddr addr = new MemAddr(address);
//         if (shared) {
//             Reg reg = getEmptyRegister();
//             emit(OpCode.READ, addr);
//             emit(OpCode.RECEIVE, reg);
//             return reg;
//         } else {
//             Reg reg = getEmptyRegister();
//             emit(OpCode.LOAD, addr, reg);
//             return reg;
//         }
//     }

//     /**
//      * Creates a not yet in use register.
//      * Only 2 registers should ever be needed, so as long as registers are released properly this should always return a valid register
//      *
//      * @return Empty register
//      */
//     private Reg getEmptyRegister() {
//         for (int i = 1; i <= 5; i++) {
//             if (!registersInUse.contains(i)) {
//                 registersInUse.add(i);
//                 String register = "Reg" + Character.toString((char) (64 + i));
//                 return new Reg(register, i);
//             }
//         }

//         System.out.println("Warning: ran out of registers!");
//         return new Reg("Invalidreg" + registersInUse.size());
//     }

//     /**
//      * Releases a register so it can be used again.
//      *
//      * @param reg Register to release
//      */
//     private void releaseReg(Reg reg) {
//         registersInUse.remove((Integer) reg.getId());
//     }

//     /**
//      * Constructs an operation from the parameters
//      * and adds it to the program under construction.
//      */
//     private Op emit(OpCode opCode, Operand... args) {
//         Op result = new Op(opCode, args);
//         program.addOp(result);
//         return result;
//     }
// }