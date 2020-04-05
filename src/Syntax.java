import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Syntax {
    private Lexer lex;
    private List<Lexer.Token> tokens;
    private List<Line> lines;
    public Syntax(String fileName){
        lex = new Lexer();
        lex.feedMe(fileName);
        tokens = lex.createTokenList(fileName);
        lines = createLines();
        printAllLines();
    }
    
    public void printAllLines(){
        for(Line line:lines){
            List<List<compositionBase>> list = parseLine(line);
            for(int i = 0; i < line.tokens.size();i++){
                System.out.print("Token: " + line.tokens.get(i).tokenName);
                System.out.println("\t\tLexeme: " + line.tokens.get(i).lexemeName);
                for(int j = 0; j < list.get(i).size();j++){
                    System.out.println(list.get(i).get(j).getSyntaxString());
                }
            }
        }
    }
    public enum compositionBase {
        STATEMENT(0, "<Statement> -> <Declarative> | <Assignment>"),
        DECLARATIVE(1, "<Declarative> -> <Type> <id>"),
        TYPE(2),
        IDENTIFIER(3),
        ASSIGNMENT(4, "<Assignment> -> <Identifier> = <Expression>;"),
        EXPRESSION(5, "<Expression> -> <Expression> + <Term> | <Expression> - <Term> | <Term>"),
        TERM(6, "<Term> -> <Term> * <Factor> | <Term> / <Factor> | <Factor>"),
        FACTOR(7, "<Factor> -> ( <Expression> ) | <ID> | <num>"),
        NUMBER(8),
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
        NUMBER(8);

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


    public List<Line> createLines(){
        List<Line> lines = new ArrayList<>();
        Line line = new Line();

        for(Lexer.Token token: tokens){
            line.tokens.add(token);
            if(token.lexemeName.equals(";")){
                lines.add(line);
                line = new Line();
                continue;
            }

        }
        return lines;
    }

    public class currentParsing{
        public compositionBase currentSyntax;
        public Lexer.Token currentToken;
        public Lexer.Token nextToken;

        public currentParsing(compositionBase currentSyntax,Lexer.Token currentToken, Lexer.Token nextToken){
            this.currentSyntax=currentSyntax;
            this.currentToken=currentToken;
            this.nextToken=nextToken;
        }
    }
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

    public List<List<compositionBase>>parseLine(Line line){
        List<List<compositionBase>> analysis = new ArrayList<>();
        currentParsing parser = new currentParsing(compositionBase.STATEMENT,line.tokens.get(0),line.tokens.get(1));
        parser.currentSyntax = compositionBase.STATEMENT;
        for(int i = 0; i < line.tokens.size();i++){
            analysis.add(new ArrayList<>());
        }
        analysis.get(0).add(compositionBase.STATEMENT);
        for(int i = 0; i < line.tokens.size()-1;i++){
            List<compositionBase> currentCharacterAnalysis = analysis.get(i);
            parser.currentToken=line.tokens.get(i);
            parser.nextToken=line.tokens.get(i+1);
            outerwhile:
            {
                while (true) {
                    if (parser.currentToken.tokenName == Lexer.State.OPERATOR && parser.currentToken.lexemeName.equals("=")) {
                        parser.currentSyntax = compositionBase.EXPRESSION;
                        currentCharacterAnalysis.add(compositionBase.EXPRESSION);
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
                            break outerwhile;
                        case EXPRESSION:
                            if(parser.nextToken.lexemeName.equals("+")||parser.nextToken.lexemeName.equals("-")){
                                break outerwhile;
                            }
                            else{
                                currentCharacterAnalysis.add(compositionBase.TERM);
                                parser.currentSyntax = compositionBase.TERM;
                            }
                        case TERM:
                            if(parser.nextToken.lexemeName.equals("*")||parser.nextToken.lexemeName.equals("/")){
                                break outerwhile;
                            }
                            else{
                                currentCharacterAnalysis.add(compositionBase.FACTOR);
                                parser.currentSyntax = compositionBase.FACTOR;
                            }
                    }
                }
            }
        }
        return analysis;
    }
    public void printLine(Line line){
        for(Lexer.Token token: line.tokens){
            System.out.println(token.lexemeName + " ");
        }
        System.out.println();
    }



}
