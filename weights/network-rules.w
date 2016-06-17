1,000000000000000 is-aEK/2(A,B) :- is-a/2(A,B).
is-aL/2(A,C) :- is-aEK/2(A,B),is-aK/2(B,C).
1,000000000000000 is-aK/2(A,C) :- is-aL/2(A,C).
1,000000000000000 is-aK/2(A,B) :- is-a/2(A,B).
finalLambda/0 :- is-aK/2(lassie,animal).
