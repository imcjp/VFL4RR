function [res] = tSEM(n,m,k)
% The Time cost of SEM for A * B, where A with shape n*k and B with shape k*m
% The return is a 6-tuple, with each element defined as follows:
% 1. Encrypted Computation Cost
% 2. Unencrypted Computation Cost
% 3. Encrypted Sending Cost
% 4. Unencrypted Sending Cost
% 5. Encrypted Receiving Cost
% 6. Unencrypted Receiving Cost
% First line is the cost of party Fi
% Second line is the cost of party Fj
% Third line is the cost of the coordinator C
%% Analysis for role "Fi", the source is from the function coMul in class client.ClientForParticipant, the Protocol is SEM.
f = sym(zeros(1,6));
% [Code from] } while (!sendMessage(serverName, 80, rf));
g = [0 , 0 , 0 , 1 , 0 , 0];
f = f + g;
% [Code from] MWNumericArray errC=(MWNumericArray) msg.getObj();
g = [0 , 0 , 0 , 0 , 0 , m*n];
f = f + g;
% [Code from] MWNumericArray Q=MatComputeHelper.randOrthMat(sz[0]);
g = [0 , n*n , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] M=MatComputeHelper.mul(Q,M);
g = [0 , n*n*k , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] MWNumericArray errA=(MWNumericArray) msg.getObj();
g = [0 , 0 , 0 , 0 , 0 , n*k];
f = f + g;
% [Code from] MWNumericArray Alpha=MatComputeHelper.subtract(U, errA);
g = [0 , n*k , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] sendMessage(copper, coPort+4, Alpha);
g = [0 , 0 , 0 , n*k , 0 , 0];
f = f + g;
% [Code from] MWNumericArray Beta=(MWNumericArray)msg.getObj();
g = [0 , 0 , 0 , 0 , 0 , m*k];
f = f + g;
% [Code from] res=MatComputeHelper.add(errC,MatComputeHelper.mul(errA, Beta));
g = [0 , n*m*k+n*m , 0 , 0 , 0 , 0];
f = f + g;
Fi = f;
%% Analysis for role "Fj", the source is from the function coMul in class client.ClientForParticipant, the Protocol is SEM.
f = sym(zeros(1,6));
% [Code from] } while (!sendMessage(serverName, 80, rf));
g = [0 , 0 , 0 , 1 , 0 , 0];
f = f + g;
% [Code from] MWNumericArray errC=(MWNumericArray) msg.getObj();
g = [0 , 0 , 0 , 0 , 0 , m*n];
f = f + g;
% [Code from] MWNumericArray Q=MatComputeHelper.randOrthMat(sz[1]);
g = [0 , m*m , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] M=MatComputeHelper.mul(Q,M);
g = [0 , m*m*k , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] MWNumericArray errB=(MWNumericArray) msg.getObj();
g = [0 , 0 , 0 , 0 , 0 , m*k];
f = f + g;
% [Code from] MWNumericArray Beta=MatComputeHelper.subtract(V, errB);
g = [0 , m*k , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] sendMessage(copper, coPort+4, Beta);
g = [0 , 0 , 0 , m*k , 0 , 0];
f = f + g;
% [Code from] MWNumericArray Alpha=(MWNumericArray)msg.getObj();
g = [0 , 0 , 0 , 0 , 0 , n*k];
f = f + g;
% [Code from] res=MatComputeHelper.add(errC, MatComputeHelper.mul(Alpha, errB));
g = [0 , n*m*k+n*m , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] res=MatComputeHelper.add(res, MatComputeHelper.mul(Alpha, Beta));
g = [0 , n*m*k+n*m , 0 , 0 , 0 , 0];
f = f + g;
Fj = f;
%% Analysis for role "C", the source is from the function run in class server.RoomForSEM, the Protocol is SEM.
f = sym(zeros(1,6));
% [Code from] String player1=players.get(0);
g = [0 , 0 , 0 , 0 , 0 , 1];
f = f + g;
% [Code from] String player2=players.get(1);
g = [0 , 0 , 0 , 0 , 0 , 1];
f = f + g;
% [Code from] A=MatComputeHelper.mul(A, errW);
g = [0 , n*k , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] B=MatComputeHelper.mul(B, errW);
g = [0 , m*k , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] MWNumericArray C=MatComputeHelper.mul(A, B);
g = [0 , m*n*k , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] MWNumericArray C2=MatComputeHelper.subtract(C, C1);
g = [0 , m*n , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] agent.sendMessage(player1, parm1[3]+1, C1);
g = [0 , 0 , 0 , m*n , 0 , 0];
f = f + g;
% [Code from] agent.sendMessage(player2, parm2[3]+1, C2);
g = [0 , 0 , 0 , m*n , 0 , 0];
f = f + g;
% [Code from] agent.sendMessage(player1, parm1[3]+3, A);
g = [0 , 0 , 0 , n*k , 0 , 0];
f = f + g;
% [Code from] agent.sendMessage(player2, parm2[3]+3, B);
g = [0 , 0 , 0 , m*k	 , 0 , 0];
f = f + g;
C = f;
res=[Fi;Fj;C];
end

