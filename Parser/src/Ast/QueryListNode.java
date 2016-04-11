package Ast;

import java.util.List;

public class QueryListNode extends ASTnode{
	public QueryListNode(List<QueryNode> Q){
		myQueries = Q;
	}
	private List<QueryNode> myQueries;
	@Override
	public void print_this() {
		for(QueryNode query: myQueries){
			query.print_this();
		}
		
	}
}