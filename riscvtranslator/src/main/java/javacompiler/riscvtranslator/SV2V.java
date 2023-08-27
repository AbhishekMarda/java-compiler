package javacompiler.riscvtranslator;

import java.io.*;

import javacompiler.riscvtranslator.Helpers.TraversalStruct;
import javacompiler.riscvtranslator.Visitors.ProgramVisitor;
import cs132.IR.SparrowParser;
import cs132.IR.sparrowv.Program;
import cs132.IR.syntaxtree.Node;
import cs132.IR.visitor.SparrowVConstructor;

public class SV2V {
    public static String IRtoRiscV(String input) {
		
		try {
//			 File f = new File(file);
//
//			 InputStream targetStream = new FileInputStream(f);

			InputStream targetStream = new ByteArrayInputStream(input.getBytes());
//			InputStream targetStream = System.in;

			/*
			one possible fix for the multiple construction of the parser:
			create a file containing the output belonging to the same input stream
			and then reset the input stream so that you only have to call program
			and then:
			Node root = SparrowParser.Program();
			 */
			SparrowParser.ReInit(targetStream);
			Node root = SparrowParser.Program();
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
