package server;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
/**
 * The code is for paper <b>"Efficient Vertical Federated Learning Method for Ridge Regression of Large-Scale Samples via Least-Squares Solution"</b>. <br/>
 * For some that require two or more participants to complete the agreement together, we designed a room for them to join. 
 * When all participants join the room, these protocols will be executed.
 * This class achieve the above process.
 */

public abstract class RoomForProtocol<T> implements Runnable{
	protected Coordinator agent;
	@SuppressWarnings("rawtypes")
	private static Map<String, RoomForProtocol> mp=new HashMap<String, RoomForProtocol>();
	public static void addPlayer(Coordinator agent,Class<? extends RoomForProtocol> cls,int id,String playerName,Object obj) {
		String name=cls.getName()+"#"+agent+"#"+id;
		if (!mp.containsKey(name)) {
			synchronized (RoomForProtocol.class) {
				if (!mp.containsKey(name)) {
					try {
						RoomForProtocol room=cls.newInstance();
						room.agent=agent;
						mp.put(name, room);
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}
		synchronized (RoomForProtocol.class) {
			@SuppressWarnings("rawtypes")
			RoomForProtocol room=mp.get(name);
			room.add(playerName, obj);
		}
	}
	protected int maxPlayer=0;
	private int player=0;
	protected List<String> players=new ArrayList<String>();
	protected List<T> objs=new ArrayList<T>();
			
	public RoomForProtocol(){
		init();
	}
	protected abstract void init();
	public void add(String playerName,T obj) {
		synchronized (this) {
			players.add(playerName);
			objs.add(obj);
			player++;
			if (player==maxPlayer) {
				run();
			}
		}
	}
}
