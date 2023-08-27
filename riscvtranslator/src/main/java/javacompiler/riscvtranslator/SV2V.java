package javacompiler.riscvtranslator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;

import javacompiler.riscvtranslator.Helpers.TraversalStruct;
import javacompiler.riscvtranslator.Visitors.ProgramVisitor;
import cs132.IR.SparrowParser;
import cs132.IR.sparrowv.Program;
import cs132.IR.syntaxtree.Node;
import cs132.IR.visitor.SparrowVConstructor;

public class SV2V {
    public static String IRtoRiscV(String file) {
		
		try {
			 File f = new File(file);
	
			 InputStream targetStream = new FileInputStream(f);
//			InputStream targetStream = System.in;
			Node root = new SparrowParser(targetStream).Program();
			SparrowVConstructor sc = new SparrowVConstructor();
			root.accept(sc);

			Program program = sc.getProgram();
			
			javacompiler.riscvtranslator.RiscV.Program riscProgram = program.accept(new ProgramVisitor(), new TraversalStruct());
			
			return riscProgram.toString();

			// String filePath = "/Users/abhishekmarda/Documents/Academics/UCLA/ComputerScience/CS132/trial2/cs132-framework/src/main/java/out.txt";
			// BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
			// writer.write(riscProgram.toString()); 
			// writer.close();
			
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			return "";
		}
	
	}
}
