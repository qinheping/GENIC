package test.z3factory;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import Driver.SMTDriver;
import smtast.*;
import java_cup.runtime.Symbol;

import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Sort;

import z3factory.*;
public class Z3FactoryUnitTest {

	@Test
	public void test() {
		Context ctx = new Context();
		String teststring = "(forall ((x Int)) (>= (f x x) (+ x a)))";
		Map<String, Sort> sortstable = new HashMap<String,Sort>();
		sortstable.put("a", ctx.mkIntSort());
		sortstable.put("b", ctx.mkIntSort());
		Map<String, FuncDecl> funcstable = new HashMap<String,FuncDecl>();
		Sort[] sortsarg = new Sort[2];
		sortsarg[0] = ctx.mkIntSort();
		sortsarg[1] = ctx.mkIntSort();
		funcstable.put("f", ctx.mkFuncDecl("f", sortsarg, ctx.mkIntSort()));
		TermNode n = null;
		try {
			n = (TermNode) ((CmdNode)SMTDriver.GetSMTRootFromString(teststring).value).getChild();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Z3Factory fa = new Z3Factory(sortstable,funcstable,ctx);
		Expr result = fa.TermNodeToExpr(n);
		System.out.println(result);
		fail("Not yet implemented");
	}
	
}
