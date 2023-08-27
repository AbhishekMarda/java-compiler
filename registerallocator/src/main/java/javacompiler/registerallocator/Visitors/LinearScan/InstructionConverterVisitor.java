package javacompiler.registerallocator.Visitors.LinearScan;

import java.util.ArrayList;
import java.util.List;

import javacompiler.registerallocator.Helpers.SVVar;
import javacompiler.registerallocator.Helpers.SparrowVar;
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
import cs132.IR.sparrow.Program;
import cs132.IR.sparrow.Store;
import cs132.IR.sparrow.Subtract;
import cs132.IR.sparrow.visitor.ArgRetVisitor;
import cs132.IR.sparrowv.Instruction;
import cs132.IR.sparrowv.Move_Id_Reg;
import cs132.IR.sparrowv.Move_Reg_FuncName;
import cs132.IR.sparrowv.Move_Reg_Id;
import cs132.IR.sparrowv.Move_Reg_Integer;
import cs132.IR.sparrowv.Move_Reg_Reg;
import cs132.IR.token.Identifier;

public class InstructionConverterVisitor implements ArgRetVisitor<TraversalStruct, ArrayList<Instruction>>{

    public Instruction createMove(SVVar lhs, SVVar rhs) {
        if (lhs.isRegister()) {
            if (rhs.isRegister()) {
                return new Move_Reg_Reg(lhs.toRegister(), rhs.toRegister());
            }
            else {
                return new Move_Reg_Id(lhs.toRegister(), rhs.toIdentifier());
            }
        }
        else {
            if (rhs.isRegister()) { 
                return new Move_Id_Reg(lhs.toIdentifier(), rhs.toRegister());
            }
        }
        throw new RuntimeException("Move between id to id does not exist");
    }

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
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public ArrayList<Instruction> visit(LabelInstr node, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();
        instructions.add(
            new cs132.IR.sparrowv.LabelInstr(node.label)
        );
        return instructions;
    }

    /*
     * id = c
     * --------
     * temp = c
     * svid = temp
     */
    @Override
    public ArrayList<Instruction> visit(Move_Id_Integer node, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();

        int instrNumber = traversalStruct.getInstrNum();

        SVVar id = traversalStruct.getSVar(new SparrowVar(node.lhs.toString()), instrNumber);
        SVVar temp = traversalStruct.getTempRegister();
        instructions.add(new Move_Reg_Integer(temp.toRegister(), node.rhs));
        instructions.add(createMove(id, temp));
        traversalStruct.releaseTempRegister(temp);

        return instructions;
    }

    /*
     * id = @func
     * -----------
     * temp = @func
     * svid = temp
     */
    @Override
    public ArrayList<Instruction> visit(Move_Id_FuncName node, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();

        int instrNumber = traversalStruct.getInstrNum();

        SVVar id = traversalStruct.getSVar(new SparrowVar(node.lhs.toString()), instrNumber);
        SVVar temp = traversalStruct.getTempRegister();
        instructions.add(new Move_Reg_FuncName(temp.toRegister(), node.rhs));
        instructions.add(createMove(id, temp));
        traversalStruct.releaseTempRegister(temp);

        return instructions;
    }

    /*
     * id1 = id2 + id3
     * ----------------
     * temp1 = svid2
     * temp2 = svid3
     * temp1 = temp1 + temp2
     * svid1 = temp1
     */
    @Override
    public ArrayList<Instruction> visit(Add node, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();

        int instrNumber = traversalStruct.getInstrNum();
        
        SVVar id1 = traversalStruct.getSVar(new SparrowVar(node.arg1.toString()), instrNumber);
        SVVar id2 = traversalStruct.getSVar(new SparrowVar(node.arg2.toString()), instrNumber);

        SVVar temp1 = traversalStruct.getTempRegister();
        SVVar temp2 = traversalStruct.getTempRegister();

        SVVar target = traversalStruct.getSVar(new SparrowVar(node.lhs.toString()), instrNumber);

        // move ids to temps
        // add temps
        // assign resultant temp to target
        instructions.add(createMove(temp1, id1));
        instructions.add(createMove(temp2, id2));
        instructions.add(new cs132.IR.sparrowv.Add(temp1.toRegister(), temp1.toRegister(), temp2.toRegister()));
        instructions.add(createMove(target, temp1));

        traversalStruct.releaseTempRegister(temp1);
        traversalStruct.releaseTempRegister(temp2);        
        return instructions;
    }

