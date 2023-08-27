package javacompiler.translator.Visitors.SparrowTraversal;


import java.util.ArrayList;

import javacompiler.translator.Helpers.Constants;
import javacompiler.translator.Helpers.Gensym;
import javacompiler.translator.Helpers.Scope;
import javacompiler.translator.Helpers.SparrowIdentifierType;
import javacompiler.translator.Helpers.TraversalStruct;
import javacompiler.translator.Visitors.FormalParameterVisitor;
import cs132.IR.sparrow.Block;
import cs132.IR.sparrow.FunctionDecl;
import cs132.IR.sparrow.Instruction;
import cs132.IR.sparrow.Move_Id_Integer;
import cs132.IR.token.FunctionName;
import cs132.IR.token.Identifier;
import cs132.minijava.syntaxtree.ClassDeclaration;
import cs132.minijava.syntaxtree.ClassExtendsDeclaration;
import cs132.minijava.syntaxtree.MainClass;
import cs132.minijava.syntaxtree.MethodDeclaration;
import cs132.minijava.syntaxtree.Node;
import cs132.minijava.syntaxtree.TypeDeclaration;
import cs132.minijava.visitor.GJDepthFirst;

public class FunctionDeclVisitor extends GJDepthFirst<ArrayList<FunctionDecl>, TraversalStruct>{
    
    @Override
    public ArrayList<FunctionDecl> visit(TypeDeclaration node, TraversalStruct traversalStruct) {
        return node.f0.choice.accept(this, traversalStruct);
    }

    @Override
    public ArrayList<FunctionDecl> visit(ClassDeclaration node, TraversalStruct traversalStruct) {
        ArrayList<FunctionDecl> functions = new ArrayList<>();

        // variables already handled
        // just need to create a scope and the methods should handle the rest
        String className = node.f1.f0.tokenImage;
        
        if (node.f4.present()) {
            for (Node methodNode : node.f4.nodes) {
                // single funciton decl will be returned in the array from MethodDeclaration

                // reset the scope every time a new function is entered
                Scope currScope = new Scope(traversalStruct.getOffsetCollector().getClassInfo(className));
                traversalStruct.setScope(currScope);
                functions.addAll(methodNode.accept(this, traversalStruct));
            }
        }

        // reset scope for the next class
        traversalStruct.setScope(null);

        return functions;
    }

    @Override
    public ArrayList<FunctionDecl> visit(MainClass n, TraversalStruct traversalStruct) {
        
        ArrayList<Instruction> instructions = new ArrayList<>();
        InstructionVisitor instructionVisitor = new InstructionVisitor();
        String mainClassName = n.f1.f0.tokenImage;
        
        // set the new scope
        Scope scope = new Scope(traversalStruct.getOffsetCollector().getClassInfo(mainClassName));
        traversalStruct.setScope(scope);
        
        // get variable declaration instructions
        if (n.f14.present()) {
            for (Node varDec : n.f14.nodes) {
                instructions.addAll(varDec.accept(instructionVisitor, traversalStruct));
            }
        }

        // get statement instructions
        if (n.f15.present()) {
            for(Node statementNode : n.f15.nodes) {
                instructions.addAll(statementNode.accept(instructionVisitor, traversalStruct));
            }
        }

        // return 0
        Identifier ret = new Identifier(Gensym.gensym(SparrowIdentifierType.VARTYPE));
        Instruction i1 = new Move_Id_Integer(ret, 0);
        instructions.add(i1);

        Block mainBlock = new Block(instructions, ret);
        FunctionDecl mainDecl = new FunctionDecl(new FunctionName(Constants.MAIN_FUNCTION_NAME), new ArrayList<>(), mainBlock);
        ArrayList<FunctionDecl> functionDecls = new ArrayList<>();
        functionDecls.add(mainDecl);

        traversalStruct.setScope(null);
        return functionDecls;
    }

    @Override
    public ArrayList<FunctionDecl> visit(ClassExtendsDeclaration node, TraversalStruct traversalStruct) {
        ArrayList<FunctionDecl> functions = new ArrayList<>();

        // variables already handled
        // just need to create a scope and the methods should handle the rest
        String className = node.f1.f0.tokenImage;
        
        if (node.f6.present()) {
            for (Node methodNode : node.f6.nodes) {
                // single function decl will be returned in the array from MethodDeclaration
                
                // reset the scope every time a new function is entered
                Scope currScope = new Scope(traversalStruct.getOffsetCollector().getClassInfo(className));
                traversalStruct.setScope(currScope);
                functions.addAll(methodNode.accept(this, traversalStruct));
            }
        }

        // reset scope for the next class
        traversalStruct.setScope(null);

        return functions;
    }

    @Override
    public ArrayList<FunctionDecl> visit(MethodDeclaration n, TraversalStruct traversalStruct) {
        
        InstructionVisitor instructionVisitor = new InstructionVisitor();
        ArrayList<Instruction> instructions = new ArrayList<>();

        // get the formal parameter list instructions
        if (n.f4.present()) {
            instructions.addAll(n.f4.accept(instructionVisitor, traversalStruct));
        }

        // get the variable declaration instructions
        if (n.f7.present()) {
            for (Node varDecNode : n.f7.nodes) {
                instructions.addAll(varDecNode.accept(instructionVisitor, traversalStruct));
            }
        }
        
        // get the statement instructions
        if (n.f8.present()) {
            for (Node statementNode : n.f8.nodes) {
                instructions.addAll(statementNode.accept(instructionVisitor, traversalStruct));
            }
        }
        
        // run the return expression
        String returnVar = Gensym.gensym(SparrowIdentifierType.VARTYPE);
        traversalStruct.setTargetVariable(returnVar);
        instructions.addAll(n.f10.accept(instructionVisitor, traversalStruct));

        // get the parameter names
        ArrayList<String> names = new ArrayList<>();
        names.add("this");
        if (n.f4.present()) {
            names.addAll(n.f4.node.accept(new FormalParameterVisitor()));
        }
        // convert parameter names to identifiers
        ArrayList<Identifier> argumentList = new ArrayList<>();
        for (String name : names) {
            argumentList.add(new Identifier(name));
        }

        
        // create a FunctionDecl object
        String funcNameString = traversalStruct.getScope().getCurrClass().getSparrowFunctionName(n.f2.f0.tokenImage);
        FunctionName funcName = new FunctionName(funcNameString);
        Block funcBlock = new Block(instructions, new Identifier(returnVar));
        FunctionDecl funcDecl = new FunctionDecl(funcName, argumentList, funcBlock);
        ArrayList<FunctionDecl> functionDecls = new ArrayList<>();
        functionDecls.add(funcDecl);


        // scope will be reset by the TypeDeclaration choice visit function, no need to reset here

        return functionDecls;
    }


}
