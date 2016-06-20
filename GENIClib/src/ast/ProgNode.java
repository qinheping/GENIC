package ast;

import java.util.List;



public
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
	
	public List<TransitionNode> getTransList(){
		return myProgbody.getTransList();
	}
	
	public String getName(){
		return ProgName;
	}
	public TypeNode getInType(){
		return myInput.getTypeNode();
	}
	public TypeNode getOutType(){
		return myType;
	}
	
	@Override
	public void print_this() {
		System.out.print("\n function " + ProgName + " ");
		myType.print_this();
		System.out.print(" ");
		myInput.print_this();
		myProgbody.print_this();
	}
}
