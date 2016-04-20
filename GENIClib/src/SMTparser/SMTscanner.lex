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
%%

BINCONSTANT =	#b[0-1]+
HEXCONSTANT =	#x[0-9a-fA-F]+
BVCONSTANT  =   bv[0-9]+
RATCONSTANT =   [0-9]+\.[0-9]+
NUMERAL     =   [0-9]+
SYMBOL      =   [a-zA-Z0-9._+\-*=%/?!$_~&^<>@]+

%eofval{
return new Symbol(sym.EOF);
%eofval}


%cup

%%

";"[^\n]*\n     {  }
\n              {  }
\r              {  }
[ \t]           {  }

"("		{ return new Symbol(sym.LEFTPAREN); }
")"		{ return new Symbol(sym.RIGHTPAREN); }

"_"		{ return new Symbol(sym.TK_UNDERSCORE);}
"forall"	{ return new Symbol(sym.TK_FORALL); }
"exists"	{ return new Symbol(sym.TK_EXISTS); }

"define-fun"	{ return new Symbol(sym.TK_DEFINE_FUN); }

{BINCONSTANT}  { return new Symbol(sym.BINCONSTANT, new String(yytext())); }
{HEXCONSTANT}  { return new Symbol(sym.HEXCONSTANT, new String(yytext())); }
{RATCONSTANT}  { return new Symbol(sym.RATCONSTANT, new String(yytext())); }
{BVCONSTANT}   { return new Symbol(sym.BVCONSTANT, new String(yytext())); }
{NUMERAL}      { return new Symbol(sym.NUMERAL, new String(yytext())); }
{SYMBOL}       { return new Symbol(sym.SYMBOL, new String(yytext())); }
