package javacompiler.riscvtranslator.RiscV;

import java.util.ArrayList;

import javacompiler.riscvtranslator.RiscV.Instructions.Instruction;

public class Function {
    String name; 
    ArrayList<Instruction> instructions;

    public Function(String name, ArrayList<Instruction> instructions) {
        this.name = name;
        this.instructions = instructions;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(".globl ").append(name).append("\n");
        sb.append(name).append(":\n");
        for (Instruction instruction : instructions) {
            sb.append(instruction.toString()).append("\n");
        }
        return sb.toString();
    }

}
