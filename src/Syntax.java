import java.util.List;

public class Syntax {
    private Lexer lex;
    private List<Lexer.Token> tokens;
    public Syntax(String fileName){
        lex = new Lexer();
        lex.feedMe(fileName);
        tokens = lex.createTokenList(fileName);
    }
}
