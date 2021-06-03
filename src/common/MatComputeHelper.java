package common;

import matlabAccess.MatlabHelper;

import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;

/**
 * The code is for paper <b>"Efficient Vertical Federated Learning Method for Ridge Regression of Large-Scale Samples via Least-Squares Solution"</b>. <br/>
 * Realize a series of basic matrix operations by connecting to Matlab 2017.
 */
public class MatComputeHelper {
	private static MatlabHelper helper;
	static {
		try {
			helper = new MatlabHelper();
		} catch (MWException e) {
			e.printStackTrace();
		}
	}

	public static void dispose() {
		helper.dispose();
	}

	public static MWNumericArray randn(int n, int m) {
		MWNumericArray res = null;
		try {
			Object[] objs = helper.randnAgt(1, n, m);
			res = (MWNumericArray) objs[0];
		} catch (MWException e) {
			e.printStackTrace();
		}
		return res;
	}

	public static MWNumericArray rand(int n, int m) {
		MWNumericArray res = null;
		try {
			Object[] objs = helper.randAgt(1, n, m);
			res = (MWNumericArray) objs[0];
		} catch (MWException e) {
			e.printStackTrace();
		}
		return res;
	}

	public static MWNumericArray randOrthMat(int n) {
		MWNumericArray res = null;
		try {
			Object[] objs = helper.randOrthMat(1, n);
			res = (MWNumericArray) objs[0];
		} catch (MWException e) {
			e.printStackTrace();
		}
		return res;
	}

	public static MWNumericArray eye(int n) {
		MWNumericArray res = null;
		try {
			Object[] objs = helper.eyeAgt(1, n);
			res = (MWNumericArray) objs[0];
		} catch (MWException e) {
			e.printStackTrace();
		}
		return res;
	}
	private static MWNumericArray __add(Object A,Object B) {
		MWNumericArray res = null;
		try {
			Object[] objs = helper.addAgt(1, A, B);
			res = (MWNumericArray) objs[0];
		} catch (MWException e) {
			e.printStackTrace();
		}
		return res;
	}

	public static MWNumericArray add(MWNumericArray A, MWNumericArray B) {
		return __add(A, B);
	}
	public static MWNumericArray add(double A, MWNumericArray B) {
		return __add(A, B);
	}
	public static MWNumericArray add(MWNumericArray A, double B) {
		return __add(A, B);
	}
	public static MWNumericArray add(double A, double B) {
		return __add(A, B);
	}

	private static MWNumericArray __subtract(Object A,Object B) {
		MWNumericArray res = null;
		try {
			Object[] objs = helper.subtractAgt(1, A, B);
			res = (MWNumericArray) objs[0];
		} catch (MWException e) {
			e.printStackTrace();
		}
		return res;
	}

	public static MWNumericArray subtract(MWNumericArray A, MWNumericArray B) {
		return __subtract(A, B);
	}
	public static MWNumericArray subtract(double A, MWNumericArray B) {
		return __subtract(A, B);
	}
	public static MWNumericArray subtract(MWNumericArray A, double B) {
		return __subtract(A, B);
	}
	public static MWNumericArray subtract(double A, double B) {
		return __subtract(A, B);
	}
	
	private static MWNumericArray __mul(Object A,Object B) {
		MWNumericArray res = null;
		try {
			Object[] objs = helper.mulAgt(1, A, B);
			res = (MWNumericArray) objs[0];
		} catch (MWException e) {
			e.printStackTrace();
		}
		return res;
	}

	public static MWNumericArray mul(MWNumericArray A, MWNumericArray B) {
		return __mul(A, B);
	}
	public static MWNumericArray mul(double A, MWNumericArray B) {
		return __mul(A, B);
	}
	public static MWNumericArray mul(MWNumericArray A, double B) {
		return __mul(A, B);
	}
	public static MWNumericArray mul(double A, double B) {
		return __mul(A, B);
	}

	public static MWNumericArray inv(MWNumericArray A) {
		MWNumericArray res = null;
		try {
			Object[] objs = helper.invAgt(1, A);
			res = (MWNumericArray) objs[0];
		} catch (MWException e) {
			e.printStackTrace();
		}
		return res;
	}

	public static MWNumericArray transpose(MWNumericArray A) {
		MWNumericArray res = null;
		try {
			Object[] objs = helper.transposeAgt(1, A);
			res = (MWNumericArray) objs[0];
		} catch (MWException e) {
			e.printStackTrace();
		}
		return res;
	}
	public static MWNumericArray chol(MWNumericArray A) {
		MWNumericArray res = null;
		try {
			Object[] objs = helper.cholAgt(1, A);
			res = (MWNumericArray) objs[0];
		} catch (MWException e) {
			e.printStackTrace();
		}
		return res;
	}
}
