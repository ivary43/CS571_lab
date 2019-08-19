himalayan_member(a).
himalayan_member(b).
himalayan_member(c).

himalayan_member(X):- \+(mountain_climber(X)), \+(skier(X)), !, fail.
himalayan_member(X).

likes(a, rain).
likes(a, snow).

likes(b, X):- \+(likes(a, X)).
likes(b, X) :- likes(a, X), !, fail.
likes(b, X).

mountain_climber(X) :- likes(X, rain), !, fail.
mountain_climber(X).

skier(X) :- \+(likes(X, snow)), !, fail.
skier(X).

f(X) :- himalayan_member(X), mountain_climber(X), \+(skier(X)).