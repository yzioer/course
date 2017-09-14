package com.agentecon.finance;

import java.util.Iterator;
import java.util.LinkedList;

import com.agentecon.agent.IAgent;
import com.agentecon.firm.IRegister;
import com.agentecon.firm.IStockMarket;
import com.agentecon.firm.Position;
import com.agentecon.firm.Ticker;
import com.agentecon.goods.IStock;
import com.agentecon.market.Bid;
import com.agentecon.util.MovingAverage;
import com.agentecon.util.Numbers;

public class ShareRegister implements IRegister {
	
	private Ticker ticker;
	private Position rootPosition;
	private MovingAverage dividend;
	private LinkedList<Position> all;

	public ShareRegister(Ticker ticker, IStock wallet) {
		this.ticker = ticker;
		this.all = new LinkedList<>();
		this.dividend = new MovingAverage(0.8);
		this.rootPosition = new Position(this, ticker, wallet.getGood(), SHARES_PER_COMPANY);
		this.all.add(rootPosition);
	}
	
	public void claimCompanyShares(Position owner){
		owner.absorb(rootPosition);
	}
	
	public void raiseCapital(IStockMarket dsm, IAgent owner, IStock wallet) {
		if (!rootPosition.isEmpty()){
			collectRootDividend(wallet);
			Bid bid = dsm.getBid(getTicker());
			if (bid != null){
				bid.accept(owner, wallet, rootPosition, rootPosition.getQuantity());
			}
		}
	}
	
	public void collectRootDividend(IStock wallet){
		rootPosition.collectDividend(wallet);
	}
	
	public void payDividend(IStock sourceWallet, double totalDividends) {
		dividend.add(totalDividends);

		if (!Numbers.equals(getTotalShares(), SHARES_PER_COMPANY)) {
			double diff = getTotalShares() - SHARES_PER_COMPANY;
			if (diff > 0) {
				rootPosition.add(diff);
			}
		}

		Iterator<Position> iter = all.iterator();
		while (iter.hasNext()) {
			Position pos = iter.next();
			if (pos.isDisposed()) {
				iter.remove();
			} else {
				pos.receiveDividend(sourceWallet, totalDividends / SHARES_PER_COMPANY);
			}
		}
	}
	
	@Override
	public double getAverageDividend() {
		return dividend.getAverage();
	}
	
	public Position createPosition(){
		Position pos = new Position(this, getTicker(), rootPosition.getCurrency(), 0.0);
		all.add(pos);
		return pos;
	}
	
	public void inherit(Position pos){
		pos.dispose(rootPosition);
	}

	public Ticker getTicker() {
		return ticker;
	}

	private double getTotalShares() {
		double tot = 0.0;
		for (Position p : all) {
			tot += p.getAmount();
		}
		return tot;
	}
	
	public int getShareholderCount(){
		return all.size();
	}
	
	public double getFreeFloatShares() {
		return SHARES_PER_COMPANY - rootPosition.getAmount();
	}

	@Override
	public String toString() {
		return ticker + " has " + all.size() + " shareholders and pays " + dividend;
	}

}
