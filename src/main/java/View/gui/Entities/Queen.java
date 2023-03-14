package View.gui.Entities;


import View.Main.Handler;

public class Queen extends ViewPieces
{
	public Queen(Handler handler, boolean isWhite, int column, int row, int squaresize, int pieceWidth) {
		super(handler, isWhite, column, row, squaresize, pieceWidth);
		id = 5;
	}

	public void update() {}


}
