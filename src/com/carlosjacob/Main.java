package com.carlosjacob;

import java.util.List;

public class Main {

    public static void main(String[] args) {
    	try{
    		Lexer lex = new Lexer();
    		List<Token> tokenList = lex.createTokenList(args[0]);
    		lex.createLexerOutputFile(tokenList);
    		
    		Syntax syntax = new Syntax(tokenList);
    	} catch(Exception e){
    		System.out.println("Please specify an input file in the command line");
    	}
    }
}