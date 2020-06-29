package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.Operand.Type;

/** ILOC program.
 * @author Arend Rensink
 */
public class Program {
	/** Indexed list of all instructions in the program. */
	private final List<Instr> instrList;
	/**
	 * Indexed list of all operations in the program.
	 * This is the flattened list of instructions.
	 */
	private final List<Op> opList;
	/** Mapping from labels defined in the program to corresponding
	 * index locations.
	 */
	private final Map<Label, Integer> labelMap;

	/** Creates a program with an initially empty instruction list. */
	public Program() {
		this.instrList = new ArrayList<>();
		this.opList = new ArrayList<>();
		this.labelMap = new LinkedHashMap<>();
	}

	/** Adds an instruction to the instruction list of this program.
	 * @throws IllegalArgumentException if the instruction has a known label 
	 */
	public void addInstr(Instr instr) {
		instr.setProgram(this);
		instr.setLine(this.opList.size());
		if (instr.hasLabel()) {
			registerLabel(instr);
		}
		this.instrList.add(instr);
		for (Op op : instr) {
			this.opList.add(op);
		}
	}

	/** Registers the label of a given instruction. */
	void registerLabel(Instr instr) {
		Label label = instr.getLabel();
		Integer loc = this.labelMap.get(label);
		if (loc != null) {
			throw new IllegalArgumentException(String.format(
					"Label %s already occurred at location %d", label, loc));
		}
		this.labelMap.put(label, instr.getLine());
	}

	/** Returns the current list of instructions of this program. */
	public List<Instr> getInstr() {
		return Collections.unmodifiableList(this.instrList);
	}

	/** Returns the flattened list of operations in this program. */
	public List<Op> getOps() {
		return Collections.unmodifiableList(this.opList);
	}
	
	/** Returns the operation at a given line number. */
	public Op getOpAt(int line) {
		return this.opList.get(line);
	}

	/** Returns the size of the program, in number of operations. */
	public int size() {
		return this.instrList.size();
	}

	/**
	 * Returns the location at which a given label is defined, if any.
	 * @return the location of an instruction with the label, or {@code -1}
	 * if the label is undefined
	 */
	public int getLine(Label label) {
		Integer result = this.labelMap.get(label);
		return result == null ? -1 : result;
	}

	/**
	 * Checks for internal consistency, in particular whether
	 * all used labels are defined.
	 */
	// public void check() throws FormatException {
	// 	List<String> messages = new ArrayList<>();
	// 	for (Instr instr : getInstr()) {
	// 		for (Op op : instr) {
	// 			messages.addAll(checkOpnds(op.getLine(), op.getArgs()));
	// 		}
	// 	}
	// 		throw new FormatException(messages);
	// 	}
	// }

	public Instr removeInstruction() {
		return this.instrList.remove(this.instrList.size()-1);
	}

	private List<String> checkOpnds(int loc, List<Operand> opnds) {
		List<String> result = new ArrayList<>();
		for (Operand opnd : opnds) {
			if (opnd instanceof Label) {
				if (getLine((Label) opnd) < 0) {
					result.add(String.format("Line %d: Undefined label '%s'",
							loc, opnd));
				}
			}
		}
		return result;
	}

	/**
	 * Returns a mapping from registers to line numbers
	 * in which they appear.
	 */
	public Map<String, Set<Integer>> getRegLines() {
		Map<String, Set<Integer>> result = new LinkedHashMap<>();
		for (Op op : this.opList) {
			for (Operand opnd : op.getArgs()) {
				if (opnd.getType() == Type.REG) {
					Set<Integer> ops = result.get(((Reg) opnd).getName());
					if (ops == null) {
						result.put(((Reg) opnd).getName(),
								ops = new LinkedHashSet<>());
					}
					ops.add(op.getLine());
				}
			}
		}
		return result;
	}

	/** Returns a line-by-line printout of this program. */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("import Sprockell \n");
		result.append("prog :: [Instruction]\n");
		result.append("prog = [\n");
		result.append("		");
		result.append(getInstr().get(0).toString());
		result.append("\n");
		for (int i = 1; i < getInstr().size(); i++) {
			result.append("        , ");
			result.append(getInstr().get(i).toString());
			result.append("\n");
		}
		result.append("		]\n");
		result.append("main = runWithDebugger (debuggerSimplePrintAndWait myShow) [prog]\n");
		result.append("main_1 = runWithDebugger (debuggerSimplePrint showLocalMem) [prog]\n");
		result.append("showLocalMem :: DbgInput -> String\n");
		result.append("showLocalMem ( _ , systemState ) = show $ localMem $ head $ sprStates systemState");

		return result.toString();
	}

	@Override
	public int hashCode() {
		return this.instrList.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Program)) {
			return false;
		}
		Program other = (Program) obj;
		if (!this.instrList.equals(other.instrList)) {
			return false;
		}
		return true;
	}

	// /** Returns a string consisting of this program in a nice layout.
	//  */
	// public String prettyPrint() {
	// 	StringBuilder result = new StringBuilder();
	// 	// first print the symbolic declaration map
	// 	int idSize = 0;
	// 	if (idSize > 0) {
	// 		result.append('\n');
	// 	}
	// 	// then print the instructions
	// 	int labelSize = 0;
	// 	int sourceSize = 0;
	// 	int targetSize = 0;
	// 	for (Instr i : getInstr()) {
	// 		labelSize = Math.max(labelSize, i.toLabelString().length());
	// 		if (i instanceof Op && ((Op) i).getOpCode() != OpCode.out) {
	// 			Op op = (Op) i;
	// 			sourceSize = Math.max(sourceSize, op.toSourceString().length());
	// 			targetSize = Math.max(targetSize, op.toTargetString().length());
	// 		}
	// 	}
	// 	for (Instr i : getInstr()) {
	// 		result.append(i.prettyPrint(labelSize, sourceSize, targetSize));
	// 	}
	// 	return result.toString();
	// }
}
