package com.carlosjacob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
<Statement> -> <Declarative>
<Declarative> -> <Type> <id>

<Statement> -> <Assign>
<Assign> -> <ID> = <Expression>;

<Expression> -> <Expression> + <Term> | <Expression> - <Term> | <Term>

<Term> -> <Term> * <Factor> | <Term> / <Factor> | <Factor>

<Factor> -> ( <Expression> ) | <ID> | <num>

<ID> -> id
*/

public class Syntax {
    private Lexer lex;
    private List<Lexer.Token> tokens;
    private List<Line> lines;
    public Syntax(String fileName){
        lex = new Lexer();
        lex.feedMe(fileName);
        tokens = lex.createTokenList(fileName);
        try {
        	lines = createLines();        	
        } catch(Exception e){
        	System.out.println(e.getMessage());
        	return;
        }
        printAllLines();   
    }
    
    public enum compositionBase {
        STATEMENT(0, "<Statement> -> <Declarative> | <Assignment>"),
        DECLARATIVE(1, "<Type> <ID> <MoreIds>; | <empty>"),
        TYPE(2, "int, float, bool"),
        IDENTIFIER(3),
        ASSIGNMENT(4, "<Assignment> -> <Identifier> = <Expression>;"),
        EXPRESSION(5, "<Expression> -> <Expression> + <Term> | <Expression> - <Term> | <Term>"),
        TERM(6, "<Term> -> <Term> * <Factor> | <Term> / <Factor> | <Factor>"),
        FACTOR(7, "<Factor> -> ( <Expression> ) | <ID> | <num>"),
        NUMBER(8),
        EMPTY(9,"<Empty> -> Epsilon"),
        PRIMARY(10,"<ID>,<num>"),
        ERROR(-1);

        private int specificComposition;
        private String syntaxString;
        
        compositionBase(int composition, String syntaxString){
            this.syntaxString = syntaxString;
            this.specificComposition = composition;
        }
        
        compositionBase(int composition){
            this.specificComposition = composition;
        }

        public int getComposition(){
            return specificComposition;
        }

        public String getSyntaxString(){
            return syntaxString;
        }
    }

    public enum fullComposition {
        STATEMENT(0, "<Statement> -> <Declarative> | <Assignment>"),
        DECLARATIVE(1, "<Declarative> -> <Type> <id>"),
        TYPE(2),
        IDENTIFIER(3),
        ASSIGNMENT(4, "<Assign> -> <Identifier> = <Expression>;"),
        EXPRESSION(5, "<Expression> -> <Expression> + <Term> | <Expression> - <Term> | <Term>"),
        TERM(6, "<Term> -> <Term> * <Factor> | <Term> / <Factor> | <Factor>"),
        FACTOR(7, "<Factor> -> ( <Expression> ) | <ID> | <num>"),
        NUMBER(8),
        EMPTY(9,"<Empty> -> Epsilon"),
        PRIMARY(10,"<ID>,<num>");

        private int specificCompositionIndex;
        private String syntaxString;
        private compositionBase[][][] fullCompositions = {
                {{compositionBase.DECLARATIVE}, {compositionBase.ASSIGNMENT}},
                {{compositionBase.TYPE, compositionBase.IDENTIFIER}}
        };
        
        public String getString(){
            return syntaxString;
        }
        fullComposition(int index, String syntaxString){
            this.specificCompositionIndex = index;
            this.syntaxString = syntaxString;

        }
        fullComposition(int index){
            this.specificCompositionIndex = index;
        }

        public compositionBase[][] getComposition(){
            if(this.getCompositionIndex() == -1) {
                return null;
            }
            return this.fullCompositions[this.getCompositionIndex()];
        }

        public int getCompositionIndex(){
            return specificCompositionIndex;
        }

        public static compositionBase returnEnumFromId(int index){
            switch(index){
                case 0:
                    return compositionBase.STATEMENT;
                case 1:
                    return compositionBase.DECLARATIVE;
                case 2:
                    return compositionBase.TYPE;
                case 3:
                    return compositionBase.IDENTIFIER;
                case 4:
                    return compositionBase.ASSIGNMENT;
                case 5:
                    return compositionBase.EXPRESSION;
                case 6:
                    return compositionBase.TERM;
                case 7:
                    return compositionBase.FACTOR;
                case 8:
                    return compositionBase.NUMBER;
            }
            return compositionBase.ERROR;
        }
    }

    public class Line{
        private List<Lexer.Token> tokens;
        public Line(){
            tokens = new ArrayList<>();
        }
    }

    public List<Line> createLines() throws Exception{
        List<Line> lines = new ArrayList<>();
        Line line = new Line();
        Lexer.Token currToken;
        
        List<String> errors = new ArrayList<>();

        for(int i = 0; i < tokens.size(); i++){
        	currToken = tokens.get(i);
            line.tokens.add(currToken);
            if(currToken.lexemeName.equals(";")){
                lines.add(line);
                line = new Line();
                continue;
            } else if(i == tokens.size()-1) {
            	//They didn't at least have the end of the token with a semicolon
            	errors.add("Expected a semicolon after token " + currToken.lexemeName);
            }
        }
        if(errors.size() != 0) {
        	for(String error : errors) {
        		System.out.println(error);
        	}
        	throw new Exception("Syntax Error");
        }
        return lines;
    }

