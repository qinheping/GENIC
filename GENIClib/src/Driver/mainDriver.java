
package Driver;
import injchecker.*;

import java.io.*;
import java.util.*;

import com.microsoft.z3.ApplyResult;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Goal;
import com.microsoft.z3.Log;
import com.microsoft.z3.Model;
import com.microsoft.z3.Pattern;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Sort;
import com.microsoft.z3.Status;

import parser.parser;
import smtast.*;
import theory.Z3Pred.Z3BooleanAlgebra;
import z3factory.Z3Factory;
import ast.*;
import automata.sfa.SFA;
import java_cup.runtime.*;

public class mainDriver {
	//TODO 
	static List<String> funcdefs;
	static List<String> funcdefs_z3;
	static List<String> inverted_funcdefs;
	static List<String> domains;
	static CoderNode Decoder;
	static int bvlength = 8;
	static GrammarFactory gf;
	static Stack<String> toInvert;
	static Set<String> inverted;
	static Context ctx;
	
	public static void main(String args[]) throws Exception {
			// read input
			Symbol root = GetRoot("test/base/base64encode");
    		CoderNode Root = (CoderNode) root.value;
    		funcdefs = Root.getFuncdefs();
    		// init def
    		inverted_funcdefs = new ArrayList<String>();
    		funcdefs_z3 = new ArrayList<String>();
    		domains = Root.getDomains();
    		gf = new GrammarFactory(new ArrayList(), new ArrayList(), new ArrayList());
    		for(int i = 0; i < funcdefs.size(); i++){
    			funcdefs_z3.add(toString_z3(funcdefs.get(i)));
    			if(domains.get(i) != null)
    				inverted_funcdefs.add(Invertor.invertUnaryFunc(funcdefs.get(i),domains.get(i)));	// 0 for BV, 1 for LIA
    			
    		}
    		
    		gf = new GrammarFactory(funcdefs, funcdefs_z3, inverted_funcdefs);
    		
    		//////////
    		long start = System.currentTimeMillis();
    		com.microsoft.z3.Global.ToggleWarningMessages(true);
            Log.open("test.log");
    		HashMap<String, String> cfg = new HashMap<String, String>();
            cfg.put("model", "true");
            ctx = new Context(cfg);
            //Context ctx2 = new Context(cfg);
    		SFAConvertor converter = new SFAConvertor(Root, ctx);
    		SFA<BoolExpr, Expr> sfa = converter.Convert();
    		Z3BooleanAlgebra Z3ba = new Z3BooleanAlgebra(ctx);
    		//System.out.println(sfa);
    		System.out.println(sfa.getAmbiguousInput(Z3ba));
    		long end = System.currentTimeMillis();
    		System.out.println("inj: " + (end-start));
    		//////////

    		
    		start = System.currentTimeMillis();
    		Decoder = new CoderNode();		
    		List<DeclNode>myDecls = new ArrayList<DeclNode>();
    		for(int i=0; i< funcdefs.size();i++)
   			myDecls.add(new DeclNode(funcdefs.get(i)));
    		Decoder.setDeclList(new DeclListNode(myDecls));
    		
    		toInvert = new Stack<String>();
    		inverted = new HashSet<String>();
    		toInvert.push("S0");
    		while(!toInvert.isEmpty()){
    			String prog = toInvert.pop();
   			if(Root.findProg(prog) == null || inverted.contains(prog))
    				continue;
   			ProgNode invertedProg = InvertProg(prog,Root);
       		invertedProg.print_this();
       		inverted.add(prog);
       		toInvert.remove(prog);
    		}
    		end = System.currentTimeMillis();
    		System.out.println("invert: :"+(end-start));
    		//Root.print_this();
	}
	
