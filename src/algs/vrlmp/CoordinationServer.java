package algs.vrlmp;

import java.util.HashMap;
import java.util.Map;

import model.Stage;
import server.Coordinator;
import alg.AlgInfo;

import common.Common;
/**
 * The code is for paper <b>"Efficient Vertical Federated Learning Method for Ridge Regression of Large-Scale Samples via Least-Squares Solution"</b>. <br/>
 * This class is the Coordination for VRL-MP algorithm.
 */
public class CoordinationServer extends Coordinator{
	private Party[] participants;
	private Map<String, Object> report;
	private AlgInfo algInfo;
	public CoordinationServer(AlgInfo algInfo) {
		super(algInfo.getCoordinator());
		this.algInfo=algInfo;
	}
	public void prepare() {
		for (Party party : participants) {
			party.setPubKey(getPubKey());
		}
	}
	public void run(){
		Common.tic();
		int m=algInfo.getMxPid();
		Stage stage = new Stage(m);
		stage.start();
		for (int i = 0; i < m; i++) {
			stage.addData(participants[i]);
		}
		stage.waitForCompletion();
		double tm=Common.toc(false);
		report=new HashMap<String, Object>();
		report.put("time", tm);
	}
	public Map<String, Object> getReport() {
		return report;
	}
	public Party[] getParticipants() {
		return participants;
	}
	public void setParticipants(Party[] participants) {
		this.participants = participants;
	}
	
}
