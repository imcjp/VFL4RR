package algs.cvrg;

import server.Coordinator;
import alg.AlgInfo;
/**
 * The code is for paper <b>"Edge Computing Aided Coded Vertical Federated Linear Regression"</b>. <br/>
 * This class is the Coordination for CVRG algorithm.
 */
public class CoordinationServer extends Coordinator{
	public CoordinationServer(AlgInfo algInfo) {
		super(algInfo.getCoordinator());
	}
}
