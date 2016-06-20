package Driver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ast.TypeNode;
import smtast.CmdNode;
import smtast.TermNode;

public class GrammarFactory {
	static int constdepth = 1;
	static List<String> funcdefs;
	static List<String> funcdefs_z3;
	static List<String> inverted_funcdefs;
	
	public GrammarFactory(List<String> fd, List<String> fdz3, List<String> ifd){
		funcdefs = fd;
		funcdefs_z3 = fdz3;
		inverted_funcdefs = ifd;
	}
	
	public static String getSynthBody(String func, int numArgs) throws Exception{
		List<String> varnames = new ArrayList<String>();
		for(int i = 0; i < numArgs; i++){
			varnames.add("y_"+i);
		}
		return getSynthBody(func, varnames);
	}
	
	public String getSynthBody(String name, List<String> funcs, List<String>varnames, TypeNode type) throws Exception{
		String result = "(";
		List<TermNode> terms = new ArrayList<TermNode> ();
		for(String s: funcs) terms.add( ((CmdNode)SMTDriver.GetSMTRootFromString(s).value).getTermNode() );

		String head = "synth-fun " + name + " (" + 
								mainDriver.getVarList(varnames, type) + ") " + type.toString() + "\n";
		String grammar = getGrammar(terms, type, varnames);
		result += head + grammar + ")\n";
		
		return result;
	}
	
	public static String getSynthBody(String func, List<String> varnames) throws Exception{
		CmdNode Root = (CmdNode)SMTDriver.GetSMTRootFromString(func).value;
		String name = Root.getFuncName();
		List<String> varlist = Root.getVarList();
		TermNode term = Root.getTermNode();
		TypeNode type = Root.getOuttype();
		String typeString = type.toString();
		
		String result = "(";
		String head = "synth-fun inverted_" + name + " (" + 
								mainDriver.getVarList(varnames, type) + ") " + type.toString() + "\n";
		String grammar = getGrammar(term, type, varnames);
		result += head + grammar + ")\n";
		return result;
	}
	
	public static String getGrammar(TermNode term, TypeNode type, List<String> varnames){
		if(type.getType() == 0) // INT
			return getGrammar_Int(term, varnames);
		return null;
	}
	
	public static String getGrammar(List<TermNode> terms, TypeNode type, List<String> varnames){
		if(type.getType() == 0) // INT
			return getGrammar_Int(terms, varnames);
		return null;
	}
	
	private static String getGrammar_Int(List<TermNode> terms,
			List<String> varnames) {
		String start = getStart_Int( terms, varnames);
		String constset = getConstSet_Int(terms);
		String varset = getVarSet_Int(varnames);
		String result = "(" + start + varset + constset + ")";
		return result;
	}





	public static String getGrammar_Int(TermNode term, List<String> varnames){
		String start = getStart_Int( term, varnames);
		String constset = getConstSet_Int(term);
		String varset = getVarSet_Int(varnames);
		String result = "(" + start + varset + constset + ")";
		return result;
	}
	
	public static String getVarSet_Int(List<String> varnames){
		if(varnames.size()==0) return "";
		String result = "( VarSet Int (";
		result += mainDriver.getVarNameList(varnames) + "\n";
		result += "))";
		
		return result;
	}
	
	private static String getStart_Int(List<TermNode> terms,
			List<String> varnames) {
		String result = "( Start Int ( ConstSet";
		if(varnames.size() !=0) result+=" VarSet\n";
		for(TermNode term: terms){
			if(term.containFunc("+") || term.containFunc("-")){
				result += "(- Start Start)\n";
				result += "(+ Start Start)\n";
				break;
		}}
		result += "))";
		return result;
	}
	
	public static String getStart_Int(TermNode term, List<String> varnames){
		String result = "( Start Int ( VarSet ConstSet";
		if(term.containFunc("+") || term.containFunc("-")){
			result += "(- Start Start)\n";
			result += "(+ Start Start)\n";
		}
		result += "))";
		return result;
	}
	
	private static String getConstSet_Int(List<TermNode> terms) {
		String result = " (ConstSet Int(";
		Set<String> set = new HashSet<String>();
		for(TermNode term: terms) {
			//System.out.println(term);
			set.addAll(term.getConstSet_Int(constdepth));
		}
		result += expandToString(set);
		result += "))";
		return result;
	}
	
	public static String getConstSet_Int(TermNode term){
		String result = "( ConstSet Int(";
		expandToString(term.getConstSet_Int(constdepth));
		result += "))";
		return result;
	}
	
	public static String expandToString(Set<String> set){
		String result = "";
		for(String s: set)
			result += s + " ";
		return result;
	}
}
