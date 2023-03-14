package View.BoardViewStructure;

import Model.Model;
import Model.Move;
import Model.MoveHuman;
import Model.Piece;
import View.Main.Handler;
import View.gui.Entities.*;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;

import java.util.ArrayList;
import java.util.List;

public class ChessBoard{
	private boolean isClickable;
	private Pane chessboard;
	private Handler handler;
	private Move currentMove;
	private int boardDimension = 8;
	private int maxIndex = 7;
	private int boardsize;
	private int squaresize;
	private int pieceWidth = 100;
	private Group piecesGroup;

	private ViewPieces currentPiece;
	private List<Move> allPossibleMoves, currentPossibleMoves;
	private Group possibleMovesGroup;
	private double initPieceRow, initPieceColumn;

	private ArrayList<PieceMovement> pieces;
	private Color colorTheme;
	private String movedPiece;

	private int currentWidth, currentHeight;
	private boolean allowFutureMoves;

	public ChessBoard(Handler handler) {
		this.allowFutureMoves = false;
		this.handler = handler;
		this.isClickable = false;
		currentMove = new MoveHuman();

		Rectangle2D screenBounds = Screen.getPrimary().getBounds();
		this.currentWidth = (int) screenBounds.getWidth();
		this.currentHeight = (int) screenBounds.getHeight();

		//test screensize
		//this.currentWidth = 1920;
		//this.currentHeight = 1080;
		boardResize();
	}

	private void boardResize() {

		boardsize = fitToScreenDimension(850);
		squaresize = (boardsize - fitToScreenDimension(50))/boardDimension;
		pieceWidth = fitToScreenDimension(100);
	}

	public void setColorTheme(Color colorTheme){
		this.colorTheme = colorTheme;
	}

	public void createChessBoard(Model model) {
		chessboard = new Pane();
		pieces = new ArrayList<>();

		chessboard.setMaxSize(850, 850);
		chessboard.setMinSize(400, 400);

		generateBoardFields(boardDimension);

		addAlphabetLayout(boardDimension, boardsize);

		addNumbersLayout(boardDimension, boardsize);

		if(allowFutureMoves){
			possibleMovesGroup = new Group();
			chessboard.getChildren().add(possibleMovesGroup);
		}

		renderModel(model);
	}

	private void generateBoardFields(int s)
	{
		boolean isWhite = true;
		for(int columns = 0; columns < s; columns++) {
			for (int rows = 0; rows < s; rows++){
				Rectangle square = new Rectangle(columns*squaresize, rows*squaresize, squaresize, squaresize);
				Color color;
				if((columns+rows) % 2 == 0) {
					color = colorTheme;
					isWhite = true;
				} else {
					color = Color.WHITE;
					isWhite = false;

				}
				square.setFill(color);
				chessboard.getChildren().add(square);
			}
		}
	}

	private void addAlphabetLayout(int s, int size){
		String[] alphabet = {"A", "B", "C", "D", "E", "F", "G", "H"};
		for(int columns = 0; columns < s; columns++) {
			Label bottom = new Label(alphabet[columns]);
			BorderPane cellLayout = new BorderPane();
			bottom.setLayoutX(columns*squaresize + (squaresize/2));
			bottom.setLayoutY(size);
			chessboard.getChildren().add(bottom);
		}
	}

	private void addNumbersLayout(int s, int size)
	{
		int offset = 1;
		for(int rows = 0; rows < s; rows++) {
			Label bottom = new Label(""+(rows+offset));
			BorderPane cellLayout = new BorderPane();
			bottom.setLayoutX(size);
			bottom.setLayoutY(rows*squaresize + (squaresize/2));
			chessboard.getChildren().add(bottom);
		}
		for(int rows = 0; rows < s; rows++) {
			Label bottom = new Label(""+(rows+offset));
			BorderPane cellLayout = new BorderPane();
			bottom.setLayoutX(0-squaresize/2);
			bottom.setLayoutY(rows*squaresize + (squaresize/2));
			chessboard.getChildren().add(bottom);
		}
	}

