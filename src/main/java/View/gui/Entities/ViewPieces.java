package View.gui.Entities;


import View.Main.Handler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public abstract class ViewPieces {
	private int pieceWidth;
	private boolean playerId;
	protected ImageView image;
	protected Handler handler;
	protected int column, row;
	protected int squaresize;
	protected int id;

	protected double x,y;

	public ViewPieces(Handler handler, boolean playerId, int column, int row, int squaresize, int pieceWidth) {
		this.handler = handler;
		this.playerId = playerId;
		this.column = column;
		this.row = row;
		this.squaresize = squaresize;
		this.x = squaresize/2 + column*squaresize;
		this.y = squaresize/2 + row*squaresize;
		this.pieceWidth = pieceWidth;
	}

	protected void setPieceColor() {
		if(!playerId) {
			try {
				image = new ImageView(new Image(new FileInputStream(this.handler.getLoadImages().getWPieces().get(id))));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			image.setFitWidth(pieceWidth);
			image.setPreserveRatio(true);
		} else {
			try {
				image = new ImageView(new Image(new FileInputStream(handler.getLoadImages().getBPieces().get(id))));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			image.setFitWidth(pieceWidth);
			image.setPreserveRatio(true);
		}
	}

	public ImageView getImage() {
		this.setPieceColor();
		return image;
	}

	public void setColumn(int x) {
		this.column = column;
	}
	public void setRow(int row) {
		this.row = row;
	}

	public void setCellPosition(int column, int row) {
		this.setX(squaresize/2 + squaresize*column);
		this.setY(squaresize/2 + squaresize*row);
	}

	public void draw() {
		image.setTranslateX(x-(image.getFitWidth()/2));
		image.setTranslateY(y-(image.getFitWidth()/2));
	}

	public double getColumn() {
		return column;
	}

	public double getRow() {
		return row;
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
	public String getColorString(){
		if(!playerId){
			return "White";
		} else {
			return "Black";
		}
	}

}
