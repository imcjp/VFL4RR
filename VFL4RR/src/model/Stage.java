package model;

import common.Common;

import client.ClientForParticipant;

/**
 * The code is for paper <b>"Efficient Vertical Federated Learning Method for Ridge Regression of Large-Scale Samples via Least-Squares Solution"</b>. <br/>
 * The class defines a stage. Bring the participants of federated learning to this stage and enable them to implement federated learning according to their own algorithms.
 */
public class Stage extends PCModelBase<ClientForParticipant>{
	public Stage(int member) {
		super(member);
	}
	@Override
	protected void beforRun(int id) {
	}
	@Override
	protected int run(int id, ClientForParticipant obj) {
		// TODO Auto-generated method stub
		Common.println(obj.getName()+" ready to go...");
		obj.run();
		Common.println(obj.getName()+" calculation completed");
		return 0;
	}

	@Override
	protected void afterRun(int id) {
		// TODO Auto-generated method stub
		
	}

}
