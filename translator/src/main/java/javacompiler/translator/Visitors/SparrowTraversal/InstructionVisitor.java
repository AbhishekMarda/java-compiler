package javacompiler.translator.Visitors.SparrowTraversal;

import java.util.ArrayList;
import java.util.Arrays;

import javacompiler.translator.Helpers.ClassInfo;
import javacompiler.translator.Helpers.Constants;
import javacompiler.translator.Helpers.Gensym;
import javacompiler.translator.Helpers.MiniJavaType;
import javacompiler.translator.Helpers.SparrowIdentifierType;
import javacompiler.translator.Helpers.SparrowType;
import javacompiler.translator.Helpers.TraversalStruct;
import javacompiler.translator.Visitors.ClassInfoVisitor;
import cs132.IR.sparrow.Add;
import cs132.IR.sparrow.Alloc;
import cs132.IR.sparrow.Call;
import cs132.IR.sparrow.ErrorMessage;
import cs132.IR.sparrow.Goto;
import cs132.IR.sparrow.IfGoto;
import cs132.IR.sparrow.Instruction;
import cs132.IR.sparrow.LabelInstr;
import cs132.IR.sparrow.LessThan;
import cs132.IR.sparrow.Load;
import cs132.IR.sparrow.Move_Id_Id;
import cs132.IR.sparrow.Move_Id_Integer;
import cs132.IR.sparrow.Multiply;
import cs132.IR.sparrow.Print;
import cs132.IR.sparrow.Store;
import cs132.IR.sparrow.Subtract;
import cs132.IR.token.Label;
import cs132.IR.token.Identifier;
import cs132.minijava.syntaxtree.AllocationExpression;
import cs132.minijava.syntaxtree.AndExpression;
import cs132.minijava.syntaxtree.ArrayAllocationExpression;
import cs132.minijava.syntaxtree.ArrayAssignmentStatement;
import cs132.minijava.syntaxtree.ArrayLength;
import cs132.minijava.syntaxtree.ArrayLookup;
import cs132.minijava.syntaxtree.AssignmentStatement;
import cs132.minijava.syntaxtree.Block;
import cs132.minijava.syntaxtree.BracketExpression;
import cs132.minijava.syntaxtree.CompareExpression;
import cs132.minijava.syntaxtree.Expression;
import cs132.minijava.syntaxtree.ExpressionList;
import cs132.minijava.syntaxtree.ExpressionRest;
import cs132.minijava.syntaxtree.FalseLiteral;
import cs132.minijava.syntaxtree.FormalParameter;
import cs132.minijava.syntaxtree.FormalParameterList;
import cs132.minijava.syntaxtree.FormalParameterRest;
import cs132.minijava.syntaxtree.IfStatement;
import cs132.minijava.syntaxtree.IntegerLiteral;
import cs132.minijava.syntaxtree.MessageSend;
import cs132.minijava.syntaxtree.MinusExpression;
import cs132.minijava.syntaxtree.Node;
import cs132.minijava.syntaxtree.NotExpression;
import cs132.minijava.syntaxtree.PlusExpression;
import cs132.minijava.syntaxtree.PrimaryExpression;
import cs132.minijava.syntaxtree.PrintStatement;
import cs132.minijava.syntaxtree.Statement;
import cs132.minijava.syntaxtree.ThisExpression;
import cs132.minijava.syntaxtree.TimesExpression;
import cs132.minijava.syntaxtree.TrueLiteral;
import cs132.minijava.syntaxtree.VarDeclaration;
import cs132.minijava.syntaxtree.WhileStatement;
import cs132.minijava.visitor.GJDepthFirst;

/*
 * Covers variable declarations, statements, and expressions
 * 
 * Convention for expressions:
 * The caller is responsible for setting the target variable using gensym
 * The caller is also responsible for udpating / flushing the target once done
 * Once the callee is finished executing, Gensym.gensym() will return the next available unused variable name
 * Callee must not change its current target
 * Callee must update the final variable's type to the scope
 */
public class InstructionVisitor extends GJDepthFirst<ArrayList<Instruction>, TraversalStruct>{
    
    // Statements
    @Override
    public ArrayList<Instruction> visit(Statement n, TraversalStruct traversalStruct) {
        return n.f0.choice.accept(this, traversalStruct);
    }

    /*
     * print(expr)
     */
    @Override
    public ArrayList<Instruction> visit(PrintStatement n, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();

        String exprResult = Gensym.gensym(SparrowIdentifierType.VARTYPE);
        Identifier exprResulIdentifier = new Identifier(exprResult);
        traversalStruct.setTargetVariable(exprResult);
        instructions.addAll(n.f2.accept(this, traversalStruct));
        
        Instruction i1 = new Print(exprResulIdentifier);

        instructions.add(i1);

        return instructions;
    }

    /*
     * existing_var = expr
     *  
     * OR 
     * 
     * if local var:
     * 
     * [this + offset] = expr
     */
    @Override
    public ArrayList<Instruction> visit(AssignmentStatement n, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();

        String exprResult = Gensym.gensym(SparrowIdentifierType.VARTYPE);
        Identifier exprResulIdentifier = new Identifier(exprResult);
        traversalStruct.setTargetVariable(exprResult);
        instructions.addAll(n.f2.accept(this, traversalStruct));

        String lhs = n.f0.f0.tokenImage;
        
        if (traversalStruct.getScope().isLocalVar(lhs)){
            String lhsSparrow = traversalStruct.getScope().getSparrowVar(lhs);
            Instruction i1 = new Move_Id_Id(new Identifier(lhsSparrow), exprResulIdentifier);
            instructions.add(i1);
        } 
        else {
            int offset = traversalStruct.getScope().getCurrClass().getMemberOffset(lhs);
            Instruction i1 = new Store(new Identifier("this"), offset, exprResulIdentifier);
            instructions.add(i1);
        }

        return instructions;
    }

