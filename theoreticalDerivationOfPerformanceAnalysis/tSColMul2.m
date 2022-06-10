function [res] = tSColMul2(n,sn_1_m,k,ln)
% The Cumulative time cost of SEM for A * Bi for i from 1 to n, where A with shape n*k and Bi with shape k*m_i satisfying sn_1_m=sum(m_i) for i from 1 to m.
% ln is the number of Bi.
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
syms tmpn tmpm1 tmpm2 tmpk
% Define consTerm as the expression of the constant term in tColMul
consTerm=simplify(tColMul(tmpn,tmpm1,tmpk)+tColMul(tmpn,tmpm2,tmpk)-tColMul(tmpn,tmpm1+tmpm2,tmpk));
consTerm=subs(consTerm,{tmpn,tmpk},{n,k});
res=simplify(tColMul(n,sn_1_m,k)+consTerm*(ln-1));
end

