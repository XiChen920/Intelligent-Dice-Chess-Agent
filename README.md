# Dice Chess
An application to play dice chess (single dice variant) against humans for humans and an environment for future development of dice chess AI's

## How to run
Project is structured using a gradle build. 
From the command line within the project directory, execute the terminal command 'gradle build', followed by 'gradle run' to execute the program.
The launcher: [src/main/java/App.java](src/main/java/App.java)
## Rules summary
This game is a variant of classic Chess, which adds a random factor to the strategy. The following list describes all differences of the rules:
1. There is no check or checkmate, it is allowed to move the king to a square attacked by opponent's piece. The goal is to capture opponent's king.
2. A die is rolled for every move. The die is displayed below the game board and the number determines which piece can be used to make the move. 1 - pawn, 2 - knight, 3 - bishop, 4 - rook, 5 - queen, 6 - king. Use getMovablePieceTypes() to automatically detects which pieces can be moved at the actual situation and void rolling a number of an immobile one.
3. If a pawn is to be promoted (would advance to the last row), the player can move it even if the die does not show 1. However, he can only promote it to the piece chosen by the die roll - for example, if 3 is rolled, the pawn can be promoted to a bishop only. If 1 is rolled, the pawn can be promoted to any piece.
4. this game is played without en passant
   Source: https://brainking.com/en/GameRules?tp=95
## Rules in detail
Setup
1.  The game is played on a bord consistent of eight-by-eight squares
2.  White starts the game
3.  The first row of each player (the row farthest on his/her/their side) contains form left to right at the start of the game: rook, knight, bishop, queen, king, bishop, knight, rook
4.  The second row of each player (the only row adjacent to their first row) is at the start of the game filled with 8 pawns

A turn
1. The first thing that happens on a turn, is the roll of the dice, determining the type of piece that can be moved, only a movable type of piece should be rolled
2. If a piece is at the endpoint of a move, that piece is captured (removed from the game board)
3. A player may not capture its own pieces
4. The path a piece travels may not contain any piece (except if the moving piece is a knight)
5. A player may regardless of the dice roll execute a legal promotion move
6. Pawns:

   a. The first move of a pawn in a game may be 2 squares forward form the perspective of the playing player

   b. Otherwise a pawn may only move 1 square forward without capturing a piece or capture a piece diagonally forward from the perspective of the playing player

   c. Promotion: if a pawn reaches the opponents side of the game board, it becomes the piece type that was rolled
7. Rook: a rook may move horizontal, vertical, or initiate a castling move
8. Knight: a knight's move always consists of two horizontal or vertical squares and one diagonal
9. Bishop: a bishop may only move diagonally
10. Queen: a queen may move diagonally, horizontally, or vertically
11. King: a king may move like a queen (diagonally, horizontally, or vertically), but only one square, or it may initiate a castling move
12. Castling:
    a. If between a king and a rook of the same player there are no pieces

    b. Neither of them has moved this game

    c. And the king is not attacked (during the move) (if the king would stop, it would be attacked) during the move to come

    d. Than the king may move two squares towards the rook, and the rook may move to the position between the kings old and new position
13. The player that captures an opponent’s king is the winner, if the opponent has two kings (promotion), still a win
14. If one of the following statements is true, the game has ended with a draw:
    a. A player has no legal dice roll (no piece owned by this player can move)

    b. It has bin at least seventy-five moves since the last time a piece was captured, or a pawn has moved moved
## The pieces
1. Pawn ![white pawn](src/main/java/Resources/WPawn.png) ![black pawn](src/main/java/Resources/BPawn.png)
2. Rook ![white rook](src/main/java/Resources/WRook.png) ![black rook](src/main/java/Resources/BRook.png)
3. Knight ![white knight](src/main/java/Resources/WKnight.png) ![black knight](src/main/java/Resources/BKnight.png)
4. Bishop ![white bishop](src/main/java/Resources/WBishop.png) ![black bishop](src/main/java/Resources/BBishop.png)
5. Queen ![white queen](src/main/java/Resources/WQueen.png) ![black queen](src/main/java/Resources/BQueen.png)
6. King ![white king](src/main/java/Resources/WKing.png) ![black king](src/main/java/Resources/BKing.png)
## Thing to keep in mind
1. Make a move by moving/dragging a piece on the board
2. To cancel a move, move the piece back to its original position, of course before pressing "finish turn".
## Versioning
- Gradle build: Gradle 7.1.1
- Java: Java 16.0 SDK
## Advanced settings
Alter agent selection in file [src/main/java/View/GameViewer.java](src/main/java/View/GameViewer.java) constructor, String agent, agent_1, agent_2 respectively in constructor of PVCScene and CVCScene

Add agents in file [src/main/java/View/Scene/PVCScene.java](src/main/java/View/Scene/PVCScene.java) method agentSelection