    @Override
    public ArrayList<Instruction> visit(Block n, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();

        if (n.f1.present()) {
            for (Node statement : n.f1.nodes) {
                instructions.addAll(statement.accept(this, traversalStruct));
            }
        }

        return instructions;
    }

    /*
     * IF member variable
     * id = [this + offset]
     * 
     *  if0 id goto null1
     * goto l1
     * null1:
     * error("null pointer")
     * l1:
     * var0 = [id + 0] // get the size
     * var1 = expr1 < var0         // 0 if expr1 >= var0, bad
     * if0 var1 goto l2:
     * var0 = 0
     * var1 = expr1 < var0            // 0 if expr1 >= 0, good
     * if0 var1 goto l3
     * l2:
     * error("out of bounds error")
     * l3:
     * var2 = 4
     * var3 = 1
     * var3 = var3 + expr1      // get the right index
     * var3 = var2 * var3       // multiply by 4 to get the right offset
     * var4 = id + var3      // base + offset
     * [var4 + 0] = expr2
     */
    @Override
    public ArrayList<Instruction> visit(ArrayAssignmentStatement n, TraversalStruct traversalStruct) {
        // identifier [ expr1 ] = expr2
        ArrayList<Instruction> instructions = new ArrayList<>();

        String expr1eval = Gensym.gensym(SparrowIdentifierType.VARTYPE);
        Identifier expr1Identifier = new Identifier(expr1eval);
        traversalStruct.setTargetVariable(expr1eval);
        instructions.addAll(n.f2.accept(this, traversalStruct));

        String expr2eval = Gensym.gensym(SparrowIdentifierType.VARTYPE);
        Identifier expr2Identifier = new Identifier(expr2eval);
        traversalStruct.setTargetVariable(expr2eval);
        instructions.addAll(n.f5.accept(this, traversalStruct));      
        
        String miniJavaVarName = n.f0.f0.tokenImage;
        
        String sparrowId = null;
        if (traversalStruct.getScope().isLocalVar(miniJavaVarName)) {
            sparrowId = traversalStruct.getScope().getSparrowVar(miniJavaVarName);
        } 
        else {
            // account for the case if it is a member variable
            int offset = traversalStruct.getScope().getCurrClass().getMemberOffset(miniJavaVarName);
            sparrowId = Gensym.gensym(SparrowIdentifierType.VARTYPE);
            instructions.add(new Load(new Identifier(sparrowId), new Identifier("this"), offset));
        }
        
        Identifier id = new Identifier(sparrowId);
        Identifier var0 = new Identifier(Gensym.gensym(SparrowIdentifierType.VARTYPE));
        Identifier var1 = new Identifier(Gensym.gensym(SparrowIdentifierType.VARTYPE));
        Identifier var2 = new Identifier(Gensym.gensym(SparrowIdentifierType.VARTYPE));
        Identifier var3 = new Identifier(Gensym.gensym(SparrowIdentifierType.VARTYPE));
        Identifier var4 = new Identifier(Gensym.gensym(SparrowIdentifierType.VARTYPE));
        Label l1 = new Label(Gensym.gensym(SparrowIdentifierType.LABELTYPE));
        Label l2 = new Label(Gensym.gensym(SparrowIdentifierType.LABELTYPE));
        Label l3 = new Label(Gensym.gensym(SparrowIdentifierType.LABELTYPE));
        Label nullLabel = new Label(Gensym.gensym(SparrowIdentifierType.NULLTYPE));
        


        Instruction i1 = new IfGoto(id, nullLabel);
        Instruction i2 = new Goto(l1);
        Instruction i3 = new LabelInstr(nullLabel);
        Instruction i4 = new ErrorMessage(ErrorMessages.NULL_POINTER);
        Instruction i5 = new LabelInstr(l1);
        Instruction i6 = new Load(var0, id, 0);
        Instruction i7 = new LessThan(var1, expr1Identifier, var0);
        Instruction i8 = new IfGoto(var1, l2);
        Instruction i8point5 = new Move_Id_Integer(var0, 0);
        Instruction i9 = new LessThan(var1, expr1Identifier, var0);
        Instruction i9point5 = new IfGoto(var1, l3);
        Instruction i10 = new LabelInstr(l2);
        Instruction i11 = new ErrorMessage(ErrorMessages.OUT_OF_BOUNDS);
        Instruction i12 = new LabelInstr(l3);
        Instruction i13 = new Move_Id_Integer(var2, Constants.DATA_SIZE);
        Instruction i14 = new Move_Id_Integer(var3, 1);
        Instruction i15 = new Add(var3, var3, expr1Identifier);
        Instruction i16 = new Multiply(var3, var2, var3);
        Instruction i17 = new Add(var4, id, var3);
        Instruction i18 = new Store(var4, 0, expr2Identifier);

        
        instructions.addAll(Arrays.asList(i1,i2,i3,i4,i5,i6,i7,i8,i8point5,i9,i9point5,i10,i11,i12,i13,i14,i15,i16,i17,i18));

        return instructions;
    }

