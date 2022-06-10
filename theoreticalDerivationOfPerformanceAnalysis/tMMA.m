function [res] = tMMA(n,m,k)
% The Time cost of MMA for A * B, where A with shape n*k and B with shape k*m
% The return is a 6-tuple, with each element defined as follows:
% 1. Encrypted Computation Cost
% 2. Unencrypted Computation Cost
% 3. Encrypted Sending Cost
% 4. Unencrypted Sending Cost
% 5. Encrypted Receiving Cost
% 6. Unencrypted Receiving Cost
% First line is the cost of party F
% Second line is the cost of the coordinator C
%% Analysis for role "F", the source is from the function mul in class client.ClientForParticipant, the Protocol is MMA.
f = sym(zeros(1,6));
% [Code from] MWNumericArray errA=MatComputeHelper.mul(MatComputeHelper.randn(encA.getN(), encA.getM()),errW);
g = [0 , n*k , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] MWNumericArray errB=MatComputeHelper.mul(MatComputeHelper.randn(encB.getN(), encB.getM()),errW);
g = [0 , k*m , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] EncMat inA=encA.add(errA);
g = [n*k , 0 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] EncMat inB=encB.add(errB);
g = [k*m , 0 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] rf=new ReqFunc(this.getName(), NameOfProtocols.MUL, port, inA,inB);
g = [0 , 0 , n*k+m*k , 0 , 0 , 0];
f = f + g;
% [Code from] EncMat encC=(EncMat) msg.getObj();
g = [0 , 0 , 0 , 0 , n*m , 0];
f = f + g;
% [Code from] MWNumericArray errAB=MatComputeHelper.mul(errA, errB);
g = [0 , k*m*n , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] encC=encC.subtract(encA.mul(errB)).subtract(EncMat.mul2(errA, encB)).subtract(errAB);
g = [2*k*m*n+4*n*m , 0 , 0 , 0 , 0 , 0];
f = f + g;
F=f;
%% Analysis for role "C", the source is from the function run in class server.ProtocolThread, the Protocol is MMA.
f = sym(zeros(1,6));
% [Code from] EncMat encA = (EncMat) params[0];
g = [0 , 0 , 0 , 0 , n*k , 0];
f = f + g;
% [Code from] EncMat encB = (EncMat) params[1];
g = [0 , 0 , 0 , 0 , m*k , 0];
f = f + g;
% [Code from] MWNumericArray A = encA.getDecrMW(priKey);
g = [n*k , 0 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] MWNumericArray B = encB.getDecrMW(priKey);
g = [m*k , 0 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] MWNumericArray C = MatComputeHelper.mul(A, B);
g = [0 , n*m*k , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] EncMat encC = new EncMatMultThread(C, agent.getPubKey());
g = [n*m , 0 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] agent.sendMessage(obj.getSender(), obj.getRetPort(), encC);
g = [0 , 0 , n*m , 0 , 0 , 0];
f = f + g;
C=f;
res=[F;C];
end

