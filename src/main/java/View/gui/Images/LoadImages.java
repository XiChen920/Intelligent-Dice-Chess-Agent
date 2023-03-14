package View.gui.Images;

import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.util.ArrayList;


public class LoadImages {
	
	private ArrayList<Image> icons;
	private ArrayList<String> bPieces, wPieces, dice;
	private PiecesImages pi;
	
	
	public LoadImages() {
		icons = new ArrayList<>();
		pi = new PiecesImages();
		bPieces = fillBPieces();
		wPieces = fillWPieces();

		try
		{
			icons.add(new Image(new FileInputStream("src/main/java/Resources/Chess-icon.png")));

		}
		catch(Exception e)
		{
			e.getStackTrace();
		}

	}



	private ArrayList<String> fillBPieces(){
		ArrayList<String> bPieces = new ArrayList<>();
		bPieces.add("src/main/java/Resources/error.png");
		bPieces.add("src/main/java/Resources/BPawn.png");
		bPieces.add("src/main/java/Resources/BRook.png");
		bPieces.add("src/main/java/Resources/BKnight.png");
		bPieces.add("src/main/java/Resources/BBishop.png");
		bPieces.add("src/main/java/Resources/BQueen.png");
		bPieces.add("src/main/java/Resources/BKing.png");
		return bPieces;
	}

	private ArrayList<String> fillWPieces() {
		ArrayList<String> wPieces = new ArrayList<>();
		wPieces.add("src/main/java/Resources/error.png");
		wPieces.add("src/main/java/Resources/WPawn.png");
		wPieces.add("src/main/java/Resources/WRook.png");
		wPieces.add("src/main/java/Resources/WKnight.png");
		wPieces.add("src/main/java/Resources/WBishop.png");
		wPieces.add("src/main/java/Resources/WQueen.png");
		wPieces.add("src/main/java/Resources/WKing.png");
		return wPieces;
	}


	
	public ArrayList<Image> getIcons() {
		return icons;
	}

	public ArrayList<String> getBPieces() {
		return bPieces;
	}
	
	public ArrayList<String> getWPieces() {
		return wPieces;
	}

	public ArrayList<String> getDice(){
		return dice;
	}
}
