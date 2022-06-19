package algs.cvrg;

import security.EncMat;
import security.MatThreadAdapter;

import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWNumericArray;
import common.MatComputeHelper;

/**
 * The code is for paper <b>"Edge Computing Aided Coded Vertical Federated Linear Regression"</b>. <br/>
 * This class is the Coordination for CVRG algorithm.
 */
public class EdgeComputingNodes {
	private MWNumericArray vanMat=null;
	private MWNumericArray iVanMat=null;
	private int k=0;
	public EdgeComputingNodes(int k) {
		double[][] tmp =new double[k][k];
		for (int i = 0; i < tmp.length; i++) {
			for (int j = 0; j < tmp[0].length; j++) {
				tmp[i][j]=Math.pow(i+1.0, j);
			}
		}
		vanMat = new MWNumericArray(tmp, MWClassID.DOUBLE);
		iVanMat = MatComputeHelper.inv(vanMat);
		this.k=k;
	}
	public MWNumericArray edgeMatVecMul(MWNumericArray M, MWNumericArray x) {
		int n=M.getDimensions()[0];
		int m=M.getDimensions()[1];
		int blkSize=(int) (Math.ceil(n/((double)k)));
		int nUp=(blkSize *k);
		MWNumericArray M1=M;
		if (nUp>n) {
			M1=MatComputeHelper.matVerCatAgt(M1, MatComputeHelper.zeros(nUp-n, m));
		}
		MWNumericArray Mn[]=new MWNumericArray[k];
		int sr=1;
		int tr=blkSize;
		for (int i = 0; i < k; i++) {
			Mn[i]=MatComputeHelper.matSplitRAgt(M1, sr, tr);
			sr+=blkSize;
			tr+=blkSize;
		}
		MWNumericArray Mn2[]=new MWNumericArray[k];
		for (int i = 0; i < k; i++) {
			double [] vanVec=MatComputeHelper.matSplitRAgt(vanMat, i+1, i+1).getDoubleData();
			Mn2[i]=MatComputeHelper.mul(Mn[0], vanVec[0]);
			for (int j = 1; j < k; j++) {
				MWNumericArray tmp=MatComputeHelper.mul(Mn[j], vanVec[j]);
				Mn2[i]=MatComputeHelper.add(Mn2[i], tmp);
			}
		}
		MWNumericArray yn[]=new MWNumericArray[k];
		MatThreadAdapter mdp=new MatThreadAdapter() {
			MWNumericArray x;
			MWNumericArray yn[];
			MWNumericArray Mn2[];
			@Override
			protected int run(int id, int[] obj) {
				int i=obj[0];
				this.yn[i]=MatComputeHelper.mul(this.Mn2[i], this.x);
				return 0;
				
			}
			@Override
			public void init(Object[] objs) {
				this.x=(MWNumericArray) objs[0];
				this.Mn2=(MWNumericArray[]) objs[1];
				this.yn=(MWNumericArray[]) objs[2];
			}
		};
		Object[] objs={x,Mn2,yn};
		mdp.init(objs);
		mdp.start();
		for (int i = 0; i < k; i++) {
			int[] dt={i};
			mdp.addData(dt);
		}
		mdp.waitForCompletion();
		return decode(yn, n);
	}

	public MWNumericArray decode(MWNumericArray yn[],int n) {
		int blkSize=(int) (Math.ceil(n/((double)k)));
		MWNumericArray yn2[]=new MWNumericArray[k];
		for (int i = 0; i < k; i++) {
			double [] ivanVec=MatComputeHelper.matSplitRAgt(iVanMat, i+1, i+1).getDoubleData();
			yn2[i]=MatComputeHelper.mul(yn[0], ivanVec[0]);
			for (int j = 1; j < k; j++) {
				MWNumericArray tmp=MatComputeHelper.mul(yn[j], ivanVec[j]);
				yn2[i]=MatComputeHelper.add(yn2[i], tmp);
			}
		}
		double[] res0=new double[n];
		int p=0;
		for (int i = 0; i < k; i++) {
			double [] tmp=yn2[i].getDoubleData();
			for (int j = 0; j < blkSize&&p<n; j++) {
				res0[p++]=tmp[j];
			}
		}
		return MatComputeHelper.transpose(new MWNumericArray(res0, MWClassID.DOUBLE));
		
	}
	public EncMat [] edgeMatVecEncMul(MWNumericArray M, EncMat x) {
		int n=M.getDimensions()[0];
		int m=M.getDimensions()[1];
		int blkSize=(int) (Math.ceil(n/((double)k)));
		int nUp=(blkSize *k);
		MWNumericArray M1=M;
		if (nUp>n) {
			M1=MatComputeHelper.matVerCatAgt(M1, MatComputeHelper.zeros(nUp-n, m));
		}
		MWNumericArray Mn[]=new MWNumericArray[k];
		int sr=1;
		int tr=blkSize;
		for (int i = 0; i < k; i++) {
			Mn[i]=MatComputeHelper.matSplitRAgt(M1, sr, tr);
			sr+=blkSize;
			tr+=blkSize;
		}
		MWNumericArray Mn2[]=new MWNumericArray[k];
		for (int i = 0; i < k; i++) {
			double [] vanVec=MatComputeHelper.matSplitRAgt(vanMat, i+1, i+1).getDoubleData();
			Mn2[i]=MatComputeHelper.mul(Mn[0], vanVec[0]);
			for (int j = 1; j < k; j++) {
				MWNumericArray tmp=MatComputeHelper.mul(Mn[j], vanVec[j]);
				Mn2[i]=MatComputeHelper.add(Mn2[i], tmp);
			}
		}
		EncMat yn[]=new EncMat[k];
		MatThreadAdapter mdp=new MatThreadAdapter() {
			EncMat x;
			EncMat yn[];
			MWNumericArray Mn2[];
			@Override
			protected int run(int id, int[] obj) {
				int i=obj[0];
				this.yn[i]=EncMat.mul2(this.Mn2[i], this.x);
				return 0;
				
			}
			@Override
			public void init(Object[] objs) {
				this.x=(EncMat) objs[0];
				this.Mn2=(MWNumericArray[]) objs[1];
				this.yn=(EncMat[]) objs[2];
			}
		};
		Object[] objs={x,Mn2,yn};
		mdp.init(objs);
		mdp.start();
		for (int i = 0; i < k; i++) {
			int[] dt={i};
			mdp.addData(dt);
		}
		mdp.waitForCompletion();
		return yn;
	}

	public int getK() {
		return k;
	}
	public void setK(int k) {
		this.k = k;
	}
}
