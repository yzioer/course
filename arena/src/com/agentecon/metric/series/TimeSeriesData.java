package com.agentecon.metric.series;

import java.util.ArrayList;

public class TimeSeriesData {

	private String name;
	private float[][] values;
	private float[][] minmax;

	public TimeSeriesData(String name, ArrayList<Point> points) {
		this.name = name;
		this.values = new float[points.size()][2];
		int pos = 0;
		for (Point p: points){
			values[pos][0] = p.x;
			values[pos][1] = p.y;
			pos++;
		}
	}

	public TimeSeriesData(String name, ArrayList<Point> points, ArrayList<MinMaxPoint> minmax) {
		this(name, points);
		this.minmax = new float[points.size()][3];
		int pos = 0;
		for (MinMaxPoint p: minmax){
			values[pos][0] = p.x;
			values[pos][1] = p.y;
			values[pos][2] = p.max;
			pos++;
		}
	}
	
	public String getName() {
		return name;
	}

	public float[][] getValues() {
		return values;
	}

	public float[][] getMinMax() {
		return minmax;
	}

	public float getLastY() {
		return values[values.length - 1][1];
	}

}
