package common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.alibaba.fastjson.JSON;
import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLDouble;
import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWNumericArray;

import exps.ExpScript;

/**
 * The code is for paper <b>
 * "Efficient Vertical Federated Learning Method for Ridge Regression of Large-Scale Samples via Least-Squares Solution"
 * </b>. <br/>
 * The common class defines some constants and some public methods involved in
 * the project.
 */
public class Common {
	public static final int ROOMID_BIAS = 10000; // The port of SEM.
	public static boolean IS_PRINT_PROCESS = false; // Whether to output the
													// intermediate process of
													// algorithm operation.
	public static int NUMBER_OF_THREAD_OF_MATOP = 8; // Number of threads used
														// for encryption matrix
														// operations.
	// X:/cjpProj/论文资料/联邦学习/code/exp1.db
	public static String DB_PATH = "X:/cjpProj/论文资料/联邦学习/code/exp1.db";

	// public static String DB_PATH="C:/Users/CJP/Desktop/WorkSpace/db/exp1.db";
	// ////////////////////////////////////////////////////////
	public static void startingExp() {
		Common.deleteDir(Common.getPath()+"objs");
		MatComputeHelper.add(1.0, 1.0);
	}

	public static void endingExp() {
		Common.deleteDir(Common.getPath()+"objs");
	}
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

	public static int[] getPartBlocks(int p, int allNum) {
		if (p <= 0 || allNum < 0) {
			System.out.println("输入参数错误");
			return null;
		}
		int res[] = new int[p + 1];
		int r = allNum / p;
		int t = allNum % p;
		res[0] = 0;
		for (int j = 0; j < p; j++) {
			res[j + 1] = r;
			if (j < t) {
				res[j + 1]++;
			}
		}
		for (int j = 1; j < res.length; j++) {
			res[j] += res[j - 1];
		}
		return res;
	}
	public static MWNumericArray[] readFromTxt(String fname, int[] dim) {
		int[] sdim = new int[dim.length + 1];
		sdim[0] = 0;
		for (int i = 0; i < dim.length; i++) {
			sdim[i + 1] = sdim[i] + dim[i];
		}
		File fin = new File(fname);
		MWNumericArray[] res = new MWNumericArray[dim.length];
		try {
			@SuppressWarnings("resource")
			Scanner cin = new Scanner(fin);
			int n, m;
			n = cin.nextInt();
			m = cin.nextInt();
			double[][] dt = new double[n][m];
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < dt[0].length; j++) {
					dt[i][j] = cin.nextDouble();
				}
			}
			double[][] dt1 = null;
			for (int k = 0; k < dim.length; k++) {
				int n0 = dim[k];
				dt1 = new double[n][n0];
				for (int i = 0; i < n; i++) {
					for (int j = 0; j < n0; j++) {
						dt1[i][j] = dt[i][j + sdim[k]];
					}
				}
				res[k] = new MWNumericArray(dt1, MWClassID.DOUBLE);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return res;
	}

