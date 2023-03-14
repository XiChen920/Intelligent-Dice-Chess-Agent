package View.gui.Entities;

import View.Main.Handler;

public class King extends ViewPieces
{
	public King(Handler handler, boolean isWhite, int column, int row, int squaresize, int pieceWidth) {
		super(handler, isWhite, column, row, squaresize, pieceWidth);
		id = 6;
	}

	public void update() {}
}