    /*
     * id1 = id2 - id3
     * ----------------
     * temp1 = svid2
     * temp2 = svid3
     * temp1 = temp1 - temp2
     * svid1 = temp1
     */
    @Override
    public ArrayList<Instruction> visit(Subtract node, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();

        int instrNumber = traversalStruct.getInstrNum();
        
        SVVar id1 = traversalStruct.getSVar(new SparrowVar(node.arg1.toString()), instrNumber);
        SVVar id2 = traversalStruct.getSVar(new SparrowVar(node.arg2.toString()), instrNumber);

        SVVar temp1 = traversalStruct.getTempRegister();
        SVVar temp2 = traversalStruct.getTempRegister();

        SVVar target = traversalStruct.getSVar(new SparrowVar(node.lhs.toString()), instrNumber);

        // move ids to temps
        // add temps
        // assign resultant temp to target
        instructions.add(createMove(temp1, id1));
        instructions.add(createMove(temp2, id2));
        instructions.add(new cs132.IR.sparrowv.Subtract(temp1.toRegister(), temp1.toRegister(), temp2.toRegister()));
        instructions.add(createMove(target, temp1));

        traversalStruct.releaseTempRegister(temp1);
        traversalStruct.releaseTempRegister(temp2);        
        return instructions;
    }

    /*
     * id1 = id2 * id3
     * ----------------
     * temp1 = svid2
     * temp2 = svid3
     * temp1 = temp1 * temp2
     * svid1 = temp1
     */
    @Override
    public ArrayList<Instruction> visit(Multiply node, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();

        int instrNumber = traversalStruct.getInstrNum();
        
        SVVar id1 = traversalStruct.getSVar(new SparrowVar(node.arg1.toString()), instrNumber);
        SVVar id2 = traversalStruct.getSVar(new SparrowVar(node.arg2.toString()), instrNumber);

        SVVar temp1 = traversalStruct.getTempRegister();
        SVVar temp2 = traversalStruct.getTempRegister();

        SVVar target = traversalStruct.getSVar(new SparrowVar(node.lhs.toString()), instrNumber);

        // move ids to temps
        // add temps
        // assign resultant temp to target
        instructions.add(createMove(temp1, id1));
        instructions.add(createMove(temp2, id2));
        instructions.add(new cs132.IR.sparrowv.Multiply(temp1.toRegister(), temp1.toRegister(), temp2.toRegister()));
        instructions.add(createMove(target, temp1));

        traversalStruct.releaseTempRegister(temp1);
        traversalStruct.releaseTempRegister(temp2);        
        return instructions;
    }

    /*
     * id1 = id2 * id3
     * ----------------
     * temp1 = svid2
     * temp2 = svid3
     * temp1 = temp1 < temp2
     * svid1 = temp1
     */
    @Override
    public ArrayList<Instruction> visit(LessThan node, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();

        int instrNumber = traversalStruct.getInstrNum();
        
        SVVar id1 = traversalStruct.getSVar(new SparrowVar(node.arg1.toString()), instrNumber);
        SVVar id2 = traversalStruct.getSVar(new SparrowVar(node.arg2.toString()), instrNumber);

        SVVar temp1 = traversalStruct.getTempRegister();
        SVVar temp2 = traversalStruct.getTempRegister();

        SVVar target = traversalStruct.getSVar(new SparrowVar(node.lhs.toString()), instrNumber);

        // move ids to temps
        // add temps
        // assign resultant temp to target
        instructions.add(createMove(temp1, id1));
        instructions.add(createMove(temp2, id2));
        instructions.add(new cs132.IR.sparrowv.LessThan(temp1.toRegister(), temp1.toRegister(), temp2.toRegister()));
        instructions.add(createMove(target, temp1));

        traversalStruct.releaseTempRegister(temp1);
        traversalStruct.releaseTempRegister(temp2);        
        return instructions;
    }

