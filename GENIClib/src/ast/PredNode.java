package ast;


public
class PredNode extends ASTnode{
	public PredNode(String pstr){
		myPredstr = pstr;
	}
	private String myPredstr;
	
	public String getPred(){
		return myPredstr;
	}
	@Override
	public void print_this() {
		System.out.print(" " + myPredstr + " ");
		
	}
}
