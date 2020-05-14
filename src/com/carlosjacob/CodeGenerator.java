package com.carlosjacob;

import java.util.ArrayList;
import java.util.Deque;

public class CodeGenerator {
    SymbolTable symbols;
    Instructions instructions;
    CodeGenerator(){
        symbols = new SymbolTable();
        instructions = new Instructions(symbols); //The SymbolTable in instructions will become a reference to
        // Symboltable in CodeGenerator. Changing one will change the other.
    }

}
