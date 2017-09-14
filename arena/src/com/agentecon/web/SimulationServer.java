package com.agentecon.web;

import java.io.IOException;
import java.util.StringTokenizer;

import com.agentecon.classloader.GitSimulationHandle;
import com.agentecon.classloader.LocalSimulationHandle;
import com.agentecon.web.methods.AgentsMethod;
import com.agentecon.web.methods.ChildrenMethod;
import com.agentecon.web.methods.DownloadCSVMethod;
import com.agentecon.web.methods.InfoMethod;
import com.agentecon.web.methods.ListMethod;
import com.agentecon.web.methods.MethodsMethod;
import com.agentecon.web.methods.MetricsMethod;
import com.agentecon.web.methods.MiniChartMethod;
import com.agentecon.web.methods.RankingMethod;
import com.agentecon.web.methods.SizeTypesMethod;
import com.agentecon.web.methods.TradeGraphMethod;
import com.agentecon.web.methods.WebApiMethod;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class SimulationServer extends VisServer {

	private MethodsMethod methods;
	private ListMethod simulations;

	public SimulationServer(int port) throws IOException, InterruptedException {
		super(port);
		String owner = "meisserecon";
		String repo = "agentecon";

		this.simulations = new ListMethod();
		LocalSimulationHandle local = new LocalSimulationHandle();
		if (local.isPresent()) {
			this.simulations.add(new LocalSimulationHandle());
		}
		this.simulations.add(new GitSimulationHandle(owner, repo, "master"));
		this.simulations.add(new GitSimulationHandle(owner, repo, "demo"));
		// this.simulations.add(new GitSimulationHandle(owner, repo, "multigoodtag"));

		this.methods = new MethodsMethod();
		this.methods.add(this.simulations);
		this.methods.add(new SizeTypesMethod());
		this.methods.add(new MetricsMethod());
		this.methods.add(new InfoMethod(this.simulations));
		this.methods.add(new AgentsMethod(this.simulations));
		this.methods.add(new TradeGraphMethod(this.simulations));
		this.methods.add(new ChildrenMethod(this.simulations));
		this.methods.add(new RankingMethod(this.simulations));
		this.methods.add(new DownloadCSVMethod(this.simulations));
		this.methods.add(new MiniChartMethod(this.simulations));
	}

	@Override
	public Response serve(IHTTPSession session) {
		Method method = session.getMethod();
		assert method == Method.GET : "Received a " + method;
		String uri = session.getUri();
		Response res = createResponse(session, uri);
		res.addHeader("Access-Control-Allow-Origin", "*");
		return res;
	}

	private Response createResponse(IHTTPSession session, String uri) {
		StringTokenizer tok = new StringTokenizer(uri, "\\/");
		if (tok.hasMoreTokens()) {
			try {
				String methodName = tok.nextToken();
				WebApiMethod calledMethod = methods.getMethod(methodName);
				if (calledMethod != null) {
					return calledMethod.execute(session);
				} else {
					return super.serve(session);
				}
			} catch (RuntimeException e) {
				String msg = "Failed to handle call due to " + e.toString();
				System.out.println(msg);
				return NanoHTTPD.newFixedLengthResponse(Status.INTERNAL_ERROR, getMimeTypeForFile(".html"), msg);
			} catch (IOException e) {
				String msg = "Failed to handle call due to " + e.toString();
				System.out.println(msg);
				return NanoHTTPD.newFixedLengthResponse(Status.INTERNAL_ERROR, getMimeTypeForFile(".html"), msg);
			}
		} else {
			return super.serve(session);
		}
	}

	// protected Response serveSimulation(IHTTPSession session, String uri, StringTokenizer tok) throws IOException {
	// if (tok.hasMoreTokens()) {
	// String methodName = tok.nextToken();
	// WebApiMethod calledMethod = methods.getMethod(methodName);
	// if (calledMethod != null) {
	// return serve(session, calledMethod.execute(new StringTokenizer(uri, "\\/"), new Parameters(session)));
	// } else {
	// return super.serve(session, "sim.html");
	// }
	// } else {
	// return super.serve(session, "sim.html");
	// }
	// }

	public static void main(String[] args) throws IOException, InterruptedException {
		SimulationServer server = new SimulationServer(8080);
		server.run();
	}

}
