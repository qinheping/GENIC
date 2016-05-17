package Driver;

import java.io.*;
import java.util.*;

import parser.parser;
import smtast.*;
import ast.*;
import java_cup.runtime.*;

public class mainDriver {
	//TODO 
	static List<String> funcdefs;
	static List<String> funcdefs_z3;
	static List<String> inverted_funcdefs;
	static CoderNode Decoder;
	
	public static void main(String args[]) throws Exception {
			Symbol root = GetRoot("src/Driver/usercode.txt");
    		CoderNode Root = (CoderNode) root.value;
    		funcdefs = Root.getFuncdefs();
    		inverted_funcdefs = new ArrayList<String>();
    		funcdefs_z3 = new ArrayList<String>();
    		for(int i = 0; i < funcdefs.size(); i++){
    			funcdefs_z3.add(to_String_z3(funcdefs.get(i)));
    			inverted_funcdefs.add(invertFuncFromString(funcdefs.get(i)));
    		}
    		
    		Decoder = new CoderNode();		
    		List<DeclNode>myDecls = new ArrayList<DeclNode>();
    		for(int i=0; i< funcdefs.size();i++)
    			myDecls.add(new DeclNode(funcdefs.get(i)));
    		Decoder.setDeclList(new DeclListNode(myDecls));
    		
    		ProgNode invertedProg = InvertProg("f",Root);
    		invertedProg.print_this();
    		//Root.print_this();
	}
	
	// Given a TransitionNode t and its input output types, this method will invert it
	static public TransitionNode InvertTransition(TransitionNode t,TypeNode intype, TypeNode outtype) throws Exception{
		List<String> outpattern = getPattern(t.getOutputFuncList().size());
		List<String> invertedOutputFunc = invertOutputFuncs(t,intype,outtype);
		//System.out.println(check_func_Injectivity(t.getOutputFuncList().get(0).getOutputFunc(),t.getPred().getPred(),t.getPattern().subList(0, 1)));
		String invertedPred = invertPred(t,intype,outtype);
		TransitionNode invertedTrans = new TransitionNode(outpattern, new PredNode(invertedPred), new OutputNode(new OutputFuncListNode(invertedOutputFunc,1),new FuncNode("inverted_"+t.getOutput().getFunc().FuncName(),"tail")));
		return invertedTrans;
	}
	
	static public String check_func_Injectivity(String func, String pred, List<String> pattern) throws InterruptedException{
		String z3script = "(define-fun f ("+getVarList_z3(pattern,new TypeNode(Type.BV,8))+") (_ BitVec 8) " + func+")";
		z3script += "(define-fun Pred ("+getVarList_z3(pattern,new TypeNode(Type.BV,8))+") Bool " +pred +")";
		z3script += "(assert (exists((x (_ BitVec 8)) (y (_ BitVec 8))) (and (not (= x y)) (= (f x) (f y)) (Pred x) (Pred y))))\n(check-sat)";
		System.out.println(z3script);
		return CallZ3.CallByString(z3script);
	}
	
	static public String invertFuncFromString(String s) throws Exception{
		String script = "(set-logic BV) (set-logic LIA)\n";
		CmdNode Root = (CmdNode)SMTDriver.GetSMTRootFromString(s).value;
		List<TypeNode> intypes = Root.getIntype();
		TypeNode outtype = Root.getOuttype();
		String funcname = Root.getFuncName();
		List<String> varlist = Root.getVarList();
		script += s + getGrammar("inverted_"+funcname,varlist) + "\n";
		for(int i=0;i<varlist.size();i++) script +="(declare-var " + varlist.get(i) +" (BitVec 8))\n"; 
		script += "(constraint (= "+varlist.get(0)+" ("+funcname+" (inverted_"+funcname+" "+varlist.get(0)+"))))\n(check-synth)";
		//script += "(constraint (= "+varlist.get(0)+" (inverted_"+funcname+" ("+funcname+" "+varlist.get(0)+"))))\n(check-synth)";
		s = CallCVC4.CallByString(script);
		return s;
	}
	
