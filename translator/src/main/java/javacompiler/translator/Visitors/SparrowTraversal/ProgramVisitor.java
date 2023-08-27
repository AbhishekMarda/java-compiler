package javacompiler.translator.Visitors.SparrowTraversal;

import java.util.ArrayList;

import javacompiler.translator.Helpers.OffsetCollector;
import javacompiler.translator.Helpers.TraversalStruct;
import cs132.IR.sparrow.FunctionDecl;
import cs132.IR.sparrow.Program;
import cs132.minijava.syntaxtree.Goal;
import cs132.minijava.syntaxtree.Node;
import cs132.minijava.visitor.GJNoArguDepthFirst;

public class ProgramVisitor extends GJNoArguDepthFirst<Program> {
    @Override
    public Program visit(Goal node) {
        FunctionDeclVisitor functionDeclVisitor = new FunctionDeclVisitor();
        OffsetCollector offsetCollector = new OffsetCollector(node);
        TraversalStruct traversalStruct = new TraversalStruct(offsetCollector);

        ArrayList<FunctionDecl> mainDecl = node.f0.accept(functionDeclVisitor, traversalStruct);
        ArrayList<FunctionDecl> classDecls = new ArrayList<>(mainDecl);

        if (node.f1.present()) {
            for (Node n : node.f1.nodes) {
                ArrayList<FunctionDecl> funcs = n.accept(functionDeclVisitor, traversalStruct);
                classDecls.addAll(funcs);
            }
        }

        return new Program(classDecls);
    }
}
