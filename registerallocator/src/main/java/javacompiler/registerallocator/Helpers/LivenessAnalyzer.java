package javacompiler.registerallocator.Helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import javacompiler.registerallocator.Visitors.LivenessAnalysis.InstructionInfoVisitor;
import cs132.IR.syntaxtree.Identifier;
import cs132.IR.syntaxtree.Node;
import cs132.IR.token.Label;

public class LivenessAnalyzer {
    
    public LivenessAnalyzer(Vector<Node> instructions, ArrayList<Identifier> params, SparrowVar returnId) {
        InstructionInfoVisitor infoVisitor = new InstructionInfoVisitor();
        
        HashSet<SparrowVar> paramTokens = new HashSet<>();
        for (Identifier param : params) {
            paramTokens.add(new SparrowVar(param.f0.tokenImage));
            this.identifiers.add(new SparrowVar(param.f0.tokenImage));
        }
        InstructionInfo paramInfo = new InstructionInfo(paramTokens, null, null, true);

        this.instructionList.add(paramInfo);

        
        for (Node instruction : instructions) {
            InstructionInfo instructionInfo = instruction.accept(infoVisitor);
            if (instructionInfo != null) { // ie, an instruction we care about
                this.instructionList.add(instructionInfo);
                
                // add all identifiers to our set
                for (SparrowVar id : instructionInfo.getDef()) {
                    this.identifiers.add(id);
                }
                for (SparrowVar id : instructionInfo.getUse()) {
                    this.identifiers.add(id);
                }
            }
            else {
                throw new RuntimeException("An instruction which was not accepted!");
            }
        }
        
        HashSet<SparrowVar> retTokens = new HashSet<>();
        retTokens.add(returnId);
        InstructionInfo returnInfo = new InstructionInfo(retTokens, null, null, false);
        this.instructionList.add(returnInfo);

        this.setInstructionIndices();
        this.runAnalysis();
        this.setIntervals();
    }

    // members
    private ArrayList<InstructionInfo> instructionList = new ArrayList<>();
    private HashSet<SparrowVar> identifiers = new HashSet<>();
    private HashMap<SparrowVar, IntervalInfo> idToInterval = new HashMap<>();


    // methods

    public ArrayList<InstructionInfo> getInstructionList() {
        return instructionList;
    }

    public HashSet<SparrowVar> getIdentifiers() {
        return identifiers;
    }
    
    public HashMap<SparrowVar, IntervalInfo> getIdToInterval() {
        return idToInterval;
    }

    // method assumes that the liveness analysis has been completed
    private void setIntervals() {

        // init
        // for each identifier, put an array list of size 3 in the hash map

        for (SparrowVar identifier : this.identifiers) {
            this.idToInterval.put(identifier, new IntervalInfo(identifier));
        }

        // run
        // iterate over the instructions
        for (InstructionInfo info : this.instructionList) {
            
            // if the identifier is going out, then that instruction at least be the start
            for (SparrowVar id : info.getOut()) {
                IntervalInfo intervalInfo = this.idToInterval.get(id);
                if (intervalInfo.start == null) {
                    intervalInfo.start = info.getInstructionNumber();
                }
            } 

            // if the identifier is coming in, then that instruction must at least be the end
            for (SparrowVar id : info.getIn()) {
                IntervalInfo intervalInfo = this.idToInterval.get(id);
                intervalInfo.end = info.getInstructionNumber();
            } 

            // if the identifer was used or defined, there was an occurrence of the variable there
            // and thus must be updated in the frequency analysis
            for (SparrowVar id : info.getDef()) {
                IntervalInfo intervalInfo = this.idToInterval.get(id);
                intervalInfo.totalFrequency += 1;
                intervalInfo.occurrences.add(info.getInstructionNumber());
            }
            for (SparrowVar id : info.getUse()) {
                IntervalInfo intervalInfo = this.idToInterval.get(id);
                intervalInfo.totalFrequency += 1;
                intervalInfo.occurrences.add(info.getInstructionNumber());
            }
        }


        // TODO: eventually remove this so that dead code variables are just used in the temps
        // will be fine even if that code runs wrong, since it won't really matter

        // iterate over all the instruction infos again, and if there is a use or def id with 
        // unassigned start or end values, then assign it

        for (InstructionInfo info : this.instructionList) {
            for (SparrowVar id : info.getDef()) {
                IntervalInfo intervalInfo = this.idToInterval.get(id);
                if (intervalInfo.start == null) {
                    intervalInfo.start = info.getInstructionNumber();
                }
                if (intervalInfo.end == null) {
                    intervalInfo.end = info.getInstructionNumber();
                }
            }
            for (SparrowVar id : info.getUse()) {
                IntervalInfo intervalInfo = this.idToInterval.get(id);
                if (intervalInfo.start == null) {
                    intervalInfo.start = info.getInstructionNumber();
                }
                
                if (intervalInfo.end == null) {
                    intervalInfo.end = info.getInstructionNumber();
                }
            }
        }

    }

