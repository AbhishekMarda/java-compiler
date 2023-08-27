package javacompiler.riscvtranslator.Visitors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javacompiler.riscvtranslator.Helpers.Constants;
import javacompiler.riscvtranslator.Helpers.SVVar;
import cs132.IR.sparrowv.Add;
import cs132.IR.sparrowv.Alloc;
import cs132.IR.sparrowv.Block;
import cs132.IR.sparrowv.Call;
import cs132.IR.sparrowv.ErrorMessage;
import cs132.IR.sparrowv.FunctionDecl;
import cs132.IR.sparrowv.Goto;
import cs132.IR.sparrowv.IfGoto;
import cs132.IR.sparrowv.Instruction;
import cs132.IR.sparrowv.LabelInstr;
import cs132.IR.sparrowv.LessThan;
import cs132.IR.sparrowv.Load;
import cs132.IR.sparrowv.Move_Id_Reg;
import cs132.IR.sparrowv.Move_Reg_FuncName;
import cs132.IR.sparrowv.Move_Reg_Id;
import cs132.IR.sparrowv.Move_Reg_Integer;
import cs132.IR.sparrowv.Move_Reg_Reg;
import cs132.IR.sparrowv.Multiply;
import cs132.IR.sparrowv.Print;
import cs132.IR.sparrowv.Program;
import cs132.IR.sparrowv.Store;
import cs132.IR.sparrowv.Subtract;
import cs132.IR.sparrowv.visitor.ArgVisitor;
import cs132.IR.token.Identifier;

// get the stack variables 
public class StackVarVisitor implements ArgVisitor<ArrayList<SVVar>>{

    public StackVarVisitor(List<Identifier> params, List<Instruction> instructions) {
        ArrayList<SVVar> stackVars = new ArrayList<>();

        for (Instruction instr : instructions) {
            instr.accept(this, stackVars);
        }

        int i = 0;
        // allocate params the opposite way
        ArrayList<Identifier> paramCopy = new ArrayList<>(params);

        for (Identifier param : paramCopy) {
            this.offsetMap.put(new SVVar(param.toString()), i);
            i--;
        }

        i = 3; // leaving space for function pointer and return address
        for (SVVar var : stackVars) {
            this.offsetMap.put(var, i);
            i++;
            numStackVars++;
        }


    }

    private HashMap<SVVar, Integer> offsetMap = new HashMap<>();
    private Integer numStackVars = 0;

    public int getMappingFromFP(String name) {
        SVVar var = new SVVar(name);
        return -1 * Constants.WORD_SIZE * this.offsetMap.get(var);
    }

    public int getNumStackVars() {
        return numStackVars;
    }

    @Override
    public void visit(Program node, ArrayList<SVVar> stackVars) {
        
        
    }

    @Override
    public void visit(FunctionDecl node, ArrayList<SVVar> stackVars) {
        
        
    }

    @Override
    public void visit(Block node, ArrayList<SVVar> stackVars) {
        
        
    }

    @Override
    public void visit(LabelInstr node, ArrayList<SVVar> stackVars) {
        
        
    }

    @Override
    public void visit(Move_Reg_Integer node, ArrayList<SVVar> stackVars) {
        
        
    }

    @Override
    public void visit(Move_Reg_FuncName node, ArrayList<SVVar> stackVars) {
        
        
    }

    @Override
    public void visit(Add node, ArrayList<SVVar> stackVars) {
        
        
    }

    @Override
    public void visit(Subtract node, ArrayList<SVVar> stackVars) {
        
        
    }

    @Override
    public void visit(Multiply node, ArrayList<SVVar> stackVars) {
        
        
    }

    @Override
    public void visit(LessThan node, ArrayList<SVVar> stackVars) {
        
        
    }

    @Override
    public void visit(Load node, ArrayList<SVVar> stackVars) {
        
        
    }

    @Override
    public void visit(Store node, ArrayList<SVVar> stackVars) {
        
        
    }

    @Override
    public void visit(Move_Reg_Reg node, ArrayList<SVVar> stackVars) {
        
        
    }

    @Override
    public void visit(Move_Id_Reg node, ArrayList<SVVar> stackVars) {
        SVVar var = new SVVar(node.lhs.toString());
        
        if (!stackVars.contains(var) && !var.isRegister()) {
            stackVars.add(var);
        }
    }

    @Override
    public void visit(Move_Reg_Id node, ArrayList<SVVar> stackVars) {
        
        
    }

    @Override
    public void visit(Alloc node, ArrayList<SVVar> stackVars) {
        
        
    }

    @Override
    public void visit(Print node, ArrayList<SVVar> stackVars) {
        
        
    }

    @Override
    public void visit(ErrorMessage node, ArrayList<SVVar> stackVars) {
        
        
    }

    @Override
    public void visit(Goto node, ArrayList<SVVar> stackVars) {
        
        
    }

    @Override
    public void visit(IfGoto node, ArrayList<SVVar> stackVars) {
        
        
    }

    @Override
    public void visit(Call node, ArrayList<SVVar> stackVars) {
        
        
    }
    
}
