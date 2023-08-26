package javacompiler.typechecker.myvisitors;

import javacompiler.typechecker.environment.Environment;
import javacompiler.typechecker.environment.MethodInfo;
import javacompiler.typechecker.environment.Type;
import javacompiler.typechecker.environment.VariableInfo;

import java.util.HashSet;

import cs132.minijava.syntaxtree.ArrayAssignmentStatement;
import cs132.minijava.syntaxtree.AssignmentStatement;
import cs132.minijava.syntaxtree.Block;
import cs132.minijava.syntaxtree.ClassDeclaration;
import cs132.minijava.syntaxtree.ClassExtendsDeclaration;
import cs132.minijava.syntaxtree.Goal;
import cs132.minijava.syntaxtree.IfStatement;
import cs132.minijava.syntaxtree.MainClass;
import cs132.minijava.syntaxtree.MethodDeclaration;
import cs132.minijava.syntaxtree.PrintStatement;
import cs132.minijava.syntaxtree.VarDeclaration;
import cs132.minijava.syntaxtree.WhileStatement;
import cs132.minijava.visitor.GJVoidDepthFirst;

public class TypecheckVisitor extends GJVoidDepthFirst<Environment> {
    private Type booleanType = new Type("boolean");
    private Type intType = new Type("int");
    private Type intArrType = new Type("int[]");
    private TypeRetriever typeRetriever = new TypeRetriever();
    private ExpressionVisitor expressionVisitor = new ExpressionVisitor();

    @Override
    public void visit(Goal n, Environment environment) {

        // distinct classes already checked when creating environment
        // cycles also already checked immediately after creating environment
        n.f0.accept(this, environment);
        n.f1.accept(this, environment);
        n.f2.accept(this, environment);

    }

    @Override
    public void visit(MainClass n, Environment environment) {
        Type mainClass = n.f1.accept(this.typeRetriever);
        environment.addNewContext(mainClass.name);

        // in this case, no method declaration
        // directly add the function ourselves
        environment.setMethodToContext("main");

        // var declarations will automatically be added to the context
        n.f14.accept(this, environment);
        n.f15.accept(this, environment);
        environment.restoreContext();
    }

    @Override
    public void visit(ClassDeclaration n, Environment environment) {
        String className = n.f1.f0.tokenImage;
        environment.addNewContext(className);

        // distinctness of member and method names is already done

        n.f4.accept(this, environment);
        environment.restoreContext();
    }

    @Override
    public void visit(ClassExtendsDeclaration n, Environment environment) {
        String className = n.f1.f0.tokenImage;
        environment.addNewContext(className);

        // distinctness of member and method names is already done

        // overloading will be checked in the method declaration
        n.f6.accept(this, environment);
        environment.restoreContext();
    }

    @Override
    public void visit(MethodDeclaration n, Environment environment) {
        String methodName = n.f2.f0.tokenImage;
        Type methodReturnType = n.f1.accept(typeRetriever);
        
        environment.addNewContext(environment.currentContext.classInfo.name);
        environment.setMethodToContext(methodName);

        if (!environment.noOverloading(methodName, environment.currentContext.classInfo.name)) {
            // method is overloaded!
            throw new RuntimeException("Overloading detected." + environment.currentContext.toString());
        }


        // ensure all arguments are of classes that exist
        MethodInfo currMethod = environment.currentContext.classInfo.getMethodByName(methodName);
        if (currMethod == null) {
            throw new RuntimeException("Could not find method despite knowing it exists. This should be impossible! Method name: " + environment.currentContext.toString());
        }
        
        for (VariableInfo arg : currMethod.args) {
            if (!environment.classExists(arg.type.name)) {
                throw new RuntimeException(
                    "Parameter type inexistent. Arg: " + arg.name + environment.currentContext.toString()
                );
            }
        }

        // ensure variable names are distint
        HashSet<String> varNames = new HashSet<>();
        for (VariableInfo arg : currMethod.args) {
            if (!varNames.contains(arg.name)) {
                varNames.add(arg.name);
            }
            else {
                throw new RuntimeException("Duplicate argument names detected." + environment.currentContext.toString());
            }

        }
        

        // ensure that the return type also exists
        if (!environment.classExists(methodReturnType.name)) {
            throw new RuntimeException(
                "Return type does not exist. Type: " + methodReturnType.name + environment.currentContext.toString()
            );
        }

        n.f7.accept(this, environment);
        n.f8.accept(this, environment);

        Type retType = n.f10.accept(this.expressionVisitor, environment);

        if (!environment.isSubtype(retType.name, methodReturnType.name)) {
            throw new RuntimeException("Return expression is not a subtype of the return type." + environment.currentContext.toString());
        }

        environment.restoreContext();
    }

