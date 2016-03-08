#!/bin/sh
java JLex.Main scanner.lex
mv scanner.lex.java Yylex.java
java java_cup.Main scanner.cup
javac -d . parser.java sym.java Yylex.java
