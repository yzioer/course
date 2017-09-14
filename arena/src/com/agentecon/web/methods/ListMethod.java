/**
 * Created by Luzius Meisser on Jun 15, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.web.methods;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.agentecon.classloader.SimulationHandle;
import com.agentecon.runner.SimulationLoader;
import com.agentecon.runner.SimulationStepper;
import com.agentecon.web.data.JsonData;

public class ListMethod extends WebApiMethod {
	
	private transient HashMap<String, SimulationHandle> handles;
	private transient HashMap<SimulationHandle, SimulationStepper> simulations;

	public ListMethod() {
		this.handles = new HashMap<>();
		this.simulations = new HashMap<>();
	}

	public void add(SimulationHandle handle) {
		this.handles.put(handle.getName(), handle);
	}
	
	public synchronized SimulationStepper getSimulation(SimulationHandle handle) throws IOException {
		SimulationStepper stepper = this.simulations.get(handle);
		if (stepper == null){
			stepper = new SimulationStepper(handle);
			simulations.put(handle, stepper);
		}
		return stepper;
	}
	
	public SimulationStepper getSimulation(String name) throws IOException{
		SimulationHandle handle = handles.get(name);
		return getSimulation(handle);
	}
	
	public SimulationHandle getHandle(String simulation) {
		return handles.get(simulation);
	}

	@Override
	public JsonData getJsonAnswer(Parameters params) {
		return new SimulationList(handles.values());
	}

	class SimulationList extends JsonData {
		
		public Collection<SimulationInfo> sims = new ArrayList<>();
		
		public SimulationList(Collection<SimulationHandle> collection) {
			for (SimulationHandle handle: collection){
				this.sims.add(new SimulationInfo(handle));
			}
		}
	}
	
	class SimulationInfo {
		
		public String owner;
		public String path;
		public String sourceUrl;

		public SimulationInfo(SimulationHandle handle) {
			this.owner = handle.getOwner();
			this.path = handle.getName();
			this.sourceUrl = handle.getBrowsableURL(SimulationLoader.SIM_CLASS).toExternalForm();
		}
		
	}
}
