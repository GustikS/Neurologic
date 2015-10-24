function nic = displayWeightMatrix(name, type, atomCount, bondCount)

[allweights, lambdaH, kappaH] = importWeights(name);

% delete lambda from names of atoms
i=1;
for str = lambdaH
    if strfind(str{1}, '=') > 0
        res{i} = strtrim(str{1}(strfind(str{1}, '=')+2:end));
    else
        res{i} = str{1};
    end
    i = i+1;
end
lambdaH = res(1:end-1);

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
    kappaSubIndex = [7:7];
    lambdaSubIndex = [1:243];
    mat = allweights(kappaSubIndex,lambdaSubIndex);
    lH = lambdaH(1,lambdaSubIndex)';
    kH = kappaH(kappaSubIndex,1);
    
    drawSubMatrix(mat,lH,kH);
    title(strcat('final clustering at improvement step - ',num2str(step)));
end

if strcmp(type,'bonds')
    step = name(isstrprop(name, 'digit'));
    figure('name',step,'units','normalized','outerposition',[0 0 1 1]);
    %% bonds
    % this is where the bonds should be!!
    kappaSubIndex = [4:6];
    lambdaSubIndex = [(244+atomCount+1):(244+atomCount+bondCount)];
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
    lambdaSubIndex = [244:(244+atomCount-1)];
    mat = allweights(kappaSubIndex,lambdaSubIndex);
    lH = lambdaH(1,lambdaSubIndex)';
    kH = kappaH(kappaSubIndex,1);
    
    drawSubMatrix(mat,lH,kH);
    %title(strcat('atom-type clustering at improvement step - ',num2str(step)));
end
