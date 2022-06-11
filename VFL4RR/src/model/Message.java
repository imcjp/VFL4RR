package model;

/**
 * The code is for paper <b>"Efficient Vertical Federated Learning Method for Ridge Regression of Large-Scale Samples via Least-Squares Solution"</b>. <br/>
 * The message for one participant to another.
 */
public class Message{
	private String sender;
	private Object obj;
	public Message(String sender,Object obj) {
		this.sender=sender;
		this.obj=obj;
	}
	public String getSender() {
		return sender;
	}
	public Object getObj() {
		return obj;
	}
}