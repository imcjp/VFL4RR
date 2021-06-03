package model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import common.Common;
import common.ObjCounter;
/**
 * The code is for paper <b>"Efficient Vertical Federated Learning Method for Ridge Regression of Large-Scale Samples via Least-Squares Solution"</b>. <br/>
 * The class simulates data transmission between multiple participants, similar to real Internet transmission.
 */
public class NetCloud {
	private static Map<String,Map<Integer, BlockingQueue<Message>>> net;
	private static Map<String,Long> transLog;
	
	static {
		net=new HashMap<String, Map<Integer,BlockingQueue<Message>>>();
		transLog=new HashMap<String, Long>();
	}
	static void logTrans(String sender,String target,long sz){
		String s1=sender+"==>"+target;
        synchronized (NetCloud.class) {
            if (!transLog.containsKey(s1)) {
            	transLog.put(s1, sz);
            }else{
            	long sz0=transLog.get(s1);
            	transLog.put(s1, sz+sz0);
            }
        }
	}

	public static void initTrans(){
		transLog=new HashMap<String, Long>();
		net=new HashMap<String, Map<Integer,BlockingQueue<Message>>>();
	}
	
	public static long showTrans(){
		System.out.println("\tthe trans size is:");
		long res=0;
		for (Entry<String, Long> ent : transLog.entrySet()) {
			String key=ent.getKey();
			String [] ss=key.split("==>");
			String sender=ss[0];
			String target=ss[1];
			System.out.println("\t\t"+sender+" send message to "+target+", the File Size is "+ent.getValue());
			res+=ent.getValue();
		}
		return res;
	}
	public static long getAllTrans(){
		long res=0;
		for (Entry<String, Long> ent : transLog.entrySet()) {
			res+=ent.getValue();
		}
		return res;
	}
	static boolean registRole(String name){
		boolean res=true;
        if (!net.containsKey(name)) {
            synchronized (NetCloud.class) {
                if (!net.containsKey(name)) {
                	net.put(name, new HashMap<Integer, BlockingQueue<Message>>());
                }else{
                	res=false;
                }
            }
        }else{
        	res=false;
        }
		return res;
	}
	static void showRegistedUser(){
		Set<String> st=net.keySet();
		for (String user : st) {
			System.out.print(user+"\t");
		}
		Common.println();
	}
	static boolean sendMessage(String sender,String target,int port,Object obj) {
		if (!net.containsKey(target)) {
			Common.println("The target does not exist...");
			return false;
		}
		if (!net.containsKey(sender)) {
			Common.println("The sender does not exist...");
			return false;
		}
		if (port<0||port>=1000000000) {
			Common.println("The sending port is illegal and should be in the range of 0-999999999:");
			return false;
		}
		Message msg=new Message(sender, obj);
		BlockingQueue<Message> que=null;
        if (!net.get(target).containsKey(port)) {
            synchronized (NetCloud.class) {
                if (!net.get(target).containsKey(port)) {
                	net.get(target).put(port, new LinkedBlockingQueue<Message>());
                }
            }
        }
        que=net.get(target).get(port);
        try {
			long sz=ObjCounter.getMsgSize(obj);
//			long sz=0;
			que.put(msg);
        	Common.println(msg.getSender()+"send message to "+target+":"+port+", the File Size is "+sz);
        	logTrans(sender,target,sz);
			return true;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	static BlockingQueue<Message> getQue(String name,int port){
		BlockingQueue<Message> que=null;
		if (!net.containsKey(name)) {
			Common.println("The name("+name+") does not exist...");
			return que;
		}
		if (port<0||port>=1000000000) {
			Common.println("The sending port is illegal and should be in the range of 0-999999999:");
			return que;
		}
        if (!net.get(name).containsKey(port)) {
            synchronized (NetCloud.class) {
                if (!net.get(name).containsKey(port)) {
                	net.get(name).put(port, new LinkedBlockingQueue<Message>());
                }
            }
        }
        que=net.get(name).get(port);
        return que;
	}
	
}
