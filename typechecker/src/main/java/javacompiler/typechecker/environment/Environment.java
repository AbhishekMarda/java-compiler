package javacompiler.typechecker.environment;;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

/*
 * An environment context requires the following:
 * 1. A map of a child to its parent
 * 2. A list of all class names
 * 3. A map of a class to its methods with the types and members with the types
 * 4. A map that stores the context, storing the members, methods, arguments, and local variables available in the current scope
 */
public class Environment {

    // Set at discovery
    private HashMap<String, ClassInfo> classInfo = new HashMap<>();

    public void addClassInfo(String className, ClassInfo info) {
        if (this.classInfo.containsKey(className)) {
            throw new RuntimeException("Duplicate class name found: " + className);
        }

        this.classInfo.put(className, info);
    }

    public ClassInfo getClassInfo(String className) {
        return this.classInfo.get(className);
    }
    
    public boolean classExists(String className) {
        return this.classInfo.containsKey(className);
    }

    // Inheritance
    // Set at discovery
    private HashMap<String, String> childToParent = new HashMap<>();

    public void addLink(String child, String parent){
        this.childToParent.put(child, parent);
    }
    public String getParent(String child){
        return this.childToParent.get(child);
    }

    public boolean isSubtype(String subType, String superType) {
        String curr = subType; 
        while(curr != null) {
            if (curr.equals(superType)) {
                return true;
            }
            curr = this.getParent(curr);
        }
        return false;
    }

    // given the name of a class, return a list of the fields for that class, including those from its parents
    // accouting for overriding
    public ArrayList<VariableInfo> getFields(String className){
        ClassInfo currInfo = this.classInfo.get(className);
        ArrayList<VariableInfo> currMembers = new ArrayList<>(currInfo.members);

        if (this.getParent(className) != null) {
            ArrayList<VariableInfo> parentMembers = getFields(this.getParent(className));   
            
            // if the variable name exists from a parent, remove it from the parent
            for (VariableInfo member : currMembers) {
                for (int i = 0; i < parentMembers.size(); i++) {
                    VariableInfo v = parentMembers.get(i);
                    if (member.name.equals(v.name)) {
                        parentMembers.remove(i);
                        i--;
                    }
                }
            }
            currMembers.addAll(parentMembers);
        }
        return currMembers;
    }

    // given name and class of a method, return a MethodInfo object specifying information about that method
    public MethodInfo methodType(String methodName, String currClassName) {
        MethodInfo ret = null;
        ClassInfo currClass = null; 
        if (currClassName == null) {
            currClass = this.currentContext.classInfo;
        }
        else {
            currClass = this.classInfo.get(currClassName);
        }

        if (currClass == null)
            throw new RuntimeException("Could not find class: " + currClassName);

        ret = currClass.getMethodByName(methodName);

        if (ret == null && this.childToParent.containsKey(currClass.name)) {
            ret = this.methodType(methodName, this.getParent(currClass.name));
        }

        return ret;
    }

    // context functions
    public Context currentContext = null;

    public Stack<Context> contexts = new Stack<>();
    
    public void saveContext() {
        if (this.currentContext != null ) {
            this.contexts.push(this.currentContext);
        }    
    }

    public void restoreContext() {
        if (this.contexts.empty()) {
            this.currentContext = null;
        } 
        else {
            this.currentContext = this.contexts.pop();
        }
    }

    public void addNewContext(Context c) {
        this.saveContext();
        this.currentContext = c;
    }

    public void setMethodToContext(String methodName) {
        MethodInfo currMethod = this.currentContext.classInfo.getMethodByName(methodName);
        if (currMethod == null) {
            throw new RuntimeException("Method: " + methodName + " does not exist in class " + this.currentContext.classInfo.name);
        }
        this.currentContext.methodInfo = currMethod;
    }

    public void addNewContext(String className)  {
        ClassInfo currClass = this.classInfo.get(className);
        if (currClass != null) {
            this.saveContext();
            Context c = new Context();
            c.classInfo = currClass;
            this.currentContext = c;
        }
        else {
            throw new RuntimeException("Could not find the class: " + className);
        }
    }

