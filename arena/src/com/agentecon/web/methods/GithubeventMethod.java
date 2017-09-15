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

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class GithubeventMethod extends WebApiMethod {
	
	private ListMethod simulations;
	
	public GithubeventMethod(ListMethod simulations) {
		this.simulations = simulations;
	}

	@Override
	public Response execute(IHTTPSession session, Parameters params) throws IOException {
		System.out.println("Received github event with the following parameters: " + params.toString());
		return NanoHTTPD.newFixedLengthResponse("");
	}

}
