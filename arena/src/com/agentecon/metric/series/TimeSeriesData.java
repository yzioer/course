package com.agentecon.metric.series;

import java.util.Collection;

public class TimeSeriesData {

	private String name;
	private float[] xs;
	private float[] ys;

	public TimeSeriesData(String name, Collection<Point> points) {
		this.name = name;
		this.xs = new float[points.size()];
		this.ys = new float[points.size()];
		int pos = 0;
		for (Point p: points){
			xs[pos] = p.x;
			ys[pos] = p.y;
			pos++;
		}
	}

	public String getName() {
		return name;
	}

	public float getLastY() {
		return ys[ys.length - 1];
	}

}
