package SMTAst;



public class NumconstNode extends SMTASTNode{
	public NumconstNode(Integer i, String s){
		setMytype(i);
		content = s;
	}
	
	public final static Integer NUM = 0;
	public final static Integer BIN = 1;
	public final static Integer HEX = 2;
	public final static Integer BV = 3;
	public final static Integer RAT = 4;
	
	private Integer mytype;
	private String content;
	@Override
	public void print_this() {
		System.out.print(content);
	}
	public Integer getMytype() {
		return mytype;
	}
	public void setMytype(Integer mytype) {
		this.mytype = mytype;
	}
	@Override
	public String to_String_z3() {
		return content;
	}
}