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
		for(int i = 0; i < mylist.size(); i++){
			mylist.get(i).print_this();
		}		
	}
	@Override
	public String to_String_z3() {
		String result = "";
		for(int i = 0; i < mylist.size(); i++){
			result += mylist.get(i).to_String_z3();
		}		
		return result;
	}
}
