package Parser;
import java.io.*;
import java.util.*;

//**********************************************************************
//
//  Subclass            Kids
//  --------            ----
//  CoderNode			DeclListNode, ProgListNode, QueryListNode
//  DeclListNode        linked list of DeclNode
//	ProgListNode        linked list of ProgNode
//	QueryListNode       linked list of QueryNode
//  DeclNode			String
//
//Here are the different kinds of AST nodes again, organized according to
//whether they are leaves, internal nodes with linked lists of kids, or
//internal nodes with a fixed number of kids:
//
//(1) Leaf nodes:
//    
//
//(2) Internal nodes with (possibly empty) linked lists of children:
//    
//
//(3) Internal nodes with fixed numbers of kids:
//
//**********************************************************************

//**********************************************************************
//ASTnode class (base class for all other kinds of nodes)
//**********************************************************************

class Type{
	public final  static Integer INT = 0;
	public final static Integer BOOL =1;
	public final static Integer CHAR =2;
	public final static Integer BV =3;
}

abstract class ASTnode { 
	abstract public void print_this();
}

class CoderNode extends ASTnode{
	public CoderNode(DeclListNode D, ProgListNode P, QueryListNode Q){
		myDeclList = D;
		myProgList = P;
		myQueryList = Q;
	}
	
	private DeclListNode myDeclList;
	private ProgListNode myProgList;
	private QueryListNode myQueryList;
	@Override
	public void print_this() {
		myDeclList.print_this();
		System.out.println("===");
		myProgList.print_this();
		System.out.println("===");
		myQueryList.print_this();
	}
}

class DeclListNode extends ASTnode{
	public DeclListNode(List<DeclNode> D){
		myDecls = D;
	}
	private List<DeclNode> myDecls;
	@Override
	public void print_this() {
		for(DeclNode decl: myDecls){
			decl.print_this();
		}
	}
}

class ProgListNode extends ASTnode{
	public ProgListNode(List<ProgNode> P){
		myProgs = P;
	}
	private List<ProgNode> myProgs;
	@Override
	public void print_this() {
		for(ProgNode prog: myProgs){
			prog.print_this();
		}
		
	}
}

class QueryListNode extends ASTnode{
	public QueryListNode(List<QueryNode> Q){
		myQueries = Q;
	}
	private List<QueryNode> myQueries;
	@Override
	public void print_this() {
		for(QueryNode query: myQueries){
			query.print_this();
		}
		
	}
}

class QueryNode extends ASTnode{
	public QueryNode(String idstr){
		myQuerystr = idstr;
	}
	private String myQuerystr;
	@Override
	public void print_this() {
		System.out.println("Query: " + myQuerystr);
	}
}

class DeclNode extends ASTnode{
	public DeclNode(String s){
		myDecl = s;
	}
	private String myDecl;
	public String getmyDecl(){
		return myDecl;
	}
	@Override
	public void print_this() {
		System.out.println("Decl: " + myDecl);
	}
}

class ProgNode extends ASTnode{
	public ProgNode(ProgheadNode ph, ProgbodyNode pb){
		myProghead = ph;
		myProgbody = pb;
		ProgName = ph.getName();
		myType = ph.getType();
		myInput = ph.getInput();
	}
	private ProgheadNode myProghead;
	private ProgbodyNode myProgbody;
	private InputNode myInput;
	private String ProgName;
	private TypeNode myType;
	@Override
	public void print_this() {
		System.out.print("Program " + ProgName + ": ");
		myType.print_this();
		System.out.print(" with input ");
		myInput.print_this();
		myProgbody.print_this();
	}
}

class ProgheadNode extends ASTnode{
	public ProgheadNode(String idstr, InputNode innode, TypeNode type){
		myName = idstr;
		myInput = innode;
		myType = type;
	}
	private String myName;
	private TypeNode myType;
	private InputNode myInput;
	public String getName(){
		return myName;
	}
	public TypeNode getType(){
		return myType;
	}
	public InputNode getInput(){
		return myInput;
	}
	@Override
	public void print_this() {
		System.out.print("Program " + myName + ": ");
		myType.print_this();
		System.out.print("with input ");
		myInput.print_this();
	}
}

