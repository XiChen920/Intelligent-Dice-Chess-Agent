package View.gui.Entities;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class PieceMovement
{
    private double x, y;
    private double radius;
    private Circle c;
    private int column, row;
    private int squaresize;

    public PieceMovement(int column, int row, double radius, int squaresize, Circle c) {
        this.column = column;
        this.row = row;
        this.radius = radius;
        this.squaresize = squaresize;
        this.c = c;
        this.x = squaresize/2 + column*squaresize;
        this.y = squaresize/2 + row*squaresize;

    }
    private void convertToColumn(double x) {
        this.column = (int) (x/squaresize);
    }

    private void convertToRow(double y) {
        this.row = (int) (y/squaresize);
    }

    public double getX() {

        return x;
    }

    public void setX(double x) {
        this.x = x;
        convertToColumn(this.x);
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
        convertToRow(this.y);
    }

    public void setColor(Color color) {
        c.setFill(color);
    }
    public void draw() {
        c.setRadius(radius);
        c.setTranslateX(x);
        c.setTranslateY(y);
    }

    public void setCellPosition(int column, int row) {
        this.setX(squaresize/2 + squaresize*column);
        this.setY(squaresize/2 + squaresize*row);
    }

    public double getColumn() {
        return column;
    }

    public double getRow() {
        return row;
    }
}