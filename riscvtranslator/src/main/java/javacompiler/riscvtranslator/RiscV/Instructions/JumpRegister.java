package javacompiler.riscvtranslator.RiscV.Instructions;



public class JumpRegister extends Instruction {
    private Register register;

    public JumpRegister(Register register) {
        this.register = register;
    }

    public String toString() {
        return "jr " + register.toString();
    }
}
