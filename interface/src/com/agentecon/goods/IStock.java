package com.agentecon.goods;

public interface IStock {
	
	/**
	 * @return new Quantity(getGood(), getAmount())
	 */
	public Quantity getQuantity();

	/**
	 * The type of the good.
	 */
	public Good getGood();

	/**
	 * The amount that is available.
	 * For the HiddenStock class, part of the stock can be hidden.
	 */
	public double getAmount();

	/**
	 * Consumes the available stock.
	 */
	public double consume();

	public void remove(double quantity);

	public void add(double quantity);
	
	/**
	 * Will be protected from overnight depreciation
	 */
	public void addFreshlyProduced(double quantity);

	public void transfer(IStock source, double amount);

	/**
	 * Take everything that is available (i.e. not hidden) from the provided source
	 * and add it to 'this' stock.
	 */
	public void absorb(IStock source);

	/**
	 * Returns a reference to this stock with an absolute part of the amount hidden
	 */
	public IStock hide(double amount);

	/**
	 * Returns a reference to this stock with a relative part of the amount hidden.
	 * According fractions of added reserves are also hidden.
	 */
	public IStock hideRelative(double fraction);
	
	/**
	 * Positive opposite of "isEmpty". Should be preferred as negative formulations tend to confuse more.
	 */
	public boolean hasSome();
	
	/**
	 * The same as !hasSome()
	 */
	public boolean isEmpty();

	public void deprecate();
	
	public IStock duplicate();

}