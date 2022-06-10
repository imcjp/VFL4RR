clear all;
syms d_i d_ia1_m d_1_is1 d_1_i n i m d_1_m d2_1_m
%% Analysis for role "C", the source is from the function run in class algs.vrlmp.Party, the Algorithm is VRL-MP.
%% Passive Execution by F2 - Fm
f = sym(zeros(1,6));
% [Code from] //F1s[i]=coMul(X, roomNum, 2);
% This is a special computing for sum_(2,m)(tSColMul1(d_1_is1,d_i,n,i-1))
% Define d2_1_m as sum_(1,m)(d_i^2);
g = [0,(d_1_m^2-d2_1_m)*(n+1)/2+d_1_m*(m-1)*n,0,(d_1_m^2-d2_1_m)+d_1_m*(m-1)*n,0,m*(m - 1)];
f = f + g;
resSpec = f;
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
f = sym(zeros(1,6));
% [Code from] //<Passive Execution> EncMat encB=mul(encMrt, encR0);
g = tMMA(d_i,d_1_is1,d_1_is1);
g = g(2,:);
f = f + g;
% [Code from] //<Passive Execution> EncMat encR4=mul(encB, encMr);
g = tMMA(d_i,d_i,d_1_is1);
g = g(2,:);
f = f + g;
% [Code from] //<Passive Execution> encR4=inv(encR4);
g = tMIA(d_i);
g = g(2,:);
f = f + g;
% [Code from] //<Passive Execution> EncMat encR1=mul(encBt, encR4);
g = tMMA(d_1_is1,d_i,d_i);
g = g(2,:);
f = f + g;
% [Code from] //<Passive Execution> encR1=mul(encR1, encB);
g = tMMA(d_1_is1,d_1_is1,d_i);
g = g(2,:);
f = f + g;
% [Code from] //<Passive Execution> encR2=mul(encR2,encR1);
g = tMMA(d_i,d_1_is1,d_1_is1);
g = g(2,:);
f = f + g;
% [Code from] //<Passive Execution> MWNumericArray resw=decr(encResW);
g = tDecr(d_i,1);
g = g(2,:);
f = f + g;
cumSumRes = f;
syms d_1_m d_i_m d_1_i
cumSumRes(3) = simplify(subs(cumSumRes(3),d_1_is1,d_1_i-d_i));
%% Passive Execution by F1
f = sym(zeros(1,6));
% [Code from] //<Passive Execution> MWNumericArray resw=decr(encResW);
g = tDecr(d_i,1);
g = g(2,:);
f = f + g;
res1 = f;
syms d_1 d_2_m d_1_m
res1 = subs(res1,d_i,d_1);
res1 = subs(res1,d_ia1_m,d_2_m);
res1 = subs(res1,d_1_is1,0);
%% Passive Execution by only Fm
f = sym(zeros(1,6));
% [Code from] //<Passive Execution> y2s[i]=coMul(y, roomNum, 2);
g = tSColMul1(d_1_is1,1,n,i-1);
g = g(3,:);
f = f + g;
% [Code from] //<Passive Execution> EncMat encw=mul(encR, encr);
g = tMMA(d_1_is1,1,d_1_is1);
g = g(2,:);
f = f + g;
resm = f;
syms d_m d_1_ms1  d_1_m m
resm = subs(resm,i,m);
resm = subs(resm,d_i,d_m);
resm = subs(resm,d_ia1_m,0);
resm = subs(resm,d_1_is1,d_1_ms1);
res = resSpec + res1 + resm;
%% disp
disp('Analysis for role "C", the source is from the function run in class algs.vrlmp.Party, the Algorithm is VRL-MP.');
expn={'n'};
res=simplify(res);
res=collect(res,expn);
res=simplify(res);
disp('Encrypted Operations:');
fprintf('\tComputation Cost:\tsum_(2 to m)(%s) + %s\n',cumSumRes(1),res(1));
fprintf('\tSending Cost:\t\tsum_(2 to m)(%s) + %s\n',cumSumRes(3),res(3));
fprintf('\tReceiving Cost:\t\tsum_(2 to m)(%s) + %s\n',cumSumRes(5),res(5));
disp('Unencrypted Operations:');
fprintf('\tComputation Cost:\tsum_(2 to m)(%s) + %s\n',cumSumRes(2),res(2));
fprintf('\tSending Cost:\t\tsum_(2 to m)(%s) + %s\n',cumSumRes(4),res(4));
fprintf('\tReceiving Cost:\t\t%s\n',simplify(symsum(cumSumRes(6),i,2,m)+res(6)));

