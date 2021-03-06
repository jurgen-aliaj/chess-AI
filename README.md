# chess-AI

To play, run the .JAR file contained in this folder. Source code is available in /src.

This is an implementation of a simple AI that plays chess. We use a simplie heuristic for 
evaluating a position and combine this with minimax search to find the best move in any given 
position. The standard technique of alpha-beta pruning is used to speed up this search.

So far, we can get a search depth of 6 reasonably efficiently.

Here are some goals I hope to accomplish in the future:

1. Efficiently search 10 levels deep.
2. Develop a more sophisticated evaluation function.
3. Allow the computer to play either black or white.
4. Allow the user to play custom positions against the computer.
5. Improve the user interface.