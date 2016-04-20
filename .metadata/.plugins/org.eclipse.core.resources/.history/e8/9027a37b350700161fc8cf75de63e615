package SMTAst;
import Ast.*;

public class SortNode extends SMTASTNode{
	public SortNode(String s){
		mysymbol = s;
		myparameter = null;
		mytype = SYMBOL;
	}
	public SortNode(String s, Integer i){
		mysymbol = s;
		myparameter = i;
		mytype = QUOTED;
	}
	public final static Integer SYMBOL = 0;
	public final static Integer QUOTED = 1;
	
	private String mysymbol;
	private Integer myparameter;
	private Integer mytype;
	
	@Override
	public void print_this() {
		if(mytype == 0){
			System.out.print(mysymbol);
		} else{
			System.out.println("(_ " + mysymbol + " " + myparameter + ")");
		}
	}
	@Override
	public String to_String_z3() {
		String result;
		if(mytype == 0){
			result = mysymbol;
		} else{
			result = "(_ " + mysymbol + " " + myparameter + ")";
		}
		return result;
	}
	
	public TypeNode getType(){
		if(mysymbol.equals("Int"))
			return new TypeNode(Type.INT);
		if(mysymbol.equals("BitVec"))
			return new TypeNode(Type.BV,myparameter);
		return null;
	}
}