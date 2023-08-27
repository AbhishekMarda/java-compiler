package javacompiler.registerallocator;

import java.io.*;
import java.util.HashMap;

import javacompiler.registerallocator.Helpers.LivenessAnalyzer;
import javacompiler.registerallocator.Visitors.LinearScan.ProgramVisitor;
import javacompiler.registerallocator.Visitors.LivenessAnalysis.LivenessAnalysisVisitor;
import cs132.IR.SparrowParser;
import cs132.IR.sparrow.Program;
import cs132.IR.visitor.SparrowConstructor;
import cs132.IR.syntaxtree.Node;

public class S2SV {
	public static String allocateRegisters(String input) {
		
		try {
//			 File f = new File(filename);
	
//			 InputStream targetStream = new FileInputStream(f);
//			InputStream targetStream = System.in;

			InputStream targetStream = new ByteArrayInputStream(input.getBytes());
			Node root = new SparrowParser(targetStream).Program();
			SparrowConstructor sc = new SparrowConstructor();
			root.accept(sc);

			Program program = sc.getProgram();
			
			// get all liveness analyses
			HashMap<String, LivenessAnalyzer> funcToLiveness = new HashMap<>();
			root.accept(new LivenessAnalysisVisitor(), funcToLiveness);

			cs132.IR.sparrowv.Program svProgram = program.accept(new ProgramVisitor(), funcToLiveness);

			return svProgram.toString();
		}
		catch (RuntimeException e) {
			System.out.println(e.getMessage());
			throw e;
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			return "";
		}
	}
}