notgate(false,true).
notgate(true,false).

orgate(true,true,true).
orgate(true,false,true).
orgate(false,true,true).
orgate(false,false,false).

andgate(true,true,true).
andgate(true,false,false).
andgate(false,true,false).
andgate(false,false,false).

xorgate(true, true, false).
xorgate(true, false, true).
xorgate(false, true, true).
xorgate(false, false, false).

% xorgate(A,B):-notgate(B,X), notgate(A,Y), andgate(A,X,Z), andgate(Y,B,W), orgate(Z,W).

% xorgate(A, B) :- orgate(andgate(A, notgate(B)), andgate(notgate(A), B)).

in(N, X) :- andgate(connected(M, in(N, X)), input(M)), signal(M, SigM).
in(N, X) :- connected(M, in(N, X)), out(M).

% out(X) :- type(X, notgate), connected(A, in(1, X)), !, notgate(signal(in(1, X), M) signal(out(X), Y)).
signal(out(X), Y) :- type(X, notgate), write('Wire open at NOT gate\n'), !.
signal(out(X), Y) :- type(X, orgate), connected(A, in(1, X)), connected(B, in(2, X)), signal(in(1, X), M), signal(in(2, X), L), !, orgate(M, L, Y).
signal(out(X), Y) :- type(X, orgate), write('Wire open at OR gate\n'), !.
signal(out(X), Y) :- type(X, andgate), connected(A, in(1, X)), connected(B, in(2, X)), signal(in(1, X), M), signal(in(2, X), L), !, andgate(M, L, Y).
signal(out(X), Y) :- type(X, andgate), write('Wire open at AND gate\n'), !.
signal(out(X), Y) :- type(X, xorgate), connected(A, in(1, X)), connected(B, in(2, X)), signal(in(1, X), M), signal(in(2, X), L), !, xorgate(M, L, Y).
signal(out(X), Y) :- type(X, xorgate), write('Wire open at XOR gate\n'), !.

signal(X, SigM) :- connected(Y, X), signal(Y, SigM).

signal(1, true).
signal(2, false).
signal(3, false).

input(1).
input(2).
input(3).

connected(1, in(1, g1)).
connected(1, in(1, g4)).
connected(2, in(2, g1)).
connected(2, in(2, g4)).
connected(3, in(2, g2)).
connected(3, in(2, g3)).
connected(out(g1), in(1, g2)).
connected(out(g1), in(1, g3)).
connected(out(g3), in(1, g5)).
connected(out(g4), in(2, g5)).

type(g1, xorgate).
type(g2, xorgate).
type(g3, andgate).
type(g4, andgate).
type(g5, orgate).

output:- out(g2).