package SMTAst;



public class QuantvarNode extends SMTASTNode{
	public QuantvarNode(String s, SortNode sn){
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
}
