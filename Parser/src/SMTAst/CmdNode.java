package SMTAst;


public 
class CmdNode extends SMTASTNode{
	public CmdNode(SMTASTNode n){
		mychild = n;
		childtype = CMDTERM;
	}
	public final static Integer CMDDEF = 0;
	public final static Integer CMDTERM = 1;
	
	private SMTASTNode mychild;
	private int childtype;
	
	public void print_this() {
		mychild.print_this();
		System.out.println();
	}
	
	public Integer gettype(){
		return childtype;
	}
}