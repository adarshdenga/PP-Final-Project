package model;

public class Target extends Operand {
    public enum TargetEnum {
        Rel, Abs, Ind
    }
    private TargetEnum target;
    private int val;

    public Target(TargetEnum target, int val) {
        super(Type.TARGET);
        this.target = target;
        this.val = val;
    }

    public int getVal() {
        return val;
    }

    public TargetEnum getTarget() {
        return target;
    }

    @Override
    public String toString() {
        String tempVal = Integer.toString(val);

        if (this.val < 0) {
            tempVal = "$ negate " + (-1 * this.val);
        }
        return "(" + this.target.toString() + " "+ tempVal + ")";
    }
}