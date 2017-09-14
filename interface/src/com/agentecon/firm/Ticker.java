package com.agentecon.firm;

import com.agentecon.goods.Good;

// Immutable
public class Ticker extends Good {
	
	private String type;
	
	public Ticker(String type, int number) {
		super(generateSymbol(type) + number);
		this.type = type;
	}

	protected static String generateSymbol(String type) {
		int index = type.indexOf('-');
		if (index > 3){
			return type.substring(0, 3) + type.substring(index, index + 4);
		} else {
			return type.substring(0, 3);
		}
	}
	
	public String getType(){
		return type;
	}
	
}
