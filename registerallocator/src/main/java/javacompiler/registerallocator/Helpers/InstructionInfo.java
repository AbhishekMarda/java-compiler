package javacompiler.registerallocator.Helpers;

import java.util.ArrayList;
import java.util.HashSet;

import cs132.IR.sparrow.Instruction;
import cs132.IR.token.Label;

public class InstructionInfo {

    public InstructionInfo() {

    }

    public InstructionInfo(HashSet<SparrowVar> use, SparrowVar def, Instruction i, Boolean isNextLineSuccessor) {
        if (use != null) {
            this.use = use;
        }
        if (def != null) {
            this.def.add(def);
        }
        if (i != null) {
            this.instruction = i;
        }
        this.nextLineIsSuccessor = isNextLineSuccessor;
    }

    private HashSet<SparrowVar> use = new HashSet<>();
    private HashSet<SparrowVar> def = new HashSet<>();
    private HashSet<SparrowVar> in  = new HashSet<>();
    private HashSet<SparrowVar> out = new HashSet<>();
    private Instruction instruction = null;
    private Label gotoLabel = null;
    private Label selfLabel = null;
    private ArrayList<Integer> successors = new ArrayList<>();
    private Integer instructionNumber = null;
    private Boolean nextLineIsSuccessor = null;

    // getters

    public HashSet<SparrowVar> getUse() {
        return this.use;
    }

    public HashSet<SparrowVar> getDef() {
        return this.def;
    }

    public HashSet<SparrowVar> getIn() {
        return this.in;
    }

    public HashSet<SparrowVar> getOut() {
        return this.out;
    }

    public Label getGotoLabel() {
        return gotoLabel;
    }

    public Label getSelfLabel() {
        return selfLabel;
    }

    public Instruction getInstruction() {
        return instruction;
    }

    public ArrayList<Integer> getSuccessors() {
        return successors;
    }

    public Integer getInstructionNumber() {
        return instructionNumber;
    }

    public Boolean isNextLineIsSuccessor() {
        return nextLineIsSuccessor;
    }

    // setters

    public void addSuccessor(Integer instrNum) {
        this.successors.add(instrNum);
    }

    public void setInstructionNumber(Integer instructionNumber) {
        this.instructionNumber = instructionNumber;
    }

    public void setInstruction(Instruction instruction) {
        this.instruction = instruction;
    }

    public void addUse(SparrowVar id) {
        this.use.add(id);
    }

    public void addDef(SparrowVar id) {
        this.def.add(id);
    }

    public void addIn(SparrowVar id) {
        this.in.add(id);
    }

    public void addOut(SparrowVar id) {
        this.out.add(id);
    }

    public void setGotoLabel(Label gotoLabel) {
        this.gotoLabel = gotoLabel;
    }
    
    public void setSelfLabel(Label selfLabel) {
        this.selfLabel = selfLabel;
    }

    public void setIn(HashSet<SparrowVar> in) {
        this.in = in;
    }

    public void setOut(HashSet<SparrowVar> out) {
        this.out = out;
    }

    // methods

    public boolean containsIn(SparrowVar hashableIdentifier) {
        return this.in.contains(hashableIdentifier);
    }

    public boolean containsOut(SparrowVar hashableIdentifier) {
        return this.out.contains(hashableIdentifier);
    }


    // compares ins and outs of the two instructions
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        InstructionInfo other = (InstructionInfo) obj;

        return this.compSets(this.in, other.in) && this.compSets(this.out, other.out);
    }

    private Boolean compSets(HashSet<SparrowVar> first, HashSet<SparrowVar> second) {
        if (first.size() != second.size()) {
            return false;
        }

        for (SparrowVar i : first) {    
            if (!second.contains(i)) {
                return false;
            }
        }

        for (SparrowVar i : second) {    
            if (!first.contains(i)) {
                return false;
            }
        }

        return true;
    }
}
