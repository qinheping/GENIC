package SMTParser;
import java_cup.runtime.*;
class TokenVal{
	Integer index;
	String str;
	TokenVal(Integer idx, String strin){
	this.index = idx;
	this.str = strin;}
}
class TempString {
	static String str = new String();
}
class IndexNum {
	static Integer num = 0;
}


class Yylex implements java_cup.runtime.Scanner {
	private final int YY_BUFFER_SIZE = 512;
	private final int YY_F = -1;
	private final int YY_NO_STATE = -1;
	private final int YY_NOT_ACCEPT = 0;
	private final int YY_START = 1;
	private final int YY_END = 2;
	private final int YY_NO_ANCHOR = 4;
	private final int YY_BOL = 128;
	private final int YY_EOF = 129;
	private java.io.BufferedReader yy_reader;
	private int yy_buffer_index;
	private int yy_buffer_read;
	private int yy_buffer_start;
	private int yy_buffer_end;
	private char yy_buffer[];
	private boolean yy_at_bol;
	private int yy_lexical_state;

	Yylex (java.io.Reader reader) {
		this ();
		if (null == reader) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(reader);
	}

	Yylex (java.io.InputStream instream) {
		this ();
		if (null == instream) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(new java.io.InputStreamReader(instream));
	}

	private Yylex () {
		yy_buffer = new char[YY_BUFFER_SIZE];
		yy_buffer_read = 0;
		yy_buffer_index = 0;
		yy_buffer_start = 0;
		yy_buffer_end = 0;
		yy_at_bol = true;
		yy_lexical_state = YYINITIAL;
	}

