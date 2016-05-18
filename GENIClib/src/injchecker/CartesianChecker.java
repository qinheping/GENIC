package injchecker;

import com.microsoft.z3.*;

public class CartesianChecker {
	public static boolean checkFormula (Context ctx, BoolExpr formula, String[] varsName) throws Exception{
		Solver s = ctx.mkSolver();
		s.add(formula);
		if (s.check() == Status.UNSATISFIABLE)
			return true;
		if (s.check() == Status.UNKNOWN)
			throw new Exception("check sat unknown");
		Model model = s.getModel();
		FuncDecl[] varsDecl = model.getConstDecls();
		Integer size = varsDecl.length;
		Expr[] vars = new Expr[size];
		BoolExpr[] partialFormulas = new BoolExpr[size];
		for(int i = 0; i < size; i++){
			vars[i] = ctx.mkConst(varsDecl[i].getName(), varsDecl[i].getRange());
		}

		for(int i = 0; i < size; i++){
			partialFormulas[i] = formula;
			for(int j = 0; j < size; j++){
				if(i == j)
					continue;
				partialFormulas[i] = (BoolExpr) partialFormulas[i].substitute(vars[j], model.getConstInterp(varsDecl[j]));
			}
		}
		BoolExpr query = ctx.mkNot(ctx.mkEq(formula, ctx.mkAnd(partialFormulas)));
		s.reset();
		s.add(query);
		if (s.check() == Status.UNSATISFIABLE)
			return true;
		if (s.check() == Status.UNKNOWN)
			throw new Exception("check sat unknown");
		return false;
	}
}
