package javacompiler.typechecker.myvisitors;
import cs132.minijava.visitor.GJVoidDepthFirst;

import java.util.ArrayList;

import javacompiler.typechecker.environment.*;
import cs132.minijava.syntaxtree.ClassDeclaration;
import cs132.minijava.syntaxtree.ClassExtendsDeclaration;
import cs132.minijava.syntaxtree.MainClass;
import cs132.minijava.syntaxtree.MethodDeclaration;
import cs132.minijava.syntaxtree.Node;
import cs132.minijava.syntaxtree.VarDeclaration;

public class EnvironmentCreator extends GJVoidDepthFirst<Environment>{
    private TypeRetriever typeRetriever = new TypeRetriever();
    private FormalParameterRetriever formalParameterRetriever = new FormalParameterRetriever();

    /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> "public"
    * f4 -> "static"
    * f5 -> "void"
    * f6 -> "—"
    * f7 -> "("
    * f8 -> "String"
    * f9 -> "["
    * f10 -> "]"
    * f11 -> Identifier()
    * f12 -> ")"
    * f13 -> "{"
    * f14 -> ( VarDeclaration() )*
    * f15 -> ( Statement() )*
    * f16 -> "}"
    * f17 -> "}"
    */
    @Override
    public void visit(MainClass n, Environment A) {
        super.visit(n, A);
        ClassInfo main = new ClassInfo();
        // get the name of the main function
        main.name = n.f1.f0.tokenImage;

        // get the return type of the main function (void)
        Type ret = new Type(n.f5.tokenImage);

        // get the argument type, "String[]"
        String argTypeStr = n.f8.tokenImage + n.f9.tokenImage + n.f10.tokenImage;
        Type argType = new Type(argTypeStr);

        // create argument now given name and type of argument
        VariableInfo arg = new VariableInfo(n.f11.f0.tokenImage, argType);

        // create method and add it to the main class
        ArrayList<VariableInfo> mainArgs = new ArrayList<>();
        mainArgs.add(arg);
        MethodInfo mainMethod = new MethodInfo(n.f6.tokenImage, ret, mainArgs);
        main.methods.add(mainMethod);

        // put the main class into the table
        A.addClassInfo(main.name, main);
    }


    /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> ( VarDeclaration() )*
    * f4 -> ( MethodDeclaration() )*
    * f5 -> "}"
    */
    @Override
    public void visit(ClassDeclaration n, Environment A) {
        super.visit(n, A);
        ClassInfo classInfo = new ClassInfo();
        classInfo.name = n.f1.f0.tokenImage;

        ArrayList<VariableInfo> members = new ArrayList<>();
        
        if (n.f3.present()) {
            // for each member declaration, get its name and type, and add it to the ArrayList
            for (Node var : n.f3.nodes) {
                if (var instanceof VarDeclaration) {
                    VarDeclaration varDec = (VarDeclaration) var;
                    Type varType = varDec.f0.accept(this.typeRetriever);
                    String varName = varDec.f1.f0.tokenImage;
                    members.add(new VariableInfo(varName, varType));
                }   
                else {
                    throw new RuntimeException("Did not find VarDeclaration inside class: " + classInfo.name);
                }
            }
        }

        classInfo.members = members;

        ArrayList<MethodInfo> methods = new ArrayList<>();
        if (n.f4.present()) {
            // for each method declaration, get its name, return type, and args, and add it to the ArrayList
            for (Node method : n.f4.nodes) {
                if (method instanceof MethodDeclaration) {
                    MethodDeclaration methodDec = (MethodDeclaration) method;
                    String methodName = methodDec.f2.f0.tokenImage;
                    Type retType = methodDec.f1.accept(this.typeRetriever);
                    ArrayList<VariableInfo> args = new ArrayList<>();
                    
                    // for each arg in the method declaration, get its name and type, and add it to the ArrayList
                    if (methodDec.f4.present()) {
                        args = methodDec.f4.node.accept(formalParameterRetriever);
                    }
                    MethodInfo currMethod = new MethodInfo(methodName, retType, args);
                    methods.add(currMethod);
                }
                else {
                    throw new RuntimeException("Did not find MethodDeclaration inside class: " + classInfo.name);
                }
            }
        }

        classInfo.methods = methods;

        A.addClassInfo(classInfo.name, classInfo);
    }  


    /**
     * Grammar production:
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "extends"
     * f3 -> Identifier()
     * f4 -> "{"
     * f5 -> ( VarDeclaration() )*
     * f6 -> ( MethodDeclaration() )*
     * f7 -> "}"
     */
    @Override
    public void visit(ClassExtendsDeclaration n, Environment A) {
        super.visit(n, A);
        ClassInfo classInfo = new ClassInfo();
        classInfo.name = n.f1.f0.tokenImage;

        // link the parent and child classes
        String parent = n.f3.f0.tokenImage;
        A.addLink(classInfo.name, parent);

        ArrayList<VariableInfo> members = new ArrayList<>();
        
        if (n.f5.present()) {
            // for each member declaration, get its name and type, and add it to the ArrayList
            for (Node var : n.f5.nodes) {
                if (var instanceof VarDeclaration) {
                    VarDeclaration varDec = (VarDeclaration) var;
                    Type varType = varDec.f0.accept(this.typeRetriever);
                    String varName = varDec.f1.f0.tokenImage;
                    members.add(new VariableInfo(varName, varType));
                }   
                else {
                    throw new RuntimeException("Did not find VarDeclaration inside class: " + classInfo.name);
                }
            }
        }

        classInfo.members = members;

        ArrayList<MethodInfo> methods = new ArrayList<>();
        if (n.f6.present()) {
            // for each method declaration, get its name, return type, and args, and add it to the ArrayList
            for (Node method : n.f6.nodes) {
                if (method instanceof MethodDeclaration) {
                    MethodDeclaration methodDec = (MethodDeclaration) method;
                    String methodName = methodDec.f2.f0.tokenImage;
                    Type retType = methodDec.f1.accept(this.typeRetriever);
                    ArrayList<VariableInfo> args = new ArrayList<>();
                    
                    // for each arg in the method declaration, get its name and type, and add it to the ArrayList
                    if (methodDec.f4.present()) {
                        args = methodDec.f4.node.accept(formalParameterRetriever);
                    }
                    MethodInfo currMethod = new MethodInfo(methodName, retType, args);
                    methods.add(currMethod);
                }
                else {
                    throw new RuntimeException("Did not find MethodDeclaration inside class: " + classInfo.name);
                }
            }
        }

        classInfo.methods = methods;

        A.addClassInfo(classInfo.name, classInfo);
    }
}
