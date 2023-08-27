package javacompiler.translator.Helpers;

import java.util.ArrayList;
import java.util.HashMap;

import javacompiler.translator.Visitors.InheritanceVisitor;
import javacompiler.translator.Visitors.ListClassesVisitor;
import cs132.minijava.syntaxtree.Node;

public class OffsetCollector {
    public OffsetCollector(Node node) {
        this.goalNode = node;

        // get the class name
        this.classNames = new ListClassesVisitor().getClasses(goalNode);

        // get the inheritance tree
        this.childToParent = new InheritanceVisitor().mapInheritance(goalNode);
        
        // create class information
        HashMap<String, Boolean> visited = new HashMap<>();
        for (String className : this.classNames) {
            visited.put(className, false);
        }

        this.createObjects(visited);
        this.createPrimitiveTypes();

    }

    // Members
    private HashMap<String, String> childToParent = new HashMap<>();
    private HashMap<String, ClassInfo> classInfoMap = new HashMap<>();
    private ArrayList<String> classNames = new ArrayList<>();
    private Node goalNode;

    // Methods

    public ClassInfo getClassInfo(String name) {
        return this.classInfoMap.get(name);
    }
    
    // for each of the classes, create an object. 
    // if one does not exist for the parent, create one for the parent first
    private void createObjects(HashMap<String, Boolean> visited) {
        for (String className : this.classNames) {
            // check if it hasn't already been visited
            if (!visited.get(className)) {
                this.createObject(className, visited);
            }
        }
    }

    private ClassInfo createObject(String name, HashMap<String, Boolean> visited) {
        String parent = this.childToParent.get(name);
        
        // if the parent has not been created yet, create that first
        if (parent != null && this.classInfoMap.get(parent) == null) {
            this.createObject(parent, visited);
        }

        // create the current object 
        ClassInfo curr = new ClassInfo(name, this.childToParent, this.classInfoMap, this.goalNode);

        // mark as visited
        visited.put(name, true);

        // set the value
        this.classInfoMap.put(name, curr);

        return curr;
    }

    private void createPrimitiveTypes() {
        String intString = MiniJavaType.INTEGER.getName();
        String boolString = MiniJavaType.BOOLEAN.getName();
        String intArrString = MiniJavaType.INTEGER_ARRAY.getName();
        
        ClassInfo intClassInfo = new ClassInfo(intString);
        ClassInfo boolClassInfo = new ClassInfo(boolString);
        ClassInfo intArrClassInfo = new ClassInfo(intArrString);

        classInfoMap.put(intString, intClassInfo);
        classInfoMap.put(boolString, boolClassInfo);
        classInfoMap.put(intArrString, intArrClassInfo);
    }

    public void printClasses() {
        for (ClassInfo c : this.classInfoMap.values()) {
            System.out.println(c.toString());
        }
    }

}