	private boolean yy_eof_done = false;
	private final int YYINITIAL = 0;
	private final int yy_state_dtrans[] = {
		0
	};
	private void yybegin (int state) {
		yy_lexical_state = state;
	}
	private int yy_advance ()
		throws java.io.IOException {
		int next_read;
		int i;
		int j;

		if (yy_buffer_index < yy_buffer_read) {
			return yy_buffer[yy_buffer_index++];
		}

		if (0 != yy_buffer_start) {
			i = yy_buffer_start;
			j = 0;
			while (i < yy_buffer_read) {
				yy_buffer[j] = yy_buffer[i];
				++i;
				++j;
			}
			yy_buffer_end = yy_buffer_end - yy_buffer_start;
			yy_buffer_start = 0;
			yy_buffer_read = j;
			yy_buffer_index = j;
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}

		while (yy_buffer_index >= yy_buffer_read) {
			if (yy_buffer_index >= yy_buffer.length) {
				yy_buffer = yy_double(yy_buffer);
			}
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}
		return yy_buffer[yy_buffer_index++];
	}
	private void yy_move_end () {
		if (yy_buffer_end > yy_buffer_start &&
		    '\n' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
		if (yy_buffer_end > yy_buffer_start &&
		    '\r' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
	}
	private boolean yy_last_was_cr=false;
	private void yy_mark_start () {
		yy_buffer_start = yy_buffer_index;
	}
	private void yy_mark_end () {
		yy_buffer_end = yy_buffer_index;
	}
	private void yy_to_mark () {
		yy_buffer_index = yy_buffer_end;
		yy_at_bol = (yy_buffer_end > yy_buffer_start) &&
		            ('\r' == yy_buffer[yy_buffer_end-1] ||
		             '\n' == yy_buffer[yy_buffer_end-1] ||
		             2028/*LS*/ == yy_buffer[yy_buffer_end-1] ||
		             2029/*PS*/ == yy_buffer[yy_buffer_end-1]);
	}
	private java.lang.String yytext () {
		return (new java.lang.String(yy_buffer,
			yy_buffer_start,
			yy_buffer_end - yy_buffer_start));
	}
	private int yylength () {
		return yy_buffer_end - yy_buffer_start;
	}
	private char[] yy_double (char buf[]) {
		int i;
		char newbuf[];
		newbuf = new char[2*buf.length];
		for (i = 0; i < buf.length; ++i) {
			newbuf[i] = buf[i];
		}
		return newbuf;
	}
	private final int YY_E_INTERNAL = 0;
	private final int YY_E_MATCH = 1;
	private java.lang.String yy_error_string[] = {
		"Error: Internal error.\n",
		"Error: Unmatched input.\n"
	};
	private void yy_error (int code,boolean fatal) {
		java.lang.System.out.print(yy_error_string[code]);
		java.lang.System.out.flush();
		if (fatal) {
			throw new Error("Fatal Error.\n");
		}
	}
	private int[][] unpackFromString(int size1, int size2, String st) {
		int colonIndex = -1;
		String lengthString;
		int sequenceLength = 0;
		int sequenceInteger = 0;

		int commaIndex;
		String workString;

		int res[][] = new int[size1][size2];
		for (int i= 0; i < size1; i++) {
			for (int j= 0; j < size2; j++) {
				if (sequenceLength != 0) {
					res[i][j] = sequenceInteger;
					sequenceLength--;
					continue;
				}
				commaIndex = st.indexOf(',');
				workString = (commaIndex==-1) ? st :
					st.substring(0, commaIndex);
				st = st.substring(commaIndex+1);
				colonIndex = workString.indexOf(':');
				if (colonIndex == -1) {
					res[i][j]=Integer.parseInt(workString);
					continue;
				}
				lengthString =
					workString.substring(colonIndex+1);
				sequenceLength=Integer.parseInt(lengthString);
				workString=workString.substring(0,colonIndex);
				sequenceInteger=Integer.parseInt(workString);
				res[i][j] = sequenceInteger;
				sequenceLength--;
			}
		}
		return res;
	}
	private int yy_acpt[] = {
		/* 0 */ YY_NOT_ACCEPT,
		/* 1 */ YY_NO_ANCHOR,
		/* 2 */ YY_NO_ANCHOR,
		/* 3 */ YY_NO_ANCHOR,
		/* 4 */ YY_NO_ANCHOR,
		/* 5 */ YY_NO_ANCHOR,
		/* 6 */ YY_NO_ANCHOR,
		/* 7 */ YY_NO_ANCHOR,
		/* 8 */ YY_NO_ANCHOR,
		/* 9 */ YY_NO_ANCHOR,
		/* 10 */ YY_NO_ANCHOR,
		/* 11 */ YY_NO_ANCHOR,
		/* 12 */ YY_NO_ANCHOR,
		/* 13 */ YY_NO_ANCHOR,
		/* 14 */ YY_NO_ANCHOR,
		/* 15 */ YY_NO_ANCHOR,
		/* 16 */ YY_NO_ANCHOR,
		/* 17 */ YY_NO_ANCHOR,
		/* 18 */ YY_NO_ANCHOR,
		/* 19 */ YY_NO_ANCHOR,
		/* 20 */ YY_NOT_ACCEPT,
		/* 21 */ YY_NO_ANCHOR,
		/* 22 */ YY_NOT_ACCEPT,
		/* 23 */ YY_NO_ANCHOR,
		/* 24 */ YY_NOT_ACCEPT,
		/* 25 */ YY_NO_ANCHOR,
		/* 26 */ YY_NOT_ACCEPT,
		/* 27 */ YY_NO_ANCHOR,
		/* 28 */ YY_NOT_ACCEPT,
		/* 29 */ YY_NO_ANCHOR,
		/* 30 */ YY_NO_ANCHOR,
		/* 31 */ YY_NO_ANCHOR,
		/* 32 */ YY_NO_ANCHOR,
		/* 33 */ YY_NO_ANCHOR,
		/* 34 */ YY_NO_ANCHOR,
		/* 35 */ YY_NO_ANCHOR,
		/* 36 */ YY_NO_ANCHOR,
		/* 37 */ YY_NO_ANCHOR,
		/* 38 */ YY_NO_ANCHOR,
		/* 39 */ YY_NO_ANCHOR,
		/* 40 */ YY_NO_ANCHOR,
		/* 41 */ YY_NO_ANCHOR,
		/* 42 */ YY_NO_ANCHOR,
		/* 43 */ YY_NO_ANCHOR,
		/* 44 */ YY_NO_ANCHOR,
		/* 45 */ YY_NO_ANCHOR,
		/* 46 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,130,
"2:9,5,3,2:2,4,2:18,5,9,2,24,33:3,2,6,7,33:2,2,22,29,31,26:2,28:8,32,1,33:5," +
"27:6,33:20,2:3,33,8,2,13,25,27,20,15,10,33:2,17,33:2,14,33,21,11,33:2,12,18" +
",19,23,30,33,16,33:2,2:3,33,2,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,47,
"0,1:6,2:2,3,4,1,5,6,7,8,9,2:3,10,8,11,9,5,12,6,13,7,14,15,16,17,18,19,20,21" +
",22,23,24,25,26,27,28,2,29,30")[0];

	private int yy_nxt[][] = unpackFromString(31,34,
"1,20,-1,2,3,4,5,6,7,8,9,44:4,45,44:4,46,44:3,22,30,10,44,10,44:3,24,44,-1:4" +
"2,44:16,-1,44:7,-1,44,-1:8,44:3,37,44:12,-1,44:7,-1,44,-1:8,44:16,-1,44,10," +
"44,10,23,44:2,-1,44,-1:8,12:16,-1,12:6,-1:2,12,-1:10,13,-1:2,13,-1,13,-1:4," +
"13,-1:4,13:4,-1:31,14,-1:15,44:16,-1,44,15,44,15,44:3,-1,44,-1:8,44:16,-1,4" +
"4,16,44,16,44:3,-1,44,-1,20:2,11,20:30,-1:16,26,-1:8,28,-1:16,44:6,17,44:9," +
"-1,44:7,-1,44,-1:8,44:10,18,44:5,-1,44:7,-1,44,-1:8,44:13,19,44:2,-1,44:7,-" +
"1,44,-1:8,44:16,-1,44:5,21,44,-1,44,-1:8,44:6,25,44:9,-1,44:7,-1,44,-1:8,44" +
":11,27,44:4,-1,44:7,-1,44,-1:8,44:15,29,-1,44:7,-1,44,-1:8,44:5,31,44:10,-1" +
",44:7,-1,44,-1:8,44:10,32,44:5,-1,44:7,-1,44,-1:8,44:2,33,44:13,-1,44:7,-1," +
"44,-1:8,44:4,34,44:11,-1,44:7,-1,44,-1:8,44:9,35,44:6,-1,44:7,-1,44,-1:8,44" +
":2,40,44:13,-1,44:7,-1,44,-1:8,44:9,41,44:6,-1,44:7,-1,44,-1:8,44:13,42,44:" +
"2,-1,44:7,-1,44,-1:8,44:7,43,44:8,-1,44:7,-1,44,-1:8,44:14,36,44,-1,44:7,-1" +
",44,-1:8,44:8,38,44:7,-1,44:7,-1,44,-1:8,44:7,39,44:8,-1,44:7,-1,44");

	public java_cup.runtime.Symbol next_token ()
		throws java.io.IOException {
		int yy_lookahead;
		int yy_anchor = YY_NO_ANCHOR;
		int yy_state = yy_state_dtrans[yy_lexical_state];
		int yy_next_state = YY_NO_STATE;
		int yy_last_accept_state = YY_NO_STATE;
		boolean yy_initial = true;
		int yy_this_accept;

		yy_mark_start();
		yy_this_accept = yy_acpt[yy_state];
		if (YY_NOT_ACCEPT != yy_this_accept) {
			yy_last_accept_state = yy_state;
			yy_mark_end();
		}
		while (true) {
			if (yy_initial && yy_at_bol) yy_lookahead = YY_BOL;
			else yy_lookahead = yy_advance();
			yy_next_state = YY_F;
			yy_next_state = yy_nxt[yy_rmap[yy_state]][yy_cmap[yy_lookahead]];
			if (YY_EOF == yy_lookahead && true == yy_initial) {

return new Symbol(sym.EOF);
			}
			if (YY_F != yy_next_state) {
				yy_state = yy_next_state;
				yy_initial = false;
				yy_this_accept = yy_acpt[yy_state];
				if (YY_NOT_ACCEPT != yy_this_accept) {
					yy_last_accept_state = yy_state;
					yy_mark_end();
				}
			}
			else {
				if (YY_NO_STATE == yy_last_accept_state) {
					throw (new Error("Lexical Error: Unmatched Input."));
				}
				else {
					yy_anchor = yy_acpt[yy_last_accept_state];
					if (0 != (YY_END & yy_anchor)) {
						yy_move_end();
					}
					yy_to_mark();
					switch (yy_last_accept_state) {
					case 1:
						
					case -2:
						break;
					case 2:
						{  }
					case -3:
						break;
					case 3:
						{  }
					case -4:
						break;
					case 4:
						{  }
					case -5:
						break;
					case 5:
						{ return new Symbol(sym.LEFTPAREN); }
					case -6:
						break;
					case 6:
						{ return new Symbol(sym.RIGHTPAREN); }
					case -7:
						break;
					case 7:
						{ return new Symbol(sym.TK_UNDERSCORE);}
					case -8:
						break;
					case 8:
						{ return new Symbol(sym.TK_BANG);}
					case -9:
						break;
					case 9:
						{ return new Symbol(sym.SYMBOL, new String(yytext())); }
					case -10:
						break;
					case 10:
						{ return new Symbol(sym.NUMERAL, new String(yytext())); }
					case -11:
						break;
					case 11:
						{  }
					case -12:
						break;
					case 12:
						{ return new Symbol(sym.KEYWORD, new String(yytext())); }
					case -13:
						break;
					case 13:
						{ return new Symbol(sym.HEXCONSTANT, new String(yytext())); }
					case -14:
						break;
					case 14:
						{ return new Symbol(sym.BINCoNSTANT, new String(yytext())); }
					case -15:
						break;
					case 15:
						{ return new Symbol(sym.BVCONSTANT, new String(yytext())); }
					case -16:
						break;
					case 16:
						{ return new Symbol(sym.RATCONSTANT, new String(yytext())); }
					case -17:
						break;
					case 17:
						{ return new Symbol(sym.TK_FORALL); }
					case -18:
						break;
					case 18:
						{ return new Symbol(sym.TK_EXISTS); }
					case -19:
						break;
					case 19:
						{ return new Symbol(sym.TK_DEFINE_FUN); }
					case -20:
						break;
					case 21:
						{ return new Symbol(sym.SYMBOL, new String(yytext())); }
					case -21:
						break;
					case 23:
						{ return new Symbol(sym.SYMBOL, new String(yytext())); }
					case -22:
						break;
					case 25:
						{ return new Symbol(sym.SYMBOL, new String(yytext())); }
					case -23:
						break;
					case 27:
						{ return new Symbol(sym.SYMBOL, new String(yytext())); }
					case -24:
						break;
					case 29:
						{ return new Symbol(sym.SYMBOL, new String(yytext())); }
					case -25:
						break;
					case 30:
						{ return new Symbol(sym.SYMBOL, new String(yytext())); }
					case -26:
						break;
					case 31:
						{ return new Symbol(sym.SYMBOL, new String(yytext())); }
					case -27:
						break;
					case 32:
						{ return new Symbol(sym.SYMBOL, new String(yytext())); }
					case -28:
						break;
					case 33:
						{ return new Symbol(sym.SYMBOL, new String(yytext())); }
					case -29:
						break;
					case 34:
						{ return new Symbol(sym.SYMBOL, new String(yytext())); }
					case -30:
						break;
					case 35:
						{ return new Symbol(sym.SYMBOL, new String(yytext())); }
					case -31:
						break;
					case 36:
						{ return new Symbol(sym.SYMBOL, new String(yytext())); }
					case -32:
						break;
					case 37:
						{ return new Symbol(sym.SYMBOL, new String(yytext())); }
					case -33:
						break;
					case 38:
						{ return new Symbol(sym.SYMBOL, new String(yytext())); }
					case -34:
						break;
					case 39:
						{ return new Symbol(sym.SYMBOL, new String(yytext())); }
					case -35:
						break;
					case 40:
						{ return new Symbol(sym.SYMBOL, new String(yytext())); }
					case -36:
						break;
					case 41:
						{ return new Symbol(sym.SYMBOL, new String(yytext())); }
					case -37:
						break;
					case 42:
						{ return new Symbol(sym.SYMBOL, new String(yytext())); }
					case -38:
						break;
					case 43:
						{ return new Symbol(sym.SYMBOL, new String(yytext())); }
					case -39:
						break;
					case 44:
						{ return new Symbol(sym.SYMBOL, new String(yytext())); }
					case -40:
						break;
					case 45:
						{ return new Symbol(sym.SYMBOL, new String(yytext())); }
					case -41:
						break;
					case 46:
						{ return new Symbol(sym.SYMBOL, new String(yytext())); }
					case -42:
						break;
					default:
						yy_error(YY_E_INTERNAL,false);
					case -1:
					}
					yy_initial = true;
					yy_state = yy_state_dtrans[yy_lexical_state];
					yy_next_state = YY_NO_STATE;
					yy_last_accept_state = YY_NO_STATE;
					yy_mark_start();
					yy_this_accept = yy_acpt[yy_state];
					if (YY_NOT_ACCEPT != yy_this_accept) {
						yy_last_accept_state = yy_state;
						yy_mark_end();
					}
				}
			}
		}
	}
}
