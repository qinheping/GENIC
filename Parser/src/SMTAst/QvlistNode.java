package SMTAst;

import java.util.LinkedList;


public class QvlistNode extends SMTASTNode{
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