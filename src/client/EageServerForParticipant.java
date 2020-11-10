package client;

import com.mathworks.toolbox.javabuilder.MWNumericArray;

import common.NameOfProtocols;
import common.Common;
import common.MatComputeHelper;
import security.EncMat;
import security.EncMatMultThread;
import security.PaillierExpand;
import security.PrivateKey;
import security.PublicKey;
import server.ReqFunc;
import main.ParticipantClientForVRL_MP;
import model.Message;
import model.Communicator;
import model.Stage;
/**
 * The code is for paper <b>"Efficient Vertical Federated Learning Method for Ridge Regression of Large-Scale Samples via Least-Squares Solution"</b>. <br/>
 * The class implements the calculation process of edge server for each participant.
 */
public abstract class EageServerForParticipant extends Communicator implements Runnable{
	private static final int INIT_LISTEN_PORT=100000000;
	protected PublicKey pubKey;
	private String serverName;
	public EageServerForParticipant(String name,String serverName) {
		super(name);
		this.serverName=serverName;
	}
	public EncMat mul(EncMat encA,EncMat encB) {
		return mul(encA, encB, 100);
	}
	public EncMat mul(EncMat encA,EncMat encB,double errW) {
		int port=INIT_LISTEN_PORT;
		Common.println("masking process...");
		EncMat.tic();
		MWNumericArray errA=MatComputeHelper.mul(MatComputeHelper.randn(encA.getN(), encA.getM()),errW);
		MWNumericArray errB=MatComputeHelper.mul(MatComputeHelper.randn(encB.getN(), encB.getM()),errW);
		ReqFunc rf=null;
		EncMat inA=encA.add(errA);
		EncMat inB=encB.add(errB);
		EncMat.toc();
		do {
			while (isPortLocked(port)) {
				port++;
			}
			rf=new ReqFunc(this.getName(), NameOfProtocols.MUL, port, inA,inB);
		} while (!sendMessage(serverName, 80, rf));
		Message msg= waitMessage(port);
		Common.println("unmasking process...");
		EncMat.tic();
		EncMat encC=(EncMat) msg.getObj();
		MWNumericArray errAB=MatComputeHelper.mul(errA, errB);		
		encC=encC.subtract(encA.mul(errB)).subtract(EncMat.mul2(errA, encB)).subtract(errAB);
		EncMat.toc();
		return encC;
	}
	public MWNumericArray coMul(MWNumericArray M,int taskId,int p) {
		if (p<1 ||p>2) {
			return null;
		}
		int port=INIT_LISTEN_PORT;
		int[] sz=M.getDimensions();
		ReqFunc rf=null;
		do {
			while (isPortLocked(port)) {
				port++;
			}
			rf=new ReqFunc(this.getName(), NameOfProtocols.COMUL, port, taskId,sz[0],sz[1],p);
		} while (!sendMessage(serverName, 80, rf));
		Message msg= null;
		msg= waitMessage(port);
		MWNumericArray errC=(MWNumericArray) msg.getObj();
		msg= waitMessage(port);
		Object[] obj=(Object[]) msg.getObj();
		String copper=(String) obj[0];
		int coPort=(int) obj[1];
		MWNumericArray res=null;
		if (p==1) {
			msg= waitMessage(port);
			MWNumericArray errA=(MWNumericArray) msg.getObj();
			MWNumericArray U=M;
			MWNumericArray Alpha=MatComputeHelper.subtract(U, errA);
			sendMessage(copper, coPort, Alpha);
			msg= waitMessage(port);
			MWNumericArray Beta=(MWNumericArray)msg.getObj();
			res=MatComputeHelper.add(errC,MatComputeHelper.mul(errA, Beta));
		}else{
			msg= waitMessage(port);
			MWNumericArray errB=(MWNumericArray) msg.getObj();
			MWNumericArray V=M;
			MWNumericArray Beta=MatComputeHelper.subtract(V, errB);
			sendMessage(copper, coPort, Beta);
			msg= waitMessage(port);
			MWNumericArray Alpha=(MWNumericArray)msg.getObj();
			res=MatComputeHelper.add(errC, MatComputeHelper.mul(Alpha, errB));
			res=MatComputeHelper.add(res, MatComputeHelper.mul(Alpha, Beta));
		}
		return res;
	}
	public EncMat inv(EncMat encA) {
		int port=INIT_LISTEN_PORT;
		if (encA.getN()!=encA.getM()) {
			Common.println("The matrix to be inverted must be a square matrix!!!");
			return null;
		}
		Common.println("masking process...");
		EncMat.tic();
		MWNumericArray errA=MatComputeHelper.randn(encA.getN(), encA.getM());
		EncMat inA=EncMat.mul2(errA, encA);
		EncMat.toc();
		ReqFunc rf=null;
		do {
			while (isPortLocked(port)) {
				port++;
			}
			rf=new ReqFunc(this.getName(), NameOfProtocols.INV, port, inA);
		} while (!sendMessage(serverName, 80, rf));
		Message msg= waitMessage(port);
		Common.println("unmasking process...");
		EncMat.tic();
		EncMat enciA=(EncMat) msg.getObj();
		EncMat res=enciA.mul(errA);
		EncMat.toc();
		return res;
	}
	public MWNumericArray decr(EncMat encA) {
		int port=INIT_LISTEN_PORT;
		Common.println("masking process...");
		EncMat.tic();
		double errW=100000;
		MWNumericArray errA=MatComputeHelper.mul(MatComputeHelper.randn(encA.getN(), encA.getM()),errW);
		ReqFunc rf=null;
		EncMat inA=encA.add(errA);
		EncMat.toc();
		do {
			while (isPortLocked(port)) {
				port++;
			}
			rf=new ReqFunc(this.getName(), NameOfProtocols.DECR, port, inA);
		} while (!sendMessage(serverName, 80, rf));
		Message msg= waitMessage(port);
		Common.println("unmasking process...");
		EncMat.tic();
		MWNumericArray res=(MWNumericArray) msg.getObj();
		res=MatComputeHelper.subtract(res, errA);
		EncMat.toc();
		return res;
	}
	public PublicKey getPubKey() {
		return pubKey;
	}
	public void setPubKey(PublicKey pubKey) {
		this.pubKey = pubKey;
	}
	public abstract void loadData(Object obj);

	public abstract Object showResult();
}
