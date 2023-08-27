package javacompiler.registerallocator.Visitors.LivenessAnalysis;

import java.util.ArrayList;
import java.util.HashSet;
import javacompiler.registerallocator.Helpers.SparrowVar;
import javacompiler.registerallocator.Helpers.InstructionInfo;
import cs132.IR.syntaxtree.Add;
import cs132.IR.syntaxtree.Alloc;
import cs132.IR.syntaxtree.Call;
import cs132.IR.syntaxtree.ErrorMessage;
import cs132.IR.syntaxtree.Goto;
import cs132.IR.syntaxtree.IfGoto;
import cs132.IR.syntaxtree.Instruction;
import cs132.IR.syntaxtree.LabelWithColon;
import cs132.IR.syntaxtree.LessThan;
import cs132.IR.syntaxtree.Load;
import cs132.IR.syntaxtree.Move;
import cs132.IR.syntaxtree.Multiply;
import cs132.IR.syntaxtree.Node;
import cs132.IR.syntaxtree.Print;
import cs132.IR.syntaxtree.SetFuncName;
import cs132.IR.syntaxtree.SetInteger;
import cs132.IR.syntaxtree.Store;
import cs132.IR.syntaxtree.Subtract;
import cs132.IR.token.FunctionName;
import cs132.IR.token.Identifier;
import cs132.IR.token.Label;
import cs132.IR.visitor.GJNoArguDepthFirst;


// returns the instruction info struct for an instruction
// also converts from tree type instruction to parse type instruction

// note that labels are not identifiers and they do not need to be handled
public class InstructionInfoVisitor extends GJNoArguDepthFirst<InstructionInfo> {
    @Override
    public InstructionInfo visit(Instruction n) {
        return n.f0.choice.accept(this);
    }

    @Override
    public InstructionInfo visit(Add n) {

        SparrowVar def = new SparrowVar(n.f0.f0.tokenImage);

        HashSet<SparrowVar> use = new HashSet<>();
        use.add(new SparrowVar(n.f2.f0.tokenImage));
        use.add(new SparrowVar(n.f4.f0.tokenImage));

        cs132.IR.sparrow.Instruction i = new cs132.IR.sparrow.Add(
            new Identifier(n.f0.f0.tokenImage), 
            new Identifier(n.f2.f0.tokenImage), 
            new Identifier(n.f4.f0.tokenImage)
            );

        return new InstructionInfo(use, def, i, true);
    }

    @Override
    public InstructionInfo visit(Alloc n) {
        SparrowVar def = new SparrowVar(n.f0.f0.tokenImage);
        HashSet<SparrowVar> use = new HashSet<>();
        use.add(new SparrowVar(n.f4.f0.tokenImage));

        cs132.IR.sparrow.Instruction i = new cs132.IR.sparrow.Alloc(
            new Identifier(n.f0.f0.tokenImage),
            new Identifier(n.f4.f0.tokenImage)
        );
        
        return new InstructionInfo(use, def, i, true);
    }

    @Override
    public InstructionInfo visit(Call n) {
        SparrowVar def = new SparrowVar(n.f0.f0.tokenImage);
        HashSet<SparrowVar> use = new HashSet<>();
        HashSet<SparrowVar> params = new HashSet<>();
        ArrayList<Identifier> normalParamList = new ArrayList<>();
        // add all the parameters to param list
        if (n.f5.present()) {
            for (Node param : n.f5.nodes) {
                try {
                    cs132.IR.syntaxtree.Identifier paramId = (cs132.IR.syntaxtree.Identifier) param;
                    SparrowVar paramToken = new SparrowVar(paramId.f0.tokenImage);
                    params.add(paramToken);
                    normalParamList.add(new Identifier(paramId.f0.tokenImage));
                }
                catch (ClassCastException e) {
                    throw new RuntimeException("Unable to cast parameter in " + n.f3.f0.tokenImage + " to an identifier.");
                }
            }
        }

        cs132.IR.sparrow.Instruction i = new cs132.IR.sparrow.Call(new Identifier(n.f0.f0.tokenImage), new Identifier(n.f3.f0.tokenImage), normalParamList);

        // add the function call variable to use
        use.add(new SparrowVar(n.f3.f0.tokenImage));
        
        // add all the params to use
        use.addAll(params);
        

        return new InstructionInfo(use, def, i, true);
    }


    @Override
    public InstructionInfo visit(IfGoto n) {
        HashSet<SparrowVar> use = new HashSet<>();
        use.add(new SparrowVar(n.f1.f0.tokenImage));
        cs132.IR.sparrow.Instruction i = new cs132.IR.sparrow.IfGoto(new Identifier(n.f1.f0.tokenImage), new Label(n.f3.f0.tokenImage));

        InstructionInfo instructionInfo = new InstructionInfo(use, null, i, true);
        instructionInfo.setGotoLabel(new Label(n.f3.f0.tokenImage));
        return instructionInfo;
    }


    @Override
    public InstructionInfo visit(LessThan n) {
        HashSet<SparrowVar> use = new HashSet<>();
        SparrowVar def = new SparrowVar(n.f0.f0.tokenImage);

        use.add(new SparrowVar(n.f2.f0.tokenImage));
        use.add(new SparrowVar(n.f4.f0.tokenImage));

        cs132.IR.sparrow.Instruction i = new cs132.IR.sparrow.LessThan(
            new Identifier(n.f0.f0.tokenImage),
            new Identifier(n.f2.f0.tokenImage),
            new Identifier(n.f4.f0.tokenImage)
        );

        return new InstructionInfo(use, def, i, true);
    }

