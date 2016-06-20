package smtast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



public class TermNode extends SMTASTNode{
	public TermNode(QVListNode qvl, TermNode t, Integer i){
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
		symboltype = true;
	}
	public TermNode(NumConstNode cn){
		mychild = cn;
		setChildtype(CONSTANT);
		mylist = null;
		mysymbol = null;
	}
	public TermNode(String s, TermListNode l){
		mysymbol = s;
		mylist = null;
		setChildtype(LIST);
		mychild = l;
		symboltype = false;
	}

	public final static Integer FORALL = 0;
	public final static Integer EXISTS = 1;
	public final static Integer SYMBOL = 2;
	public final static Integer CONSTANT = 3;
	public final static Integer LIST = 4;
	public final static boolean FUNC = false;
	public final static boolean VAR = true;
	
	public List<String> getVarList(){
		List<String> result = new ArrayList();
		switch(this.getChildtype()){
		case 0: 
		case 1: 
		case 2: result.add(mysymbol);
				break;
		case 3: return result;
		case 4: result.addAll(((TermListNode)mychild).getVarList());
		}
		return result;
	}
	
	public boolean containVar(String vname){

		switch(this.getChildtype()){
		case 0: 
		case 1: return ((TermNode)mychild).containVar(vname);
		case 2: return mysymbol.equals(vname);
		case 3: return false;
		case 4: return ((TermListNode)mychild).containVar(vname);
		}
		return false;
	}
	
	public boolean containFunc(String fname){
		switch(this.getChildtype()){
		case 0: 
		case 1: 
		case 2: 
		case 3: return false;
		case 4: return mysymbol.equals(fname);
		}
		return false;
	}
	
	public Set<String> getConstSet_Int(){
		Set<Integer> constset = new HashSet<Integer>();
		Set<String> result = new HashSet<String>();
		switch(this.getChildtype()){
		case 0: 
		case 1: 
		case 2: break;
		case 3: 
				if(((NumConstNode)mychild).getContent().matches("\\d+")) result.add(((NumConstNode)mychild).getContent());
				break;
		case 4: result.addAll(((TermListNode)mychild).getConstSet_Int());
		}
		return result;
	}
	
	public Set<String> getConstSet_Int(int depth){
		Set<String> result = getConstSet_Int();
		Set<Integer> constset = new HashSet<Integer>();
		for(String s: result) {
			constset.add(Integer.parseInt(s));
		}
		for(int i = 0; i < depth; i++)
			expand(constset);
		for(Integer n: constset) result.add(n.toString());
		return result;
	}
	
	public Set<Integer> expand(Set<Integer> constset){
		constset.add(1);
		constset.add(0);
		Set<Integer> temp = new HashSet<Integer>();
		for(Integer x:constset){
			for(Integer y:constset){
				temp.add(x+y);
				if(x >= y)
					temp.add(x-y);
			}
		}
		constset.addAll(temp);
		return constset;
	}
	
	private boolean symboltype;
	private SMTASTNode mylist;
	private SMTASTNode mychild;
	private String mysymbol;
	private Integer childtype;
	@Override
	public void print_this() {
		//TODO
	}
	public String getSymbol(){
		return mysymbol;
	}
	public boolean isFuncSymbol(){
		return !symboltype;
	}
	public boolean isVarSymbol(){
		return symboltype;
	}
	public QVListNode getQVList(){
		return (QVListNode) mylist;
	}
	public SMTASTNode getChild(){
		return mychild;
	}
	public Integer getChildtype() {
		return childtype;
	}
	
	public void setChildtype(Integer childtype) {
		this.childtype = childtype;
	}
	
	@Override
	public String toString() {
		String result = "";
		switch(this.getChildtype()){
		case 0: result += "(forall (";
				result += mylist.toString_z3();
				result += ") ";
				result += mychild.toString_z3() + ") ";
				break;
		case 1: result += "(exists (";
				result += mylist.toString_z3();
				result += ") ";
				result += mychild.toString_z3() + ") ";
				break;
		case 2: result += " "+mysymbol + " ";
				break;
		case 3: result += mychild.toString_z3();
				break;
		case 4: result += "(" + mysymbol + " " + mychild.toString_z3() + ") ";
				break;
		}
		return result;
	}
	@Override
	public String toString_z3() {
		String result = "";
		switch(this.getChildtype()){
		case 0: result += "(forall (";
				result += mylist.toString_z3();
				result += ") ";
				result += mychild.toString_z3() + ") ";
				break;
		case 1: result += "(exists (";
				result += mylist.toString_z3();
				result += ") ";
				result += mychild.toString_z3() + ") ";
				break;
		case 2: result += mysymbol + " ";
				break;
		case 3: result += mychild.toString_z3();
				break;
		case 4: result += "(" + mysymbol + " " + mychild.toString_z3() + ") ";
				break;
		}
		return result;
	}
	
}