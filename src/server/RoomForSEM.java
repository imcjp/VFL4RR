package server;

import com.mathworks.toolbox.javabuilder.MWNumericArray;
import common.MatComputeHelper;

/**
 * The code is for paper <b>"Efficient Vertical Federated Learning Method for Ridge Regression of Large-Scale Samples via Least-Squares Solution"</b>. <br/>
 * The class implements the SEM. It is a room, waiting for participant i and participant j to join in and execute the SEM protocol.
 */
public class RoomForSEM extends RoomForProtocol<int[]>{

	@Override
	public void run() {
		int[] parm1=objs.get(0);
		int[] parm2=objs.get(1);
		String player1=players.get(0);
		String player2=players.get(1);
		if (parm2[2]==1 && parm1[2]==2){
			int[] t=parm1;
			parm1=parm2;
			parm2=t;
			String st=player1;
			player1=player2;
			player2=st;
		}else if (parm2[2]==2 && parm1[2]==1) {
			
		}else{
			System.out.println("The 2 matrices number error...");
			return ;
		}
		if (parm1[1]!=parm2[0]) {
			System.out.println("Matrix shape mismatch, cannot multiply...");
			return ;
		}
		double errW=1000;
		MWNumericArray A=MatComputeHelper.randn(parm1[0], parm1[1]);
		MWNumericArray B=MatComputeHelper.randn(parm2[0], parm2[1]);
		A=MatComputeHelper.mul(A, errW);
		B=MatComputeHelper.mul(B, errW);
		MWNumericArray C=MatComputeHelper.mul(A, B);
		MWNumericArray C1=MatComputeHelper.randn(parm1[0], parm2[1]);
		C1=MatComputeHelper.mul(C1, (errW*10)*(errW*10));
		MWNumericArray C2=MatComputeHelper.subtract(C, C1);
		agent.sendMessage(player1, parm1[3]+1, C1);
		agent.sendMessage(player2, parm2[3]+1, C2);
		agent.sendMessage(player1, parm1[3]+2, new Object[]{player2,parm2[3]});
		agent.sendMessage(player2, parm2[3]+2, new Object[]{player1,parm1[3]});
		agent.sendMessage(player1, parm1[3]+3, A);
		agent.sendMessage(player2, parm2[3]+3, B);		
	}

	@Override
	protected void init() {
		maxPlayer=2;
	}

}