    /*
     * if0 expr goto l1
     * true_stmt
     * goto l2
     * l1: 
     * false_stmt
     * l2:
     */
    @Override
    public ArrayList<Instruction> visit(IfStatement n, TraversalStruct traversalStruct) {
        // "if" "(" expr1 ")" Statement "else" Statement
        ArrayList<Instruction> instructions = new ArrayList<>();
        String expr1eval = Gensym.gensym(SparrowIdentifierType.VARTYPE);
        Identifier expr1Identifier = new Identifier(expr1eval);
        traversalStruct.setTargetVariable(expr1eval);
        instructions.addAll(n.f2.accept(this, traversalStruct));

        Label l1 = new Label(Gensym.gensym(SparrowIdentifierType.LABELTYPE));
        Label l2 = new Label(Gensym.gensym(SparrowIdentifierType.LABELTYPE));
        
        Instruction i1 = new IfGoto(expr1Identifier, l1);
        Instruction i2 = new Goto(l2);
        Instruction i3 = new LabelInstr(l1);
        Instruction i4 = new LabelInstr(l2);

        ArrayList<Instruction> trueInstructions = n.f4.accept(this, traversalStruct);
        ArrayList<Instruction> falseInstructions = n.f6.accept(this, traversalStruct);

        instructions.add(i1);
        instructions.addAll(trueInstructions);
        instructions.add(i2);
        instructions.add(i3);
        instructions.addAll(falseInstructions);
        instructions.add(i4);

        return instructions;
    }

    /* 
     * l1:
     * <expr instructions>
     * if0 exprEval goto l2
     * statement instructions
     * goto l1
     * l2:
     */
    @Override
    public ArrayList<Instruction> visit(WhileStatement n, TraversalStruct traversalStruct) {
        // while" "(" expr ")" Statement

        ArrayList<Instruction> instructions = new ArrayList<>();
        String expr1eval = Gensym.gensym(SparrowIdentifierType.VARTYPE);
        Identifier expr1Identifier = new Identifier(expr1eval);
        traversalStruct.setTargetVariable(expr1eval);

        ArrayList<Instruction> exprInstructions = n.f2.accept(this, traversalStruct);

        ArrayList<Instruction> stmtInstructions = n.f4.accept(this, traversalStruct);

        Label l1 = new Label(Gensym.gensym(SparrowIdentifierType.LABELTYPE));
        Label l2 = new Label(Gensym.gensym(SparrowIdentifierType.LABELTYPE));

        Instruction i1 = new LabelInstr(l1);
        Instruction i2 = new IfGoto(expr1Identifier, l2);
        Instruction i3 = new Goto(l1);
        Instruction i4 = new LabelInstr(l2);

        instructions.add(i1);
        instructions.addAll(exprInstructions);
        instructions.add(i2);
        instructions.addAll(stmtInstructions);
        instructions.add(i3);
        instructions.add(i4);

        return instructions;
    }



    // Formal Parameter Declarations
    @Override
    public ArrayList<Instruction> visit(FormalParameterList n, TraversalStruct traversalStruct) {
        ArrayList<Instruction> list1 = n.f0.accept(this, traversalStruct);
        if (n.f1.present()) {
            for (Node restNode : n.f1.nodes) {
                list1.addAll(restNode.accept(this, traversalStruct));
            }
        }

        return list1;
    }

    /*
     * varx = arg
     * 
     * we do not have a target variable created in this case
     */
    @Override
    public ArrayList<Instruction> visit(FormalParameter n, TraversalStruct traversalStruct) {
        MiniJavaType parameterType = n.f0.accept(new ClassInfoVisitor(), traversalStruct.getScope().getCurrClass());
        String argName = traversalStruct.getScope().getArgumentName(n.f1.f0.tokenImage);

        String sparrowVar = traversalStruct.getScope().addVar(argName, parameterType, traversalStruct.getOffsetCollector());

        Instruction i1 = new Move_Id_Id(new Identifier(sparrowVar), new Identifier(argName));
        
        ArrayList<Instruction> instructions = new ArrayList<>();
        instructions.add(i1);

        return instructions;
    }

    @Override
    public ArrayList<Instruction> visit(FormalParameterRest n, TraversalStruct traversalStruct) {
        return n.f1.accept(this, traversalStruct);
    }

    // Var declarations

    /*
     * new_var = 0 
     * 
     * all sparrow variables are initialized to zero
     * 
     * we do not have a target variable for var declarations
     */
    @Override
    public ArrayList<Instruction> visit(VarDeclaration n, TraversalStruct traversalStruct) {
        MiniJavaType varMiniJavaType = n.f0.accept(new ClassInfoVisitor(), traversalStruct.getScope().getCurrClass()); 
        String varName = n.f1.f0.tokenImage;
        
        String sparrowVar = traversalStruct.getScope().addVar(varName, varMiniJavaType, traversalStruct.getOffsetCollector());

        Instruction i1 = new Move_Id_Integer(new Identifier(sparrowVar), 0);

        ArrayList<Instruction> instructions = new ArrayList<>();
        instructions.add(i1);

        return instructions;
    }


    // Expressions

    @Override
    public ArrayList<Instruction> visit(Expression n, TraversalStruct traversalStruct) {
        return n.f0.choice.accept(this, traversalStruct);
    }

    @Override
    public ArrayList<Instruction> visit(ExpressionList n, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();
        ArrayList<String> targets = traversalStruct.getTargetVariableList();
        
        String target = Gensym.gensym(SparrowIdentifierType.VARTYPE);
        traversalStruct.setTargetVariable(target);
        instructions.addAll(n.f0.accept(this, traversalStruct));

        targets.add(target);
        traversalStruct.setTargetVariableList(targets);

        // run on the rest, collect the targets, return to MessageSend
        if (n.f1.present()) {
            for (Node expressionRest : n.f1.nodes) {
                instructions.addAll(expressionRest.accept(this, traversalStruct));
            }
        }

        return instructions;
    }

