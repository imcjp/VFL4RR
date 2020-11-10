package security;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.mathworks.toolbox.javabuilder.MWNumericArray;

import common.Common;

/**
 * The code is for paper <b>"Efficient Vertical Federated Learning Method for Ridge Regression of Large-Scale Samples via Least-Squares Solution"</b>. <br/>
 * This class extends from EncMat, which is its multi-threaded version, and more efficient. 
 * In this project, we mainly use this class instead of EncMat. 
 * In the algorithm implementation process, this class can be compatible with EncMat.
 */
public class EncMatMultThread extends EncMat{

	public EncMatMultThread(double[][] dn, PublicKey pubKey) {
		super(dn, pubKey);
		// TODO Auto-generated constructor stub
	}
	
	public EncMatMultThread(MWNumericArray mat, PublicKey pubKey) {
		super(mat, pubKey);
		// TODO Auto-generated constructor stub
	}
	@Override
	protected void __initData(double[][] dn, PublicKey pubKey) {
        this.pk=pubKey;
		this.n=dn.length;
		if (this.n==0) {
			this.m=0;
		}else{
			this.m=dn[0].length;
			this.data=new EncFloat[this.n][this.m];
	        MatThreadAdapter mdp=new MatThreadAdapter() {
	        	double[][] dn;
	            EncFloat data[][];
	            PublicKey pk;
				@Override
				protected int run(int id, int[] obj) {
					int i=obj[0];
					int j=obj[1];
					this.data[i][j]=new EncFloat(dn[i][j], pk);
					return 0;
				}
				@Override
				public void init(Object[] objs) {
					// TODO Auto-generated method stub
	        		this.dn=(double[][]) objs[0];
	        		this.data=(EncFloat[][]) objs[1];
	        		this.pk=(PublicKey) objs[2];
				}
			};
			Object[] objs={dn,this.data,this.pk};
			mdp.init(objs);
			mdp.start();
			for (int i = 0; i < this.n; i++) {
				for (int j = 0; j < this.m; j++) {
					int[] dt={i,j};
					mdp.addData(dt);
				}
			}
			mdp.waitForCompletion();
		}
	}
	public EncMatMultThread(int n, int m, PublicKey pk) {
		// TODO Auto-generated constructor stub
		super(n, m, pk);
	}

	public EncMatMultThread(EncMat mat) {
		// TODO Auto-generated constructor stub
		super(mat.n, mat.m, mat.pk);
		this.data=mat.data.clone();
	}
	
