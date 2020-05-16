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
    String symbolForValue = "";

    CodeGenerator(List<List<List<Syntax.compositionBase>>> syntaxRules, List<Syntax.Line> lines){
        symbols = new SymbolTable();
        instructions = new Instructions(symbols); //The SymbolTable in instructions will become a reference to
        // Symboltable in CodeGenerator. Changing one will change the other.
        instructionsList = instructions.getInstructList(); //Reference to instructionList held within the
        //instruction class, otherwise we'd be writing instructions.instruct.add() everywhere
        this.syntaxRules = syntaxRules;
        this.lines = lines;
        generateCode();
		symbols.printSymbolTable();
    }

    public void generateCode(){
        SymbolType type = null;
        List<String> operators = new ArrayList<>();
        List<Integer> indexedNumbers = new ArrayList<>();
        boolean assignment;
        
        for(int i = 0; i < syntaxRules.size(); i++){
            assignment = false;
            for(int j = 0; j < syntaxRules.get(i).size(); j++){
                Token currentToken = lines.get(i).tokens.get(j);
                Symbol currentSymbol = null;
                int amountOfRules = syntaxRules.get(i).get(j).size();
                
                if(amountOfRules > 0){
                	
                	//For some reason, some of the rules have a shitload of nulls in them, not sure right now and I don't care right now!
                	while(syntaxRules.get(i).get(j).get(amountOfRules-1) == null){
                		amountOfRules--;
                	}
	                Syntax.compositionBase lastRule = syntaxRules.get(i).get(j).get(amountOfRules-1);
	                
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
	                        break;
	                    case ASSIGNMENT:
	                    	if(symbols.find_symbol(currentToken.lexemeName) == null){
	                    		currentSymbol = new Symbol(currentToken.lexemeName, type);
	                    		symbols.insert_symbol(currentSymbol);
//	                        currentSymbol = new Symbol(currentToken.lexemeName, type);
	                    		assignment = true;
	                    		symbolForValue = currentToken.lexemeName;
	                    	}
	                        break;
	                    
	                    //We need a way to identify what identifier is using this primary, if we don't the else clause is unreachable
	                    case PRIMARY:
	                    	currentSymbol = symbols.find_symbol(symbolForValue);
	                        if (assignment) {
	                            if (currentToken.tokenName == Lexer.State.NUMBER) {
	                            	symbols.insert_value(currentSymbol.getIdentifer(), currentToken.lexemeName);
	                                indexedNumbers.add(Integer.parseInt(currentToken.lexemeName));
	                            } else {
	                            	if(currentSymbol != null){
	                            		symbols.insert_value(currentSymbol.getIdentifer(), 1);
	                            		int value = (int)symbols.findValue(currentSymbol.getAddress());	                            		
	                            		indexedNumbers.add(1);
	                            	}
	                            }
	                        }
	                        break;
	                    default:
	                        break;
	                }
                }
                
                if(amountOfRules == 0 && currentToken.tokenName == Lexer.State.IDENTIFIER){
                    Symbol symbol = new Symbol(currentToken.lexemeName, type);
                    symbols.insert_symbol(symbol);
                    indexedNumbers.add(Integer.getInteger(currentToken.lexemeName));
                }
                
                if(currentToken.tokenName == Lexer.State.OPERATOR && !(currentToken.lexemeName.equals("="))){
                    operators.add(currentToken.lexemeName);
                }
                
                if(currentToken.tokenName == Lexer.State.END_STATEMENT && assignment){
                        while (findMulOrDivIndex(operators) != null || findAddOrSub(operators) != null) {
                        	
                            Integer index = findMulOrDivIndex(operators);
                            String operator = operators.get(index);
                            
                            if (index != null) {
                                instructions.performInstruction(InstructionEnum.PUSHI, indexedNumbers.get(index));
                                instructions.performInstruction(InstructionEnum.PUSHI, indexedNumbers.get(index + 1));
                                
                                switch (operator) {
                                    case "*":
                                        instructions.performInstruction(InstructionEnum.MUL);
                                        indexedNumbers.set(index, indexedNumbers.get(index) * indexedNumbers.get(index + 1));
                                        break;
                                    case "/":
                                        instructions.performInstruction(InstructionEnum.DIV);
                                        indexedNumbers.set(index, indexedNumbers.get(index + 1) / indexedNumbers.get(index));
                                        break;
                                }
                            } else {
                                index = findAddOrSub(operators);
                                if (index != null) {
                                    instructions.performInstruction(InstructionEnum.PUSHI, indexedNumbers.get(index));
                                    instructions.performInstruction(InstructionEnum.PUSHI, indexedNumbers.get(index + 1));
                                    
                                    switch (operator) {
                                        case "+":
                                            instructions.performInstruction(InstructionEnum.ADD);
                                            indexedNumbers.set(index, indexedNumbers.get(index) + indexedNumbers.get(index + 1));
                                            break;
                                        case "-":
                                            instructions.performInstruction(InstructionEnum.SUB);
                                            indexedNumbers.set(index, indexedNumbers.get(index + 1) - indexedNumbers.get(index));
                                            break;
                                    }
                                }
                            }
                            symbols.insertValue(currentSymbol.getAddress(),indexedNumbers.get(index));
                            indexedNumbers.remove(index + 1);
                            operators.remove(index);
                            instructions.performInstruction(InstructionEnum.POPM, currentSymbol.getAddress());
                        }
                    }
                if(indexedNumbers.size() == 1){
                    instructions.performInstruction(InstructionEnum.PUSHI, indexedNumbers.get(0));
                    instructions.performInstruction(InstructionEnum.POPM);
                    indexedNumbers.remove(0);
                }
            }
        }
    }

    public Integer findMulOrDivIndex(List<String> operators){
        for(int i = 0; i < operators.size();i++){
            String operator = operators.get(i);
            if(operator.equals("*") || operator.equals("/") || operator.equals("%")){
                return i;
            }
        }
        return null;
    }

    public Integer findAddOrSub(List<String> operators){
        for(int i = 0; i < operators.size();i++){
            String operator = operators.get(i);
            if(operator.equals("+") || operator.equals("-")){
                return i;
            }
        }
        return null;
    }
}
