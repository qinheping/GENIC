package ast;

public class DeclNode extends ASTnode{
	public DeclNode(String s){
		myDecl = s;
		domain = null;
	}
	public DeclNode(String s, String domain){
		myDecl = s;
		this.domain = domain;
	}
	private String myDecl;
	private String domain;
	public String getmyDecl(){
		return myDecl;
	}
	@Override
	public void print_this() {
		System.out.println("Decl: " + myDecl);
	}
	public String getDomain() {
				return domain;
	}
}