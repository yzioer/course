// Created by Luzius on Apr 22, 2014

package com.agentecon.goods;

import com.agentecon.util.Numbers;

public class Stock implements IStock {
	
	private Good good;
	private double amount;
	private double fresh;
	
	public Stock(Good good){
		this(good, 0);
	}

	public Stock(Good good, double initial) {
		assert good != null;
		this.good = good;
		this.amount = initial;
	}
	
	@Override
	public void addFreshlyProduced(double quantity) {
		add(quantity);
		this.fresh += quantity;
	}
	
	public void deprecate() {
		if (amount > fresh){
			double current = amount - fresh;
			double pers = good.getPersistence();
			double kept = pers * current;
			double loss = current - kept;
			assert Math.abs(loss - (1-pers) * current) < Numbers.EPSILON;
			this.amount = kept + fresh;
			this.fresh = 0.0;
		}
	}
	
	@Override
	public IStock hide(double amount) {
		return new HiddenStock(this, amount);
	}
	
	@Override
	public IStock hideRelative(double fraction) {
		return new RelativeHiddenStock(this, fraction);
	}
	
	public IStock duplicate() {
		return new Stock(good, amount);
	}
	
	@Override
	public Good getGood(){
		return good;
	}
	
	@Override
	public double getAmount() {
		return amount;
	}
	
	@Override
	public double consume() {
		double amount = getAmount();
		remove(amount);
		return amount;
	}
	
	@Override
	public void remove(double quantity) {
		assert quantity >= 0.0;
		assert Math.abs(quantity - amount) >= -Numbers.EPSILON;
		if (quantity > amount){
			quantity = amount; // prevent negative values due to rounding errors
		}
		assert this.amount >= quantity;
		this.amount -= quantity;
	}

	@Override
	public void add(double quantity) {
		assert quantity >= 0.0;
		assert this.amount >= -quantity;
		this.amount += quantity;
	}
	
	@Override
	public void transfer(IStock source, double amount) {
		assert source.getGood().equals(getGood());
		assert this != source;
		assert source.getAmount() - amount >= -Numbers.EPSILON;
		assert this.getAmount() + amount >= -Numbers.EPSILON;
		this.amount = Math.max(0.0, this.amount + amount);
		if (amount > 0){
			source.remove(amount);
		} else {
			source.add(-amount);
		}
		assert source.getAmount() < 10000000;
		assert getAmount() < 10000000;
	}
	
	@Override
	public void absorb(IStock s) {
		assert s.getGood() == getGood();
		assert this != s;
		this.amount += s.consume();
	}

	@Override
	public final boolean isEmpty() {
		return !hasSome();
	}
	
	@Override
	public String toString(){
		return Numbers.toString(amount) + " " + good.toString();
	}

	@Override
	public Quantity getQuantity() {
		return new Quantity(good, amount);
	}

	@Override
	public boolean hasSome() {
		assert amount >= 0.0;
		return amount > Numbers.EPSILON;
	}

}
