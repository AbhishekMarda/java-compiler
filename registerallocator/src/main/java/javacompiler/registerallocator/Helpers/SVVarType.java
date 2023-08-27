package javacompiler.registerallocator.Helpers;

public class SVVarType {
    private SVVarType(String name) {
        this.name = name;
    }
    private String name;

    @Override
    public boolean equals(Object obj) {
        SVVarType other = (SVVarType) obj;
        return other.name.equals(this.name);
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static SVVarType CALLEE_SAVED_REGISTER = new SVVarType("callee");
    public static SVVarType CALLER_SAVED_REGISTER = new SVVarType("caller");
    public static SVVarType ARGUMENT_REGISTER = new SVVarType("arg");
    public static SVVarType SPILL_REGISTER = new SVVarType("spill");
    public static SVVarType STACK_REGISTER = new SVVarType("stack");
    public static SVVarType TEMPORARY_REGISTER = new SVVarType("temp");
}
