package exps;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import common.Common;

import experiment.ExpHelper;
/**
 * The code is for paper <b>"Efficient Vertical Federated Learning Method for Ridge Regression of Large-Scale Samples via Least-Squares Solution"</b>. <br/>
 * This class is the entrance to run our algorithm. Through the jar package packaged by this class, you can run our algorithm on the console..
 */
public class ExpScript {
	public static Map<String, Object> runExp(String arg0) {
		// TODO Auto-generated method stub
		Map<String, Object> res=null;
		Map<String,Object> param=(Map<String, Object>) JSON.parse(arg0,
				ExpHelper.MYJSON_FEATURE);
		String alg=(String) param.get("algorithm");
		if ("VRG".equals(alg)) {
			res=ExpForVRG.run(arg0);
		}else if("VRL_2P".equals(alg)){
			res=ExpForVRL_2P.run(arg0);
		}else if ("VRL_MP".equals(alg)) {
			res=ExpForVRL_MP.run(arg0);
		}else if ("NoFed".equals(alg)) {
			res=ExpForNoFed.run(arg0);
		}
		return res;
	}

//	For VRG:
//	"{'lambda':10.0,'data':{'fileName':'Superconductivty.mat','X':'XData_norm','Y':'yData','learningRate':1.0,'initBias':10.0},'parties':{'partyNum':2,'attrBlockNum':2},'iteration':10,'subDataNum':100,'sampleInfo':{'sampleBlockNum':10,'testBlockId':1},'algorithm':'VRG'}"
//	For VRL-2P:
//	"{'lambda':10.0,'data':{'fileName':'Superconductivty.mat','X':'XData_norm','Y':'yData'},'parties':{'partyNum':2,'attrBlockNum':2},'subDataNum':10000,'sampleInfo':{'sampleBlockNum':10,'testBlockId':1},'algorithm':'VRL_2P'}"
//	For VRL-MP:
//	"{'lambda':10.0,'data':{'fileName':'Superconductivty.mat','X':'XData_norm','Y':'yData'},'parties':{'partyNum':5,'attrBlockNum':5},'subDataNum':10000,'sampleInfo':{'sampleBlockNum':10,'testBlockId':1},'algorithm':'VRL_MP'}"

	public static void main(String[] args) {
		Common.startingExp();
		System.out.println("The input parameters are as follows:");
		System.out.println(args[0]);
		Map<String, Object> res=runExp(args[0]);
		System.out.println("The results of the algorithm are as follows:");
		System.out.println(JSON.toJSONString(res,SerializerFeature.PrettyFormat));
		Common.endingExp();
	}
}