    @Override
    public void visit(VarDeclaration n, Environment environment) {
        Type varType = n.f0.accept(this.typeRetriever);

        if (!environment.classExists(varType.name)) {
            throw new RuntimeException("Attempting to declare variable with non-existent class name: " + varType.name + environment.currentContext.toString());
        }

        String name = n.f1.f0.tokenImage;
        VariableInfo var = new VariableInfo(name, varType);

        // below also ensures that the variable is not overriding a parameter
        // or a previously defined variable inside the current scope
        environment.addLocalVarToContext(var);
    }

    // Statements
    @Override
    public void visit(Block n, Environment environment) {
        n.f1.accept(this, environment);
    }

    @Override
    public void visit(AssignmentStatement n, Environment environment) {
        Type exprType = n.f2.accept(this.expressionVisitor, environment);
        String idName = n.f0.f0.tokenImage;
        VariableInfo idInfo = environment.getIdentifierInfo(idName);
        if (idInfo == null) {
            throw new RuntimeException("Could not find a valid identifier: " + idName + "." + environment.currentContext.toString());
        }

        if (!environment.isSubtype(exprType.name, idInfo.type.name)) {
            throw new RuntimeException("Assignment statement types are incompatible. Id type: " + idInfo.type.name + " Expr type: " + exprType.name);
        }

    }

    @Override
    public void visit(ArrayAssignmentStatement n, Environment environment) {
        String arrName = n.f0.f0.tokenImage;
        VariableInfo idInfo = environment.getIdentifierInfo(arrName);
        if (idInfo == null || !idInfo.type.equals(this.intArrType)) {
            throw new RuntimeException("Unfound identifier or not of int[] type. Id: " + arrName);
        }

        Type indexType = n.f2.accept(this.expressionVisitor, environment);
        if (indexType == null || !indexType.equals(this.intType)) {
            throw new RuntimeException("Index in array assignment not of int type. Found type: " + indexType.name + environment.currentContext.toString());
        }

        Type assignType = n.f5.accept(this.expressionVisitor, environment);
        if (assignType == null || !assignType.equals(this.intType)) {
            throw new RuntimeException("Assigned value in array assignment not of int type. Found type: " + assignType.name + environment.currentContext.toString());
        }
    }

    @Override 
    public void visit(IfStatement n, Environment environment) {
        Type condType = n.f2.accept(this.expressionVisitor, environment);
        if (condType == null || !condType.equals(this.booleanType)) {
            throw new RuntimeException("Conditional expr in if statement not of bool type. Found type: " + condType.name + environment.currentContext.toString());
        }

        n.f4.accept(this, environment);

        n.f6.accept(this, environment);
    }

    @Override
    public void visit(WhileStatement n, Environment environment) {
        Type condType = n.f2.accept(this.expressionVisitor, environment);
        if (condType == null || !condType.equals(this.booleanType)) {
            throw new RuntimeException("Conditional expr in while statement not of bool type. Found type: " + condType.name + environment.currentContext.toString());
        }

        n.f4.accept(this, environment);
    }

    @Override
    public void visit(PrintStatement n, Environment environment) {
        Type exprType = n.f2.accept(this.expressionVisitor, environment);
        if (exprType == null || !exprType.equals(this.intType)) {
            throw new RuntimeException("Expr in print statement not of int type. Found type: " + exprType.name + environment.currentContext.toString());
        }

    }

}
