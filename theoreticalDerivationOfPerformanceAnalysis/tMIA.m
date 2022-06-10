function [res] = tMIA(n)
% The Time cost of MIA for M with shape n*n
% The return is a 6-tuple, with each element defined as follows:
% 1. Encrypted Computation Cost
% 2. Unencrypted Computation Cost
% 3. Encrypted Sending Cost
% 4. Unencrypted Sending Cost
% 5. Encrypted Receiving Cost
% 6. Unencrypted Receiving Cost
% First line is the cost of party F
% Second line is the cost of the coordinator C
%% Analysis for role "F", the source is from the function inv in class client.ClientForParticipant, the Protocol is MIA.
f = sym(zeros(1,6));
% [Code from] MWNumericArray errA=MatComputeHelper.randn(encA.getN(), encA.getM());
g = [0 , n^2 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] EncMat inA=EncMat.mul2(errA, encA);
g = [n^3 , 0 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] rf=new ReqFunc(this.getName(), NameOfProtocols.INV, port, inA);
g = [0 , 0 , n^2 , 0 , 0 , 0];
f = f + g;
% [Code from] EncMat enciA=(EncMat) msg.getObj();
g = [0 , 0 , 0 , 0 , n^2 , 0];
f = f + g;
% [Code from] EncMat res=enciA.mul(errA);
g = [n^3 , 0 , 0 , 0 , 0 , 0];
f = f + g;
F = f;
%% Analysis for role "C", the source is from the function run in class server.ProtocolThread, the Protocol is MIA.
f = sym(zeros(1,6));
% [Code from] Object[] params = obj.getParams();
g = [0 , 0 , 0 , 0 , n^2 , 0];
f = f + g;
% [Code from] MWNumericArray A = encA.getDecrMW(priKey);
g = [n^2 , 0 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] MWNumericArray iA = MatComputeHelper.inv(A);
g = [0 , n^3 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] EncMat enciA = new EncMatMultThread(iA, agent.getPubKey());
g = [n^2 , 0 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] agent.sendMessage(obj.getSender(), obj.getRetPort(), enciA);
g = [0 , 0 , n^2 , 0 , 0 , 0];
f = f + g;
C = f;
res=[F;C];
end

