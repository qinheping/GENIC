package injchecker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import theory.Z3Pred.Z3BooleanAlgebra;
import ast.*;
import automata.sfa.SFA;
import automata.sfa.SFAEpsilon;
import automata.sfa.SFAInputMove;
import automata.sfa.SFAMove;
import z3factory.Z3Factory;

import com.microsoft.z3.*;

import Driver.Invertor;
import Driver.mainDriver;

public class SFAConvertor {
	List<ProgNode> progs;
	List<Integer> finalStates;
	String initialState;
	Map<String, Integer> nameList;
	String finalstate;
	Context ctx;
	Expr unit;
	Sort sort;

	List<String> funcdefs_string;
	FuncDecl[] funcDecls;
	Expr[] funcExprs;
	Expr[] funcDefs;
	String[] funcNames;
	List<String> inverted_funcdefs;

	public SFAConvertor(CoderNode root, Context ctx, List<String> funcdefs2, List<String> inverted_funcdefs2,
			String init) throws Exception {
		this.progs = root.getProgList();

		finalStates = new ArrayList<Integer>();
		initialState = init;
		funcdefs_string = funcdefs2;
		inverted_funcdefs = inverted_funcdefs2;
		nameList = new HashMap<String, Integer>();
		finalstate = "FINAL";
		nameList.put(finalstate, getId(finalstate));
		finalStates.add(getId(finalstate));

		sort = Invertor.getSort(progs.get(0).getInType(), ctx);

		Z3Factory factory = new Z3Factory(ctx, sort);
		funcDecls = new FuncDecl[funcdefs_string.size()];
		funcExprs = new Expr[funcdefs_string.size()];
		funcDefs = new Expr[funcdefs_string.size()];
		funcNames = new String[funcdefs_string.size()];
		for (int i = 0; i < funcdefs_string.size(); i++) {
			funcDecls[i] = factory.StringToFuncDecl(funcdefs_string.get(i));
			funcNames[i] = mainDriver.toCmdNode(funcdefs_string.get(i)).getFuncName();
			// System.out.println(funcdefs_string.get(i));
			funcExprs[i] = factory.StringToExpr(mainDriver.toCmdNode(funcdefs_string.get(i)).getTermNode().toString());

			Expr[] boundvars = new Expr[mainDriver.toCmdNode(funcdefs_string.get(i)).getVarList().size()];
			Expr[] vars = new Expr[mainDriver.toCmdNode(funcdefs_string.get(i)).getVarList().size()];
			Sort[] sorts = new Sort[mainDriver.toCmdNode(funcdefs_string.get(i)).getVarList().size()];
			Symbol[] names = new Symbol[mainDriver.toCmdNode(funcdefs_string.get(i)).getVarList().size()];

			for (int j = 0; j < boundvars.length; j++) {
				vars[j] = ctx.mkConst(mainDriver.toCmdNode(funcdefs_string.get(i)).getVarList().get(j), sort);
				boundvars[j] = ctx.mkBound(j, sort);
				sorts[j] = sort;
				names[j] = ctx.mkSymbol("T_" + j);
			}

			funcExprs[i] = funcExprs[i].substitute(vars, boundvars);
			funcDefs[i] = ctx.mkForall(sorts, names, ctx.mkEq(funcExprs[i], ctx.mkApp(funcDecls[i], boundvars)), 1,
					null, null, null, null);
			// System.out.println(funcDefs[i]);

		}

		this.ctx = ctx;
	}

	Integer getId(String s) {
		if (nameList.get(s) != null)
			return nameList.get(s);
		else {
			nameList.put(s, nameList.size() + 1);
			return nameList.size();
		}
	}

