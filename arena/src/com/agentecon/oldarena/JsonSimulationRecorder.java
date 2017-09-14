package com.agentecon.oldarena;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import com.agentecon.ISimulation;
import com.agentecon.classloader.GitSimulationHandle;
import com.agentecon.classloader.LocalSimulationHandle;
import com.agentecon.metric.series.Chart;
import com.agentecon.runner.SimulationLoader;
import com.agentecon.runner.SimulationRunner;

public class JsonSimulationRecorder {

	private Path webPath;
	private SimulationList sims;
	private JsonPersister persister;

	public JsonSimulationRecorder() {
		this.webPath = FileSystems.getDefault().getPath("Webcontent");
		this.persister = new JsonPersister(webPath.resolve("data"));
		try {
			this.sims = (SimulationList) persister.load(SimulationList.class, SimulationList.DEFAULT_ID);
		} catch (IOException e) {
			this.sims = new SimulationList();
		}
	}

	public void generateFromRepo(GitSimulationHandle handle) throws IOException {
		SimulationLoader loader = new SimulationLoader(handle);
		SimulationInfo info = new SimulationInfo(loader.getChecksum(), handle);
		generate(info, loader);
	}

	public void generateLocal(File simulationJarFile) throws IOException {
		SimulationLoader loader = new SimulationLoader(new LocalSimulationHandle(simulationJarFile));
		SimulationInfo info = new SimulationInfo(loader.getChecksum(), "local");
		generate(info, loader);
	}

	private void generate(SimulationInfo info, SimulationLoader loader) throws IOException {
		SimulationInfo existing = sims.getSimulation(loader.getChecksum());
		int[] chartsToRecycle = existing == null ? new int[0] : existing.getChartIds();
		ISimulation sim = loader.loadSimulation();
		SimulationRunner runner = new SimulationRunner(sim);
		info.notifyStarted(sim.getConfig().getRounds());
		runner.run(null);
		Chart[] charts = runner.getCharts(info.getHash());
		int[] toDel = info.recycleChartIds(chartsToRecycle, charts);
		for (int del : toDel) {
			persister.delete(Chart.class, del);
		}
		for (Chart chart : charts) {
			persister.save(chart);
		}
		info.notifyEnded(runner.getSystemOutput(), charts);
		persister.save(info);
		sims.put(info);
		persister.save(sims);
	}

	// public static void main(String[] args) throws InterruptedException, IOException {
	// JsonSimulationRecorder generator = new JsonSimulationRecorder();
	// GitBasedSimulationList list = new GitBasedSimulationList("meisserecon", "agentecon");
	//
	// boolean on = false;
	// for (GitSimulationHandle handle : list.getSims()) {
	// if (handle.getName().equals("PairedExploration")) {
	// on = true;
	// }
	// if (on) {
	// try {
	// System.out.println("Generating files for " + handle);
	// generator.generateFromRepo(handle);
	// } catch (IOException e) {
	// System.out.println("Failed to load " + handle + " from " + handle.getSimulationURL());
	// e.printStackTrace();
	// }
	// }
	// }
	// }

}
