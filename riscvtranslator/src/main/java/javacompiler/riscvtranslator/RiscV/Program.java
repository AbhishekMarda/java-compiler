package javacompiler.riscvtranslator.RiscV;

import java.util.ArrayList;

public class Program {
    Prologue prologue = new Prologue();
    ArrayList<Function> functions = new ArrayList<>();
    Epilogue epilogue = new Epilogue();

    public Program(ArrayList<Function> functions) {
        this.functions = functions;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(prologue.toString()).append("\n");
        
        for (Function function : functions) {
            sb.append(function.toString()).append("\n");
        }
        
        sb.append(epilogue.toString()).append("\n");
        return sb.toString();
    }
}
