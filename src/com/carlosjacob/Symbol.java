package com.carlosjacob;

public class Symbol {
	private String identifer;
	private SymbolType type;
	private int address;
	public Symbol(String identifer, SymbolType type){
		this.identifer = identifer;
		this.type = type;
	}

	public String getIdentifer(){
		return identifer;
	}

	public int getAddress(){
		return address;
	}

	public void setAddress(int address){
		this.address = address;
	}

	public SymbolType getType(){
		return type;
	}

	public void setType(SymbolType type){
		this.type = type;
	}
}
