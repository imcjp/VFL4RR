clear all;
% {"head":"(^-^)/ExpDesc",
% "ʵ����":"����ѧϰ��ع��ʵ��",
% "ʵ����":"VRG�㷨�з������ļ�������",
% "����":"���ñ��ʽ�Լ������Ľ��з���",
% "�ؼ���":"���ʽ�������������ģ�ͨ������",
% "��Ҫ��":4,
% "����":"�̽�ƽ"}
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