package exps;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import model.NetCloud;
import alg.AlgInfo;
import algs.vrlmp.CoordinationServer;
import algs.vrlmp.Party;

import com.alibaba.fastjson.JSON;
import com.mathworks.toolbox.javabuilder.MWNumericArray;

import common.Common;
import common.MatComputeHelper;
import experiment.ExpFramework;
import experiment.ExpHelper;
import experiment.SimpleExpReporter;
/**
 * The code is for paper <b>"Efficient Vertical Federated Learning Method for Ridge Regression of Large-Scale Samples via Least-Squares Solution"</b>. <br/>
 * This experiment uses VRL-2P algorithm proposed by us.
 */
public class ExpForVRL_MP{

	public static Map<String,Object> run(String paramJson) {
		NetCloud.initTrans();
		Map<String,Object> param=(Map<String, Object>) JSON.parse(paramJson,
				ExpHelper.MYJSON_FEATURE);
		AlgInfo algInfo=new AlgInfo();
		algInfo.setLambda(((Number)param.get("lambda")).doubleValue());
		algInfo.setExpName("ExpOn"+(System.currentTimeMillis()%10000000));
		
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

		int nSample=yTrain.getDimensions()[0];
		int partyNum=((Map<String, Integer>) param.get("parties")).get("partyNum");
		int dim[]=new int[partyNum];
		for (int i = 0; i < dim.length; i++) {
			dim[i]=XTrainSet[i].getDimensions()[1];
		}
		algInfo.setDim(dim);
		//////////////////////////////////////////
		
		CoordinationServer coor=new CoordinationServer(algInfo);

		Party ccs[]=new Party[dim.length];
		for (int i = 0; i < ccs.length; i++) {
			ccs[i]=new Party(algInfo, i);
			
			Map<String, Object> mp=new HashMap<String, Object>();
			mp.put("DataPiece", XTrainSet[i]);
			if (i+1==ccs.length) {
				mp.put("DataDecision", yTrain);
			}
			ccs[i].loadData(mp);
		}
		coor.setParticipants(ccs);
		coor.prepare();
		coor.startThread();
		coor.run();
		Map<String, Object> res=coor.getReport();
		//////////////////////////////////////////
		MWNumericArray[] ws=new MWNumericArray[ccs.length];
		MWNumericArray d=null;
		MWNumericArray wlen=null;
		for (int i = 0; i < ccs.length; i++) {
			ws[i]=(MWNumericArray) ccs[i].showResult();
			if (d==null) {
				d=MatComputeHelper.mul(XTrainSet[i], ws[i]);
			}else{
				d=MatComputeHelper.add(d,MatComputeHelper.mul(XTrainSet[i], ws[i]));
			}
			if (wlen==null) {
				wlen=MatComputeHelper.mul(algInfo.getLambda(),MatComputeHelper.mul(MatComputeHelper.transpose(ws[i]), ws[i]));
			}else{
				wlen=MatComputeHelper.add(wlen,MatComputeHelper.mul(algInfo.getLambda(),MatComputeHelper.mul(MatComputeHelper.transpose(ws[i]), ws[i])));
			}
		}
		d=MatComputeHelper.subtract(d,yTrain);
		MWNumericArray L1=MatComputeHelper.mul(MatComputeHelper.transpose(d), d);
		double optVal=MatComputeHelper.add(L1, wlen).getDouble();
		///////////////////////////////////
		System.out.println("For experiment No."+(algInfo.getTestId()+1)+" by VRL-MP:");
		System.out.println("\tThe number of samples is "+nSample+".");
		System.out.println("\tThehe experiment is for "+dim.length+" parties.");
		System.out.println("\t\tThe number of features they have is "+Arrays.toString(dim)+", respectively.");
		System.out.println("\tThe running time of the experiment is "+(((Double) res.get("time"))/1000)+"s.");
		System.out.println("\tThe object values is "+optVal+".");
		if (hasTest) {
			// 求预测结果
			int nSampleTest=yTest.getDimensions()[0];
			
			d=null;
			for (int i = 0; i < ccs.length; i++) {
				ws[i]=(MWNumericArray) ccs[i].showResult();
				if (d==null) {
					d=MatComputeHelper.mul(XTestSet[i], ws[i]);
				}else{
					d=MatComputeHelper.add(d,MatComputeHelper.mul(XTestSet[i], ws[i]));
				}
			}
			
			d=MatComputeHelper.subtract(d,yTest);
			L1=MatComputeHelper.mul(MatComputeHelper.transpose(d), d);
			double rmse=Math.sqrt(L1.getDouble()/nSampleTest);
			System.out.println("\tThe RMSE is "+rmse+".");
			res.put("rmse", rmse);
		}
		res.put("optVal", optVal);
		long trans=NetCloud.showTrans();
		res.put("transport", trans);
		////////////////////////////////////
		coor.stop();
		return res;
	}

}
