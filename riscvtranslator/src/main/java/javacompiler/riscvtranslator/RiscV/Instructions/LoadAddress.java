package javacompiler.riscvtranslator.RiscV.Instructions;



public class LoadAddress extends Instruction {
    private Register target;
    private String label;

    public LoadAddress(Register target, String label) {
        this.target = target;
        this.label = label;
    }

    public String toString() {
        return "la " + target.toString() + ", " + label;
    }
}
