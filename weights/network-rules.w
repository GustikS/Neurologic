pathK(A,B) :- pathL(A,B).
pathK(A,B) :- nextL(A,B).
nextK(A,B) :- nextL(A,B).
pathL(A,B) :- nextK(A,X),pathK(X,B).
finalK :- pathL(a,e).
finalLambda :- finalK.
