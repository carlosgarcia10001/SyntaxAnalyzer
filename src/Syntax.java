import java.util.ArrayList;
import java.util.List;

public class Syntax {
    private Lexer lex;
    private List<Lexer.Token> tokens;
    private List<Line> lines;
    public Syntax(String fileName){
        lex = new Lexer();
        lex.feedMe(fileName);
        tokens = lex.createTokenList(fileName);
        lines = createLines();
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

    public enum syntaxAnalysis {
        STATEMENT(new syntaxAnalysis[][]{{syntaxAnalysis.DECLARATIVE},{syntaxAnalysis.ASSIGNMENT}}),
        DECLARATIVE(new syntaxAnalysis[][]{{syntaxAnalysis.TYPE,syntaxAnalysis.IDENTIFIER}}),
        TYPE(new syntaxAnalysis[][]{{syntaxAnalysis.TYPE}}),
        IDENTIFIER(new syntaxAnalysis[][]{{syntaxAnalysis.IDENTIFIER}}),
        ASSIGNMENT(new syntaxAnalysis[][]{{syntaxAnalysis.IDENTIFIER,syntaxAnalysis.EXPRESSION}}),
        EXPRESSION(new syntaxAnalysis[][]{{syntaxAnalysis.EXPRESSION,syntaxAnalysis.TERM},{syntaxAnalysis.TERM}}),
        TERM(new syntaxAnalysis[][]{{syntaxAnalysis.TERM,syntaxAnalysis.FACTOR},{syntaxAnalysis.FACTOR}}),
        FACTOR(new syntaxAnalysis[][]{{syntaxAnalysis.EXPRESSION},{syntaxAnalysis.IDENTIFIER},{syntaxAnalysis.NUMBER}}),
        NUMBER(new syntaxAnalysis[][]{{syntaxAnalysis.NUMBER}});

        private syntaxAnalysis [] [] composition;

        syntaxAnalysis(syntaxAnalysis [] [] composition){
            this.composition= composition;
        }

        public syntaxAnalysis [] [] getComposition(){
            return composition;
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
            if(token.lexemeName.equals(";")){
                lines.add(line);
                printLine(line);
                line = new Line();
                continue;
            }
            line.tokens.add(token);
        }
        return lines;
    }

    public void printLine(Line line){
        for(Lexer.Token token: line.tokens){
            System.out.print(token.lexemeName + " ");
        }
        System.out.println();
    }
}
