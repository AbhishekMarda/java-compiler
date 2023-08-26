package javacompiler.typechecker.environment;;


import java.util.ArrayList;

// this class only handles local variables
// the rest of the functions and variables would be handled by the environment
public class Context {
    public ArrayList<VariableInfo> localVariables = new ArrayList<>();
    public ClassInfo classInfo = null;
    public MethodInfo methodInfo = null;

    @Override
    public String toString() {
        return ", Class name: " + classInfo.name + ", Method name: " + methodInfo.name;
    }
}
