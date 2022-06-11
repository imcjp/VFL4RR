package security;

import model.PCModelBase;
import common.Common;

/**
 * The code is for paper <b>"Efficient Vertical Federated Learning Method for Ridge Regression of Large-Scale Samples via Least-Squares Solution"</b>. <br/>
 * It is an adapter for multithreaded calculation of encryption matrix.
 */
public abstract class MatThreadAdapter extends PCModelBase<int[]>{
	public MatThreadAdapter() {
		super(Common.NUMBER_OF_THREAD_OF_MATOP);
	}

	@Override
	protected void beforRun(int id) {
		
	}

	abstract public void init(Object[] objs) ;
	
	@Override
	abstract protected int run(int id, int[] obj) ;

	@Override
	protected void afterRun(int id) {
	}

}
