clear all  clc, close all;

% type = 'finals';
% type = 'bonds';
% type = 'atoms';
% type = 'all';

 type = 'letters';
 mypath = '../weights/flows/letters/';
 
%  type = 'times';
%  mypath = '../weights/flows/relations/';

% mypath = '../weights/mutaFlipAtRestart/';
% mypath = '../weights/';

% mypath = '../weights/mutasymmetryb/';
% atomCount = 36;
% bondCount = 6;

% mypath = '../weights/ptcmr/';
% atomCount = 19;
% bondCount = 4;


atomCount = 19;
bondCount = 4;

files = dir(strcat(mypath,'*.csv'));
% sort the matrix weight file by date (so better do not change that)
[~,idx] = sort([files.datenum]);

for file = files(idx)'
    displayWeightMatrix(strcat(mypath,file.name), type, atomCount, bondCount);
%      break
end