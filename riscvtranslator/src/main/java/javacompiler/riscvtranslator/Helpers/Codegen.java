package javacompiler.riscvtranslator.Helpers;

import java.util.ArrayList;

import javacompiler.riscvtranslator.RiscV.Instructions.AddImmediate;
import javacompiler.riscvtranslator.RiscV.Instructions.Instruction;
import javacompiler.riscvtranslator.RiscV.Instructions.JumpRegister;
import javacompiler.riscvtranslator.RiscV.Instructions.LoadImmediate;
import javacompiler.riscvtranslator.RiscV.Instructions.LoadWord;
import javacompiler.riscvtranslator.RiscV.Instructions.Move;
import javacompiler.riscvtranslator.RiscV.Instructions.Register;
import javacompiler.riscvtranslator.RiscV.Instructions.StoreWord;
import javacompiler.riscvtranslator.RiscV.Instructions.Subtraction;

public class Codegen {
    public static ArrayList<Instruction> generateFunctionPrologue(int numStackVars) {
        ArrayList<Instruction> instructions = new ArrayList<>();

        numStackVars += 2;  // since fp and ra are also on the stack

        instructions.add(new StoreWord(Register.fp, Register.sp, -1 * Constants.WORD_SIZE * FP_POSITION));
        instructions.add(new Move(Register.fp, Register.sp));
        instructions.add(new LoadImmediate(Register.t6, numStackVars * Constants.WORD_SIZE));
        instructions.add(new Subtraction(Register.sp, Register.sp, Register.t6));
        instructions.add(new StoreWord(Register.ra, Register.fp, -1 * Constants.WORD_SIZE * RET_POSITION));
    
        return instructions;
    }

    public static ArrayList<Instruction> generateFunctionEpilogue(int numStackVars, int numFuncArgs) {
        ArrayList<Instruction> instructions = new ArrayList<>();

        numStackVars += 2; // since fp and ra are also on the stack

        instructions.add(new LoadWord(Register.ra, Register.fp, -1 * Constants.WORD_SIZE * RET_POSITION));
        instructions.add(new LoadWord(Register.fp, Register.fp, -1 * Constants.WORD_SIZE * FP_POSITION));
        instructions.add(new AddImmediate(Register.sp, Register.sp, Constants.WORD_SIZE * numStackVars));
        instructions.add(new AddImmediate(Register.sp, Register.sp, Constants.WORD_SIZE * numFuncArgs));
        instructions.add(new JumpRegister(Register.ra));

        return instructions;
    }

    final static int RET_POSITION = 1;
    final static int FP_POSITION = 2;

}
