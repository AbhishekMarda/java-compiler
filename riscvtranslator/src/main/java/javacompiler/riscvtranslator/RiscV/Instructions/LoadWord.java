package javacompiler.riscvtranslator.RiscV.Instructions;



public class LoadWord extends Instruction {
    private Register target;
    private Register address;
    private int offset;

    public LoadWord(Register target, Register address, int offset) {
        this.target = target;
        this.address = address;
        this.offset = offset;
    }

    public String toString() {
        return "lw " + target.toString() + ", " + Integer.toString(offset) + "(" + address.toString() + ")";
    }
}
