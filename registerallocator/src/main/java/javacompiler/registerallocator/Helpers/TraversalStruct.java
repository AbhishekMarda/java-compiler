package javacompiler.registerallocator.Helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import cs132.IR.sparrowv.Instruction;
import cs132.IR.sparrowv.Move_Id_Reg;
import cs132.IR.sparrowv.Move_Reg_Id;
import cs132.IR.token.Identifier;

public class TraversalStruct {
    
    public TraversalStruct(HashMap<String, LivenessAnalyzer> analyses) {
        for (SVVar temp : SVVar.getTemporaryRegisters()) {
            temps.put(temp, false);
        }
        this.analyses = analyses;
    }
    
    // members 

    int instrNum = 0; 
    RegisterAllocator registerAllocator = null;
    HashMap<String, LivenessAnalyzer> analyses;
    // map temp sparrow var to whether it is in use
    HashMap<SVVar, Boolean> temps = new HashMap<>();

    // to be used for call instruction related function only
    ArrayList<Instruction> restoreList = new ArrayList<>();
    HashMap<Identifier, SVVar> paramToStack = new HashMap<>();


    // methods

    public void initRegisterAllocator(String funcName, ArrayList<Identifier> params) {
        ArrayList<SparrowVar> sparrowParams = new ArrayList<>();
        for (Identifier param : params) {
            sparrowParams.add(new SparrowVar(param.toString()));
        }
        
        this.registerAllocator = new RegisterAllocator(
            funcName,
            this.analyses.get(funcName),
            sparrowParams
        );
//        System.err.println(registerAllocator.toString());
    }

    public ArrayList<Instruction> saveRegisters(ArrayList<SVVar> registers, HashSet<SparrowVar> outSet) {
        ArrayList<Instruction> instructions = new ArrayList<>();

        // for (SVVar register : registers) {
        //     SVVar stackLoc = new SVVar(Gensym.gensym(SVVarType.STACK_REGISTER), SVVarType.STACK_REGISTER, false);
        //     instructions.add(new Move_Id_Reg(stackLoc.toIdentifier(), register.toRegister()));
            
        //     // don't restore the register if it shoudln't be restored
        //     this.restoreList.add(new Move_Reg_Id(register.toRegister(), stackLoc.toIdentifier()));
        // }

        // only save the registers in the out of the call
        for (SparrowVar out : outSet) {
            SVVar svVarForOut = this.getSVar(out, this.instrNum);
            
            // don't care if not a register
            if (svVarForOut.isRegister() && registers.contains(svVarForOut)) {
                SVVar stackLoc = new SVVar(Gensym.gensym(SVVarType.STACK_REGISTER), SVVarType.STACK_REGISTER, false);
                instructions.add(new Move_Id_Reg(stackLoc.toIdentifier(), svVarForOut.toRegister()));
                
                // don't restore the register if it shoudln't be restored
                this.restoreList.add(new Move_Reg_Id(svVarForOut.toRegister(), stackLoc.toIdentifier()));
            }

        }

        return instructions;    
    }

    // params here will only be the params after argument 6
    public ArrayList<Instruction> saveBeforeCall(ArrayList<Identifier> params, int instrNum) {
        this.restoreList = new ArrayList<>();
        this.paramToStack = new HashMap<>();
        ArrayList<Instruction> instructions = new ArrayList<>();

        // optimization step: get the out set of the call instruction
        HashSet<SparrowVar> callOutSet = this.registerAllocator.getOutSet(instrNum);

        instructions.addAll(this.saveRegisters(SVVar.getCallerSavedRegisters(), callOutSet));
        instructions.addAll(this.saveRegisters(SVVar.getCalleeSavedRegisters(), callOutSet));
        instructions.addAll(this.saveRegisters(SVVar.getArgumentRegisters(), callOutSet));


        //TODO: if the register was already saved above, no need to resave it into a new stack location. just utilize the old one.
        // potential fix could involve having param mapping inside save registers
        for (Identifier param : params) {
            SVVar paramSVVar = this.getSVar(new SparrowVar(param.toString()), instrNum);
            if (paramSVVar.isRegister()) {
                SVVar stackLoc = new SVVar(Gensym.gensym(SVVarType.STACK_REGISTER), SVVarType.STACK_REGISTER, false);
                this.paramToStack.put(param, stackLoc);
                instructions.add(new Move_Id_Reg(stackLoc.toIdentifier(), paramSVVar.toRegister()));
            }
            else {
                this.paramToStack.put(param, paramSVVar);
            }
        }
        return instructions;
    }

    public SVVar getParamSVVar(Identifier id) {
        return this.paramToStack.get(id);
    }

    public ArrayList<Instruction> restoreAfterCall() {
        ArrayList<Instruction> instructions = new ArrayList<>();

        instructions.addAll(this.restoreList);

        this.restoreList = new ArrayList<>();
        this.paramToStack = new HashMap<>();

        return instructions;
    }

    public void setRegisterAllocator(RegisterAllocator registerAllocator) {
        this.registerAllocator = registerAllocator;
    }

    public SVVar getSVar(SparrowVar var, int instrNum) {
        return registerAllocator.getSVVar(var, instrNum);
    }

    public int getInstrNum() {
        return instrNum;
    }

    public void setInstrNum(int instrNum) {
        this.instrNum = instrNum;
    }

    public SVVar getTempRegister() {
        for (HashMap.Entry<SVVar, Boolean> entry : this.temps.entrySet()) {
            if (!entry.getValue()) {
                entry.setValue(true);
                return entry.getKey();
            }
        }
        return null;
    }

    public void releaseTempRegister(SVVar reg) {
        this.temps.put(reg, false);
    }
    
}