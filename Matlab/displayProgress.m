clear all  clc, close all;

% type = 'finals';
% type = 'bonds';
 type = 'atoms';
% type = 'all';

mypath = 'C:/Users/IBM_ADMIN/Google Drive/Neuralogic/sourcecodes/gusta/Neurologic/weights/';

files = dir(strcat(mypath,'*.csv'));
% sort the matrix weight file by date (so better do not change that)
[~,idx] = sort([files.datenum]);

for file = files(idx)'
    displayWeightMatrix(strcat(mypath,file.name), type);
end