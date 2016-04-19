package Driver;

import java.util.*;
import SMTAst.*;

public class SyGusConvertor {
	public SyGusConvertor(){
		setFuncdeflist(new ArrayList<String>());
		synthlist = new ArrayList<String>();
	}
	
	private List<String> funcdeflist;
	private List<String> synthlist;
	
	public String StringToScript(String s){
		String result = "";
		result = result + "(check-synth)";
		return result;
	}
	public void setFuncdeflist(List<String> funcdeflist) {
		this.funcdeflist = funcdeflist;
	} 
	private void addFuncdef(String s){
		funcdeflist.add(s);
	}
	
}