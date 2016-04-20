package ast;

import java.util.List;

public class CoderNode extends ASTnode{
	public CoderNode(DeclListNode D, ProgListNode P, QueryListNode Q){
		myDeclList = D;
		myProgList = P;
		myQueryList = Q;
	}
	public CoderNode(){
		myDeclList = null;
		myProgList = null;
		myQueryList = null;
	}
	
	private DeclListNode myDeclList;
	private ProgListNode myProgList;
	private QueryListNode myQueryList;
	public List<String> getFuncdefs(){
		return myDeclList.getFuncdefs();
	}
	public ProgNode findProg(String name){
		return myProgList.findProg(name);
	}
	
	public void setDeclList(DeclListNode dl){
		myDeclList = dl;
	}
	@Override
	public void print_this() {
		myDeclList.print_this();
		System.out.println("===");
		myProgList.print_this();
		System.out.println("===");
		myQueryList.print_this();
	}
}