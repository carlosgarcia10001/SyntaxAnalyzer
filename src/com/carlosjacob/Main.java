package com.carlosjacob;

public class Main {

    public static void main(String[] args) {
    	try{
    		Syntax syntax = new Syntax(args[0]);    		
    	} catch(Exception e){
    		System.out.println("Please specify an input file in the command line");
    	}
    }
}