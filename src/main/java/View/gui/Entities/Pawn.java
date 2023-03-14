package View.gui.Entities;


import View.Main.Handler;

public class Pawn extends ViewPieces
{
	public Pawn(Handler handler, boolean isWhite, int column, int row, int squaresize, int pieceWidth) {
		super(handler, isWhite, column, row, squaresize, pieceWidth);
		id = 1;
	}

	public void update() {}

}
