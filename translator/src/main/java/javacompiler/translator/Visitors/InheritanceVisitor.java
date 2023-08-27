package javacompiler.translator.Visitors;

import java.util.HashMap;

import cs132.minijava.syntaxtree.ClassExtendsDeclaration;
import cs132.minijava.syntaxtree.Node;
import cs132.minijava.visitor.GJVoidDepthFirst;

public class InheritanceVisitor extends GJVoidDepthFirst<HashMap<String, String>> {
    public HashMap<String, String> mapInheritance(Node node) {
        HashMap<String, String> childToParent = new HashMap<>();
        node.accept(this, childToParent);
        return childToParent;
    }
    
    @Override
    public void visit(ClassExtendsDeclaration node, HashMap<String, String> inheritanceFinder) {
        String child = node.f1.f0.tokenImage;
        String parent = node.f3.f0.tokenImage;
        inheritanceFinder.put(child, parent);
    }
}
