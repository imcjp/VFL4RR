function [res] = tDecr(n,m)
% The Time cost of Security Decryption for M with shape n*m
% The return is a 6-tuple, with each element defined as follows:
% 1. Encrypted Computation Cost
% 2. Unencrypted Computation Cost
% 3. Encrypted Sending Cost
% 4. Unencrypted Sending Cost
% 5. Encrypted Receiving Cost
% 6. Unencrypted Receiving Cost
% First line is the cost of party F
% Second line is the cost of the coordinator C
%% Analysis for role "F", the source is from the function decr in class client.ClientForParticipant, the Protocol is Decr.
f = sym(zeros(1,6));
% [Code from] MWNumericArray errA=MatComputeHelper.mul(MatComputeHelper.randn(encA.getN(), encA.getM()),errW);
g = [0 , n*m , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] EncMat inA=encA.add(errA);
g = [n*m , 0 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] rf=new ReqFunc(this.getName(), NameOfProtocols.DECR, port, inA);
g = [0 , 0 , n*m , 0 , 0 , 0];
f = f + g;
% [Code from] MWNumericArray res=(MWNumericArray) msg.getObj();
g = [0 , 0 , 0 , 0 , 0 , n*m];
f = f + g;
% [Code from] res=MatComputeHelper.subtract(res, errA);
g = [0 , n*m , 0 , 0 , 0 , 0];
f = f + g;
F = f;
%% Analysis for role "C", the source is from the function run in class server.ProtocolThread, the Protocol is Decr.
f = sym(zeros(1,6));
% [Code from] Object[] params = obj.getParams();
g = [0 , 0 , 0 , 0 , n*m , 0];
f = f + g;
% [Code from] MWNumericArray res = encA.getDecrMW(priKey);
g = [n*m , 0 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] agent.sendMessage(obj.getSender(), obj.getRetPort(), res);
g = [0 , 0 , 0 , n*m , 0 , 0];
f = f + g;
C = f;
res=[F;C];
end

