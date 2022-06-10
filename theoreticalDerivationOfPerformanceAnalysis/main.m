clear all;close all;clc;
disp('Part I. The Time Cost of Various Protocols');
syms n m k d_i d_j
disp('1. The Time Cost of Protocol MMA');
R=tMMA(n,m,k);
disp('a) Analysis for role "F"')
showRes(R(1,:))
disp('b) Analysis for role "C"')
showRes(R(2,:))
disp('2. The Time Cost of Protocol MIA');
R=tMIA(n);
disp('a) Analysis for role "F"')
showRes(R(1,:))
disp('b) Analysis for role "C"')
showRes(R(2,:))

disp('3. The Time Cost of Protocol SEM');
R=tColMulWithOrth(d_i,d_j,n);
disp('a) Analysis for role "Fi"')
showRes(R(1,:))
disp('b) Analysis for role "Fj"')
showRes(R(2,:))
disp('c) Analysis for role "C"')
showRes(R(3,:))

disp('4. The Time Cost of Secure Decryption');
R=tDecr(n,m);
disp('a) Analysis for role "F"')
showRes(R(1,:))
disp('b) Analysis for role "C"')
showRes(R(2,:))

disp('========================')
disp('Part II. The Time Cost of Various Algorithms');
disp('1. The Time Cost of Algorithm VRG');
fprintf('a) ')
tVRGF1
fprintf('b) ')
tVRGF2
fprintf('c) ')
tVRGC
disp('2. The Time Cost of Algorithm VRL-2P');
fprintf('a) ')
tVRL2PF1
fprintf('b) ')
tVRL2PF2
fprintf('c) ')
tVRL2PC
disp('3. The Time Cost of Algorithm VRL-MP');
fprintf('a) ')
tVRLMPF1
fprintf('b) ')
tVRLMPFi
fprintf('c) ')
tVRLMPFm
fprintf('d) ')
tVRLMPC