package Driver;

import java.io.ByteArrayInputStream;

import java_cup.runtime.Symbol;
import SMTParser.parser;

public class SMTDriver {
	static Symbol GetSMTRootFromString(String str) throws Exception{
		ByteArrayInputStream stream = new ByteArrayInputStream(str.getBytes());
		Symbol root = new parser().ParsingStreamToAST(stream);
		return root;
	}
}