    public void addLocalVarToContext(VariableInfo v) {
        String varName = v.name;
        
        // first, go over the params
        for (VariableInfo arg : this.currentContext.methodInfo.args) {
            if (arg.name.equals(varName)) {
                throw new RuntimeException("Attempting to duplicate variable " + v.name + this.currentContext.toString());
            }
        }

        // then go over existing local vars
        for (VariableInfo var : this.currentContext.localVariables) {
            if (var.name.equals(varName)) {
                throw new RuntimeException("Attempting to duplicate variable " + v.name + this.currentContext.toString());
            }
        }
        
        this.currentContext.localVariables.add(v);
    }



    public void runChecks() {
        this.ensureClassesExist();
        this.checkAcyclic();
        this.checkDistinctMethodNames();
        this.checkDistinctMemberNames();
        this.checkExistentMemberTypes();
    }

    private void ensureClassesExist() {
        for (Map.Entry<String, String> entry : this.childToParent.entrySet()) {
            if (!this.classInfo.containsKey(entry.getKey())){
                throw new RuntimeException("Running checks. Could not find class: " + entry.getKey());
            }

            if (!this.classInfo.containsKey(entry.getValue())) {
                throw new RuntimeException("Running checks. Could not find class: " + entry.getValue() );
            }       
        }
    }

    private void checkAcyclic() {
        HashSet<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        for (String node : this.childToParent.keySet()) {
            if (!visited.contains(node)) {
                queue.add(node);
                visited.add(node);
                while (!queue.isEmpty()) {
                    String current = queue.remove();
                    String parent = this.childToParent.get(current);
                    if (parent != null) {
                        if (visited.contains(parent)) {
                            // If the parent is already visited, a cycle exists.
                            throw new RuntimeException("Running checks. Found a cycle in class inheritance. Child: " + current + " Parent: " + parent);
                        } else {
                            queue.add(parent);
                            visited.add(parent);
                        }
                    }
                }
            }
        }
    }
    
    private void checkDistinctMethodNames() {
        for (Map.Entry<String, ClassInfo> entry : this.classInfo.entrySet()) {
            if (entry.getKey().equals("int") || entry.getKey().equals("int[]")  || entry.getKey().equals("boolean") ) {
                continue;
            }
            if (!entry.getValue().hasDistinctMethodNames()) {
                throw new RuntimeException("Class " + entry.getKey() + " does not have distinct method names.");
            }
        }
    }

    private void checkDistinctMemberNames() {
        for (Map.Entry<String, ClassInfo> entry : this.classInfo.entrySet()) {
            if (entry.getKey().equals("int") || entry.getKey().equals("int[]")  || entry.getKey().equals("boolean") ) {
                continue;
            }
            if (!entry.getValue().hasDistinctMemberNames()) {
                throw new RuntimeException("Class " + entry.getKey() + " does not have distinct member names");
            }
        }
    } 

    public boolean noOverloading(String methodName, String className) {
        MethodInfo currMethod = this.methodType(methodName, className);
        if (currMethod == null) {
            return true;
        }

        String parentName = this.getParent(className);
        if (parentName == null) {
            return true; // impossible to overload in this case
        }
        

        MethodInfo parentMethod = this.methodType(methodName, parentName);
        if (parentMethod != null) {
            // if both are the same, then there is no overloading
            return parentMethod.equals(currMethod);
        }

        return true;
    }

    public VariableInfo getIdentifierInfo(String id) {
        // first check the local variables

        for (VariableInfo v : this.currentContext.localVariables) {
            if (v.name.equals(id)) {
                return v;
            }
        }

        // then check mehtod params
        if (this.currentContext.methodInfo != null) {
            for (VariableInfo v : this.currentContext.methodInfo.args) {
                if (v.name.equals(id)) {
                    return v;
                }
            }
        }

        // then finally check class fields up the inheritence tree
        ClassInfo currClass = this.currentContext.classInfo;
        while (currClass != null) {
            VariableInfo var = currClass.getMemberByName(id);
            if (var != null) 
                return var;
            String parentName = this.getParent(currClass.name);
            currClass = this.getClassInfo(parentName);
        }
        
        return null;
    }
    private void checkExistentMemberTypes() {
        for (Map.Entry<String, ClassInfo> entry : this.classInfo.entrySet()) {
            if (entry.getKey().equals("int") || entry.getKey().equals("int[]")  || entry.getKey().equals("boolean") ) {
                continue;
            }
            ClassInfo currClass = entry.getValue();
            for (VariableInfo member : currClass.members) {
                if (! this.classExists(member.type.name)) {
                    throw new RuntimeException("Creating member with type that does not exist. Class: " + currClass.name);
                }
            }
        }
    }

}
