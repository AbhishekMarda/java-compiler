package javacompiler.typechecker.myvisitors;
import cs132.minijava.syntaxtree.*;
import javacompiler.typechecker.environment.Type;
import cs132.minijava.visitor.*;

public class TypeRetriever extends GJNoArguDepthFirst<Type>{
    @Override 
    public Type visit(cs132.minijava.syntaxtree.Type n) {
        return n.f0.choice.accept(this);
    }

    @Override 
    public Type visit(ArrayType n) {
        return new Type(n.f0.tokenImage + n.f1.tokenImage + n.f2.tokenImage);
    }

    @Override
    public Type visit(BooleanType n) {
        return new Type(n.f0.tokenImage);
    }

    @Override 
    public Type visit(IntegerType n) {
        return new Type(n.f0.tokenImage);
    }

    @Override 
    public Type visit(Identifier n) {
        return new Type(n.f0.tokenImage);
    }
}