	//////////////////////////////////////////////////////////////////////////////////
	// Given a TransitionNode t and its input output types, this method will invert it
	static public TransitionNode InvertTransition(TransitionNode t,TypeNode intype, TypeNode outtype) throws Exception{
		List<String> outpattern = generateY_Pattern(t.getOutputFuncList().size());

		List<String> varlist = t.getVarList();
		List<String> invertedOutputFunc = new ArrayList<String>();
		//List<String> invertedOutputFunc = invertOutputFuncs(t,intype,outtype);
		List<String> encodeFuncs = new ArrayList<String> ();
		List<String> decodeFuncs = new ArrayList<String> ();
		int j = 1;
		for(OutputFuncNode n: t.getOutputFuncList()){ 
			String target = stringToFuncDef(n.getOutputFunc(), "encodeFunc_" + j,intype);
			j++;
			encodeFuncs.add(target);
		}
		String domain = stringToFuncDef(t.getPred(), "domain", intype, new TypeNode(1));
		
		j = 1;
		for(String v: varlist){
			List<String> decodeVars = new ArrayList<String>();
			//List<Integer> keyposition = new ArrayList<Integer>();
			List<String> usedFuncs = new ArrayList<String>();
			for(int i = 0; i < encodeFuncs.size(); i++){
				if(toTermNode(encodeFuncs.get(i)).containVar(v)){
					//keyposition.add(i+1);
					decodeVars.add("y_"+(i+1));
					usedFuncs.add(encodeFuncs.get(i));
				}
			}
			decodeFuncs.add(invertFuncs("decodeFunc_"+j,usedFuncs,varlist , decodeVars,domain, intype, v));
			j++;
		}
		
		for(String s: decodeFuncs){
			invertedOutputFunc.add(toTermNode(s).toString());
		}
		
		String invertedPred = invertPred(t,intype,outtype);
		TransitionNode invertedTrans = new TransitionNode(outpattern, new PredNode(invertedPred), new OutputNode(new OutputFuncListNode(invertedOutputFunc,1),new FuncNode("inverted_"+t.getOutput().getFunc().FuncName(),"tail")));
		
		if(!inverted.contains(t.getOutput().getFunc().FuncName()))
			toInvert.push(t.getOutput().getFunc().FuncName());
		
		return invertedTrans;
	}


	// Invert several funcs
	static public String invertFuncs(String name, List<String> encodeFuncs, List<String> encodeVars, 
				List<String> decodeVars, String domain, TypeNode type, String keyVar) throws Exception{
		String script = "";
		if(type.getType() == 0) script = "(set-logic LIA)\n";
		if(type.getType() == 3) script = "(set-logic BV)\n";
		for(String s: encodeFuncs) script += s + "\n";
		script+=domain+"\n";
		
		if(encodeFuncs.size() == 0){
			encodeFuncs.add(domain);
			script += gf.getSynthBody(name ,encodeFuncs, decodeVars, type) + "\n";
		}else
		script += gf.getSynthBody(name ,encodeFuncs, decodeVars, type) + "\n";

		for(String var: encodeVars){
			script += "(declare-var "+var+ " " + type.toString() + ")\n";
		}
		
		script += generateConstrain(name, encodeFuncs, encodeVars, keyVar) + "(check-synth)\n";

		String s = CallCVC4.CallByString(script);

		return s;
		
	}
	private static String generateConstrain(String name,
			List<String> encodeFuncs, List<String> encodeVars,
			String keyVar) throws Exception {
		String result = "(constraint (or (not (domain " + getVarNameList(encodeVars) +")) (= " 
						+ keyVar + " (" + name + " ";
		for(String func: encodeFuncs){
			CmdNode cmd = toCmdNode(func);
			TermNode term = cmd.getTermNode();
			
			if(cmd.getVarList().size()==0) return cmd.getFuncName();
			
			result += " (" + cmd.getFuncName() + getVarNameList(cmd.getVarList()) + ") ";
		}
		result += "))))\n";
		return result;
	}
	static CmdNode toCmdNode(String source) throws Exception {
		return (CmdNode)SMTDriver.GetSMTRootFromString(source).value;
	}

	static public String invertPred(TransitionNode t, TypeNode intype, TypeNode outtype) throws Exception{
		Z3Factory factory = new Z3Factory(ctx);
		List<Expr> output = new ArrayList<Expr>();

		
		int size = t.getVarList().size();
		com.microsoft.z3.Symbol[] varsname= new com.microsoft.z3.Symbol[size];
		com.microsoft.z3.Symbol[] testnames= new com.microsoft.z3.Symbol[size];
		List<Expr> varlist = new ArrayList<Expr>();
		Expr[] vars = new Expr[size];
		Expr[] boundvars = new Expr[size];
		Sort[] sorts = new Sort[size];
		List<Expr> decodevarlist = new ArrayList<Expr>();
		List<Sort> types = new ArrayList<Sort>();
		BoolExpr eq = ctx.mkTrue();
		for(int i = 0; i < t.getVarList().size(); i++){
			varsname[i] = ctx.mkSymbol(t.getVarList().get(i));
			testnames[i] = ctx.mkSymbol("t"+i);
			varlist.add(ctx.mkIntConst(varsname[i]));
			vars[i] = varlist.get(i);
			boundvars[i] = ctx.mkBound(i, ctx.mkIntSort());
			sorts[i] = ctx.mkIntSort();
			types.add(ctx.mkIntSort());
		}
		for(int i = 0; i < t.getOutputFuncList().size(); i++) {
			output.add(factory.StringToExpr(t.getOutputFuncList().get(i).getOutputFunc()).substitute(vars, boundvars));
		}
		BoolExpr pred = (BoolExpr) factory.StringToExpr(t.getPred()).substitute(vars, boundvars);
		for(int i = 0; i < t.getOutputFuncList().size(); i++){
			decodevarlist.add(ctx.mkIntConst("y"+i));
		}
		for(int i = 0; i < decodevarlist.size(); i++){
			eq = ctx.mkAnd(eq, ctx.mkEq(decodevarlist.get(i), output.get(i)));
		}
		eq = ctx.mkAnd(eq, pred);
		BoolExpr q = ctx.mkExists(sorts, testnames, eq, 1, null, null,
                null, null );
		//System.out.println(q);
		Solver s = ctx.mkSolver();
        s.add(q);
        Model m = null;
        if (s.check() == Status.SATISFIABLE)
        	m = s.getModel();
        //System.out.println(m);
        Goal g = ctx.mkGoal(true, true, false);
        g.add(q);
        //System.out.println(g.AsBoolExpr());
        ApplyResult ar = ctx.mkTactic("qe").apply(g);
       // System.out.println(ar.getNumSubgoals());
		String result = "(and ";
		String[] lines = ar.toString().split(System.getProperty("line.separator"));
		for(int i = 2; i < lines.length; i++){
			result += lines[i];
		}
		return result;
	}
	