    @Override
    public ArrayList<Instruction> visit(ExpressionRest n, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();

        String target = Gensym.gensym(SparrowIdentifierType.VARTYPE);
        traversalStruct.setTargetVariable(target);
        traversalStruct.getTargetVariableList().add(target);
        instructions.addAll(n.f1.accept(this, traversalStruct));

        return instructions;
    }

    /*<gather the variables to make the call by evaluating the expressions>
     * if0 eval goto null1
     *  vmt_0 = [eval + 0]
     * var0 = [vmt_0 + offset]    // get the function
     * target = call var0(...)
     * goto l1
     * null1:
     * error("null pointer")
     * l1:
     */
    @Override
    public ArrayList<Instruction> visit(MessageSend n, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();
        // save callee target information
        String targetString = traversalStruct.getTargetVariable();
        Identifier targetIdentifier = new Identifier(targetString);

        // evaluate primary expression
        String eval1 = Gensym.gensym(SparrowIdentifierType.VARTYPE);
        traversalStruct.setTargetVariable(eval1);
        Identifier eval1Identifier = new Identifier(eval1);
        ArrayList<Instruction> primaryInstructions = n.f0.accept(this, traversalStruct);

        // get the class of the evaluated primary expression
        // note that this can't be a primitive type, so we don't need to worry about that 
        MiniJavaType primaryExprType = traversalStruct.getScope().getSparrowVarMiniJavaType(eval1);
        ClassInfo classInfo = traversalStruct.getOffsetCollector().getClassInfo(primaryExprType.getName());
        
        // get the offset
        String funcName = n.f2.f0.tokenImage;
        int offset = classInfo.getMethodOffset(funcName);
        
        // get return type
        MiniJavaType methodReturnType = classInfo.getMethodReturnType(funcName);

        // execute the expressions
        // the return variables will be stored in the array in the traversal struct
        ArrayList<String> prevTargets = traversalStruct.getTargetVariableList();
        traversalStruct.setTargetVariableList(new ArrayList<>());
        ArrayList<Instruction> paramInstructions = new ArrayList<>();
        if (n.f4.present()) {
            paramInstructions = n.f4.node.accept(this, traversalStruct);
        }

        // convert array of strings to array of identifiers
        ArrayList<Identifier> params = new ArrayList<>();
        for (String paramString : traversalStruct.getTargetVariableList()) {
            params.add(new Identifier(paramString));
        }

        // tack the calling class to the beginning of the parameters to fill in for "this"
        params.add(0, eval1Identifier);

        // write the instructions

        Label nullLabel = new Label(Gensym.gensym(SparrowIdentifierType.NULLTYPE));
        Label l1 = new Label(Gensym.gensym(SparrowIdentifierType.LABELTYPE));
        Identifier vmt = new Identifier(Gensym.gensym(SparrowIdentifierType.VTABLE_TYPE));
        Identifier var = new Identifier(Gensym.gensym(SparrowIdentifierType.VARTYPE));

        Instruction i1 = new IfGoto(eval1Identifier, nullLabel);
        Instruction i2 = new Load(vmt, eval1Identifier, 0);
        Instruction i3 = new Load(var, vmt, offset);
        Instruction i4 = new Call(targetIdentifier, var, params);
        Instruction i5 = new Goto(l1);
        Instruction i6 = new LabelInstr(nullLabel);
        Instruction i7 = new ErrorMessage(ErrorMessages.NULL_POINTER);
        Instruction i8 = new LabelInstr(l1);

        instructions.addAll(paramInstructions);
        instructions.addAll(primaryInstructions);
        instructions.add(i1);
        instructions.add(i2);
        instructions.add(i3);
        instructions.add(i4);
        instructions.add(i5);
        instructions.add(i6);
        instructions.add(i7);
        instructions.add(i8);

        traversalStruct.setTargetVariableList(prevTargets);
        traversalStruct.setTargetVariable(targetString);
        traversalStruct.setTargetMiniJavaType(methodReturnType);

        return instructions;
    }


    /*
     * IF expr1 IS INT
     * 
     *  var_x = expr1
     * var_x+1 = expr2
     * target = var_x + var_x+1
     * 
     * OR 
     * IF expr1 is HEAP ADDRESS
     * 
     * var_x = expr1
     * var_x+1 = expr2
     * var_x+2 = 4
     * var_x+1 = var_x+1 * var_x+2
     * target = var_x + var_x+1
     */
    @Override
    public ArrayList<Instruction> visit(PlusExpression n, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();
        
        String target = traversalStruct.getTargetVariable();
        
        String expr1Eval = Gensym.gensym(SparrowIdentifierType.VARTYPE);
        Identifier expr1EvalIdentifier = new Identifier(expr1Eval);
        traversalStruct.setTargetVariable(expr1Eval);
        instructions.addAll(n.f0.accept(this, traversalStruct));

        String expr2Eval = Gensym.gensym(SparrowIdentifierType.VARTYPE);
        Identifier expr2EvalIdentifier = new Identifier(expr2Eval);
        traversalStruct.setTargetVariable(expr2Eval);
        instructions.addAll(n.f2.accept(this, traversalStruct));

        Identifier targIdentifier = new Identifier(target);

        // if we are adding to a heap address, we need to multiply addition by 4
        if (traversalStruct.getScope().getSparrowVarSparrowType(expr1Eval).equals(SparrowType.ADDRESS_TYPE)) {
            Identifier val4 = new Identifier(Gensym.gensym(SparrowIdentifierType.VARTYPE));
            Instruction i1 = new Move_Id_Integer(val4, Constants.DATA_SIZE);
            Instruction i2 = new Multiply(expr2EvalIdentifier, expr2EvalIdentifier, val4);

            instructions.add(i1);
            instructions.add(i2);
        }

        Instruction iEnd= new Add(targIdentifier, expr1EvalIdentifier, expr2EvalIdentifier);
        instructions.add(iEnd);
        traversalStruct.setTargetVariable(target);

        // the target variable type should be the same as expr1eval type;; int if int, address if address
        MiniJavaType targetMiniJavaType = traversalStruct.getScope().getSparrowVarMiniJavaType(expr1Eval);
        traversalStruct.setTargetMiniJavaType(targetMiniJavaType);
        return instructions;
    }

