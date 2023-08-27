package javacompiler.riscvtranslator.RiscV.Instructions;

public class Jump extends Instruction {
    private String label;

    public Jump(String label) {
        this.label = label;
    }

    public String toString() {
        return "j " + label;
    }
}
