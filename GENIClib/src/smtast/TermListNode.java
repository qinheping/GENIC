package smtast;

import java.util.LinkedList;
import java.util.List;


public class TermListNode extends SMTASTNode{
	public TermListNode(){
		mylist = new LinkedList<TermNode>();
	}
	
	public TermListNode(TermNode t){
		mylist = new LinkedList<TermNode>();
		mylist.add(t);
	}
	
	public TermListNode(List<TermNode> l){
		mylist = new LinkedList<TermNode>();
		mylist.addAll(l);
	}	
	public boolean containVar(String vname){
		for(TermNode t: mylist){
			if(t.containVar(vname))
				return true;
		}
		return false;
	}
	
	public void add(TermNode t){
		mylist.addLast(t);
	}
	
	private LinkedList<TermNode> mylist;
	
	public List<TermNode> getList(){
		return mylist;
	}
	
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
