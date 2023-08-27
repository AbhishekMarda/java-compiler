package javacompiler.riscvtranslator.RiscV.Instructions;



public class Move extends Instruction {
    private Register target;
    private Register source;

    public Move(Register target, Register source) {
        this.target = target;
        this.source = source;
    }

    public String toString() {
        return "mv " + target.toString() + ", " + source.toString();
    }
}