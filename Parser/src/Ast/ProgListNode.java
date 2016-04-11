package Ast;

import java.util.List;

public class ProgListNode extends ASTnode{
	public ProgListNode(List<ProgNode> P){
		myProgs = P;
	}
	public ProgListNode(){
		myProgs = null;
	}
	private List<ProgNode> myProgs;
	
	public ProgNode findProg(String name){
		for(int i = 0; i < myProgs.size(); i++){
			if(name.equals(myProgs.get(i).getName()))
				return myProgs.get(i);
		}
		return null;
	}
	
	@Override
	public void print_this() {
		for(ProgNode prog: myProgs){
			prog.print_this();
		}
		
	}
}