package javacompiler.riscvtranslator.RiscV.Instructions;

public class LoadImmediateFunc extends Instruction{
    private Register target;
    private String var;

    public LoadImmediateFunc(Register target, String var) {
        this.target = target;
        this.var = var;
    }

    public String toString() {
        return "li " + target.toString() + ", @" + var;
    }
}
