clc; clear all;
h = gcf;
axesObjs = get(h, 'Children');
dataObjs = get(axesObjs, 'Children');
wNames = {};
vectors = dataObjs(77).CData;

%%
[coeff,score] = princomp(zscore(vectors'));

figure
scatter(score(:,1),score(:,2),'b');
hold on
text(score(:,1),score(:,2),wNames,'color','black');

%%

figure
scatter3(vectors(1,:),vectors(2,:),vectors(3,:),'b');
hold on
text(vectors(1,:),vectors(2,:),vectors(3,:),wNames,'color','black');