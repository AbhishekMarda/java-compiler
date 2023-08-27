package javacompiler.riscvtranslator.RiscV.Instructions;



public class SetLessThan extends Instruction {
    private Register target;
    private Register source1;
    private Register source2;

    public SetLessThan(Register target, Register source1, Register source2) {
        this.target = target;
        this.source1 = source1;
        this.source2 = source2;
    }

    public String toString() {
        return "slt " + target.toString() + ", " + source1.toString() + ", " + source2.toString();
    }
}
