package SMTParser;
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

abstract class SMTASTNode { 
	abstract public void print_this();
}

class CmdNode extends SMTASTNode{
	public CmdNode(SMTASTNode n){
		mychild = n;
		childtype = CMDTERM;
	}
	public final static Integer CMDDEF = 0;
	public final static Integer CMDTERM = 1;
	
	private SMTASTNode mychild;
	private int childtype;
	
	public void print_this() {
		mychild.print_this();
		System.out.println();
	}
	
	public Integer gettype(){
		return childtype;
	}
}

class TermNode extends SMTASTNode{
	public TermNode(QvlistNode qvl, TermNode t, Integer i){
		setChildtype(i);
		mychild = t;
		mylist = qvl;
		mysymbol = null;
	}
	public TermNode(String s){
		mysymbol = s;
		setChildtype(SYMBOL);
		mylist = null;
		mychild = null;
	}
	public TermNode(NumconstNode cn){
		mychild = cn;
		setChildtype(CONSTANT);
		mylist = null;
		mysymbol = null;
	}
	public TermNode(String s, TermlistNode l){
		mysymbol = s;
		mylist = l;
		setChildtype(LIST);
		mychild = null;
	}
	
	public final static Integer FORALL = 0;
	public final static Integer EXISTS = 1;
	public final static Integer SYMBOL = 2;
	public final static Integer CONSTANT = 3;
	public final static Integer LIST = 4;
	
	private SMTASTNode mylist;
	private SMTASTNode mychild;
	private String mysymbol;
	private Integer childtype;
	@Override
	public void print_this() {
		System.out.print("( ");
		switch(this.getChildtype()){
		case 0: System.out.print("forall (");
				mylist.print_this();
				System.out.print(") ");
				mychild.print_this();
				break;
		case 1: System.out.print("exists (");
				mylist.print_this();
				System.out.print(") ");
				mychild.print_this();
				break;
		case 2: System.out.println(mysymbol);
				break;
		case 3: mychild.print_this();
				break;
		case 4: mylist.print_this();
				break;
		}
		System.out.print(") ");
	}
	
	public Integer getChildtype() {
		return childtype;
	}
	public void setChildtype(Integer childtype) {
		this.childtype = childtype;
	}
	
}

class TermlistNode extends SMTASTNode{
	public TermlistNode(){
		mylist = new LinkedList<TermNode>();
	}
	public void add(TermNode t){
		mylist.addLast(t);
	}
	
	private LinkedList<TermNode> mylist;
	
	@Override
	public void print_this() {
		// TODO Auto-generated method stub
		
	}
}

class QvlistNode extends SMTASTNode{
	public QvlistNode(){
		mylist = new LinkedList<QuantvarNode>();
	}
	
	private LinkedList<QuantvarNode> mylist;
	
	public void add(QuantvarNode n){
		mylist.addLast(n);
	}
	@Override
	public void print_this() {
		for(int i = 0; i < mylist.size(); i++){
			mylist.get(i).print_this();
		}
	}
	
}

class QuantvarNode extends SMTASTNode{
	public QuantvarNode(String s, SortNode sn){
		mysymbol = s;
		mysort = sn;
	}
	
	private String mysymbol;
	private SortNode mysort;
	
	@Override
	public void print_this() {
		System.out.print("(" + mysymbol +" ");
		mysort.print_this();
		System.out.print(")");
	}	
}

class NumconstNode extends SMTASTNode{
	public NumconstNode(Integer i, String s){
		setMytype(i);
		content = s;
	}
	
	public final static Integer NUM = 0;
	public final static Integer BIN = 1;
	public final static Integer HEX = 2;
	public final static Integer BV = 3;
	public final static Integer RAT = 4;
	
	private Integer mytype;
	private String content;
	@Override
	public void print_this() {
		System.out.print(content);
	}
	public Integer getMytype() {
		return mytype;
	}
	public void setMytype(Integer mytype) {
		this.mytype = mytype;
	}
}

class SortNode extends SMTASTNode{
	public SortNode(String s){
		mysymbol = s;
		myparameter = null;
		mytype = SYMBOL;
	}
	public SortNode(String s, Integer i){
		mysymbol = s;
		myparameter = i;
		mytype = QUOTED;
	}
	public final static Integer SYMBOL = 0;
	public final static Integer QUOTED = 1;
	
	private String mysymbol;
	private Integer myparameter;
	private Integer mytype;
	
	@Override
	public void print_this() {
		if(mytype == 0){
			System.out.print(mysymbol);
		} else{
			System.out.println("(_ " + mysymbol + " " + myparameter + ")");
		}
	}
}

class CmddefNode extends SMTASTNode{
	public CmddefNode(String s, QvlistNode qvl, SortNode ast, TermNode t){
		funcname = s;
		funcsort = ast;
		mylist = qvl;
		functerm = t;
	}
	
	private String funcname;
	private SortNode funcsort;
	private TermNode functerm;
	private QvlistNode mylist;
	
	@Override
	public void print_this() {
		System.out.print("(" + funcname + " (");
		mylist.print_this();
		System.out.print(") ");
		funcsort.print_this();
		System.out.print(" ");
		functerm.print_this();
	}
}