package com.agentecon.consumer;

import com.agentecon.firm.IShareholder;
import com.agentecon.firm.IStockMarket;
import com.agentecon.firm.Portfolio;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Inventory;

public class Inheritance implements IShareholder {
	
	private Inventory inventory;
	private Portfolio portfolio;
	
	public Inheritance(Inventory inventory, Portfolio portfolio) {
		this.inventory = inventory;
		this.portfolio = portfolio;
	}

	public IStock getMoney() {
		return inventory.getMoney();
	}

	public Inventory getInventory() {
		return inventory;
	}

	public Portfolio getPortfolio() {
		return portfolio;
	}

	@Override
	public void managePortfolio(IStockMarket dsm) {
	}
	
	public Inheritance clone() {
		throw new RuntimeException("Not yet implemented");
	}

}
