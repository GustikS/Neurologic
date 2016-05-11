apAtom4(X0) :- male(X0).
apAtom4(X0) :- female(X0).
apAtom5Lambda(X0) :- apAtom4(X0).
finalKappa(X0) :- apAtom5Lambda(X0).
finalLambda :- finalKappa(X).
