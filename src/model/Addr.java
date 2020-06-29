package model;

public class Addr extends Operand {
    private AddrEnum type;
    private int val;
    private Reg reg;

    public enum AddrEnum {
        ImmValue, DirAddr, IndAddr
    }
    
    public Addr(AddrEnum type, Reg reg) {
        super(Type.ADDR);
        this.type = type;
        this.reg = reg;
    }

    public Addr(AddrEnum type, int val) {
        super(Type.ADDR);
        this.type = type;
        this.val = val;    
    }

    @Override
    public String toString() {
        return "(" + this.type + " " + (this.reg != null ? this.reg.toString() : this.val) + ")";
    }
}