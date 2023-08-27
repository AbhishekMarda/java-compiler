package javacompiler.translator.Helpers;

import java.util.HashMap;

/*
 * Keep track of 
 *  1. Current class
 *  2. Current method (?)
 *  3. Variable -> ClassInfo type
 *  4. MiniJava variable -> Sparrow variable
 *  5. Sparrow variable -> Sparrow type (might be unnecessary)
 */
public class Scope {
    public Scope(ClassInfo currClass) {
        this.currClass = currClass;
    }

    // Members
    private ClassInfo currClass;
    private HashMap<String, ClassInfo> miniJavaVarToClassInfo = new HashMap<>();
    private HashMap<String, String> miniJavaVarToSparrowVar = new HashMap<>();
    private HashMap<String, SparrowType> sparrowVarToSparrowType = new HashMap<>();
    private HashMap<String, MiniJavaType> sparrowVarToMiniJavaType = new HashMap<>();

    // Methods
    public ClassInfo getCurrClass() {
        return this.currClass;
    }   

    public SparrowType getSparrowVarSparrowType(String sparrowVar) {
        return this.sparrowVarToSparrowType.get(sparrowVar);
    }

    // Generate a link between the MiniJava variable name and its type. 
    // Create a sparrow variable name and link that to the minijava variable name
    // Detect the sparrow variable type from the minijava variable type and store that information
    // finally, link the sparrow variable to the minijava type
    public String addVar(String miniJavaName, MiniJavaType miniJavaType, OffsetCollector offsetCollector) {
        
        String sparrowName = Gensym.gensym(SparrowIdentifierType.VARTYPE);
        
        return this.addVar(miniJavaName, miniJavaType, sparrowName, offsetCollector);
    }

    // Generate a link between the MiniJava variable name and its type
    // Get the sparrow variable name and link that to the minijava variable name
    // Detect the sparrow variable type from the minijava variable type and store that information
    // finally, link the sparrow variable to the minijava type 
    public String addVar(String miniJavaName, MiniJavaType miniJavaType, String sparrowName, OffsetCollector offsetCollector) {
        
        // get the class info for the variable
        ClassInfo miniJavaClass = offsetCollector.getClassInfo(miniJavaType.getName());
        this.miniJavaVarToClassInfo.put(miniJavaName, miniJavaClass);

        this.miniJavaVarToSparrowVar.put(miniJavaName, sparrowName);

        this.setSparrowVarSparrowType(sparrowName, miniJavaType);

        this.sparrowVarToMiniJavaType.put(sparrowName, miniJavaType);

        return sparrowName;
    }

    public void setSparrowVarSparrowType(String sparrowName, MiniJavaType miniJavaType) {
        // set the type of the sparrow variable
        SparrowType sparrowType;
        if (miniJavaType.equals(MiniJavaType.INTEGER) || miniJavaType.equals(MiniJavaType.BOOLEAN)) {
            sparrowType = SparrowType.INTEGER_TYPE;
        }
        else {
            sparrowType = SparrowType.ADDRESS_TYPE;
        }
        this.sparrowVarToSparrowType.put(sparrowName, sparrowType);
    }

    public ClassInfo getMiniJavaVarClassInfo(String var) {
        if (this.miniJavaVarToClassInfo.containsKey(var)) {
            return this.miniJavaVarToClassInfo.get(var);
        }
        else if (this.miniJavaVarToClassInfo.containsKey(this.getArgumentName(var))) {
            return this.miniJavaVarToClassInfo.get(this.getArgumentName(var));
        }
        else {
            throw new RuntimeException("Couldn't find var: " + var + " or argument " + this.getArgumentName(var));
        }
    }

    public String getSparrowVar(String miniJavaVariableName) {
        if (this.miniJavaVarToSparrowVar.containsKey(miniJavaVariableName)) {
            return this.miniJavaVarToSparrowVar.get(miniJavaVariableName);
        } 
        else if (this.miniJavaVarToSparrowVar.containsKey(this.getArgumentName(miniJavaVariableName))) {
            return this.miniJavaVarToSparrowVar.get(this.getArgumentName(miniJavaVariableName));
        } 
        else {
            throw new RuntimeException("Couldn't find var: " + miniJavaVariableName + " or argument " + this.getArgumentName(miniJavaVariableName));
        }
    }

    public void storeSparrowVarType(String var, MiniJavaType miniJavaType) {
        this.sparrowVarToMiniJavaType.put(var, miniJavaType);
    }

    public MiniJavaType getSparrowVarMiniJavaType(String var) {
        return this.sparrowVarToMiniJavaType.get(var);
    }

    // returns true if it is a local variable or a parameter name, else false
    public boolean isLocalVar(String miniJavaVarName) {
        return this.miniJavaVarToClassInfo.containsKey(miniJavaVarName) || this.miniJavaVarToClassInfo.containsKey(this.getArgumentName(miniJavaVarName));
    }

    public String getArgumentName(String name) {
        return Constants.ARGNAME_PREFIX + name;
    }

}



    