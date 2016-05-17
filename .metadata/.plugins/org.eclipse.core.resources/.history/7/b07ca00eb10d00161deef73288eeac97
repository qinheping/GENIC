package jpdr;

import static jpdr.expr.Expr.and;
import static jpdr.expr.Expr.equal;
import static jpdr.expr.Expr.not;
import static jpdr.expr.Expr.or;
import static jpdr.expr.Expr.xor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import jpdr.aiger.JaigerToExpr;
import jpdr.eval.Interpretation;
import jpdr.expr.Expr;
import jpdr.expr.Var;
import jpdr.pdr.PDR;

public class Main {
	public static void main(String[] args) throws Exception {
		// homemade2();
		aiger();
	}

	private static void homemade1() {
		Var a = new Var("a");
		Var b = new Var("b");

		Expr I = not(or(a, b));

		Expr Ta = equal(a.prime(), not(a));
		Expr Tb = equal(b.prime(), xor(b, a));
		Expr T = and(Ta, Tb);

		Expr P = not(and(a, b));
		check(I, T, P);
	}

	private static void homemade2() {
		Var a = new Var("a");
		Var b = new Var("b");
		Var c = new Var("c");
		Var d = new Var("d");
		Var e = new Var("e");

		Var a2 = new Var("a2");
		Var b2 = new Var("b2");
		Var c2 = new Var("c2");
		Var d2 = new Var("d2");
		Var e2 = new Var("e2");

		Expr I = not(or(a, b, c, d, e, a2, b2, c2, d2, e2));

		Expr Ta = equal(a.prime(), not(a));
		Expr Tb = equal(b.prime(), xor(b, a));
		Expr Tc = equal(c.prime(), xor(c, and(a, b)));
		Expr Td = equal(d.prime(), xor(d, and(a, b, c)));
		Expr Te = equal(e.prime(), xor(e, and(a, b, c, d)));

		Expr Ta2 = equal(a2.prime(), not(a2));
		Expr Tb2 = equal(b2.prime(), xor(b2, a2));
		Expr Tc2 = equal(c2.prime(), xor(c2, and(a2, b2)));
		Expr Td2 = equal(d2.prime(), xor(d2, and(a2, b2, c2)));
		Expr Te2 = equal(e2.prime(), xor(e2, and(a2, b2, c2, d2)));

		Expr T = and(Ta, Tb, Tc, Td, Te, Ta2, Tb2, Tc2, Td2, Te2);

		Expr P = and(equal(a, a2), equal(b, b2), equal(c, c2), equal(d, d2), equal(e, e2));
		check(I, T, P);

		Expr P2 = not(and(a, b, c, d, e));
		check(I, T, P2);
	}

	private static void aiger() throws IOException, FileNotFoundException {
		JaigerToExpr reader;
		try (InputStream in = new FileInputStream("c:/desktop/test.aig")) {
			reader = new JaigerToExpr(in);
			reader.parse();
		}
		
		Expr I = reader.getInitial();
		Expr T = reader.getTransition();
		Expr P = reader.getProperty();
		check(I, T, P);
	}

	private static void check(Expr I, Expr T, Expr P) {
		long start = System.currentTimeMillis();
		// BMC mc = new BMC(I, T, P, 30);
		PDR mc = new PDR(I, T, P);
		for (Interpretation interp : mc.check()) {
			System.out.println(interp);
		}
		long stop = System.currentTimeMillis();
		System.out.println("Time: " + (stop - start) / 1000.0);
		mc.showFrames();
		System.out.println();
	}
}
