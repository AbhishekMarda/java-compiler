package javacompiler.typechecker.myvisitors;
import cs132.minijava.syntaxtree.*;
import javacompiler.typechecker.environment.*;
import javacompiler.typechecker.environment.Type;
import cs132.minijava.visitor.*;
import java.util.ArrayList;

public class FormalParameterRetriever extends GJNoArguDepthFirst<ArrayList<VariableInfo>>{
    private TypeRetriever typeRetriever = new TypeRetriever();

    @Override
    public ArrayList<VariableInfo> visit(FormalParameterList n) {
        ArrayList<VariableInfo> ret = n.f0.accept(this);

        if (n.f1.present()) {
            for (Node param : n.f1.nodes) {
                ArrayList<VariableInfo> val = param.accept(this);
                ret.addAll(val);
            }
        }

        return ret;
    }

    @Override
    public ArrayList<VariableInfo> visit(FormalParameterRest n) {
        return n.f1.accept(this);
    }

    @Override
    public ArrayList<VariableInfo> visit(FormalParameter n) {   
        Type t = n.f0.accept(typeRetriever);
        String name = n.f1.f0.tokenImage;

        ArrayList<VariableInfo> ret = new ArrayList<>();
        ret.add(new VariableInfo(name, t));
        return ret;
    }
}
