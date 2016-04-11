package SMTAst;



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
		System.out.print("( ");
		switch(this.getChildtype()){
		case 0: System.out.print("forall (");
				mylist.print_this();
				System.out.print(") ");
				mychild.print_this();
				break;
		case 1: System.out.print("exists (");
				mylist.print_this();
				System.out.print(") ");
				mychild.print_this();
				break;
		case 2: System.out.println(mysymbol);
				break;
		case 3: mychild.print_this();
				break;
		case 4: mylist.print_this();
				break;
		}
		System.out.print(") ");
	}
	
	public Integer getChildtype() {
		return childtype;
	}
	public void setChildtype(Integer childtype) {
		this.childtype = childtype;
	}
	
}