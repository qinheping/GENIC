package ast;


public
class ProgheadNode extends ASTnode{
	public ProgheadNode(String idstr, InputNode innode, TypeNode type){
		myName = idstr;
		myInput = innode;
		myType = type;
	}
	private String myName;
	private TypeNode myType;
	private InputNode myInput;
	public String getName(){
		return myName;
	}
	public TypeNode getType(){
		return myType;
	}
	public InputNode getInput(){
		return myInput;
	}
	@Override
	public void print_this() {
		System.out.print("Program " + myName + ": ");
		myType.print_this();
		System.out.print("with input ");
		myInput.print_this();
	}
}