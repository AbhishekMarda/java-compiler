package javacompiler.translator.Helpers;

import java.util.ArrayList;
import java.util.HashMap;

import javacompiler.translator.Visitors.ClassInfoVisitor;
import cs132.minijava.syntaxtree.Node;

public class ClassInfo {

    public ClassInfo(String name, HashMap<String, String> childToParent, HashMap<String, ClassInfo> classes, Node goal) {
        this.name = name;        
        // update the list of the member and method names in the current class
        goal.accept(new ClassInfoVisitor(), this);

        // combine overlapping methods and members with the parent
        // note that the parent will have already combined values from the ancestry
        if (childToParent.get(name)!=null) {
            String parent = childToParent.get(name);
            this.combineWithParent(classes.get(parent));
        }

        this.miniJavaType = new MiniJavaType(name);

    }

    public ClassInfo(String name) {
        // this constructor should only be called in the case that it is a primitive type
        if (!MiniJavaType.isPrimitiveType(new MiniJavaType(name))) {
            throw new RuntimeException("Sending invalid type in constructor for ClassInfo: " + name);
        }

        this.name = name;
        this.miniJavaType = new MiniJavaType(name);
    }
    
    // Members
    private String name; 
    private ArrayList<MethodInfo> methods = new ArrayList<>();
    private ArrayList<VariableInfo> members = new ArrayList<>();
    private MiniJavaType miniJavaType;

    // Methods

    public ArrayList<MethodInfo> getMethods() {
        return methods;
    }

    public MiniJavaType getMiniJavaType() {
        return miniJavaType;
    }

    public String getName() {
        return name;
    }

    public MiniJavaType getMemberType(String name) {
        // for (VariableInfo member : this.members) {
        //     if (member.name.equals(name)) {
        //         return member.type;
        //     }
        // }

        for (int i = this.members.size() - 1; i >= 0; i--) {
            if (this.members.get(i).name.equals(name)) {
                return this.members.get(i).type;
            }
        }

        throw new RuntimeException("Could not find member: " + name + "in class : " + this.name);
    }

    public MiniJavaType getMethodReturnType(String name){ 
        for (MethodInfo method : this.methods) {
            if (method.name.equals(name)) {
                return method.returnType;
            }
        }
        throw new RuntimeException("Could not find method: " + name + "in class : " + this.name);
    }

    public void addMethodName(String methodName, MiniJavaType returnType) {
        MethodInfo m = new MethodInfo(methodName, this.name, returnType);
        this.methods.add(m);
    }

    public void addMemberName(String memberName, MiniJavaType type) {
        VariableInfo v = new VariableInfo(memberName, this.name, type);
        this.members.add(v);
    }

    private void combineWithParent(ClassInfo parent) {
        // assumption is that methodNames and memberNames at this point for both objects will be sorted
        ArrayList<VariableInfo> newMembers = new ArrayList<>();
        ArrayList<MethodInfo> newMethods = new ArrayList<>();

        // for (VariableInfo var : parent.members) {
        //     if (this.members.contains(var)) {
        //         this.members.remove(var);
                
        //         // in the same position, but note down overriding
        //         newMembers.add(new VariableInfo(var.name, this.name, var.type)); 
        //     } 
        //     else {
        //         newMembers.add(var);
        //     }
        // }

        // even if same variable names, add all of them
        newMembers.addAll(parent.members);

        for (MethodInfo method: parent.methods) {
            if (this.methods.contains(method)) {
                this.methods.remove(method);

                newMethods.add(new MethodInfo(method.name, this.name, method.returnType));
            }
            else {
                newMethods.add(method);
            }
        }

        newMembers.addAll(this.members);
        this.members = newMembers;
        
        newMethods.addAll(this.methods);
        this.methods = newMethods;

    }

    public int getMemberOffset(String name) {
        // int ans = Constants.DATA_SIZE; // initial 4 bytes left for virtual table pointer
        // for (VariableInfo member : this.members) {
        //     if (member.name.equals(name)) {
        //         return ans; 
        //     }
        //     ans += Constants.DATA_SIZE;
        // }

        int size = this.getFirstLevelSize();
        int ans = size - Constants.DATA_SIZE; // where the last element *starts* from
        
        for (int i = this.members.size() - 1; i >= 0; i--) {
            if (this.members.get(i).name.equals(name)) {
                return ans;
            }   
            ans -= Constants.DATA_SIZE;
        }

        throw new RuntimeException("Name provided: " + name + " does not match any member in class: " + this.name);
    }

    public int getMethodOffset(String name) {
        int ans = 0; // methods start from the beginning
        for (MethodInfo method : this.methods) {
            if (method.name.equals(name)) {
                return ans; 
            }
            ans += Constants.DATA_SIZE;
        }

        throw new RuntimeException("Name provided: " + name + " does not match any method in class: " + this.name);
    }

    public int getFirstLevelSize() {
        return Constants.DATA_SIZE * (this.members.size() + 1); // 1 left for the virtual table pointer
    }

    public int getSecondLevelSize() {
        return Constants.DATA_SIZE * this.methods.size();
    }

    public String getSparrowFunctionName(String funcName) {
        for (MethodInfo method : this.methods) {
            if (method.name.equals(funcName)) {
                return method.assocClass + "_" + funcName;
            }
        }
        throw new RuntimeException("Name provided: " + name + " does not match any method in class: " + this.name);
    }

    @Override
    public String toString(){
        String outputString = "";
        outputString += "Class name: " + this.name + "\n";

        outputString += "\tMembers: \n";

        for (VariableInfo member : this.members) {
            outputString += "\t\t" + member.name + ", Offset: " + this.getMemberOffset(member.name) + ", Class: " + member.assocClass + ", Type:" + member.type.getName() + "\n";
        }

        outputString += "\n\tMethods: \n";
        for (MethodInfo method : this.methods) {
            outputString += "\t\t" + method.name + ", Offset: " + this.getMethodOffset(method.name) + ", Class: " + method.assocClass + "\n";
        }

        outputString += "\nFirst level size: " + this.getFirstLevelSize() + ". Second level size: " + this.getSecondLevelSize() + "\n\n"; 

        return outputString;
    }

}
