package javacompiler.registerallocator.Visitors.LinearScan;

import java.util.ArrayList;
import java.util.HashMap;

import javacompiler.registerallocator.Helpers.LivenessAnalyzer;
import javacompiler.registerallocator.Helpers.TraversalStruct;
import cs132.IR.sparrow.Add;
import cs132.IR.sparrow.Alloc;
import cs132.IR.sparrow.Block;
import cs132.IR.sparrow.Call;
import cs132.IR.sparrow.ErrorMessage;
import cs132.IR.sparrow.FunctionDecl;
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
import cs132.IR.sparrow.Store;
import cs132.IR.sparrow.Subtract;
import cs132.IR.sparrow.visitor.ArgRetVisitor;
import cs132.IR.sparrowv.Program;

public class ProgramVisitor implements ArgRetVisitor<HashMap<String,LivenessAnalyzer>, Program> {

    @Override
    public Program visit(cs132.IR.sparrow.Program node, HashMap<String, LivenessAnalyzer> analyses) {
        TraversalStruct traversalStruct = new TraversalStruct(analyses);
        ArrayList<cs132.IR.sparrowv.FunctionDecl> decls = new ArrayList<>();

        for (FunctionDecl funcDecl : node.funDecls) {
            decls.add(funcDecl.accept(new FuncVisitor(), traversalStruct));
        }

        return new Program(decls);

    }

    @Override
    public Program visit(FunctionDecl arg0, HashMap<String, LivenessAnalyzer> arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Program visit(Block arg0, HashMap<String, LivenessAnalyzer> arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Program visit(LabelInstr arg0, HashMap<String, LivenessAnalyzer> arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Program visit(Move_Id_Integer arg0, HashMap<String, LivenessAnalyzer> arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Program visit(Move_Id_FuncName arg0, HashMap<String, LivenessAnalyzer> arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Program visit(Add arg0, HashMap<String, LivenessAnalyzer> arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Program visit(Subtract arg0, HashMap<String, LivenessAnalyzer> arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Program visit(Multiply arg0, HashMap<String, LivenessAnalyzer> arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Program visit(LessThan arg0, HashMap<String, LivenessAnalyzer> arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Program visit(Load arg0, HashMap<String, LivenessAnalyzer> arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Program visit(Store arg0, HashMap<String, LivenessAnalyzer> arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Program visit(Move_Id_Id arg0, HashMap<String, LivenessAnalyzer> arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Program visit(Alloc arg0, HashMap<String, LivenessAnalyzer> arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Program visit(Print arg0, HashMap<String, LivenessAnalyzer> arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Program visit(ErrorMessage arg0, HashMap<String, LivenessAnalyzer> arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Program visit(Goto arg0, HashMap<String, LivenessAnalyzer> arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Program visit(IfGoto arg0, HashMap<String, LivenessAnalyzer> arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public Program visit(Call arg0, HashMap<String, LivenessAnalyzer> arg1) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }
    
}