	private void renderModel(Model model)
	{
		piecesGroup = new Group();
		for(int x = 0; x < boardDimension; x++)
		{
			for(int y = 0; y < boardDimension; y++)
			{
				Piece currentPiece = model.getBoardState().getPieceAt(x, y);
				if(currentPiece != null){
					ViewPieces piece = createPiece(currentPiece, x, y);
					ImageView img = piece.getImage();

					piecesGroup.getChildren().add(img);
					piece.draw();
					if(!allowFutureMoves){
						assignControls(img, piece);
					} else {
						assignExtraControls(img, piece);
					}

				}
			}
		}
		chessboard.getChildren().add(piecesGroup);
	}

	public void boardReversion(Model model) {
		chessboard.getChildren().remove(piecesGroup);
		renderModel(model);
	}

	public void setMovedPiece(Model currentModel, Move move)
	{
		movedPiece = currentModel.getBoardState().getPieceAt(move.getX1(), move.getY1()).toString();
	}

	public int modelGUIAdaptionX(int x, int y)
	{
		return y;
	}

	public int modelGUIAdaptionY(int x, int y)
	{
		return x;
	}

	public int GUImodelAdaptionX(double x, double y)
	{
		return (int)y;
	}

	public int GUImodelAdaptionY(double x, double y)
	{
		return (int)x;
	}

	public void assignControls(ImageView img, ViewPieces piece){
		img.setOnMousePressed(e -> pressed(e, piece));
		img.setOnMouseDragged(e -> dragged(e, piece));
		img.setOnMouseReleased(e -> released(e, piece));
	}

	public void assignExtraControls(ImageView img, ViewPieces piece){
		img.setOnMousePressed(e -> pressedExtra(e, piece));
		img.setOnMouseDragged(e -> draggedExtra(e, piece));
		img.setOnMouseReleased(e -> releasedExtra(e, piece));
	}

	private ViewPieces createPiece(Piece currentPiece, int xPosition, int yPosition)
	{
		int guiPositionX = modelGUIAdaptionX(xPosition, yPosition);
		int guiPositionY = modelGUIAdaptionY(xPosition, yPosition);

		switch(currentPiece.getTypeOfPiece())
		{
			case 1:
				return new Pawn(handler, currentPiece.getPlayer(), guiPositionX, guiPositionY, squaresize, pieceWidth);
			case 2:
				return new Rook(handler, currentPiece.getPlayer(), guiPositionX, guiPositionY, squaresize, pieceWidth);
			case 3:
				return new Knight(handler, currentPiece.getPlayer(), guiPositionX, guiPositionY, squaresize, pieceWidth);
			case 4:
				return new Bishop(handler, currentPiece.getPlayer(), guiPositionX, guiPositionY, squaresize, pieceWidth);
			case 5:
				return new Queen(handler, currentPiece.getPlayer(), guiPositionX, guiPositionY, squaresize, pieceWidth);
			case 6:
				return new King(handler, currentPiece.getPlayer(), guiPositionX, guiPositionY, squaresize, pieceWidth);
			default:
				return null;
		}
	}

	/**
	 * Converts a horizontal position or width, of default screen width 2560, height 1440
	 * to an integer relative to the actual screen size
	 *
	 * @param 	defaultWidth	horizontal position or width in integers of an object
	 * @return	the result of the defaultWidth multiplied by the ratio
	 */
	public int fitToScreenDimension(int defaultWidth) {
		double defaultResolution = 2560*1440;
		double screenRatio = (currentWidth*currentHeight)/defaultResolution;
		if(screenRatio<1){
			screenRatio+=0.25;
		} else if(screenRatio >1){
			screenRatio-=0.25;
		}
		return (int) Math.round(defaultWidth * screenRatio);
	}


	private void pressed(MouseEvent e, ViewPieces piece) {

		if(isClickable){
			int modelMoveX = GUImodelAdaptionX(piece.getColumn(), piece.getRow());
			currentMove.setX0((byte)modelMoveX);

			movedPiece = piece.getColorString()+" "+piece.getClass().getSimpleName();

			int modelMoveY = GUImodelAdaptionY(piece.getColumn(), piece.getRow());
			currentMove.setY0((byte)modelMoveY);
			System.out.println(movedPiece+": Set X0: " + currentMove.getX0() + " Y0 " + currentMove.getY0());
		}
	}

	private void dragged(MouseEvent e, ViewPieces piece) {
		if(isClickable){
			piece.setX(piece.getX() + e.getX() - fitToScreenDimension(50));
			piece.setY(piece.getY() + e.getY() - fitToScreenDimension(50));
			piece.draw();
		}
	}