	public SFA<BoolExpr, Expr> Convert() throws Exception {
		Collection<SFAMove<BoolExpr, Expr>> transitions = new LinkedList<SFAMove<BoolExpr, Expr>>();
		Z3BooleanAlgebra Z3ba = new Z3BooleanAlgebra(ctx);

		unit = ctx.mkConst("x", sort);

		for (ProgNode node : this.progs) {
			// the id and name of FROM node
			Integer from = getId(node.getName());
			String from_s = node.getName();
			List<TransitionNode> transList = node.getTransList();

			for (int j = 0; j < transList.size(); j++) {
				TransitionNode trans = transList.get(j);
				// the id and name of TO node
				Integer to = getTo(trans);
				String to_s = getTos(trans);

				//////////
				Expr[] decodevars = new Expr[trans.getOutputFuncList().size()];
				for (int i = 0; i < decodevars.length; i++) {
					decodevars[i] = ctx.mkConst("y_" + i, sort);
				}
				//////////

				// System.out.println(TransitionInjChecker.check(ctx, domain,
				// varsName, outputFuncs, sort));

				Integer lookahead = trans.getLookahead();
				Integer out_lookahead = trans.getOutputFuncList().size();

				if (out_lookahead == 0) { // no output
					transitions.add(new SFAEpsilon<BoolExpr, Expr>(from, to));
					continue;
				}

				///////// initial
				Map<String, Sort> sortstable = new HashMap<String, Sort>();
				Map<String, FuncDecl> funcdeclstable = new HashMap<String, FuncDecl>();
				Map<String, Expr> funcdefstable = new HashMap<String, Expr>();
				for (int i = 0; i < funcDecls.length; i++)
					funcdeclstable.put(funcNames[i], funcDecls[i]);
				Z3Factory factory = new Z3Factory(sortstable, funcdeclstable, funcdefstable, ctx);
				factory.setSort(sort);
				/////////

				////////// generate auxiliary arrays
				Expr[] boundvars = new Expr[trans.getVarList().size()];
				Expr[] vars = new Expr[trans.getVarList().size()];
				Sort[] sorts = new Sort[trans.getVarList().size()];
				Symbol[] names = new Symbol[trans.getVarList().size()];
				for (int i = 0; i < boundvars.length; i++) {
					vars[i] = ctx.mkConst(trans.getVarList().get(i), sort);
					boundvars[i] = ctx.mkBound(i, sort);
					sorts[i] = sort;
					names[i] = ctx.mkSymbol("Q_" + i);
				}
				//////////

				///////// generate output pred
				BoolExpr pred = (BoolExpr) factory.StringToExpr(trans.getPred()).substitute(vars, boundvars);
				Expr[] outputs = new Expr[trans.getOutputFuncList().size()];
				BoolExpr[] eqs = new BoolExpr[trans.getOutputFuncList().size()];
				BoolExpr outputpred = pred;
				BoolExpr funcDef = ctx.mkTrue();
				for (int i = 0; i < funcDefs.length; i++)
					funcDef = ctx.mkAnd(funcDef, (BoolExpr) funcDefs[i]);
				for (int i = 0; i < outputs.length; i++) {
					outputs[i] = factory.StringToExpr(trans.getOutputFuncList().get(i).getOutputFunc()).substitute(vars,
							boundvars);
					// System.out.println(outputs[i]);
					eqs[i] = ctx.mkEq(decodevars[i], outputs[i]);
					outputpred = ctx.mkAnd(outputpred, eqs[i]);
				}
				Goal g = ctx.mkGoal(true, false, false);
				for (int i = 0; i < funcDefs.length; i++) {
					g.add((BoolExpr) funcDefs[i]);
					g.add(outputpred);
				}
				ApplyResult ar = ctx.mkTactic("macro-finder").apply(g);
				outputpred = ctx.mkAnd(ar.getSubgoals()[0].getFormulas());
				outputpred = ctx.mkExists(sorts, names, outputpred, 1, null, null, null, null);
				BoolExpr[] outputpreds = toCartesianPreds(outputpred, new ArrayList<Expr>(Arrays.asList(decodevars)),
						false);
				outputpred = ctx.mkAnd(qe(outputpreds, ctx));
				/////////

				BoolExpr[] preds = new BoolExpr[out_lookahead];
				List<String> varsname = trans.getVarList();

				preds = toCartesianPreds(outputpred, new ArrayList<Expr>(Arrays.asList(decodevars)), true);
				// get id and name of way points
				String[] wayPoints_s = new String[out_lookahead - 1];
				Integer[] wayPoints = new Integer[out_lookahead - 1];
				for (int i = 0; i < wayPoints.length; i++) {
					wayPoints_s[i] = from_s + "_" + j + "_" + i + "_" + to_s;
					wayPoints[i] = getId(wayPoints_s[i]);
				}

				if (out_lookahead == 1)
					transitions.add(new SFAInputMove<BoolExpr, Expr>(from, to, preds[0]));
				else {
					transitions.add(new SFAInputMove<BoolExpr, Expr>(from, wayPoints[0], preds[0]));
					for (int i = 0; i < out_lookahead - 2; i++) {
						transitions.add(new SFAInputMove<BoolExpr, Expr>(wayPoints[i], wayPoints[i + 1], preds[i + 1]));
					}
					transitions.add(new SFAInputMove<BoolExpr, Expr>(wayPoints[out_lookahead - 2], to,
							preds[out_lookahead - 1]));
				}
			}
		}
		// System.out.println(SFA.MkSFA(transitions, getId(initialState),
		// Arrays.asList(getId(finalState)), Z3ba));
		return SFA.MkSFA(transitions, getId(initialState), finalStates, Z3ba);
	}

