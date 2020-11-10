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
}
