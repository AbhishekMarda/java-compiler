package javacompiler.translator.Visitors;

import java.util.ArrayList;

import cs132.minijava.syntaxtree.ClassDeclaration;
import cs132.minijava.syntaxtree.ClassExtendsDeclaration;
import cs132.minijava.syntaxtree.Node;
import cs132.minijava.visitor.GJVoidDepthFirst;

public class ListClassesVisitor extends GJVoidDepthFirst<ArrayList<String>>{

    public ArrayList<String> getClasses(Node goal){
        ArrayList<String> classes = new ArrayList<>();
        goal.accept(this, classes);
        return classes;
    }

    @Override
    public void visit(ClassDeclaration node, ArrayList<String> classes) {
        String className = node.f1.f0.tokenImage;
        classes.add(className);
    }

    @Override
    public void visit(ClassExtendsDeclaration node, ArrayList<String> classes) {
        String className = node.f1.f0.tokenImage;
        classes.add(className);
    }
}