    @Override
    public InstructionInfo visit(Load n) {
        HashSet<SparrowVar> use = new HashSet<>();
        SparrowVar def = new SparrowVar(n.f0.f0.tokenImage);

        use.add(new SparrowVar(n.f3.f0.tokenImage));

        cs132.IR.sparrow.Instruction i = new cs132.IR.sparrow.Load(new Identifier(n.f0.f0.tokenImage), new Identifier(n.f3.f0.tokenImage), Integer.parseInt(n.f5.f0.tokenImage));

        return new InstructionInfo(use, def, i, true);
    }

    @Override
    public InstructionInfo visit(Move n) {
        HashSet<SparrowVar> use = new HashSet<>();
        SparrowVar def = new SparrowVar(n.f0.f0.tokenImage);

        use.add(new SparrowVar(n.f2.f0.tokenImage));

        cs132.IR.sparrow.Instruction i = new cs132.IR.sparrow.Move_Id_Id(new Identifier(n.f0.f0.tokenImage), new Identifier(n.f2.f0.tokenImage));

        return new InstructionInfo(use, def, i, true);
    }

    @Override
    public InstructionInfo visit(SetFuncName n) {
        
        HashSet<SparrowVar> use = new HashSet<>();
        SparrowVar def = new SparrowVar(n.f0.f0.tokenImage);
        
        cs132.IR.sparrow.Instruction i = new cs132.IR.sparrow.Move_Id_FuncName(new Identifier(n.f0.f0.tokenImage), new FunctionName(n.f3.f0.tokenImage));

        return new InstructionInfo(use, def, i, true);
    }

    @Override
    public InstructionInfo visit(SetInteger n) {
        HashSet<SparrowVar> use = new HashSet<>();
        SparrowVar def = new SparrowVar(n.f0.f0.tokenImage);
        
        cs132.IR.sparrow.Instruction i = new cs132.IR.sparrow.Move_Id_Integer(new Identifier(n.f0.f0.tokenImage), Integer.parseInt(n.f2.f0.tokenImage));

        return new InstructionInfo(use, def, i, true);
    }

    @Override
    public InstructionInfo visit(Multiply n) {
        HashSet<SparrowVar> use = new HashSet<>();
        SparrowVar def = new SparrowVar(n.f0.f0.tokenImage);

        use.add(new SparrowVar(n.f2.f0.tokenImage));
        use.add(new SparrowVar(n.f4.f0.tokenImage));
        
        cs132.IR.sparrow.Instruction i = new cs132.IR.sparrow.Multiply(
            new Identifier(n.f0.f0.tokenImage),
            new Identifier(n.f2.f0.tokenImage),
            new Identifier(n.f4.f0.tokenImage)
        );

        return new InstructionInfo(use, def, i, true);
    }

    @Override
    public InstructionInfo visit(Subtract n) {
        HashSet<SparrowVar> use = new HashSet<>();
        SparrowVar def = new SparrowVar(n.f0.f0.tokenImage);

        use.add(new SparrowVar(n.f2.f0.tokenImage));
        use.add(new SparrowVar(n.f4.f0.tokenImage));
        
        cs132.IR.sparrow.Instruction i = new cs132.IR.sparrow.Subtract(
            new Identifier(n.f0.f0.tokenImage),
            new Identifier(n.f2.f0.tokenImage),
            new Identifier(n.f4.f0.tokenImage)
        );

        return new InstructionInfo(use, def, i, true);
    }

    @Override
    public InstructionInfo visit(Print n) {
        HashSet<SparrowVar> use = new HashSet<>();

        use.add(new SparrowVar(n.f2.f0.tokenImage));
        
        cs132.IR.sparrow.Instruction i = new cs132.IR.sparrow.Print(new Identifier(n.f2.f0.tokenImage));

        return new InstructionInfo(use, null, i, true);
    }

    @Override
    public InstructionInfo visit(Store n) {
        HashSet<SparrowVar> use = new HashSet<>();
        use.add(new SparrowVar(n.f1.f0.tokenImage));
        use.add(new SparrowVar(n.f6.f0.tokenImage));
        
        cs132.IR.sparrow.Instruction i = new cs132.IR.sparrow.Store(new Identifier(n.f1.f0.tokenImage), Integer.parseInt(n.f3.f0.tokenImage), new Identifier(n.f6.f0.tokenImage));

        return new InstructionInfo(use, null, i, true);
    }

    @Override
    public InstructionInfo visit(ErrorMessage n) {
        HashSet<SparrowVar> use = new HashSet<>();
        cs132.IR.sparrow.Instruction i = new cs132.IR.sparrow.ErrorMessage(n.f2.f0.tokenImage);
        return new InstructionInfo(use, null, i, false);
    }

    @Override
    public InstructionInfo visit(LabelWithColon n) {
        HashSet<SparrowVar> use = new HashSet<>();
        cs132.IR.sparrow.Instruction i = new cs132.IR.sparrow.LabelInstr(new Label(n.f0.f0.tokenImage));
        
        InstructionInfo instructionInfo = new InstructionInfo(use, null, i, true);
        instructionInfo.setSelfLabel(new Label(n.f0.f0.tokenImage));
        return instructionInfo;
    }

    @Override
    public InstructionInfo visit(Goto n) {
        HashSet<SparrowVar> use = new HashSet<>();
        cs132.IR.sparrow.Instruction i = new cs132.IR.sparrow.Goto(new Label(n.f1.f0.tokenImage));
        InstructionInfo instructionInfo = new InstructionInfo(use, null, i, false);
        instructionInfo.setGotoLabel(new Label(n.f1.f0.tokenImage));

        return instructionInfo;
    }

}
