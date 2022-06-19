package algs.cvrg;

import java.util.Map;

import model.Message;
import security.EncMat;
//import security.EncMatMultThread;
import alg.AlgInfo;
import client.ClientForParticipant;

import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWNumericArray;

import common.MatComputeHelper;
import common.RandNumGenerator;
/**
 * The code is for paper <b>"Edge Computing Aided Coded Vertical Federated Linear Regression"</b>. <br/>
 * This class is the Coordination for CVRG algorithm.
 */
public class Party2 extends ClientForParticipant{
	private AlgInfo algInfo;
	private EdgeComputingNodes ew=null;
	public Party2(AlgInfo algInfo) {
		super(algInfo.getParty(2), algInfo.getCoordinator());
		this.algInfo=algInfo;
		this.ew=new EdgeComputingNodes(6);
	}

	private MWNumericArray X0 = null;
	private MWNumericArray y = null;
	private int n;
	private int d;
	private MWNumericArray w = null;
	private MWNumericArray g = null;
	private MWNumericArray gBak = null;
	private MWNumericArray wBak = null;
	
	private MWNumericArray m = null;
	private MWNumericArray v = null;
	
	@Override
	public void run() {
		Message msg=null;
		msg=waitMessage(21);
		EncMat encua=(EncMat) msg.getObj();
		msg=waitMessage(21);
		EncMat encLa=(EncMat) msg.getObj();
		MWNumericArray X=X0;
		MWNumericArray Xt = MatComputeHelper.transpose(X);
		MWNumericArray u=ew.edgeMatVecMul(X, w);
		MWNumericArray db=MatComputeHelper.subtract(u, y);
		EncMat encdb=new EncMat(db, pubKey);
		MWNumericArray Lb=MatComputeHelper.mul(MatComputeHelper.transpose(db), db);
		Lb=MatComputeHelper.add(Lb, MatComputeHelper.mul(algInfo.getLambda(),MatComputeHelper.mul(MatComputeHelper.transpose(w), w)));
		EncMat encLab=EncMat.mul2(MatComputeHelper.transpose(MatComputeHelper.mul(2,db)), encua);
		EncMat encL=encLa.add(Lb).add(encLab);
		sendMessage(algInfo.getParty(1), 21, encdb);
		sendMessage(algInfo.getCoordinator(), 21, encL);
		EncMat encd=encdb.add(encua);
		MWNumericArray wt=MatComputeHelper.mul(algInfo.getLambda(), w);
		EncMat [] encgn=ew.edgeMatVecEncMul(Xt, encd);
		MWNumericArray gn[]=new MWNumericArray[encgn.length];
		for (int i = 0; i < encgn.length; i++) {
			gn[i]=decr(encgn[i]);
		}
		g=ew.decode(gn, d);
		g=MatComputeHelper.add(g, wt);
		g=MatComputeHelper.mul(1.0/n, g);
	}

	public void update(int iter) {
		double beta1=algInfo.getBeta1();
		double beta2=algInfo.getBeta2();
		double learningRate=algInfo.getLearningRate();
		double epsilon=algInfo.getEpsilon();
		double g2n[]=g.getDoubleData();
		for (int i = 0; i < g2n.length; i++) {
			g2n[i]=g2n[i]*g2n[i];
		}
		MWNumericArray g2 = MatComputeHelper.transpose(new MWNumericArray(g2n, MWClassID.DOUBLE));
		if (m==null) {
			m=MatComputeHelper.mul(1-beta1, g);
		}else {
			m=MatComputeHelper.add(MatComputeHelper.mul(beta1, m), MatComputeHelper.mul(1-beta1, g));
		}
		if (v==null) {
			v=MatComputeHelper.mul(1-beta2, g2);
		}else {
			v=MatComputeHelper.add(MatComputeHelper.mul(beta2, v), MatComputeHelper.mul(1-beta2, g2));
		}
		MWNumericArray mHat=MatComputeHelper.mul(m, 1/(1-Math.pow(beta1, iter)));
		MWNumericArray vHat=MatComputeHelper.mul(v, 1/(1-Math.pow(beta2, iter)));
		double [] mHatData=mHat.getDoubleData();
		double [] vHatData=vHat.getDoubleData();
		double [] dtWData=new double[mHatData.length];
		for (int i = 0; i < mHatData.length; i++) {
			dtWData[i]=learningRate*mHatData[i]/(Math.sqrt(vHatData[i])+epsilon);
		}
		MWNumericArray dtW=MatComputeHelper.transpose(new MWNumericArray(dtWData, MWClassID.DOUBLE));
		wBak=w;
		w=MatComputeHelper.subtract(w, dtW);
		gBak=g;
	}
	public void rollback() {
		w=wBak;
		g=gBak;		
	}
	@Override
	public void loadData(Object obj) {
		Map<String,Object> mp=(Map<String, Object>) obj;
		X0 = (MWNumericArray) mp.get("DataPiece");
		n=X0.getDimensions()[0];
		d=X0.getDimensions()[1];
		y = (MWNumericArray) mp.get("DataDecision");
		w=MatComputeHelper.mul(algInfo.getInitBias(), RandNumGenerator.getRandn(d, 1));
		m=null;
		v=null;
	}

	@Override
	public Object showResult() {
		return w;
	}

}
