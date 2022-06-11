package security;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * The code is for paper <b>
 * "Efficient Vertical Federated Learning Method for Ridge Regression of Large-Scale Samples via Least-Squares Solution"
 * </b>. <br/>
 * The public key, where variables are used to encrypt plaintext
 */

public class PublicKey implements Serializable {

	private static final long serialVersionUID = 1L;
	private BigInteger n;
	private BigInteger nsquare;
	private BigInteger g;
	private int bitLength;

	public PublicKey(BigInteger n, BigInteger nsquare, BigInteger g,
			int bitLength) {
		this.n = n;
		this.g = g;
		this.nsquare = nsquare;
		this.bitLength = bitLength;
	}

	public BigInteger getN() {
		return n;
	}

	public BigInteger getNsquare() {
		return nsquare;
	}

	public BigInteger getG() {
		return g;
	}

	public int getBitLength() {
		return bitLength;
	}
}
