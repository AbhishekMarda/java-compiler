package javacompiler.riscvtranslator.Helpers;

import java.util.ArrayList;
import java.util.Arrays;

public class SVVar {
    public SVVar(String name) {
        this.isRegister = registerNames.contains(name);
        this.name = name;
    }

    private String name;
    private boolean isRegister;
    private static ArrayList<String> registerNames = new ArrayList<>(
        Arrays.asList(
            "a0", "a1", "a2", "a3", "a4", "a5", "a6", "a7",
            "s1", "s2", "s3", "s4", "s5", "s6", "s7", "s8", "s9", "s10", "s11",
            "t0", "t1", "t2", "t3", "t4", "t5",
            "fp", "sp", "ra"
        )
    );

    // methods
    public String getName() {
        return name;
    }

    public boolean isRegister() {
        return isRegister;
    } 
    
    @Override
    public boolean equals(Object obj) {
        SVVar other = (SVVar) obj;
        return other.name.equals(this.name);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public String toString() {
        return this.name;
    }
}
