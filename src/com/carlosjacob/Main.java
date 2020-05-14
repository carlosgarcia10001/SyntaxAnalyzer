package com.carlosjacob;

import java.util.List;

import com.carlosjacob.Syntax.Line;

public class Main {

    public static void main(String[] args) {
    	List<Token> tokenList = null;
    	try{
    		Lexer lex = new Lexer();
    		tokenList = lex.createTokenList(args[0]);
    		lex.createLexerOutputFile(tokenList);    		
    	}catch(Exception e){
    		System.out.println("Please specify an input file in the command line");
    	}
    	
    	try {
			Syntax syntax = new Syntax();
        	List<Line> lines = syntax.createLines(tokenList);        	
        	syntax.printAllLines(lines);
        	System.out.println();
			CodeGenerator generator = new CodeGenerator(syntax.syntaxRules(lines), syntax.createLines(tokenList));
        }catch(Exception e){
        	System.out.println(e.getMessage());
        	return;
        }
    }
}