	private void released(MouseEvent e, ViewPieces piece) {
		if(isClickable){
			int column = (int) piece.getX() / squaresize;
			int row = (int) piece.getY() / squaresize;

			piece.setX(squaresize/2 + squaresize*column);
			piece.setY(squaresize/2 + squaresize*row);
			piece.draw();

			movedPiece = piece.getColorString()+" "+piece.getClass().getSimpleName();

			int modelMoveX = GUImodelAdaptionX(piece.getColumn(), piece.getRow());
			currentMove.setX1((byte)modelMoveX);

			int modelMoveY = GUImodelAdaptionY(piece.getColumn(), piece.getRow());
			currentMove.setY1((byte)modelMoveY);
			System.out.println(movedPiece+": New X1 " + currentMove.getX1() + " Y1 " + currentMove.getY1());
		}
	}

	private void pressedExtra(MouseEvent e, ViewPieces piece) {
		if(isClickable){
			removePossibleMoves();
			currentPossibleMoves = new ArrayList<Move>();
			for(Move move: allPossibleMoves) {
				if(move.getX0() == piece.getRow() && move.getY0() == piece.getColumn()){
					displayPossibleMove(move);
					currentPossibleMoves.add(move);
				}
			}

			initPieceRow = piece.getRow();
			initPieceColumn = piece.getColumn();
			int modelMoveX = GUImodelAdaptionX(piece.getColumn(), piece.getRow());
			currentMove.setX0((byte)modelMoveX);

			movedPiece = piece.getColorString()+" "+piece.getClass().getSimpleName();

			int modelMoveY = GUImodelAdaptionY(piece.getColumn(), piece.getRow());
			currentMove.setY0((byte)modelMoveY);
			System.out.println(movedPiece+": Set X0: " + currentMove.getX0() + " Y0 " + currentMove.getY0());
		}
	}

	private void draggedExtra(MouseEvent e, ViewPieces piece) {
		if(isClickable){
			piece.setX(piece.getX() + e.getX() - fitToScreenDimension(50));
			piece.setY(piece.getY() + e.getY() - fitToScreenDimension(50));
			piece.draw();
		}
	}

	private void releasedExtra(MouseEvent e, ViewPieces piece) {
		if(isClickable){
			int column = (int) piece.getX() / squaresize;
			int row = (int) piece.getY() / squaresize;

			for(Move move: currentPossibleMoves){
				if(move.getX1() == row && move.getY1() == column){
					piece.setX(squaresize / 2 + squaresize * column);
					piece.setY(squaresize / 2 + squaresize * row);
					piece.draw();

					movedPiece = piece.getColorString()+" "+piece.getClass().getSimpleName();

					int modelMoveX = GUImodelAdaptionX(piece.getColumn(), piece.getRow());
					currentMove.setX1((byte)modelMoveX);

					int modelMoveY = GUImodelAdaptionY(piece.getColumn(), piece.getRow());
					currentMove.setY1((byte)modelMoveY);
					System.out.println(movedPiece+": New X1 " + currentMove.getX1() + " Y1 " + currentMove.getY1());
					isClickable = false;
					return;
				}
			}

			piece.setX(squaresize/2 + squaresize*column);
			piece.setY(squaresize/2 + squaresize*row);
			piece.draw();

		}
	}

	private void displayPossibleMove(Move move){
		int row = move.getX1();
		int column = move.getY1();
		Rectangle square = new Rectangle(column*squaresize, row*squaresize, squaresize, squaresize);
		square.setFill(Color.rgb(0, 153, 0, 0.5));
		possibleMovesGroup.getChildren().add(square);
	}

	public boolean getIsClickable(){
		return isClickable;
	}

	public void setPossibleMoves(List<Move> allPossibleMoves){
		this.allPossibleMoves = allPossibleMoves;
	}

	public void removePossibleMoves(){
		possibleMovesGroup.getChildren().clear();
	}

	public Move getCurrentMove()
	{
		return currentMove;
	}

	public String getMovedPiece(){
		return movedPiece;
	}

	public Pane getChessBoard()
	{
		return chessboard;
	}

	public void setClickable(boolean isClickable){
		this.isClickable = isClickable;
	}

	public void setAllowFutureMoves(boolean allowFutureMoves){
		this.allowFutureMoves = allowFutureMoves;
	}

	public boolean getAllowFutureMoves(){
		return this.allowFutureMoves;
	}
}