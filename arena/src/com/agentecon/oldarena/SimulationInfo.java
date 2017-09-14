// Created on Jun 8, 2015 by Luzius Meisser

package com.agentecon.oldarena;

import java.util.Date;

import com.agentecon.classloader.GitSimulationHandle;
import com.agentecon.metric.series.Chart;
import com.agentecon.runner.Checksum;
import com.agentecon.runner.SimulationRunner;

public class SimulationInfo extends Persistable {

	private static final int MAX_OUTPUT_LEN = 10000;

	private int[] chartids;
	private int serverVersion;

	private String hash;
	private String name;
	private String sourceUrl;

	private int currentRound, rounds;
	private long lastRunStarted, lastRunEnded, lastProgress;

	private String output;

	public SimulationInfo(Checksum checksum, String name) {
		this(checksum);
		this.name = name;
		this.sourceUrl = "";
	}

	public SimulationInfo(Checksum checksum, GitSimulationHandle handle) {
		this(checksum);
		this.name = handle.getName();
		this.sourceUrl = handle.getBrowsableURL("com.agentecon.Simulation").toString();
	}

	private SimulationInfo(Checksum checksum) {
		this.chartids = new int[] {};
		this.serverVersion = SimulationRunner.VERSION;
		this.lastRunEnded = 0;
		this.lastRunStarted = 0;
		this.lastProgress = 0;
		this.hash = checksum.toString();
	}

	public boolean hasChecksum(Checksum checksum) {
		return hash.equals(checksum.toString());
	}

	// public SimulationInfo(GitSimulationHandle handle) {
	// this(handle.getId());
	// this.name = handle.getName();
	// this.hash = handle.getHash();
	// this.sourceUrl = handle.getSourceUrl();
	// this.description = handle.getDescription();
	// }

	public String getName() {
		return name;
	}

	public int getCompletionPercent() {
		if (lastRunEnded != 0) {
			return 100;
		} else if (rounds == 0) {
			return 0;
		} else {
			return currentRound * 100 / rounds;
		}
	}

	public boolean isMaster() {
		return name.equals("master");
	}

	public String getPermanentId() {
		return isMaster() ? hash : name;
	}

	public long getStartDate() {
		return lastRunStarted;
	}

	public long getEndDate() {
		return lastRunEnded;
	}

	public int[] getChartIds() {
		return chartids;
	}

	public void triggerRun() {
		this.lastRunStarted = 0;
		this.lastRunEnded = 0;
		this.lastProgress = 0;
	}

	public String getSourceUrl() {
		return sourceUrl;
	}

	public boolean shouldRun() {
		return serverVersion < SimulationRunner.VERSION || hasError() || ((lastRunEnded == 0 || chartids == null) && !isRunning());
	}

	private boolean isRunning() {
		long diff = System.currentTimeMillis() - lastProgress;
		return diff < 10 * 60 * 1000;
	}
	
	public int[] recycleChartIds(int[] existing, Chart[] charts) {
		int[] toDel = new int[] {};
		if (existing.length > charts.length) {
			toDel = new int[existing.length - charts.length];
			System.arraycopy(existing, charts.length, toDel, 0, toDel.length);
			int[] newIds = new int[charts.length];
			System.arraycopy(existing, 0, newIds, 0, newIds.length);
			existing = newIds;
		}
		for (int i = 0; i < charts.length && i < existing.length; i++) {
			charts[i].setId(existing[i]);
		}
		return toDel;
	}

	public void notifyWorkerStarted(int serverVersion) {
		this.serverVersion = serverVersion;
		this.lastRunStarted = System.currentTimeMillis();
		this.lastProgress = lastRunStarted;
		this.lastRunEnded = 0;
		this.output = "Run started at " + new Date(lastRunStarted);
	}

	public void notifyStarted(int rounds) {
		this.rounds = rounds;
	}

	public void notifyProgress(int day, String output, Chart... charts) {
		int len = output.length();
		if (len > MAX_OUTPUT_LEN) {
			output = output.substring(len - 10000, len);
		}
		this.output = output;
		this.currentRound = day;
		this.lastProgress = System.currentTimeMillis();
		if (this.chartids == null) {
			this.chartids = new int[] {};
		}
		if (charts.length > chartids.length) {
			int[] ids = new int[charts.length];
			System.arraycopy(chartids, 0, ids, 0, chartids.length);
			for (int i = chartids.length; i < charts.length; i++) {
				ids[i] = charts[i].getId();
			}
			this.chartids = ids;
		}
	}

	public boolean hasError() {
		return output != null && output.startsWith("Error while accessing");
	}

	public void notifyEnded(String output, Chart... charts) {
		notifyProgress(rounds, output, charts);
		this.lastRunEnded = lastProgress;
	}

	public String getOutput() {
		return output;
	}

	@Override
	public boolean equals(Object o) {
		return ((SimulationInfo) o).hash.equals(hash);
	}

	public String getHash() {
		return hash;
	}

}
