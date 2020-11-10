package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import model.Message;
import model.Stage;
import client.EageServerForParticipant;

import com.mathworks.toolbox.javabuilder.MWNumericArray;

import common.Common;
import common.MatComputeHelper;
import security.EncMat;
import security.EncMatMultThread;
import server.Coordinator;

/**
 * The code is for paper <b>"Efficient Vertical Federated Learning Method for Ridge Regression of Large-Scale Samples via Least-Squares Solution"</b>. <br/>
 * The implementation of Alg.VRL-2P.
 * It is the vertical federated learning algorithm for ridge regression via least-squares with 2-party scenario proposed in this paper.
 */
public class SceneForVRL_2P {
	public static void main(String[] args) {
		double lambda = 1; // the lambda
		int[] dim={41,40}; // features of each participant
//		int[] sampleArr={1000,2000,3000,4000,5000,6000,7000,8000,9000,10000,11000,12000,13000,14000,15000,16000,17000,18000,19000,20000,21000,21263};
		int[] sampleArr={100,500}; // numbers of samples, ith number is the number of samples of ith experiment
		//////////////////////////////////////////////////////////////
		String agentName = "coordinator";
		Coordinator ca = new Coordinator(agentName);
		ca.startThread();
		MWNumericArray[] XSet=null;
		MWNumericArray[] YSet=null;				
		EageServerForParticipant participant1 = new EageServerForParticipant("participant1", agentName) {
			private MWNumericArray X0 = null;
			private int n;
			private int d;
			private MWNumericArray res = null;
			private int testId;
			@Override
			public void run() {
				Common.println("Participant1 started...");
				double omiga=1.0/n;
				int p = 1;
				Message msg = null;
				MWNumericArray Q=MatComputeHelper.randOrthMat(d);
				MWNumericArray X=MatComputeHelper.mul(X0, Q);
				MWNumericArray Xt = MatComputeHelper.mul(MatComputeHelper.transpose(X),omiga);
				MWNumericArray F1=coMul(Xt, testId*Common.ROOMID_BIAS+1, 1);
				EncMat encF1=new EncMatMultThread(F1,pubKey);
				sendMessage("participant2", 21, encF1);
				msg=waitMessage(21);
				EncMat M12=(EncMat) msg.getObj();
				EncMat M21=M12.transpose();
				/////////////////////////////////////////////////////
				MWNumericArray M11 = MatComputeHelper.add(
						MatComputeHelper.mul(Xt, X),
						MatComputeHelper.mul(MatComputeHelper.eye(d), lambda*omiga));
				MWNumericArray C = MatComputeHelper.inv(M11);
				EncMat encC=new EncMatMultThread(C,pubKey);
				sendMessage("participant2", 21, encC);
				Common.println(getName() + "========" + (p++)
						+ "===========================");
				msg = waitMessage(21);
				EncMat encCt = (EncMat) msg.getObj();
				EncMat encR=mul(encCt, M21);
				encR=mul(M12, encR);
				encR=new EncMatMultThread(M11, pubKey).subtract(encR);
				encR=inv(encR);
				MWNumericArray r11=coMul(Xt, testId*Common.ROOMID_BIAS+2, 1);
				msg=waitMessage(21);
				EncMat encr12=(EncMat) msg.getObj();
				EncMat encr=encr12.add(r11);
				EncMat encu1=mul(encR, encr);
				sendMessage("participant2", 21, encu1);
				msg=waitMessage(21);
				EncMat encu2=(EncMat) msg.getObj();
				EncMat encv1=EncMat.mul2(C, mul(M12, encu2));
				EncMat encw = encu1.subtract(encv1);
				res = MatComputeHelper.mul(Q,decr(encw));
				Common.println(getName() + "========" + (p++)
						+ "===========================");
			}
			@Override
			public void loadData(Object obj) {
				Object[] dataSet=(Object[]) obj;
				MWNumericArray[] XSet=(MWNumericArray[]) dataSet[0];
				X0 = XSet[0];
				testId=(int) dataSet[1];
				n=X0.getDimensions()[0];
				d=X0.getDimensions()[1];
			}
			@Override
			public Object showResult() {
				return res;
			}
		};
		EageServerForParticipant participant2 = new EageServerForParticipant("participant2", agentName) {
			private MWNumericArray X0 = null;
			private MWNumericArray y = null;
			private int n;
			private int d;
			private MWNumericArray res = null;
			private int testId;
			@Override
			public void run() {
				Common.println("Participant2 started...");
				double omiga=1.0/n;
				int p = 1;
				Message msg = null;
				MWNumericArray Q=MatComputeHelper.randOrthMat(d);
				MWNumericArray X=MatComputeHelper.mul(X0, Q);
				MWNumericArray Xt = MatComputeHelper.mul(MatComputeHelper.transpose(X),omiga);
				MWNumericArray F2=coMul(X, testId*Common.ROOMID_BIAS+1, 2);
				msg=waitMessage(21);
				EncMat encF1=(EncMat) msg.getObj();
				EncMat M12=encF1.add(F2);
				sendMessage("participant1", 21, M12);
				EncMat M21=M12.transpose();
				MWNumericArray M22 = MatComputeHelper.add(
						MatComputeHelper.mul(Xt, X),
						MatComputeHelper.mul(MatComputeHelper.eye(d), lambda*omiga));
				MWNumericArray C = MatComputeHelper.inv(M22);
				EncMat encC=new EncMatMultThread(C,pubKey);
				sendMessage("participant1", 21, encC);
				msg = waitMessage(21);
				EncMat encCt = (EncMat) msg.getObj();
				EncMat encR=mul(encCt, M12);
				encR=mul(M21, encR);
				encR=new EncMatMultThread(M22, pubKey).subtract(encR);
				encR=inv(encR);
				MWNumericArray r12=coMul(y, testId*Common.ROOMID_BIAS+2, 2);
				EncMat encr12=new EncMatMultThread(r12, pubKey);
				sendMessage("participant1", 21, encr12);
				MWNumericArray r2=MatComputeHelper.mul(Xt, y);
				EncMat encu2=encR.mul(r2);
				sendMessage("participant1", 21, encu2);
				msg = waitMessage(21);
				EncMat encu1=(EncMat) msg.getObj();
				EncMat encv2=EncMat.mul2(C, mul(M21, encu1));
				EncMat encw = encu2.subtract(encv2);
				res = MatComputeHelper.mul(Q,decr(encw));
				Common.println(getName() + "========" + (p++)
						+ "===========================");
			}

			@Override
			public void loadData(Object obj) {
				Object[] dataSet=(Object[]) obj;
				MWNumericArray[] XSet=(MWNumericArray[]) dataSet[0];
				MWNumericArray[] YSet=(MWNumericArray[]) dataSet[1];
				testId=(int) dataSet[2];
				X0 = XSet[1];
				n=X0.getDimensions()[0];
				d=X0.getDimensions()[1];
				y = YSet[0];
			}
			@Override
			public Object showResult() {
				return res;
			}
		};
		List<Double> errLs=new ArrayList<Double>();
		List<Double> tmLs=new ArrayList<Double>();
		for (int k = 0; k < sampleArr.length; k++) {
			int nSample=sampleArr[k];
			XSet=Common.readFromTxt("in/XData_norm.txt", dim,0,nSample);
			YSet=Common.readFromTxt("in/yData.txt",new int[]{1},0,nSample);
			tic();
			participant1.setPubKey(ca.getPubKey());
			participant2.setPubKey(ca.getPubKey());
			Object[] dataSet={XSet,new Integer(k)};
			participant1.loadData(dataSet);
			dataSet=new Object[]{XSet,YSet,new Integer(k)};
			participant2.loadData(dataSet);
			Stage stage = new Stage(2);
			stage.start();
			stage.addData(participant1);
			stage.addData(participant2);
			stage.waitForCompletion();
			double tm=toc(false);
			MWNumericArray w1=(MWNumericArray) participant1.showResult();
			MWNumericArray w2=(MWNumericArray) participant2.showResult();
			MWNumericArray d1=MatComputeHelper.mul(XSet[0], w1);
			MWNumericArray d2=MatComputeHelper.mul(XSet[1], w2);
			MWNumericArray d=MatComputeHelper.subtract(MatComputeHelper.add(d1, d2),YSet[0]);
			MWNumericArray L1=MatComputeHelper.mul(MatComputeHelper.transpose(d), d);
			MWNumericArray wlen1=MatComputeHelper.mul(lambda,MatComputeHelper.mul(MatComputeHelper.transpose(w1), w1));
			MWNumericArray wlen2=MatComputeHelper.mul(lambda,MatComputeHelper.mul(MatComputeHelper.transpose(w2), w2));
			MWNumericArray wlen=MatComputeHelper.add(wlen1, wlen2);
			double err=MatComputeHelper.add(L1, wlen).getDouble();
			System.out.println("For experiment No."+(k+1)+" by VRL-2P:");
			System.out.println("\tThe number of samples is "+nSample+".");
			System.out.println("\tThehe experiment is for "+dim.length+" parties.");
			System.out.println("\t\tThe number of features they have is "+Arrays.toString(dim)+", respectively.");
			System.out.println("\tThe running time of the experiment is "+(tm/1000)+"s.");
			System.out.println("\tThe object values is "+err+".");
			double resMeasure=Math.sqrt(err/nSample);
			System.out.println("\tThe running res is "+resMeasure+".");
		    errLs.add(err);
			tmLs.add(tm);
		}
		printRes(sampleArr,errLs,tmLs);
		System.exit(1);

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
	public static synchronized void showMat(MWNumericArray mat,String name) {
		System.out.println("The values of "+name+" are as follows:");
		double dn[][]=(double[][]) mat.toDoubleArray();
		for (int i = 0; i < dn.length; i++) {
			for (int j = 0; j < dn[0].length; j++) {
				System.out.print(dn[i][j]+"\t");
			}
			System.out.println();
		}
		System.out.println("///////////////////////////////////////////");
	}
	public static void printRes(int[] sampleArr, List<Double> errLs,List<Double> tmLs) {
		DateFormat df=new SimpleDateFormat("yyMMddhhmmss");
		File fout=new File("out/"+df.format(new Date())+".txt");
		PrintWriter pw=null;
		try {
			pw = new PrintWriter(fout);
			pw.println(sampleArr.length);
			for (int i = 0; i < sampleArr.length; i++) {
				pw.print(sampleArr[i]+"\t");
			}
			pw.println();
			pw.println(errLs.size());
			for (int i = 0; i < errLs.size(); i++) {
				pw.print(errLs.get(i)+"\t");
			}
			pw.println();
			pw.println(tmLs.size());
			for (int i = 0; i < tmLs.size(); i++) {
				pw.print(tmLs.get(i)+"\t");
			}
			pw.println();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}finally{
			if (pw!=null) {
				pw.flush();
				pw.close();
			}
		}
	}
}
