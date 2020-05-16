package com.carlosjacob;

import java.util.Hashtable;
import java.util.Iterator;

public class SymbolTable {
	int current_memory_addr = 0;
	Hashtable<String, Symbol> symbols = new Hashtable<String, Symbol>();
	Hashtable<Integer, Integer> int_memory_mapper = new Hashtable<Integer, Integer>();
	Hashtable<Integer, Float> float_memory_mapper = new Hashtable<Integer, Float>();
	Hashtable<Integer, String> string_memory_mapper = new Hashtable<Integer, String>();
	Hashtable<Integer, Object> memoryMapper = new Hashtable<>();
	public SymbolTable(){

	}

	public void insert_symbol(Symbol symbol){
		if(!symbolInserted(symbol.getIdentifer())) {
			symbol.setAddress(current_memory_addr);
			symbols.put(symbol.getIdentifer(), symbol);
			current_memory_addr++;
		}
	}

	public void insertValue(int address, Object value){
		memoryMapper.put(address,value);
	}

	public Object findValue(int address){
		return memoryMapper.get(address);
	}

	public boolean symbolInserted(String symbol_name){
		Symbol symbol = symbols.get(symbol_name);
		return symbol!=null;
	}
	public Symbol find_symbol(String symbol_name){
		Symbol symbol = symbols.get(symbol_name);
		if(symbol != null){
			System.out.println("Symbol " + symbol_name + " was found!");
		}
		return symbol;
	}
	
	//Returns Object because we dont know what type the symbol is yet
	public Object find_value(Symbol symbol){
		switch(symbol.getType()){
			case INT:
				return int_memory_mapper.get(symbol.getAddress());
			case FLOAT:
				return float_memory_mapper.get(symbol.getAddress());
			case STRING:
				return string_memory_mapper.get(symbol.getAddress());
			default:
				return null;
		}
	}
	
	public void insert_value(String symbol_name, Object value_to_insert){
		Symbol sym = this.find_symbol(symbol_name);
		switch(sym.getType()){
		case INT:
			int_memory_mapper.put(sym.getAddress(), Integer.valueOf((String)value_to_insert));
		case FLOAT:
			float_memory_mapper.put(sym.getAddress(), Float.valueOf((String)value_to_insert));
		case STRING:
			string_memory_mapper.put(sym.getAddress(), (String)value_to_insert);
		}
	}
	
	public void printSymbolTable(){
		System.out.println("Identifier\t\tMemoryLocation\t\tType");
		symbols.forEach(
				(key, value) -> System.out.println(value.getIdentifer() + "\t\t\t\t" + value.getAddress() + "\t\t" + value.getType().toString() + "\t\t" + find_value(value))
		);
	}
}
