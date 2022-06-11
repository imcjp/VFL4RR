package algs.vrg;

import server.Coordinator;
import alg.AlgInfo;
/**
 * The code is for paper <b>"Efficient Vertical Federated Learning Method for Ridge Regression of Large-Scale Samples via Least-Squares Solution"</b>. <br/>
 * This class is the Coordination for VRG algorithm.
 */
public class CoordinationServer extends Coordinator{
	public CoordinationServer(AlgInfo algInfo) {
		super(algInfo.getCoordinator());
	}
}
