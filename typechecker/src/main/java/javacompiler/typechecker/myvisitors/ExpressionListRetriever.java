package javacompiler.typechecker.myvisitors;

import java.util.ArrayList;
import javacompiler.typechecker.environment.Environment;
import javacompiler.typechecker.environment.Type;
import cs132.minijava.visitor.GJDepthFirst;
import cs132.minijava.syntaxtree.*;
public class ExpressionListRetriever extends GJDepthFirst<ArrayList<Type>, Environment>{
    public ExpressionListRetriever(ExpressionVisitor ex) {
        this.expressionVisitor = ex;
    }
    
    private ExpressionVisitor expressionVisitor;

    @Override
    public ArrayList<Type> visit(ExpressionList n, Environment environment) {
        ArrayList<Type> ret = n.f0.accept(this, environment);

        if (n.f1.present()) {
            for (Node param : n.f1.nodes) {
                ArrayList<Type> val = param.accept(this, environment);
                ret.addAll(val);
            }
        }

        return ret;
    }

    @Override
    public ArrayList<Type> visit(ExpressionRest n, Environment environment) {
        return n.f1.accept(this, environment);
    }

    @Override
    public ArrayList<Type> visit(Expression n, Environment environment) {   
        Type expType = n.accept(expressionVisitor, environment);
        ArrayList<Type> ret = new ArrayList<>();
        ret.add(expType);
        return ret;
    }
}
