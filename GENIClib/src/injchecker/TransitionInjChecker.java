package injchecker;

import com.microsoft.z3.*;

public class TransitionInjChecker {
	
	public static boolean check(Context ctx, BoolExpr domain, String[] varsName, Expr[] outputFuncs, Sort sort) throws Exception{
		Integer lookahead = varsName.length;
		Expr[] vars = new Expr[lookahead];
		Expr[] boundsA = new Expr[lookahead];
		Expr[] boundsB = new Expr[lookahead];
		Symbol[] boundNames = new Symbol[2 * lookahead];
		Sort[] types = new Sort[2 * lookahead];
		for(int i = 0; i < lookahead; i++){
			vars[i] = ctx.mkConst(varsName[i], sort); 
			boundsA[i] = ctx.mkBound(i, sort);
			boundsB[i] = ctx.mkBound(lookahead + i, sort);
			boundNames[i] = ctx.mkSymbol("A_"+i);
			boundNames[i + lookahead] = ctx.mkSymbol("B_"+ i);
			types[i] = sort;
			types[i + lookahead] = sort;
		}
		
		BoolExpr inDomain = ctx.mkAnd((BoolExpr)domain.substitute(vars, boundsA), (BoolExpr)domain.substitute(vars, boundsB));
		
		
		BoolExpr notEQ = ctx.mkNot(ctx.mkEq(boundsA[0], boundsB[0]));
		for(int i = 1; i < lookahead; i++){
			notEQ = ctx.mkOr(notEQ,ctx.mkNot(ctx.mkEq(boundsA[i], boundsB[i])));
		}
		
		BoolExpr outEQ = ctx.mkTrue();
		for(int i = 0; i < outputFuncs.length; i++){
			outEQ = ctx.mkAnd(outEQ, ctx.mkEq(outputFuncs[i].substitute(vars, boundsA), outputFuncs[i].substitute(vars, boundsB)));
		}
		
		BoolExpr query = ctx.mkExists(types, boundNames, ctx.mkAnd(inDomain,notEQ,outEQ), 1, null, null, null, null);
		Solver s = ctx.mkSolver();
		s.add(query);
		if (s.check() == Status.UNSATISFIABLE)
			return true;
		if (s.check() == Status.UNKNOWN)
			throw new Exception("check sat unknown");
		return false;
	}
	
}