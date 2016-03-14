#!/bin/sh
java JLex.Main scanner.lex
mv scanner.lex.java Yylex.java
java java_cup.Main parser.cup
