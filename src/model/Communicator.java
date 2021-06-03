package model;

import java.net.BindException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import common.Common;
/**
 * The code is for paper <b>"Efficient Vertical Federated Learning Method for Ridge Regression of Large-Scale Samples via Least-Squares Solution"</b>. <br/>
 * The class defines a communicator who can send messages to and receive messages from others.
 */
public class Communicator {
	private String name=null;
	private Set<Integer> busyPorts=new HashSet<Integer>();
	public Communicator(String name) {
		DateFormat df=new SimpleDateFormat("yyMMddhhmmssSSS");
		while (true) {
			if (NetCloud.registRole(name)) {
				Common.println(name+" has registed");
				this.name=name;
				break;
			}
			name="U"+df.format(new Date());
		}
	}
	public boolean sendMessage(String target,int port,Object obj) {
		return NetCloud.sendMessage(name, target, port, obj);
	}
	public Message waitMessage(int port) {
		if (!lockPort(port)) {
			try {
				throw new BindException("Port "+port+" is occupied...");
			} catch (BindException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		try {
			BlockingQueue<Message> que=NetCloud.getQue(name, port);
			if (que!=null) {
					Message msg = que.take();
		        	Common.println(getName()+"receive message from port "+port+", which comes from sender "+msg.getSender());
					return msg;
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			freePort(port);
		}
		return null;
	}
	protected synchronized boolean lockPort(int port) {
		if (busyPorts.contains(port)) {
			return false;
		}
		busyPorts.add(port);
		return true;
	}
	protected synchronized boolean freePort(int port) {
		if (!busyPorts.contains(port)) {
			return false;
		}
		busyPorts.remove(port);
		return true;
	}
	protected synchronized boolean isPortLocked(int port) {
		return busyPorts.contains(port);
	}
	public String getName() {
		return name;
	}
}
