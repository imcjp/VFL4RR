package server;

import java.io.Serializable;

/**
 * The code is for paper <b>"Efficient Vertical Federated Learning Method for Ridge Regression of Large-Scale Samples via Least-Squares Solution"</b>. <br/>
 * The class defines how to transfer data during the execution of the protocols.
 */

public class ReqFunc implements Serializable {

	private static final long serialVersionUID = 1L;
	private String sender;
	private String funcName;
	private Object[] params;
	private int retPort;
	public ReqFunc(String sender,String funcName,int retPort,Object... objs) {
		this.params=objs;
		this.funcName=funcName;
		this.retPort=retPort;
		this.sender = sender;
	}
	public String getFuncName() {
		return funcName;
	}
	public Object[] getParams() {
		return params;
	}
	public int getRetPort() {
		return retPort;
	}
	public String getSender() {
		return sender;
	}
}
