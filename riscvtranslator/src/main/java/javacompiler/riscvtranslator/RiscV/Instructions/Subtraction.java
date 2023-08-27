package javacompiler.riscvtranslator.RiscV.Instructions;



public class Subtraction extends Instruction {
    private Register target;
    private Register source1;
    private Register source2;

    public Subtraction(Register target, Register source1, Register source2) {
        this.target = target;
        this.source1 = source1;
        this.source2 = source2;
    }

    public String toString() {
        return "sub " + target.toString() + ", " + source1.toString() + ", " + source2.toString();
    }
}
