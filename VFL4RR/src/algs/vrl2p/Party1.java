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
 * This class is the first participant for VRL-2P algorithm.
 */
public class Party1 extends ClientForParticipant{
	private AlgInfo algInfo;
	public Party1(AlgInfo algInfo) {
		super(algInfo.getParty(1), algInfo.getCoordinator());
		this.algInfo=algInfo;
	}
	private MWNumericArray X0 = null;
	private int n;
	private int d;
	private MWNumericArray res = null;
	@Override
	public void run() {
		Common.println("Participant1 started...");
		double omiga=1.0/n;
		int p = 1;
		Message msg = null;
		MWNumericArray Q=MatComputeHelper.randOrthMat(d);
		MWNumericArray X=MatComputeHelper.mul(X0, Q);
		MWNumericArray Xt = MatComputeHelper.mul(MatComputeHelper.transpose(X),omiga);
		MWNumericArray F1=coMul(Xt, algInfo.getTestId()*Common.ROOMID_BIAS+1, 1);
		EncMat encF1=new EncMatMultThread(F1,pubKey);
		sendMessage(algInfo.getParty(2), 21, encF1);
		msg=waitMessage(21);
		EncMat M12=(EncMat) msg.getObj();
		EncMat M21=M12.transpose();
		/////////////////////////////////////////////////////
		MWNumericArray M11 = MatComputeHelper.add(
				MatComputeHelper.mul(Xt, X),
				MatComputeHelper.mul(MatComputeHelper.eye(d), algInfo.getLambda()*omiga));
		MWNumericArray C = MatComputeHelper.inv(M11);
		EncMat encC=new EncMatMultThread(C,pubKey);
		sendMessage(algInfo.getParty(2), 21, encC);
		Common.println(getName() + "========" + (p++)
				+ "===========================");
		msg = waitMessage(21);
		EncMat encCt = (EncMat) msg.getObj();
		EncMat encR=mul(encCt, M21);
		encR=mul(M12, encR);
		encR=new EncMatMultThread(M11, pubKey).subtract(encR);
		encR=inv(encR);
		MWNumericArray r11=coMul(Xt, algInfo.getTestId()*Common.ROOMID_BIAS+2, 1);
		msg=waitMessage(21);
		EncMat encr12=(EncMat) msg.getObj();
		EncMat encr=encr12.add(r11);
		EncMat encu1=mul(encR, encr);
		sendMessage(algInfo.getParty(2), 21, encu1);
		msg=waitMessage(21);
		EncMat encu2=(EncMat) msg.getObj();
		EncMat encv1=EncMat.mul2(C, mul(M12, encu2));
		EncMat encw = encu1.subtract(encv1);
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
	}
	@Override
	public Object showResult() {
		return res;
	}

}
