package com.agentecon.firm;

import java.util.Collection;
import java.util.HashMap;

import com.agentecon.goods.IStock;
import com.agentecon.market.IMarketStatistics;
import com.agentecon.production.PriceUnknownException;

public class Portfolio implements Cloneable {

	protected IStock wallet;
	private double dividends;
	protected HashMap<Ticker, Position> inv;

	public Portfolio(IStock money) {
		this.wallet = money;
		this.inv = new HashMap<>();
		this.dividends = 0.0;
	}

	public void absorb(Portfolio other) {
		wallet.absorb(other.wallet);
		for (Position p : other.inv.values()) {
			Position existing = inv.get(p.getTicker());
			if (existing == null) {
				inv.put(p.getTicker(), p);
			} else {
				p.dispose(existing);
			}
		}
	}

	public void absorbPositions(double ratio, Portfolio other) {
		for (Position p : other.inv.values()) {
			Position myPosition = inv.get(p.getTicker());
			if (myPosition == null) {
				myPosition = p.createNewPosition();
				inv.put(p.getTicker(), myPosition);
			}
			myPosition.transfer(p, p.getAmount() * ratio);
		}
	}
	
	public void addPosition(Position pos) {
		if (pos != null) {
			Position prev = inv.put(pos.getTicker(), pos);
			if (prev != null && prev != pos) {
				prev.dispose(pos);
			}
		}
	}

	public Collection<Position> getPositions() {
		return inv.values();
	}

	public Position getPosition(Ticker ticker) {
		return inv.get(ticker);
	}

	public void disposePosition(Ticker t) {
		Position p = inv.remove(t);
		if (p != null) {
			p.dispose();
		}
	}

	public void dispose() {
		this.inv.clear();
	}

	public boolean hasPositions() {
		return inv.size() > 0;
	}

	public void collectDividends() {
		double money = wallet.getAmount();
		for (Position p : inv.values()) {
			p.collectDividend(wallet);
		}
		this.dividends = wallet.getAmount() - money;
	}

	public double getLatestDividendIncome() {
		return dividends;
	}

	public double getCash() {
		return wallet.getAmount();
	}

	public double calculateValue(IMarketStatistics stats) {
		double value = 0.0;
		for (IStock stock : inv.values()) {
			try {
				value += stats.getPriceBelief(stock.getQuantity());
			} catch (PriceUnknownException e) {
			}
		}
		return value;
	}

	public Portfolio clone(IStock wallet) {
		try {
			Portfolio klon = (Portfolio) super.clone();
			klon.wallet = wallet;
			// TODO: duplicate positions
			return klon;
		} catch (CloneNotSupportedException e) {
			throw new java.lang.RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		return wallet + ", " + inv.values();
	}

}
