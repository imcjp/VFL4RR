package common;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWNumericArray;
/**
 * The code is for paper <b>"Efficient Vertical Federated Learning Method for Ridge Regression of Large-Scale Samples via Least-Squares Solution"</b>. <br/>
 * The common class defines some constants and some public methods involved in the project.
 */
public class Common {
	public static final int ROOMID_BIAS=10000; // The port of SEM.
	public static boolean IS_PRINT_PROCESS=false; // Whether to output the intermediate process of algorithm operation.
	public static int NUMBER_OF_THREAD_OF_MATOP=8; // Number of threads used for encryption matrix operations.
	
	//////////////////////////////////////////////////////////
	public static void print(Object obj) {
		if (IS_PRINT_PROCESS) {
			System.out.print(obj);
		}
	}
	public static void println(Object obj) {
		if (IS_PRINT_PROCESS) {
			System.out.println(obj);
		}
	}
	public static void println() {
		if (IS_PRINT_PROCESS) {
			System.out.println();
		}
	}
	public static MWNumericArray[] readFromTxt(String fname,int[] dim) {
		int[] sdim=new int[dim.length+1];
		sdim[0]=0;
		for (int i = 0; i < dim.length; i++) {
			sdim[i+1]=sdim[i]+dim[i];
		}
		File fin=new File(fname);
		MWNumericArray[] res=new MWNumericArray[dim.length];
		try {
			@SuppressWarnings("resource")
			Scanner cin=new Scanner(fin);
			int n,m;
			n=cin.nextInt();
			m=cin.nextInt();
			double[][] dt=new double[n][m];
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < dt[0].length; j++) {
					dt[i][j]=cin.nextDouble();
				}
			}
			double[][] dt1=null;
			for (int k = 0; k < dim.length; k++) {
				int n0=dim[k];
				dt1=new double[n][n0];
				for (int i = 0; i < n; i++) {
					for (int j = 0; j < n0; j++) {
						dt1[i][j]=dt[i][j+sdim[k]];
					}
				}
				res[k] = new MWNumericArray(dt1, MWClassID.DOUBLE);
			}			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return res;
	}
	public static MWNumericArray[] readFromTxt(String fname,int[] dim,int s,int t) {
		int[] sdim=new int[dim.length+1];
		sdim[0]=0;
		for (int i = 0; i < dim.length; i++) {
			sdim[i+1]=sdim[i]+dim[i];
		}
		File fin=new File(fname);
		MWNumericArray[] res=new MWNumericArray[dim.length];
		try {
			@SuppressWarnings("resource")
			Scanner cin=new Scanner(fin);
			int n,m;
			n=cin.nextInt();
			m=cin.nextInt();
			double[][] dt=new double[n][m];
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < dt[0].length; j++) {
					dt[i][j]=cin.nextDouble();
				}
			}
			double[][] dt1=null;
			for (int k = 0; k < dim.length; k++) {
				int n0=dim[k];
				dt1=new double[t-s][n0];
				int p=0;
				for (int i = s; i < t; i++) {
					for (int j = 0; j < n0; j++) {
						dt1[p][j]=dt[i][j+sdim[k]];
					}
					p++;
				}
				res[k] = new MWNumericArray(dt1, MWClassID.DOUBLE);
			}			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return res;
	}

	public static boolean deleteDir(String path) {
		File file = new File(path);
		if (!file.exists()) {// 判断是否待删除目录是否存在
			System.err.println("The dir are not exists!");
			return false;
		}

		String[] content = file.list();// 取得当前目录下所有文件和文件夹
		for (String name : content) {
			File temp = new File(path, name);
			if (temp.isDirectory()) {// 判断是否是目录
				deleteDir(temp.getAbsolutePath());// 递归调用，删除目录里的内容
				temp.delete();// 删除空目录
			} else {
				if (!temp.delete()) {// 直接删除文件
					System.err.println("Failed to delete " + name);
				}
			}
		}
		return true;
	}
	public static void finished() {
		System.exit(1);
//		while (true) {
//			Toolkit.getDefaultToolkit().beep();
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
	}
}
