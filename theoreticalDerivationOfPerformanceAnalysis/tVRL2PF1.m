clear all;
syms d_1 d_2 n
%% Analysis for role "F1", the source is from the function run in class algs.vrl2p.Party1, the Algorithm is VRL-2P.
f = sym(zeros(1,6));
% [Code from] MWNumericArray Q=MatComputeHelper.randOrthMat(d);
g = [0 , d_1^2 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] MWNumericArray X=MatComputeHelper.mul(X0, Q);
g = [0 , n*d_1^2 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] MWNumericArray Xt = MatComputeHelper.mul(MatComputeHelper.transpose(X),omiga);
g = [0 , n*d_1 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] MWNumericArray F1=coMul(Xt, algInfo.getTestId()*Common.ROOMID_BIAS+1, 1);
g = tColMul(d_1,d_2,n);
g = g(1,:);
f = f + g;
% [Code from] EncMat encF1=new EncMatMultThread(F1,pubKey);
g = [d_1*d_2 , 0 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] sendMessage(algInfo.getParty(2), 21, encF1);
g = [0 , 0 , d_1*d_2 , 0 , 0 , 0];
f = f + g;
% [Code from] EncMat M12=(EncMat) msg.getObj();
g = [0 , 0 , 0 , 0 , d_1*d_2 , 0];
f = f + g;
% [Code from] MatComputeHelper.mul(MatComputeHelper.eye(d), algInfo.getLambda()*omiga));
g = [0 , d_1^2*n+d_1 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] MWNumericArray C = MatComputeHelper.inv(M11);
g = [0 , d_1^3 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] EncMat encC=new EncMatMultThread(C,pubKey);
g = [d_1^2 , 0 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] sendMessage(algInfo.getParty(2), 21, encC);
g = [0 , 0 , d_1^2 , 0 , 0 , 0];
f = f + g;
% [Code from] EncMat encCt = (EncMat) msg.getObj();
g = [0 , 0 , 0 , 0 , d_2^2 , 0];
f = f + g;
% [Code from] EncMat encR=mul(encCt, M21);
g = tMMA(d_2,d_1,d_2);
g = g(1,:);
f = f + g;
% [Code from] encR=mul(M12, encR);
g = tMMA(d_1,d_1,d_2);
g = g(1,:);
f = f + g;
% [Code from] encR=new EncMatMultThread(M11, pubKey).subtract(encR);
g = [d_1^2 , 0 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] encR=inv(encR);
g = tMIA(d_1);
g = g(1,:);
f = f + g;
% [Code from] MWNumericArray r11=coMul(Xt, algInfo.getTestId()*Common.ROOMID_BIAS+2, 1);
g = tColMul(d_1,1,n);
g = g(1,:);
f = f + g;
% [Code from] EncMat encr12=(EncMat) msg.getObj();
g = [0 , 0 , 0 , 0 , d_1 , 0];
f = f + g;
% [Code from] EncMat encr=encr12.add(r11);
g = [d_1 , 0 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] EncMat encu1=mul(encR, encr);
g = tMMA(d_1,1,d_1);
g = g(1,:);
f = f + g;
% [Code from] sendMessage(algInfo.getParty(2), 21, encu1);
g = [0 , 0 , d_1 , 0 , 0 , 0];
f = f + g;
% [Code from] EncMat encu2=(EncMat) msg.getObj();
g = [0 , 0 , 0 , 0 , d_2 , 0];
f = f + g;
% [Code from] EncMat encv1=EncMat.mul2(C, mul(M12, encu2));
g = tMMA(d_1,1,d_2);
g = g(1,:);
f = f + g;
% [Code from] //<Same as Last Line> EncMat encv1=EncMat.mul2(C, mul(M12, encu2));
g = [d_1^2 , 0 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] EncMat encw = encu1.subtract(encv1);
g = [d_1 , 0 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] res = MatComputeHelper.mul(Q,decr(encw));
g = tDecr(d_1,1);
g = g(1,:);
f = f + g;
% [Code from] //<Same as Last Line> res = MatComputeHelper.mul(Q,decr(encw));
g = [0 , d_1^2 , 0 , 0 , 0 , 0];
f = f + g;
res = f;
disp('Analysis for role "F1", the source is from the function run in class algs.vrl2p.Party1, the Algorithm is VRL-2P.');
showRes(res)