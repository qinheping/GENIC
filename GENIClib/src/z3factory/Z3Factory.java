package z3factory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Driver.SMTDriver;
import ast.TypeNode;

import com.microsoft.z3.*;

import smtast.*;


public class Z3Factory {
	Map<String, Sort> sortstable;
	Map<String, FuncDecl> funcdeclstable;
	Map<String, Expr>	funcdefstable;
	Integer bvsize; // all bit vectors have same size
	Context ctx;
	// convert a termnode to its corresponding Expr in given context
	
	public Z3Factory(Context ctx){
		sortstable = new HashMap<String, Sort>();
		funcdeclstable = new HashMap<String, FuncDecl>();
		funcdefstable = new HashMap<String, Expr>();
		this.ctx = ctx;
	}
	public Z3Factory(Map<String, Sort> st, Map<String, FuncDecl> ft, Map<String, Expr> fd, Context ctx){
		sortstable = st;
		funcdeclstable = ft;
		funcdefstable = fd;
		this.ctx = ctx;
	}
	public Z3Factory(Map<String, Sort> st, Map<String, FuncDecl> ft, Context ctx){
		sortstable = st;
		funcdeclstable = ft;
		funcdefstable = new HashMap<String, Expr>();
		this.ctx = ctx;
	}
	
	public void addFunc(DefCmdNode n){
		Sort funcsort = getSortFromSortNode(n.getSort());
		String funcname = n.getFuncName();
		int dsize = n.getVarList().size();
		Sort[] domain = getSortsFromQVListNode(n.getQVListNode());
		funcdeclstable.put(funcname, ctx.mkFuncDecl(funcname, domain, funcsort));
		funcdefstable.put(funcname, TermNodeToExpr(n.getTermNode()));
	}
	
	public Expr StringToExpr(String s) throws Exception{
		TermNode n = (TermNode)((CmdNode)SMTDriver.GetSMTRootFromString(s).value).getChild();
		return TermNodeToExpr(n);
	}
	public Expr TermNodeToExpr(TermNode n){
		Expr result = null;
		Sort[] types;
		String[] names;
		switch(n.getChildtype()){
		case 0: 		//forall
				types = getSortsFromQVListNode(n.getQVList());
				names = n.getQVList().getVarList().toArray(new String[n.getQVList().getVarList().size()]);
				for(int i = 0; i < n.getQVList().getVarList().size(); i++){
					sortstable.put(names[i], types[i]);
				}
				result = ctx.mkForall(types, /* types of quantified variables */
						StringsToSymbols(names), /* names of quantified variables */
		                TermNodeToExpr((TermNode)n.getChild()), 1, null/* patterns */, null, null, null);
				break;
		case 1: 		//exists
				types = getSortsFromQVListNode(n.getQVList());
				names = n.getQVList().getVarList().toArray(new String[n.getQVList().getVarList().size()]);
				for(int i = 0; i < n.getQVList().getVarList().size(); i++){
					sortstable.put(names[i], types[i]);
				}
				result = ctx.mkForall(types, /* types of quantified variables */
					StringsToSymbols(names), /* names of quantified variables */
	                TermNodeToExpr((TermNode)n.getChild()), 1, null/* patterns */, null, null, null);
				break;
		case 2: 		//symbol
				if(sortstable.containsKey(n.getSymbol()))
					result = ctx.mkConst(n.getSymbol(), sortstable.get(n.getSymbol()));
				else
					result = ctx.mkConst(n.getSymbol(), ctx.mkIntSort());
				break;
		case 3: 		//const
				NumConstNode num = (NumConstNode) n.getChild();
				if(num.getMytype() == NumConstNode.BV) {
					result = ctx.mkBV(Integer.parseInt(num.getContent().substring(2, num.getContent().length()), 16), bvsize);
				}
				if(num.getMytype() == NumConstNode.NUM){
					result = ctx.mkInt(Integer.parseInt(num.getContent()));
				}
				break;
		case 4: 		//list
				TermListNode tlistnode = (TermListNode) n.getChild();
				List<TermNode> tlist = tlistnode.getList();
				Expr[] vars = new Expr[tlist.size()];
				for(int i = 0; i < tlist.size(); i++){
					vars[i] = TermNodeToExpr(tlist.get(i));
					}
				if(funcdeclstable != null){
				if(funcdeclstable.containsKey(n.getSymbol())){
					result = ctx.mkApp(funcdeclstable.get(n.getSymbol()), vars);}else{
						result = mkApp_buildin(n.getSymbol(),vars);
					}
				}
				else{
					result = mkApp_buildin(n.getSymbol(),vars);
				}
				break;
		}

		return result;
	}
	