	private String getTos(TransitionNode trans) {
		if (trans.getOutput().getFunc() == null)
			return finalstate;
		return trans.getOutput().getFunc().FuncName();
	}

	private Integer getTo(TransitionNode trans) {
		return getId(getTos(trans));
	}

	private BoolExpr[] toCartesianPreds(BoolExpr pred, List<Expr> varlist, boolean ifsubstitute) throws Exception {
		long start = System.currentTimeMillis();
		Integer size = varlist.size();
		BoolExpr[] preds = new BoolExpr[size];
		Model model = check(ctx, pred, Status.SATISFIABLE);
		for (int i = 0; i < size; i++) {
			preds[i] = pred;
			for (int j = 0; j < size; j++) {
				if (i != j)
					preds[i] = (BoolExpr) preds[i].substitute(varlist.get(j), model.getConstInterp(varlist.get(j)));
			}
			if (ifsubstitute)
				preds[i] = (BoolExpr) preds[i].substitute(varlist.get(i), unit).simplify();
		}
		long end = System.currentTimeMillis();
		// System.out.println("Cartesian: " + (end-start));
		return preds;
	}

	@SuppressWarnings("unused")
	private boolean CartesianCheck(BoolExpr pred, List<Expr> varlist) throws Exception {
		BoolExpr cart = ctx.mkAnd(toCartesianPreds(pred, varlist, false));
		BoolExpr neq = ctx.mkNot(ctx.mkEq(cart, pred));

		return check(ctx, neq, Status.SATISFIABLE) == null;
	}

	public Sort toSort(Integer t) {
		if (t == 0)
			return ctx.mkIntSort();
		return null;
	}

	public BoolExpr[] qe(BoolExpr[] q, Context ctx) {
		BoolExpr[] result = new BoolExpr[q.length];
		for (int i = 0; i < q.length; i++) {
			result[i] = qe(q[i], ctx);
		}
		return result;
	}

	public BoolExpr qe(BoolExpr q, Context ctx) {
		// System.out.println("q:" + q);
		Goal g = ctx.mkGoal(true, true, false);
		g.add(q);
		ApplyResult ar = ctx.mkTactic("qe").apply(g);

		BoolExpr range = ctx.mkTrue();
		BoolExpr[] formulas = ar.getSubgoals()[0].getFormulas();
		// System.out.println(ar);
		for (BoolExpr e : formulas) {
			range = (BoolExpr) ctx.mkAnd(range, e).simplify();
		}
		return range;
	}

	static Model check(Context ctx, BoolExpr f, Status sat) throws Exception {
		Solver s = ctx.mkSolver();
		s.add(f);
		if (s.check() != sat)
			throw new Exception();
		if (sat == Status.SATISFIABLE)
			return s.getModel();
		else
			return null;
	}

	public boolean deterministCheck() throws Exception {
		boolean result = true;
		for (ProgNode node : this.progs) {
			List<TransitionNode> transList = node.getTransList();

			for (int j = 0; j < transList.size() - 1; j++) {
				TransitionNode trans = transList.get(j);
				Integer lookahead = trans.getLookahead();
				BoolExpr pred = getPred(trans);
				Expr[] outputs = getOutputs(trans);

				for (int k = j + 1; k < transList.size(); k++) {
					TransitionNode comparedtrans = transList.get(k);

					BoolExpr pred2 = getPred(comparedtrans);
					Expr[] outputs2 = getOutputs(comparedtrans);
					BoolExpr[] eq = new BoolExpr[outputs.length];
					if (outputs.length == outputs2.length)
						for (int i = 0; i < eq.length; i++)
							eq[i] = ctx.mkEq(outputs[i], outputs2[i]);

					///////////
					if (getTos(trans) == finalstate && getTos(comparedtrans) == finalstate) {

						if (comparedtrans.getLookahead() != lookahead)
							continue;

						if (check(ctx, ctx.mkAnd(pred, pred2), Status.SATISFIABLE) != null) {
							if (check(ctx, ctx.mkAnd(pred, pred2, ctx.mkNot(ctx.mkAnd(eq))),
									Status.SATISFIABLE) != null)
								return false;
						}
						continue;
					}

					if (getTos(trans) == finalstate) {
						if (check(ctx, ctx.mkAnd(pred, pred2), Status.SATISFIABLE) != null)
							if (comparedtrans.getLookahead() < lookahead)
								return false;
						continue;
					}
					if (getTos(comparedtrans) == finalstate) {
						if (check(ctx, ctx.mkAnd(pred, pred2), Status.SATISFIABLE) != null)
							if (comparedtrans.getLookahead() > lookahead)
								return false;
						continue;
					}

					if (check(ctx, ctx.mkAnd(pred, pred2), Status.SATISFIABLE) != null) {
						if (getTo(trans) != getTo(comparedtrans))
							return false;
						if (comparedtrans.getLookahead() != lookahead)
							return false;
						if (check(ctx, ctx.mkAnd(pred, pred2, ctx.mkNot(ctx.mkAnd(eq))), Status.SATISFIABLE) != null)
							return false;
					}
					//////////

				}
			}
		}
		return true;
	}