    /*
     * var_x = expr1
     * var_x+1 = expr2
     * target = var_x - var_x+1
     */

    @Override
    public ArrayList<Instruction> visit(MinusExpression n, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();
        
        String target = traversalStruct.getTargetVariable();
        
        String expr1Eval = Gensym.gensym(SparrowIdentifierType.VARTYPE);
        traversalStruct.setTargetVariable(expr1Eval);
        instructions.addAll(n.f0.accept(this, traversalStruct));

        String expr2Eval = Gensym.gensym(SparrowIdentifierType.VARTYPE);
        traversalStruct.setTargetVariable(expr2Eval);
        instructions.addAll(n.f2.accept(this, traversalStruct));

        Identifier targIdentifier = new Identifier(target);
        Instruction i1 = new Subtract(targIdentifier, new Identifier(expr1Eval), new Identifier(expr2Eval));

        instructions.add(i1);
        traversalStruct.setTargetVariable(target);
        traversalStruct.setTargetMiniJavaType(MiniJavaType.INTEGER);
        return instructions;
    }

    /*
     * var_x = expr1
     * var_x+1 = expr2
     * target = var_x * var_x+1
     */

     @Override
     public ArrayList<Instruction> visit(TimesExpression n, TraversalStruct traversalStruct) {
         ArrayList<Instruction> instructions = new ArrayList<>();
        
         String target = traversalStruct.getTargetVariable();
         
         String expr1Eval = Gensym.gensym(SparrowIdentifierType.VARTYPE);
         traversalStruct.setTargetVariable(expr1Eval);
         instructions.addAll(n.f0.accept(this, traversalStruct));
 
         String expr2Eval = Gensym.gensym(SparrowIdentifierType.VARTYPE);
         traversalStruct.setTargetVariable(expr2Eval);
         instructions.addAll(n.f2.accept(this, traversalStruct));
 
         Identifier targIdentifier = new Identifier(target);
         Instruction i1 = new Multiply(targIdentifier, new Identifier(expr1Eval), new Identifier(expr2Eval));
 
         instructions.add(i1);
         traversalStruct.setTargetVariable(target);
         traversalStruct.setTargetMiniJavaType(MiniJavaType.INTEGER);
         return instructions;
     }

     /*
     * var_x = expr1
     * var_x+1 = expr2
     * target = var_x < var_x+1
     */

     @Override
     public ArrayList<Instruction> visit(CompareExpression n, TraversalStruct traversalStruct) {
         ArrayList<Instruction> instructions = new ArrayList<>();
        
         String target = traversalStruct.getTargetVariable();
         
         String expr1Eval = Gensym.gensym(SparrowIdentifierType.VARTYPE);
         traversalStruct.setTargetVariable(expr1Eval);
         instructions.addAll(n.f0.accept(this, traversalStruct));
 
         String expr2Eval = Gensym.gensym(SparrowIdentifierType.VARTYPE);
         traversalStruct.setTargetVariable(expr2Eval);
         instructions.addAll(n.f2.accept(this, traversalStruct));
 
         Identifier targIdentifier = new Identifier(target);
         Instruction i1 = new LessThan(targIdentifier, new Identifier(expr1Eval), new Identifier(expr2Eval));
 
         instructions.add(i1);
         traversalStruct.setTargetVariable(target);
         traversalStruct.setTargetMiniJavaType(MiniJavaType.INTEGER);
         return instructions;
     }     