	// Given a transition t and its input output types, this method will invert it
	static public String invertPred(TransitionNode t, TypeNode intype, TypeNode outtype) throws InterruptedException{
		String result = "";
		String z3script = "";
		List<String> outpattern = getPattern(t.getOutputFuncList().size());
		List<String> inpattern = t.getPattern();
		for(int i=0; i<outpattern.size()-1;i++) z3script += "(declare-const "+outpattern.get(i)+" "+outtype.to_String_z3()+")\n";
		for(int i=0; i<funcdefs_z3.size();i++) z3script += funcdefs_z3.get(i);
		String funcdefCMD = "";
		for(int i = 0; i < t.getOutputFuncList().size();i++){
			funcdefCMD += "(define-fun f_"+ (i+1) + " (";
			for(int j = 0; j < inpattern.size()-1; j++) funcdefCMD += "(" + inpattern.get(j) + " " + intype.to_String_z3() + ")";
			funcdefCMD += ") " + outtype.to_String_z3() + t.getOutputFuncList().get(i).getOutputFunc() + ")\n";
		}
		z3script += funcdefCMD;
		String PredCMD = "(define-fun Pred (";
		for(int i = 0; i < inpattern.size()-1; i++) PredCMD += "(" + inpattern.get(i) + " " + intype.to_String_z3() + ")";
		PredCMD += ") Bool" + t.getPred() + ")\n"; 
		z3script += PredCMD + "(assert (exists (";
		for(int i = 0; i < inpattern.size()-1;i++) z3script +="(" + inpattern.get(i) + " " + intype.to_String_z3()+")";
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
		TypeNode intype = prog.getInType();
		TypeNode outtype = prog.getOutType();
		List<TransitionNode> TransList = prog.getTransList();
		ProgheadNode InvertedProghead = new ProgheadNode("inverted_"+prog.getName(), new InputNode("", intype),outtype);
		List<TransitionNode> invertedTransList = new ArrayList<TransitionNode>();
		for(int i = 0; i < TransList.size(); i++){
			invertedTransList.add(InvertTransition(TransList.get(i),intype,outtype));
		}
		ProgNode InvertedProg = new ProgNode(InvertedProghead, new ProgbodyNode("l", new TransListNode(invertedTransList)));
		return InvertedProg;
	}	
	
