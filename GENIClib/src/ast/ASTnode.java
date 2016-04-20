package ast;
import java.io.*;
import java.util.*;

//**********************************************************************
//
//  Subclass            Kids
//  --------            ----
//  CodeNode			DeclListNode, ProgListNode, QueryListNode
//  DeclListNode        linked list of DeclNode
//	ProgListNode        linked list of ProgNode
//	QueryListNode       linked list of QueryNode
//  DeclNode			String
//
//Here are the different kinds of AST nodes again, organized according to
//whether they are leaves, internal nodes with linked lists of kids, or
//internal nodes with a fixed number of kids:
//
//(1) Leaf nodes:
//    
//
//(2) Internal nodes with (possibly empty) linked lists of children:
//    
//
//(3) Internal nodes with fixed numbers of kids:
//
//**********************************************************************

//**********************************************************************
//ASTnode class (base class for all other kinds of nodes)
//**********************************************************************



public abstract class ASTnode { 
	abstract public void print_this();
}








