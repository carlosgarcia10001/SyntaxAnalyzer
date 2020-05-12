package com.carlosjacob;

import java.util.Hashtable;

public class SymbolTable {
	int current_memory_addr = 0;
	Hashtable<String, Symbol> symbols = new Hashtable<String, Symbol>();

	Hashtable<Integer, Integer> int_memory_mapper = new Hashtable<Integer, Integer>();
	Hashtable<Integer, Float> float_memory_mapper = new Hashtable<Integer, Float>();
	Hashtable<Integer, String> string_memory_mapper = new Hashtable<Integer, String>();
	
	public void insert_symbol(Symbol symbol){
		symbol.symbol_address = current_memory_addr;
		symbols.put(symbol.symbol_name, symbol);
		current_memory_addr++;
	}
	
	public Symbol find_symbol(String symbol_name){
		Symbol symbol = symbols.get(symbol_name);
		if(symbol != null){
			System.out.println("Symbol was found!");
		}
		return symbol;
	}
	
	//Returns Object because we dont know what type the symbol is yet
	public Object find_value(Symbol symbol){
		switch(symbol.symbol_type){
			case "int":
				return int_memory_mapper.get(symbol.symbol_address);
			case "float":
				return float_memory_mapper.get(symbol.symbol_address);
			case "string":
				return string_memory_mapper.get(symbol.symbol_address);
			default:
				return null;
		}
	}
	
	public void insert_value(String symbol_name, Object value_to_insert){
		Symbol sym = this.find_symbol(symbol_name);
		switch(sym.symbol_type){
		case "int":
			int_memory_mapper.put(sym.symbol_address, Integer.valueOf((String)value_to_insert));
		case "float":
			float_memory_mapper.put(sym.symbol_address, Float.valueOf((String)value_to_insert));
		case "string":
			string_memory_mapper.put(sym.symbol_address, (String)value_to_insert);
		}
	}
	
	public void printSymbolTable(){
		System.out.println("Identifier\t\tMemoryLocation\t\tType");
		symbols.forEach(
				(key, value) -> System.out.println(value.symbol_name + "\t\t\t\t" + value.symbol_address + "\t\t" + value.symbol_type + "\t\t" + find_value(value))
		);
	}
}
