package server;

import model.PCModelBase;

import com.mathworks.toolbox.javabuilder.MWNumericArray;

import common.NameOfProtocols;
import common.Common;
import common.MatComputeHelper;
import security.EncMat;
import security.EncMatMultThread;
import security.PrivateKey;

/**
 * The code is for paper <b>"Efficient Vertical Federated Learning Method for Ridge Regression of Large-Scale Samples via Least-Squares Solution"</b>. <br/>
 * The class implements the Security Agent Framework for Matrix Computing.
 * The class is used to help the coordinator complete the agent protocols, such as MMA and the MIA.
 */
public class ProtocolThread extends PCModelBase<ReqFunc> {
	private Coordinator agent;
	private PrivateKey priKey = null;

	public ProtocolThread(Coordinator agent, PrivateKey priKey) {
		super(8);
		this.agent = agent;
		this.priKey = priKey;
	}

	@Override
	protected void beforRun(int id) {
	}

	@Override
	protected int run(int id, ReqFunc obj) {
		String funcName = obj.getFuncName();
		int ret = 1;
		if (NameOfProtocols.MUL.equals(funcName)) {
			ret = 2;
			Common.println(funcName + ": Parameter acquisition process...");
			EncMat.tic();
			Object[] params = obj.getParams();
			EncMat encA = (EncMat) params[0];
			EncMat encB = (EncMat) params[1];
			MWNumericArray A = encA.getDecrMW(priKey);
			MWNumericArray B = encB.getDecrMW(priKey);
			EncMat.toc();
			ret = 3;
			Common.println(funcName + ": Calculation process...");
			EncMat.tic();
			MWNumericArray C = MatComputeHelper.mul(A, B);
			EncMat encC = new EncMatMultThread(C, agent.getPubKey());
			EncMat.toc();
			ret = 4;
			Common.println(funcName + ": Transmission process...");
			EncMat.tic();
			agent.sendMessage(obj.getSender(), obj.getRetPort(), encC);
			EncMat.toc();
			ret = 0;
		} else if (NameOfProtocols.INV.equals(funcName)) {
			ret = 2;
			Common.println(funcName + ": Parameter acquisition process...");
			EncMat.tic();
			Object[] params = obj.getParams();
			EncMat encA = (EncMat) params[0];
			MWNumericArray A = encA.getDecrMW(priKey);
			EncMat.toc();
			ret = 3;
			Common.println(funcName + ": Calculation process...");
			EncMat.tic();
			MWNumericArray iA = MatComputeHelper.inv(A);
			EncMat enciA = new EncMatMultThread(iA, agent.getPubKey());
			EncMat.toc();
			ret = 4;
			Common.println(funcName + ": Transmission process...");
			EncMat.tic();
			agent.sendMessage(obj.getSender(), obj.getRetPort(), enciA);
			EncMat.toc();
			ret = 0;
		} else if (NameOfProtocols.DECR.equals(funcName)) {
			ret = 2;
			Common.println(funcName + ": Parameter acquisition process...");
			EncMat.tic();
			Object[] params = obj.getParams();
			EncMat encA = (EncMat) params[0];
			MWNumericArray res = encA.getDecrMW(priKey);
			EncMat.toc();
			ret = 4;
			Common.println(funcName + ": Transmission process...");
			EncMat.tic();
			agent.sendMessage(obj.getSender(), obj.getRetPort(), res);
			EncMat.toc();
			ret = 0;
		}else if (NameOfProtocols.COMUL.equals(funcName)) {
			ret = 2;
			Common.println(funcName + ": Parameter acquisition process...");
			EncMat.tic();
			Object[] parm1 = obj.getParams();
			int[] dt =new int[]{(Integer)parm1[1],(Integer)parm1[2],(Integer)parm1[3],obj.getRetPort()};
			RoomForProtocol.addPlayer(agent, RoomForSEM.class, (int) parm1[0], obj.getSender(), dt);
			EncMat.toc();
			ret = 0;
		}
		return ret;
	}

	@Override
	protected boolean fail(int id, ReqFunc obj, int failId) {
		if (failId == 1) {
			Common.println("Illegal function...");
		} else if (failId == 2) {
			Common.println("Parameter error...");
		} else if (failId == 3) {
			Common.println("Calculation process error...");
		} else if (failId == 4) {
			Common.println("Transmission error...");
		}
		return true;
	}

	@Override
	protected void afterRun(int id) {
	}

}
