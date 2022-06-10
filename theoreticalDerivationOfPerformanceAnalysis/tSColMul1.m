function [res] = tSColMul1(sn_1_n,m,k,ln)
% The Cumulative time cost of SEM for Ai * B for i from 1 to n, where Ai with shape n_i*k satisfying sn_1_n=sum(n_i) for i from 1 to n and B with shape k*m.
% ln is the number of Ai.
% PS: tColMul ignores the masking operation by the orthogonal matrix Q, due to their being counted elsewhere.
% The return is a 6-tuple, with each element defined as follows:
% 1. Encrypted Computation Cost
% 2. Unencrypted Computation Cost
% 3. Encrypted Sending Cost
% 4. Unencrypted Sending Cost
% 5. Encrypted Receiving Cost
% 6. Unencrypted Receiving Cost
% First line is the cost of party Fi
% Second line is the cost of party Fj
% Third line is the cost of the coordinator C
syms tmpn1 tmpn2 tmpm tmpk
% Define consTerm as the expression of the constant term in tColMul
consTerm=simplify(tColMul(tmpn1,tmpm,tmpk)+tColMul(tmpn2,tmpm,tmpk)-tColMul(tmpn1+tmpn2,tmpm,tmpk));
consTerm=subs(consTerm,{tmpm,tmpk},{m,k});
res=simplify(tColMul(sn_1_n,m,k)+consTerm*(ln-1));
end
