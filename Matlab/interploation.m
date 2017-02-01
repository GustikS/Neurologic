clc;
y1=cell2mat(y);

xi=x(find(~isnan(y1)));
yi=y1(find(~isnan(y1)));

result=interp1(xi,yi,x,'spline',nanmean(y1));

plot(x,result)