package Driver;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.microsoft.z3.ApplyResult;
import com.microsoft.z3.BitVecExpr;
import com.microsoft.z3.BitVecSort;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Goal;
import com.microsoft.z3.Log;
import com.microsoft.z3.Model;
import com.microsoft.z3.Params;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Sort;
import com.microsoft.z3.Status;
import com.microsoft.z3.Symbol;
import com.microsoft.z3.enumerations.Z3_sort_kind;

import ast.TypeNode;
import smtast.CmdNode;
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


				BoolExpr range = ctx.mkOr(trueaction.getValue(), falseaction.getValue());
				String action = "(ite " + trueaction.getValue().toString() + " " + trueaction.getKey()
					+ " " + falseaction.getKey() + ")";
				return new AbstractMap.SimpleEntry<String, BoolExpr>( action, range); 
			}
			Entry<String, BoolExpr> result = new AbstractMap.SimpleEntry<String, BoolExpr>( invert(fterm, domain),
					getRange(fterm, domain));
			return result;
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
		
		BoolExpr pred = (BoolExpr) domain.substitute(vars[0], boundvars[0]);
		
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
        //System.out.println(g);
        BoolExpr range = ctx.mkTrue();
        BoolExpr[] formulas = ar.getSubgoals()[0].getFormulas();
        for(BoolExpr e: formulas){
        	range = (BoolExpr) ctx.mkAnd(range, e).simplify();
        }
        if(sort.getSortKind() == Z3_sort_kind.Z3_BV_SORT)
        	range = toIntervals(ctx, range, ctx.mkConst(var, sort));
        
		String result = "(and ";
		String[] lines = ar.toString().split(System.getProperty("line.separator"));
		for(int i = 2; i < lines.length; i++){
			result += lines[i];
		}
        //System.out.println(range);
		
		return range;
	}

	
	private static BoolExpr toIntervals(Context ctx2, BoolExpr range, Expr mkConst) {
		int length = ((BitVecSort)mkConst.getSort()).getSize();
		Map<Integer, Integer> intervals = new HashMap();
		Integer curr_place = 0;
		boolean flag = false;

		for(int i = 0; i < Math.pow(2, length); i++){
			Expr key = ctx2.mkBV(i, length);
			BoolExpr q = ctx2.mkAnd(ctx2.mkEq(key, mkConst), range);
			Solver s = ctx2.mkSolver();
			s.add(q);
			if(flag){
				if(s.check() != Status.SATISFIABLE){
					intervals.put(curr_place, i - 1);
					flag = false;
				}
			}else{
				if(s.check() == Status.SATISFIABLE){
					curr_place = i;
					flag = true;
				}
			}
		}
		if(flag) intervals.put(curr_place, (int) (Math.pow(2, length)-1));
		BoolExpr result = ctx.mkFalse();
		for(Integer e: intervals.keySet()){
			Expr from = ctx2.mkBV(e, length);
			Expr to = ctx2.mkBV(intervals.get(e), length);
			if(e != intervals.get(e))
				result = ctx2.mkOr(ctx2.mkAnd(ctx2.mkBVULE((BitVecExpr)mkConst, (BitVecExpr)to), 
						ctx2.mkBVUGE((BitVecExpr)mkConst, (BitVecExpr)from)), result);
			else
				result = ctx.mkOr(result, ctx2.mkEq((BitVecExpr)mkConst, (BitVecExpr)to));
		}
		return result;
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
		decodeVars.add(var);
		String result = mainDriver.invertFuncs(name, funcs, encodeVars, decodeVars, 
				mainDriver.stringToFuncDef(domain.toString(), "domain", type, new TypeNode(1)).toString(), type, var);
		CmdNode r = mainDriver.toCmdNode(result);
		return r.getTermNode().toString();
	}
	
	static public Sort getSort(TypeNode s, Context context){
		if(s.getType() == 0)
			return context.mkIntSort();
		if(s.getType() == 3)
			return context.mkBitVecSort(s.getLength());
		if(s.getType() == 1)
			return context.mkBoolSort();
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
