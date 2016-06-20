package smtast;
import java.util.List;

import ast.*;


public class DefCmdNode extends SMTASTNode{
	public DefCmdNode(String s, QVListNode qvl, SortNode ast, TermNode t){
		funcname = s;
		funcsort = ast;
		mylist = qvl;
		functerm = t;
	}
	
	private String funcname;
	private SortNode funcsort;
	private TermNode functerm;
	private QVListNode mylist;
	
	@Override
	public void print_this() {
		System.out.print("(define-fun " + funcname + " (");
		mylist.print_this();
		System.out.print(") ");
		funcsort.print_this();
		System.out.print(" ");
		functerm.print_this();
		System.out.print(")");
	}
	public List<TypeNode> getIntype(){
		return mylist.getIntype();
	}	
	public List<String> getVarList(){
		return mylist.getVarList();
	}	
	public QVListNode getQVListNode(){
		return mylist;
	}	
	public TermNode getTermNode(){
		return functerm;
	}
	public TypeNode getOuttype(){
		return funcsort.getType();
	}
	public String getFuncName(){
		return funcname;
	}
	
	public SortNode getSort(){
		return funcsort;
	}
	
	@Override
	public String toString_z3() {
		String result = "(define-fun " + funcname + " (";
		result += mylist.toString_z3();
		result += ") ";
		result += funcsort.toString_z3();
		result += " " + functerm.toString_z3() + ")";
		return result;
	}
}