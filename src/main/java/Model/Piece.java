package Model;

public class Piece implements PieceInterface {
    private byte typeOfPiece;
    private boolean player;
    private boolean isMoved = false;

    public Piece(byte typeOfPiece, boolean player) {
        this.typeOfPiece = typeOfPiece;
        this.player = player;
    }

    @Override
    public byte getTypeOfPiece() {
        return typeOfPiece;
    }

    @Override
    public void setTypeOfPiece(byte newTypeOfPiece) {
        this.typeOfPiece = newTypeOfPiece;}

    @Override
    public boolean getPlayer() {
        return player;
    }

    @Override
    public boolean isMoved() {
        return isMoved;
    }

    @Override
    public void moved() {
        isMoved = true;
    }

    @Override
    public Piece clone() {
        Piece ans = new Piece(typeOfPiece, player);
        ans.setMoved(isMoved);
        return ans;
    }

    @Override
    public void setMoved(boolean moved) {
        this.isMoved = moved;
    }

    public void setPlayer(boolean player) {
        this.player = player;
    }

    @Override
    public boolean equals(Object other){
        if(!other.getClass().equals(this.getClass())) return false;
        Piece otherPiece = (Piece) other;
        if(otherPiece.isMoved != this.isMoved || otherPiece.getTypeOfPiece() != this.getTypeOfPiece() || otherPiece.getPlayer() != this.getPlayer()) return false;
        return true;
    }

    @Override
    public String toStringShort(){
        String ans;
        if(player) ans = "B";
        else ans = "W";
        return ans+typeOfPiece;
    }

    public String toString(){
        String ans;
        if(player) ans = "Black ";
        else ans = "White ";
        String[] names = {"pawn", "rook", "knight", "bishop", "queen", "king"};
        return ans+names[typeOfPiece-1];
    }
}
