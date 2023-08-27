package javacompiler.registerallocator.Helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.TreeSet;

import javacompiler.registerallocator.Helpers.LivenessAnalyzer.IntervalInfo;

/*
Need a way of keeping track of the follows:

- When an identifier dies, so that the associated register is freed up

- Map of sparrow variable name to sparrowV register or variable name

- A cost function, that gives the cost given instruction number and variable
and returns the cost of not putting the variable in a register 

- A good way to keeping track of which variable is in memory and which
is in a register

- A visitor would be implemented to keep track out outputting the code. 
Potentially, that code will act as the brain and this object as a struct

- preference given to caller saved as compared to callee saved
 */


public class RegisterAllocator {
    public RegisterAllocator(String funcName, LivenessAnalyzer analyzer, ArrayList<SparrowVar> params) {
        this.livenessAnalyzer = analyzer;
        this.params = params;
        this.funcName = funcName;

        this.availableRegisters.add(new Stack<>()); // priority 1 registers: caller saved
        this.availableRegisters.add(new Stack<>()); // priority 2 registers: callee saved

        // availableRegisters.addAll(SVVar.getArgumentRegisters());
        this.addRegistersToStack(SVVar.getCalleeSavedRegisters());
        this.addRegistersToStack(SVVar.getCallerSavedRegisters());
        this.mapFuncArguments();

        this.allocateRegisters();
    }

    // members
    private final int REGISTER_PRIORITY_1 = 0;
    private final int REGISTER_PRIORITY_2 = 1;
    private LivenessAnalyzer livenessAnalyzer = null;
    private String funcName;
    private HashMap<SparrowVar, SVVar> sparrowToSVMap = new HashMap<>();
    private ArrayList<Stack<SVVar>> availableRegisters = new ArrayList<>();
    private ArrayList<SparrowVar> params = new ArrayList<>();

    // methods
    public HashSet<SparrowVar> getOutSet(int instrNum) {
        return this.livenessAnalyzer.getInstructionList().get(instrNum).getOut();
    }

    public SVVar getSVVar(SparrowVar var, int instrNum) {
        SVVar ret = this.sparrowToSVMap.get(var);

        if (ret == null) {
            // TODO: eventually remove dead code by just returning one of the temp variables in the case that there is no interval found
            throw new RuntimeException("Received null SVVar for var: " + var.name + ". Check for if var was a valid key: " + sparrowToSVMap.containsKey(var) + ". Function name: " + funcName);
        }

        return ret;
    }

    private void addRegistersToStack(ArrayList<SVVar> registers) {
        for (int i=registers.size() - 1; i >= 0; i--) {
            SVVar reg = registers.get(i);
            this.makeRegisterAvailable(reg);
        }
    }

    private void mapFuncArguments() {
        ArrayList<SVVar> argRegisters = SVVar.getArgumentRegisters();
        int numArgRegisters = argRegisters.size() < this.params.size() ? argRegisters.size() : this.params.size();
        
        // allocate first 6 args in argument registers
        for (int i=0; i < numArgRegisters; i++) {
            this.sparrowToSVMap.put(this.params.get(i), argRegisters.get(i));
        }

        // allocate the rest to the stack
        for (int i=numArgRegisters; i < this.params.size(); i++) {
            // keep the name same so that a register switch is saved
            this.sparrowToSVMap.put(this.params.get(i), new SVVar(this.params.get(i).name, SVVarType.SPILL_REGISTER, false));
        }
    }


    private SVVar getFreeRegister() {
        if (this.availableRegisters.get(REGISTER_PRIORITY_1).size() != 0) {
            return this.availableRegisters.get(REGISTER_PRIORITY_1).pop();
        }
        else if (this.availableRegisters.get(REGISTER_PRIORITY_2).size() != 0) {
            return this.availableRegisters.get(REGISTER_PRIORITY_2).pop();
        }
        return null;
    }

    private void makeRegisterAvailable(SVVar reg) {
        if (reg.getVarType().equals(SVVarType.CALLER_SAVED_REGISTER) ||reg.getVarType().equals(SVVarType.ARGUMENT_REGISTER)) {
            this.availableRegisters.get(REGISTER_PRIORITY_1).push(reg);
        } 
        else {
            this.availableRegisters.get(REGISTER_PRIORITY_2).push(reg);
        }
    }

    private void allocateRegisters() {
        HashMap<SparrowVar, IntervalInfo> intervalMap = livenessAnalyzer.getIdToInterval();

        ArrayList<IntervalInfo> intervals = new ArrayList<>(intervalMap.values());
        TreeSet<IntervalInfo> active = new TreeSet<>(Comparator.comparingInt(info -> info.end));

        // remove params
        Iterator<IntervalInfo> iterator = intervals.iterator();
        while (iterator.hasNext()) {
            IntervalInfo interval = iterator.next();
            if (this.params.contains(interval.var)) {
                iterator.remove();
            }
        }

        // sort array by start time
        Collections.sort(intervals, ((o1, o2) -> o1.start.compareTo(o2.start)));

        for(int i=0; i<intervals.size(); i++) {
            IntervalInfo curr = intervals.get(i);

            // expire old intervals
            ArrayList<IntervalInfo> intervalsToRemove = new ArrayList<>();
            for (IntervalInfo interval: active) {
                if (interval.end >= curr.start) {
                    break;
                }
                else {
                    intervalsToRemove.add(interval);
                }
            }
            for (IntervalInfo interval : intervalsToRemove) {
                if (interval.svId.isRegister()) {
                    this.makeRegisterAvailable(interval.svId);
                }
                active.remove(interval);
            }

            // check for any remaining registers available
            SVVar reg = this.getFreeRegister();
            if (reg == null) {
                // spill at interval
                IntervalInfo spill = active.last();
                if (spill.end > curr.end) {
                    curr.svId = spill.svId;
                    spill.svId = new SVVar(Gensym.gensym(SVVarType.SPILL_REGISTER), SVVarType.SPILL_REGISTER, false);
                    active.remove(spill);
                    active.add(curr);
                }
                else {
                    curr.svId = new SVVar(Gensym.gensym(SVVarType.SPILL_REGISTER), SVVarType.SPILL_REGISTER, false);
                }
            }
            else {
                curr.svId = reg;
                active.add(curr);
            }
        }

        // finally assign the mapping
        for (IntervalInfo info : intervals) {
            if (info.svId == null) {
                throw new RuntimeException("Null svid found for interval of variable: " + info.var.name);
            }
            this.sparrowToSVMap.put(info.var, info.svId);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("HashMap Mapping:\n");

        for (Map.Entry<SparrowVar, SVVar> entry : sparrowToSVMap.entrySet()) {
            SparrowVar key = entry.getKey();
            SVVar value = entry.getValue();
            sb.append(key.toString()).append(" -> ").append(value.toString()).append("\n");
        }

        return sb.toString();
    }
}
