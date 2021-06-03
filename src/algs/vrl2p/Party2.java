package algs.vrl2p;

import java.util.Map;

import model.Message;
import security.EncMat;
import security.EncMatMultThread;
import alg.AlgInfo;
import client.ClientForParticipant;

import com.mathworks.toolbox.javabuilder.MWNumericArray;

import common.Common;
import common.MatComputeHelper;
/**
 * The code is for paper <b>"Efficient Vertical Federated Learning Method for Ridge Regression of Large-Scale Samples via Least-Squares Solution"</b>. <br/>
 * This class is the second participant for VRL-2P algorithm.
 */
public class Party2 extends ClientForParticipant{
	private AlgInfo algInfo;
	public Party2(AlgInfo algInfo) {
		super(algInfo.getParty(2), algInfo.getCoordinator());
		this.algInfo=algInfo;
	}
	private MWNumericArray X0 = null;
	private MWNumericArray y = null;
	private int n;
	private int d;
	private MWNumericArray res = null;
	@Override
	public void run() {
		Common.println("Participant2 started...");
		double omiga=1.0/n;
		int p = 1;
		Message msg = null;
		MWNumericArray Q=MatComputeHelper.randOrthMat(d);
		MWNumericArray X=MatComputeHelper.mul(X0, Q);
		MWNumericArray Xt = MatComputeHelper.mul(MatComputeHelper.transpose(X),omiga);
		MWNumericArray F2=coMul(X, algInfo.getTestId()*Common.ROOMID_BIAS+1, 2);
		msg=waitMessage(21);
		EncMat encF1=(EncMat) msg.getObj();
		EncMat M12=encF1.add(F2);
		sendMessage(algInfo.getParty(1), 21, M12);
		EncMat M21=M12.transpose();
		MWNumericArray M22 = MatComputeHelper.add(
				MatComputeHelper.mul(Xt, X),
				MatComputeHelper.mul(MatComputeHelper.eye(d), algInfo.getLambda()*omiga));
		MWNumericArray C = MatComputeHelper.inv(M22);
		EncMat encC=new EncMatMultThread(C,pubKey);
		sendMessage(algInfo.getParty(1), 21, encC);
		msg = waitMessage(21);
		EncMat encCt = (EncMat) msg.getObj();
		EncMat encR=mul(encCt, M12);
		encR=mul(M21, encR);
		encR=new EncMatMultThread(M22, pubKey).subtract(encR);
		encR=inv(encR);
		MWNumericArray r12=coMul(y, algInfo.getTestId()*Common.ROOMID_BIAS+2, 2);
		EncMat encr12=new EncMatMultThread(r12, pubKey);
		sendMessage(algInfo.getParty(1), 21, encr12);
		MWNumericArray r2=MatComputeHelper.mul(Xt, y);
		EncMat encu2=encR.mul(r2);
		sendMessage(algInfo.getParty(1), 21, encu2);
		msg = waitMessage(21);
		EncMat encu1=(EncMat) msg.getObj();
		EncMat encv2=EncMat.mul2(C, mul(M21, encu1));
		EncMat encw = encu2.subtract(encv2);
		res = MatComputeHelper.mul(Q,decr(encw));
		Common.println(getName() + "========" + (p++)
				+ "===========================");
	}

	@Override
	public void loadData(Object obj) {
		Map<String,Object> mp=(Map<String, Object>) obj;
		X0 = (MWNumericArray) mp.get("DataPiece");
		n=X0.getDimensions()[0];
		d=X0.getDimensions()[1];
		y = (MWNumericArray) mp.get("DataDecision");
	}
	@Override
	public Object showResult() {
		return res;
	}

}
