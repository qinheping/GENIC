package SMTparser;
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
		/* 18 */ YY_NOT_ACCEPT,
		/* 19 */ YY_NO_ANCHOR,
		/* 20 */ YY_NOT_ACCEPT,
		/* 21 */ YY_NO_ANCHOR,
		/* 22 */ YY_NOT_ACCEPT,
		/* 23 */ YY_NO_ANCHOR,
		/* 24 */ YY_NOT_ACCEPT,
		/* 25 */ YY_NO_ANCHOR,
		/* 26 */ YY_NO_ANCHOR,
		/* 27 */ YY_NO_ANCHOR,
		/* 28 */ YY_NO_ANCHOR,
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
		/* 43 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,130,
"2:9,5,3,2:2,4,2:18,5,30,2,23,30:3,2,6,7,30:2,2,21,28,30,25:2,27:8,2,1,30:5," +
"26:6,30:20,2:3,30,8,2,12,24,26,19,14,9,30:2,16,30:2,13,30,20,10,30:2,11,17," +
"18,22,29,30,15,30:2,2:3,30,2,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,44,
"0,1:6,2,3,4,1,5,6,7,8,2:3,9,7,10,8,5,11,6,12,13,14,15,16,17,18,19,20,21,22," +
"23,24,25,26,27,2,28,29")[0];

	private int yy_nxt[][] = unpackFromString(30,31,
"1,18,-1,2,3,4,5,6,7,8,41:4,42,41:4,43,41:3,20,27,9,41,9,41:3,-1:39,41:15,-1" +
",41:7,-1:8,41:2,34,41:12,-1,41:7,-1:8,41:15,-1,41,9,41,9,21,41:2,-1:9,11,-1" +
":2,11,-1,11,-1:4,11,-1:4,11:4,-1:28,12,-1:13,41:15,-1,41,13,41,13,41:3,-1:8" +
",41:15,-1,41,14,41,14,41:3,-1,18:2,10,18:27,-1:15,22,-1:8,24,-1:14,41:5,15," +
"41:9,-1,41:7,-1:8,41:9,16,41:5,-1,41:7,-1:8,41:12,17,41:2,-1,41:7,-1:8,41:1" +
"5,-1,41:5,19,41,-1:8,41:5,23,41:9,-1,41:7,-1:8,41:10,25,41:4,-1,41:7,-1:8,4" +
"1:14,26,-1,41:7,-1:8,41:4,28,41:10,-1,41:7,-1:8,41:9,29,41:5,-1,41:7,-1:8,4" +
"1,30,41:13,-1,41:7,-1:8,41:3,31,41:11,-1,41:7,-1:8,41:8,32,41:6,-1,41:7,-1:" +
"8,41,37,41:13,-1,41:7,-1:8,41:8,38,41:6,-1,41:7,-1:8,41:12,39,41:2,-1,41:7," +
"-1:8,41:6,40,41:8,-1,41:7,-1:8,41:13,33,41,-1,41:7,-1:8,41:7,35,41:7,-1,41:" +
"7,-1:8,41:6,36,41:8,-1,41:7");

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
						{ return new Symbol(sym.SYMBOL, new String(yytext())); }
					case -9:
						break;
					case 9:
						{ return new Symbol(sym.NUMERAL, new String(yytext())); }
					case -10:
						break;
					case 10:
						{  }
					case -11:
						break;
					case 11:
						{ return new Symbol(sym.HEXCONSTANT, new String(yytext())); }
					case -12:
						break;
					case 12:
						{ return new Symbol(sym.BINCONSTANT, new String(yytext())); }
					case -13:
						break;
					case 13:
						{ return new Symbol(sym.BVCONSTANT, new String(yytext())); }
					case -14:
						break;
					case 14:
						{ return new Symbol(sym.RATCONSTANT, new String(yytext())); }
					case -15:
						break;
					case 15:
						{ return new Symbol(sym.TK_FORALL); }
					case -16:
						break;
					case 16:
						{ return new Symbol(sym.TK_EXISTS); }
					case -17:
						break;
					case 17:
						{ return new Symbol(sym.TK_DEFINE_FUN); }
					case -18:
						break;
					case 19:
						{ return new Symbol(sym.SYMBOL, new String(yytext())); }
					case -19:
						break;
					case 21:
						{ return new Symbol(sym.SYMBOL, new String(yytext())); }
					case -20:
						break;
					case 23:
						{ return new Symbol(sym.SYMBOL, new String(yytext())); }
					case -21:
						break;
					case 25:
						{ return new Symbol(sym.SYMBOL, new String(yytext())); }
					case -22:
						break;
					case 26:
						{ return new Symbol(sym.SYMBOL, new String(yytext())); }
					case -23:
						break;
					case 27:
						{ return new Symbol(sym.SYMBOL, new String(yytext())); }
					case -24:
						break;
					case 28:
						{ return new Symbol(sym.SYMBOL, new String(yytext())); }
					case -25:
						break;
					case 29:
						{ return new Symbol(sym.SYMBOL, new String(yytext())); }
					case -26:
						break;
					case 30:
						{ return new Symbol(sym.SYMBOL, new String(yytext())); }
					case -27:
						break;
					case 31:
						{ return new Symbol(sym.SYMBOL, new String(yytext())); }
					case -28:
						break;
					case 32:
						{ return new Symbol(sym.SYMBOL, new String(yytext())); }
					case -29:
						break;
					case 33:
						{ return new Symbol(sym.SYMBOL, new String(yytext())); }
					case -30:
						break;
					case 34:
						{ return new Symbol(sym.SYMBOL, new String(yytext())); }
					case -31:
						break;
					case 35:
						{ return new Symbol(sym.SYMBOL, new String(yytext())); }
					case -32:
						break;
					case 36:
						{ return new Symbol(sym.SYMBOL, new String(yytext())); }
					case -33:
						break;
					case 37:
						{ return new Symbol(sym.SYMBOL, new String(yytext())); }
					case -34:
						break;
					case 38:
						{ return new Symbol(sym.SYMBOL, new String(yytext())); }
					case -35:
						break;
					case 39:
						{ return new Symbol(sym.SYMBOL, new String(yytext())); }
					case -36:
						break;
					case 40:
						{ return new Symbol(sym.SYMBOL, new String(yytext())); }
					case -37:
						break;
					case 41:
						{ return new Symbol(sym.SYMBOL, new String(yytext())); }
					case -38:
						break;
					case 42:
						{ return new Symbol(sym.SYMBOL, new String(yytext())); }
					case -39:
						break;
					case 43:
						{ return new Symbol(sym.SYMBOL, new String(yytext())); }
					case -40:
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
