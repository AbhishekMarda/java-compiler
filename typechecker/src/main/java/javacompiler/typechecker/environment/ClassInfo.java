package javacompiler.typechecker.environment;

import java.util.ArrayList;
import java.util.HashSet;

public class ClassInfo {
    public ArrayList<VariableInfo> members = new ArrayList<>(); 
    public ArrayList<MethodInfo> methods = new ArrayList<>();
    public VariableInfo getMemberByName(String name) {
        for (VariableInfo member : this.members) {
            if (member.name.equals(name)) {
                return member;
            }                
        }
        return null;
    }

    public MethodInfo getMethodByName(String name) {
        for (MethodInfo method : this.methods) {
            if (method.name.equals(name)) {
                return method;
            }                
        }
        return null;
    }

    public boolean hasDistinctMethodNames() {
        HashSet<String> found = new HashSet<>();
        for (MethodInfo method : this.methods) {
            if (found.contains(method.name)) {
                return false;
            }
            else {
                found.add(method.name);
            }
        }
        return true;
    }

    public boolean hasDistinctMemberNames() {
        HashSet<String> found = new HashSet<>();
        for (VariableInfo variableInfo : this.members) {
            if (found.contains(variableInfo.name)) {
                return false;
            }
            else {
                found.add(variableInfo.name);
            }
        }
        return true;
    }

    public String name;
}