	public static MWNumericArray[] readFromTxt(String fname, int[] dim, int s,
			int t) {
		int[] sdim = new int[dim.length + 1];
		sdim[0] = 0;
		for (int i = 0; i < dim.length; i++) {
			sdim[i + 1] = sdim[i] + dim[i];
		}
		File fin = new File(fname);
		MWNumericArray[] res = new MWNumericArray[dim.length];
		try {
			@SuppressWarnings("resource")
			Scanner cin = new Scanner(fin);
			int n, m;
			n = cin.nextInt();
			m = cin.nextInt();
			double[][] dt = new double[n][m];
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < dt[0].length; j++) {
					dt[i][j] = cin.nextDouble();
				}
			}
			double[][] dt1 = null;
			for (int k = 0; k < dim.length; k++) {
				int n0 = dim[k];
				dt1 = new double[t - s][n0];
				int p = 0;
				for (int i = s; i < t; i++) {
					for (int j = 0; j < n0; j++) {
						dt1[p][j] = dt[i][j + sdim[k]];
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

	public static Map<String, Object> readFromMat(String fname,
			String[] dataNames, int[] dim) {
		Map<String, Object> resMp = new HashMap<String, Object>();
		MatFileReader read = null;
		try {
			read = new MatFileReader(fname);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int[] sdim = new int[dim.length + 1];
		sdim[0] = 0;
		for (int i = 0; i < dim.length; i++) {
			sdim[i + 1] = sdim[i] + dim[i];
		}
		MWNumericArray[] res = new MWNumericArray[dim.length];
		int n, m;
		double[][] dt = (((MLDouble) (read.getMLArray(dataNames[0])))
				.getArray());
		double[][] dt1 = null;
		n = dt.length;
		m = dt[0].length;
		for (int k = 0; k < dim.length; k++) {
			int n0 = dim[k];
			dt1 = new double[n][n0];
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n0; j++) {
					dt1[i][j] = dt[i][j + sdim[k]];
				}
			}
			res[k] = new MWNumericArray(dt1, MWClassID.DOUBLE);
		}
		resMp.put(dataNames[0], res);

		dt = (((MLDouble) (read.getMLArray(dataNames[1]))).getArray());
		MWNumericArray res1 = new MWNumericArray(dt, MWClassID.DOUBLE);
		resMp.put(dataNames[1], res1);
		return resMp;
	}

	public static void main(String[] args) {
		int an[]=Common.getPartBlocks(5, 2012);
		System.out.println(JSON.toJSONString(an));
//		Map<String, Object> res = readFromMat(getPath()+"in/Superconductivty.mat",
//				new String[] { "XData", "yData" }, new int[] { 27, 27 });
//		MWNumericArray M1 = (MWNumericArray) res.get("yData");
//		System.out.println(M1);
	}

	public static Map<String, Object> readFromMat(String fname,
			String[] dataNames, int[] dim, int s, int t) {
		Map<String, Object> resMp = new HashMap<String, Object>();
		MatFileReader read = null;
		try {
			read = new MatFileReader(fname);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int[] sdim = new int[dim.length + 1];
		sdim[0] = 0;
		for (int i = 0; i < dim.length; i++) {
			sdim[i + 1] = sdim[i] + dim[i];
		}
		MWNumericArray[] res = new MWNumericArray[dim.length];
		double[][] dt = (((MLDouble) (read.getMLArray(dataNames[0])))
				.getArray());
		if (dt.length < t) {
			t = dt.length;
		}
		double[][] dt1 = null;
		for (int k = 0; k < dim.length; k++) {
			int n0 = dim[k];
			dt1 = new double[t - s][n0];
			for (int i = s; i < t; i++) {
				for (int j = 0; j < n0; j++) {
					dt1[i - s][j] = dt[i][j + sdim[k]];
				}
			}
			res[k] = new MWNumericArray(dt1, MWClassID.DOUBLE);
		}
		resMp.put(dataNames[0], res);

		dt = (((MLDouble) (read.getMLArray(dataNames[1]))).getArray());
		dt1 = new double[t - s][1];
		for (int i = s; i < t; i++) {
			dt1[i - s][0] = dt[i][0];
		}
		MWNumericArray res1 = new MWNumericArray(dt1, MWClassID.DOUBLE);
		resMp.put(dataNames[1], res1);
		return resMp;
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
		// while (true) {
		// Toolkit.getDefaultToolkit().beep();
		// try {
		// Thread.sleep(1000);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
	}

	private static double ticTime = System.nanoTime();

	public static void tic() {
		ticTime = System.nanoTime();
	}

	public static double toc() {
		return toc(true);
	}

	public static double toc(boolean isPrint) {
		double t = (Math.max(0, System.nanoTime() - ticTime)) / 1000000.0;
		if (isPrint) {
			System.out.println("time passed: " + t + "ms");
		}
		return t;
	}

	public static Map<String, Object> loadTrainAndTestDataSet(
			Map<String, Object> info) {
		Map<String, String> dataInfo = (Map<String, String>) info.get("data");
		Map<String, Integer> partiesInfo = (Map<String, Integer>) info
				.get("parties");
		Map<String, Integer> sampleInfo = (Map<String, Integer>) info
				.get("sampleInfo");
		Map<String, Object> resMp = new HashMap<String, Object>();
		String fname = dataInfo.get("fileName");
		MatFileReader read = null;
		try {
			read = new MatFileReader(getPath()+"in/"+fname);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		double[][] dtX = (((MLDouble) (read.getMLArray(dataInfo.get("X"))))
				.getArray());
		double[][] dtY = (((MLDouble) (read.getMLArray(dataInfo.get("Y"))))
				.getArray());
		int n = dtX.length;
		int m = dtX[0].length;

		if (info.containsKey("subDataNum")) {
			int subN = (Integer)info.get("subDataNum");
			if (subN > 0 && subN <= n) {
				n = subN;
			}
		}
		if (info.containsKey("subAttrNum")) {
			int subM = (Integer)info.get("subAttrNum");
			if (subM > 0 && subM <= m) {
				m = subM;
			}
		}
		int testBlockId = sampleInfo.get("testBlockId");
		int partyNum = partiesInfo.get("partyNum");
		int[] dimPartition = getPartBlocks(partiesInfo.get("attrBlockNum"), m);
		int[] samplePartition = getPartBlocks(
				sampleInfo.get("sampleBlockNum"), n);
		int testNum =0;
		if (testBlockId!=0) {
			testNum = samplePartition[testBlockId]
					- samplePartition[testBlockId - 1];
		}
		MWNumericArray[] matXTrain = new MWNumericArray[partyNum];
		MWNumericArray[] matXTest = new MWNumericArray[partyNum];
		for (int i = 0; i < partyNum; i++) {
			double[][] dtXTrain =null;
			double[][] dtXTest =null;
			if (testBlockId!=0) {
				dtXTrain = new double[n - testNum][dimPartition[i + 1]
						- dimPartition[i]];
				dtXTest = new double[testNum][dimPartition[i + 1]
						- dimPartition[i]];
			}else{
				dtXTrain = new double[n][dimPartition[i + 1]
						- dimPartition[i]];
			}
			int trainPos = 0;
			int testPos = 0;
			for (int k = 0; k < n; k++) {
				if (testBlockId!=0&&k >= samplePartition[testBlockId - 1]
						&& k < samplePartition[testBlockId]) {
					int pt = 0;
					for (int j = dimPartition[i]; j < dimPartition[i + 1]; j++) {
						dtXTest[testPos][pt++] = dtX[k][j];
					}
					testPos++;
				} else {
					int pt = 0;
					for (int j = dimPartition[i]; j < dimPartition[i + 1]; j++) {
						dtXTrain[trainPos][pt++] = dtX[k][j];
					}
					trainPos++;
				}
			}
			matXTrain[i] = new MWNumericArray(dtXTrain, MWClassID.DOUBLE);
			if (testBlockId!=0) {
				matXTest[i] = new MWNumericArray(dtXTest, MWClassID.DOUBLE);
			}
		}
		resMp.put("trainXs", matXTrain);
		if (testBlockId!=0) {
			resMp.put("testXs", matXTest);
		}

		double[][] dtYTrain =null;
		double[][] dtYTest =null;
		if (testBlockId!=0) {
			dtYTrain = new double[n - testNum][1];
			dtYTest = new double[testNum][1];
		}else{
			dtYTrain = new double[n][1];
		}
		int trainPos = 0;
		int testPos = 0;
		for (int k = 0; k < n; k++) {
			if (testBlockId!=0&&k >= samplePartition[testBlockId - 1]
					&& k < samplePartition[testBlockId]) {
				dtYTest[testPos++][0] = dtY[k][0];
			} else {
				dtYTrain[trainPos++][0] = dtY[k][0];
			}
		}
		MWNumericArray matYTrain = new MWNumericArray(dtYTrain,
				MWClassID.DOUBLE);
		resMp.put("trainY", matYTrain);
		if (testBlockId!=0) {
			MWNumericArray matYTest = new MWNumericArray(dtYTest, MWClassID.DOUBLE);
			resMp.put("testY", matYTest);
		}
		return resMp;
	}
	
	
	public static boolean isStartupFromJar() {
        String protocol = ExpScript.class.getResource("").getProtocol();
        if ("jar".equals(protocol)) {
            return true;
        } else if ("file".equals(protocol)) {
            return false;
        }
        return true;
    }
	public static String getPath()
	{
		if (isStartupFromJar()) {
			String path = ExpScript.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			if(System.getProperty("os.name").contains("dows"))
			{
				path = path.substring(1,path.length());
			}
			if(path.contains("jar"))
			{
				path = path.substring(0,path.lastIndexOf("."));
				return path.substring(0,path.lastIndexOf("/"))+"/";
			}
			return path.replace("target/classes/", "")+"/";
		}else{
			return "";
		}
	}
	
	
}
