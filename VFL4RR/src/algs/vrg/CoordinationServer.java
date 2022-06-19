package algs.vrg;

import server.Coordinator;
import alg.AlgInfo;
/**
 * The code is for paper <b>"Yang Q., Liu Y., Chen T., et al. (2019). Federated machine learning: concept and applications"</b>. <br/>
 * This class is the Coordination for VRG algorithm.
 */
public class CoordinationServer extends Coordinator{
	public CoordinationServer(AlgInfo algInfo) {
		super(algInfo.getCoordinator());
	}
}
