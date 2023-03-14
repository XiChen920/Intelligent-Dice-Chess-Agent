package Model;

import java.util.Scanner;

public class Demo {
    public static void main(String[] args) {
        BoardState game = new BoardState();
        boolean currentPlayer = false;
        Scanner in = new Scanner(System.in);
        byte dice;
        Boolean ans;
        String instruction;
        Move tempMove;
        while (true){
            display(game.getBoardState());
            dice = (byte) (Math.random()*6+1);
            if(currentPlayer) System.out.println("Black player, dice = "+dice+", move x0 x1 y0 y1");
            else System.out.println("White player, dice = "+dice+", move x0y0x1y1 like a1b2");
            ans = null;
            try {
                instruction = "";
                while (instruction.length()!=4){
                    instruction = in.next();
                    if(instruction.length() != 4) System.out.println("wrong length");
                }
                tempMove = new MoveHuman(
                        Byte.parseByte(String.valueOf(instruction.charAt(1)))-1,
                        Byte.parseByte(String.valueOf(instruction.charAt(3)))-1,
                        "abcdefgh".indexOf(instruction.charAt(0)),
                        "abcdefgh".indexOf(instruction.charAt(2))
                );
                ans = game.updateState(tempMove, currentPlayer, dice);
            } catch (IllegalMoveExemption illegalMoveExemption) {
                illegalMoveExemption.printStackTrace();
            }
            if(ans !=null){
                if(ans) System.out.println("Black won");
                else System.out.println("Withe won");
                break;
            }
            currentPlayer = !currentPlayer;
        }
    }

    public static void display(PieceInterface[][] board){
        String[] additions = {"1 = Pawn", "2 = Rook", "3 = Knight", "4 = Bishop", "5 = Queen", "6 = King", "Illegal move is forfeit of turn", "With castling, promotion and en passant"};
        System.out.println("  a |b |c |d |e |f |g |h");
        StringBuilder ans;
        for(int i = board.length-1; i >= 0; i--){
            ans = new StringBuilder(String.valueOf(i+1));
            for(int j = 0; j < board[i].length; j++) {
                ans.append("|");
                if (board[i][j] == null) ans.append("  ");
                else ans.append(board[i][j].toStringShort());
            }
            ans.append("|").append(i+1).append("   ").append(additions[i]);
            System.out.println(ans);
        }
        System.out.println("  a |b |c |d |e |f |g |h");
    }
}
