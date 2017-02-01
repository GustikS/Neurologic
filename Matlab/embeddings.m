clc; close all; clear all;

[weightMatrix, lambdaH, kappaH] = importWeights('C:\Users\Gusta\googledrive\Github\LRNNoldVersion\Neurologic\weights\matrix-weightMatrix.csv');

atomGroups = 1:3;

real = find(sum(weightMatrix(atomGroups,:),1));

atoms = weightMatrix(atomGroups,real);
wNames = lambdaH(real);

[coeff,score] = princomp(zscore(atoms'));
% [score, mapping] = compute_mapping(weights,'SNE');

%%

scatter(score(:,1),score(:,2),'b');
hold on
text(score(:,1),score(:,2),wNames,'color','black');

%%
