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
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.agentecon.IAgentFactory;
import com.agentecon.classloader.GitSimulationHandle;
import com.agentecon.classloader.LocalSimulationHandle;
import com.agentecon.configuration.AgentFactoryMultiplex;
import com.agentecon.configuration.CompilingAgentFactory;

public class ExerciseAgentLoader extends AgentFactoryMultiplex {

	private static final Collection<String> TEAMS = createRepos(1,2,3,4,5,7,10);

	public ExerciseAgentLoader(String classname, boolean remoteTeams) throws SocketTimeoutException, IOException {
		super(createFactories(classname, remoteTeams));
	}

	private static Collection<String> createRepos(int... numbers) {
		ArrayList<String> repos = new ArrayList<>();
		for (int i: numbers) {
			String number = Integer.toString(i);
			repos.add("team" + (number.length() == 1 ? "00" : "0") + number);
		}
		return repos;
	}

	private static IAgentFactory[] createFactories(String classname, boolean remoteTeams) throws SocketTimeoutException, IOException {
		ArrayList<CompilingAgentFactory> factories = new ArrayList<>();
		CompilingAgentFactory defaultFactory = new CompilingAgentFactory(classname, "meisser", "course");
		factories.add(defaultFactory);
		if (remoteTeams) {
			Stream<CompilingAgentFactory> stream = TEAMS.parallelStream().map(team -> {
				try {
					CompilingAgentFactory factory = new CompilingAgentFactory(classname, new GitSimulationHandle("meisser", team));
					factory.preload();
					return factory;
				} catch (IOException e) {
					return null;
				} catch (ClassNotFoundException e) {
					return null;
				}
			}).filter(factory -> factory != null);
			factories.addAll(stream.collect(Collectors.toList()));
		} else {
			LocalSimulationHandle local = new LocalSimulationHandle(new File("../exercises/src"));
			factories.add(new CompilingAgentFactory(classname, local));
		}
		return factories.toArray(new IAgentFactory[factories.size()]);
	}

}
