package SMTAst;



public class CmddefNode extends SMTASTNode{
	public CmddefNode(String s, QvlistNode qvl, SortNode ast, TermNode t){
		funcname = s;
		funcsort = ast;
		mylist = qvl;
		functerm = t;
	}
	
	private String funcname;
	private SortNode funcsort;
	private TermNode functerm;
	private QvlistNode mylist;
	
	@Override
	public void print_this() {
		System.out.print("(" + funcname + " (");
		mylist.print_this();
		System.out.print(") ");
		funcsort.print_this();
		System.out.print(" ");
		functerm.print_this();
	}
}