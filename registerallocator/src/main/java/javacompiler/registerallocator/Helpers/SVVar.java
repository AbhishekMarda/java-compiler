package javacompiler.registerallocator.Helpers;

import java.util.ArrayList;
import java.util.Arrays;

import cs132.IR.token.Identifier;
import cs132.IR.token.Register;

public class SVVar {
    

    public SVVar(String name, SVVarType varType, boolean isRegister) {
        this.name = name;
        this.isRegister = isRegister;
        this.varType = varType;
    }

    private String name;
    private Boolean isRegister;
    private SVVarType varType;

    public Boolean isRegister() {
        return this.isRegister;
    }

    public String getName() {
        return name;
    }

    public SVVarType getVarType() {
        return varType;
    }

    public Identifier toIdentifier() {
        if (isRegister) {
            throw new RuntimeException("Requesting to become identifier when var:" + name + " is a register!");
        }
        return new Identifier(name);
    }

    public Register toRegister() {
        if (!isRegister) {
            throw new RuntimeException("Requesting to become register when var:" + name + " is not a register!");
        }

        return new Register(name);
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        SVVar other = (SVVar) obj;

        return this.name.equals(other.name) && isRegister.equals(other.isRegister) && varType.equals(other.varType);
    }

    // arguments
    public static SVVar a2 = new SVVar("a2", SVVarType.ARGUMENT_REGISTER, true);
    public static SVVar a3 = new SVVar("a3", SVVarType.ARGUMENT_REGISTER, true);
    public static SVVar a4 = new SVVar("a4", SVVarType.ARGUMENT_REGISTER, true);
    public static SVVar a5 = new SVVar("a5", SVVarType.ARGUMENT_REGISTER, true);
    public static SVVar a6 = new SVVar("a6", SVVarType.ARGUMENT_REGISTER, true);
    public static SVVar a7 = new SVVar("a7", SVVarType.ARGUMENT_REGISTER, true);

    // callee saved
    public static SVVar s1 = new SVVar("s1", SVVarType.CALLEE_SAVED_REGISTER, true);
    public static SVVar s2 = new SVVar("s2", SVVarType.CALLEE_SAVED_REGISTER, true);
    public static SVVar s3 = new SVVar("s3", SVVarType.CALLEE_SAVED_REGISTER, true);
    public static SVVar s4 = new SVVar("s4", SVVarType.CALLEE_SAVED_REGISTER, true);
    public static SVVar s5 = new SVVar("s5", SVVarType.CALLEE_SAVED_REGISTER, true);
    public static SVVar s6 = new SVVar("s6", SVVarType.CALLEE_SAVED_REGISTER, true);
    public static SVVar s7 = new SVVar("s7", SVVarType.CALLEE_SAVED_REGISTER, true);
    public static SVVar s8 = new SVVar("s8", SVVarType.CALLEE_SAVED_REGISTER, true);
    public static SVVar s9 = new SVVar("s9", SVVarType.CALLEE_SAVED_REGISTER, true);
    public static SVVar s10 = new SVVar("s10", SVVarType.CALLEE_SAVED_REGISTER, true);
    public static SVVar s11 = new SVVar("s11", SVVarType.CALLEE_SAVED_REGISTER, true);


    // caller saved
    public static SVVar t1 = new SVVar("t1", SVVarType.CALLER_SAVED_REGISTER, true);
    public static SVVar t2 = new SVVar("t2", SVVarType.CALLER_SAVED_REGISTER, true);
    public static SVVar t3 = new SVVar("t3", SVVarType.CALLER_SAVED_REGISTER, true);

    // temporaries
    public static SVVar t4 = new SVVar("t4", SVVarType.TEMPORARY_REGISTER, true);
    public static SVVar t5 = new SVVar("t5", SVVarType.TEMPORARY_REGISTER, true);

    public static ArrayList<SVVar> getArgumentRegisters() {
        return new ArrayList<>(Arrays.asList(
            a2,a3,a4,a5,a6,a7
        ));
    }

    public static ArrayList<SVVar> getCalleeSavedRegisters() {
        return new ArrayList<>(Arrays.asList(
            s1,s2,s3,s4,s5,s6,s7,s8,s9,s10,s11
        ));
    }

    public static ArrayList<SVVar> getCallerSavedRegisters() {
        return new ArrayList<>(Arrays.asList(
            t1,t2,t3
        ));
    }

    public static ArrayList<SVVar> getTemporaryRegisters() {
        return new ArrayList<>(Arrays.asList(
            t4,t5
        ));
    }

}
