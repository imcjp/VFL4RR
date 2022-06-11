package exps;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import model.NetCloud;
import alg.AlgInfo;
import algs.vrl2p.CoordinationServer;
import algs.vrl2p.Party1;
import algs.vrl2p.Party2;

import com.alibaba.fastjson.JSON;
import com.mathworks.toolbox.javabuilder.MWNumericArray;

import common.Common;
import common.MatComputeHelper;
import experiment.ExpFramework;
import experiment.ExpHelper;
import experiment.SimpleExpReporter;
/**
* {"head":"(^-^)/ExpDesc",
* "实验组":"联邦学习岭回归的实验",
* "实验名":"基于最小二乘的双方联邦学习算法",
* "描述":"该实验使用论文设计的最小二乘双方联邦学习算法实现",
* "关键词":"联邦学习，双方，最小二乘",
* "重要性":5,
* "作者":"蔡剑平"}
 */
public class ExpForNoFed extends ExpFramework{
	public ExpForNoFed(String name, boolean renew) {
		super(name, renew);
		// TODO Auto-generated constructor stub
	}
	public ExpForNoFed(String name, String path, boolean renew) {
		super(name,path, renew);
	}

	public static Map<String,Object> run(String paramJson) {
		NetCloud.initTrans();
		Map<String,Object> param=(Map<String, Object>) JSON.parse(paramJson,
				ExpHelper.MYJSON_FEATURE);
		AlgInfo algInfo=new AlgInfo();
		algInfo.setLambda(((Number)param.get("lambda")).doubleValue());
		algInfo.setExpName("ExpOn"+(System.currentTimeMillis()%10000000));
		////////////////////////////////////////////////////////////////////
		Map<String, Object> inputData=Common.loadTrainAndTestDataSet(param);
		boolean hasTest = ((Map<String, Integer>) param.get("sampleInfo"))
				.get("testBlockId") > 0;
		
		MWNumericArray[] XTrainSet=(MWNumericArray[]) inputData.get("trainXs");
		MWNumericArray yTrain=(MWNumericArray) inputData.get("trainY");
		MWNumericArray[] XTestSet = null;
		MWNumericArray yTest = null;
		if (hasTest) {
			XTestSet = (MWNumericArray[]) inputData.get("testXs");
			yTest = (MWNumericArray) inputData.get("testY");
		}
		//////////////////////////////////////////
		Common.tic();
		MWNumericArray X=XTrainSet[0];
		int dim=X.getDimensions()[1];
		int nSample=X.getDimensions()[0];
		double omiga=1.0/nSample;
		MWNumericArray Xt=MatComputeHelper.mul(MatComputeHelper.transpose(X),omiga);
		MWNumericArray M11 = MatComputeHelper.add(
				MatComputeHelper.mul(Xt, X),
				MatComputeHelper.mul(MatComputeHelper.eye(dim), algInfo.getLambda()*omiga));
		MWNumericArray C = MatComputeHelper.inv(M11);
		MWNumericArray w = MatComputeHelper.mul(C,MatComputeHelper.mul(Xt, yTrain));
		double tm=Common.toc(false);
		Map<String, Object> res=new HashMap<String, Object>();
		res.put("time", tm);
		//////////////////////////////////////////
		// 求目标函数值
		MWNumericArray d1=MatComputeHelper.mul(X, w);
		MWNumericArray d=MatComputeHelper.subtract(d1,yTrain);
		MWNumericArray L1=MatComputeHelper.mul(MatComputeHelper.transpose(d), d);
		MWNumericArray wlen=MatComputeHelper.mul(algInfo.getLambda(),MatComputeHelper.mul(MatComputeHelper.transpose(w), w));
		double optVal=MatComputeHelper.add(L1, wlen).getDouble();
		///////////////////////////////////
		System.out.println("For experiment No."+(algInfo.getTestId()+1)+" by NoFed:");
		System.out.println("\tThe number of samples is "+nSample+".");
		System.out.println("\tThehe experiment is for 1 parties.");
		System.out.println("\t\tThe number of features they have is "+dim+", respectively.");
		System.out.println("\tThe running time of the experiment is "+(((Double) res.get("time"))/1000)+"s.");
		System.out.println("\tThe object values is "+optVal+".");
		if (hasTest) {
			// 求预测结果
			int nSampleTest=yTest.getDimensions()[0];
//			System.out.println(w);
			d=MatComputeHelper.subtract(MatComputeHelper.mul(XTestSet[0], w),yTest);
			L1=MatComputeHelper.mul(MatComputeHelper.transpose(d), d);
			double rmse=Math.sqrt(L1.getDouble()/nSampleTest);
			System.out.println("\tThe RMSE is "+rmse+".");
			res.put("rmse", rmse);
		}
		res.put("optVal", optVal);
		res.put("transport", 0);
		////////////////////////////////////
		return res;
	}
	public static void main(String[] args) throws FileNotFoundException {
		Common.deleteDir("objs");
		//init process/////////////////////////////////
		MatComputeHelper.add(1.0, 1.0);
		/////////////////////////////////////
		ExpForNoFed ep = new ExpForNoFed("vrl2pExpN4",Common.DB_PATH, true);
		ep.setReportListener(new SimpleExpReporter());
		ep.runTask();
		System.out.println("finished");
		Common.deleteDir("objs");
	}
	@Override
	public Map<String, Object> excuteExp(String arg0) throws Exception {
		Runtime.getRuntime().gc();
		return ExpForNoFed.run(arg0);
	}
	@Override
	public String requestInputJson() {
		String json=null;
		try {
			json = ExpHelper.readInputJsonFromFile(Common.getPath()+"json/2.json");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return json;
	}
}