    private void runAnalysis() {
        ArrayList<HashSet<SparrowVar>> insCurrent = new ArrayList<>();
        ArrayList<HashSet<SparrowVar>> insPrev = new ArrayList<>();
        ArrayList<HashSet<SparrowVar>> outsCurrent = new ArrayList<>();
        ArrayList<HashSet<SparrowVar>> outsPrev = new ArrayList<>();
        
        for (int i=0; i<this.instructionList.size(); i++) {
            insCurrent.add(new HashSet<>());
            outsCurrent.add(new HashSet<>());
        }

        // variables initialized, now begin liveness analysis

        // while convergence is not achieved
        while (!insCurrent.equals(insPrev) || !outsCurrent.equals(outsPrev)) {     

            insPrev = new ArrayList<>(insCurrent);
            outsPrev = new ArrayList<>(outsCurrent);

            // in[n] = use [n] U (out[n] - def[n])
            for (int i=0; i<this.instructionList.size(); i++) {
                HashSet<SparrowVar> temp = new HashSet<>(outsPrev.get(i));
                for (SparrowVar id :  this.instructionList.get(i).getDef()) {
                    temp.remove(id);
                }

                for (SparrowVar id : this.instructionList.get(i).getUse()) {
                    temp.add(id);
                }   
                insCurrent.set(i, temp);
            }


            // out[n] = U (over successors) in[n]
            for (int i=0; i<this.instructionList.size(); i++) {
                HashSet<SparrowVar> temp = new HashSet<>();
                
                for (Integer index : this.instructionList.get(i).getSuccessors()) {
                    for (SparrowVar id : insCurrent.get(index)) {
                        temp.add(id);
                    }
                }

                outsCurrent.set(i, temp);
            }
        }

        // set the outs and ins for the individual instructions
        for (int i=0; i < this.instructionList.size(); i++) {
            this.instructionList.get(i).setIn(insCurrent.get(i));
            this.instructionList.get(i).setOut(outsCurrent.get(i));
        }

    }

    private void setInstructionIndices() {
        for (int i=0; i < this.instructionList.size(); i++) {
            InstructionInfo info = this.instructionList.get(i);
            info.setInstructionNumber(i);

            // for the last one, the next line should not be a successor
            if (info.isNextLineIsSuccessor() && i != this.instructionList.size() - 1) {
                info.addSuccessor(i+1);
            }

            if(info.getGotoLabel() != null) {
                Integer gotoInstrNum = this.getLabelInstructionNumber(info.getGotoLabel());
                info.addSuccessor(gotoInstrNum);
            }
        }   

    }

    private Integer getLabelInstructionNumber(Label label) {
        for(int i=0; i < this.instructionList.size(); i++) {
            if (this.instructionList.get(i).getSelfLabel() == null) {
                continue;
            }
            else if (this.instructionList.get(i).getSelfLabel().toString().equals(label.toString())) {
                return i;
            }
        }

        throw new RuntimeException("Could not find " + label + " in the instruction list");
    }

    public class IntervalInfo {
        public IntervalInfo(SparrowVar var) {
            this.var = var;
        }
        public Integer start = null;
        public Integer end = null;
        public Integer totalFrequency = 0;
        public SparrowVar var;
        public SVVar svId = null;
        // can contain duplicates if the same variable is used multiple times in the same instruction
        public ArrayList<Integer> occurrences = new ArrayList<>(); 
    }

}
