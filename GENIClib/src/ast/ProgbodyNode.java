package ast;

import java.util.List;




public
class ProgbodyNode extends ASTnode{
	public ProgbodyNode(String idstr, TransListNode cl){
		matchName = idstr;
		myTransListNode = cl;
	}
	private String matchName;
	private TransListNode myTransListNode;
	
	public List<TransitionNode> getTransList(){
		return myTransListNode.getTransList();
	}
	
	public String getName(){ return matchName;}
	public TransListNode getCaseslist(){ return myTransListNode; }
	@Override
	public void print_this() {
		System.out.println("match " + matchName + " with");
		myTransListNode.print_this();
	}
}