	// Given a CoderNode and the prog name we want to invert, this method will invert it
	static public ProgNode InvertProg(String progname, CoderNode root) throws Exception{
		ProgNode target = root.findProg(progname);
		return InvertProg(target);
	}
	
	// Given a ProgNode, this method will invert it
	static public ProgNode InvertProg(ProgNode prog) throws Exception{
		TypeNode inType = prog.getInType();
		TypeNode outType = prog.getOutType();
		List<TransitionNode> TransList = prog.getTransList();
		ProgheadNode InvertedProghead = new ProgheadNode("inverted_"+prog.getName(), new InputNode("", inType),outType);
		List<TransitionNode> invertedTransList = new ArrayList<TransitionNode>();
		
		for(int i = 0; i < TransList.size(); i++){
			invertedTransList.add(InvertTransition(TransList.get(i),inType,outType));
		}
		
		ProgNode InvertedProg = new ProgNode(InvertedProghead, new ProgbodyNode("l", new TransListNode(invertedTransList)));
		return InvertedProg;
	}	
	
	
	// Given a formula string in SMTlib, convert it to z3 formula strings
	public static String toString_z3(String s) throws Exception{
		Symbol root = SMTDriver.GetSMTRootFromString(s);
		CmdNode Root = (CmdNode) root.value;
		return Root.toString_z3();
	}
	
	// Get root from a user code given by a file path
	static Symbol GetRoot(String path) throws Exception{
		File file = new File(path);
        InputStream in = new FileInputStream(file);
		Symbol root = new parser().ParsingStreamToAST(in);
		in.close();
		return root;
	}
	
	static public TermNode toTermNode(String source) throws Exception{
		return ((CmdNode)SMTDriver.GetSMTRootFromString(source).value).getTermNode();
	}
	
	static public String stringToFuncDef(String source, String name, TypeNode type) throws Exception{
		return stringToFuncDef(source, name, type, type);
	}
	
	static public String stringToFuncDef(String source, String name, TypeNode inType, TypeNode outType) throws Exception{
		String result = "(define-fun " + name + " (";
		CmdNode Root = (CmdNode)SMTDriver.GetSMTRootFromString(source).value;
		TermNode term = Root.getTermNode();
		List<String> varlist = term.getVarList();
		result += getVarList(varlist, inType) + ") " + outType.toString() + " " + source + ")";
		return result;
	}
	
	
	
	static public List<String> generateY_Pattern(Integer l){
		List<String> outpattern = new ArrayList<String>();
		for(int i = 0; i < l; i++)
			outpattern.add( "y_" + (i+1));
		outpattern.add("tail");
		return outpattern;
	}
	
	static public String getVarList(List<String> varnames, TypeNode type){
		String result = "";
		for(int i = 0; i<varnames.size();i++) result+="("+varnames.get(i)+" "+type.toString()+")";
		return result;
	}
	static public String getVarNameList(List<String> varnames){
		String result = "";
		for(int i = 0; i<varnames.size();i++) result+=" "+varnames.get(i);
		return result;
	}
	static public String getVarList_z3(List<String> varnames, TypeNode type){
		String result = "";
		for(int i = 0; i<varnames.size();i++) result+="("+varnames.get(i)+" "+type.toString_z3()+")";
		return result;
	}
	

}