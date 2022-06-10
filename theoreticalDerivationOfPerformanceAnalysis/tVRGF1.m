clear all;
syms d_1 d_2 n T
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%% Analysis for role "F1", the source is from the function run & update in class algs.vrg.Party1, the Algorithm is VRG.
f = sym(zeros(1,6));
% [Code from] MWNumericArray u=MatComputeHelper.mul(X, w);
g = [0 , n*d_1 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] EncMat encua=new EncMatMultThread(u, pubKey);
g = [n , 0 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] MWNumericArray La=MatComputeHelper.mul(MatComputeHelper.transpose(u), u);
g = [0 , n , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] La=MatComputeHelper.add(La, MatComputeHelper.mul(algInfo.getLambda(),MatComputeHelper.mul(MatComputeHelper.transpose(w), w)));
g = [0 , d_1+2 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] EncMat encLa=new EncMatMultThread(La, pubKey);
g = [1 , 0 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] sendMessage(algInfo.getParty(2), 21, encua);
g = [0 , 0 , n , 0 , 0 , 0];
f = f + g;
% [Code from] sendMessage(algInfo.getParty(2), 21, encLa);
g = [0 , 0 , 1 , 0 , 0 , 0];
f = f + g;
% [Code from] EncMat encdb=(EncMat) msg.getObj();
g = [0 , 0 , 0 , 0 , n , 0];
f = f + g;
% [Code from] EncMat encd=encdb.add(u);
g = [n , 0 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] EncMat encg=EncMat.mul2(Xt, encd).add(MatComputeHelper.mul(algInfo.getLambda(), w));
g = [n*d_1+d_1 , d_1 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] g=decr(encg);
g = tDecr(d_1,1);
g = g(1,:);
f = f + g;
% [Code from] g=MatComputeHelper.mul(1.0/n, g);
g = [0 , d_1 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] m=MatComputeHelper.add(MatComputeHelper.mul(beta1, m), MatComputeHelper.mul(1-beta1, g));
g = [0 , 3*d_1 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] v=MatComputeHelper.add(MatComputeHelper.mul(beta2, v), MatComputeHelper.mul(1-beta2, g2));
g = [0 , 3*d_1 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] MWNumericArray mHat=MatComputeHelper.mul(m, 1/(1-Math.pow(beta1, iter)));
g = [0 , d_1 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] MWNumericArray vHat=MatComputeHelper.mul(v, 1/(1-Math.pow(beta2, iter)));
g = [0 , d_1 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] dtWData[i]=learningRate*mHatData[i]/(Math.sqrt(vHatData[i])+epsilon);
g = [0 , 3*d_1 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] w=MatComputeHelper.subtract(w, dtW);
g = [0 , d_1 , 0 , 0 , 0 , 0];
f = f + g;
res = f*T;
disp('Analysis for role "F1", the source is from the function run & update in class algs.vrg.Party1, the Algorithm is VRG.');
showRes(res)