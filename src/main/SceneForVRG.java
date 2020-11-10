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
 * The implementation of Alg.VRG. It is the vertical federated learning algorithm for ridge regression proposed by Yang[1].
 * [1]	Yang Q., Liu Y., Chen T., et al. (2019). Federated machine learning: concept and applications. ACM Transactions on Intelligent Systems and Technology, 10(2), 1-19.
 */
public class SceneForVRG {

	public static void main(String[] args) {
		/////////////////////////////////////////////////////////////////
		double lambda = 1; // the lambda
		int[] dim={41,40}; // features of each participant
		double learningRate=0.1; // learning rate
		double momentum=0.9; // momentum
		int nSample=1000; // number of samples
		//////////////////////////////////////////////////////////////////
		String agentName = "coordinator";
		Coordinator ca = new Coordinator(agentName);
		ca.startThread();
		MWNumericArray[] XSet=Common.readFromTxt("in/XData_norm.txt", dim,0,nSample);
		MWNumericArray[] YSet=Common.readFromTxt("in/yData.txt",new int[]{1},0,nSample);
		EncMat bakVals[]=new EncMat[1];
		EageServerForParticipant participant1 = new EageServerForParticipant("participant1", agentName) {
			private MWNumericArray X0 = null;
			private int n;
			private int d;
			private MWNumericArray res = null;
			private MWNumericArray w = null;
			private MWNumericArray dw = null;
			
			@Override
			public void run() {
				Message msg=null;
				MWNumericArray X=X0;
				MWNumericArray Xt = MatComputeHelper.transpose(X);
				MWNumericArray u=MatComputeHelper.mul(X, w);
				EncMat encua=new EncMatMultThread(u, pubKey);
				MWNumericArray La=MatComputeHelper.mul(MatComputeHelper.transpose(u), u);
				La=MatComputeHelper.add(La, MatComputeHelper.mul(lambda,MatComputeHelper.mul(MatComputeHelper.transpose(w), w)));
				EncMat encLa=new EncMatMultThread(La, pubKey);
				sendMessage("participant2", 21, encua);
				sendMessage("participant2", 21, encLa);
				msg=waitMessage(21);
				EncMat encdb=(EncMat) msg.getObj();
				EncMat encd=encdb.add(u);
				EncMat encg=EncMat.mul2(Xt, encd).add(MatComputeHelper.mul(lambda, w));
				MWNumericArray g=decr(encg);
				MWNumericArray gt=MatComputeHelper.mul(g, 1.0/n);
				if (dw==null) {
					dw=MatComputeHelper.mul(-learningRate, gt);
				}else{
					dw=MatComputeHelper.add(MatComputeHelper.mul(momentum, dw), MatComputeHelper.mul(-learningRate, gt));
				}
				w=MatComputeHelper.add(w, dw);
			}

			@Override
			public void loadData(Object obj) {
				X0 = XSet[0];
				n=X0.getDimensions()[0];
				d=X0.getDimensions()[1];
				w=MatComputeHelper.randn(d, 1);
			}

			@Override
			public Object showResult() {
				if (res != null) {
					showMat(res, "the result w");
				} else {
					System.out.println("No result!!!");
				}
				return null;
			}
		};
		EageServerForParticipant participant2 = new EageServerForParticipant("participant2", agentName) {
			private MWNumericArray X0 = null;
			private MWNumericArray y = null;
			private int n;
			private int d;
			private MWNumericArray res = null;
			private MWNumericArray w = null;
			private MWNumericArray dw = null;

			@Override
			public void run() {
				Message msg=null;
				msg=waitMessage(21);
				EncMat encua=(EncMat) msg.getObj();
				msg=waitMessage(21);
				EncMat encLa=(EncMat) msg.getObj();
				MWNumericArray X=X0;
				MWNumericArray Xt = MatComputeHelper.transpose(X);
				MWNumericArray u=MatComputeHelper.mul(X, w);
				MWNumericArray db=MatComputeHelper.subtract(u, y);
				EncMat encdb=new EncMatMultThread(db, pubKey);
				MWNumericArray Lb=MatComputeHelper.mul(MatComputeHelper.transpose(db), db);
				Lb=MatComputeHelper.add(Lb, MatComputeHelper.mul(lambda,MatComputeHelper.mul(MatComputeHelper.transpose(w), w)));
				EncMat encLab=EncMat.mul2(MatComputeHelper.transpose(MatComputeHelper.mul(2,db)), encua);
				EncMat encL=encLa.add(Lb).add(encLab);
				sendMessage("participant1", 21, encdb);
				bakVals[0]=encL;
				EncMat encd=encdb.add(encua);
				EncMat encg=EncMat.mul2(Xt, encd).add(MatComputeHelper.mul(lambda, w));
				MWNumericArray g=decr(encg);
				MWNumericArray gt=MatComputeHelper.mul(g, 1.0/n);
				if (dw==null) {
					dw=MatComputeHelper.mul(-learningRate, gt);
				}else{
					dw=MatComputeHelper.add(MatComputeHelper.mul(momentum, dw), MatComputeHelper.mul(-learningRate, gt));
				}
				w=MatComputeHelper.add(w, dw);
			}

			@Override
			public void loadData(Object obj) {
				X0 = XSet[1];
				n=X0.getDimensions()[0];
				d=X0.getDimensions()[1];
				y = YSet[0];
				w=MatComputeHelper.randn(d, 1);
			}

			@Override
			public Object showResult() {
				if (res != null) {
					showMat(res, "the result w");
				} else {
					System.out.println("No result!!!");
				}
				return null;
			}
		};
		participant1.setPubKey(ca.getPubKey());
		participant2.setPubKey(ca.getPubKey());
		participant1.loadData(null);
		participant2.loadData(null);
		int iter=500;
		double lastL=1e+20;
		List<Double> errLs=new ArrayList<Double>();
		List<Double> tmLs=new ArrayList<Double>();
		System.out.println("For VRG:");
		System.out.println("\tThe number of samples is "+nSample+".");
		System.out.println("\tThehe experiment is for "+dim.length+" parties.");
		System.out.println("\t\tThe number of features they have is "+Arrays.toString(dim)+", respectively.");
		tic();
		for (int i = 0; i < iter; i++) {
			Stage stage = new Stage(2);
			stage.start();
			stage.addData(participant1);
			stage.addData(participant2);
			stage.waitForCompletion();
			double L=ca.decr(bakVals[0]).getDouble();
		    if (Math.abs(L-lastL)<1e-6){
		    	System.out.println("finished");
			    lastL=L;
			    errLs.add(lastL);
			    break;
		    }
			double tm=toc(false);
		    lastL=L;
			System.out.println("\tAfter "+(i+1)+"-th iteration:");
			System.out.println("\t\tThe running time past "+(tm/1000)+"s.");
			System.out.println("\t\tThe object values is "+lastL+".");
			double resMeasure=Math.sqrt(lastL/nSample);
			System.out.println("\t\tThe running res after is "+resMeasure+".");
		    errLs.add(lastL);
			tmLs.add(tm);
		}
		printRes(iter, tmLs,nSample,dim,errLs);
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
	public static void printRes(int iter,List<Double> tmLs,int nSample,int dim[], List<Double> errLs) {
		DateFormat df=new SimpleDateFormat("yyMMddhhmmss");
		File fout=new File("out/"+df.format(new Date())+".txt");
		PrintWriter pw=null;
		try {
			pw = new PrintWriter(fout);
			pw.print(iter+"\t");
			pw.println(nSample);
			pw.println(dim.length);
			for (int i = 0; i < dim.length; i++) {
				pw.print(dim[i]+"\t");
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
