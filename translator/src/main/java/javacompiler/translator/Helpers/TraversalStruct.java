package javacompiler.translator.Helpers;

import java.util.ArrayList;

public class TraversalStruct {
    public TraversalStruct(OffsetCollector offsetCollector) {
        this.offsetCollector = offsetCollector;
    }

    private Scope scope = null;
    private OffsetCollector offsetCollector = null;
    private String targetVariable = null;

    // to be used only by MessageSend and ExpressionListVisitor
    private ArrayList<String> targetVariableList = null;

    public ArrayList<String> getTargetVariableList() {
        return this.targetVariableList;
    }

    public void setTargetVariableList(ArrayList<String> targetVariableList) {
        this.targetVariableList = targetVariableList;
    }

    public OffsetCollector getOffsetCollector() {
        return this.offsetCollector;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public Scope getScope() {
        return this.scope;
    }

    public String getTargetVariable() {
        return this.targetVariable;
    }

    public void setTargetVariable(String targetVariable) {
        this.targetVariable = targetVariable;
    }

    public void flushTargetVariable() {
        this.setTargetVariable(null);
    }

    public void setTargetMiniJavaType(MiniJavaType type) {
        this.scope.storeSparrowVarType(this.targetVariable, type);
        this.scope.setSparrowVarSparrowType(this.targetVariable, type);
    }


}
