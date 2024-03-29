clear all;
syms d_i d_ia1_m d_1_is1 n i
%% Analysis for role "Fm", the source is from the function run in class algs.vrlmp.Party, the Algorithm is VRL-MP.
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
% [Code from] EncMat F2=(EncMat) msg.getObj();
g = [0 , 0 , 0 , 0 , d_1_is1*d_i , 0];
f = f + g;
% [Code from] encMs[sid]=F2.add(F1s[sid]);
g = [d_1_is1*d_i , 0 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] y2s[i]=coMul(y, roomNum, 2);
g = tSColMul1(d_1_is1,1,n,i-1);
g = g(2,:);
f = f + g;
% [Code from] encYs[pid]=new EncMat(MatComputeHelper.mul(Xt, y), pubKey);
g = [d_i , d_i*n , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] EncMat encY1=(EncMat) msg.getObj();
g = [0 , 0 , 0 , 0 , d_1_is1 , 0];
f = f + g;
% [Code from] encYs[sid]=encY1.add(y2s[sid]);
g = [d_1_is1 , 0 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] MatComputeHelper.mul(MatComputeHelper.eye(d), algInfo.getLambda()*omiga));
g = [0 , d_i^2*n+d_i , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] EncMat encR0=(EncMat) msg.getObj();
g = [0 , 0 , 0 , 0 , d_1_is1*d_1_is1 , 0];
f = f + g;
% [Code from] EncMat encB=mul(encMrt, encR0);
g = tMMA(d_i,d_1_is1,d_1_is1);
g = g(1,:);
f = f + g;
% [Code from] EncMat encR4=mul(encB, encMr);
g = tMMA(d_i,d_i,d_1_is1);
g = g(1,:);
f = f + g;
% [Code from] EncMat encC=new EncMatMultThread(C,pubKey);
g = [d_i^2 , 0 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] encR4=encC.subtract(encR4);
g = [d_i^2 , 0 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] encR4=inv(encR4);
g = tMIA(d_i);
g = g(1,:);
f = f + g;
% [Code from] encR4=encR4.add(encR4.transpose()).mul(0.5);
g = [2*d_i^2 , 0 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] EncMat encR1=mul(encBt, encR4);
g = tMMA(d_1_is1,d_i,d_i);
g = g(1,:);
f = f + g;
% [Code from] encR1=mul(encR1, encB);
g = tMMA(d_1_is1,d_1_is1,d_i);
g = g(1,:);
f = f + g;
% [Code from] encR1=encR0.add(encR1);
g = [d_1_is1*d_1_is1 , 0 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] MWNumericArray G=MatComputeHelper.mul(MatComputeHelper.inv(C),-1);
g = [0 , d_i^3+d_i^2 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] EncMat encR2=EncMat.mul2(G, encMrt);
g = [d_i^2*d_1_is1 , 0 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] encR2=mul(encR2,encR1);
g = tMMA(d_i,d_1_is1,d_1_is1);
g = g(1,:);
f = f + g;
% [Code from] EncMat encw=mul(encR, encr);
g = tMMA(d_1_is1,1,d_1_is1);
g = g(1,:);
f = f + g;
% [Code from] for (int i = 0; i < mxPid-1; i++) {
g = [0 , 0 , d_1_is1 , 0 , 0 , 0];
f = f + g;
% [Code from] MWNumericArray resw=decr(encResW);
g = tDecr(d_i,1);
g = g(1,:);
f = f + g;
% [Code from] res=MatComputeHelper.mul(Q, resw);
g = [0 , d_i^2 , 0 , 0 , 0 , 0];
f = f + g;
res = f;
%% Substituting the data of F1
syms d_m d_1_ms1  d_1_m m
res = subs(res,i,m);
res = subs(res,d_i,d_m);
res = subs(res,d_ia1_m,0);
res = subs(res,d_1_is1,d_1_ms1);
res(5) = subs(res(5),d_1_ms1,d_1_m-d_m);
disp('Analysis for role "Fm", the source is from the function run in class algs.vrlmp.Party, the Algorithm is VRL-MP.');
showRes(res)