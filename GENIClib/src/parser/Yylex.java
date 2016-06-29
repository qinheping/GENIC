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
	private final int PATTERNSTATE = 7;
	private final int DOMAINSTATE = 6;
	private final int yy_state_dtrans[] = {
		0,
		50,
		53,
		59,
		62,
		64,
		66,
		67
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
		/* 42 */ YY_NO_ANCHOR,
		/* 43 */ YY_NO_ANCHOR,
		/* 44 */ YY_NO_ANCHOR,
		/* 45 */ YY_NOT_ACCEPT,
		/* 46 */ YY_NO_ANCHOR,
		/* 47 */ YY_NO_ANCHOR,
		/* 48 */ YY_NO_ANCHOR,
		/* 49 */ YY_NO_ANCHOR,
		/* 50 */ YY_NOT_ACCEPT,
		/* 51 */ YY_NO_ANCHOR,
		/* 52 */ YY_NO_ANCHOR,
		/* 53 */ YY_NOT_ACCEPT,
		/* 54 */ YY_NO_ANCHOR,
		/* 55 */ YY_NO_ANCHOR,
		/* 56 */ YY_NOT_ACCEPT,
		/* 57 */ YY_NO_ANCHOR,
		/* 58 */ YY_NO_ANCHOR,
		/* 59 */ YY_NOT_ACCEPT,
		/* 60 */ YY_NO_ANCHOR,
		/* 61 */ YY_NO_ANCHOR,
		/* 62 */ YY_NOT_ACCEPT,
		/* 63 */ YY_NO_ANCHOR,
		/* 64 */ YY_NOT_ACCEPT,
		/* 65 */ YY_NO_ANCHOR,
		/* 66 */ YY_NOT_ACCEPT,
		/* 67 */ YY_NOT_ACCEPT,
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
		/* 87 */ YY_NO_ANCHOR,
		/* 88 */ YY_NO_ANCHOR,
		/* 89 */ YY_NO_ANCHOR,
		/* 90 */ YY_NO_ANCHOR,
		/* 91 */ YY_NO_ANCHOR,
		/* 92 */ YY_NO_ANCHOR,
		/* 93 */ YY_NO_ANCHOR,
		/* 94 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,130,
"4:9,2:2,4,2:2,4:18,2,4:7,1,24,4,28,5,26,4:2,12:10,13,6,4,3,27,4:2,11,30,33," +
"11:5,29,11:12,32,11:4,14,4,15,4:3,17,11,19,11,9,20,11,8,22,11:2,31,16,10,23" +
",11:2,34,11,18,21,35,7,11:3,4,25,4:3,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,95,
"0,1:3,2,1:7,3,4,5,1:7,6:7,1:7,7:2,1:3,8,1:2,9,10,1,11,12,13,14,15,16,17,18," +
"19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,9,37,38,39,40,41,42,4" +
"3,44,45,46,47,6,48,9,49,50,51,52,53,54,55")[0];

	private int yy_nxt[][] = unpackFromString(56,36,
"1,2,3,4,46:32,-1:39,45,-1:39,85:6,-1:3,85:6,72,85,-1:5,85:7,-1:12,13,-1:26," +
"18,-1:39,85:6,-1:3,85:8,-1:5,85:7,-1:7,36:6,-1:3,36:8,-1:5,36:7,-1:7,87,71," +
"87:3,73,-1:3,87:8,-1:5,87:7,-1:7,87:5,73,-1:3,87:8,-1:5,87:7,-1:3,5,-1:39,8" +
"5:6,-1:3,85:2,22,85:5,-1:5,85:7,-1:7,36:6,-1:3,36:2,37,36:5,-1:5,36:7,-1:7," +
"87:3,44,87,73,-1:3,87:8,-1:5,87:7,1,6:4,7,8,6:29,-1:27,19,-1:15,85,23,85:4," +
"-1:3,85:8,-1:5,85:7,1,9,10,68,46:2,11,12,85:4,13,14,15,46,89,85:3,90,85:3,1" +
"6,17,51,46,54,69,91,85:2,92,85:2,-1:28,20,-1:14,85:6,-1:3,85:8,-1:5,85:2,24" +
",85:4,-1:3,21,-1:45,42,-1:29,85:6,-1:3,85:8,-1:5,85:5,25,85,1,29:5,30,29:29" +
",-1:15,43,-1:27,85,26,85:4,-1:3,85:8,-1:5,85:7,1,31:5,32,31:8,33,31:20,-1:7" +
",85:6,-1:3,85:3,27,85:4,-1:5,85:7,1,46,34,46:3,35,36:5,46:4,36:6,94,36,46:5" +
",36:7,-1:7,85:3,28,85:2,-1:3,85:8,-1:5,85:7,1,38:4,46,39,38:29,1,46,40,46:4" +
",41,87:4,46,57,60,46,87:8,46:5,87:7,-1:3,56,-1:39,85:3,47,85:2,-1:3,85:8,-1" +
":5,85:7,-1:7,36:6,-1:3,36:8,-1:5,36:5,48,36,-1:7,87:2,49,87:2,73,-1:3,87:8," +
"-1:5,87:7,-1:7,85:6,-1:3,85:2,52,85:5,-1:5,85:7,-1:7,85:6,-1:3,85:2,78,85:5" +
",-1:5,85:7,-1:7,85:3,79,85:2,-1:3,85:8,-1:5,85:7,-1:7,85:6,-1:3,85:7,55,-1:" +
"5,85:7,-1:7,85:6,-1:3,85,58,85:6,-1:5,85:7,-1:7,85:6,-1:3,85:3,61,85:4,-1:5" +
",85:7,-1:7,85:6,-1:3,85:3,81,85:4,-1:5,85:7,-1:7,85:6,-1:3,85:8,-1:5,85:3,8" +
"2,85:3,-1:7,85:6,-1:3,85:2,83,85:5,-1:5,85:7,-1:7,85:2,63,85:3,-1:3,85:8,-1" +
":5,85:7,-1:7,85:6,-1:3,85:6,84,85,-1:5,85:7,-1:7,85:6,-1:3,85:7,65,-1:5,85:" +
"7,-1:7,36:2,70,36:3,-1:3,36:8,-1:5,36:7,-1:7,85:6,-1:3,85:2,80,85:5,-1:5,85" +
":7,-1:7,85:6,-1:3,85,74,85:6,-1:5,85:7,-1:7,85:6,-1:3,85:5,75,85:2,-1:5,85:" +
"7,-1:7,85:6,-1:3,85:6,88,76,-1:5,85:7,-1:7,85,77,85:4,-1:3,85:8,-1:5,85:7,-" +
"1:7,36:6,-1:3,36:8,-1:5,36:6,86,-1:7,36:3,93,36:2,-1:3,36:8,-1:5,36:7");

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
						{ return new Symbol(sym.INTVALUE, Integer.parseInt(yytext()));}
					case -14:
						break;
					case 14:
						{ return new Symbol(sym.COLON);}
					case -15:
						break;
					case 15:
						{ yybegin(OUTPUTFUNCTIONSTATE);
				  TempString.str = "";
				  IndexNum.num = 1;}
					case -16:
						break;
					case 16:
						{ return new Symbol(sym.RPAREN);}
					case -17:
						break;
					case 17:
						{ yybegin(PATTERNSTATE); return new Symbol(sym.VLINE);}
					case -18:
						break;
					case 18:
						{ return new Symbol(sym.DEFINE);}
					case -19:
						break;
					case 19:
						{ return new Symbol(sym.MAPTO);}
					case -20:
						break;
					case 20:
						{ return new Symbol(sym.APPEND);}
					case -21:
						break;
					case 21:
						{ yybegin(QUERYSTATE); }
					case -22:
						break;
					case 22:
						{ return new Symbol(sym.INT);}
					case -23:
						break;
					case 23:
						{ return new Symbol(sym.WITH);}
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
						{ return new Symbol(sym.MATCH);}
					case -27:
						break;
					case 27:
						{ return new Symbol(sym.BV);}
					case -28:
						break;
					case 28:
						{ return new Symbol(sym.FUNCTION);}
					case -29:
						break;
					case 29:
						{ TempString.str += yytext();}
					case -30:
						break;
					case 30:
						{ yybegin(PROGRAMSTATE);
			 	  return new Symbol(sym.PREDICATE, TempString.str);}
					case -31:
						break;
					case 31:
						{ TempString.str += yytext();}
					case -32:
						break;
					case 32:
						{ Symbol S = new Symbol(sym.OUTPUTFUNCTION, TempString.str);
				  TempString.str = "";
				  IndexNum.num ++;
				  return S;}
					case -33:
						break;
					case 33:
						{ yybegin(PROGRAMSTATE);
				  Symbol S = new Symbol(sym.OUTPUTFUNCTION, TempString.str);
				  return S;}
					case -34:
						break;
					case 34:
						{}
					case -35:
						break;
					case 35:
						{return new Symbol(sym.SEMICOLON);}
					case -36:
						break;
					case 36:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -37:
						break;
					case 37:
						{return new Symbol(sym.INVERT);}
					case -38:
						break;
					case 38:
						{ TempString.str += yytext();}
					case -39:
						break;
					case 39:
						{ yybegin(YYINITIAL); return new Symbol(sym.DOMAIN, TempString.str);}
					case -40:
						break;
					case 40:
						{}
					case -41:
						break;
					case 41:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -42:
						break;
					case 42:
						{ return new Symbol(sym.CONCAT);}
					case -43:
						break;
					case 43:
						{ return new Symbol(sym.NIL);}
					case -44:
						break;
					case 44:
						{ yybegin(PREDICATESTATE);
			  	  TempString.str = "";
			  	  return new Symbol(sym.WHEN);}
					case -45:
						break;
					case 46:
						{ System.err.println("Illegal character: "+yytext()); }
					case -46:
						break;
					case 47:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -47:
						break;
					case 48:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -48:
						break;
					case 49:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -49:
						break;
					case 51:
						{ System.err.println("Illegal character: "+yytext()); }
					case -50:
						break;
					case 52:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -51:
						break;
					case 54:
						{ System.err.println("Illegal character: "+yytext()); }
					case -52:
						break;
					case 55:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -53:
						break;
					case 57:
						{ System.err.println("Illegal character: "+yytext()); }
					case -54:
						break;
					case 58:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -55:
						break;
					case 60:
						{ System.err.println("Illegal character: "+yytext()); }
					case -56:
						break;
					case 61:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -57:
						break;
					case 63:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -58:
						break;
					case 65:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -59:
						break;
					case 68:
						{ System.err.println("Illegal character: "+yytext()); }
					case -60:
						break;
					case 69:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -61:
						break;
					case 70:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -62:
						break;
					case 71:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -63:
						break;
					case 72:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -64:
						break;
					case 73:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -65:
						break;
					case 74:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -66:
						break;
					case 75:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -67:
						break;
					case 76:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -68:
						break;
					case 77:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -69:
						break;
					case 78:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -70:
						break;
					case 79:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -71:
						break;
					case 80:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -72:
						break;
					case 81:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -73:
						break;
					case 82:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -74:
						break;
					case 83:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -75:
						break;
					case 84:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -76:
						break;
					case 85:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -77:
						break;
					case 86:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -78:
						break;
					case 87:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -79:
						break;
					case 88:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -80:
						break;
					case 89:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -81:
						break;
					case 90:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -82:
						break;
					case 91:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -83:
						break;
					case 92:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -84:
						break;
					case 93:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -85:
						break;
					case 94:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -86:
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
