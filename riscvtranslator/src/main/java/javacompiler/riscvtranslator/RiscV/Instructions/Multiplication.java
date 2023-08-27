package javacompiler.riscvtranslator.RiscV.Instructions;



public class Multiplication extends Instruction {
    private Register target;
    private Register source1;
    private Register source2;

    public Multiplication(Register target, Register source1, Register source2) {
        this.target = target;
        this.source1 = source1;
        this.source2 = source2;
    }

    public String toString() {
        return "mul " + target.toString() + ", " + source1.toString() + ", " + source2.toString();
    }
}
