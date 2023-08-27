package javacompiler.registerallocator.Visitors.LinearScan;

import java.util.ArrayList;

import javacompiler.registerallocator.Helpers.SVVar;
import javacompiler.registerallocator.Helpers.TraversalStruct;
import cs132.IR.sparrow.Add;
import cs132.IR.sparrow.Alloc;
import cs132.IR.sparrow.Block;
import cs132.IR.sparrow.Call;
import cs132.IR.sparrow.ErrorMessage;
import cs132.IR.sparrow.Goto;
import cs132.IR.sparrow.IfGoto;
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
import cs132.IR.sparrowv.FunctionDecl;
import cs132.IR.token.Identifier;

public class FuncVisitor implements ArgRetVisitor<TraversalStruct, FunctionDecl>{

    @Override
    public FunctionDecl visit(Program arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public FunctionDecl visit(cs132.IR.sparrow.FunctionDecl node, TraversalStruct traversalStruct) {
        
        ArrayList<Identifier> params = new ArrayList<>(node.formalParameters);
        
        ArrayList<Identifier> spiltParams = new ArrayList<>();

        if (params.size() > SVVar.getArgumentRegisters().size()) {
            spiltParams = new ArrayList<>(params.subList(SVVar.getArgumentRegisters().size(), params.size()));
        }

        traversalStruct.initRegisterAllocator(node.functionName.name, params);

        cs132.IR.sparrowv.Block b = node.block.accept(new BlockVisitor(), traversalStruct);

        return new FunctionDecl(node.functionName, spiltParams, b);
        
    }

    @Override
    public FunctionDecl visit(Block arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public FunctionDecl visit(LabelInstr arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public FunctionDecl visit(Move_Id_Integer arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public FunctionDecl visit(Move_Id_FuncName arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public FunctionDecl visit(Add arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public FunctionDecl visit(Subtract arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public FunctionDecl visit(Multiply arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public FunctionDecl visit(LessThan arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public FunctionDecl visit(Load arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public FunctionDecl visit(Store arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public FunctionDecl visit(Move_Id_Id arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public FunctionDecl visit(Alloc arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public FunctionDecl visit(Print arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public FunctionDecl visit(ErrorMessage arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public FunctionDecl visit(Goto arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public FunctionDecl visit(IfGoto arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public FunctionDecl visit(Call arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }
    
}
