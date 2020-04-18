package com.carlosjacob;
import java.util.ArrayList;
import java.util.List;

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

    public Syntax(){
    }

    public enum compositionBase {
        STATEMENT(0, "<Statement> -> <Declarative> | <Assignment>"),
        DECLARATIVE(1, "<Declarative> -> <Type> <ID>"),
        TYPE(2, "int, float, bool"),
        IDENTIFIER(3, "<ID> - > id"),
        ASSIGNMENT(4, "<Assignment> -> <Identifier> = <Expression>;"),
        EXPRESSION(5, "<Expression> -> <Expression> + <Term> | <Expression> - <Term> | <Term>"),
        TERM(6, "<Term> -> <Term> * <Factor> | <Term> / <Factor> | <Factor>"),
        FACTOR(7, "<Factor> -> ( <Expression> ) | <ID> | <num>"),
        NUMBER(8),
        EMPTY(9,"<Empty> -> Epsilon"),
        PRIMARY(10,"<Primary> -> <ID>,<num>"),
        TERMPRIME(11,"<TermPrime> -> * <Factor> <TermPrime> | / <Factor> <TermPrime> | <Empty>"),
        EXPRESSIONPRIME(12,"<ExpressionPrime> -> + <Term> <ExpressionPrime> | - <Term> <ExpressionPrime> | <Empty>"),
        EMPTYPRIME(13,"<Empty> -> Epsilon"),
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

    public class Line{
        public List<Token> tokens;
        public Line(){
            tokens = new ArrayList<>();
        }
    }

    public List<Line> createLines(List<Token> tokensFromLexer) throws Exception{
        List<Line> lines = new ArrayList<>();
        Line line = new Line();
        Token currToken;

        for(int i = 0; i < tokensFromLexer.size(); i++){
            currToken = tokensFromLexer.get(i);
            line.tokens.add(currToken);
            if(currToken.lexemeName.equals(";")){
                lines.add(line);
                line = new Line();
                continue;
            }
        }
        return lines;
    }

    public class currentParsing{
        public compositionBase currentSyntax;
        public Token currentToken;
        public Token nextToken;

        public currentParsing(compositionBase currentSyntax, Token currentToken, Token nextToken){
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

            addRules(parser, currentCharacterAnalysis);
        }
        return tokenRules;
    }

    public void addRules(currentParsing parser, List<compositionBase> currentCharacterAnalysis) {
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
            if(parser.currentToken.tokenName == Lexer.State.IDENTIFIER && parser.currentSyntax==compositionBase.TERMPRIME){
                parser.currentSyntax = compositionBase.FACTOR;
            }
            if(parser.currentToken.tokenName == Lexer.State.IDENTIFIER && parser.currentSyntax==compositionBase.EXPRESSIONPRIME){
                parser.currentSyntax = compositionBase.TERM;
            }
            switch (parser.currentSyntax) {
                case STATEMENT:
                    if (parser.currentToken.tokenName == Lexer.State.IDENTIFIER) {
                        currentCharacterAnalysis.add(compositionBase.ASSIGNMENT);
                        parser.currentSyntax = compositionBase.ASSIGNMENT;
                    } else if (parser.currentToken.tokenName == Lexer.State.KEYWORD) {
                        currentCharacterAnalysis.add(compositionBase.DECLARATIVE);
                        parser.currentSyntax = compositionBase.PRIMARY;
                        return;
                    }
                    break;
                case ASSIGNMENT:
                    return;
                case NUMBER:
                case IDENTIFIER:
                case DECLARATIVE:
                case EXPRESSION:
                    currentCharacterAnalysis.add(compositionBase.EXPRESSION);
                    parser.currentSyntax = compositionBase.TERM;
                case TERM:
                    currentCharacterAnalysis.add(compositionBase.TERM);
                    parser.currentSyntax = compositionBase.FACTOR;
                    break;
                case FACTOR:
                    currentCharacterAnalysis.add(compositionBase.FACTOR);
                    parser.currentSyntax = compositionBase.PRIMARY;
                case PRIMARY:
                    currentCharacterAnalysis.add(compositionBase.PRIMARY);
                    parser.currentSyntax = compositionBase.EMPTY;
                    return;
                case ERROR:
                    break;
                case TYPE:
                    break;
                case EMPTY:
                    currentCharacterAnalysis.add(compositionBase.EMPTY);
                    parser.currentSyntax = compositionBase.TERMPRIME;
                    break;
                case TERMPRIME:
                    currentCharacterAnalysis.add(compositionBase.TERMPRIME);
                    if(parser.currentToken.lexemeName.equals("*") || parser.currentToken.lexemeName.equals("/")){
                        return;
                    }
                    parser.currentSyntax = compositionBase.EMPTYPRIME;
                    break;
                case EMPTYPRIME:
                    currentCharacterAnalysis.add(compositionBase.EMPTYPRIME);
                    parser.currentSyntax = compositionBase.EXPRESSIONPRIME;
                    break;
                case EXPRESSIONPRIME:
                    currentCharacterAnalysis.add(compositionBase.EXPRESSIONPRIME);
                    if(parser.currentToken.lexemeName.equals("+") || parser.currentToken.lexemeName.equals("-")){
                        return;
                    }
                    currentCharacterAnalysis.add(compositionBase.EMPTY);
                default:
                    return;
            }
        }
    }

    public void printLine(Line line){
        for(Token token : line.tokens){
            System.out.print(token.lexemeName + " ");
        }
        System.out.println();
    }

    public void printAllLines(List<Line> lines){
        Token currToken = null;
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
