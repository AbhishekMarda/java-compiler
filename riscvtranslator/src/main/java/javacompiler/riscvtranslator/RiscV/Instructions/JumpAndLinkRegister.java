package javacompiler.riscvtranslator.RiscV.Instructions;

public class JumpAndLinkRegister extends Instruction {
    private Register target;

    public JumpAndLinkRegister(Register target) {
        this.target = target;
    }

    public String toString() {
        return "jalr " + target.toString();
    }
}
