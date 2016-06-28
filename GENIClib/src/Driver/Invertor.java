package Driver;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.microsoft.z3.ApplyResult;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Goal;
import com.microsoft.z3.Log;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Sort;
import com.microsoft.z3.Status;
import com.microsoft.z3.Symbol;

import ast.TypeNode;
import smtast.DefCmdNode;
import smtast.SortNode;
import smtast.TermNode;
import z3factory.Z3Factory;

public class Invertor {
	static String var;
	static Context ctx;
	static Z3Factory factory;
	static TypeNode type;
	static Sort sort;
	static public String invertUnaryFunc(String fstring, String domainstring) throws Exception{
		com.microsoft.z3.Global.ToggleWarningMessages(true);
        Log.open("test.log");
		HashMap<String, String> cfg = new HashMap<String, String>();
        cfg.put("model", "true");
        ctx = new Context(cfg);
		String result = "";
		//System.out.println(fstring);
		//System.out.println(domainstring);
		DefCmdNode fdecl = (DefCmdNode) mainDriver.toCmdNode(fstring).getChild();
		DefCmdNode domaindecl = (DefCmdNode) mainDriver.toCmdNode(domainstring).getChild();

		var = fdecl.getVarList().get(0);
		type = fdecl.getOuttype();
		sort = getSort(fdecl.getOuttype(), ctx);

		Map<String, Sort> sortstable = new HashMap<String, Sort>();
		Map<String, FuncDecl> funcdeclstable = new HashMap<String, FuncDecl>();
		sortstable.put(var, sort);
		
		TermNode domainterm = domaindecl.getTermNode();
		//System.out.println(domainstring);
		factory = new Z3Factory(sortstable, funcdeclstable, ctx);
		
		BoolExpr domain = (BoolExpr) factory.TermNodeToExpr(domainterm);
		//System.out.println(domain);
		TermNode fterm = fdecl.getTermNode();
		return unification(fterm,domain).getKey();
		
	}
	private static Entry<String, BoolExpr> unification(TermNode fterm, BoolExpr domain) throws Exception {
		if(fterm.getChildtype() == 4){
			if(fterm.getSymbol().equals("ite")){
				BoolExpr truecondition = ctx.mkAnd(domain, (BoolExpr)factory.TermNodeToExpr(fterm.getArgs().get(0)));
				BoolExpr falsecondition = ctx.mkAnd(domain, ctx.mkNot((BoolExpr)factory.TermNodeToExpr(fterm.getArgs().get(0))));
				Entry<String, BoolExpr> trueaction = unification(fterm.getArgs().get(1), truecondition);
				Entry<String, BoolExpr> falseaction = unification(fterm.getArgs().get(2), falsecondition);
				
				return null;
			}
			return new AbstractMap.SimpleEntry<String, BoolExpr>( invert(fterm, domain), getRange(fterm, domain));
		}
		if(fterm.getChildtype() == 3 ){
			Solver s = ctx.mkSolver();
			s.add(domain);
			Model m = null;
			if (s.check() == Status.SATISFIABLE)
				m = s.getModel();
			String value = toFormat(type, m.getConstInterp(m.getConstDecls()[0]).toString());
			BoolExpr range = ctx.mkEq(factory.TermNodeToExpr(fterm), ctx.mkConst(var,sort));
			return new AbstractMap.SimpleEntry<String, BoolExpr>( value, range); 
			
		}
			
		return null;
	}
	
	private static BoolExpr getRange(TermNode fterm, BoolExpr domain) throws Exception {
		int size = 1;
		
		Symbol[] names = new Symbol[size];
		names[0] = ctx.mkSymbol("T");
		
		Expr[] vars = new Expr[size];
		vars[0] = getVar(var,type,ctx);
		
		Expr[] boundvars = new Expr[size];
		boundvars[0] = ctx.mkBound(0, sort);
		
		Sort[] sorts = new Sort[size];
		sorts[0] = sort;
		
		Expr[] decodevars = new Expr[size];
		
		BoolExpr eq = ctx.mkTrue();
		Expr func = factory.TermNodeToExpr(fterm).substitute(vars, boundvars);
		
		BoolExpr pred = domain;
		
		decodevars[0] = getVar("Y", type, ctx);
		
		eq = ctx.mkAnd(eq, ctx.mkEq(decodevars[0], func));
		eq = ctx.mkAnd(eq, pred);
		BoolExpr q = ctx.mkExists(sorts, names, eq, 1, null, null,
                null, null );
		Solver s = ctx.mkSolver();
        s.add(q);
        Model m = null;
        if (s.check() == Status.SATISFIABLE)
        	m = s.getModel();
        //System.out.println(m);
        Goal g = ctx.mkGoal(true, true, false);
        g.add(q);
        //System.out.println(g.AsBoolExpr());
        ApplyResult ar = ctx.mkTactic("qe").apply(g);
		String result = "(and ";
		String[] lines = ar.toString().split(System.getProperty("line.separator"));
		for(int i = 2; i < lines.length; i++){
			result += lines[i];
		}
        System.out.println(result);
		return (BoolExpr) factory.StringToExpr(result);
	}

	
	private static Expr getVar(String var2, TypeNode type2, Context ctx2) {
		if(type2.getType() == 0)
			return ctx2.mkConst(var, ctx2.mkIntSort());
		if(type2.getType() == 3)
			return ctx2.mkConst(var, ctx2.mkBitVecSort(type2.getLength()));
		return null;
	}
	static public String invert(TermNode fterm, BoolExpr domain) throws Exception{
		String name = "g";
		List<String> funcs = new ArrayList();
		funcs.add(mainDriver.stringToFuncDef(fterm.toString(), "f", type));
		List<String> encodeVars = new ArrayList();
		encodeVars.add(var);		
		List decodeVars = new ArrayList<String>();
		decodeVars.add("Y");
		String result = mainDriver.invertFuncs(name, funcs, encodeVars, decodeVars, 
				mainDriver.stringToFuncDef(domain.toString(), "domain", type, new TypeNode(1)).toString(), type, var);

		return null;
	}
	
	static public Sort getSort(TypeNode s, Context context){
		if(s.getType() == 3)
			return context.mkBitVecSort(s.getLength());
		return null;
	}	
	static public String toFormat(TypeNode type, String s){
		if(type.getType() == 0)
			return s;
		if(type.getType() == 3)
			return "#x" + String.format("%0"+ (type.getLength()/4) +"X", Integer.parseInt(s));
		return null;
	}
}
