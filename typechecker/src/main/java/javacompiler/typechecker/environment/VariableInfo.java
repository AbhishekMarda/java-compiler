package javacompiler.typechecker.environment;

// class to store info about a member in a class
public class VariableInfo {
    public VariableInfo(String memberName, Type type){ 
        this.name = memberName;
        this.type = type;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof VariableInfo)) {
            return false;
        }
        VariableInfo other = (VariableInfo) obj;
        return this.name.equals(other.name) &&
            this.type.equals(other.type);
    }

    public boolean typeEquals(VariableInfo other) {
        return this.type.equals(other.type);
    }

    public String name;
    public Type type;
}