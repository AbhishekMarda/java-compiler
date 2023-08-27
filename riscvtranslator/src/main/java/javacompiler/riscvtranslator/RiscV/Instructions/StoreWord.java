package javacompiler.riscvtranslator.RiscV.Instructions;



public class StoreWord extends Instruction {
    private Register source;
    private Register address;
    private int offset;

    public StoreWord(Register source, Register address, int offset) {
        this.source = source;
        this.address = address;
        this.offset = offset;
    }

    public String toString() {
        return "sw " + source.toString() + ", " + Integer.toString(offset) + "(" + address.toString() + ")";
    }
}