	public EncMatMultThread add(double num) {
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
        EncMatMultThread res=new EncMatMultThread(1, 1, this.pk);
        EncFloat sn[]=new EncFloat[Common.NUMBER_OF_THREAD_OF_MATOP];
        MatThreadAdapter mdp=new MatThreadAdapter() {
            EncFloat data[][];
			@Override
			protected int run(int id, int[] obj) {
				int i=obj[0];
				int j=obj[1];
				if (sn[id]==null) {
					sn[id]=data[i][j];
				}else{
					sn[id]=sn[id].add(data[i][j]);
				}
				return 0;
			}
			@Override
			public void init(Object[] objs) {
        		this.data=(EncFloat[][]) objs[0];
			}
		};
		
		Object[] objs={this.data};
		mdp.init(objs);
		mdp.start();
		for (int i = 0; i < this.n; i++) {
			for (int j = 0; j < this.m; j++) {
				int[] dt={i,j};
				mdp.addData(dt);
			}
		}
		mdp.waitForCompletion();
		EncFloat ef=sn[0];
		for (int i = 1; i < Common.NUMBER_OF_THREAD_OF_MATOP; i++) {
			if (sn[i]!=null) {
				if (ef==null) {
					ef=sn[i];
				}else{
					ef=ef.add(sn[i]);
				}
			}
		}
		res.data[0][0]=ef;
		return res;
	}
	public EncMatMultThread add(EncFloat eb) {
        EncMatMultThread res=new EncMatMultThread(this.n, this.m, this.pk);
        MatThreadAdapter mdp=new MatThreadAdapter() {
            private Map<Integer, EncFloat> mp=new HashMap<Integer, EncFloat>();
            EncFloat eb;
            EncFloat data[][];
            EncFloat res[][];
			@Override
			protected int run(int id, int[] obj) {
				int i=obj[0];
				int j=obj[1];
				if(eb.getDecpoints()<this.data[i][j].getDecpoints()){
					int bias=this.data[i][j].getDecpoints()-eb.getDecpoints();
					if (!mp.containsKey(bias)) {
						EncFloat ef2=eb.__addDecpoints(bias);
						mp.put(bias, ef2);
					}
					res[i][j]=this.data[i][j].add(mp.get(bias));
				}else{
					res[i][j]=this.data[i][j].add(eb);
				}
				
				return 0;
			}
			@Override
			public void init(Object[] objs) {
        		this.eb=(EncFloat) objs[0];
        		this.data=(EncFloat[][]) objs[1];
        		this.res=(EncFloat[][]) objs[2];
			}
		};
		Object[] objs={eb,this.data,res.data};
		mdp.init(objs);
		mdp.start();
		for (int i = 0; i < this.n; i++) {
			for (int j = 0; j < this.m; j++) {
				int[] dt={i,j};
				mdp.addData(dt);
			}
		}
		mdp.waitForCompletion();
		return res;
	}
	public EncMatMultThread add2(EncFloat eb) {
        EncMatMultThread res=new EncMatMultThread(this.n, this.m, this.pk);
		for (int i = 0; i < this.n; i++) {
			for (int j = 0; j < this.m; j++) {
				res.data[i][j]=this.data[i][j].add(eb);
			}
		}        
		return res;
	}
	public EncMatMultThread add(MWNumericArray mat) {
        double[][] dn=(double[][]) mat.toDoubleArray();
		if (this.n != dn.length || this.m != dn[0].length )
			return null;
        EncMatMultThread res=new EncMatMultThread(this.n, this.m, this.pk);
        MatThreadAdapter mdp=new MatThreadAdapter() {
        	double[][] dn;
            EncFloat data[][];
            EncFloat res[][];
			@Override
			protected int run(int id, int[] obj) {
				int i=obj[0];
				int j=obj[1];
				res[i][j]=this.data[i][j].add(dn[i][j]);
				return 0;
			}
			@Override
			public void init(Object[] objs) {
        		this.dn=(double[][]) objs[0];
        		this.data=(EncFloat[][]) objs[1];
        		this.res=(EncFloat[][]) objs[2];
				
			}
		};
		Object[] objs={dn,this.data,res.data};
		mdp.init(objs);
		mdp.start();
		for (int i = 0; i < this.n; i++) {
			for (int j = 0; j < this.m; j++) {
				int[] dt={i,j};
				mdp.addData(dt);
			}
		}
		mdp.waitForCompletion();
		return res;
	}
	public EncMatMultThread add(EncMat b0) {
		EncMatMultThread b=(EncMatMultThread) b0;
		if (this.n != b.n || this.m != b.m )
			return null;
        EncMatMultThread res=new EncMatMultThread(this.n, this.m, this.pk);
        MatThreadAdapter mdp=new MatThreadAdapter() {
            EncFloat dataB[][];
            EncFloat data[][];
            EncFloat res[][];
			@Override
			protected int run(int id, int[] obj) {
				int i=obj[0];
				int j=obj[1];
				res[i][j]=this.data[i][j].add(dataB[i][j]);
				return 0;
			}
			@Override
			public void init(Object[] objs) {
				// TODO Auto-generated method stub
        		this.dataB=(EncFloat[][]) objs[0];
        		this.data=(EncFloat[][]) objs[1];
        		this.res=(EncFloat[][]) objs[2];
				
			}
		};
		Object[] objs={b.data,this.data,res.data};
		mdp.init(objs);
		mdp.start();
		for (int i = 0; i < this.n; i++) {
			for (int j = 0; j < this.m; j++) {
				int[] dt={i,j};
				mdp.addData(dt);
			}
		}
		mdp.waitForCompletion();
		return res;
	}
	public EncMatMultThread subtract(double b) {
		if (b==0) {
			return this;
		}
		return add(-b);
	}
	public EncMatMultThread subtract(EncFloat eb) {
		return add(eb.mul(-1));
	}
	public EncMatMultThread subtract(MWNumericArray mat) {
        double[][] dn=(double[][]) mat.toDoubleArray();
		if (this.n != dn.length || this.m != dn[0].length )
			return null;
        EncMatMultThread res=new EncMatMultThread(this.n, this.m, this.pk);
        MatThreadAdapter mdp=new MatThreadAdapter() {
        	double[][] dn;
            EncFloat data[][];
            EncFloat res[][];
			@Override
			protected int run(int id, int[] obj) {
				int i=obj[0];
				int j=obj[1];
				res[i][j]=this.data[i][j].subtract(dn[i][j]);
				return 0;
			}
			@Override
			public void init(Object[] objs) {
				// TODO Auto-generated method stub
        		this.dn=(double[][]) objs[0];
        		this.data=(EncFloat[][]) objs[1];
        		this.res=(EncFloat[][]) objs[2];
				
			}
		};
		Object[] objs={dn,this.data,res.data};
		mdp.init(objs);
		mdp.start();
		for (int i = 0; i < this.n; i++) {
			for (int j = 0; j < this.m; j++) {
				int[] dt={i,j};
				mdp.addData(dt);
			}
		}
		mdp.waitForCompletion();
		return res;
	}
	public EncMatMultThread subtract(EncMat b0) {
		EncMatMultThread b=(EncMatMultThread) b0;
		if (this.n != b.n || this.m != b.m )
			return null;
        EncMatMultThread res=new EncMatMultThread(this.n, this.m, this.pk);
        MatThreadAdapter mdp=new MatThreadAdapter() {
            EncFloat dataB[][];
            EncFloat data[][];
            EncFloat res[][];
			@Override
			protected int run(int id, int[] obj) {
				int i=obj[0];
				int j=obj[1];
				res[i][j]=this.data[i][j].subtract(dataB[i][j]);
				return 0;
			}
			@Override
			public void init(Object[] objs) {
        		this.dataB=(EncFloat[][]) objs[0];
        		this.data=(EncFloat[][]) objs[1];
        		this.res=(EncFloat[][]) objs[2];
				
			}
		};
		Object[] objs={b.data,this.data,res.data};
		mdp.init(objs);
		mdp.start();
		for (int i = 0; i < this.n; i++) {
			for (int j = 0; j < this.m; j++) {
				int[] dt={i,j};
				mdp.addData(dt);
			}
		}
		mdp.waitForCompletion();
		return res;
	}
	public EncMatMultThread mul(double b) {
		if (b==1) {
			return this;
		}
		BigDecimal bd=new BigDecimal(b);
		return mul(bd);
	}
	public EncMatMultThread mul(BigDecimal b) {
		if (b.compareTo(BigDecimal.ONE)==0) {
			return this;
		}
		int decpoints1=EncFloat.ORG_DECPOINTS;
    	BigInteger pn=b.multiply(BigDecimal.TEN.pow(decpoints1)).toBigInteger();
    	while (pn.compareTo(BigInteger.ZERO)!=0 && pn.mod(BigInteger.TEN).compareTo(BigInteger.ZERO)==0) {
    		pn=pn.divide(BigInteger.TEN);
			decpoints1--;
		}
        EncMatMultThread res=new EncMatMultThread(this.n, this.m, this.pk);
        MatThreadAdapter mdp=new MatThreadAdapter() {
            EncFloat data[][];
            EncFloat res[][];
            BigInteger pn;
            int decpoints1;
			@Override
			protected int run(int id, int[] obj) {
				int i=obj[0];
				int j=obj[1];
				res[i][j]=this.data[i][j].__mul(pn, decpoints1);
				return 0;
			}
			@Override
			public void init(Object[] objs) {
        		this.pn=(BigInteger) objs[0];
        		this.data=(EncFloat[][]) objs[1];
        		this.res=(EncFloat[][]) objs[2];
        		this.decpoints1=(int) objs[3];
				
			}
		};
		Object[] objs={pn,this.data,res.data,decpoints1};
		mdp.init(objs);
		mdp.start();
		for (int i = 0; i < this.n; i++) {
			for (int j = 0; j < this.m; j++) {
				int[] dt={i,j};
				mdp.addData(dt);
			}
		}
		mdp.waitForCompletion();
		return res;
	}
	public EncMatMultThread mul(MWNumericArray mat) {
        double[][] dn=(double[][]) mat.toDoubleArray();
		if (this.m != dn.length)
			return null;
		BigInteger[][] pnn=new BigInteger[dn.length][dn[0].length];
		int[][] decpointsn1=new int[dn.length][dn[0].length];
        MatThreadAdapter mdp0=new MatThreadAdapter() {
        	double dn[][];
            BigInteger[][] pnn;
            int[][] decpointsn1;
			@Override
			protected int run(int id, int[] obj) {
				int i=obj[0];
				int j=obj[1];
				double t1=dn[i][j];
				if (t1!=0) {
					BigDecimal b=new BigDecimal(t1);
					int decpoints1=EncFloat.ORG_DECPOINTS;
			    	BigInteger pn=b.multiply(BigDecimal.TEN.pow(decpoints1)).toBigInteger();
			    	while (pn.compareTo(BigInteger.ZERO)!=0 && pn.mod(BigInteger.TEN).compareTo(BigInteger.ZERO)==0) {
			    		pn=pn.divide(BigInteger.TEN);
						decpoints1--;
					}
			    	pnn[i][j]=pn;
			    	decpointsn1[i][j]=decpoints1;
				}else{
			    	pnn[i][j]=null;
				}
				return 0;
			}
			@Override
			public void init(Object[] objs) {
        		this.pnn=(BigInteger[][]) objs[0];
        		this.dn=(double[][]) objs[1];
        		this.decpointsn1=(int[][]) objs[2];
				
			}
		};
		Object[] objs0={pnn,dn,decpointsn1};
		mdp0.init(objs0);
		mdp0.start();
		for (int i = 0; i < dn.length; i++) {
			for (int j = 0; j < dn[0].length; j++) {
				int[] dt={i,j};
				mdp0.addData(dt);
			}
		}
		mdp0.waitForCompletion();
        EncMatMultThread res=new EncMatMultThread(this.n, dn[0].length, this.pk);
        MatThreadAdapter mdp=new MatThreadAdapter() {
            EncFloat data[][];
            EncFloat res[][];
            BigInteger[][] pnn;
            int[][] decpointsn1;
            int len;
			@Override
			protected int run(int id, int[] obj) {
				int i=obj[0];
				int j=obj[1];
	        	boolean xx=false;
				for (int k = 0; k < len; k++) {
					BigInteger pn=pnn[k][j];
					if (pn!=null) {
						int decpoints1=decpointsn1[k][j];
						if (!xx) {
							res[i][j]=this.data[i][k].__mul(pn, decpoints1);
							xx=true;
						}else{
							res[i][j]=res[i][j].add(this.data[i][k].__mul(pn, decpoints1));
						}
					}
				}
				if (!xx) {
					res[i][j]=new EncFloat(0.0, pk);
				}
				return 0;
			}
			@Override
			public void init(Object[] objs) {
        		this.pnn=(BigInteger[][]) objs[0];
        		this.data=(EncFloat[][]) objs[1];
        		this.res=(EncFloat[][]) objs[2];
        		this.decpointsn1=(int[][]) objs[3];
        		this.len=(int) objs[4];
				
			}
		};
		Object[] objs={pnn,this.data,res.data,decpointsn1,dn.length};
		mdp.init(objs);
		mdp.start();
		for (int i = 0; i < res.n; i++) {
			for (int j = 0; j < res.m; j++) {
				int[] dt={i,j};
				mdp.addData(dt);
			}
		}
		mdp.waitForCompletion();
		return res;
	}
	public EncMat getSubRow(int s,int t) {
		if (s<0||t>this.n||s>=t) {
			return null;
		}
		EncMatMultThread res=new EncMatMultThread(t-s, this.m, this.pk);
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
		EncMatMultThread res=new EncMatMultThread(this.n, t-s, this.pk);
		for (int i = 0; i < this.n; i++) {
	        int p=0;
			for (int j = s; j < t; j++) {
				res.data[i][p]=this.data[i][j];
				p++;
			}
		}        
		return res;
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
		EncMatMultThread res=new EncMatMultThread(n, m, mats[0].pk);
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
	protected EncMat __hstack(EncMat... mats) {
		int m=mats[0].m;
		int n=mats[0].n;
		for (int i = 1; i < mats.length; i++) {
			if (mats[i].n!=n) {
				return null;
			}
			m+=mats[i].m;
		}
		EncMatMultThread res=new EncMatMultThread(n, m, mats[0].pk);
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
	public EncMatMultThread __leftMul(MWNumericArray mat) {
        double[][] dn=(double[][]) mat.toDoubleArray();
		if (dn[0].length != this.n)
			return null;
		BigInteger[][] pnn=new BigInteger[dn.length][dn[0].length];
		int[][] decpointsn1=new int[dn.length][dn[0].length];
        MatThreadAdapter mdp0=new MatThreadAdapter() {
        	double dn[][];
            BigInteger[][] pnn;
            int[][] decpointsn1;
			@Override
			protected int run(int id, int[] obj) {
				int i=obj[0];
				int j=obj[1];
				double t1=dn[i][j];
				if (t1!=0) {
					BigDecimal b=new BigDecimal(t1);
					int decpoints1=EncFloat.ORG_DECPOINTS;
			    	BigInteger pn=b.multiply(BigDecimal.TEN.pow(decpoints1)).toBigInteger();
			    	while (pn.compareTo(BigInteger.ZERO)!=0 && pn.mod(BigInteger.TEN).compareTo(BigInteger.ZERO)==0) {
			    		pn=pn.divide(BigInteger.TEN);
						decpoints1--;
					}
			    	pnn[i][j]=pn;
			    	decpointsn1[i][j]=decpoints1;
				}else{
			    	pnn[i][j]=null;
				}
				return 0;
			}
			@Override
			public void init(Object[] objs) {
        		this.pnn=(BigInteger[][]) objs[0];
        		this.dn=(double[][]) objs[1];
        		this.decpointsn1=(int[][]) objs[2];
				
			}
		};
		Object[] objs0={pnn,dn,decpointsn1};
		mdp0.init(objs0);
		mdp0.start();
		for (int i = 0; i < dn.length; i++) {
			for (int j = 0; j < dn[0].length; j++) {
				int[] dt={i,j};
				mdp0.addData(dt);
			}
		}
		mdp0.waitForCompletion();
        EncMatMultThread res=new EncMatMultThread(dn.length, this.m, this.pk);
        MatThreadAdapter mdp=new MatThreadAdapter() {
            EncFloat data[][];
            EncFloat res[][];
            BigInteger[][] pnn;
            int[][] decpointsn1;
            int len;
            PublicKey pk;
			@Override
			protected int run(int id, int[] obj) {
				int i=obj[0];
				int j=obj[1];
	        	boolean xx=false;
				for (int k = 0; k < len; k++) {
					BigInteger pn=pnn[i][k];
					if (pn!=null) {
						int decpoints1=decpointsn1[i][k];
						if (!xx) {
							res[i][j]=data[k][j].__mul(pn, decpoints1);
							xx=true;
						}else{
							res[i][j]=res[i][j].add(data[k][j].__mul(pn, decpoints1));
						}
					}
				}
				if (!xx) {
					res[i][j]=new EncFloat(0.0, pk);
				}
				return 0;
			}
			@Override
			public void init(Object[] objs) {
        		this.pnn=(BigInteger[][]) objs[0];
        		this.data=(EncFloat[][]) objs[1];
        		this.res=(EncFloat[][]) objs[2];
        		this.decpointsn1=(int[][]) objs[3];
        		this.len=(int) objs[4];
        		this.pk=(PublicKey) objs[5];
				
			}
		};
		Object[] objs={pnn,this.data,res.data,decpointsn1,this.n,this.pk};
		mdp.init(objs);
		mdp.start();
		for (int i = 0; i < res.n; i++) {
			for (int j = 0; j < res.m; j++) {
				int[] dt={i,j};
				mdp.addData(dt);
			}
		}
		mdp.waitForCompletion();
	
		return res;
	}
	public EncMatMultThread dotMul(MWNumericArray mat) {
        double[][] dn=(double[][]) mat.toDoubleArray();
		if (this.n != dn.length || this.m != dn[0].length )
			return null;
        EncMatMultThread res=new EncMatMultThread(this.n, this.m, this.pk);
        MatThreadAdapter mdp=new MatThreadAdapter() {
            double dn[][];
            EncFloat data[][];
            EncFloat res[][];
			@Override
			protected int run(int id, int[] obj) {
				int i=obj[0];
				int j=obj[1];
				res[i][j]=this.data[i][j].mul(dn[i][j]);
				return 0;
			}
			@Override
			public void init(Object[] objs) {
        		this.dn=(double[][]) objs[0];
        		this.data=(EncFloat[][]) objs[1];
        		this.res=(EncFloat[][]) objs[2];
				
			}
		};
		Object[] objs={dn,this.data,res.data};
		mdp.init(objs);
		mdp.start();
		for (int i = 0; i < this.n; i++) {
			for (int j = 0; j < this.m; j++) {
				int[] dt={i,j};
				mdp.addData(dt);
			}
		}
		mdp.waitForCompletion();
		return res;
	}
	public BigDecimal[][] getDecrMat(PrivateKey priKey) {
		// TODO Auto-generated method stub
		BigDecimal[][] res=new BigDecimal[this.n][this.m];
        MatThreadAdapter mdp=new MatThreadAdapter() {
            EncFloat data[][];
            BigDecimal res[][];
            PrivateKey priKey;
			@Override
			protected int run(int id, int[] obj) {
				int i=obj[0];
				int j=obj[1];
				res[i][j]=this.data[i][j].getDecrNum(priKey);
				return 0;
			}
			@Override
			public void init(Object[] objs) {
        		this.data=(EncFloat[][]) objs[0];
        		this.res=(BigDecimal[][]) objs[1];
        		this.priKey=(PrivateKey) objs[2];
				
			}
		};
		Object[] objs={this.data,res,priKey};
		mdp.init(objs);
		mdp.start();
		for (int i = 0; i < this.n; i++) {
			for (int j = 0; j < this.m; j++) {
				int[] dt={i,j};
				mdp.addData(dt);
			}
		}
		mdp.waitForCompletion();
		return res;
	}
	public EncMatMultThread transpose() {
		EncMatMultThread res=new EncMatMultThread(this.m, this.n, this.pk);
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
			System.out.println("time passed: " + t + "ms");
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
	public static EncMatMultThread readFromTxt(String fname,PublicKey pubKey) {
		File fin=new File(fname);
		EncMatMultThread res=null;
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
			res=new EncMatMultThread(dt, pubKey);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}

}
