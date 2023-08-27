package javacompiler.riscvtranslator.RiscV.Instructions;



public class LoadImmediate extends Instruction {
    private Register target;
    private int immediate;

    public LoadImmediate(Register target, int immediate) {
        this.target = target;
        this.immediate = immediate;
    }

    public String toString() {
        return "li " + target.toString() + ", " + Integer.toString(immediate);
    }
}