     /*
      * <expr1 instr>
      * if0 expr1 goto l1
      * <expr2 instr> 
      * if0 expr2 goto l1
      * target = 1
      * goto l2
      * l1:
      * target = 0
      * l2:
      */
     @Override
     public ArrayList<Instruction> visit(AndExpression n, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();
        
        String target = traversalStruct.getTargetVariable();
        
        String expr1Eval = Gensym.gensym(SparrowIdentifierType.VARTYPE);
        traversalStruct.setTargetVariable(expr1Eval);
        ArrayList<Instruction> expr1Instructions = n.f0.accept(this, traversalStruct);

        String expr2Eval = Gensym.gensym(SparrowIdentifierType.VARTYPE);
        traversalStruct.setTargetVariable(expr2Eval);
        ArrayList<Instruction> expr2Instructions = n.f2.accept(this, traversalStruct);

        Identifier targetIdentifier = new Identifier(target);
        Identifier expr1Identifier = new Identifier(expr1Eval);
        Identifier expr2Identifier = new Identifier(expr2Eval);

        Label l1 = new Label(Gensym.gensym(SparrowIdentifierType.LABELTYPE));
        Label l2 = new Label(Gensym.gensym(SparrowIdentifierType.LABELTYPE));

        Instruction i1 = new IfGoto(expr1Identifier, l1);
        Instruction i2 = new IfGoto(expr2Identifier, l1);
        Instruction i3 = new Move_Id_Integer(targetIdentifier, 1);
        Instruction i4 = new Goto(l2);
        Instruction i5 = new LabelInstr(l1);
        Instruction i6 = new Move_Id_Integer(targetIdentifier, 0);
        Instruction i7 = new LabelInstr(l2);

        instructions.addAll(expr1Instructions);
        instructions.add(i1);
        instructions.addAll(expr2Instructions);
        instructions.add(i2);
        instructions.add(i3);
        instructions.add(i4);
        instructions.add(i5);
        instructions.add(i6);
        instructions.add(i7);

        traversalStruct.setTargetVariable(target);
        traversalStruct.setTargetMiniJavaType(MiniJavaType.BOOLEAN);
        return instructions;
     }

    
    /*
     * if0 epxr1 goto null1
     * var_x0 = [expr1 + 0]         // get the size
     * var_x1 = expr2 < var_x0      // 0 if index >= size
     * if0 var_x1 goto l1       
     * var_x0 = 0
     * var_x1 = expr2 < var_x0           // 0 if expr2 >= 0
     * if0 var_x1 goto l2
     * goto l1
     * l2:
     * var_x2 = 4
     * var_x3 = 1
     * var_x3 = expr2 + var_x3      // increment 1 for the size
     * var_x4 = var_x3 * var_x2     // multiply by 4 to get offset
     * var_x4 = expr1 + var_x4      // add offset to base
     * target = [var_x4 + 0]
     * goto l3
     * null1:
     * error("null pointer")
     * l1:
     * error("index out of bounds")
     * l3:
     * 
     */
     @Override
    public ArrayList<Instruction> visit(ArrayLookup n, TraversalStruct traversalStruct) {
        // PrimaryExpr [ PrimaryExpr ]
        
        ArrayList<Instruction> instructions = new ArrayList<>();
        String target = traversalStruct.getTargetVariable();
        
        String expr1Eval = Gensym.gensym(SparrowIdentifierType.VARTYPE);
        traversalStruct.setTargetVariable(expr1Eval);
        instructions.addAll(n.f0.accept(this, traversalStruct));

        String expr2Eval = Gensym.gensym(SparrowIdentifierType.VARTYPE);
        traversalStruct.setTargetVariable(expr2Eval);
        instructions.addAll(n.f2.accept(this, traversalStruct));

        Identifier targetIdentifier = new Identifier(target);
        Identifier expr1Identifier = new Identifier(expr1Eval);
        Identifier expr2Identifier = new Identifier(expr2Eval);
        Identifier var0 = new Identifier(Gensym.gensym(SparrowIdentifierType.VARTYPE));
        Identifier var1 = new Identifier(Gensym.gensym(SparrowIdentifierType.VARTYPE));
        Identifier var2 = new Identifier(Gensym.gensym(SparrowIdentifierType.VARTYPE));
        Identifier var3 = new Identifier(Gensym.gensym(SparrowIdentifierType.VARTYPE));
        Identifier var4 = new Identifier(Gensym.gensym(SparrowIdentifierType.VARTYPE));
        Label nullLabel = new Label(Gensym.gensym(SparrowIdentifierType.NULLTYPE));
        Label l1 = new Label(Gensym.gensym(SparrowIdentifierType.LABELTYPE));
        Label l2 = new Label(Gensym.gensym(SparrowIdentifierType.LABELTYPE));
        Label l3 = new Label(Gensym.gensym(SparrowIdentifierType.LABELTYPE));

        Instruction i1 = new IfGoto(expr1Identifier, nullLabel);
        Instruction i2 = new Load(var0, expr1Identifier, 0);
        Instruction i3 = new LessThan(var1, expr2Identifier, var0);
        Instruction i4 = new IfGoto(var1, l1);
        Instruction i4point1 = new Move_Id_Integer(var0, 0);
        Instruction i4point2 = new LessThan(var1, expr2Identifier, var0);
        Instruction i4point3 = new IfGoto(var1, l2);
        Instruction i4point4 = new Goto(l1);
        Instruction i4point5 = new LabelInstr(l2);
        Instruction i5 = new Move_Id_Integer(var2, Constants.DATA_SIZE);
        Instruction i6 = new Move_Id_Integer(var3, 1);
        Instruction i7 = new Add(var3, expr2Identifier, var3);
        Instruction i8 = new Multiply(var4, var3, var2);
        Instruction i9 = new Add(var4, expr1Identifier, var4); 
        Instruction i10 = new Load(targetIdentifier, var4, 0); 
        Instruction i11 = new Goto(l3);
        Instruction i12 = new LabelInstr(nullLabel);
        Instruction i13 = new ErrorMessage(ErrorMessages.NULL_POINTER);
        Instruction i14 = new LabelInstr(l1);
        Instruction i15 = new ErrorMessage(ErrorMessages.OUT_OF_BOUNDS);
        Instruction i16 = new LabelInstr(l3);

        instructions.addAll(Arrays.asList(i1,i2,i3,i4,i4point1, i4point2, i4point3,i4point4,i4point5,i5,i6,i7,i8,i9,i10,i11,i12,i13,i14,i15,i16));

        traversalStruct.setTargetVariable(target);
        traversalStruct.setTargetMiniJavaType(MiniJavaType.INTEGER); // only case of an array lookup
        return instructions;
    }

