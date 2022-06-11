package algs.vrlmp;

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
 * In Alg. VRL-MP, the implementation for each participant.
 */

public class Party extends ClientForParticipant{
	private int pid=0;
	private MWNumericArray X0 = null;
	private MWNumericArray y = null;
	private int n;
	private int d;
	private MWNumericArray res = null;
	private AlgInfo algInfo;
	public Party(AlgInfo algInfo,int pid) {
		super(algInfo.getParty(pid), algInfo.getCoordinator());
		this.algInfo=algInfo;
		this.pid=pid;
	}
	public static synchronized void showMat(MWNumericArray mat,String name) {
		System.out.println("The values of "+name+" are as follows:");
		double dn[][]=(double[][]) mat.toDoubleArray();
		for (int i = 0; i < dn.length; i++) {
			for (int j = 0; j < dn[0].length; j++) {
				System.out.print(dn[i][j]+"\t");
			}
			System.out.println();
		}
		System.out.println("///////////////////////////////////////////");
	}
	@Override
	public void run() {
		int mxPid=algInfo.getMxPid();
		double omiga=1.0/n;
		Message msg = null;
		MWNumericArray Q=MatComputeHelper.randOrthMat(d);
		MWNumericArray X=MatComputeHelper.mul(X0, Q);
		MWNumericArray Xt = MatComputeHelper.transpose(MatComputeHelper.mul(X, omiga));
		int roomNum=0;
		MWNumericArray F1s[]=new MWNumericArray[pid];
		for (int i = 0; i < mxPid; i++) {
			if (i!=pid) {
				if (i<pid) {
					roomNum=pid*100+i+algInfo.getTestId()*Common.ROOMID_BIAS;
					F1s[i]=coMul(X, roomNum, 2);
				}else if(i>pid){
					roomNum=i*100+pid+algInfo.getTestId()*Common.ROOMID_BIAS;
					MWNumericArray F1=coMul(Xt, roomNum, 1);
					EncMat encF1=new EncMatMultThread(F1,pubKey);
					sendMessage(algInfo.getParty(i), 21, encF1);
				}
			}
		}
		EncMat encMs[]=new EncMat[pid];
		for (int i = 0; i < pid; i++) {
			msg=waitMessage(21);
			String sender=msg.getSender();
			int sid=Integer.parseInt(sender.substring(algInfo.getPartyGroup().length()));
			EncMat F2=(EncMat) msg.getObj();
			encMs[sid]=F2.add(F1s[sid]);
		}
		EncMat encMr=null;
		if (pid>0) {
			encMr=EncMat.vstack(encMs);
		}
		EncMat encr=null;
		if (pid<mxPid-1) {
			roomNum=10000+pid+algInfo.getTestId()*Common.ROOMID_BIAS;
			MWNumericArray y1=coMul(Xt, roomNum, 1);
			EncMat ency1=new EncMatMultThread(y1,pubKey);
			sendMessage(algInfo.getParty(mxPid-1), 20, ency1);
		}else{
			MWNumericArray y2s[]=new MWNumericArray[pid];
			for (int i = 0; i < mxPid-1; i++) {
				roomNum=10000+i+algInfo.getTestId()*Common.ROOMID_BIAS;
				y2s[i]=coMul(y, roomNum, 2);
			}
			EncMat encYs[]=new EncMat[mxPid];
			encYs[pid]=new EncMat(MatComputeHelper.mul(Xt, y), pubKey);
			for (int i = 0; i < pid; i++) {
				msg=waitMessage(20);
				String sender=msg.getSender();
				int sid=Integer.parseInt(sender.substring(algInfo.getPartyGroup().length()));
				EncMat encY1=(EncMat) msg.getObj();
				encYs[sid]=encY1.add(y2s[sid]);
			}
			encr=EncMat.vstack(encYs);
		}
		EncMat encR=null;
		EncMat encResW=null;
		if (pid==0) {
			MWNumericArray XX = MatComputeHelper.add(
			MatComputeHelper.mul(Xt, X),
			MatComputeHelper.mul(MatComputeHelper.eye(d), algInfo.getLambda()*omiga));
			MWNumericArray R = MatComputeHelper.inv(XX);
			encR=new EncMatMultThread(R,pubKey);
			sendMessage(algInfo.getParty(pid+1), 22, encR);
			msg=waitMessage(23);
			encResW=(EncMat) msg.getObj();
		}else{
			MWNumericArray C = MatComputeHelper.add(
			MatComputeHelper.mul(Xt, X),
			MatComputeHelper.mul(MatComputeHelper.eye(d), algInfo.getLambda()*omiga));
			msg=waitMessage(22);
			EncMat encR0=(EncMat) msg.getObj();
			EncMat encMrt=encMr.transpose();
			EncMat encB=mul(encMrt, encR0);
			EncMat encR4=mul(encB, encMr);
			EncMat encC=new EncMatMultThread(C,pubKey);
			encR4=encC.subtract(encR4);
			encR4=inv(encR4);
			encR4=encR4.add(encR4.transpose()).mul(0.5);
			EncMat encBt=encB.transpose();
			EncMat encR1=mul(encBt, encR4);
			encR1=mul(encR1, encB);
			encR1=encR0.add(encR1);
			MWNumericArray G=MatComputeHelper.mul(MatComputeHelper.inv(C),-1);
			EncMat encR2=EncMat.mul2(G, encMrt);
			encR2=mul(encR2,encR1);
			EncMat encR3=encR2.transpose();
			encR=EncMat.hstack(EncMat.vstack(encR1,encR2),EncMat.vstack(encR3,encR4));
			if (pid<mxPid-1) {
				sendMessage(algInfo.getParty(pid+1), 22, encR);
				msg=waitMessage(23);
				encResW=(EncMat) msg.getObj();
			}else{
				EncMat encw=mul(encR, encr);
				int[] dim=algInfo.getDim();
				int p=0;
				for (int i = 0; i < mxPid-1; i++) {
					EncMat encwi=encw.getSubRow(p, dim[i]+p);
					sendMessage(algInfo.getParty(i), 23, encwi);
					p+=dim[i];
				}
				encResW=encw.getSubRow(p, dim[mxPid-1]+p);
			}
		}
		MWNumericArray resw=decr(encResW);
		res=MatComputeHelper.mul(Q, resw);
	}

	@Override
	public void loadData(Object obj) {
		Map<String,Object> mp=(Map<String, Object>) obj;
		X0 = (MWNumericArray) mp.get("DataPiece");
		n=X0.getDimensions()[0];
		d=X0.getDimensions()[1];
		if (pid+1==algInfo.getMxPid()) {
			y = (MWNumericArray) mp.get("DataDecision");
		}
	}

	@Override
	public Object showResult() {
		return res;
	}
}
