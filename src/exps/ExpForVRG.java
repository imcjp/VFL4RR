package exps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Message;
import model.NetCloud;
import model.Stage;
import security.EncMat;
import alg.AlgInfo;
import algs.vrg.CoordinationServer;
import algs.vrg.Party1;
import algs.vrg.Party2;

import com.alibaba.fastjson.JSON;
import com.mathworks.toolbox.javabuilder.MWNumericArray;
import common.Common;
import common.MatComputeHelper;


/**
 * The code is for paper <b>"Efficient Vertical Federated Learning Method for Ridge Regression of Large-Scale Samples via Least-Squares Solution"</b>. <br/>
 * This experiment uses VRG method proposed by Yang et al.[1].
 * [1] Yang Q., Liu Y., Chen T., et al. (2019). Federated machine learning: concept and applications. ACM Transactions on Intelligent Systems and Technology, 10(2), 1-19.
 */
public class ExpForVRG {
	public static Map<String, Object> run(String paramJson) {
		NetCloud.initTrans();
		Map<String, Object> param = (Map<String, Object>) JSON.parse(paramJson,
				Common.MYJSON_FEATURE);
		AlgInfo algInfo = new AlgInfo();
		algInfo.setLambda((double) param.get("lambda"));
		algInfo.setIter((int) param.get("iteration"));
		algInfo.setBeta1(0.9);
		algInfo.setBeta2(0.999);
		algInfo.setEpsilon(1.4901e-08);
		algInfo.setExpName("ExpOn" + (System.currentTimeMillis() % 10000000));
		double learningRate = (Double) ((Map) param.get("data"))
				.get("learningRate");
		double initBias = (Double) ((Map) param.get("data")).get("initBias");
		algInfo.setInitBias(initBias);
		algInfo.setLearningRate(learningRate);
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

		int nSample = yTrain.getDimensions()[0];
		int dim[] = new int[2];
		for (int i = 0; i < dim.length; i++) {
			dim[i] = XTrainSet[i].getDimensions()[1];
		}
		algInfo.setDim(dim);
		CoordinationServer coor = new CoordinationServer(algInfo);
		Party1 party1 = new Party1(algInfo);
		Party2 party2 = new Party2(algInfo);
		Map<String, Object> mp1 = new HashMap<String, Object>();
		Map<String, Object> mp2 = new HashMap<String, Object>();
		mp1.put("DataPiece", XTrainSet[0]);
		mp2.put("DataPiece", XTrainSet[1]);
		mp2.put("DataDecision", yTrain);
		party1.loadData(mp1);
		party2.loadData(mp2);
		coor.startThread();
		System.out.println("For VRG:");
		System.out.println("\tThe number of samples is " + nSample + ".");
		System.out.println("\tThehe experiment is for " + dim.length
				+ " parties.");
		System.out.println("\t\tThe number of features they have is "
				+ Arrays.toString(dim) + ", respectively.");
		party1.setPubKey(coor.getPubKey());
		party2.setPubKey(coor.getPubKey());
		Map<String, Object> res = new HashMap<String, Object>();
		Message msg = null;
		List<Double> timeList = new ArrayList<Double>();
		List<Double> optValList = new ArrayList<Double>();
		List<Double> rmseList = new ArrayList<Double>();
		res.put("timeList", timeList);
		res.put("optValList", optValList);
		int nSampleTest = 0;
		if (hasTest) {
			res.put("rmseList", rmseList);
			nSampleTest = yTest.getDimensions()[0];
		}
		MWNumericArray d1 = null;
		MWNumericArray d = null;
		MWNumericArray d2 = null;
		MWNumericArray L1 = null;
		double fval = Double.POSITIVE_INFINITY;
		double lastFval = 0;
		double tm = 0;
		double rmse = 0;
		int noUpdateTime = 0;
		for (int i = 0; i <= 10000; i++) {
			Common.tic();
			lastFval = fval;
			Stage stage = new Stage(2);
			stage.start();
			stage.addData(party1);
			stage.addData(party2);
			stage.waitForCompletion();
			msg = coor.waitMessage(21);
			EncMat encL = (EncMat) msg.getObj();
			fval = coor.decr(encL).getDouble();
			if (fval / lastFval > 0.9999) {
				party1.rollback();
				party2.rollback();
				fval = lastFval;
				noUpdateTime++;
			} else {
				noUpdateTime = 0;
			}
			tm += Common.toc(false);
			timeList.add(tm);
			optValList.add(fval);
			System.out.println("\tAfter " + (i) + "-th iteration:");
			System.out.println("\t\tThe running time past " + (tm / 1000)
					+ "s.");
			System.out.println("\t\tThe object values is " + fval + ".");
			if (hasTest) {
				MWNumericArray w1 = (MWNumericArray) party1.showResult();
				MWNumericArray w2 = (MWNumericArray) party2.showResult();

				d1 = MatComputeHelper.mul(XTestSet[0], w1);
				d2 = MatComputeHelper.mul(XTestSet[1], w2);
				d = MatComputeHelper.subtract(MatComputeHelper.add(d1, d2),
						yTest);
				L1 = MatComputeHelper.mul(MatComputeHelper.transpose(d), d);
				rmse = Math.sqrt(L1.getDouble() / nSampleTest);
				rmseList.add(rmse);
				System.out.println("\t\tThe RMSE is " + rmse + ".");
			}
			Common.tic();
			party1.update(i + 1);
			party2.update(i + 1);
			tm += Common.toc(false);
			if (noUpdateTime >= algInfo.getIter()) {
				break;
			}
		}
		res.put("optVal", optValList.get(optValList.size() - 1));
		if (hasTest) {
			res.put("rmse", rmseList.get(rmseList.size() - 1));
		}
		System.out.println(rmseList.size());
		res.put("time", tm);
		coor.stop();
		return res;
	}
}
