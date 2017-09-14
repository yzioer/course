// Created on Jun 1, 2015 by Luzius Meisser

package com.agentecon.util;

public class MovingAverage implements Cloneable, IAverage {

	private double memory;
	private double mean, var;

	public MovingAverage() {
		this(0.95);
	}

	public MovingAverage(double memory) {
		this(memory, 0.0);
	}
	
	public MovingAverage(double memory, double start) {
		this.memory = memory;
		this.mean = start;
		this.var = 1.0;
	}

	public double getAverage() {
		return mean;
	}

	public double getVariance() {
		assert var >= 0;
		return var;
	}

	public void add(double point) {
		assert !Double.isNaN(point);
		double oldMean = this.mean;
		this.mean = memory * oldMean + (1 - memory) * point;
		double adjustment = this.mean - oldMean;
		double delta = point - this.mean;
		this.var = memory * (this.var + adjustment * adjustment) + (1 - memory) * delta * delta;
		assert !Double.isNaN(mean);
		assert !Double.isNaN(var);
	}

	public String normalize(double f) {
		return getAverage() / f + " (" + Math.sqrt(getVariance()) / f + ")";
	}

	@Override
	public MovingAverage clone() {
		try {
			return (MovingAverage) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new java.lang.RuntimeException(e);
		}
	}

	public String toString() {
		return getAverage() + " (" + Math.sqrt(getVariance()) + ")";
	}

}
