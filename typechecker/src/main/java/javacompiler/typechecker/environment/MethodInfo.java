package javacompiler.typechecker.environment;;

import java.util.ArrayList;

// class to store info about a method in a class
public class MethodInfo {

    public MethodInfo(String methodName, Type ret, ArrayList<VariableInfo> arguments) {
        this.returnType = ret;
        for (VariableInfo argument : arguments) {
            this.args.add(argument);
        }

        this.name = methodName;
    }

    @Override
    public boolean equals(Object other) {
        super.equals(other);
        if (this == other)
            return true;
        if (other == null)
            return false;
        if (this.getClass() != other.getClass())
            return false;

        MethodInfo otherInfo = (MethodInfo) other;
        
        if (otherInfo.args.size() != this.args.size()) {
            return false;
        }

        for (int i=0; i < this.args.size(); i++) {
            if (!otherInfo.args.get(i).typeEquals(this.args.get(i))) {
                return false; 
            }
        }
        return otherInfo.returnType.equals(this.returnType)
            && otherInfo.name.equals(this.name);
    }

    public Type returnType;
    public ArrayList<VariableInfo> args = new ArrayList<>();
    public String name;
}