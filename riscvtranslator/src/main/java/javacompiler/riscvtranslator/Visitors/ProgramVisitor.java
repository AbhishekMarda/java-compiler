package javacompiler.riscvtranslator.Visitors;

import java.util.ArrayList;

import javacompiler.riscvtranslator.Helpers.TraversalStruct;
import javacompiler.riscvtranslator.RiscV.Function;
import javacompiler.riscvtranslator.RiscV.Program;
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
import cs132.IR.sparrowv.Store;
import cs132.IR.sparrowv.Subtract;
import cs132.IR.sparrowv.visitor.ArgRetVisitor;

public class ProgramVisitor implements ArgRetVisitor<TraversalStruct, Program> {

    @Override
    public Program visit(cs132.IR.sparrowv.Program node, TraversalStruct traversalStruct) {
        ArrayList<Function> functions = new ArrayList<>();

        for (FunctionDecl decl : node.funDecls) {
            functions.add(decl.accept(new FunctionVisitor(), new TraversalStruct()));
        }

        return new Program(functions);
    }

    @Override
    public Program visit(FunctionDecl arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Program visit(Block arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Program visit(LabelInstr arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Program visit(Move_Reg_Integer arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Program visit(Move_Reg_FuncName arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Program visit(Add arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Program visit(Subtract arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Program visit(Multiply arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Program visit(LessThan arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Program visit(Load arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Program visit(Store arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Program visit(Move_Reg_Reg arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Program visit(Move_Id_Reg arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Program visit(Move_Reg_Id arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Program visit(Alloc arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Program visit(Print arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Program visit(ErrorMessage arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Program visit(Goto arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Program visit(IfGoto arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Program visit(Call arg0, TraversalStruct arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }
    
}
