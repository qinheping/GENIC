package ast;

import java.util.List;



public
class OutputNode extends ASTnode{
	public OutputNode(OutputFuncListNode ofl, FuncNode f){
		myOFL = ofl;
		myFunc = f;
	}
	private OutputFuncListNode myOFL;
	private FuncNode myFunc;
	public List<OutputFuncNode> getOutputFuncList(){
		return myOFL.getOutputFuncList();
	}
	public FuncNode getFunc(){
		return myFunc;
	}
	@Override
	public void print_this() {
		System.out.print(" [");
		myOFL.print_this();
		System.out.print("]");
		if(myFunc != null){
		System.out.print(" ++ ");
		myFunc.print_this();}else System.out.println("");
	}
}