plot(1:100);

% make the axis smaller
pos = get(gca, 'Position');
set(gca,'Position',[pos(1), .2, pos(3) 0.7]);

% place custom text instead of xlabel
% note that the position is relative to your X/Y axis values
t = text(50, -5, {'X-axis' 'label'}, 'FontSize', 14);
set(t,'HorizontalAlignment','right','VerticalAlignment','top', ...
'Rotation',45);