package javacompiler.translator.Visitors.SparrowTraversal;

import java.util.ArrayList;

import javacompiler.translator.Helpers.ClassInfo;
import javacompiler.translator.Helpers.Gensym;
import javacompiler.translator.Helpers.MethodInfo;
import javacompiler.translator.Helpers.SparrowIdentifierType;
import cs132.IR.sparrow.*;
import cs132.IR.token.FunctionName;
import cs132.IR.token.Identifier;
import cs132.IR.token.Label;

/*
 * Helper class to get the sparrow code for getting a function
 */
public class OffsetCodeGenerator {

    /*
     * if0 classVar goto nullx
     * goto l_x+1 
     * nullx : error("null pointer")
     * l_x+1: 
     * target = [classVar + offset]
     * 
     */
    public static ArrayList<Instruction> getMemberOffsetGivenClass(String classVar, String targetVar, String memberName, ClassInfo classInfo) {
        ArrayList<Instruction> instructions = new ArrayList<>();
        int offset = classInfo.getMemberOffset(memberName);
        String nullLabel = Gensym.gensym(SparrowIdentifierType.NULLTYPE);
        String label = Gensym.gensym(SparrowIdentifierType.LABELTYPE);
        Instruction i1 = new IfGoto(new Identifier(classVar), new Label(nullLabel));
        Instruction i2 = new Goto(new Label(label));
        Instruction i3 = new LabelInstr(new Label(nullLabel)); 
        Instruction i4 = new ErrorMessage(ErrorMessages.NULL_POINTER);
        Instruction i5 = new LabelInstr(new Label(label));
        Instruction i6 = new Load( new Identifier(targetVar), new Identifier(classVar), offset);

        instructions.add(i1);
        instructions.add(i2);
        instructions.add(i3);
        instructions.add(i4);
        instructions.add(i5);
        instructions.add(i6);

        return instructions;
    }

    /*
     * if0 classVar goto nullx
     * goto l_x+1
     * nullx : error("null pointer")
     * l_x+1:
     * vmt_x+2 = [classVar + 0]
     * target = [vmt_x+2 + offset]
     */
    public static ArrayList<Instruction> getMethodOffsetGivenClass(String classVar, String targetVar, String methodName, ClassInfo classInfo) {
        ArrayList<Instruction> instructions = new ArrayList<>();
        int offset = classInfo.getMethodOffset(methodName);
        String nullLabel = Gensym.gensym(SparrowIdentifierType.NULLTYPE);
        String label = Gensym.gensym(SparrowIdentifierType.LABELTYPE);
        String vmtVar = Gensym.gensym(SparrowIdentifierType.VTABLE_TYPE);
        Instruction i1 = new IfGoto(new Identifier(classVar), new Label(nullLabel));
        Instruction i2 = new Goto(new Label(label));
        Instruction i3 = new LabelInstr(new Label(nullLabel)); 
        Instruction i4 = new ErrorMessage(ErrorMessages.NULL_POINTER);
        Instruction i5 = new LabelInstr(new Label(label));
        Instruction i6 = new Load( new Identifier(vmtVar), new Identifier(classVar), 0);
        Instruction i7 = new Load( new Identifier(methodName), new Identifier(vmtVar), offset);

        instructions.add(i1);
        instructions.add(i2);
        instructions.add(i3);
        instructions.add(i4);
        instructions.add(i5);
        instructions.add(i6);
        instructions.add(i7);

        return instructions;
    }

    /*
     * var1 = membersize+1
     * target = alloc(var1)
     * var1 = methodsize
     * vmt_var = alloc(var1) 
     *  <assign functions to dereferenced vmt>
     * [target + 0] = vmt_var
     * 
     */
    public static ArrayList<Instruction> generateClassAllocation(String targetVar, ClassInfo classInfo) {
        ArrayList<Instruction> instructions = new ArrayList<>();

        int layer1_size = classInfo.getFirstLevelSize();
        int layer2_size = classInfo.getSecondLevelSize();

        Identifier vmt = new Identifier(Gensym.gensym(SparrowIdentifierType.VTABLE_TYPE));
        Identifier targetIdentifier = new Identifier(targetVar);
        Identifier var1 = new Identifier(Gensym.gensym(SparrowIdentifierType.VARTYPE));
        Identifier var2 = new Identifier(Gensym.gensym(SparrowIdentifierType.VARTYPE));
        ArrayList<MethodInfo> methods = classInfo.getMethods();

        Instruction i1 = new Move_Id_Integer(var1, layer1_size);
        Instruction i2 = new Alloc(targetIdentifier, var1);
        Instruction i3 = new Move_Id_Integer(var1, layer2_size);
        Instruction i4 = new Alloc(vmt, var1);

        instructions.add(i1);
        instructions.add(i2);
        instructions.add(i3);
        instructions.add(i4);

        for(MethodInfo method : methods) {
            int funcOffest = classInfo.getMethodOffset(method.name);
            String sparrowMethodName = classInfo.getSparrowFunctionName(method.name);
            Instruction i5 = new Move_Id_FuncName(var2, new FunctionName(sparrowMethodName));
            Instruction i6 = new Store(vmt, funcOffest, var2);
            instructions.add(i5);
            instructions.add(i6);
        }

        Instruction i7 = new Store(targetIdentifier, 0, vmt);
        instructions.add(i7);

        return instructions;
    }

}
