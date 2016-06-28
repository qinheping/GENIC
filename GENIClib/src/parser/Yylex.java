package parser;
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
	private final int QUERYSTATE = 5;
	private final int PREDICATESTATE = 3;
	private final int YYINITIAL = 0;
	private final int OUTPUTFUNCTIONSTATE = 4;
	private final int PROGRAMSTATE = 2;
	private final int DECLARESTATE = 1;
	private final int DOMAINSTATE = 6;
	private final int yy_state_dtrans[] = {
		0,
		46,
		49,
		54,
		56,
		58,
		60
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
		/* 20 */ YY_NO_ANCHOR,
		/* 21 */ YY_NO_ANCHOR,
		/* 22 */ YY_NO_ANCHOR,
		/* 23 */ YY_NO_ANCHOR,
		/* 24 */ YY_NO_ANCHOR,
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
		/* 42 */ YY_NOT_ACCEPT,
		/* 43 */ YY_NO_ANCHOR,
		/* 44 */ YY_NO_ANCHOR,
		/* 45 */ YY_NO_ANCHOR,
		/* 46 */ YY_NOT_ACCEPT,
		/* 47 */ YY_NO_ANCHOR,
		/* 48 */ YY_NO_ANCHOR,
		/* 49 */ YY_NOT_ACCEPT,
		/* 50 */ YY_NO_ANCHOR,
		/* 51 */ YY_NO_ANCHOR,
		/* 52 */ YY_NOT_ACCEPT,
		/* 53 */ YY_NO_ANCHOR,
		/* 54 */ YY_NOT_ACCEPT,
		/* 55 */ YY_NO_ANCHOR,
		/* 56 */ YY_NOT_ACCEPT,
		/* 57 */ YY_NO_ANCHOR,
		/* 58 */ YY_NOT_ACCEPT,
		/* 59 */ YY_NO_ANCHOR,
		/* 60 */ YY_NOT_ACCEPT,
		/* 61 */ YY_NO_ANCHOR,
		/* 62 */ YY_NO_ANCHOR,
		/* 63 */ YY_NO_ANCHOR,
		/* 64 */ YY_NO_ANCHOR,
		/* 65 */ YY_NO_ANCHOR,
		/* 66 */ YY_NO_ANCHOR,
		/* 67 */ YY_NO_ANCHOR,
		/* 68 */ YY_NO_ANCHOR,
		/* 69 */ YY_NO_ANCHOR,
		/* 70 */ YY_NO_ANCHOR,
		/* 71 */ YY_NO_ANCHOR,
		/* 72 */ YY_NO_ANCHOR,
		/* 73 */ YY_NO_ANCHOR,
		/* 74 */ YY_NO_ANCHOR,
		/* 75 */ YY_NO_ANCHOR,
		/* 76 */ YY_NO_ANCHOR,
		/* 77 */ YY_NO_ANCHOR,
		/* 78 */ YY_NO_ANCHOR,
		/* 79 */ YY_NO_ANCHOR,
		/* 80 */ YY_NO_ANCHOR,
		/* 81 */ YY_NO_ANCHOR,
		/* 82 */ YY_NO_ANCHOR,
		/* 83 */ YY_NO_ANCHOR,
		/* 84 */ YY_NO_ANCHOR,
		/* 85 */ YY_NO_ANCHOR,
		/* 86 */ YY_NO_ANCHOR,
		/* 87 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,130,
"34:9,2:2,34,2:2,34:18,2,34:7,1,17,34,22,5,19,34:2,31:10,21,6,34,3,20,34:2,3" +
"3,24,28,33:5,23,33:12,26,33:4,32,34,4,34:3,8,33,10,33,27,12,33,11,15,33:2,2" +
"5,7,14,16,33:2,29,33,9,13,35,30,33:3,34,18,34:3,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,88,
"0,1:3,2,1:7,3,1:2,4,5,1:6,6:8,1:7,7:2,1:2,8,1,9,10,11,12,13,14,15,16,17,18," +
"19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,6,42,4" +
"3,44,45,46,47,6,48,49,50")[0];

	private int yy_nxt[][] = unpackFromString(51,36,
"1,2,3,4,43:32,-1:39,42,-1:39,84,76,84:8,-1:6,84:8,77,-1,84,-1,84,-1:3,19,-1" +
":17,20,-1:45,16,-1:11,84:10,-1:6,84:8,77,-1,84,-1,84,-1:7,38:10,-1:6,38:9,-" +
"1,38,-1,38,-1:3,5,-1:39,84:2,23,84:7,-1:6,84:8,77,-1,84,-1,84,-1:7,38:2,39," +
"38:7,-1:6,38:9,-1,38,-1,38,1,6:4,7,8,6:29,-1:20,18,-1:22,84:10,-1:6,84:2,24" +
",84:5,77,-1,84,-1,84,1,9,10,62,43:2,11,12,84:4,86,84:4,13,14,47,43,15,50,63" +
",72,84:3,74,84,75,16,17,84,43,84,-1:22,21,-1:20,84:10,-1:6,84:6,25,84,77,-1" +
",84,-1,84,-1:3,22,-1:39,84:7,26,84:2,-1:6,84:8,77,-1,84,-1,84,1,31:5,32,31:" +
"29,-1:7,84:4,27,84:5,-1:6,84:8,77,-1,84,-1,84,1,33:3,34,33,35,33:29,-1:7,84" +
":4,28,84:5,-1:6,84:8,77,-1,84,-1,84,1,43,36,43:3,37,38:8,87,38,43:6,38:8,43" +
":2,38,43,38,-1:7,84:3,29,84:6,-1:6,84:8,77,-1,84,-1,84,1,40:4,43,41,40:29,-" +
"1:7,84:7,30,84:2,-1:6,84:8,77,-1,84,-1,84,-1:3,52,-1:39,84:7,44,84:2,-1:6,8" +
"4:8,77,-1,84,-1,84,-1:7,38:10,-1:6,38:6,45,38:2,-1,38,-1,38,-1:7,84:9,48,-1" +
":6,84:8,77,-1,84,-1,84,-1:7,84,51,84:8,-1:6,84:8,77,-1,84,-1,84,-1:7,84:10," +
"-1:6,84:4,53,84:3,77,-1,84,-1,84,-1:7,84:2,55,84:7,-1:6,84:8,77,-1,84,-1,84" +
",-1:7,84:3,57,84:6,-1:6,84:8,77,-1,84,-1,84,-1:7,84:10,-1:6,84:4,59,84:3,77" +
",-1,84,-1,84,-1:7,84:9,61,-1:6,84:8,77,-1,84,-1,84,-1:7,84:8,79,65,-1:6,84:" +
"8,77,-1,84,-1,84,-1:7,38:10,-1:6,38:4,64,38:4,-1,38,-1,38,-1:7,84:4,66,84:5" +
",-1:6,84:8,77,-1,84,-1,84,-1:7,84:4,67,84:3,68,84,-1:6,84:8,77,-1,84,-1,84," +
"-1:7,84:2,69,84:7,-1:6,84:8,77,-1,84,-1,84,-1:7,84:7,80,84:2,-1:6,84:8,77,-" +
"1,84,-1,84,-1:7,84:2,81,84:7,-1:6,84:8,77,-1,84,-1,84,-1:7,84:3,82,84:6,-1:" +
"6,84:8,77,-1,84,-1,84,-1:7,84:10,-1:6,84:3,70,84:4,77,-1,84,-1,84,-1:7,84:2" +
",83,84:7,-1:6,84:8,77,-1,84,-1,84,-1:7,84:8,71,84,-1:6,84:8,77,-1,84,-1,84," +
"-1:7,38:10,-1:6,38:9,-1,38,-1,73,-1:7,84:6,78,84:3,-1:6,84:8,77,-1,84,-1,84" +
",-1:7,38:7,85,38:2,-1:6,38:9,-1,38,-1,38");

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
						{ yybegin(DECLARESTATE); TempString.str = "("; }
					case -3:
						break;
					case 3:
						{}
					case -4:
						break;
					case 4:
						{ System.err.println("Illegal character: "+yytext()); }
					case -5:
						break;
					case 5:
						{ yybegin(PROGRAMSTATE);}
					case -6:
						break;
					case 6:
						{ TempString.str += yytext();}
					case -7:
						break;
					case 7:
						{ yybegin(DOMAINSTATE); String result = TempString.str; TempString.str = ""; return new Symbol(sym.DECLARATION, result);}
					case -8:
						break;
					case 8:
						{ yybegin(YYINITIAL); return new Symbol(sym.DECLARATION, TempString.str);}
					case -9:
						break;
					case 9:
						{ return new Symbol(sym.LPAREN);}
					case -10:
						break;
					case 10:
						{}
					case -11:
						break;
					case 11:
						{ return new Symbol(sym.SEMICOLON);}
					case -12:
						break;
					case 12:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -13:
						break;
					case 13:
						{ return new Symbol(sym.RPAREN);}
					case -14:
						break;
					case 14:
						{ return new Symbol(sym.VLINE);}
					case -15:
						break;
					case 15:
						{ return new Symbol(sym.COLON);}
					case -16:
						break;
					case 16:
						{ return new Symbol(sym.INTVALUE, Integer.parseInt(yytext()));}
					case -17:
						break;
					case 17:
						{ yybegin(OUTPUTFUNCTIONSTATE);
				  TempString.str = "";
				  IndexNum.num = 1;}
					case -18:
						break;
					case 18:
						{ return new Symbol(sym.MAPTO);}
					case -19:
						break;
					case 19:
						{ return new Symbol(sym.DEFINE);}
					case -20:
						break;
					case 20:
						{ return new Symbol(sym.CONCAT);}
					case -21:
						break;
					case 21:
						{ return new Symbol(sym.APPEND);}
					case -22:
						break;
					case 22:
						{ yybegin(QUERYSTATE); }
					case -23:
						break;
					case 23:
						{ return new Symbol(sym.INT);}
					case -24:
						break;
					case 24:
						{ return new Symbol(sym.BOOL);}
					case -25:
						break;
					case 25:
						{ return new Symbol(sym.CHAR);}
					case -26:
						break;
					case 26:
						{ yybegin(PREDICATESTATE);
			  	  TempString.str = "";
			  	  return new Symbol(sym.WHEN);}
					case -27:
						break;
					case 27:
						{ return new Symbol(sym.WITH);}
					case -28:
						break;
					case 28:
						{ return new Symbol(sym.MATCH);}
					case -29:
						break;
					case 29:
						{ return new Symbol(sym.BV);}
					case -30:
						break;
					case 30:
						{ return new Symbol(sym.FUNCTION);}
					case -31:
						break;
					case 31:
						{ TempString.str += yytext();}
					case -32:
						break;
					case 32:
						{ yybegin(PROGRAMSTATE);
			 	  return new Symbol(sym.PREDICATE, TempString.str);}
					case -33:
						break;
					case 33:
						{ TempString.str += yytext();}
					case -34:
						break;
					case 34:
						{ yybegin(PROGRAMSTATE);
				  Symbol S = new Symbol(sym.OUTPUTFUNCTION, TempString.str);
				  return S;}
					case -35:
						break;
					case 35:
						{ Symbol S = new Symbol(sym.OUTPUTFUNCTION, TempString.str);
				  TempString.str = "";
				  IndexNum.num ++;
				  return S;}
					case -36:
						break;
					case 36:
						{}
					case -37:
						break;
					case 37:
						{return new Symbol(sym.SEMICOLON);}
					case -38:
						break;
					case 38:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -39:
						break;
					case 39:
						{return new Symbol(sym.INVERT);}
					case -40:
						break;
					case 40:
						{ TempString.str += yytext();}
					case -41:
						break;
					case 41:
						{ yybegin(YYINITIAL); return new Symbol(sym.DOMAIN, TempString.str);}
					case -42:
						break;
					case 43:
						{ System.err.println("Illegal character: "+yytext()); }
					case -43:
						break;
					case 44:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -44:
						break;
					case 45:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -45:
						break;
					case 47:
						{ System.err.println("Illegal character: "+yytext()); }
					case -46:
						break;
					case 48:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -47:
						break;
					case 50:
						{ System.err.println("Illegal character: "+yytext()); }
					case -48:
						break;
					case 51:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -49:
						break;
					case 53:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -50:
						break;
					case 55:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -51:
						break;
					case 57:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -52:
						break;
					case 59:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -53:
						break;
					case 61:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -54:
						break;
					case 62:
						{ System.err.println("Illegal character: "+yytext()); }
					case -55:
						break;
					case 63:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -56:
						break;
					case 64:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -57:
						break;
					case 65:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -58:
						break;
					case 66:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -59:
						break;
					case 67:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -60:
						break;
					case 68:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -61:
						break;
					case 69:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -62:
						break;
					case 70:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -63:
						break;
					case 71:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -64:
						break;
					case 72:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -65:
						break;
					case 73:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -66:
						break;
					case 74:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -67:
						break;
					case 75:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -68:
						break;
					case 76:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -69:
						break;
					case 77:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -70:
						break;
					case 78:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -71:
						break;
					case 79:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -72:
						break;
					case 80:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -73:
						break;
					case 81:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -74:
						break;
					case 82:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -75:
						break;
					case 83:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -76:
						break;
					case 84:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -77:
						break;
					case 85:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -78:
						break;
					case 86:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -79:
						break;
					case 87:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -80:
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
