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
		if(type == Type.INT){
			System.out.print("Int");
		}else if(type == Type.BOOL){
			System.out.print("Bool");
		}else if(type == Type.CHAR){
			System.out.print("Char");
		}else if(type == Type.BV){
			System.out.print("(BitVec " + length + ")");
		}
		
	}
	
	public String to_String(){
		String result = "";
		if(type == Type.INT){
			result = "Int";
		}else if(type == Type.BOOL){
			result = "bool";
		}else if(type == Type.CHAR){
			result = "Char";
		}else if(type == Type.BV){
			result = "(BitVec " + length + ")";
		}
		return result;
	}
	public String to_String_z3(){
		String result = "";
		if(type == Type.INT){
			result = "Int";
		}else if(type == Type.BOOL){
			result = "bool";
		}else if(type == Type.CHAR){
			result = "Char";
		}else if(type == Type.BV){
			result = "(_ BitVec " + length + ")";
		}
		return result;
	}
}