    /*
     * id1 = [id2 + c]
     * ----------------
     * temp2 = svid2
     * temp1 = [temp2 + c]
     * svid1 = temp1
     */
    @Override
    public ArrayList<Instruction> visit(Load node, TraversalStruct traversalStruct) {
        
        ArrayList<Instruction> instructions = new ArrayList<>();

        int instrNumber = traversalStruct.getInstrNum();
        
        SVVar id1 = traversalStruct.getSVar(new SparrowVar(node.lhs.toString()), instrNumber);
        SVVar id2 = traversalStruct.getSVar(new SparrowVar(node.base.toString()), instrNumber);

        SVVar temp1 = traversalStruct.getTempRegister();
        SVVar temp2 = traversalStruct.getTempRegister();

        // move ids to temps
        // add temps
        // assign resultant temp to target
        instructions.add(createMove(temp2, id2));
        instructions.add(new cs132.IR.sparrowv.Load(temp1.toRegister(), temp2.toRegister(), node.offset));
        instructions.add(createMove(id1, temp1));

        traversalStruct.releaseTempRegister(temp1);
        traversalStruct.releaseTempRegister(temp2);        
        return instructions;
    }

    /*
     * [id1 + c] = id2
     * --------------------
     * temp1 = svid1
     * temp2 = svid2
     * [temp1 + c] = temp2
     */
    @Override
    public ArrayList<Instruction> visit(Store node, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();
        int instrNumber = traversalStruct.getInstrNum();

        SVVar temp1 = traversalStruct.getTempRegister();
        SVVar temp2 = traversalStruct.getTempRegister();
        SVVar id1 = traversalStruct.getSVar(new SparrowVar(node.base.toString()), instrNumber);
        SVVar id2 = traversalStruct.getSVar(new SparrowVar(node.rhs.toString()), instrNumber);

        instructions.add(createMove(temp1, id1));
        instructions.add(createMove(temp2, id2));
        instructions.add(new cs132.IR.sparrowv.Store(temp1.toRegister(), node.offset, temp2.toRegister()));

        traversalStruct.releaseTempRegister(temp1);
        traversalStruct.releaseTempRegister(temp2);        
        return instructions;
    }

    /*
     * id1 = id2
     * ---------
     * temp1 = svid2
     * svid1 = temp1
     */
    @Override
    public ArrayList<Instruction> visit(Move_Id_Id node, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();
        int instrNumber = traversalStruct.getInstrNum();

        SVVar temp1 = traversalStruct.getTempRegister();
        SVVar id1 = traversalStruct.getSVar(new SparrowVar(node.lhs.toString()), instrNumber);
        SVVar id2 = traversalStruct.getSVar(new SparrowVar(node.rhs.toString()), instrNumber);

        instructions.add(createMove(temp1, id2));
        instructions.add(createMove(id1, temp1));

        traversalStruct.releaseTempRegister(temp1);
        return instructions;
    }

    /*
     * id1 = alloc(id2)
     * ----------------
     * temp1 = svid2
     * temp1 = alloc(temp1)
     * svid1 = temp1
     */
    @Override
    public ArrayList<Instruction> visit(Alloc node, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();
        int instrNumber = traversalStruct.getInstrNum();
        SVVar temp1 = traversalStruct.getTempRegister();
        SVVar id1 = traversalStruct.getSVar(new SparrowVar(node.lhs.toString()), instrNumber);
        SVVar id2 = traversalStruct.getSVar(new SparrowVar(node.size.toString()), instrNumber);

        instructions.add(createMove(temp1, id2));
        instructions.add(new cs132.IR.sparrowv.Alloc(temp1.toRegister(), temp1.toRegister()));
        instructions.add(createMove(id1, temp1));

        traversalStruct.releaseTempRegister(temp1);

        return instructions;
    }

    /*
     * print(id)
     * ----------
     * temp1 = svid
     * print(temp1)
     */
    @Override
    public ArrayList<Instruction> visit(Print node, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();
        int instrNumber = traversalStruct.getInstrNum();
        SVVar temp1 = traversalStruct.getTempRegister();
        SVVar id1 = traversalStruct.getSVar(new SparrowVar(node.content.toString()), instrNumber);
        instructions.add(createMove(temp1, id1));
        instructions.add(new cs132.IR.sparrowv.Print(temp1.toRegister()));
        traversalStruct.releaseTempRegister(temp1);

        return instructions;
    }

