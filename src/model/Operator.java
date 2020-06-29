package model;

public class Operator extends Operand {
    public enum OperatorType {
        Add,
        Sub,
        Mul,
        Equal,
        NEq,
        Gt, 
        Lt, 
        GtE,
        LtE,
        And,
        Or,
        Xor,
        LShift,
        RShift,
        Decr,
        Incr
    }

    private OperatorType type;

    public Operator(OperatorType type) {
        super(Type.OPERATOR);
        this.type = type;
    }

    @Override
    public String toString() {
        return this.type.toString();
    }
}