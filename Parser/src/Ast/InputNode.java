package Ast;


public
class InputNode extends ASTnode{
	public InputNode(String idstr, TypeNode t){
		inputName = idstr;
		inputType = t;
	}
	private String inputName;
	private TypeNode inputType;
	private String getInputName(){
		return inputName;
	}
	public TypeNode getTypeNode(){
		return inputType;
	}
	@Override
	public void print_this() {
		System.out.print(inputName + ": ");
		inputType.print_this();
		System.out.println();
	}
}
