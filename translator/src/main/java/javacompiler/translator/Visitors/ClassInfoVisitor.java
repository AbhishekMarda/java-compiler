package javacompiler.translator.Visitors;

import javacompiler.translator.Helpers.ClassInfo;
import javacompiler.translator.Helpers.MiniJavaType;
import cs132.minijava.visitor.GJDepthFirst;
import cs132.minijava.syntaxtree.Identifier;
import cs132.minijava.syntaxtree.IntegerType;
import cs132.minijava.syntaxtree.ArrayType;
import cs132.minijava.syntaxtree.BooleanType;
import cs132.minijava.syntaxtree.ClassDeclaration;
import cs132.minijava.syntaxtree.ClassExtendsDeclaration;
import cs132.minijava.syntaxtree.MethodDeclaration;
import cs132.minijava.syntaxtree.Type;
import cs132.minijava.syntaxtree.VarDeclaration;


public class ClassInfoVisitor extends GJDepthFirst<MiniJavaType, ClassInfo> {
    @Override
    public MiniJavaType visit(ClassDeclaration node, ClassInfo currClass) {
        if (!node.f1.f0.tokenImage.equals(currClass.getName())){
            return null;
        }
        node.f3.accept(this, currClass);
        node.f4.accept(this, currClass);
        return null;
    }

    @Override
    public MiniJavaType visit(ClassExtendsDeclaration node, ClassInfo currClass) {
        if (!node.f1.f0.tokenImage.equals(currClass.getName())){
            return null;
        }
        node.f5.accept(this, currClass);
        node.f6.accept(this, currClass);
        return null;
    }

    @Override
    public MiniJavaType visit(VarDeclaration node, ClassInfo currClass) {
        MiniJavaType type = node.f0.f0.choice.accept(this, currClass);
        currClass.addMemberName(node.f1.f0.tokenImage, type);
        return null;
    }
    
    @Override
    public MiniJavaType visit(MethodDeclaration node, ClassInfo currClass) {
        MiniJavaType returnType = node.f1.accept(this, currClass);
        currClass.addMethodName(node.f2.f0.tokenImage, returnType);
        return null;
    }


    // Type expressions
    @Override
    public MiniJavaType visit(Type node, ClassInfo currClass) {
        return node.f0.choice.accept(this, currClass);
    }

    @Override
    public MiniJavaType visit(BooleanType node, ClassInfo currClass) {
        return new MiniJavaType(node.f0.tokenImage);
    }
    
    @Override
    public MiniJavaType visit(Identifier node, ClassInfo currClass) {
        return new MiniJavaType(node.f0.tokenImage);
    }

    @Override
    public MiniJavaType visit(IntegerType node, ClassInfo currClass) {
        return new MiniJavaType(node.f0.tokenImage);
    }

    @Override
    public MiniJavaType visit(ArrayType node, ClassInfo currClass) {
        return new MiniJavaType(node.f0.tokenImage + node.f1.tokenImage + node.f2.tokenImage);
    } 

}
