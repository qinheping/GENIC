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
%%

WHITESPACE = [ \n\t\r\f]
LETTER = [a-zA-Z]
DIGIT = [0-9]
BIDIGIT = [0-1]
HEXLETTER = [a-fA-F0-9]


%eofval{
return new Symbol(sym.EOF);
%eofval}

%state DECLARESTATE
%state PROGRAMSTATE
%state PREDICATESTATE
%state OUTPUTFUNCTIONSTATE
%state QUERYSTATE

%cup

%%

<YYINITIAL> 	"("		{ yybegin(DECLARESTATE); TempString.str = "("; }
<YYINITIAL>	{WHITESPACE}	{}
<YYINITIAL>	"==="		{ yybegin(PROGRAMSTATE);}

<DECLARESTATE>	[^;]		{ TempString.str += yytext();}
<DECLARESTATE>	";"		{ yybegin(YYINITIAL); return new Symbol(sym.DECLARATION, TempString.str);}


<PROGRAMSTATE>	{WHITESPACE}	{}
<PROGRAMSTATE>	"match"		{ return new Symbol(sym.MATCH);}
<PROGRAMSTATE>	"function"	{ return new Symbol(sym.FUNCTION);}
<PROGRAMSTATE>	"("		{ return new Symbol(sym.LPAREN);}
<PROGRAMSTATE>	")"		{ return new Symbol(sym.RPAREN);}
<PROGRAMSTATE>	";"		{ return new Symbol(sym.SEMICOLON);}
<PROGRAMSTATE>	"|"		{ return new Symbol(sym.VLINE);}
<PROGRAMSTATE>	"->"		{ return new Symbol(sym.MAPTO);}
<PROGRAMSTATE>	":"		{ return new Symbol(sym.COLON);}
<PROGRAMSTATE>	":="		{ return new Symbol(sym.DEFINE);}
<PROGRAMSTATE>	"::"		{ return new Symbol(sym.CONCAT);}
<PROGRAMSTATE>	"++"		{ return new Symbol(sym.APPEND);}
<PROGRAMSTATE>	"Int"		{ return new Symbol(sym.INT);}
<PROGRAMSTATE>	"Bool"		{ return new Symbol(sym.BOOL);}
<PROGRAMSTATE>	"BitVec" 	{ return new Symbol(sym.BV);}
<PROGRAMSTATE>	"Char"		{ return new Symbol(sym.CHAR);}
<PROGRAMSTATE>	"with"		{ return new Symbol(sym.WITH);}
<PROGRAMSTATE>	{DIGIT}+	{ return new Symbol(sym.INTVALUE, Integer.parseInt(yytext()));}
<PROGRAMSTATE>	"["		{ yybegin(OUTPUTFUNCTIONSTATE);
				  TempString.str = "";
				  IndexNum.num = 1;}

<PROGRAMSTATE>	"when"		{ yybegin(PREDICATESTATE);
			  	  TempString.str = "";
			  	  return new Symbol(sym.WHEN);}

<PROGRAMSTATE>	"==="		{ yybegin(QUERYSTATE); }
<PROGRAMSTATE>	{LETTER}({LETTER}|{DIGIT})*	{ return new Symbol(sym.ID, new String(yytext()));}


<PREDICATESTATE>	[^;]	{ TempString.str += yytext();}
<PREDICATESTATE>	";"	{ yybegin(PROGRAMSTATE);
			 	  return new Symbol(sym.PREDICATE, TempString.str);}


<OUTPUTFUNCTIONSTATE>	[^;\]]	{ TempString.str += yytext();}
<OUTPUTFUNCTIONSTATE>	";"	{ Symbol S = new Symbol(sym.OUTPUTFUNCTION, TempString.str);
				  TempString.str = "";
				  IndexNum.num ++;
				  return S;}
<OUTPUTFUNCTIONSTATE>	"]"	{ yybegin(PROGRAMSTATE);
				  Symbol S = new Symbol(sym.OUTPUTFUNCTION, TempString.str);
				  return S;}

<QUERYSTATE>	{WHITESPACE}	{}
<QUERYSTATE>	"invert"	{return new Symbol(sym.INVERT);}
<QUERYSTATE>	";"		{return new Symbol(sym.SEMICOLON);}
<QUERYSTATE>	{LETTER}({LETTER}|{DIGIT})*	{ return new Symbol(sym.ID, new String(yytext()));}
. 				{ System.err.println("Illegal character: "+yytext()); }
