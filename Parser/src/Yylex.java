package Scanner;
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
	private final int yy_state_dtrans[] = {
		0,
		41,
		44,
		49,
		51,
		53
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
		/* 37 */ YY_NOT_ACCEPT,
		/* 38 */ YY_NO_ANCHOR,
		/* 39 */ YY_NO_ANCHOR,
		/* 40 */ YY_NO_ANCHOR,
		/* 41 */ YY_NOT_ACCEPT,
		/* 42 */ YY_NO_ANCHOR,
		/* 43 */ YY_NO_ANCHOR,
		/* 44 */ YY_NOT_ACCEPT,
		/* 45 */ YY_NO_ANCHOR,
		/* 46 */ YY_NO_ANCHOR,
		/* 47 */ YY_NOT_ACCEPT,
		/* 48 */ YY_NO_ANCHOR,
		/* 49 */ YY_NOT_ACCEPT,
		/* 50 */ YY_NO_ANCHOR,
		/* 51 */ YY_NOT_ACCEPT,
		/* 52 */ YY_NO_ANCHOR,
		/* 53 */ YY_NOT_ACCEPT,
		/* 54 */ YY_NO_ANCHOR,
		/* 55 */ YY_NO_ANCHOR,
		/* 56 */ YY_NO_ANCHOR,
		/* 57 */ YY_NO_ANCHOR,
		/* 58 */ YY_NO_ANCHOR,
		/* 59 */ YY_NO_ANCHOR,
		/* 60 */ YY_NO_ANCHOR,
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
		/* 82 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,130,
"33:9,35,2,33,35,2,33:18,35,33:7,1,16,33,21,33,18,33:2,29:10,20,5,33,3,19,33" +
":2,32,23,28,32:5,22,32:12,25,32:4,30,33,4,33:3,7,32,9,32,26,11,32,10,14,32:" +
"2,24,6,13,15,32:2,27,32,8,12,34,31,32:3,33,17,33:3,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,83,
"0,1:3,2,1:6,3,1:2,4,5,1:6,6:7,1:6,7:2,8,1,9,10,11,12,13,14,15,16,17,18,19,2" +
"0,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,6,39,40,41,42,43,44" +
",45,46,47,6,48,49,50")[0];

	private int yy_nxt[][] = unpackFromString(51,36,
"1,2,3,4,38:31,3,-1:39,37,-1:38,79,68,79:8,-1:6,79:7,69,-1,79:2,-1,79,-1:4,1" +
"8,-1:16,19,-1:44,15,-1:12,79:10,-1:6,79:7,69,-1,79:2,-1,79,-1:7,35:10,-1:6," +
"35:8,-1,35:2,-1,35,-1:4,5,-1:38,79:2,22,79:7,-1:6,79:7,69,-1,79:2,-1,79,-1:" +
"7,35:2,36,35:7,-1:6,35:8,-1,35:2,-1,35,-1,1,6:4,7,6:30,-1:19,17,-1:22,79:10" +
",-1:6,79:2,23,79:4,69,-1,79:2,-1,79,-1,1,8,9,55,38,10,11,79:4,81,79:4,12,13" +
",42,38,14,45,56,64,79:4,66,15,16,67,79,38,79,9,-1:21,20,-1:20,79:10,-1:6,79" +
":5,24,79,69,-1,79:2,-1,79,-1:4,21,-1:38,79:4,25,79:5,-1:6,79:7,69,-1,79:2,-" +
"1,79,-1,1,29:4,30,29:30,-1:6,79:4,26,79:5,-1:6,79:7,69,-1,79:2,-1,79,-1,1,3" +
"1:3,32,33,31:30,-1:6,79:7,27,79:2,-1:6,79:7,69,-1,79:2,-1,79,-1,1,38,-1,38:" +
"2,34,35:8,82,35,38:6,35:7,38:2,35:2,38,35,38,-1:6,79:10,-1:6,79:5,28,79,69," +
"-1,79:2,-1,79,-1:4,47,-1:38,79:7,39,79:2,-1:6,79:7,69,-1,79:2,-1,79,-1:7,35" +
":10,-1:6,35:5,40,35:2,-1,35:2,-1,35,-1:7,79:9,43,-1:6,79:7,69,-1,79:2,-1,79" +
",-1:7,79,46,79:8,-1:6,79:7,69,-1,79:2,-1,79,-1:7,79:2,48,79:7,-1:6,79:7,69," +
"-1,79:2,-1,79,-1:7,79:3,50,79:6,-1:6,79:7,69,-1,79:2,-1,79,-1:7,79:9,52,-1:" +
"6,79:7,69,-1,79:2,-1,79,-1:7,79:9,54,-1:6,79:7,69,-1,79:2,-1,79,-1:7,79:8,7" +
"1,58,-1:6,79:7,69,-1,79:2,-1,79,-1:7,35:10,-1:6,35:4,57,35:3,-1,35:2,-1,35," +
"-1:7,79:4,59,79:5,-1:6,79:7,69,-1,79:2,-1,79,-1:7,79:8,60,79,-1:6,79:7,69,-" +
"1,79:2,-1,79,-1:7,79:2,61,79:7,-1:6,79:7,69,-1,79:2,-1,79,-1:7,79:7,72,79:2" +
",-1:6,79:7,69,-1,79:2,-1,79,-1:7,79:2,73,79:7,-1:6,79:7,69,-1,79:2,-1,79,-1" +
":7,79:3,74,79:6,-1:6,79:7,69,-1,79:2,-1,79,-1:7,79:10,-1:6,79:3,75,79:3,69," +
"-1,79:2,-1,79,-1:7,79:2,76,79:7,-1:6,79:7,69,-1,79:2,-1,79,-1:7,79:10,-1:6," +
"79:4,77,79:2,69,-1,79:2,-1,79,-1:7,79:8,62,79,-1:6,79:7,69,-1,79:2,-1,79,-1" +
":7,79:3,78,79:6,-1:6,79:7,69,-1,79:2,-1,79,-1:7,79:2,63,79:7,-1:6,79:7,69,-" +
"1,79:2,-1,79,-1:7,35:10,-1:6,35:8,-1,35:2,-1,65,-1:7,79:6,70,79:3,-1:6,79:7" +
",69,-1,79:2,-1,79,-1:7,35:7,80,35:2,-1:6,35:8,-1,35:2,-1,35,-1");

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
						{ yybegin(YYINITIAL); return new Symbol(sym.DECLARATION, TempString.str);}
					case -8:
						break;
					case 8:
						{ return new Symbol(sym.LPAREN);}
					case -9:
						break;
					case 9:
						{}
					case -10:
						break;
					case 10:
						{ return new Symbol(sym.SEMICOLON);}
					case -11:
						break;
					case 11:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -12:
						break;
					case 12:
						{ return new Symbol(sym.RPAREN);}
					case -13:
						break;
					case 13:
						{ return new Symbol(sym.VLINE);}
					case -14:
						break;
					case 14:
						{ return new Symbol(sym.COLON);}
					case -15:
						break;
					case 15:
						{ return new Symbol(sym.INTVALUE, Integer.parseInt(yytext()));}
					case -16:
						break;
					case 16:
						{ yybegin(OUTPUTFUNCTIONSTATE);
				  TempString.str = "";
				  IndexNum.num = 1;}
					case -17:
						break;
					case 17:
						{ return new Symbol(sym.MAPTO);}
					case -18:
						break;
					case 18:
						{ return new Symbol(sym.DEFINE);}
					case -19:
						break;
					case 19:
						{ return new Symbol(sym.CONCAT);}
					case -20:
						break;
					case 20:
						{ return new Symbol(sym.APPEND);}
					case -21:
						break;
					case 21:
						{ yybegin(QUERYSTATE);}
					case -22:
						break;
					case 22:
						{ return new Symbol(sym.INT);}
					case -23:
						break;
					case 23:
						{ return new Symbol(sym.BOOL);}
					case -24:
						break;
					case 24:
						{ return new Symbol(sym.CHAR);}
					case -25:
						break;
					case 25:
						{ yybegin(PREDICATESTATE);
			  	  TempString.str = "";
			  	  return new Symbol(sym.WITH);}
					case -26:
						break;
					case 26:
						{ return new Symbol(sym.MATCH);}
					case -27:
						break;
					case 27:
						{ return new Symbol(sym.FUNCTION);}
					case -28:
						break;
					case 28:
						{ return new Symbol(sym.BV);}
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
						{ yybegin(PROGRAMSTATE);
				  Symbol S = new Symbol(sym.OUTPUTFUNCTION, TempString.str);
				  return S;}
					case -33:
						break;
					case 33:
						{ Symbol S = new Symbol(sym.OUTPUTFUNCTION, TempString.str);
				  TempString.str = "";
				  IndexNum.num ++;
				  return S;}
					case -34:
						break;
					case 34:
						{return new Symbol(sym.SEMICOLON);}
					case -35:
						break;
					case 35:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -36:
						break;
					case 36:
						{return new Symbol(sym.INVERT);}
					case -37:
						break;
					case 38:
						{ System.err.println("Illegal character: "+yytext()); }
					case -38:
						break;
					case 39:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -39:
						break;
					case 40:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -40:
						break;
					case 42:
						{ System.err.println("Illegal character: "+yytext()); }
					case -41:
						break;
					case 43:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -42:
						break;
					case 45:
						{ System.err.println("Illegal character: "+yytext()); }
					case -43:
						break;
					case 46:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -44:
						break;
					case 48:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -45:
						break;
					case 50:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -46:
						break;
					case 52:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -47:
						break;
					case 54:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -48:
						break;
					case 55:
						{ System.err.println("Illegal character: "+yytext()); }
					case -49:
						break;
					case 56:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -50:
						break;
					case 57:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -51:
						break;
					case 58:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -52:
						break;
					case 59:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -53:
						break;
					case 60:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -54:
						break;
					case 61:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -55:
						break;
					case 62:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -56:
						break;
					case 63:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -57:
						break;
					case 64:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -58:
						break;
					case 65:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -59:
						break;
					case 66:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -60:
						break;
					case 67:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -61:
						break;
					case 68:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -62:
						break;
					case 69:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -63:
						break;
					case 70:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -64:
						break;
					case 71:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -65:
						break;
					case 72:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -66:
						break;
					case 73:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -67:
						break;
					case 74:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -68:
						break;
					case 75:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -69:
						break;
					case 76:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -70:
						break;
					case 77:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -71:
						break;
					case 78:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -72:
						break;
					case 79:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -73:
						break;
					case 80:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -74:
						break;
					case 81:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -75:
						break;
					case 82:
						{ return new Symbol(sym.ID, new String(yytext()));}
					case -76:
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
