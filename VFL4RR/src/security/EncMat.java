package security;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWNumericArray;

import common.Common;

/**
 * The code is for paper <b>"Efficient Vertical Federated Learning Method for Ridge Regression of Large-Scale Samples via Least-Squares Solution"</b>. <br/>
 * The class extends EncFloat to matrix and realizes the related ciphertext operations of some matrix operations.
 */
public class EncMat implements Serializable{
	private static final long serialVersionUID = 1L;
	protected int n,m;
	protected EncFloat data[][]=null;
	protected PublicKey pk;
	public EncMat(MWNumericArray mat,PublicKey pubKey) {
        double[][] dn=(double[][]) mat.toDoubleArray();
		__initData(dn, pubKey);
	}
	public EncMat(double[][] dn,PublicKey pubKey) {
		__initData(dn, pubKey);
	}
	protected void __initData(double[][] dn,PublicKey pubKey){
        this.pk=pubKey;
		this.n=dn.length;
		if (this.n==0) {
			this.m=0;
		}else{
			this.m=dn[0].length;
			this.data=new EncFloat[this.n][this.m];
			for (int i = 0; i < this.n; i++) {
				for (int j = 0; j < this.m; j++) {
					this.data[i][j]=new EncFloat(dn[i][j], this.pk);
				}
			}
		}
	}
	protected EncMat(int n,int m,PublicKey pubKey) {
        this.pk=pubKey;
		this.n=n;
		this.m=m;
		this.data=new EncFloat[this.n][this.m];
	}
	public EncMat add(double num) {
		if (num==0) {
			return this;
		}
		BigDecimal bd=new BigDecimal(num);
		BigInteger pn=null;
		int decpoints=0;
		if (num==0) {
			pn=BigInteger.ZERO;
		}else{
			decpoints=EncFloat.ORG_DECPOINTS;
			pn=bd.multiply(BigDecimal.TEN.pow(decpoints)).toBigInteger();
	    	while (pn.compareTo(BigInteger.ZERO)==1 && pn.mod(BigInteger.TEN).compareTo(BigInteger.ZERO)==0) {
	    		pn=pn.divide(BigInteger.TEN);
	    		decpoints--;
			}
		}
		EncFloat encB=new EncFloat(this.pk,decpoints);
		encB.encNum=pk.getG().modPow(pn, pk.getNsquare());
		return add(encB);
	}
	public EncMat sum() {
        EncMat res=new EncMat(1, 1, this.pk);
        EncFloat ef=null;
		for (int i = 0; i < this.n; i++) {
			for (int j = 0; j < this.m; j++) {
				if (ef==null) {
					ef=this.data[i][j];
				}else{
					ef=ef.add(this.data[i][j]);
				}
			}
		}
		res.data[0][0]=ef;
		return res;
	}
	public EncMat add(EncFloat eb) {
        EncMat res=new EncMat(this.n, this.m, this.pk);
        Map<Integer, EncFloat> mp=new HashMap<Integer, EncFloat>();
		for (int i = 0; i < this.n; i++) {
			for (int j = 0; j < this.m; j++) {
				if(eb.getDecpoints()<this.data[i][j].getDecpoints()){
					int bias=this.data[i][j].getDecpoints()-eb.getDecpoints();
					if (!mp.containsKey(bias)) {
						EncFloat ef2=eb.__addDecpoints(bias);
						mp.put(bias, ef2);
					}
					res.data[i][j]=this.data[i][j].add(mp.get(bias));
				}else{
					res.data[i][j]=this.data[i][j].add(eb);
				}
			}
		}        
		return res;
	}
	public EncMat getSubRow(int s,int t) {
		if (s<0||t>this.n||s>=t) {
			return null;
		}
        EncMat res=new EncMat(t-s, this.m, this.pk);
        int p=0;
		for (int i = s; i < t; i++) {
			for (int j = 0; j < this.m; j++) {
				res.data[p][j]=this.data[i][j];
			}
			p++;
		}        
		return res;
	}
	public EncMat getSubCol(int s,int t) {
		if (s<0||t>this.m||s>=t) {
			return null;
		}
        EncMat res=new EncMat(this.n, t-s, this.pk);
		for (int i = 0; i < this.n; i++) {
	        int p=0;
			for (int j = s; j < t; j++) {
				res.data[i][p]=this.data[i][j];
				p++;
			}
		}        
		return res;
	}
	public EncMat add2(EncFloat eb) {
        EncMat res=new EncMat(this.n, this.m, this.pk);
		for (int i = 0; i < this.n; i++) {
			for (int j = 0; j < this.m; j++) {
				res.data[i][j]=this.data[i][j].add(eb);
			}
		}        
		return res;
	}
	public EncMat add(MWNumericArray mat) {
        double[][] dn=(double[][]) mat.toDoubleArray();
		if (this.n != dn.length || this.m != dn[0].length )
			return null;
        EncMat res=new EncMat(this.n, this.m, this.pk);
		for (int i = 0; i < this.n; i++) {
			for (int j = 0; j < this.m; j++) {
				res.data[i][j]=this.data[i][j].add(dn[i][j]);
			}
		}        
		return res;
	}
	public static EncMat vstack(EncMat... mats) {
		if (mats.length==1) {
			return mats[0];
		}
		return mats[0].__vstack(mats);
	}
	protected EncMat __vstack(EncMat... mats) {
		int m=mats[0].m;
		int n=mats[0].n;
		for (int i = 1; i < mats.length; i++) {
			if (mats[i].m!=m) {
				return null;
			}
			n+=mats[i].n;
		}
        EncMat res=new EncMat(n, m, mats[0].pk);
		int p=0;
		for (int k = 0; k < mats.length; k++) {
			for (int i = 0; i < mats[k].n; i++) {
				for (int j = 0; j < m; j++) {
					res.data[i+p][j]=mats[k].data[i][j];
				}
			}
			p+=mats[k].n;
		}
		return res;
	}
	public static EncMat hstack(EncMat... mats) {
		return mats[0].__hstack(mats);
	}
	protected EncMat __hstack(EncMat... mats) {
		int m=mats[0].m;
		int n=mats[0].n;
		for (int i = 1; i < mats.length; i++) {
			if (mats[i].n!=n) {
				return null;
			}
			m+=mats[i].m;
		}
        EncMat res=new EncMat(n, m, mats[0].pk);
		int p=0;
		for (int k = 0; k < mats.length; k++) {
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < mats[k].m; j++) {
					res.data[i][j+p]=mats[k].data[i][j];
				}
			}
			p+=mats[k].m;
		}
		return res;
	}
	public EncMat add(EncMat b) {
		if (this.n != b.n || this.m != b.m )
			return null;
        EncMat res=new EncMat(this.n, this.m, this.pk);
		for (int i = 0; i < this.n; i++) {
			for (int j = 0; j < this.m; j++) {
				res.data[i][j]=this.data[i][j].add(b.data[i][j]);
			}
		}        
		return res;
	}
	public EncMat subtract(double b) {
		if (b==0) {
			return this;
		}
		return add(-b);
	}
	public EncMat subtract(EncFloat eb) {
		return add(eb.mul(-1));
	}
	public EncMat subtract(MWNumericArray mat) {
        double[][] dn=(double[][]) mat.toDoubleArray();
		if (this.n != dn.length || this.m != dn[0].length )
			return null;
        EncMat res=new EncMat(this.n, this.m, this.pk);
		for (int i = 0; i < this.n; i++) {
			for (int j = 0; j < this.m; j++) {
				res.data[i][j]=this.data[i][j].subtract(dn[i][j]);
			}
		}        
		return res;
	}
	public EncMat subtract(EncMat b) {
		if (this.n != b.n || this.m != b.m )
			return null;
        EncMat res=new EncMat(this.n, this.m, this.pk);
		for (int i = 0; i < this.n; i++) {
			for (int j = 0; j < this.m; j++) {
				res.data[i][j]=this.data[i][j].subtract(b.data[i][j]);
			}
		}        
		return res;
	}
	public EncMat mul(double b) {
		if (b==1) {
			return this;
		}
		BigDecimal bd=new BigDecimal(b);
		return mul(bd);
	}
	public EncMat mul(BigDecimal b) {
		if (b.compareTo(BigDecimal.ONE)==0) {
			return this;
		}
		int decpoints1=EncFloat.ORG_DECPOINTS;
    	BigInteger pn=b.multiply(BigDecimal.TEN.pow(decpoints1)).toBigInteger();
    	while (pn.compareTo(BigInteger.ZERO)!=0 && pn.mod(BigInteger.TEN).compareTo(BigInteger.ZERO)==0) {
    		pn=pn.divide(BigInteger.TEN);
			decpoints1--;
		}
        EncMat res=new EncMat(this.n, this.m, this.pk);
		for (int i = 0; i < this.n; i++) {
			for (int j = 0; j < this.m; j++) {
				res.data[i][j]=this.data[i][j].__mul(pn, decpoints1);
			}
		}        
		return res;
	}
	public EncMat mul(MWNumericArray mat) {
        double[][] dn=(double[][]) mat.toDoubleArray();
		if (this.m != dn.length)
			return null;
        EncMat res=new EncMat(this.n, dn[0].length, this.pk);
        for (int j = 0; j < dn[0].length; j++) {
        	boolean xx=false;
			for (int k = 0; k < dn.length; k++) {
				double t1=dn[k][j];
				if (t1!=0) {
					BigDecimal b=new BigDecimal(t1);
					int decpoints1=EncFloat.ORG_DECPOINTS;
			    	BigInteger pn=b.multiply(BigDecimal.TEN.pow(decpoints1)).toBigInteger();
			    	while (pn.compareTo(BigInteger.ZERO)!=0 && pn.mod(BigInteger.TEN).compareTo(BigInteger.ZERO)==0) {
			    		pn=pn.divide(BigInteger.TEN);
						decpoints1--;
					}
					if (!xx) {
						for (int i = 0; i < this.n; i++) {
							res.data[i][j]=this.data[i][k].__mul(pn, decpoints1);
						}
						xx=true;
					}else{
						for (int i = 0; i < this.n; i++) {
							res.data[i][j]=res.data[i][j].add(this.data[i][k].__mul(pn, decpoints1));
						}						
					}
				}
			}
			if (!xx) {
				for (int i = 0; i < this.n; i++) {
					res.data[i][j]=new EncFloat(0.0, pk);
				}		
			}
		}
		return res;
	}
	public static EncMat mul2(MWNumericArray mat,EncMat bMat) {
		return bMat.__leftMul(mat);
	}
	protected EncMat __leftMul(MWNumericArray mat) {
        double[][] dn=(double[][]) mat.toDoubleArray();
		if (dn[0].length != this.n)
			return null;
        EncMat res=new EncMat(dn.length, this.m, this.pk);
        for (int i = 0; i < dn.length; i++) {
        	boolean xx=false;
			for (int k = 0; k < this.n; k++) {
				double t1=dn[i][k];
				if (t1!=0) {
					BigDecimal b=new BigDecimal(t1);
					int decpoints1=EncFloat.ORG_DECPOINTS;
			    	BigInteger pn=b.multiply(BigDecimal.TEN.pow(decpoints1)).toBigInteger();
			    	while (pn.compareTo(BigInteger.ZERO)!=0 && pn.mod(BigInteger.TEN).compareTo(BigInteger.ZERO)==0) {
			    		pn=pn.divide(BigInteger.TEN);
						decpoints1--;
					}
					if (!xx) {
						for (int j = 0; j < this.m; j++) {
							res.data[i][j]=this.data[k][j].__mul(pn, decpoints1);
						}
						xx=true;
					}else{
						for (int j = 0; j < this.m; j++) {
							res.data[i][j]=res.data[i][j].add(this.data[k][j].__mul(pn, decpoints1));
						}						
					}
				}
			}
			if (!xx) {
				for (int j = 0; j < this.m; j++) {
					res.data[i][j]=new EncFloat(0.0, this.pk);
				}
			}
		}
		return res;
	}
	public static EncMat mul2(EncMat aMat,MWNumericArray mat) {      
		return aMat.mul(mat);
	}
	public EncMat dotMul(MWNumericArray mat) {
        double[][] dn=(double[][]) mat.toDoubleArray();
		if (this.n != dn.length || this.m != dn[0].length )
			return null;
        EncMat res=new EncMat(this.n, this.m, this.pk);
		for (int i = 0; i < this.n; i++) {
			for (int j = 0; j < this.m; j++) {
				res.data[i][j]=this.data[i][j].mul(dn[i][j]);
			}
		}        
		return res;
	}
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		for (int i = 0; i < this.n; i++) {
			for (int j = 0; j < this.m; j++) {
				if (j>0) {
					sb.append("\t");
				}
				sb.append(this.data[i][j].toString());
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	public String toDecrString(PrivateKey priKey) {
		BigDecimal[][] res=getDecrMat(priKey);
		StringBuilder sb=new StringBuilder();
		for (int i = 0; i < res.length; i++) {
			for (int j = 0; j < res[0].length; j++) {
				if (j>0) {
					sb.append("\t");
				}
				sb.append(res[i][j].toString());
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	public BigDecimal[][] getDecrMat(PrivateKey priKey) {
		// TODO Auto-generated method stub
		BigDecimal[][] res=new BigDecimal[this.n][this.m];
		for (int i = 0; i < res.length; i++) {
			for (int j = 0; j < res[0].length; j++) {
				res[i][j]=this.data[i][j].getDecrNum(priKey);
			}
		}
		return res;
	}
	public MWNumericArray getDecrMW(PrivateKey priKey) {
		BigDecimal[][] bgn=getDecrMat(priKey);
		double dn[][]=new double[this.n][this.m];
		for (int i = 0; i < dn.length; i++) {
			for (int j = 0; j < dn[0].length; j++) {
				dn[i][j]=bgn[i][j].doubleValue();
			}
		}
		MWNumericArray res = new MWNumericArray(dn, MWClassID.DOUBLE);
		return res;
	}
	public EncMat transpose() {
		EncMat res=new EncMat(this.m, this.n, this.pk);
		for (int i = 0; i < this.n; i++) {
			for (int j = 0; j < this.m; j++) {
				res.data[j][i]=this.data[i][j];
			}
		}
		return res;
	}
	///////////////////////////////////////////////////////////////////
	private static double ticTime = System.nanoTime();
	public static void tic() {
		ticTime = System.nanoTime();
	}
	public static double toc() {
		return toc(true);
	}
	public static double toc(boolean isPrint) {
		double t = (Math.max(0, System.nanoTime() - ticTime)) / 1000000.0;
		if (isPrint) {
			Common.println("time passed: " + t + "ms");
		}
		return t;
	}
	public int getN() {
		return n;
	}
	public int getM() {
		return m;
	}
	public PublicKey getPk() {
		return pk;
	}
	///////////////////////////////////////////////////////////////////
	public static EncMat readFromTxt(String fname,PublicKey pubKey) {
		File fin=new File(fname);
		EncMat res=null;
		try {
			@SuppressWarnings("resource")
			Scanner cin=new Scanner(fin);
			int n,m;
			n=cin.nextInt();
			m=cin.nextInt();
			double[][] dt=new double[n][m];
			for (int i = 0; i < dt.length; i++) {
				for (int j = 0; j < dt[0].length; j++) {
					dt[i][j]=cin.nextDouble();
				}
			}
			res=new EncMat(dt, pubKey);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	public static MWNumericArray readFromTxt(String fname) {
		File fin=new File(fname);
		MWNumericArray res=null;
		try {
			@SuppressWarnings("resource")
			Scanner cin=new Scanner(fin);
			int n,m;
			n=cin.nextInt();
			m=cin.nextInt();
			double[][] dt=new double[n][m];
			for (int i = 0; i < dt.length; i++) {
				for (int j = 0; j < dt[0].length; j++) {
					dt[i][j]=cin.nextDouble();
				}
			}
			res = new MWNumericArray(dt, MWClassID.DOUBLE);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return res;
	}
	public static MWNumericArray[] readFromTxt(String fname,int[] dim) {
		int[] sdim=new int[dim.length+1];
		sdim[0]=0;
		for (int i = 0; i < dim.length; i++) {
			sdim[i+1]=sdim[i]+dim[i];
		}
		File fin=new File(fname);
		MWNumericArray[] res=new MWNumericArray[dim.length];
		try {
			@SuppressWarnings("resource")
			Scanner cin=new Scanner(fin);
			int n,m;
			n=cin.nextInt();
			m=cin.nextInt();
			double[][] dt=new double[n][m];
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < dt[0].length; j++) {
					dt[i][j]=cin.nextDouble();
				}
			}
			double[][] dt1=null;
			for (int k = 0; k < dim.length; k++) {
				int n0=dim[k];
				dt1=new double[n][n0];
				for (int i = 0; i < n; i++) {
					for (int j = 0; j < n0; j++) {
						dt1[i][j]=dt[i][j+sdim[k]];
					}
				}
				res[k] = new MWNumericArray(dt1, MWClassID.DOUBLE);
			}			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return res;
	}
	public static EncFloat sn[];
}
