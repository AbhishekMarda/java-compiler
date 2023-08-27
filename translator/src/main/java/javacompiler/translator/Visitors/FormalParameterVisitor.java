package javacompiler.translator.Visitors;

import java.util.ArrayList;

import javacompiler.translator.Helpers.Constants;
import cs132.minijava.syntaxtree.Node;
import cs132.minijava.syntaxtree.FormalParameter;
import cs132.minijava.syntaxtree.FormalParameterList;
import cs132.minijava.syntaxtree.FormalParameterRest;
import cs132.minijava.visitor.GJNoArguDepthFirst;


// Get the names of the formal parameters
public class FormalParameterVisitor extends GJNoArguDepthFirst<ArrayList<String>>{
    @Override
    public ArrayList<String> visit(FormalParameterList n) {
        ArrayList<String> list1 = n.f0.accept(this);
        if (n.f1.present()) {
            for (Node restNode : n.f1.nodes) {
                list1.addAll(restNode.accept(this));
            }
        }
        return list1;
    }

    @Override
    public ArrayList<String> visit(FormalParameter n) {
        ArrayList<String> arguName = new ArrayList<>();
        String name = Constants.ARGNAME_PREFIX +  n.f1.f0.tokenImage;
        arguName.add(name);
        return arguName;
    }

    @Override
    public ArrayList<String> visit(FormalParameterRest n) {
        return n.f1.accept(this);
    }
}
