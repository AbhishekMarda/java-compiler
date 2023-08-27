package javacompiler.registerallocator.Visitors.LinearScan;

import java.util.ArrayList;

import javacompiler.registerallocator.Helpers.Gensym;
import javacompiler.registerallocator.Helpers.SVVar;
import javacompiler.registerallocator.Helpers.SVVarType;
import javacompiler.registerallocator.Helpers.SparrowVar;
import javacompiler.registerallocator.Helpers.TraversalStruct;
import cs132.IR.sparrow.Add;
import cs132.IR.sparrow.Alloc;
import cs132.IR.sparrow.Call;
import cs132.IR.sparrow.ErrorMessage;
import cs132.IR.sparrow.FunctionDecl;
import cs132.IR.sparrow.Goto;
import cs132.IR.sparrow.IfGoto;
import cs132.IR.sparrow.Instruction;
import cs132.IR.sparrow.LabelInstr;
import cs132.IR.sparrow.LessThan;
import cs132.IR.sparrow.Load;
import cs132.IR.sparrow.Move_Id_FuncName;
import cs132.IR.sparrow.Move_Id_Id;
import cs132.IR.sparrow.Move_Id_Integer;
import cs132.IR.sparrow.Multiply;
import cs132.IR.sparrow.Print;
import cs132.IR.sparrow.Program;
import cs132.IR.sparrow.Store;
import cs132.IR.sparrow.Subtract;
import cs132.IR.sparrow.visitor.ArgRetVisitor;
import cs132.IR.sparrowv.Block;
import cs132.IR.sparrowv.Move_Id_Reg;

public class BlockVisitor implements ArgRetVisitor<TraversalStruct, Block> {

    @Override
    public Block visit(Program arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Block visit(FunctionDecl arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Block visit(cs132.IR.sparrow.Block node, TraversalStruct traversalStruct) {
        ArrayList<cs132.IR.sparrowv.Instruction> instructions = new ArrayList<>();
        InstructionConverterVisitor icv = new InstructionConverterVisitor();

        // save callee saved registers
        // TODO: uncomment and handle callee saved separately if necessary
        // instructions.addAll(traversalStruct.saveRegisters(SVVar.getCalleeSavedRegisters(), new ArrayList<>()));
        ArrayList<cs132.IR.sparrowv.Instruction> restoreCalleeSavedInstrs = traversalStruct.restoreAfterCall();

        int instrNum = 1; // starting from the second index

        for (Instruction intr : node.instructions) {
            traversalStruct.setInstrNum(instrNum);
            instructions.addAll(intr.accept(icv, traversalStruct));
            instrNum++;
        }   

        // if the return id is a register, need to bring it to an id
        SparrowVar returnSVar = new SparrowVar(node.return_id.toString());
        SVVar returnSVVar = traversalStruct.getSVar(returnSVar, instrNum);
        if (returnSVVar.isRegister()) {
            SVVar stackLoc = new SVVar(Gensym.gensym(SVVarType.STACK_REGISTER), SVVarType.STACK_REGISTER, false);
            instructions.add(new Move_Id_Reg(stackLoc.toIdentifier(), returnSVVar.toRegister()));
            returnSVVar = stackLoc;
        }

        instructions.addAll(restoreCalleeSavedInstrs);
        
        return new Block(instructions, returnSVVar.toIdentifier());
    }

    @Override 
    public Block visit(LabelInstr arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Block visit(Move_Id_Integer arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Block visit(Move_Id_FuncName arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Block visit(Add arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Block visit(Subtract arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Block visit(Multiply arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Block visit(LessThan arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Block visit(Load arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Block visit(Store arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Block visit(Move_Id_Id arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Block visit(Alloc arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Block visit(Print arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Block visit(ErrorMessage arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Block visit(Goto arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Block visit(IfGoto arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Block visit(Call arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }
    
}
