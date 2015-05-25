function nic = displayWeightMatrix(name, type)

[allweights, lambdaH, kappaH] = importWeights(name);

if strcmp(type,'all')
    %% the whole matrix
    figure('name',name,'units','normalized','outerposition',[0 0 1 1]);
    imagesc(allweights);            %# Create a colored plot of the matrix values
end

if strcmp(type,'finals')
    step = name(isstrprop(name, 'digit'));
    figure('name',step,'units','normalized','outerposition',[0 0 1 1]);
    %% bonds
    % this is where the finals should be!!
    kappaSubIndex = [6:6];
    lambdaSubIndex = [1:243];
    mat = allweights(kappaSubIndex,lambdaSubIndex);
    lH = lambdaH(1,lambdaSubIndex)';
    kH = kappaH(kappaSubIndex,1);
    
    drawSubMatrix(mat,lH,kH);
    title(strcat('bond-type clustering at improvement step - ',num2str(step)));
end

if strcmp(type,'bonds')
    step = name(isstrprop(name, 'digit'));
    figure('name',step,'units','normalized','outerposition',[0 0 1 1]);
    %% bonds
    % this is where the bonds should be!!
    kappaSubIndex = [4:6];
    lambdaSubIndex = [280:size(allweights,2)];
    mat = allweights(kappaSubIndex,lambdaSubIndex);
    lH = lambdaH(1,lambdaSubIndex)';
    kH = kappaH(kappaSubIndex,1);
    
    drawSubMatrix(mat,lH,kH);
    title(strcat('bond-type clustering at improvement step - ',num2str(step)));
end

if strcmp(type,'atoms')
    step = name(isstrprop(name, 'digit'));
    figure('name',step,'units','normalized','outerposition',[0 0 1 1]);
    %% atoms
    % this is where the atoms should be!!
    kappaSubIndex = [1:3];
    lambdaSubIndex = [244:279];
    mat = allweights(kappaSubIndex,lambdaSubIndex);
    lH = lambdaH(1,lambdaSubIndex)';
    kH = kappaH(kappaSubIndex,1);
    
    drawSubMatrix(mat,lH,kH);
    title(strcat('atom-type clustering at improvement step - ',num2str(step)));
end
