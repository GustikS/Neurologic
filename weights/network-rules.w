apAtom60 :- h_3(X0).
atomKappa_A2(X2) :- bond(X0,X1,X2).
lambda_A1(X0) :- atomKappa_A2(X0),apAtom60(atomKappa_A1,X1).
finalKappa(X) :- lambda_A1(X).
finalLambda(X0) :- finalKappa(X0).
