package Model;

import java.io.Serializable;

public interface PieceInterface extends Serializable {
    /**
     * The type of piece
     * 1 = pawn
     * 2 = rook
     * 3 = knight
     * 4 = bishop
     * 5 = queen
     * 6 = king
     * @return the type
     */
    byte getTypeOfPiece();

    /**
     * Only for promotion of a pawn
     * @param newTypeOfPiece the new type of piece
     */
    void setTypeOfPiece(byte newTypeOfPiece);

    /**
     * The player controlling the piece
     * false = white player
     * true = black player
     * @return the player
     */
    boolean getPlayer();

    /**
     * Whether the piece has been moved
     * @return false for not moved (yet), true for moved at least 1
     */
    boolean isMoved();

    /**
     * Call this method if the piece has been moved or is guaranteed to move
     */
    void moved();

    PieceInterface clone();

    void setMoved(boolean moved);

    boolean equals(Object other);

    String toStringShort();

    String toString();
}