    /*
     * if0 expr goto null1
     * target = [expr + 0]
     * goto l1
     * null1:
     * error("null pointer")
     * l1:
     */
    @Override
    public ArrayList<Instruction> visit(ArrayLength n, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();
        String target = traversalStruct.getTargetVariable();
        
        String expr1Eval = Gensym.gensym(SparrowIdentifierType.VARTYPE);
        traversalStruct.setTargetVariable(expr1Eval);
        instructions.addAll(n.f0.accept(this, traversalStruct));

        Identifier targetIdentifier = new Identifier(target);
        Identifier expr1Identifier = new Identifier(expr1Eval);

        Label nullLabel = new Label(Gensym.gensym(SparrowIdentifierType.NULLTYPE));
        Label l1 = new Label(Gensym.gensym(SparrowIdentifierType.LABELTYPE));

        Instruction i1 = new IfGoto(expr1Identifier, nullLabel);
        Instruction i2 = new Load(targetIdentifier, expr1Identifier, 0);
        Instruction i3 = new Goto(l1);
        Instruction i4=  new LabelInstr(nullLabel);
        Instruction i5 = new ErrorMessage(ErrorMessages.NULL_POINTER);
        Instruction i6 = new LabelInstr(l1);

        instructions.add(i1);
        instructions.add(i2);
        instructions.add(i3);
        instructions.add(i4);
        instructions.add(i5);
        instructions.add(i6);

        traversalStruct.setTargetVariable(target);
        traversalStruct.setTargetMiniJavaType(MiniJavaType.INTEGER); // only type for size
        return instructions;
    }



    // Primary expressions

    @Override
    public ArrayList<Instruction> visit(PrimaryExpression n, TraversalStruct traversalStruct) {
        return n.f0.choice.accept(this, traversalStruct);
    }

    /*
     * vk = c
     */
    @Override
    public ArrayList<Instruction> visit(IntegerLiteral n, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();
        
        Instruction i1 = new Move_Id_Integer(
            new Identifier(traversalStruct.getTargetVariable()),
            Integer.parseInt(n.f0.tokenImage)
        );

        instructions.add(i1);

        traversalStruct.setTargetMiniJavaType(MiniJavaType.INTEGER);

        return instructions;
    }

    /*
     * vk = 1
     */
    @Override
    public ArrayList<Instruction> visit(TrueLiteral n, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();
        
        Instruction i1 = new Move_Id_Integer(
            new Identifier(traversalStruct.getTargetVariable()),
            1
        );

        instructions.add(i1);

        traversalStruct.setTargetMiniJavaType(MiniJavaType.BOOLEAN);
        return instructions;
    }
    
    /*
     * vk = 0
     */
    @Override
    public ArrayList<Instruction> visit(FalseLiteral n, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();
        
        Instruction i1 = new Move_Id_Integer(
            new Identifier(traversalStruct.getTargetVariable()),
            0
        );

        instructions.add(i1);
        traversalStruct.setTargetMiniJavaType(MiniJavaType.BOOLEAN);
        return instructions;
    }


    @Override
    public ArrayList<Instruction> visit(cs132.minijava.syntaxtree.Identifier n, TraversalStruct traversalStruct) {
        /*
         * An identifier could mean 
         *  1. A local variable 
         *  2. A member variable
         * 
         *  In either case, this could be a primitive type or an object
         *  If primitive type, assign the value to the target
         *  If object, assign the address to the target
         *  
         * The only case this will be entered is from an expression ONLY
         * If this is entered from the LHS of a statement, this will result in erroneous code
         */
        ArrayList<Instruction> instructions = new ArrayList<>();

        String id = n.f0.tokenImage;
        if (traversalStruct.getScope().isLocalVar(id)) {
            String sparrowVar = traversalStruct.getScope().getSparrowVar(id);
            Instruction i1 = new Move_Id_Id(
                new Identifier(traversalStruct.getTargetVariable()),
                new Identifier(sparrowVar)
            );
            instructions.add(i1);

            MiniJavaType targetMiniJavaType = traversalStruct.getScope().getMiniJavaVarClassInfo(id).getMiniJavaType();
            traversalStruct.setTargetMiniJavaType(targetMiniJavaType);
        }
        else {
            // is a member variable
            instructions.addAll(OffsetCodeGenerator.getMemberOffsetGivenClass("this", traversalStruct.getTargetVariable(), id, traversalStruct.getScope().getCurrClass()));
            MiniJavaType varMiniJavaType = traversalStruct.getScope().getCurrClass().getMemberType(id);
            traversalStruct.setTargetMiniJavaType(varMiniJavaType);
        }

        return instructions;
    }

    /*
     * targetVar = this
     */
    @Override
    public ArrayList<Instruction> visit(ThisExpression n, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();
        Instruction i1 = new Move_Id_Id(
            new Identifier(traversalStruct.getTargetVariable()),
            new Identifier("this")
        );
        instructions.add(i1);

        MiniJavaType varMiniJavaType = traversalStruct.getScope().getCurrClass().getMiniJavaType();
        traversalStruct.setTargetMiniJavaType(varMiniJavaType);
        
        return instructions;
    }

