package com.carlosjacob;

import java.util.ArrayList;
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
        generateCode();
    }

    public void generateCode(){
        SymbolType type = null;
        List<String>operators = new ArrayList<>();
        List<Integer>numbers = new ArrayList<>();
        boolean assignment;
        for(int i = 0; i < syntaxRules.size();i++){
            assignment = false;
            for(int j = 0; j < syntaxRules.get(i).size();j++){
                Token currentToken = lines.get(i).tokens.get(j);
                Symbol currentSymbol = null;
                int size = syntaxRules.get(i).get(j).size();
                if(size-1>0){
                Syntax.compositionBase lastRule = syntaxRules.get(i).get(j).get(size-1);
                switch(lastRule) {
                    case DECLARATIVE:
                        switch (currentToken.lexemeName.toLowerCase()) {
                            case "int":
                                type = SymbolType.INT;
                                break;
                            case "string":
                                type = SymbolType.STRING;
                            case "float":
                                type = SymbolType.FLOAT;
                        }
                    case ASSIGNMENT:
                        currentSymbol = new Symbol(currentToken.lexemeName, type);
                        assignment = true;
                        break;
                    case PRIMARY:
                        if (assignment) {
                            if (currentToken.tokenName == Lexer.State.NUMBER) {
                                numbers.add(Integer.parseInt(currentToken.lexemeName));
                            } else {
                                int value = (int)symbols.findValue(currentSymbol.getAddress());
                                numbers.add(value);
                            }
                        }
                        break;
                    default:
                        break;
                }
                }
                if(size==0 && currentToken.tokenName == Lexer.State.IDENTIFIER){
                    Symbol symbol;
                    symbol = new Symbol(currentToken.lexemeName, type);
                    symbols.insert_symbol(symbol);
                    numbers.add(Integer.getInteger(currentToken.lexemeName));
                }
                if(currentToken.tokenName == Lexer.State.OPERATOR && !(currentToken.lexemeName.equals("="))){
                    operators.add(currentToken.lexemeName);
                }
                if(currentToken.tokenName== Lexer.State.END_STATEMENT && assignment){
                        while (findMulOrDivIndex(operators) != null || findAddOrSub(operators) != null) {
                            Integer index = findMulOrDivIndex(operators);
                            String operator = operators.get(index);
                            if (index != null) {
                                instructions.performInstruction(InstructionEnum.PUSHI, numbers.get(index));
                                instructions.performInstruction(InstructionEnum.PUSHI, numbers.get(index + 1));
                                switch (operator) {
                                    case "*":
                                        instructions.performInstruction(InstructionEnum.MUL);
                                        numbers.set(index, numbers.get(index) * numbers.get(index + 1));
                                        break;
                                    case "/":
                                        instructions.performInstruction(InstructionEnum.DIV);
                                        numbers.set(index, numbers.get(index + 1) / numbers.get(index));
                                        break;
                                }
                            } else {
                                index = findAddOrSub(operators);
                                if (index != null) {
                                    instructions.performInstruction(InstructionEnum.PUSHI, numbers.get(index));
                                    instructions.performInstruction(InstructionEnum.PUSHI, numbers.get(index + 1));
                                    switch (operator) {
                                        case "+":
                                            instructions.performInstruction(InstructionEnum.ADD);
                                            numbers.set(index, numbers.get(index) + numbers.get(index + 1));
                                            break;
                                        case "-":
                                            instructions.performInstruction(InstructionEnum.SUB);
                                            numbers.set(index, numbers.get(index + 1) - numbers.get(index));
                                            break;
                                    }
                                }
                            }
                            symbols.insertValue(currentSymbol.getAddress(),numbers.get(index));
                            numbers.remove(index + 1);
                            operators.remove(index);
                            instructions.performInstruction(InstructionEnum.POPM, currentSymbol.getAddress());
                        }
                    }
                if(numbers.size()==1){
                    instructions.performInstruction(InstructionEnum.PUSHI,numbers.get(0));
                    instructions.performInstruction(InstructionEnum.POPM);
                    numbers.remove(0);
                }
            }
        }
    }

    public Integer findMulOrDivIndex(List<String>operators){
        for(int i = 0; i < operators.size();i++){
            String operator = operators.get(i);
            if(operator.equals("*") || operator.equals("/") || operator.equals("%")){
                return i;
            }
        }
        return null;
    }

    public Integer findAddOrSub(List<String>operators){
        for(int i = 0; i < operators.size();i++){
            String operator = operators.get(i);
            if(operator.equals("+") || operator.equals("-")){
                return i;
            }
        }
        return null;
    }
}
