package com.carlosjacob;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class CodeGenerator {
    int address;
    SymbolTable symbols;
    Instructions instructions;
    List<String> instructionsList;
    List<List<List<Syntax.compositionBase>>> syntaxRules;
    List<Syntax.Line> lines;
    CodeGenerator(List<List<List<Syntax.compositionBase>>> syntaxRules, List<Syntax.Line> lines){
        symbols = new SymbolTable();
        instructions = new Instructions(symbols); //The SymbolTable in instructions will become a reference to
        // Symboltable in CodeGenerator. Changing one will change the other.
        instructionsList = instructions.getInstructList(); //Reference to instructionList held within the
        //instruction class, otherwise we'd be writing instructions.instruct.add() everywhere
        this.syntaxRules = syntaxRules;
        this.lines = lines;
        address = 5000;
    }

    public void generateCode(){
        SymbolType type = null;
        Symbol symbol = null;
        for(int i = 0; i < syntaxRules.size();i++){
            for(int j = 0; j < syntaxRules.get(i).size();j++){
                int size = syntaxRules.get(i).get(j).size();
                Syntax.compositionBase lastRule = syntaxRules.get(i).get(j).get(size-1);
                switch(lastRule){
                    case DECLARATIVE:
                        switch(lines.get(i).tokens.get(j).lexemeName.toLowerCase()){
                            case "int":
                                type = SymbolType.INT;
                                break;
                            case "string":
                                type = SymbolType.STRING;
                            case "float":
                                type = SymbolType.FLOAT;
                        }
                        j++;
                    case ASSIGNMENT:
                        symbol = new Symbol(lines.get(i).tokens.get(j).lexemeName, type);
                        break;


                }
                if(size==0 && lines.get(i).tokens.get(j).tokenName == Lexer.State.IDENTIFIER){
                    symbol = new Symbol(lines.get(i).tokens.get(j).lexemeName, type);
                    symbols.insertValue(symbol.getAddress(),type);
                }
            }
        }
    }

}
