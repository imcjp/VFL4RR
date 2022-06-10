function showRes(res)
% Show for the time cost of 6-tuple
% 1. Encrypted Computation Cost
% 2. Unencrypted Computation Cost
% 3. Encrypted Sending Cost
% 4. Unencrypted Sending Cost
% 5. Encrypted Receiving Cost
% 6. Unencrypted Receiving Cost
expn={'n'};
res=simplify(res);
res=collect(res,expn);
res=simplify(res);
disp('Encrypted Operations:');
fprintf('\tComputation Cost:\t%s\n',res(1));
fprintf('\tSending Cost:\t\t%s\n',res(3));
fprintf('\tReceiving Cost:\t\t%s\n',res(5));
disp('Unencrypted Operations:');
fprintf('\tComputation Cost:\t%s\n',res(2));
fprintf('\tSending Cost:\t\t%s\n',res(4));
fprintf('\tReceiving Cost:\t\t%s\n',res(6));
end

