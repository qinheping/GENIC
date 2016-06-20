package ast;

public
class TypeNode extends ASTnode{
	public TypeNode(Integer t){
		type = t;
		length = null;
	}
	public TypeNode(Integer t, Integer l){
		type = t;
		length = l;
	}
	private Integer type;
	private Integer length;
	public Integer getType(){
		return type;
	}
	public Integer getLength(){
		return length;
	}
	@Override
	public void print_this() {
		if(type == Type.INT){			// 0
			System.out.print("Int");
		}else if(type == Type.BOOL){	// 1
			System.out.print("Bool");
		}else if(type == Type.CHAR){	// 2
			System.out.print("Char");
		}else if(type == Type.BV){		// 3
			System.out.print("(BitVec " + length + ")");
		}
		
	}
	
	public String toString(){
		String result = "";
		if(type == Type.INT){
			result = "Int";
		}else if(type == Type.BOOL){
			result = "Bool";
		}else if(type == Type.CHAR){
			result = "Char";
		}else if(type == Type.BV){
			result = "(BitVec " + length + ")";
		}
		return result;
	}
	public String toString_z3(){
		String result = "";
		if(type == Type.INT){
			result = "Int";
		}else if(type == Type.BOOL){
			result = "Bool";
		}else if(type == Type.CHAR){
			result = "Char";
		}else if(type == Type.BV){
			result = "(_ BitVec " + length + ")";
		}
		return result;
	}
}