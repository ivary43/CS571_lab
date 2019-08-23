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
signal(4, true).
signal(5, true).

input(1).
input(2).
input(3).
input(4).
input(5).

connected(1, in(1, g11)).
connected(1, in(1, g14)).
connected(2, in(2, g11)).
connected(2, in(2, g14)).
connected(3, in(2, g12)).
connected(3, in(2, g13)).
connected(out(g11), in(1, g12)).
connected(out(g11), in(1, g13)).
connected(out(g13), in(1, g15)).
connected(out(g14), in(2, g15)).

connected(4, in(1, g21)).
connected(4, in(1, g24)).
connected(5, in(2, g21)).
connected(5, in(2, g24)).
connected(out(g15), in(2, g22)).
connected(out(g15), in(2, g23)).
connected(out(g21), in(1, g22)).
connected(out(g21), in(1, g23)).
connected(out(g23), in(1, g25)).
connected(out(g24), in(2, g25)).

type(g11, xorgate).
type(g12, xorgate).
type(g13, andgate).
type(g14, andgate).
type(g15, orgate).

type(g21, xorgate).
type(g22, xorgate).
type(g23, andgate).
type(g24, andgate).
type(g25, orgate).

outputLower(X):- signal(out(g12), X).
outputHigher(X):- signal(out(g22), X).
outputCarry(X):- signal(out(g25), X).