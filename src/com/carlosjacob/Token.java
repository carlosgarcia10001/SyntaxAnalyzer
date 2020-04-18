package com.carlosjacob;

import com.carlosjacob.Lexer.State;

public class Token {
    public State tokenName;
    public String lexemeName;

    public Token(){
    }
    
    public Token(Lexer.State tokenName, String lexemeName){
    	this.tokenName = tokenName;
    	this.lexemeName = lexemeName;
    }
    
}
