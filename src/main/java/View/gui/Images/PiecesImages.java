package View.gui.Images;

import java.io.FileInputStream;
import java.util.ArrayList;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PiecesImages {
	
	ArrayList<String> bPieces, wPieces;
	private int PIECE_WIDTH = 60;
	 
	
	public PiecesImages()
	{
		bPieces = new ArrayList<>();
		wPieces = new ArrayList<>();
		loadPiecesImages();
	}
	
	private void loadPiecesImages() {
			wPieces.add("src/main/java/Resources/WKing.png");
			wPieces.add("src/main/java/Resources/WQueen.png");
			wPieces.add("src/main/java/Resources/WRook.png");
			wPieces.add("src/main/java/Resources/WBishop.png");
			wPieces.add("src/main/java/Resources/WKnight.png");
			wPieces.add("src/main/java/Resources/WPawn.png");

			bPieces.add("src/main/java/Resources/BKing.png");
			bPieces.add("src/main/java/Resources/BQueen.png");
			bPieces.add("src/main/java/Resources/BRook.png");
			bPieces.add("src/main/java/Resources/BBishop.png");
			bPieces.add("src/main/java/Resources/BKnight.png");
			bPieces.add("src/main/java/Resources/BPawn.png");
	}
	
	public int getPieceWidth() {
		return PIECE_WIDTH;
	}
	
	public ArrayList<String> getBPieces() {
		return bPieces;
	}
	
	public ArrayList<String> getWPieces() {
		return wPieces;
	}
}
