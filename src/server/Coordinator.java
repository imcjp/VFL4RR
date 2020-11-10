package server;

import com.mathworks.toolbox.javabuilder.MWNumericArray;

import model.Message;
import model.Communicator;
import security.EncMat;
import security.PaillierExpand;
import security.PrivateKey;
import security.PublicKey;


/**
 * The code is for paper <b>"Efficient Vertical Federated Learning Method for Ridge Regression of Large-Scale Samples via Least-Squares Solution"</b>. <br/>
 * The third party coordinator, who hold the private key, and starts a thread to support various protocols.
 */

public class Coordinator extends Communicator {
	private PrivateKey priKey=null;
	private PublicKey pubKey=null;
	private ProtocolThread ct;
	private boolean isRunning;
	Thread thd;
	public PublicKey getPubKey() {
		return pubKey;
	}
	public Coordinator(String name) {
		super(name);
		PaillierExpand pf=new PaillierExpand();
		this.pubKey=pf.getPublicKey();
		this.priKey=pf.getPrivateKey();
	}
	public void startThread(){
		thd=new Thread(new Runnable() {
			
			@Override
			public void run() {
				ct=new ProtocolThread(Coordinator.this,priKey);
				ct.start();
				isRunning=true;
				while (isRunning) {
					Message msg=waitMessage(80);
					ct.addData((ReqFunc) msg.getObj());
				}
			}
		});
		thd.start();
	}
	public MWNumericArray decr(EncMat mat) {
		return mat.getDecrMW(priKey);
	}
}
