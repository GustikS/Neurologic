function nic = drawSubMatrix(mat,lambdaH,kappaH)

% set(gca,'Position',[0.1 0.3 0.87 0.66])
 set(gca,'Position',[0.1 0.18 0.87 0.80])

imagesc(mat);            %# Create a colored plot of the matrix values
colormap(flipud(gray));  %# Change the colormap to gray (so higher values are
                         %#   black and lower values are white)

textStrings = num2str(mat(:),'%0.2f');  %# Create strings from the matrix values
textStrings = strtrim(cellstr(textStrings));  %# Remove any space padding


[x,y] = meshgrid(1:size(mat,2),1:size(mat,1));   %# Create x and y coordinates for the strings
hStrings = text(x(:),y(:),textStrings(:),...      %# Plot the strings
                'HorizontalAlignment','center');
midValue = mean(get(gca,'CLim'));  %# Get the middle value of the color range
textColors = repmat(mat(:) > midValue,1,3);  %# Choose white or black for the
                                             %#   text color of the strings so
                                             %#   they can be easily seen over
                                             %#   the background color
set(hStrings,{'Color'},num2cell(textColors,2));  %# Change the text colors

set(gca,'XTick',1:size(mat,2),...                         %# Change the axes tick marks
        'XTickLabel',lambdaH,...  %#   and tick labels
        'YTick',1:size(mat,1),...
        'YTickLabel',kappaH,...
        'TickLength',[0 0]);

XTickLabel = get(gca,'XTickLabel');
set(gca,'XTickLabel',' ');
hxLabel = get(gca,'XLabel');
set(hxLabel,'Units','data');
xLabelPosition = get(hxLabel,'Position');
y = xLabelPosition(2);
XTick = get(gca,'XTick');
y=repmat(y,length(XTick),1);
% fs = get(gca,'fontsize');
fs = 16;
hText = text(XTick, y, XTickLabel,'fontsize',fs);
% set(hText,'Rotation',0,'HorizontalAlignment','right','interpreter','none');
set(hText,'HorizontalAlignment','center','interpreter','none');
set(gca,'fontsize',fs);