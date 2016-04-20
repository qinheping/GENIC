package ast;

import java.util.ArrayList;
import java.util.List;



public
class OutputFuncListNode extends ASTnode{
	public OutputFuncListNode(List<OutputFuncNode> ofl){
		myOFNodes = ofl;
	}
	public OutputFuncListNode(List<String> ofl, int length){
		myOFNodes = new ArrayList<OutputFuncNode>();
		for(int i = 0; i < ofl.size(); i++){
			myOFNodes.add(new OutputFuncNode(ofl.get(i)));
		}
	}
	private List<OutputFuncNode> myOFNodes;
	public List<OutputFuncNode> getOutputFuncList(){
		return myOFNodes;
	}
	@Override
	public void print_this() {
		for(OutputFuncNode ofn: myOFNodes){
			ofn.print_this();
		}
		
	}
}