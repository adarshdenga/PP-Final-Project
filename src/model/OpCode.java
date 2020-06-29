package model;

import static model.Operand.Type.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Code defining the type of a (non-control flow) operation.
 * @author Arend Rensink
 */
public enum OpCode {
	// Register arithmetic
	/** Addition (reg0 + reg1 => reg2). */
	// Add(2, REG, REG, REG),
	// /** Subtraction (reg0 - reg1 => reg2). */
	// Sub(2, REG, REG, REG),
	// /** Multiplication (reg0 * reg1 => reg2). */
	// Mult(2, REG, REG, REG),

	// // Other bitwise operations
	// /** Bitwise OR (reg0 | reg1 => reg2). */
	// Or(2, REG, REG, REG),
	// /** Bitwise AND (reg0 & reg1 => reg2). */
	// And(2, REG, REG, REG),
	// /** Bitwise XOR (reg0 ^ reg1 => reg2). */
	// Xor(2, REG, REG, REG),

	// // Shifts (register + immediate)
	// /** Left-shift (reg0 << reg1 => reg2). */
	// LShift(2, REG, REG, REG),
	// /** Right-shift (reg0 >> reg1 => reg2). */
	// RShift(2, REG, REG, REG),


	// // Comparison operations
	// /** Less-than comparison (reg0 < reg1 => reg2). */
	// Lt(2, REG, REG, REG),
	// /** Less-or-equal comparison (reg0 <= reg1 => reg2). */
	// LtE(2, REG, REG, REG),
	// /** Equals comparison (reg0 == reg1 => reg2). */
	// Equal(2, REG, REG, REG),
	// /** Greater-or-equal comparison (reg0 >= reg1 => reg2). */
	// GtE(2, REG, REG, REG),
	// /** Greater-than comparison (reg0 > reg1 => reg2). */
	// Gt(2, REG, REG, REG),
	// /** Not-equals comparison (reg0 != reg1 => reg2). */
	// NEq(2, REG, REG, REG),

	// /** Increment (reg0 + 1 => reg0) */
	// Incr(1, REG),
	// /** Decrement (reg0 - 1 => reg0) */
	// Decr(1, REG),

	Compute(2, OPERATOR, REG, REG, REG),

	// Jump operations
	/** Register jump (reg0 => pc). */
	Jump(1, TARGET),

	Branch(1, REG, TARGET),
	
	// Memory operations
	/** Load (mem(reg0) => reg1). */
	Load(1, ADDR, REG),
	Store(1, REG, ADDR),

	// Extra ops for stack manipulation
	/** Push the (4-byte integer) value of a register onto the stack. */
	Push(1, REG),
	/** Pop the (4-byte integer) stack top into a register. */
	Pop(0, REG),

	ReadInstr(0, ADDR),
	Receive(0, REG),
	WriteInstr(1, REG, ADDR),
	TestAndSet(0, ADDR),

	EndProg(0),
	Nop(0);

	/** The source operand types. */
	private final List<Operand.Type> sourceSig;

	/** The target operand types. */
	private final List<Operand.Type> targetSig;

	/** The operand types. */
	private final List<Operand.Type> sig;

	private static final Map<String, OpCode> codeMap = new HashMap<>();

	private OpCode(int sourceCount, Operand.Type... sig) {
		this.sourceSig = new ArrayList<>(sourceCount);
		for (int i = 0; i < sourceCount; i++) {
			this.sourceSig.add(sig[i]);
		}
		this.targetSig = new ArrayList<>(sig.length - sourceCount);
		for (int i = sourceCount; i < sig.length; i++) {
			this.targetSig.add(sig[i]);
		}
		this.sig = new ArrayList<>(Arrays.asList(sig));
	}

	/** Returns the number of operands. */
	public int getSigSize() {
		return getSourceCount() + getTargetCount();
	}

	/** Returns the list of expected operand types. */
	public List<Operand.Type> getSig() {
		return this.sig;
	}

	/** Returns the number of source operands. */
	public int getSourceCount() {
		return getSourceSig().size();
	}

	/** Returns the list of expected source operand types. */
	public List<Operand.Type> getSourceSig() {
		return this.sourceSig;
	}

	/** Returns the number of target operands. */
	public int getTargetCount() {
		return getTargetSig().size();
	}

	/** Returns the list of expected target operand types. */
	public List<Operand.Type> getTargetSig() {
		return this.targetSig;
	}

	/** Returns the {@link OpCode} for a given string, if any. */
	public static OpCode parse(String code) {
		return codeMap.get(code);
	}
}
