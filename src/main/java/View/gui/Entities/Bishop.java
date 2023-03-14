package View.gui.Entities;


import View.Main.Handler;

public class Bishop extends ViewPieces
{

	public Bishop(Handler handler, boolean isWhite, int column, int row, int squaresize, int pieceWidth) {
		super(handler, isWhite, column, row, squaresize, pieceWidth);
		id = 4;
	}

	public void update() {}

}
