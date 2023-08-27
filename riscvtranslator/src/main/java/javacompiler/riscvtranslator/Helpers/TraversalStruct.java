package javacompiler.riscvtranslator.Helpers;

import java.util.HashMap;

import javacompiler.riscvtranslator.Visitors.StackVarVisitor;

public class TraversalStruct {
    public StackVarVisitor stackVarVisitor;
    private HashMap<String, String> svLabelToVLabel = new HashMap<>();

    public String createOrGetMapping(String svLabel) {
        if (this.svLabelToVLabel.containsKey(svLabel)) {
            return this.svLabelToVLabel.get(svLabel);
        }
        else {
            String vLabel = Gensym.gensym(Gensym.LABEL);
            this.svLabelToVLabel.put(svLabel, vLabel);
            return vLabel;
        }
    }

}
