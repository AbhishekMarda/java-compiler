package javacompiler.riscvtranslator.RiscV.Instructions;

public class JumpAndLink extends Instruction {
    private String label;

    public JumpAndLink(String label) {
        this.label = label;
    }

    public String toString() {
        return "jal " + label;
    }
}
