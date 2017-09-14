// Created by Luzius on Apr 28, 2014

package com.agentecon.util;

import java.text.DecimalFormat;

public class Numbers {

	private static final DecimalFormat FORMATTER = new DecimalFormat("#.####");
	private static final DecimalFormat SHORT_FORMATTER = new DecimalFormat("#.##");

	public static final double EPSILON = 0.000001; // 10e-6

	public static boolean isBigger(double bigger, double smaller) {
		return bigger - smaller > EPSILON;
	}

	public static boolean isSmaller(double smaller, double bigger) {
		return isBigger(bigger, smaller);
	}

	public static String toString(double d) {
		return FORMATTER.format(d);
	}

	public static String toShortString(double d) {
		return SHORT_FORMATTER.format(d);
	}

	public static boolean equals(double d1, double d2) {
		return !isBigger(d1, d2) && !isBigger(d2, d1);
	}

	public static double round(double amount, double from, double to) {
		if (amount < 0) {
			return -round(-amount, to, from);
		} else {
			double larger = Math.max(from, to);
			double temp = larger + amount;
			return temp - larger;
		}
	}

	public static String toTabString(String... strings) {
		String base = strings[0];
		for (int i = 1; i < strings.length; i++) {
			base += "\t" + strings[i];
		}
		return base;
	}

	public static double normalize(double average) {
		if (Double.isFinite(average)) {
			return Math.min(10.0, Math.max(1.0, average));
		} else {
			return 1.0;
		}
	}

}
