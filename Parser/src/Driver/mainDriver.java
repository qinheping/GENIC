package Driver;

import java.io.*;
import java.util.*;

import java_cup.runtime.*;
import Ast.CoderNode;
import Ast.DeclNode;
import Parser.parser;
import Ast.*;

public class mainDriver {
	static List<String> funcdefs;
	static CoderNode Decoder;
	static Symbol GetRoot(String path) throws Exception{
		File file = new File(path);
        InputStream in = new FileInputStream(file);
		Symbol root = new parser().ParsingStreamToAST(in);
		in.close();
		return root;
	}
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
		outpattern.add("l'");
		return outpattern;
	}
	
	static public TransitionNode InvertTransition(TransitionNode t,TypeNode intype, TypeNode outtype) throws InterruptedException{
		List<String> outpattern = getPattern(t.getOutputFuncList().size());
		List<String> inpattern = t.getPattern();
		List<String> invertedOutputFunc = invertOutputFuncs(t,intype,outtype);
		
		String invertedPred = invertPred(t,intype,outtype);
		TransitionNode invertedTrans = new TransitionNode(outpattern, new PredNode(invertedPred), new OutputNode(new OutputFuncListNode(invertedOutputFunc,1),new FuncNode("inverted_"+t.getOutput().getFunc().FuncName(),"l'")));
		return invertedTrans;
	}
	
	static public String invertPred(TransitionNode t, TypeNode intype, TypeNode outtype) throws InterruptedException{
		String result = "";
		String z3script = "";
		List<String> outpattern = getPattern(t.getOutputFuncList().size());
		List<String> inpattern = t.getPattern();
		for(int i=0; i<outpattern.size()-1;i++) z3script += "(declare-const "+outpattern.get(i)+" "+outtype.to_String_z3()+")\n";
		String funcdefCMD = "";
		for(int i = 0; i < t.getOutputFuncList().size();i++){
			funcdefCMD += "(define-fun f_"+ (i+1) + " (";
			for(int j = 0; j < inpattern.size()-1; j++) funcdefCMD += "(" + inpattern.get(j) + " " + intype.to_String_z3() + ")";
			funcdefCMD += ") " + outtype.to_String_z3() + t.getOutputFuncList().get(i).getOutputFunc() + ")\n";
		}
		z3script += funcdefCMD;
		String PredCMD = "(define-fun Pred (";
		for(int i = 0; i < inpattern.size()-1; i++) PredCMD += "(" + inpattern.get(i) + " " + intype.to_String_z3() + ")";
		PredCMD += ") Bool" + t.getPred().getPred() + ")\n"; 
		z3script += PredCMD + "(assert (exists (";
		for(int i = 0; i < inpattern.size()-1;i++) z3script +="(" + inpattern.get(i) + " " + intype.to_String_z3()+")";
		z3script +=") (and  (Pred "+getVarNameList(inpattern.subList(0, inpattern.size()-1))+") (and ";
		for(int i = 0; i < outpattern.size()-1; i++) z3script += "(= "+outpattern.get(i)+" (f_"+(i+1)+" "+getVarNameList(inpattern.subList(0, inpattern.size()-1))+"))\n";
		z3script +="))))\n(apply qe)";
		result = CallZ3.CallByString(z3script);
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
	
	static public ProgNode InvertProg(String progname, CoderNode root) throws InterruptedException{
		ProgNode target = root.findProg(progname);
		return InvertProg(target);
	}
	
	static public ProgNode InvertProg(ProgNode prog) throws InterruptedException{
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
	
	
	public static void main(String args[]) throws Exception {
			Symbol root = GetRoot("src/Driver/in.txt");
    		CoderNode Root = (CoderNode) root.value;
    		funcdefs = Root.getFuncdefs();
    		Decoder = new CoderNode();		
    		List<DeclNode>myDecls = new ArrayList<DeclNode>();
    		for(int i=0; i< funcdefs.size();i++)
    			myDecls.add(new DeclNode(funcdefs.get(i)));
    		Decoder.setDeclList(new DeclListNode(myDecls));
    		
    		ProgNode invertedProg = InvertProg("f",Root);
    		invertedProg.print_this();
    		Root.print_this();
	}
	
	public static String getGrammar(TypeNode t, List<String> vlist){
		String result = null;
		if(t.getType() == Type.BV){
			result = "\t((Start (BitVec 8) ((bvnot Start)\n\t(ite StartBool Start Start)\n\t(bvxor Start Start)\n\t(bvand Start Start)\n\t(bvor Start Start)\n\t(bvneg Start)\n\t(bvadd Start Start)\n\t(bvmul Start Start)\n\t(bvudiv Start Start)\n\t(bvurem Start Start)\n\t(bvlshr Start Start)\n\t(bvashr Start Start)\n\t(bvshl Start Start)\n\t(bvsdiv Start Start)\n\t(bvsrem Start Start)\n\t(bvsub Start Start)\n\t";
			for(int i = 0; i < vlist.size()-1; i++) result += vlist.get(i) + " ";
			result+= "#x00 #xFF #x01 #x02 #x03 #x04 #x05 #x06 #x07 #x08 #x09 #x0a #x0b #x0c #x0d #x0e #x0f #x10 #x20 #x30 #x40 #x50 #x60 #x70 #x80 #x90 #xa0 #xb0 #xc0 #xd0 #xe0 #xf0))\n\t(StartBool Bool ((and StartBool StartBool)\n\t(or StartBool StartBool)\n\t(not StartBool)\n\t(bvule Start Start)\n\t(= Start Start)\n\t(bvuge Start Start))))\n";
		}
		if(t.getType() == Type.INT){
			result = "((Start Int ( 0 1 2 3 ";
			for(int i = 0; i < vlist.size()-1; i++) result += vlist.get(i) + " ";
			result += "(ite BoolExpr Start Start))) (BoolExpr Bool ((< Start Start) (<= Start Start) (> Start Start) (>= Start Start))))";
		}
		return result;
	}
	static public List<String> invertOutputFuncs(TransitionNode t,TypeNode intype, TypeNode outtype) throws InterruptedException{
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
		PredCMD += ") Bool" + t.getPred().getPred() + ")\n"; 
		String SyGusScript = null;
		for(int i = 0; i < inpattern.size() - 1; i++){
			SyGusScript = setlogic + funcdefCMD + PredCMD;
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
}