    public class currentParsing{
        public compositionBase currentSyntax;
        public Lexer.Token currentToken;
        public Lexer.Token nextToken;

        public currentParsing(compositionBase currentSyntax, Lexer.Token currentToken, Lexer.Token nextToken){
            this.currentSyntax = currentSyntax;
            this.currentToken = currentToken;
            this.nextToken = nextToken;
        }
    }
    
    // Parses the input line and returns a list of the proper grammatical rules for that token
    public List<List<compositionBase>> parseLine(Line line){
    	
    	//A 2D array containing the rules for each token in the line
        List<List<compositionBase>> tokenRules = new ArrayList<>();
        
        currentParsing parser = new currentParsing(compositionBase.STATEMENT, line.tokens.get(0), line.tokens.get(1));
        parser.currentSyntax = compositionBase.STATEMENT;
        
        for(int i = 0; i < line.tokens.size(); i++){
            tokenRules.add(new ArrayList<>());
        }
        
        //"Blimith Today at 11:16 AM
        //Every line is a statement of some sort"
        tokenRules.get(0).add(compositionBase.STATEMENT);
        
        for(int i = 0; i < line.tokens.size(); i++){
            List<compositionBase> currentCharacterAnalysis = tokenRules.get(i);
            parser.currentToken = line.tokens.get(i);
            
            //Ignore displaying comments
            if(parser.currentToken.tokenName == Lexer.State.IN_COMMENT) {
            	continue;
            }
            
            //Check to make sure we can get the next token
            //If we can, get it, else the next is the current
            if(i != line.tokens.size()-1) {
            	parser.nextToken = line.tokens.get(i+1);            	
            } else {
            	parser.nextToken = parser.currentToken;
            }
            
            rulesNeedToBeAdded:
            {
                while (true) {
                    if(parser.currentToken.lexemeName.equals(";")){
                        parser.currentSyntax = compositionBase.EMPTY;
                        currentCharacterAnalysis.add(compositionBase.EMPTY);
                        break;
                    }
                    if (parser.currentToken.tokenName == Lexer.State.OPERATOR && parser.currentToken.lexemeName.equals("=")) {
                        parser.currentSyntax = compositionBase.EXPRESSION;
                        break;
                    }
                    switch (parser.currentSyntax) {
                        case STATEMENT:
                            if (parser.currentToken.tokenName == Lexer.State.IDENTIFIER) {
                                currentCharacterAnalysis.add(compositionBase.ASSIGNMENT);
                                parser.currentSyntax = compositionBase.ASSIGNMENT;
                            } else if (parser.currentToken.tokenName == Lexer.State.KEYWORD) {
                                currentCharacterAnalysis.add(compositionBase.DECLARATIVE);
                                parser.currentSyntax = compositionBase.DECLARATIVE;
                            }
                            break;
                        case ASSIGNMENT:
                        case NUMBER:
                        case IDENTIFIER:
                        case DECLARATIVE:
                        case FACTOR:
//                            break rulesNeedToBeAdded;
                        case EXPRESSION:
                            if(parser.nextToken.lexemeName.equals("+")||parser.nextToken.lexemeName.equals("-")){
                                currentCharacterAnalysis.add(compositionBase.TERM);
                                parser.currentSyntax = compositionBase.TERM;
//                                break rulesNeedToBeAdded;
                            }else{
                                currentCharacterAnalysis.add(compositionBase.TERM);
                                parser.currentSyntax = compositionBase.TERM;
                            }
                        case TERM:
                            if(parser.nextToken.lexemeName.equals("*")||parser.nextToken.lexemeName.equals("/")){
//                            	break;
                                break rulesNeedToBeAdded;
                            }else{
                                currentCharacterAnalysis.add(compositionBase.FACTOR);
                                parser.currentSyntax = compositionBase.FACTOR;
                                break rulesNeedToBeAdded;
                            }
                    }
                }
            }
        }
        return tokenRules;
    }
    
    public void printLine(Line line){
        for(Lexer.Token token : line.tokens){
            System.out.print(token.lexemeName + " ");
        }
        System.out.println();
    }
    
    public void printAllLines(){
    	Lexer.Token currToken = null;
        for(Line line : lines){
            List<List<compositionBase>> list = parseLine(line);
            for(int i = 0; i < line.tokens.size(); i++){
            	currToken = line.tokens.get(i);
                System.out.print("Token: " + currToken.tokenName);
                System.out.println("\t\tLexeme: " + currToken.lexemeName);
            	if(currToken.tokenName != Lexer.State.IN_COMMENT && list.get(i).size() != 0) {

            		
            		//Print all the syntax rules for this token type
            		for(int j = 0; j < list.get(i).size(); j++){
            			System.out.println(list.get(i).get(j).getSyntaxString());
            		}            		
            	}
            }
        }
    }
}
