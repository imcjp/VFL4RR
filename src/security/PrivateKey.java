package security;

import java.math.BigInteger;

/**
 * The code is for paper <b>"Efficient Vertical Federated Learning Method for Ridge Regression of Large-Scale Samples via Least-Squares Solution"</b>. <br/>
 * The private key, the variable in it can decrypt the ciphertext.
 */

public class PrivateKey {
	
    private BigInteger lambda;
    public PrivateKey(BigInteger lambda) {
    	this.lambda=lambda;
	}
	public BigInteger getLambda() {
		return lambda;
	}
}
