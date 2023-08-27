package javacompiler.riscvtranslator.RiscV.Instructions;

public class Register {
    public Register(String name) {
        this.name = name;
    }

    public Register(cs132.IR.token.Register reg) {
        this.name = reg.toString();
    }

    private String name;

    @Override
    public String toString() {
        return this.name;
    }

    // Static objects for RISC-V registers
    public static Register a0 = new Register("a0");
    public static Register a1 = new Register("a1");
    public static Register a2 = new Register("a2");
    public static Register a3 = new Register("a3");
    public static Register a4 = new Register("a4");
    public static Register a5 = new Register("a5");
    public static Register a6 = new Register("a6");
    public static Register a7 = new Register("a7");

    public static Register t1 = new Register("t1");
    public static Register t2 = new Register("t2");
    public static Register t3 = new Register("t3");
    public static Register t4 = new Register("t4");
    public static Register t5 = new Register("t5");
    public static Register t6 = new Register("t6");

    public static Register s1 = new Register("s1");
    public static Register s2 = new Register("s2");
    public static Register s3 = new Register("s3");
    public static Register s4 = new Register("s4");
    public static Register s5 = new Register("s5");
    public static Register s6 = new Register("s6");
    public static Register s7 = new Register("s7");
    public static Register s8 = new Register("s8");
    public static Register s9 = new Register("s9");
    public static Register s10 = new Register("s10");
    public static Register s11 = new Register("s11");

    public static Register sp = new Register("sp");
    public static Register fp = new Register("fp");
    public static Register ra = new Register("ra");
}
