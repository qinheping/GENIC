package SMTAst;

import java.util.LinkedList;


public class TermlistNode extends SMTASTNode{
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
