package javacompiler.registerallocator.Visitors.LivenessAnalysis;

import java.util.ArrayList;
import java.util.HashMap;

import javacompiler.registerallocator.Helpers.SparrowVar;
import javacompiler.registerallocator.Helpers.LivenessAnalyzer;
import cs132.IR.syntaxtree.Block;
import cs132.IR.syntaxtree.FunctionDeclaration;
import cs132.IR.syntaxtree.Node;
import cs132.IR.syntaxtree.Identifier;
import cs132.IR.visitor.GJVoidDepthFirst;

public class LivenessAnalysisVisitor extends GJVoidDepthFirst<HashMap<String, LivenessAnalyzer>> {
    @Override
    public void visit(FunctionDeclaration n, HashMap<String, LivenessAnalyzer> argu) {
        
        ArrayList<Identifier> params = new ArrayList<>();
        ArrayList<SparrowVar> sparrowParams = new ArrayList<>();
		for (Node param : n.f3.nodes) {
            params.add((Identifier) param);
            sparrowParams.add(new SparrowVar(((Identifier) param).f0.tokenImage));
        }

        Block b = n.f5;
        String retId = n.f5.f2.f0.tokenImage;
        LivenessAnalyzer livenessAnalyzer = new LivenessAnalyzer(b.f0.nodes, params, new SparrowVar(retId));

        argu.put(n.f1.f0.tokenImage, livenessAnalyzer);
    }
}