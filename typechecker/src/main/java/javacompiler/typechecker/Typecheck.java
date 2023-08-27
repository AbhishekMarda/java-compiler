package javacompiler.typechecker;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javacompiler.typechecker.environment.*;
import javacompiler.typechecker.myvisitors.EnvironmentCreator;
import javacompiler.typechecker.myvisitors.TypecheckVisitor;
import cs132.minijava.syntaxtree.*;
import cs132.minijava.MiniJavaParser;

public class Typecheck {
    public static boolean typecheck(String filename) {
        try{

             File f = new File(filename);

             InputStream targetStream = new FileInputStream(f);
//            InputStream in = System.in;
            Node root =  new MiniJavaParser(targetStream).Goal();

            try {
                Environment e = new Environment();
                EnvironmentCreator ec = new EnvironmentCreator();
                TypecheckVisitor t = new TypecheckVisitor();
                root.accept(ec, e);
                e.addClassInfo("int", null);
                e.addClassInfo("int[]", null);
                e.addClassInfo("boolean", null);
                e.runChecks();
                root.accept(t, e);
                return true;
            } catch (RuntimeException r) {
                // System.out.println(r.getMessage());
                return false;
            }


        } catch (Exception e) {
            return false;
        }
    }
}