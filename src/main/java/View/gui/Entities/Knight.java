package View.gui.Entities;


import View.Main.Handler;

public class Knight extends ViewPieces {
	public Knight(Handler handler, boolean isWhite, int column, int row, int squaresize, int pieceWidth) {
		super(handler, isWhite, column, row, squaresize, pieceWidth);
		id = 3;
	}

	public void update() {

	}
}
