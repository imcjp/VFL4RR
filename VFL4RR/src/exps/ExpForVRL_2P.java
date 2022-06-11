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
 * The code is for paper <b>
 * "Efficient Vertical Federated Learning Method for Ridge Regression of Large-Scale Samples via Least-Squares Solution"
 * </b>. <br/>
 * This experiment uses VRL-2P algorithm proposed by us.
 */
public class ExpForVRL_2P {
	public static Map<String, Object> run(String paramJson) {
		NetCloud.initTrans();
		Map<String, Object> param = (Map<String, Object>) JSON.parse(paramJson,
				ExpHelper.MYJSON_FEATURE);
		AlgInfo algInfo = new AlgInfo();
		algInfo.setLambda(((Number) param.get("lambda")).doubleValue());
		algInfo.setExpName("ExpOn" + (System.currentTimeMillis() % 10000000));
		Map<String, Object> inputData = Common.loadTrainAndTestDataSet(param);
		boolean hasTest = ((Map<String, Integer>) param.get("sampleInfo"))
				.get("testBlockId") > 0;

		MWNumericArray[] XTrainSet = (MWNumericArray[]) inputData
				.get("trainXs");
		MWNumericArray yTrain = (MWNumericArray) inputData.get("trainY");
		MWNumericArray[] XTestSet = null;
		MWNumericArray yTest = null;
		if (hasTest) {
			XTestSet = (MWNumericArray[]) inputData.get("testXs");
			yTest = (MWNumericArray) inputData.get("testY");
		}
		// ////////////////////////////////////////

		int nSample = yTrain.getDimensions()[0];
		int dim[] = new int[2];
		for (int i = 0; i < dim.length; i++) {
			dim[i] = XTrainSet[i].getDimensions()[1];
		}
		// ////////////////////////////////////////
		CoordinationServer coor = new CoordinationServer(algInfo);
		Party1 party1 = new Party1(algInfo);
		Party2 party2 = new Party2(algInfo);
		coor.setTheParticipants(party1, party2);
		Map<String, Object> mp1 = new HashMap<String, Object>();
		Map<String, Object> mp2 = new HashMap<String, Object>();
		mp1.put("DataPiece", XTrainSet[0]);
		mp2.put("DataPiece", XTrainSet[1]);
		mp2.put("DataDecision", yTrain);
		party1.loadData(mp1);
		party2.loadData(mp2);
		coor.prepare();
		coor.startThread();
		coor.run();
		Map<String, Object> res = coor.getReport();
		MWNumericArray w1 = (MWNumericArray) party1.showResult();
		MWNumericArray w2 = (MWNumericArray) party2.showResult();
		// 求目标函数值
		MWNumericArray d1 = MatComputeHelper.mul(XTrainSet[0], w1);
		MWNumericArray d2 = MatComputeHelper.mul(XTrainSet[1], w2);
		MWNumericArray d = MatComputeHelper.subtract(
				MatComputeHelper.add(d1, d2), yTrain);
		MWNumericArray L1 = MatComputeHelper.mul(MatComputeHelper.transpose(d),
				d);
		MWNumericArray wlen1 = MatComputeHelper.mul(algInfo.getLambda(),
				MatComputeHelper.mul(MatComputeHelper.transpose(w1), w1));
		MWNumericArray wlen2 = MatComputeHelper.mul(algInfo.getLambda(),
				MatComputeHelper.mul(MatComputeHelper.transpose(w2), w2));
		MWNumericArray wlen = MatComputeHelper.add(wlen1, wlen2);
		double optVal = MatComputeHelper.add(L1, wlen).getDouble();
		// /////////////////////////////////
		System.out.println("For experiment No." + (algInfo.getTestId() + 1)
				+ " by VRL-2P:");
		System.out.println("\tThe number of samples is " + nSample + ".");
		System.out.println("\tThehe experiment is for " + dim.length
				+ " parties.");
		System.out.println("\t\tThe number of features they have is "
				+ Arrays.toString(dim) + ", respectively.");
		System.out.println("\tThe running time of the experiment is "
				+ (((Double) res.get("time")) / 1000) + "s.");
		System.out.println("\tThe object values is " + optVal + ".");
		if (hasTest) {
			// 求预测结果
			int nSampleTest = yTest.getDimensions()[0];
			d1 = MatComputeHelper.mul(XTestSet[0], w1);
			d2 = MatComputeHelper.mul(XTestSet[1], w2);
			d = MatComputeHelper.subtract(MatComputeHelper.add(d1, d2), yTest);
			L1 = MatComputeHelper.mul(MatComputeHelper.transpose(d), d);
			double rmse = Math.sqrt(L1.getDouble() / nSampleTest);
			System.out.println("\tThe RMSE is " + rmse + ".");
			res.put("rmse", rmse);
		}

		res.put("optVal", optVal);
		long trans = NetCloud.showTrans();
		res.put("transport", trans);
		// //////////////////////////////////
		coor.stop();
		return res;
	}
}