	// extract sort of vars from QVListNode, QuantVarNode and SortNode. Now we have sorts: BitVect, Int.
	Sort[] getSortsFromQVListNode(QVListNode n){
		int sz = n.getVarList().size();
		Sort[] result = new Sort[sz]; // size of result should be equal to the size of varlist
		for(int i = 0; i < sz; i++){
			result[i] = getSortFromQunatVarNode(n.getQVList().get(i));
		}
		return result;	
	}
	
	// extract sort of vars from QVListNode, QuantVarNode and SortNode. Now we have sorts: BitVect, Int.
	Sort getSortFromQunatVarNode(QuantVarNode n){
		return getSortFromSortNode(n.getSort());
	}
	
	// extract sort of vars from QVListNode, QuantVarNode and SortNode. Now we have sorts: BitVect, Int.
	Sort getSortFromSortNode(SortNode n){
		if(n.getSort().equals("BitVect")){
			return ctx.mkBitVecSort(n.getParameter());
		}
		if(n.getSort().equals("Int")){
			return ctx.mkIntSort();
		}
		try {
			throw new Exception("Sort Not Found: "+n.getSort());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	// make symbol array from string array
	Symbol[] StringsToSymbols(String[] sarray){
		Symbol[] result = new Symbol[sarray.length];
		for(int i = 0; i < sarray.length; i++){
			result[i] = ctx.mkSymbol(sarray[i]);
		}
		return result;
	}
	
	// find and apply z3 build in function such as + - * / bvadd bvsub
	public Expr mkApp_buildin(String funcname, Expr[] vars){
		if(funcname.equals("+")){
			int size = vars.length;
			ArithExpr result = (ArithExpr) vars[0];
			for(int i = 1; i < size; i++)
				result = ctx.mkAdd(result,(ArithExpr) vars[i]);

			return result;
			}
		if(funcname.equals("-")){
			int size = vars.length;
			ArithExpr result = (ArithExpr) vars[0];
			for(int i = 1; i < size; i++)
				result = ctx.mkSub(result,(ArithExpr) vars[i]);
			return result;
			}
		if(funcname.equals("*")){
			int size = vars.length;
			ArithExpr result = (ArithExpr) vars[0];
			for(int i = 1; i < size; i++)
				result = ctx.mkMul(result,(ArithExpr) vars[i]);
			return result;
			}
		if(funcname.equals("/"))
			return ctx.mkDiv((ArithExpr)vars[0], (ArithExpr)vars[1]);
		if(funcname.equals("<"))
			return ctx.mkLt((ArithExpr)vars[0], (ArithExpr)vars[1]);
		if(funcname.equals(">"))
			return ctx.mkGt((ArithExpr)vars[0], (ArithExpr)vars[1]);
		if(funcname.equals("<="))
			return ctx.mkLe((ArithExpr)vars[0], (ArithExpr)vars[1]);
		if(funcname.equals(">="))
			return ctx.mkGe((ArithExpr)vars[0], (ArithExpr)vars[1]);
		if(funcname.equals("="))
			return ctx.mkEq(vars[0], vars[1]);
		if(funcname.equals("bvadd"))
			return ctx.mkBVAdd((BitVecExpr)vars[0], (BitVecExpr)vars[1]);
		if(funcname.equals("bvsub"))
			return ctx.mkBVSub((BitVecExpr)vars[0], (BitVecExpr)vars[1]);
		if(funcname.equals("bvmul"))
			return ctx.mkBVMul((BitVecExpr)vars[0], (BitVecExpr)vars[1]);
		if(funcname.equals("bvxor"))
			return ctx.mkBVXOR((BitVecExpr)vars[0], (BitVecExpr)vars[1]);
		if(funcname.equals("bvand"))
			return ctx.mkBVAND((BitVecExpr)vars[0], (BitVecExpr)vars[1]);
		if(funcname.equals("bvor"))
			return ctx.mkBVOR((BitVecExpr)vars[0], (BitVecExpr)vars[1]);
		if(funcname.equals("bvneg"))
			return ctx.mkBVNeg((BitVecExpr)vars[0]);
		if(funcname.equals("bvudiv"))
			return ctx.mkBVUDiv((BitVecExpr)vars[0], (BitVecExpr)vars[1]);
		if(funcname.equals("bvurem"))
			return ctx.mkBVURem((BitVecExpr)vars[0], (BitVecExpr)vars[1]);
		if(funcname.equals("bvsdiv"))
			return ctx.mkBVSDiv((BitVecExpr)vars[0], (BitVecExpr)vars[1]);
		if(funcname.equals("bvsrem"))
			return ctx.mkBVSRem((BitVecExpr)vars[0], (BitVecExpr)vars[1]);
		if(funcname.equals("bvlshr"))
			return ctx.mkBVLSHR((BitVecExpr)vars[0], (BitVecExpr)vars[1]);
		if(funcname.equals("bvashr"))
			return ctx.mkBVASHR((BitVecExpr)vars[0], (BitVecExpr)vars[1]);
		if(funcname.equals("bvshl"))
			return ctx.mkBVSHL((BitVecExpr)vars[0], (BitVecExpr)vars[1]);
		if(funcname.equals("bvlnot"))
			return ctx.mkBVNot((BitVecExpr)vars[0]);
		if(funcname.equals("and")){
			int size = vars.length;
			BoolExpr result = (BoolExpr) vars[0];
			for(int i = 1; i < size; i++)
				result = ctx.mkAnd(result,(BoolExpr) vars[i]);
			return result;
			}
		if(funcname.equals("not"))
			return ctx.mkNot((BoolExpr) vars[0]);
		if(funcname.equals("or")){
			int size = vars.length;
			BoolExpr result = (BoolExpr) vars[0];
			for(int i = 1; i < size; i++)
				result = ctx.mkOr(result,(BoolExpr) vars[i]);
			return result;
			}
		if(funcname.equals("xor"))
			return ctx.mkXor((BoolExpr) vars[0],(BoolExpr) vars[1]);
		if(funcname.equals("bvule"))
			return ctx.mkBVULE((BitVecExpr)vars[0], (BitVecExpr)vars[1]);
		if(funcname.equals("bvuge"))
			return ctx.mkBVUGE((BitVecExpr)vars[0], (BitVecExpr)vars[1]);
		if(funcname.equals("bvult"))
			return ctx.mkBVULT((BitVecExpr)vars[0], (BitVecExpr)vars[1]);
		if(funcname.equals("bvugt"))
			return ctx.mkBVUGT((BitVecExpr)vars[0], (BitVecExpr)vars[1]);
		if(funcname.equals("bvsle"))
			return ctx.mkBVSLE((BitVecExpr)vars[0], (BitVecExpr)vars[1]);
		if(funcname.equals("bvsge"))
			return ctx.mkBVSGE((BitVecExpr)vars[0], (BitVecExpr)vars[1]);
		if(funcname.equals("bvslt"))
			return ctx.mkBVSLT((BitVecExpr)vars[0], (BitVecExpr)vars[1]);
		if(funcname.equals("bvsgt"))
			return ctx.mkBVSGT((BitVecExpr)vars[0], (BitVecExpr)vars[1]);
		return null;
	}
}