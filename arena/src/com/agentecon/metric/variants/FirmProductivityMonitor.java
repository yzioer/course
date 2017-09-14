// Created on Jun 1, 2015 by Luzius Meisser

package com.agentecon.metric.variants;

import com.agentecon.goods.Quantity;
import com.agentecon.metric.series.TimeSeries;
import com.agentecon.production.IProducer;
import com.agentecon.production.IProducerListener;

public abstract class FirmProductivityMonitor extends TimeSeries implements IProducerListener {
	
	public FirmProductivityMonitor(IProducer firm){
		super(firm.getName());
	}
	
	@Override
	public void reportResults(IProducer comp, double revenue, double cogs, double profits) {
	}
	
	@Override
	public void notifyProduced(IProducer comp, Quantity[] inputs, Quantity output) {
		super.set(getDay(), output.getAmount());
	}

	protected abstract int getDay();
	
}
