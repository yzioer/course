/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.exercises;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import com.agentecon.IAgentFactory;
import com.agentecon.classloader.GitSimulationHandle;
import com.agentecon.classloader.LocalSimulationHandle;
import com.agentecon.configuration.AgentFactoryMultiplex;
import com.agentecon.configuration.CompilingAgentFactory;

public class ExerciseAgentLoader extends AgentFactoryMultiplex {
	
	private static final String[] TEAMS = new String[]{"team000"};
	
	public ExerciseAgentLoader(String classname) throws SocketTimeoutException, IOException {
		super(createFactories(classname));
	}

	private static IAgentFactory[] createFactories(String classname) throws SocketTimeoutException, IOException {
		ArrayList<CompilingAgentFactory> factories = new ArrayList<>();
		for (String team: TEAMS){
			factories.add(new CompilingAgentFactory(classname, new GitSimulationHandle("meisser", team)));
		}
		LocalSimulationHandle local = new LocalSimulationHandle(new File("../exercises/src"));
		if (local.isPresent()){
			factories.add(new CompilingAgentFactory(classname, local));
		}
		return factories.toArray(new IAgentFactory[factories.size()]);
	}

}
