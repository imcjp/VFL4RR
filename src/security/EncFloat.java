package security;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;

/**
 * The code is for paper <b>"Efficient Vertical Federated Learning Method for Ridge Regression of Large-Scale Samples via Least-Squares Solution"</b>. <br/>
 * The class implements floating-point digital encryption and related ciphertext calculations based on Paillier's Homomorphic encryption.
 */
public class EncFloat  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	BigInteger encNum;
	private int decpoints;
	private PublicKey pk;
	public final static int ORG_DECPOINTS=15;
	EncFloat(PublicKey pk,int decpoints) {
		// TODO Auto-generated constructor stub
		this.pk=pk;
		this.decpoints=decpoints;
	}
	public EncFloat(double num,int decpoints,PublicKey pk) {
		// TODO Auto-generated constructor stub
		this.pk=pk;
		this.decpoints=decpoints;
		BigDecimal bd=new BigDecimal(num);
		this.encNum=Encryption(bd.multiply(BigDecimal.TEN.pow(decpoints)).toBigInteger());
	}
	public EncFloat(double num,PublicKey pk) {
		// TODO Auto-generated constructor stub
		this.pk=pk;
		this.decpoints=ORG_DECPOINTS;
		BigDecimal bd=new BigDecimal(num);
		BigInteger pn=null;
		if (num==0) {
			this.decpoints=0;
			pn=BigInteger.ZERO;
		}else{
			pn=bd.multiply(BigDecimal.TEN.pow(decpoints)).toBigInteger();
	    	while (pn.compareTo(BigInteger.ZERO)==1 && pn.mod(BigInteger.TEN).compareTo(BigInteger.ZERO)==0) {
	    		pn=pn.divide(BigInteger.TEN);
	    		this.decpoints--;
			}
		}
		this.encNum=Encryption(pn);
	}
    private BigInteger Encryption(BigInteger m) {
    	int bitLength=pk.getBitLength();
    	BigInteger n=pk.getN();
    	BigInteger nsquare=pk.getNsquare();
    	BigInteger g=pk.getG();
        BigInteger r = new BigInteger(bitLength, new Random());
        return g.modPow(m, nsquare).multiply(r.modPow(n, nsquare)).mod(nsquare);

    }
    public BigInteger Decryption(BigInteger c,PrivateKey priKey) {
    	BigInteger n=pk.getN();
    	BigInteger nsquare=pk.getNsquare();
    	BigInteger g=pk.getG();
    	BigInteger lambda=priKey.getLambda();
        BigInteger u = g.modPow(lambda, nsquare).subtract(BigInteger.ONE).divide(n).modInverse(n);
        return c.modPow(lambda, nsquare).subtract(BigInteger.ONE).divide(n).multiply(u).mod(n);
    }
	public EncFloat add(double num) {
		BigDecimal bd=new BigDecimal(num);
		BigInteger pn=null;
		int decpoints=0;
		if (num==0) {
			pn=BigInteger.ZERO;
		}else{
			decpoints=Math.max(ORG_DECPOINTS, this.decpoints);
			pn=bd.multiply(BigDecimal.TEN.pow(decpoints)).toBigInteger();
	    	while (pn.compareTo(BigInteger.ZERO)==1 && decpoints>this.decpoints && pn.mod(BigInteger.TEN).compareTo(BigInteger.ZERO)==0) {
	    		pn=pn.divide(BigInteger.TEN);
	    		decpoints--;
			}
		}
		EncFloat encB=new EncFloat(this.pk,decpoints);
		encB.encNum=pk.getG().modPow(pn, pk.getNsquare());
		return add(encB);
	}
	public EncFloat add(EncFloat b) {
		BigInteger aencNum=encNum;
		BigInteger bencNum=b.encNum;
		if(b.decpoints<decpoints){
	    	BigInteger nsquare=pk.getNsquare();
	    	bencNum=bencNum.modPow(BigInteger.TEN.pow(decpoints-b.decpoints), nsquare);
		}else if(b.decpoints>decpoints){
	    	BigInteger nsquare=pk.getNsquare();
	    	aencNum=aencNum.modPow(BigInteger.TEN.pow(b.decpoints-decpoints), nsquare);
		}
		EncFloat res=new EncFloat(pk,Math.max(decpoints, b.decpoints));
		res.encNum = aencNum.multiply(bencNum).mod(pk.getNsquare());
		return res;
	}
	public EncFloat subtract(double b) {
		return add(-b);
	}
	public EncFloat subtract(EncFloat b) {
		BigInteger aencNum=encNum;
		BigInteger bencNum=b.encNum;
    	BigInteger nsquare=pk.getNsquare();
		if(b.decpoints<decpoints){
	    	bencNum=bencNum.modPow(BigInteger.TEN.pow(decpoints-b.decpoints), nsquare);
		}else if(b.decpoints>decpoints){
	    	aencNum=aencNum.modPow(BigInteger.TEN.pow(b.decpoints-decpoints), nsquare);
		}
		EncFloat res=new EncFloat(pk,Math.max(decpoints, b.decpoints));
		res.encNum = aencNum.multiply(bencNum.modInverse(nsquare)).mod(pk.getNsquare());
		return res;
	}
	public EncFloat mul(int b) {
    	return mul(new BigInteger(b+""));
	}
	public EncFloat mul(BigInteger b) {
    	BigInteger nsquare=pk.getNsquare();
    	EncFloat res=new EncFloat(pk,decpoints);
    	res.encNum = encNum.modPow(b, nsquare);
    	return res;
	}
	public EncFloat mul(double b) {
		BigDecimal bd=new BigDecimal(b);
		return mul(bd);
	}
	public EncFloat mul(BigDecimal b) {
    	BigInteger nsquare=pk.getNsquare();
		int decpoints1=ORG_DECPOINTS;
    	BigInteger pn=b.multiply(BigDecimal.TEN.pow(decpoints1)).toBigInteger();
    	while (pn.compareTo(BigInteger.ZERO)!=0 && pn.mod(BigInteger.TEN).compareTo(BigInteger.ZERO)==0) {
    		pn=pn.divide(BigInteger.TEN);
			decpoints1--;
		}
    	EncFloat res =new EncFloat(pk,decpoints+ decpoints1);
    	res.encNum = encNum.modPow(pn, nsquare);
		return res;
	}
	public EncFloat divide(double b) {
		return mul(1.0/b);
	}
	public EncFloat divide(BigDecimal b) {
		return mul(BigDecimal.ONE.divide(b));
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuilder sb=new StringBuilder();
		sb.append("{\"encNum\":"+encNum);
		sb.append(",\"decpoints\":"+decpoints+"}");
		return sb.toString();
	}
	
	public String toDecrString(PrivateKey priKey) {
		return getDecrNum(priKey).toString();
	}
	public BigDecimal getDecrNum(PrivateKey priKey) {
		// TODO Auto-generated method stub
		BigInteger dec1=Decryption(encNum, priKey);
		if (dec1.compareTo(pk.getN().divide(new BigInteger("2")))>0) {
			dec1=dec1.subtract(pk.getN());
		}
		BigDecimal res=new BigDecimal(dec1);
		res=res.divide(new BigDecimal(10).pow(decpoints));
		return res;
	}
	public BigInteger getEncNum() {
		return encNum;
	}
	public int getDecpoints() {
		return decpoints;
	}
	public PublicKey getPk() {
		return pk;
	}

	EncFloat __addDecpoints(int bias) {
		EncFloat res=new EncFloat(pk,decpoints+bias);
		res.encNum=this.encNum.modPow(BigInteger.TEN.pow(bias), pk.getNsquare());
		return res;
	}
	public EncFloat __mul(BigInteger pn,int decpoints1) {
    	BigInteger nsquare=pk.getNsquare();
    	EncFloat res =new EncFloat(pk,decpoints+ decpoints1);
    	res.encNum = encNum.modPow(pn, nsquare);
		return res;
	}
}
