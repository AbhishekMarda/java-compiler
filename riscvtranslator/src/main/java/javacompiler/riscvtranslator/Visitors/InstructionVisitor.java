package javacompiler.riscvtranslator.Visitors;

import java.util.ArrayList;

import javacompiler.riscvtranslator.Helpers.Constants;
import javacompiler.riscvtranslator.Helpers.Gensym;
import javacompiler.riscvtranslator.Helpers.SVVar;
import javacompiler.riscvtranslator.Helpers.TraversalStruct;
import javacompiler.riscvtranslator.RiscV.Instructions.Addition;
import javacompiler.riscvtranslator.RiscV.Instructions.BranchEqualZero;
import javacompiler.riscvtranslator.RiscV.Instructions.Ecall;
import javacompiler.riscvtranslator.RiscV.Instructions.Instruction;
import javacompiler.riscvtranslator.RiscV.Instructions.Jump;
import javacompiler.riscvtranslator.RiscV.Instructions.JumpAndLink;
import javacompiler.riscvtranslator.RiscV.Instructions.JumpAndLinkRegister;
import javacompiler.riscvtranslator.RiscV.Instructions.Label;
import javacompiler.riscvtranslator.RiscV.Instructions.LoadAddress;
import javacompiler.riscvtranslator.RiscV.Instructions.LoadImmediate;
import javacompiler.riscvtranslator.RiscV.Instructions.LoadImmediateFunc;
import javacompiler.riscvtranslator.RiscV.Instructions.LoadWord;
import javacompiler.riscvtranslator.RiscV.Instructions.Move;
import javacompiler.riscvtranslator.RiscV.Instructions.Multiplication;
import javacompiler.riscvtranslator.RiscV.Instructions.Register;
import javacompiler.riscvtranslator.RiscV.Instructions.SetLessThan;
import javacompiler.riscvtranslator.RiscV.Instructions.StoreWord;
import javacompiler.riscvtranslator.RiscV.Instructions.Subtraction;
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
import cs132.IR.token.Identifier;

public class InstructionVisitor implements ArgRetVisitor<TraversalStruct, ArrayList<Instruction>>{

    @Override
    public ArrayList<Instruction> visit(Program node, TraversalStruct traversalStruct) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public ArrayList<Instruction> visit(FunctionDecl node, TraversalStruct traversalStruct) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public ArrayList<Instruction> visit(Block node, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();


        for (cs132.IR.sparrowv.Instruction instr : node.instructions) {
            instructions.addAll(instr.accept(this, traversalStruct));
        }

        String returnId = node.return_id.toString();
        int offset = traversalStruct.stackVarVisitor.getMappingFromFP(returnId);
        
        instructions.add(new LoadWord(Register.a0, Register.fp, offset));

        return instructions;
    }

    @Override
    public ArrayList<Instruction> visit(LabelInstr node, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();

        instructions.add(new Label(traversalStruct.createOrGetMapping(node.label.toString())));

        return instructions;
    }
    
    @Override
    public ArrayList<Instruction> visit(Move_Reg_Integer node, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();

        instructions.add(new LoadImmediate(new Register(node.lhs), node.rhs));

        return instructions;
    }
    
    @Override
    public ArrayList<Instruction> visit(Move_Reg_FuncName node, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();

        instructions.add(new LoadAddress(new Register(node.lhs), node.rhs.name));

        return instructions;
    }
    
    @Override
    public ArrayList<Instruction> visit(Add node, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();
        instructions.add(new Addition(new Register(node.lhs), new Register(node.arg1), new Register(node.arg2)));
        return instructions;
    }
    
    @Override
    public ArrayList<Instruction> visit(Subtract node, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();
        instructions.add(new Subtraction(new Register(node.lhs), new Register(node.arg1), new Register(node.arg2)));
        return instructions;
    }
    
    @Override
    public ArrayList<Instruction> visit(Multiply node, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();
        instructions.add(new Multiplication(new Register(node.lhs), new Register(node.arg1), new Register(node.arg2)));
        return instructions;
    }
    
    @Override
    public ArrayList<Instruction> visit(LessThan node, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();
        instructions.add(new SetLessThan(new Register(node.lhs), new Register(node.arg1), new Register(node.arg2)));
        return instructions;
    }
    
    @Override
    public ArrayList<Instruction> visit(Load node, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();
        instructions.add(new LoadWord(new Register(node.lhs), new Register(node.base), node.offset));
        return instructions;
    }
    
    @Override
    public ArrayList<Instruction> visit(Store node, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();
        instructions.add(new StoreWord(new Register(node.rhs), new Register(node.base), node.offset));
        return instructions;
    }
    
