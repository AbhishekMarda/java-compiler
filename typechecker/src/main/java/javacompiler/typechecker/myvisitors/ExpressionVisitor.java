package javacompiler.typechecker.myvisitors;

import java.util.ArrayList;
import javacompiler.typechecker.environment.ClassInfo;
import javacompiler.typechecker.environment.Environment;
import javacompiler.typechecker.environment.MethodInfo;
import javacompiler.typechecker.environment.Type;
import javacompiler.typechecker.environment.VariableInfo;
import cs132.minijava.visitor.GJDepthFirst;
import cs132.minijava.syntaxtree.*;

public class ExpressionVisitor extends GJDepthFirst<Type, Environment> {
    private Type booleanType = new Type("boolean");
    private Type intType = new Type("int");
    private Type intArrType = new Type("int[]");
    private ExpressionListRetriever expressionListRetriever = new ExpressionListRetriever(this);

    // Expressions
    @Override
    public Type visit(Expression n, Environment environment) {
        return n.f0.choice.accept(this, environment);
    }

    @Override
    public Type visit(AndExpression n, Environment environment) {
        Type f0 = n.f0.accept(this, environment);
        Type f2 = n.f2.accept(this, environment);
        if (!(f0.equals(f2) && f0.equals(this.booleanType))) {
            throw new RuntimeException("And expression on incorrect types.");
        }
        return this.booleanType;
    }

    @Override 
    public Type visit(CompareExpression n, Environment environment) {
        Type f0 = n.f0.accept(this, environment);
        Type f2 = n.f2.accept(this, environment);
        if (!(f0.equals(f2) && f0.equals(this.intType))) {
            throw new RuntimeException("Compare expression on incorrect types.");
        }
        return this.booleanType;
    }

    @Override
    public Type visit(PlusExpression n, Environment environment) {
        Type f0 = n.f0.accept(this, environment);
        Type f2 = n.f2.accept(this, environment);
        if (!(f0.equals(f2) && f0.equals(this.intType))) {
            throw new RuntimeException("Plus expression on incorrect types.");
        }
        return this.intType;
    }

    @Override
    public Type visit(MinusExpression n, Environment environment) {
        Type f0 = n.f0.accept(this, environment);
        Type f2 = n.f2.accept(this, environment);
        if (!(f0.equals(f2) && f0.equals(this.intType))) {
            throw new RuntimeException("Minus expression on incorrect types.");
        }
        return this.intType;
    }

    @Override
    public Type visit(TimesExpression n, Environment environment) {
        Type f0 = n.f0.accept(this, environment);
        Type f2 = n.f2.accept(this, environment);
        if (!(f0.equals(f2) && f0.equals(this.intType))) {
            throw new RuntimeException("Times expression on incorrect types.");
        }
        return this.intType;
    }

    @Override
    public Type visit(ArrayLookup n, Environment environment) {
        Type f0 = n.f0.accept(this, environment);
        Type f2 = n.f2.accept(this, environment);
        if (!(f0.equals(this.intArrType) && f2.equals(this.intType))) {
            throw new RuntimeException("ArrayLookup expression on incorrect types.");
        }
        return this.intType;
    }

    @Override
    public Type visit(ArrayLength n, Environment environment) {
        Type f0 = n.f0.accept(this, environment);
        if (!f0.equals(this.intArrType)) {
            throw new RuntimeException("ArrayLength expression on incorrect types.");
        }
        return this.intType;
    }

    @Override
    public Type visit(MessageSend n, Environment environment) {
        // identifier for expression will check if it exists in the current context
        Type callerType = n.f0.accept(this, environment);

        String methodName = n.f2.f0.tokenImage;

        MethodInfo methodSignature = environment.methodType(methodName, callerType.name);
        if (methodSignature == null) {
            throw new RuntimeException("Could not find method: " + methodName + " for the type: " + callerType);
        }

        ArrayList<Type> givenArgs = new ArrayList<>();

        if (n.f4.present()) {
            givenArgs = n.f4.node.accept(this.expressionListRetriever, environment);
        }
        if (methodSignature.args.size() != givenArgs.size()) {
            throw new RuntimeException("Number of arguments do not match. Caller type: " + callerType.name + ", method name: " + methodName);
        }

        for (int i=0; i < givenArgs.size(); i++) {
            if (!environment.isSubtype(givenArgs.get(i).name, methodSignature.args.get(i).type.name)) {
                throw new RuntimeException("Argument " + i + " does not match. Caller type: " + callerType.name + ", method name: " + methodName);
            }
        }

        return methodSignature.returnType;
    }

    // Primary expressions
    @Override
    public Type visit(PrimaryExpression n, Environment environment) {
        return n.f0.choice.accept(this, environment);
    }

    @Override
    public Type visit(IntegerLiteral n, Environment environment) {
        return this.intType;
    }

    @Override
    public Type visit(TrueLiteral n, Environment environment) {
        return this.booleanType;
    }

    @Override
    public Type visit(FalseLiteral n, Environment environment) {
        return this.booleanType;
    }

    @Override
    public Type visit(Identifier n, Environment environment) {
        // this function should only be entered if the identifier being used 
        // is that which can conform to VariableInfo type. This shoud not 
        // be used when seraching for Type based identifiers. 
        String idName = n.f0.tokenImage;
        VariableInfo idInfo = environment.getIdentifierInfo(idName);
        if (idInfo == null) {
            throw new RuntimeException("Identifier does not exist in the current context. Id: " + idName + "Class: " + environment.currentContext.classInfo.name);
        }
        return idInfo.type;
    }

    @Override
    public Type visit(ThisExpression n, Environment environment) {
        if (environment.currentContext == null) {
            throw new RuntimeException("Current context does not have a class assigned to it. \"this\" is illegal.");
        }

        return new Type(environment.currentContext.classInfo.name);

    }

    @Override
    public Type visit(ArrayAllocationExpression n, Environment environment) {
        Type innerExpr = n.f3.accept(this, environment);
        if (!innerExpr.equals(this.intType)) {
            throw new RuntimeException("Expression not of integer type in array allocation expression. " + environment.currentContext.toString());
        }
        return this.intArrType;
    }

    @Override
    public Type visit(AllocationExpression n, Environment environment) {
        // the identifier used here is not for searching variables, but rather for searching types.
        // thus we will not call on the type identifier

        // check if the id used is of a valid class
        String idString = n.f1.f0.tokenImage;
        ClassInfo c = environment.getClassInfo(idString); 

        if (c == null) {
            throw new RuntimeException("Intantiating variable of type " + idString + "which doesn't exist. " + environment.currentContext.toString());
        }

        return new Type(idString);
    }

    @Override
    public Type visit(NotExpression n, Environment environment){ 
        Type exprType = n.f1.f0.choice.accept(this, environment);
        if (!exprType.equals(this.booleanType)) {
            throw new RuntimeException("Not expression does not have a boolean type to operate on. Type received: " + exprType.name + " Context info:" + environment.currentContext.toString());
        }
        return this.booleanType;
    }

    @Override
    public Type visit(BracketExpression n, Environment environment) {
        return n.f1.accept(this, environment);
    }
}