	private BoolExpr getPred(TransitionNode trans) throws Exception {

		///////// initial
		Map<String, Sort> sortstable = new HashMap<String, Sort>();
		Map<String, FuncDecl> funcdeclstable = new HashMap<String, FuncDecl>();
		Map<String, Expr> funcdefstable = new HashMap<String, Expr>();
		for (int i = 0; i < funcDecls.length; i++)
			funcdeclstable.put(funcNames[i], funcDecls[i]);
		Z3Factory factory = new Z3Factory(sortstable, funcdeclstable, funcdefstable, ctx);
		factory.setSort(sort);
		/////////

		////////// generate auxiliary arrays
		Expr[] boundvars = new Expr[trans.getVarList().size()];
		Expr[] vars = new Expr[trans.getVarList().size()];
		Sort[] sorts = new Sort[trans.getVarList().size()];
		Symbol[] names = new Symbol[trans.getVarList().size()];
		for (int i = 0; i < boundvars.length; i++) {
			vars[i] = ctx.mkConst(trans.getVarList().get(i), sort);
			boundvars[i] = ctx.mkBound(i, sort);
			sorts[i] = sort;
			names[i] = ctx.mkSymbol("Q_" + i);
		}
		//////////

		///////// generate outputs and pred
		BoolExpr pred = (BoolExpr) factory.StringToExpr(trans.getPred()).substitute(vars, boundvars);
		Expr[] outputs = new Expr[trans.getOutputFuncList().size()];
		for (int i = 0; i < outputs.length; i++) {
			outputs[i] = factory.StringToExpr(trans.getOutputFuncList().get(i).getOutputFunc()).substitute(vars,
					boundvars);
		}
		////////
		Goal g = ctx.mkGoal(true, false, false);
		for (int i = 0; i < funcDefs.length; i++) {
			g.add((BoolExpr) funcDefs[i]);
		}
		ApplyResult ar = ctx.mkTactic("macro-finder").apply(g);
		/////////
		List<String> varsname = trans.getVarList();
		return pred;
	}

	private Expr[] getOutputs(TransitionNode trans) throws Exception {

		///////// initial
		Map<String, Sort> sortstable = new HashMap<String, Sort>();
		Map<String, FuncDecl> funcdeclstable = new HashMap<String, FuncDecl>();
		Map<String, Expr> funcdefstable = new HashMap<String, Expr>();
		for (int i = 0; i < funcDecls.length; i++)
			funcdeclstable.put(funcNames[i], funcDecls[i]);
		Z3Factory factory = new Z3Factory(sortstable, funcdeclstable, funcdefstable, ctx);
		factory.setSort(sort);
		/////////

		////////// generate auxiliary arrays
		Expr[] boundvars = new Expr[trans.getVarList().size()];
		Expr[] vars = new Expr[trans.getVarList().size()];
		Sort[] sorts = new Sort[trans.getVarList().size()];
		Symbol[] names = new Symbol[trans.getVarList().size()];
		for (int i = 0; i < boundvars.length; i++) {
			vars[i] = ctx.mkConst(trans.getVarList().get(i), sort);
			boundvars[i] = ctx.mkBound(i, sort);
			sorts[i] = sort;
			names[i] = ctx.mkSymbol("Q_" + i);
		}
		//////////

		///////// generate outputs and pred
		BoolExpr pred = (BoolExpr) factory.StringToExpr(trans.getPred()).substitute(vars, boundvars);
		Expr[] outputs = new Expr[trans.getOutputFuncList().size()];
		for (int i = 0; i < outputs.length; i++) {
			outputs[i] = factory.StringToExpr(trans.getOutputFuncList().get(i).getOutputFunc()).substitute(vars,
					boundvars);
		}
		////////
		Goal g = ctx.mkGoal(true, false, false);
		for (int i = 0; i < funcDefs.length; i++) {
			g.add((BoolExpr) funcDefs[i]);
		}
		ApplyResult ar = ctx.mkTactic("macro-finder").apply(g);
		/////////
		List<String> varsname = trans.getVarList();
		return outputs;
	}

}