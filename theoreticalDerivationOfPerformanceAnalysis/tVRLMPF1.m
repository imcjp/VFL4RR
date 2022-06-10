clear all;
% define s_ia1_m as sum of d_i from d_i+1 to d_m
% define s_1_is1 as sum of d_i from d_1 to d_i-1
syms d_i d_ia1_m d_1_is1 n m
%% Analysis for role "F1", the source is from the function run in class algs.vrlmp.Party, the Algorithm is VRL-MP.
f = sym(zeros(1,6));
% [Code from] MWNumericArray Q=MatComputeHelper.randOrthMat(d);
g = [0 , d_i^2 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] MWNumericArray X=MatComputeHelper.mul(X0, Q);
g = [0 , d_i^2*n , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] MWNumericArray Xt = MatComputeHelper.transpose(MatComputeHelper.mul(X, omiga));
g = [0 , d_i*n , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] F1s[i]=coMul(X, roomNum, 2);
g = tSColMul1(d_1_is1,d_i,n,i-1);
g = g(2,:);
f = f + g;
% [Code from] MWNumericArray F1=coMul(Xt, roomNum, 1);
g = tSColMul2(d_i,d_ia1_m,n,m-i);
g = g(1,:);
f = f + g;
% [Code from] EncMat encF1=new EncMatMultThread(F1,pubKey);
g = [d_i*d_ia1_m , 0 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] sendMessage(algInfo.getParty(i), 21, encF1);
g = [0 , 0 , d_i*d_ia1_m , 0 , 0 , 0];
f = f + g;
% [Code from] EncMat F2=(EncMat) msg.getObj();
g = [0 , 0 , 0 , 0 , d_1_is1*d_i , 0];
f = f + g;
% [Code from] encMs[sid]=F2.add(F1s[sid]);
g = [d_1_is1*d_i , 0 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] MWNumericArray y1=coMul(Xt, roomNum, 1);
g = tColMul(d_i,1,n);
g = g(1,:);
f = f + g;
% [Code from] EncMat ency1=new EncMatMultThread(y1,pubKey);
g = [d_i , 0 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] sendMessage(algInfo.getParty(mxPid-1), 20, ency1);
g = [0 , 0 , d_i , 0 , 0 , 0];
f = f + g;
% [Code from] MatComputeHelper.mul(MatComputeHelper.eye(d), algInfo.getLambda()*omiga));
g = [0 , d_i^2*n+d_i , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] MWNumericArray R = MatComputeHelper.inv(XX);
g = [0 , d_i^3 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] encR=new EncMatMultThread(R,pubKey);
g = [d_i^2 , 0 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] sendMessage(algInfo.getParty(pid+1), 22, encR);
g = [0 , 0 , d_i^2 , 0 , 0 , 0];
f = f + g;
% [Code from] encResW=(EncMat) msg.getObj();
g = [0 , 0 , 0 , 0 , d_i , 0];
f = f + g;
% [Code from] MWNumericArray resw=decr(encResW);
g = tDecr(d_i,1);
g = g(1,:);
f = f + g;
% [Code from] res=MatComputeHelper.mul(Q, resw);
g = [0 , d_i^2 , 0 , 0 , 0 , 0];
f = f + g;
res = f;
%% Substituting the symbols of F1
syms d_1 d_2_m d_1_m
res = subs(res,d_i,d_1);
res = subs(res,d_ia1_m,d_2_m);
res = subs(res,d_1_is1,0);
res(1:5) = subs(res(1:5),d_2_m,d_1_m-d_1);
disp('Analysis for role "F1", the source is from the function run in class algs.vrlmp.Party, the Algorithm is VRL-MP.');
showRes(res)