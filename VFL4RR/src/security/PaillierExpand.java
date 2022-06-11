package security;

/**
 * The code is for paper <b>"Efficient Vertical Federated Learning Method for Ridge Regression of Large-Scale Samples via Least-Squares Solution"</b>. <br/>
 * The class extends the class Paillier simply.
 * Our purpose of doing this is mainly to maintain the integrity of Paillier algorithm while adding some functions.
 */

public class PaillierExpand extends Paillier{
	public PublicKey getPublicKey() {
		return new PublicKey(this.n, this.nsquare, this.g,this.bitLength);
	}

	public PrivateKey getPrivateKey() {
		return new PrivateKey(this.lambda);
	}
}
