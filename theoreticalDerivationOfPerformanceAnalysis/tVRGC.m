clear all;
% {"head":"(^-^)/ExpDesc",
% "实验组":"联邦学习岭回归的实验",
% "实验名":"VRG算法中服务器的计算消耗",
% "描述":"采用表达式对计算消耗进行分析",
% "关键词":"表达式分析，计算消耗，通信消耗",
% "重要性":4,
% "作者":"蔡剑平"}
syms d_1 d_2 n T
% f=sym(zeros(1,6));
% %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% g=tDecr(d_1,1);
% f=f+g(2,:);
% g=tDecr(d_2,1);
% f=f+g(2,:);
% %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% showExp(f,{'n'})
%% Analysis for role "C", the source is from the function run in class exps.ExpForVRG, the Algorithm is VRG.
f = sym(zeros(1,6));
% [Code from] EncMat encL = (EncMat) msg.getObj();
g = [0 , 0 , 0 , 0 , 1 , 0];
f = f + g;
% [Code from] fval = coor.decr(encL).getDouble();
g = [1 , 0 , 0 , 0 , 0 , 0];
f = f + g;
% [Code from] //Here, Party 1 call client.ClientForParticipant.decr, the coordinator executes passively.
g = tDecr(d_1,1);
g = g(2,:);
f = f + g;
% [Code from] //Here, Party 2 call client.ClientForParticipant.decr, the coordinator executes passively.
g = tDecr(d_2,1);
g = g(2,:);
f = f + g;
res = f*T;
disp('Analysis for role "C", the source is from the function run in class exps.ExpForVRG, the Algorithm is VRG.');
showRes(res)