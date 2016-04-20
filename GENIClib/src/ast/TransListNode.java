package ast;

import java.util.List;



public class TransListNode extends ASTnode{
	public TransListNode(List<TransitionNode> cl){
		myTrans = cl;
	}
	
	public List<TransitionNode>  getTransList(){
		return myTrans;
	}
	
	private List<TransitionNode> myTrans;
	@Override
	public void print_this() {
		for(TransitionNode Case: myTrans){
			System.out.print("	| ");
			Case.print_this();
		}
		
	}
}