package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.NetCloud;
import model.Stage;
import server.Coordinator;

import com.mathworks.toolbox.javabuilder.MWNumericArray;

import common.Common;
import common.LogSaver;
import common.MatComputeHelper;

/**
 * The code is for paper <b>"Efficient Vertical Federated Learning Method for Ridge Regression of Large-Scale Samples via Least-Squares Solution"</b>. <br/>
 * The implementation of Alg.VRL-MP.
 * It is the vertical federated learning algorithm for ridge regression via least-squares with 2-party scenario proposed in this paper.
 */
/**
* {"head":"(^-^)/ExpDesc",
* "实验组":"联邦学习岭回归的实验",
* "实验名":"提出的VRL-MP算法",
* "描述":"该实验使用论文提出的多方联邦学习算法VRL_MP实现，实验效果比较高效",
* "关键词":"联邦学习，多方，岭回归，解析法",
* "重要性":5,
* "作者":"蔡剑平"}
 */
public class SceneForVRL_MP {
	public static void main(String[] args) {
		Map<String, Object> logMp=new HashMap<String, Object>();
		Common.deleteDir("objs");
		double lambda = 1; // the lambda
		int[] dim={41,40}; // features of each participant, for 2 participants
//		int[] dim={27,27,27}; // features of each participant, for 3 participants
//		int[] dim={21,20,20,20}; // features of each participant, for 4 participants
//		int[] dim={17,16,16,16,16}; // features of each participant, for 5 participants
		// numbers of samples, ith number is the number of samples of ith experiment
		int[] sampleArr={1000,2000,3000,4000,5000,6000,7000,8000,9000,10000,11000,12000,13000,14000,15000,16000,17000,18000,19000,20000,21000,21263};
		///////////////////////////////////////////////////////////////////////////////////////
		String agentName = "coordinator";
		Coordinator ca = new Coordinator(agentName);
		ca.startThread();
		List<Double> errLs=new ArrayList<Double>();
		List<Double> tmLs=new ArrayList<Double>();
		List<Long> transLs=new ArrayList<Long>();
		int m=dim.length;
		ParticipantClientForVRL_MP ccs[]=new ParticipantClientForVRL_MP[m];
		String groupName="participant";
		for (int i = 0; i < ccs.length; i++) {
			ccs[i]=new ParticipantClientForVRL_MP(groupName, agentName, i, m);
		}
		logMp.put("type", "VRLMP");
		logMp.put("dim", dim);
		logMp.put("samples", sampleArr);
		for (int k = 0; k < sampleArr.length; k++) {
			int nSample=sampleArr[k];
			tic();
			MWNumericArray[] XSet=Common.readFromTxt("in/XData_norm.txt", dim,0,nSample);
			MWNumericArray[] YSet=Common.readFromTxt("in/yData.txt",new int[]{1},0,nSample);
			MWNumericArray y=YSet[0];
			for (int i = 0; i < ccs.length; i++) {
				ccs[i].setPubKey(ca.getPubKey());
				ccs[i].loadData(XSet);
				ccs[i].setLambda(lambda);
				ccs[i].setTestId(k);
			}
			ccs[m-1].setY(y);
			ccs[m-1].setDim(dim);
			Stage stage = new Stage(m);
			stage.start();
			for (int i = 0; i < ccs.length; i++) {
				stage.addData(ccs[i]);
			}
			stage.waitForCompletion();
			double tm=toc(false);
			MWNumericArray[] ws=new MWNumericArray[ccs.length];
			MWNumericArray d=null;
			MWNumericArray wlen=null;
			for (int i = 0; i < ccs.length; i++) {
				ws[i]=(MWNumericArray) ccs[i].showResult();
				if (d==null) {
					d=MatComputeHelper.mul(XSet[i], ws[i]);
				}else{
					d=MatComputeHelper.add(d,MatComputeHelper.mul(XSet[i], ws[i]));
				}
				if (wlen==null) {
					wlen=MatComputeHelper.mul(lambda,MatComputeHelper.mul(MatComputeHelper.transpose(ws[i]), ws[i]));
				}else{
					wlen=MatComputeHelper.add(wlen,MatComputeHelper.mul(lambda,MatComputeHelper.mul(MatComputeHelper.transpose(ws[i]), ws[i])));
				}
			}
			d=MatComputeHelper.subtract(d,y);
			MWNumericArray L1=MatComputeHelper.mul(MatComputeHelper.transpose(d), d);
			double err=MatComputeHelper.add(L1, wlen).getDouble();
			System.out.println("For experiment No."+(k+1)+" by VRL-MP:");
			System.out.println("\tThe number of samples is "+nSample+".");
			System.out.println("\tThehe experiment is for "+dim.length+" parties.");
			System.out.println("\t\tThe number of features they have is "+Arrays.toString(dim)+", respectively.");
			System.out.println("\tThe running time of the experiment is "+(tm/1000)+"s.");
			System.out.println("\tThe object values is "+err+".");
			double resMeasure=Math.sqrt(err/nSample);
			System.out.println("\tThe running res is "+resMeasure+".");
		    errLs.add(err);
			tmLs.add(tm);
			transLs.add(NetCloud.getAllTrans());
		}
		logMp.put("runTimes", tmLs);
		logMp.put("errs", errLs);
		logMp.put("trans", transLs);
		LogSaver.saveData(logMp);
		Common.finished();
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
