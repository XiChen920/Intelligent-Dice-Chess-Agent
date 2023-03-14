package Model;

import java.io.Serializable;

public interface Move extends Serializable {
    public byte getX0();

    public byte getX1();

    public byte getY0();

    public byte getY1();

    String displayMove();

    void setX0(byte x0);
    void setX1(byte x1);
    void setY0(byte y0);
    void setY1(byte y1);
    public boolean equals(Object other);

    static String toString(Move move){
        String ans = "";
        String translation = "abcdefgh";
        ans += translation.charAt(move.getY0());
        ans += (move.getX0()+1);
        ans += " to ";
        ans += translation.charAt(move.getY1());
        ans += (move.getX1()+1);
        return ans;
    }

    Object clone();
}
