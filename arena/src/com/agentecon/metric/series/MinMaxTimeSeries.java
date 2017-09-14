// Created on Jun 24, 2015 by Luzius Meisser

package com.agentecon.metric.series;

import com.agentecon.util.Average;
import com.agentecon.util.Numbers;

public class MinMaxTimeSeries extends TimeSeries {
	
	private Line minmax;
	
	protected MinMaxTimeSeries(){
	}

	public MinMaxTimeSeries(String name) {
		super(name);
		this.minmax = new CompactLine();
	}
	
	@Override
	public void set(int pos, double average) {
		this.set(pos, average, average, average);
	}
	
	public void set(int pos, double average, double min, double max) {
		super.set(pos, average);
		assert min <= average + Numbers.EPSILON;
		assert max >= average - Numbers.EPSILON;
		this.minmax.add(new MinMaxPoint(pos, (float) min, (float) max));
	}
	
//	public TimeSeriesData getRawData() {
//		return new TimeSeriesData(getName(), line.getPoints(), minmax.getPoints());
//	}

	public void set(int day, Average value) {
		set(day, value.getAverage(), value.getMin(), value.getMax());
	}

}
