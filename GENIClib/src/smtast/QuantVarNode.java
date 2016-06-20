package smtast;
import ast.*;


public class QuantVarNode extends SMTASTNode{
	public QuantVarNode(String s, SortNode sn){
		mysymbol = s;
		mysort = sn;
	}
	
	private String mysymbol;
	private SortNode mysort;
	
	@Override
	public void print_this() {
		System.out.print("(" + mysymbol +" ");
		mysort.print_this();
		System.out.print(")");
	}

	@Override
	public String toString_z3() {
		String result = "(" + mysymbol +" " + mysort.toString_z3() + ")";
		return result;
	}	
	public TypeNode getType(){
		return mysort.getType();
	}
	public String getName(){
		return mysymbol;
	}
	public SortNode getSort(){
		return mysort;
	}
}