    /*
     * var_x = 0
     * var_x+1 = expr_eval < var_x
     * if0 var_x+1 goto l_x+3
     * error(negative array alloc)
     * l_x+3:
     * var_x+2 = 1
     * var_x+2 = expr_eval + var_x+2  // need 1 to store the size of the array
     * var_x+3 = 4
     * var_x+2 = var_x+3 * var_x+2
     * target = alloc(var_x+2)
     * [target + 0] = expr_eval
     */
    @Override
    public ArrayList<Instruction> visit(ArrayAllocationExpression n, TraversalStruct traversalStruct) {
        // new int [ expr ]
        
        ArrayList<Instruction> instructions = new ArrayList<>();

        String target = traversalStruct.getTargetVariable();

        traversalStruct.setTargetVariable(Gensym.gensym(SparrowIdentifierType.VARTYPE));
        instructions.addAll(n.f3.accept(this, traversalStruct));
        
        Identifier var1 = new Identifier(Gensym.gensym(SparrowIdentifierType.VARTYPE));
        Identifier var2 = new Identifier(Gensym.gensym(SparrowIdentifierType.VARTYPE));
        Identifier var3 = new Identifier(Gensym.gensym(SparrowIdentifierType.VARTYPE));
        Identifier var4 = new Identifier(Gensym.gensym(SparrowIdentifierType.VARTYPE));
        Identifier targetVar = new Identifier(target);
        Identifier exprTarget = new Identifier(traversalStruct.getTargetVariable());
        Label label = new Label(Gensym.gensym(SparrowIdentifierType.LABELTYPE));

        Instruction i1 = new Move_Id_Integer(var1, 0);
        Instruction i2 = new LessThan(var2, exprTarget, var1);
        Instruction i3 = new IfGoto(var2, label);
        Instruction i4 = new ErrorMessage(ErrorMessages.BAD_ALLOC);
        Instruction i5 = new LabelInstr(label); 
        Instruction i6 = new Move_Id_Integer(var3, 1);
        Instruction i7 = new Add(var3, exprTarget, var3);
        Instruction i8 = new Move_Id_Integer(var4, 4);
        Instruction i9 = new Multiply(var3, var4, var3);
        Instruction i10 = new Alloc(targetVar, var3);
        Instruction i11 = new Store(targetVar, 0, exprTarget);

        instructions.add(i1);
        instructions.add(i2);
        instructions.add(i3);
        instructions.add(i4);
        instructions.add(i5);
        instructions.add(i6);
        instructions.add(i7);
        instructions.add(i8);
        instructions.add(i9);
        instructions.add(i10);
        instructions.add(i11);

        // reset traversal struct back
        traversalStruct.setTargetVariable(target);

        // add latest variable to the scope
        traversalStruct.setTargetMiniJavaType(MiniJavaType.INTEGER_ARRAY);

        return instructions;
    }


    @Override
    public ArrayList<Instruction> visit(AllocationExpression n, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();

        String target = traversalStruct.getTargetVariable();

        String className = n.f1.f0.tokenImage;
        ClassInfo classInfo = traversalStruct.getOffsetCollector().getClassInfo(className);

        instructions.addAll(OffsetCodeGenerator.generateClassAllocation(target, classInfo));

        // reset traversal struct back
        traversalStruct.setTargetVariable(target);

        traversalStruct.setTargetMiniJavaType(classInfo.getMiniJavaType());
        return instructions;
    }

    /*
     * if0 expr_eval goto lx
     * target = 0
     * goto lx+2
     * lx: 
     * target = 1
     * lx+2:
     */
    @Override
    public ArrayList<Instruction> visit(NotExpression n, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();

        String target = traversalStruct.getTargetVariable();

        traversalStruct.setTargetVariable(Gensym.gensym(SparrowIdentifierType.VARTYPE));
        instructions.addAll(n.f1.accept(this, traversalStruct));

        Identifier exprVar = new Identifier(traversalStruct.getTargetVariable());
        Identifier targetVar = new Identifier(target);
        Label label1 = new Label(Gensym.gensym(SparrowIdentifierType.LABELTYPE));
        Label label2 = new Label(Gensym.gensym(SparrowIdentifierType.LABELTYPE));

        Instruction i1 = new IfGoto(exprVar, label1);
        Instruction i2 = new Move_Id_Integer(targetVar, 0);
        Instruction i3 = new Goto(label2);
        Instruction i4 = new LabelInstr(label1);
        Instruction i5 = new Move_Id_Integer(targetVar, 1);
        Instruction i6 = new LabelInstr(label2);

        instructions.add(i1);
        instructions.add(i2);
        instructions.add(i3);
        instructions.add(i4);
        instructions.add(i5);
        instructions.add(i6);  

        traversalStruct.setTargetVariable(target);
        traversalStruct.setTargetMiniJavaType(MiniJavaType.BOOLEAN);

        return instructions;
    }


    /*
     * target = expr_eval
     */
    @Override
    public ArrayList<Instruction> visit(BracketExpression n, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();

        String target = traversalStruct.getTargetVariable();

        traversalStruct.setTargetVariable(Gensym.gensym(SparrowIdentifierType.VARTYPE));
        instructions.addAll(n.f1.accept(this, traversalStruct));

        Identifier targetVar = new Identifier(target);
        String exprVarString = traversalStruct.getTargetVariable();
        Identifier exprVar = new Identifier(exprVarString);

        Instruction i1 = new Move_Id_Id(targetVar, exprVar);

        instructions.add(i1);

        traversalStruct.setTargetVariable(target);

        MiniJavaType targetMiniJavaType = traversalStruct.getScope().getSparrowVarMiniJavaType(exprVarString);
        traversalStruct.setTargetMiniJavaType(targetMiniJavaType);

        return instructions;
    }

}