    // no diff
    @Override
    public ArrayList<Instruction> visit(ErrorMessage node, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();
        instructions.add(new cs132.IR.sparrowv.ErrorMessage(node.msg));
        return instructions;
    }

    // no diff
    @Override
    public ArrayList<Instruction> visit(Goto node, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();

        instructions.add(new cs132.IR.sparrowv.Goto(node.label));

        return instructions;
    }

    /*
     * if id goto l
     * --------------
     * temp1 = svid
     * if temp1 goto l
     */
    @Override
    public ArrayList<Instruction> visit(IfGoto node, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();
        int instrNumber = traversalStruct.getInstrNum();
        SVVar temp1 = traversalStruct.getTempRegister();
        SVVar id1 = traversalStruct.getSVar(new SparrowVar(node.condition.toString()), instrNumber);
        
        instructions.add(createMove(temp1, id1));
        instructions.add(new cs132.IR.sparrowv.IfGoto(temp1.toRegister(), node.label));

        traversalStruct.releaseTempRegister(temp1);
        return instructions;
    }

    /*
     * id1 = call id2(ids)
     * ----------------
     * <save caller saved>
     * <save args>
     * <convert register params to stack space>
     * <set args 1-6>
     * temp2 = svid2
     * temp1 = call temp2 (<ids>)
     * <restore args>
     * <restore caller saved>
     * svid1 = temp1
     */
    @Override
    public ArrayList<Instruction> visit(Call node, TraversalStruct traversalStruct) {
        ArrayList<Instruction> instructions = new ArrayList<>();
        int instrNumber = traversalStruct.getInstrNum();

        int numArgRegisters = SVVar.getArgumentRegisters().size();

        // get the list of parameters that are spilt over
        ArrayList<Identifier> spiltParams = new ArrayList<>();
        if (numArgRegisters < node.args.size()) {
            List<Identifier> sublist = node.args.subList(numArgRegisters, node.args.size());
            spiltParams = new ArrayList<>(sublist);
        }

        // add the instructions to save the a and t registers
        instructions.addAll(traversalStruct.saveBeforeCall(spiltParams, traversalStruct.getInstrNum()));

        // set arguments
        int numRegArgs = Math.min(numArgRegisters, node.args.size());

        // add instructions to set the argument registers
        for (int i=0; i<numRegArgs; i++) {
            SVVar rhs = traversalStruct.getSVar(new SparrowVar(node.args.get(i).toString()), traversalStruct.getInstrNum());
            SVVar lhs = SVVar.getArgumentRegisters().get(i);
            instructions.add(createMove(lhs, rhs));
        }

        // get the sparrow-V ids that are going to be the call parameters (ie spilt argments converted to S-V)
        // the spilt parameters were assigned a stack variable in the call to `saveBeforeCall`
        ArrayList<Identifier> callParams = new ArrayList<>();
        for (int i=numRegArgs; i < node.args.size(); i++) {
            Identifier id = node.args.get(i);
            callParams.add(traversalStruct.getParamSVVar(id).toIdentifier());
        }

        // call instruction setup
        SVVar temp1 = traversalStruct.getTempRegister();
        SVVar temp2 = traversalStruct.getTempRegister();
        SVVar id1 = traversalStruct.getSVar(new SparrowVar(node.lhs.toString()), instrNumber);
        SVVar id2 = traversalStruct.getSVar(new SparrowVar(node.callee.toString()), instrNumber);
        
        // call!
        instructions.add(createMove(temp2, id2));
        instructions.add(new cs132.IR.sparrowv.Call(temp1.toRegister(), temp2.toRegister(), callParams));
        instructions.addAll(traversalStruct.restoreAfterCall());
        instructions.add(createMove(id1, temp1));


        traversalStruct.releaseTempRegister(temp1);
        traversalStruct.releaseTempRegister(temp2);      

        return instructions;
    }
    
}
