package Parser;
import java.io.*;
import java.util.*;

//**********************************************************************
//
//  Subclass            Kids
//  --------            ----
//  CoderNode			DeclListNode, ProgListNode, QueryListNode
//  DeclListNode        linked list of DeclNode
//	ProgListNode        linked list of ProgNode
//	QueryListNode       linked list of QueryNode
//  DeclNode			

//    VarDeclNode       TypeNode, IdNode, int
//    FnDeclNode        TypeNode, IdNode, FormalsListNode, FnBodyNode
//    FormalDeclNode    TypeNode, IdNode
//    StructDeclNode    IdNode, DeclListNode
//
//  FormalsListNode     linked list of FormalDeclNode
//  FnBodyNode          DeclListNode, StmtListNode
//  StmtListNode        linked list of StmtNode
//  ExpListNode         linked list of ExpNode
//
//  TypeNode:
//    IntNode           -- none --
//    BoolNode          -- none --
//    VoidNode          -- none --
//    StructNode        IdNode
//
//  StmtNode:
//    AssignStmtNode      AssignNode
//    PostIncStmtNode     ExpNode
//    PostDecStmtNode     ExpNode
//    ReadStmtNode        ExpNode
//    WriteStmtNode       ExpNode
//    IfStmtNode          ExpNode, DeclListNode, StmtListNode
//    IfElseStmtNode      ExpNode, DeclListNode, StmtListNode,
//                                 DeclListNode, StmtListNode
//    WhileStmtNode       ExpNode, DeclListNode, StmtListNode
//    CallStmtNode        CallExpNode
//    ReturnStmtNode      ExpNode
//
//  ExpNode:
//    IntLitNode          -- none --
//    StrLitNode          -- none --
//    TrueNode            -- none --
//    FalseNode           -- none --
//    IdNode              -- none --
//    DotAccessNode       ExpNode, IdNode
//    AssignNode          ExpNode, ExpNode
//    CallExpNode         IdNode, ExpListNode
//    UnaryExpNode        ExpNode
//      UnaryMinusNode
//      NotNode
//    BinaryExpNode       ExpNode ExpNode
//      PlusNode     
//      MinusNode
//      TimesNode
//      DivideNode
//      AndNode
//      OrNode
//      EqualsNode
//      NotEqualsNode
//      LessNode
//      GreaterNode
//      LessEqNode
//      GreaterEqNode
//
//Here are the different kinds of AST nodes again, organized according to
//whether they are leaves, internal nodes with linked lists of kids, or
//internal nodes with a fixed number of kids:
//
//(1) Leaf nodes:
//     IntNode,   BoolNode,  VoidNode,  IntLitNode,  StrLitNode,
//     TrueNode,  FalseNode, IdNode
//
//(2) Internal nodes with (possibly empty) linked lists of children:
//     DeclListNode, FormalsListNode, StmtListNode, ExpListNode
//
//(3) Internal nodes with fixed numbers of kids:
//     ProgramNode,     VarDeclNode,     FnDeclNode,     FormalDeclNode,
//     StructDeclNode,  FnBodyNode,      StructNode,     AssignStmtNode,
//     PostIncStmtNode, PostDecStmtNode, ReadStmtNode,   WriteStmtNode   
//     IfStmtNode,      IfElseStmtNode,  WhileStmtNode,  CallStmtNode
//     ReturnStmtNode,  DotAccessNode,   CallExpNode,
//     UnaryExpNode,    BinaryExpNode,   UnaryMinusNode, NotNode,
//     PlusNode,        MinusNode,       TimesNode,      DivideNode,
//     AndNode,         OrNode,          EqualsNode,     NotEqualsNode,
//     LessNode,        GreaterNode,     LessEqNode,     GreaterEqNode
//
//**********************************************************************

//**********************************************************************
//ASTnode class (base class for all other kinds of nodes)
//**********************************************************************

abstract class ASTnode { 

}

class CoderNode extends ASTnode{
	public CoderNode(DeclListNode D, ProgListNode P, QueryListNode Q){
		myDeclList = D;
		myProgList = P;
		myQueryList = Q;
	}
	
	private DeclListNode myDeclList;
	private ProgListNode myProgList;
	private QueryListNode myQueryList;
}

class DeclListNode extends ASTnode{
	public DeclListNode(List<DeclNode> D){
		myDecls = D;
	}
	private List<DeclNode> myDecls;
}

class ProgListNode extends ASTnode{
	public ProgListNode(List<ProgNode> P){
		myProg = P;
	}
	private List<ProgNode> myProgs;
}

class QueryListNode extends ASTnode{
	public QueryListNode(List<QueryNode> Q){
		myQueries = Q;
	}
	private List<QueryNode> myQueries;
}

class DeclNode extends ASTnode{
	
}

