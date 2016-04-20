package ast;

public class QueryNode extends ASTnode{
	public QueryNode(String idstr){
		myQuerystr = idstr;
	}
	private String myQuerystr;
	@Override
	public void print_this() {
		System.out.println("Query: " + myQuerystr);
	}
}