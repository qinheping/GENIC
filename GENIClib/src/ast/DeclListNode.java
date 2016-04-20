package ast;

import java.util.ArrayList;
import java.util.List;

public class DeclListNode extends ASTnode{
	public DeclListNode(List<DeclNode> D){
		myDecls = D;
	}
	
	public List<String> getFuncdefs(){
		List<String> result = new ArrayList<String>();
		for(int i = 0; i < myDecls.size(); i++){
			result.add(myDecls.get(i).getmyDecl());
		}
		return result;
	}
	private List<DeclNode> myDecls;
	@Override
	public void print_this() {
		for(DeclNode decl: myDecls){
			decl.print_this();
		}
	}
}