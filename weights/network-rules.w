father(C,F) :- parent(C,F),male(F).
mother(C,M) :- parent(C,M),female(M).
res :- father(C,F).
res :- mother(C,M).
finalLambda :- res.