    @Override
    public ArrayList<Instruction> visit(Move_Reg_Reg node, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();
        instructions.add(new Move(new Register(node.lhs), new Register(node.rhs)));
        return instructions;
    }
    
    @Override
    public ArrayList<Instruction> visit(Move_Id_Reg node, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();

        // accounting for the bug in SparrowVConstructor() 
        SVVar var = new SVVar(node.lhs.toString());
        if (!var.isRegister()) {
            int offset = traversalStruct.stackVarVisitor.getMappingFromFP(node.lhs.toString());
            instructions.add(new StoreWord(new Register(node.rhs), Register.fp, offset));
        }
        else {
            SVVar right = new SVVar(node.rhs.toString());
            if (right.isRegister()) {
                // do what Move_Reg_Reg does
                instructions.add(new Move(new Register(node.lhs.toString()), new Register(node.rhs.toString())));
            }
            else {
                // do what Move_Reg_Id does
                int offset = traversalStruct.stackVarVisitor.getMappingFromFP(node.rhs.toString());
                instructions.add(new LoadWord(new Register(node.lhs.toString()), Register.fp, offset));
            }
        }

        return instructions;
    }
    
    @Override
    public ArrayList<Instruction> visit(Move_Reg_Id node, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();
        int offset = traversalStruct.stackVarVisitor.getMappingFromFP(node.rhs.toString());
        instructions.add(new LoadWord(new Register(node.lhs), Register.fp, offset));
        return instructions;
    }
    
    @Override
    public ArrayList<Instruction> visit(Alloc node, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();

        instructions.add(new Move(Register.a0, new Register(node.size)));
        instructions.add(new JumpAndLink(Constants.ALLOC_LABEL));
        instructions.add(new Move(new Register(node.lhs), Register.a0));

        return instructions;
    }
    
    @Override
    public ArrayList<Instruction> visit(Print node, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();

        instructions.add(new Move(Register.a1, new Register(node.content)));
        instructions.add(new LoadImmediateFunc(Register.a0, Constants.PRINT_INT));
        instructions.add(new Ecall());
        // print newline
        instructions.add(new LoadImmediate(Register.a1, 10));
        instructions.add(new LoadImmediate(Register.a0, 11));
        instructions.add(new Ecall());

        return instructions;
    }
    
    @Override
    public ArrayList<Instruction> visit(ErrorMessage node, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();
        if (node.msg.equals("array index out of bounds")) {
            instructions.add(new LoadAddress(Register.a0, Constants.OUT_OF_BOUNDS_STRING));
            instructions.add(new Jump(Constants.ERROR_LABEL));
        }
        else {
            instructions.add(new LoadAddress(Register.a0, Constants.NULL_POINTER_STRING));
            instructions.add(new Jump(Constants.ERROR_LABEL));
        }

        return instructions;
    }
    
    @Override
    public ArrayList<Instruction> visit(Goto node, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();

        instructions.add(new Jump(traversalStruct.createOrGetMapping(node.label.toString())));

        return instructions;
    }
    
    @Override
    public ArrayList<Instruction> visit(IfGoto node, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();
        /*
         * if0 condition goto label
         * ------------------------
         * beqz condition, label1
         * j label2
         * label1:
         * j label
         * label2:
         */

        String label1 = Gensym.gensym(Gensym.LABEL);
        String label2 = Gensym.gensym(Gensym.LABEL);

        instructions.add(new BranchEqualZero(new Register(node.condition), label1));
        instructions.add(new Jump(label2));
        instructions.add(new Label(label1));
        instructions.add(new Jump(traversalStruct.createOrGetMapping(node.label.toString())));
        instructions.add(new Label(label2));

        return instructions;
    }
    
    @Override
    public ArrayList<Instruction> visit(Call node, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();
        
        int stackSpaceForParams = Constants.WORD_SIZE * node.args.size();

        // create space on the stack for the arguments
        instructions.add(new LoadImmediate(Register.t6, stackSpaceForParams));
        instructions.add(new Subtraction(Register.sp, Register.sp, Register.t6));
        
        // add each argument to the stack space
        int i = 0;
        for (Identifier arg : node.args) {
            int offset = traversalStruct.stackVarVisitor.getMappingFromFP(arg.toString());
            instructions.add(new LoadWord(Register.t6, Register.fp, offset));
            instructions.add(new StoreWord(Register.t6, Register.sp, i * Constants.WORD_SIZE));
            i++;
        }

        instructions.add(new JumpAndLinkRegister(new Register(node.callee)));
        instructions.add(new Move(new Register(node.lhs), Register.a0));
        
        return instructions;
    }
    
    
}
