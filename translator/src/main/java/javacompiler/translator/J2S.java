package javacompiler.translator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;

import javacompiler.translator.Helpers.OffsetCollector;
import javacompiler.translator.Visitors.SparrowTraversal.ProgramVisitor;
import cs132.IR.sparrow.Program;
import cs132.minijava.MiniJavaParser;
import cs132.minijava.syntaxtree.Node;

public class J2S{
    public static String translateToIR(String filename) {


        try{

             File f = new File(filename);

            String currentDirectory = System.getProperty("user.dir");
            System.out.println("Current working directory: " + currentDirectory);
             InputStream targetStream = new FileInputStream(f);
//            InputStream targetStream = System.in;
            Node root =  new MiniJavaParser(targetStream).Goal();
            ProgramVisitor programVisitor = new ProgramVisitor();
            Program sparrowRoot = root.accept(programVisitor);

            // String filePath = "/Users/abhishekmarda/Documents/Academics/UCLA/ComputerScience/CS132/trial2/cs132-framework/src/main/java/out.txt";
            // BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            // writer.write(sparrowRoot.toString());
            // writer.close();
            return sparrowRoot.toString();
        }
        catch (Exception r) {
            System.out.println(r.getMessage());
            System.out.println("An error, which should not have occurred.");
            System.exit(1);
        }

        return "";
    }

}