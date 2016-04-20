#!/bin/sh
java JLex.Main SMTscanner.lex
mv SMTscanner.lex.java Yylex.java
java java_cup.Main SMTparser.cup
