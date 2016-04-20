package SMTast;



public class TermNode extends SMTASTNode{
	public TermNode(QvlistNode qvl, TermNode t, Integer i){
		setChildtype(i);
		mychild = t;
		mylist = qvl;
		mysymbol = null;
	}
	public TermNode(String s){
		mysymbol = s;
		setChildtype(SYMBOL);
		mylist = null;
		mychild = null;
	}
	public TermNode(NumconstNode cn){
		mychild = cn;
		setChildtype(CONSTANT);
		mylist = null;
		mysymbol = null;
	}
	public TermNode(String s, TermlistNode l){
		mysymbol = s;
		mylist = l;
		setChildtype(LIST);
		mychild = null;
	}
	
	public final static Integer FORALL = 0;
	public final static Integer EXISTS = 1;
	public final static Integer SYMBOL = 2;
	public final static Integer CONSTANT = 3;
	public final static Integer LIST = 4;
	
	private SMTASTNode mylist;
	private SMTASTNode mychild;
	private String mysymbol;
	private Integer childtype;
	@Override
	public void print_this() {
		//TODO
	}
	
	public Integer getChildtype() {
		return childtype;
	}
	
	public void setChildtype(Integer childtype) {
		this.childtype = childtype;
	}
	
	@Override
	public String to_String_z3() {
		String result = "";
		switch(this.getChildtype()){
		case 0: result += "(forall (";
				result += mylist.to_String_z3();
				result += ") ";
				result += mychild.to_String_z3() + ") ";
				break;
		case 1: result += "(exists (";
				result += mylist.to_String_z3();
				result += ") ";
				result += mychild.to_String_z3() + ") ";
				break;
		case 2: result += mysymbol + " ";
				break;
		case 3: result += mychild.to_String_z3();
				break;
		case 4: result += "(" + mysymbol + " " + mylist.to_String_z3() + ") ";
				break;
		}
		return result;
	}
	
}