class InputNode extends ASTnode{
	public InputNode(String idstr, TypeNode t){
		inputName = idstr;
		inputType = t;
	}
	private String inputName;
	private TypeNode inputType;
	private String getInputName(){
		return inputName;
	}
	public TypeNode getTypeNode(){
		return inputType;
	}
	@Override
	public void print_this() {
		System.out.print(inputName + ": ");
		inputType.print_this();
		System.out.println();
	}
}

class TypeNode extends ASTnode{
	public TypeNode(Integer t){
		type = t;
		length = null;
	}
	public TypeNode(Integer t, Integer l){
		type = t;
		length = l;
	}
	private Integer type;
	private Integer length;
	public Integer getType(){
		return type;
	}
	public Integer getLength(){
		return length;
	}
	@Override
	public void print_this() {
		if(type == Type.INT){
			System.out.print("Int");
		}else if(type == Type.BOOL){
			System.out.print("Bool");
		}else if(type == Type.CHAR){
			System.out.print("Char");
		}else if(type == Type.BV){
			System.out.print("(BitVector " + length + ")");
		}
		
	}
}

class ProgbodyNode extends ASTnode{
	public ProgbodyNode(String idstr, CaseListNode cl){
		matchName = idstr;
		myCaseListNode = cl;
	}
	private String matchName;
	private CaseListNode myCaseListNode;
	
	public String getName(){ return matchName;}
	public CaseListNode getCaseslist(){ return myCaseListNode; }
	@Override
	public void print_this() {
		System.out.print("Match " + matchName + " with");
		myCaseListNode.print_this();
	}
}

class CaseListNode extends ASTnode{
	public CaseListNode(List<CaseNode> cl){
		myCases = cl;
	}
	private List<CaseNode> myCases;
	@Override
	public void print_this() {
		for(CaseNode Case: myCases){
			System.out.print("	| ");
			Case.print_this();
		}
		
	}
}

class CaseNode extends ASTnode{
	public CaseNode(List<String> p, PredNode predstr, OutputNode o){
		myPattern = p;
		myPred = predstr;
		myOutput = o;
	}
	private List<String> myPattern;
	private PredNode myPred;
	private OutputNode myOutput;
	@Override
	public void print_this() {
		System.out.print(myPattern.get(0));
		for(String s: myPattern){
			if(s != myPattern.get(0))
			System.out.print("::"+s);
		}
		System.out.print(" when ");
		myPred.print_this();
		myOutput.print_this();
		
	}
}

class PredNode extends ASTnode{
	public PredNode(String pstr){
		myPredstr = pstr;
	}
	private String myPredstr;
	@Override
	public void print_this() {
		System.out.print("Pred:" + myPredstr + " ");
		
	}
}

class OutputNode extends ASTnode{
	public OutputNode(OutputFuncListNode ofl, FuncNode f){
		myOFL = ofl;
		myFunc = f;
	}
	private OutputFuncListNode myOFL;
	private FuncNode myFunc;
	@Override
	public void print_this() {
		System.out.print("Output: ");
		myOFL.print_this();
		System.out.print(" ++ ");
		myFunc.print_this();
	}
}

class OutputFuncListNode extends ASTnode{
	public OutputFuncListNode(List<OutputFuncNode> ofl){
		myOFNodes = ofl;
	}
	private List<OutputFuncNode> myOFNodes;
	@Override
	public void print_this() {
		for(OutputFuncNode ofn: myOFNodes){
			ofn.print_this();
		}
		
	}
}

class OutputFuncNode extends ASTnode{
	public OutputFuncNode(String s){
		myOstr = s;
	}
	private String myOstr;
	@Override
	public void print_this() {
		System.out.print(myOstr+";");
		
	}
}

class FuncNode extends ASTnode{
	public FuncNode(String str1, String str2){
		funcName = str1;
		varName = str2;
	}
	private String funcName;
	private String varName;
	@Override
	public void print_this() {
		System.out.println(funcName + "(" + varName + ");");
	}
}




