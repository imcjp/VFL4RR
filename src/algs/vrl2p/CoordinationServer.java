package algs.vrl2p;

import java.util.HashMap;
import java.util.Map;

import model.Stage;
import server.Coordinator;
import alg.AlgInfo;

import common.Common;
/**
 * The code is for paper <b>"Efficient Vertical Federated Learning Method for Ridge Regression of Large-Scale Samples via Least-Squares Solution"</b>. <br/>
 * This class is the Coordination for VRL-2P algorithm.
 */
public class CoordinationServer extends Coordinator{
	private Party1 participant1;
	private Party2 participant2;
	private Map<String, Object> report;
	public CoordinationServer(AlgInfo algInfo) {
		super(algInfo.getCoordinator());
	}
	public void prepare() {
		participant1.setPubKey(getPubKey());
		participant2.setPubKey(getPubKey());
	}
	public void run(){
		Common.tic();
		Stage stage = new Stage(2);
		stage.start();
		stage.addData(participant1);
		stage.addData(participant2);
		stage.waitForCompletion();
		double tm=Common.toc(false);
		report=new HashMap<String, Object>();
		report.put("time", tm);
	}
	public Party1 getParticipant1() {
		return participant1;
	}
	public Party2 getParticipant2() {
		return participant2;
	}
	public void setTheParticipants(Party1 participant1,Party2 participant2) {
		this.participant1 = participant1;
		this.participant2=participant2;
	}
	public Map<String, Object> getReport() {
		return report;
	}
	
}