	// Given a transition t and its input output types, this method wil
	static public List<String> invertOutputFuncs(TransitionNode t,TypeNode intype, TypeNode outtype) throws Exception{
		List<String> outpattern = getPattern(t.getOutputFuncList().size());
		List<String> inpattern = t.getPattern();
		List<String> invertedOutputFunc = new ArrayList<String>();
		String setlogic = getLogiccommand(intype);
		String funcdefCMD = "";
		for(int i = 0; i < t.getOutputFuncList().size();i++){
			funcdefCMD += "(define-fun f_"+ (i+1) + " (";
			for(int j = 0; j < inpattern.size()-1; j++) funcdefCMD += "(" + inpattern.get(j) + " " + intype.to_String() + ")";
			funcdefCMD += ") " + outtype.to_String() + t.getOutputFuncList().get(i).getOutputFunc() + ")\n";
		}
		String PredCMD = "(define-fun Pred (";
		for(int i = 0; i < inpattern.size()-1; i++) PredCMD += "(" + inpattern.get(i) + " " + intype.to_String() + ")";
		PredCMD += ") Bool" + t.getPred() + ")\n"; 
		String SyGusScript = null;
		for(int i = 0; i < inpattern.size() - 1; i++){
			SyGusScript = setlogic;
			for(int j = 0; j < inverted_funcdefs.size(); j++) SyGusScript += inverted_funcdefs.get(j);
			for(int j = 0; j < funcdefs.size(); j++) SyGusScript += funcdefs.get(j) + "\n";
			SyGusScript += funcdefCMD + PredCMD;
			SyGusScript += "(synth-fun g_" + (i+1) + " (";
			for(int j = 0; j < outpattern.size() - 1; j++) SyGusScript += "(" + outpattern.get(j) + " " + outtype.to_String() + ")";
			SyGusScript += ") " + intype.to_String() + "\n" + getGrammar(intype, outpattern) + ")\n";
			for(int j = 0; j < inpattern.size() - 1; j++) SyGusScript += "(declare-var " + inpattern.get(j) + " " + outtype.to_String() + ")\n";
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
	public static String to_String_z3(String s) throws Exception{
		Symbol root = SMTDriver.GetSMTRootFromString(s);
		CmdNode Root = (CmdNode) root.value;
		return Root.to_String_z3();
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
	
	static public List<String> getPattern(Integer l){
		List<String> outpattern = new ArrayList<String>();
		for(int i = 0; i < l; i++)
			outpattern.add( "y_" + (i+1));
		outpattern.add("tail");
		return outpattern;
	}
	// we assume all types are bv 8 here
	public static String getGrammar(String name,List<String> varlist){
		String result = "(synth-fun "+name+" (";
		for(int i = 0; i < varlist.size(); i++) result += "(" + varlist.get(i) + " (BitVec 8))";
		
		result += ") (BitVec 8)\n\t ((Start (BitVec 8) ((bvnot Start)\n\t(ite StartBool Start Start)\n\t(bvxor Start Start)\n\t(bvand Start Start)\n\t(bvor Start Start)\n\t(bvneg Start)\n\t(bvadd Start Start)\n\t(bvmul Start Start)\n\t(bvudiv Start Start)\n\t(bvurem Start Start)\n\t(bvlshr Start Start)\n\t(bvashr Start Start)\n\t(bvshl Start Start)\n\t(bvsdiv Start Start)\n\t(bvsrem Start Start)\n\t(bvsub Start Start)\n\t";
		for(int i = 0; i < varlist.size(); i++) result += varlist.get(i) + " ";
		result+= "#x00 #xFF #x01 #x02 #x03 #x04 #x05 #x06 #x07 #x08 #x09 #x0a #x0b #x0c #x0d #x0e #x0f #x10 #x20 #x30 #x40 #x50 #x60 #x70 #x80 #x90 #xa0 #xb0 #xc0 #xd0 #xe0 #xf0))\n\t(StartBool Bool ((and StartBool StartBool)\n\t(or StartBool StartBool)\n\t(not StartBool)\n\t(bvule Start Start)\n\t(= Start Start)\n\t(bvuge Start Start)))))\n";

		return result;
	}

	public static String getGrammar(TypeNode t, List<String> vlist) throws Exception{
		String result = null;
		if(t.getType() == Type.BV){
			result = "\t((Start (BitVec 8) ((bvnot Start)\n\t(ite StartBool Start Start)\n\t(bvxor Start Start)\n\t(bvand Start Start)\n\t(bvor Start Start)\n\t(bvneg Start)\n\t(bvadd Start Start)\n\t(bvmul Start Start)\n\t(bvudiv Start Start)\n\t(bvurem Start Start)\n\t(bvlshr Start Start)\n\t(bvashr Start Start)\n\t(bvshl Start Start)\n\t(bvsdiv Start Start)\n\t(bvsrem Start Start)\n\t(bvsub Start Start)\n\t";
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

	static public String getVarList(List<String> varnames, TypeNode type){
		String result = "";
		for(int i = 0; i<varnames.size();i++) result+="("+varnames.get(i)+" "+type.to_String()+")";
		return result;
	}
	static public String getVarNameList(List<String> varnames){
		String result = "";
		for(int i = 0; i<varnames.size();i++) result+=" "+varnames.get(i);
		return result;
	}
	static public String getVarList_z3(List<String> varnames, TypeNode type){
		String result = "";
		for(int i = 0; i<varnames.size();i++) result+="("+varnames.get(i)+" "+type.to_String_z3()+")";
		return result;
	}
}