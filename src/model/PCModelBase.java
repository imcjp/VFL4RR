package model;

/**
 * The code is for paper <b>"Efficient Vertical Federated Learning Method for Ridge Regression of Large-Scale Samples via Least-Squares Solution"</b>. <br/>
 * The class implements an efficient multi-threaded parallel computing process. The core idea of the process was proposed by the authors of this paper earlier.
 */
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

abstract public class PCModelBase<T> {
	final public static int CACHE_SIZE = 2;
	protected int nThread;
	private ExecutorService threadPool;
	private List<TheThread> tbs;
	// ///////////////////////////////////////////////////////////////////////
	protected int cacheSize;
	private BlockingQueue<NorMsg> freeQue;
	private List<DataStore> ls;
	// ///////////////////////////////////////////////////////////////////////
	public PCModelBase(int nThread, int cacheSize) {
		// TODO Auto-generated constructor stub
		this.cacheSize = cacheSize;
		this.nThread = nThread;
		threadPool = Executors.newFixedThreadPool(nThread);
		ls = new ArrayList<DataStore>();
		tbs = new ArrayList<TheThread>();
		for (int i = 0; i < nThread; i++) {
			DataStore ds = new DataStore(i);
			ls.add(ds);
			tbs.add(new TheThread(ds));
		}
		freeQue = new LinkedBlockingQueue<NorMsg>();
		init();
	}

	public PCModelBase(int nThread) {
		this(nThread, CACHE_SIZE);
	}

	private void init() {
		for (int i = 0; i < cacheSize; i++) {
			for (int j = 0; j < nThread; j++) {
				try {
					freeQue.put(new NorMsg(j));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	public void start() {
		for (int i = 0; i < tbs.size(); i++) {
			threadPool.execute(tbs.get(i));
		}
	}

	public void addData(T td) {
		try {
			NorMsg bean = freeQue.take();
			bean.setObj(td);
			ls.get(bean.getId()).addData(bean);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void waitForCompletion() {
		for (int i = 0; i < nThread; i++) {
			try {
				ls.get(i).addFinishedData();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		threadPool.shutdown();
		try {
			threadPool.awaitTermination(10000000, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private class DataStore {
		private int id;
		private BlockingQueue<AbsMsg> que;
		private T obj;

		int getId() {
			return id;
		}

		DataStore(int id) {
			// TODO Auto-generated constructor stub
			this.id = id;
			que = new LinkedBlockingQueue<AbsMsg>();
		}

		boolean next() {
			try {
				AbsMsg nowData = que.take();
				if (nowData instanceof PCModelBase.NorMsg) {
					@SuppressWarnings("unchecked")
					NorMsg theData = (NorMsg) nowData;
					obj = theData.getObj();
					theData.setObj(null);
					freeQue.put(theData);
					return true;
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}

		T get() {
			return obj;
		}

		void addData(NorMsg bean) throws InterruptedException {
			que.put(bean);
		}

		void addFinishedData() throws InterruptedException {
			que.put(new EndMsg());
		}
	}

	private abstract class AbsMsg {
	}

	private class NorMsg extends AbsMsg {
		private int id;
		private T obj;

		NorMsg(int id) {
			// TODO Auto-generated constructor stub
			this.id = id;
		}

		public void setObj(T obj) {
			this.obj = obj;
		}

		T getObj() {
			return obj;
		}

		public int getId() {
			return id;
		}
	}

	private class EndMsg extends AbsMsg {
	}

	private class TheThread extends Thread {
		private DataStore ds;

		TheThread(DataStore ds) {
			// TODO Auto-generated constructor stub
			this.ds = ds;
		}

		public void run() {
			// TODO Auto-generated method stub
			beforRun(ds.getId());
			while (ds.next()) {
				T obj = ds.get();
				int res = PCModelBase.this.run(ds.getId(), obj);
				if (res!=0) {
					boolean isExit = fail(ds.getId(), obj,res);
					if (isExit) {
						break;
					}
				}else{
					success(ds.getId(), obj);
				}
			}
			afterRun(ds.getId());
		}
	}

	protected boolean fail(int id, T obj,int failId) {
		System.out.println("Thread No." + id + "failed when process " + obj.toString() + ". The error id is"+failId);
		return false;
	}

	protected void success(int id, T obj) {
	}

	protected abstract void beforRun(int id);

	protected abstract int run(int id, T obj);

	protected abstract void afterRun(int id);
}