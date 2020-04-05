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
        parseLine(lines.get(0));
    }

    public enum syntaxAnalysis {
        STATEMENT(0),
        DECLARATIVE(1),
        TYPE(2),
        IDENTIFIER(3),
        ASSIGNMENT(4), //=
        EXPRESSION(5),//+,-
        TERM(6),//*,/
        FACTOR(7),
        NUMBER(8),
        ERROR(-1);
        private int id;
        syntaxAnalysis(int id){

            this.id = id;
        }

        public int getId(){
            return id;
        }

        public static syntaxAnalysis returnEnumFromId(int index){
            switch(index){
                case 0:
                    return syntaxAnalysis.STATEMENT;
                case 1:
                    return syntaxAnalysis.DECLARATIVE;
                case 2:
                    return syntaxAnalysis.TYPE;
                case 3:
                    return syntaxAnalysis.IDENTIFIER;
                case 4:
                    return syntaxAnalysis.ASSIGNMENT;
                case 5:
                    return syntaxAnalysis.EXPRESSION;
                case 6:
                    return syntaxAnalysis.TERM;
                case 7:
                    return syntaxAnalysis.FACTOR;
                case 8:
                    return syntaxAnalysis.NUMBER;
            }
            return syntaxAnalysis.ERROR;
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
                printLine(line);
                line = new Line();
                continue;
            }

        }
        return lines;
    }

    public class currentParsing{
        public syntaxAnalysis currentSyntax;
        public Lexer.Token currentToken;
        public Lexer.Token nextToken;

        public currentParsing(syntaxAnalysis currentSyntax,Lexer.Token currentToken, Lexer.Token nextToken){
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
    public List<List<syntaxAnalysis>>parseLine(Line line){
        List<List<syntaxAnalysis>> analysis = new ArrayList<>();
        currentParsing parser = new currentParsing(syntaxAnalysis.STATEMENT,line.tokens.get(0),line.tokens.get(1));
        parser.currentSyntax = syntaxAnalysis.STATEMENT;
        for(int i = 0; i < line.tokens.size();i++){
            analysis.add(new ArrayList<>());
        }
        analysis.get(0).add(syntaxAnalysis.STATEMENT);
        for(int i = 0; i < line.tokens.size()-1;i++){
            List<syntaxAnalysis> currentCharacterAnalysis = analysis.get(i);
            parser.currentToken=line.tokens.get(i);
            parser.nextToken=line.tokens.get(i+1);
            outerwhile:
            {
                while (true) {
                    if (parser.currentToken.tokenName == Lexer.State.OPERATOR && parser.currentToken.lexemeName.equals("=")) {
                        parser.currentSyntax = syntaxAnalysis.EXPRESSION;
                        currentCharacterAnalysis.add(syntaxAnalysis.EXPRESSION);
                        break;
                    }

                    switch (parser.currentSyntax) {
                        case STATEMENT:
                            if (parser.currentToken.tokenName == Lexer.State.IDENTIFIER) {
                                currentCharacterAnalysis.add(syntaxAnalysis.ASSIGNMENT);
                                parser.currentSyntax = syntaxAnalysis.ASSIGNMENT;
                            } else if (parser.currentToken.tokenName == Lexer.State.KEYWORD) {
                                currentCharacterAnalysis.add(syntaxAnalysis.DECLARATIVE);
                                parser.currentSyntax = syntaxAnalysis.DECLARATIVE;
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
                                currentCharacterAnalysis.add(syntaxAnalysis.TERM);
                                parser.currentSyntax = syntaxAnalysis.TERM;
                            }
                        case TERM:
                            if(parser.nextToken.lexemeName.equals("*")||parser.nextToken.lexemeName.equals("/")){
                                break outerwhile;
                            }
                            else{
                                currentCharacterAnalysis.add(syntaxAnalysis.FACTOR);
                                parser.currentSyntax = syntaxAnalysis.FACTOR;
                            }
                    }
                }
            }
        }
        for(int i = 0; i < analysis.size();i++){
            for(int j = 0; j < analysis.get(i).size();j++){
                System.out.print(analysis.get(i).get(j) + " ");
            }
            System.out.println();
        }
        return analysis;
    }
    public void printLine(Line line){
        for(Lexer.Token token: line.tokens){
            System.out.print(token.lexemeName + " ");
        }
        System.out.println();
    }
}
