package Model;

import java.io.Serializable;

public class MoveHuman implements Move, Serializable
{
    public MoveHuman() {}

    public MoveHuman(byte x0, byte x1, byte y0, byte y1) {
        this.x0 = x0;
        this.x1 = x1;
        this.y0 = y0;
        this.y1 = y1;
    }

    public MoveHuman(int x0, int x1, int y0, int y1) {
        this.x0 = (byte) x0;
        this.x1 = (byte) x1;
        this.y0 = (byte) y0;
        this.y1 = (byte) y1;
    }

    public void reuse(byte x0, byte x1, byte y0, byte y1) {
        this.x0 = x0;
        this.x1 = x1;
        this.y0 = y0;
        this.y1 = y1;
    }

    public String displayMove()
    {
        return "x0: " + x0 + " ,y0: " + y0 + " ,x1: " + x1 + " ,y1: " + y1;
    }

    @Override
    public byte getX0() {
        return x0;
    }
    @Override
    public void setX0(byte x0) {
        this.x0 = x0;
    }

    byte x0;
    @Override
    public byte getX1() {
        return x1;
    }
    @Override
    public void setX1(byte x1) {
        this.x1 = x1;
    }

    byte x1;
    @Override
    public byte getY0() {
        return y0;
    }
    @Override
    public void setY0(byte y0) {
        this.y0 = y0;
    }

    byte y0;
    @Override
    public byte getY1() {
        return y1;
    }
    @Override
    public void setY1(byte y1) {
        this.y1 = y1;
    }

    byte y1;

    @Override
    public boolean equals(Object other){
        if(other == null) return false;
        if(!other.getClass().equals(this.getClass())) return false;
        MoveHuman otherMoveHuman = (MoveHuman) other;
        return otherMoveHuman.getX0()==this.getX0()
                && otherMoveHuman.getX1()==this.getX1()
                && otherMoveHuman.getY0()==this.getY0()
                && otherMoveHuman.getY1()==this.getY1();
    }

    @Override
    public String toString() {
        return Move.toString(this);
    }

    @Override
    public Object clone() {
        return new MoveHuman(x0, x1, y0, y1);
    }
}
