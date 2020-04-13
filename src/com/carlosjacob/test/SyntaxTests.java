package com.carlosjacob.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.carlosjacob.*;
import com.carlosjacob.Lexer.State;
import com.carlosjacob.Syntax.Line;

import junit.framework.*;

public class SyntaxTests extends TestCase{
	protected String statement1;
	protected Syntax syntaxAnalyzer;
	protected Line line;
	
	List<Token> tokenList = new ArrayList<>();
	
	protected void setUp() {
		//Token list comprising of 2 tokens: 4 and ;
		tokenList.add(new Token(State.NUMBER, "4"));
		tokenList.add(new Token(State.END_STATEMENT, ";"));
		syntaxAnalyzer = new Syntax(tokenList);
	}
	
	//createLines();
	public void testLineCreation() {
		Line expectedLine = syntaxAnalyzer.new Line();
		expectedLine.tokens.add(tokenList.get(0));
		expectedLine.tokens.add(tokenList.get(1));
		
		List<Line> expectedLineList = Arrays.asList(expectedLine);
		List<Line> linesCreated;
		try {
			linesCreated = syntaxAnalyzer.createLines(tokenList);
			assertTrue(expectedLineList.equals(linesCreated));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
//	//parseLine();
//	public void testLineParser() {
//		
//	}
//	
//	//printLine();
//	public void testPrintLine() {
//		
//	}
//	
//	//printAllLines();
//	public void testPrintAllLines() {
//		
//	}
}
