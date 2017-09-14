package com.agentecon.metric.series;

import java.util.ArrayList;
import java.util.Collections;

public class Line {

	protected ArrayList<Point> points = new ArrayList<>();

	public Line() {
		super();
	}

	public Line(Line line) {
		this.points.addAll(line.getPoints());
	}

	public void add(Point newpoint) {
		add(newpoint, 1);
	}

	public void add(Point newpoint, int agg) {
		points.add(newpoint);
	}

	public float[][] getData() {
		float[][] arr = new float[points.size()][];
		for (int i = 0; i < arr.length; i++) {
			Point p = points.get(i);
			arr[i] = p.getData();
		}
		return arr;
	}

	public Point getFirst() {
		return points.get(0);
	}

	public Point getLast() {
		return points.get(points.size() - 1);
	}

	public ArrayList<Point> getPoints() {
		return points;
	}

	public float getLatest() {
		return points.isEmpty() ? 0.0f : points.get(points.size() - 1).y;
	}

	public boolean has(int pos) {
		return Collections.binarySearch(points, new Point(pos)) >= 0;
	}

	public float get(int pos) {
		int index = Collections.binarySearch(points, new Point(pos));
		if (index >= 0) {
			return points.get(index).y;
		} else if (index == -1) {
			return 0.0f; // start with 0
		} else {
			index = -index - 1;
			if (index == points.size()) {
				return 0.0f;
			} else {
				return points.get(index - 1).y; // previous value
			}
		}
	}

	public int getStart() {
		return points.get(0).x;
	}

	public int getEnd() {
		return points.get(points.size() - 1).x;
	}

	@Override
	public String toString() {
		return points.toString();
	}

}