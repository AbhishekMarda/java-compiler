package javacompiler.riscvtranslator.RiscV.Instructions;

public class BranchEqualZero extends Instruction {
    private Register source;
    private String label;

    public BranchEqualZero(Register source, String label) {
        this.source = source;
        this.label = label;
    }

    public String toString() {
        return "beqz " + source.toString() + ", " + label;
    }
}
