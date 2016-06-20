package Driver;

import injchecker.*;

import java.io.*;
import java.util.*;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Log;

import parser.parser;
import smtast.*;
import theory.Z3Pred.Z3BooleanAlgebra;
import ast.*;
import automata.sfa.SFA;
import java_cup.runtime.*;

public class mainDriver {
	//TODO 
	static List<String> funcdefs;
	static List<String> funcdefs_z3;
	static List<String> inverted_funcdefs;
	static CoderNode Decoder;
	static int bvlength = 8;
	static GrammarFactory gf;
	static Stack<String> toInvert;
	static Set<String> inverted;
	
	public static void main(String args[]) throws Exception {
			// read input
			Symbol root = GetRoot("test/int_test_20");
    		CoderNode Root = (CoderNode) root.value;

    		funcdefs = Root.getFuncdefs();   		
    		
    		// init def
    		inverted_funcdefs = new ArrayList<String>();
    		funcdefs_z3 = new ArrayList<String>();
    		
    		for(int i = 0; i < funcdefs.size(); i++){
    			funcdefs_z3.add(toString_z3(funcdefs.get(i)));
    			inverted_funcdefs.add(invertFuncFromString(funcdefs.get(i),0));	// 0 for BV, 1 for LIA
    			//System.out.println(inverted_funcdefs.get(i));
    		}
    		
    		gf = new GrammarFactory(funcdefs, funcdefs_z3, inverted_funcdefs);

    		//////////
    		long start = System.currentTimeMillis();
    		com.microsoft.z3.Global.ToggleWarningMessages(true);
            Log.open("test.log");
    		HashMap<String, String> cfg = new HashMap<String, String>();
            cfg.put("model", "true");
            Context ctx = new Context(cfg);
            //Context ctx2 = new Context(cfg);
    		SFAConvertor converter = new SFAConvertor(Root, ctx);
    		SFA<BoolExpr, Expr> sfa = converter.Convert();
    		Z3BooleanAlgebra Z3ba = new Z3BooleanAlgebra(ctx);
    		System.out.println(sfa);
    		sfa.getAmbiguousInput(Z3ba);
    		long end = System.currentTimeMillis();
    		System.out.println("inj: " + (end-start));
    		//////////

//    		
//    		start = System.currentTimeMillis();
//    		Decoder = new CoderNode();		
//    		List<DeclNode>myDecls = new ArrayList<DeclNode>();
//    		for(int i=0; i< funcdefs.size();i++)
//    			myDecls.add(new DeclNode(funcdefs.get(i)));
//    		Decoder.setDeclList(new DeclListNode(myDecls));
//    		
//    		toInvert = new Stack<String>();
//    		inverted = new HashSet<String>();
//    		toInvert.push("S0");
//    		while(!toInvert.isEmpty()){
//    			String prog = toInvert.pop();
//    			if(Root.findProg(prog) == null || inverted.contains(prog))
//    				continue;
//    			ProgNode invertedProg = InvertProg(prog,Root);
//        		//invertedProg.print_this();
//        		inverted.add(prog);
//        		toInvert.remove(prog);
//    		}
//    		end = System.currentTimeMillis();
//    		System.out.println("invert: :"+(end-start));
//    		//Root.print_this();
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
	static public String invertFuncs(String name, List<String> encodeFuncs, List<String> encodeVars, List<String> decodeVars, String domain, TypeNode type, String keyVar) throws Exception{
		String script = "";
		if(type.getType() == 0) script = "(set-logic LIA)\n";
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
	private static CmdNode toCmdNode(String source) throws Exception {
		return (CmdNode)SMTDriver.GetSMTRootFromString(source).value;
	}

	static public String invertFuncFromString(String s, int logic) throws Exception{
		String script = null;
		
		if(logic == 0) script = "(set-logic BV)\n";
		if(logic == 1) script = "(set-logic LIA)\n";
		
		CmdNode Root = (CmdNode)SMTDriver.GetSMTRootFromString(s).value;
		
		List<TypeNode> inTypes = Root.getIntype();
		TypeNode outType = Root.getOuttype();
		
		String funcname = Root.getFuncName();
		
		List<String> varlist = Root.getVarList();
		
		script += s + getGrammar("inverted_"+funcname, varlist) + "\n";
		for(int i=0;i<varlist.size();i++) script +="(declare-var " + varlist.get(i) +" (BitVec " + bvlength + "))\n"; 
		script += "(constraint (= "+varlist.get(0)+" ("+funcname+" (inverted_"+funcname+" "+varlist.get(0)+"))))\n(check-synth)";
		
		//script += "(constraint (= "+varlist.get(0)+" (inverted_"+funcname+" ("+funcname+" "+varlist.get(0)+"))))\n(check-synth)";
		//System.out.println(script);
		s = CallCVC4.CallByString(script);
		return s;
	}
	
	// Given a transition t and its input output types, this method will invert it
	static public String invertPred(TransitionNode t, TypeNode intype, TypeNode outtype) throws InterruptedException{
		String result = "";
		String z3script = "";
		List<String> outpattern = generateY_Pattern(t.getOutputFuncList().size());
		List<String> inpattern = t.getPattern();
		for(int i=0; i<outpattern.size()-1;i++) z3script += "(declare-const "+outpattern.get(i)+" "+outtype.toString_z3()+")\n";
		for(int i=0; i<funcdefs_z3.size();i++) z3script += funcdefs_z3.get(i);
		String funcdefCMD = "";
		for(int i = 0; i < t.getOutputFuncList().size();i++){
			funcdefCMD += "(define-fun f_"+ (i+1) + " (";
			for(int j = 0; j < inpattern.size()-1; j++) funcdefCMD += "(" + inpattern.get(j) + " " + intype.toString_z3() + ")";
			funcdefCMD += ") " + outtype.toString_z3() + t.getOutputFuncList().get(i).getOutputFunc() + ")\n";
		}
		z3script += funcdefCMD;
		String PredCMD = "(define-fun Pred (";
		for(int i = 0; i < inpattern.size()-1; i++) PredCMD += "(" + inpattern.get(i) + " " + intype.toString_z3() + ")";
		PredCMD += ") Bool" + t.getPred() + ")\n"; 
		z3script += PredCMD + "(assert (exists (";
		for(int i = 0; i < inpattern.size()-1;i++) z3script +="(" + inpattern.get(i) + " " + intype.toString_z3()+")";
		z3script +=") (and  (Pred "+getVarNameList(inpattern.subList(0, inpattern.size()-1))+") (and ";
		for(int i = 0; i < outpattern.size()-1; i++) z3script += "(= "+outpattern.get(i)+" (f_"+(i+1)+" "+getVarNameList(inpattern.subList(0, inpattern.size()-1))+"))\n";
		z3script +="))))\n(apply qe)";
		result = CallZ3.CallByString(z3script);
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
	
	// Given a transition t and its input output types, this method wil
	static public List<String> invertOutputFuncs(TransitionNode t,TypeNode intype, TypeNode outtype) throws Exception{
		List<String> outpattern = generateY_Pattern(t.getOutputFuncList().size());
		List<String> inpattern = t.getPattern();
		List<String> invertedOutputFunc = new ArrayList<String>();
		String setlogic = getLogiccommand(intype);
		String funcdefCMD = "";
		for(int i = 0; i < t.getOutputFuncList().size();i++){
			funcdefCMD += "(define-fun f_"+ (i+1) + " (";
			for(int j = 0; j < inpattern.size()-1; j++) funcdefCMD += "(" + inpattern.get(j) + " " + intype.toString() + ")";
			funcdefCMD += ") " + outtype.toString() + t.getOutputFuncList().get(i).getOutputFunc() + ")\n";
		}
		String PredCMD = "(define-fun Pred (";
		for(int i = 0; i < inpattern.size()-1; i++) PredCMD += "(" + inpattern.get(i) + " " + intype.toString() + ")";
		PredCMD += ") Bool" + t.getPred() + ")\n"; 
		String SyGusScript = null;
		for(int i = 0; i < inpattern.size() - 1; i++){
			SyGusScript = setlogic;
			for(int j = 0; j < inverted_funcdefs.size(); j++) SyGusScript += inverted_funcdefs.get(j);
			for(int j = 0; j < funcdefs.size(); j++) SyGusScript += funcdefs.get(j) + "\n";
			SyGusScript += funcdefCMD + PredCMD;
			SyGusScript += "(synth-fun g_" + (i+1) + " (";
			for(int j = 0; j < outpattern.size() - 1; j++) SyGusScript += "(" + outpattern.get(j) + " " + outtype.toString() + ")";
			SyGusScript += ") " + intype.toString() + "\n" + getGrammar(intype, outpattern) + ")\n";
			for(int j = 0; j < inpattern.size() - 1; j++) SyGusScript += "(declare-var " + inpattern.get(j) + " " + outtype.toString() + ")\n";
			SyGusScript += "(constraint (or (not (Pred ";
			for(int j = 0; j < inpattern.size()-1; j++) SyGusScript += inpattern.get(j) + " ";
			SyGusScript += ")) (= " + inpattern.get(i) + " (g_" + (i+1) + " ";
			for(int j = 0; j < outpattern.size()-1; j++) {
				SyGusScript += " (f_" + (j+1) + " ";
				for(int k = 0; k < inpattern.size() - 1; k++) SyGusScript += inpattern.get(k) + " ";
				SyGusScript += ")";
			}
			SyGusScript += "))))\n(check-synth)";

			invertedOutputFunc.add(CallCVC4.CallByString(SyGusScript));
		}
		return invertedOutputFunc;
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
	
	// 
	static public String getLogiccommand(TypeNode t){
		String result = "(set-logic ";
		if(t.getType() == Type.INT){
			result = result + "LIA";
		}else if(t.getType() == Type.BOOL){
			System.out.print("Bool");
		}else if(t.getType() == Type.CHAR){
			System.out.print("Char");
		}else if(t.getType() == Type.BV){
			result = result +"BV";
		}
		return result+")\n";
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
	
	// we assume all types are bv 8 here
	public static String getGrammar(String name,List<String> varlist){
		String result = "(synth-fun "+name+" (";
		for(int i = 0; i < varlist.size(); i++) result += "(" + varlist.get(i) + " (BitVec " + bvlength + "))";
		
		result += ") (BitVec " + bvlength + ")\n\t ((Start (BitVec " + bvlength + ") ((bvnot Start)\n\t(ite StartBool Start Start)\n\t(bvxor Start Start)\n\t(bvand Start Start)\n\t(bvor Start Start)\n\t(bvneg Start)\n\t(bvadd Start Start)\n\t(bvmul Start Start)\n\t(bvudiv Start Start)\n\t(bvurem Start Start)\n\t(bvlshr Start Start)\n\t(bvashr Start Start)\n\t(bvshl Start Start)\n\t(bvsdiv Start Start)\n\t(bvsrem Start Start)\n\t(bvsub Start Start)\n\t";
		for(int i = 0; i < varlist.size(); i++) result += varlist.get(i) + " ";
		result+= "#x00 #xFF #x01 #x02 #x03 #x04 #x05 #x06 #x07 #x08 #x09 #x0a #x0b #x0c #x0d #x0e #x0f #x10 #x20 #x30 #x40 #x50 #x60 #x70 #x80 #x90 #xa0 #xb0 #xc0 #xd0 #xe0 #xf0))\n\t(StartBool Bool ((and StartBool StartBool)\n\t(or StartBool StartBool)\n\t(not StartBool)\n\t(bvule Start Start)\n\t(= Start Start)\n\t(bvuge Start Start)))))\n";

		return result;
	}

	public static String getGrammar(TypeNode t, List<String> vlist) throws Exception{
		String result = null;
		if(t.getType() == Type.BV){
			result = "\t((Start (BitVec " + bvlength + ") ((bvnot Start)\n\t(ite StartBool Start Start)\n\t(bvxor Start Start)\n\t(bvand Start Start)\n\t(bvor Start Start)\n\t(bvneg Start)\n\t(bvadd Start Start)\n\t(bvmul Start Start)\n\t(bvudiv Start Start)\n\t(bvurem Start Start)\n\t(bvlshr Start Start)\n\t(bvashr Start Start)\n\t(bvshl Start Start)\n\t(bvsdiv Start Start)\n\t(bvsrem Start Start)\n\t(bvsub Start Start)\n\t";
			for(int i = 0; i < vlist.size()-1; i++) result += vlist.get(i) + " ";
			for(int i = 0; i < inverted_funcdefs.size(); i++){
				CmdNode Root = (CmdNode)SMTDriver.GetSMTRootFromString(inverted_funcdefs.get(i)).value;
				String funcname = Root.getFuncName();
				Integer inputsize = Root.getVarList().size();
				result += "(" + funcname + " ";
				for(int j = 0; j < inputsize; j++) result += "Start ";
				result += ") ";
			}
			result+= "#x00 #xFF #x01 #x02 #x03 #x04 #x05 #x06 #x07 #x08 #x09 #x0a #x0b #x0c #x0d #x0e #x0f #x10 #x20 #x30 #x40 #x50 #x60 #x70 #x80 #x90 #xa0 #xb0 #xc0 #xd0 #xe0 #xf0))\n\t(StartBool Bool ((and StartBool StartBool)\n\t(or StartBool StartBool)\n\t(not StartBool)\n\t(bvule Start Start)\n\t(= Start Start)\n\t(bvuge Start Start))))\n";
		}
		if(t.getType() == Type.INT){
			result = "((Start Int ( 0 1 2 3 ";
			for(int i = 0; i < vlist.size()-1; i++) result += vlist.get(i) + " ";
			result += "(ite BoolExpr Start Start))) (BoolExpr Bool ((< Start Start) (<= Start Start) (> Start Start) (>= Start Start))))";
		}
		return result;
	}

	// (x Int) (y Int) ...
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
	
	static public String check_func_Injectivity(String func, String pred, List<String> pattern) throws InterruptedException{
		String z3script = "(define-fun f ("+getVarList_z3(pattern,new TypeNode(Type.BV,bvlength))+") (_ BitVec " + bvlength + ") " + func+")";
		z3script += "(define-fun Pred ("+getVarList_z3(pattern,new TypeNode(Type.BV,bvlength))+") Bool " +pred +")";
		z3script += "(assert (exists((x (_ BitVec " + bvlength + ")) (y (_ BitVec " + bvlength + "))) (and (not (= x y)) (= (f x) (f y)) (Pred x) (Pred y))))\n(check-sat)";
		//System.out.println(z3script);
		return CallZ3.CallByString(z3script);
	}
}