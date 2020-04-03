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
