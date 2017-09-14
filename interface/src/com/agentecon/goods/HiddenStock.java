package com.agentecon.goods;

import com.agentecon.util.Numbers;

public class HiddenStock implements IStock {
	
	protected IStock wrapped;
	protected double hidden;

	public HiddenStock(IStock wrapped, double hidden) {
		this.wrapped = wrapped;
		this.hidden = hidden;
		assert wrapped != null;
	}
	
	protected void hideMore(double more){
		this.hidden += more;
	}

	@Override
	public Good getGood() {
		return wrapped.getGood();
	}

	@Override
	public double getAmount() {
		return Math.max(0, wrapped.getAmount() - hidden);
	}

	@Override
	public final double consume() {
		double amount = getAmount();
		remove(amount);
		return amount;
	}

	@Override
	public void remove(double quantity) {
		assert quantity <= getAmount();
		assert quantity >= 0;
		wrapped.remove(quantity);
	}
	
	@Override
	public void addFreshlyProduced(double quantity) {
		wrapped.addFreshlyProduced(quantity);
	}

	@Override
	public void add(double quantity) {
		wrapped.add(quantity);
	}

	@Override
	public void transfer(IStock source, double amount) {
		assert !Numbers.isBigger(-amount, getAmount());
		wrapped.transfer(source, amount);
	}

	@Override
	public void absorb(IStock s) {
		wrapped.absorb(s);
	}

	@Override
	public boolean isEmpty() {
		return !hasSome();
	}
	
	@Override
	public boolean hasSome() {
		return getAmount() > Numbers.EPSILON;
	}

	@Override
	public void deprecate() {
		wrapped.deprecate();
	}

	@Override
	public IStock duplicate() {
		return new HiddenStock(wrapped.duplicate(), hidden);
	}
	
	@Override
	public String toString(){
		return Numbers.toString(getAmount()) + " hiding " + wrapped;
	}

	@Override
	public IStock hide(double amount) {
		return new HiddenStock(this, amount);
	}

	@Override
	public IStock hideRelative(double fraction) {
		return new RelativeHiddenStock(this, fraction);
	}

	@Override
	public Quantity getQuantity() {
		return new Quantity(getGood(), getAmount());
	}

}
