package javacompiler.riscvtranslator.Visitors;

import java.util.ArrayList;

import javacompiler.riscvtranslator.Helpers.Codegen;
import javacompiler.riscvtranslator.Helpers.TraversalStruct;
import javacompiler.riscvtranslator.RiscV.Function;
import javacompiler.riscvtranslator.RiscV.Instructions.Instruction;
import cs132.IR.sparrowv.Add;
import cs132.IR.sparrowv.Alloc;
import cs132.IR.sparrowv.Block;
import cs132.IR.sparrowv.Call;
import cs132.IR.sparrowv.ErrorMessage;
import cs132.IR.sparrowv.FunctionDecl;
import cs132.IR.sparrowv.Goto;
import cs132.IR.sparrowv.IfGoto;
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
import cs132.IR.sparrowv.visitor.ArgRetVisitor;

public class FunctionVisitor implements ArgRetVisitor<TraversalStruct, Function> {

    @Override
    public Function visit(Program node, TraversalStruct traversalStruct) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Function visit(FunctionDecl node, TraversalStruct traversalStruct) {
        
        // init the traversal struct
        StackVarVisitor stackVarVisitor = new StackVarVisitor(node.formalParameters, node.block.instructions);
        traversalStruct.stackVarVisitor = stackVarVisitor;

        ArrayList<Instruction> instructions = new ArrayList<>();

        instructions.addAll(Codegen.generateFunctionPrologue(stackVarVisitor.getNumStackVars()));

        instructions.addAll(node.block.accept(new InstructionVisitor(), traversalStruct));

        instructions.addAll(Codegen.generateFunctionEpilogue(stackVarVisitor.getNumStackVars(), node.formalParameters.size()));

        return new Function(node.functionName.name, instructions);
    }

    @Override
    public Function visit(Block node, TraversalStruct traversalStruct) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Function visit(LabelInstr node, TraversalStruct traversalStruct) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Function visit(Add node, TraversalStruct traversalStruct) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Function visit(Subtract node, TraversalStruct traversalStruct) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Function visit(Multiply node, TraversalStruct traversalStruct) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Function visit(LessThan node, TraversalStruct traversalStruct) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Function visit(Load node, TraversalStruct traversalStruct) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Function visit(Store node, TraversalStruct traversalStruct) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Function visit(Alloc node, TraversalStruct traversalStruct) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Function visit(Print node, TraversalStruct traversalStruct) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Function visit(ErrorMessage node, TraversalStruct traversalStruct) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Function visit(Goto node, TraversalStruct traversalStruct) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Function visit(IfGoto node, TraversalStruct traversalStruct) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Function visit(Call node, TraversalStruct traversalStruct) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Function visit(Move_Reg_Integer node, TraversalStruct traversalStruct) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Function visit(Move_Reg_FuncName node, TraversalStruct traversalStruct) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Function visit(Move_Reg_Reg node, TraversalStruct traversalStruct) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Function visit(Move_Id_Reg node, TraversalStruct traversalStruct) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Function visit(Move_Reg_Id node, TraversalStruct traversalStruct) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }
    
}
