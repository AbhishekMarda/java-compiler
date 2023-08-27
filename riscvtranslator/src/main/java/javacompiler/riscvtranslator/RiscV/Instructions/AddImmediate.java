package javacompiler.riscvtranslator.RiscV.Instructions;



public class AddImmediate extends Instruction {
    private Register target;
    private Register source;
    private int immediate;

    public AddImmediate(Register target, Register source, int immediate) {
        this.target = target;
        this.source = source;
        this.immediate = immediate;
    }

    public String toString() {
        return "addi " + target.toString() + ", " + source.toString() + ", " + Integer.toString(immediate);
    }
}
