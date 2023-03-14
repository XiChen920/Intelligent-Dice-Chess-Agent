package View.Scene;

import Model.Model;
import Model.Move;
import User.User;
import User.HumanUser;
import View.AlertWindow;
import View.BoardViewStructure.ChessBoard;
import View.Main.Handler;
import View.gui.Buttons.GameButton;
import View.gui.Images.DiceImage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

abstract class SceneTemplate implements SceneInterface
{
    protected final Stage gameStage;
    protected Scene scene;
    protected Handler handler;
    protected FlowPane sideMenuPane;
    protected Pane chessBoardLayout;
    protected BorderPane root;
    protected ChessBoard chessboard;

    protected User player1;
    protected User player2;
    protected User currentPlayer;
    protected Model currentModel;

    protected byte diceRoll;
    protected VBox diceLayout, rightPane;
    protected Label playerLabel;
    protected ListView<String> moves;


    protected DiceImage diceImage;
    protected ImageView currentDice;
    protected Color colorTheme;
    protected GameButton diceButton, finishTurn, reset;

    public SceneTemplate(Handler handler, Model currentModel, Stage gameStage)
    {
        this.handler = handler;
        this.currentModel = currentModel;
        this.gameStage = gameStage;
        chessboard = new ChessBoard(handler);
        rightPane = new VBox(10);

        diceImage = new DiceImage(handler);

        player1 = new HumanUser(currentModel, false);
        player2 = new HumanUser(currentModel, true);
        determineStartingPlayer();
    }

    public void display(){
        root = new BorderPane();

        colorTheme = randomColorTheme();
        createSideMenu();
        createMoveHistory();
        createDiceVisual();
        chessboard.setColorTheme(colorTheme);
        chessboard.createChessBoard(currentModel);
        chessBoardLayout = chessboard.getChessBoard();
        setupPlayerLabel();

        root.setLeft(diceLayout);
        root.setTop(sideMenuPane);
        root.setCenter(chessBoardLayout);
        root.setRight(rightPane);
        scene = new Scene(root);
    }

    protected void createDiceVisual() {
        diceLayout = new VBox(20);
        diceLayout.setPadding(new Insets(100,50,0,50));
        currentDice = diceImage.getWDice().get(this.diceRoll);
    }

    protected void determineStartingPlayer()
    {
        if(!player1.getPlayerFlag())
        {
            currentPlayer = player1;
        }
        else
        {
            currentPlayer = player2;
        }
    }

    protected void alternatePlayer()
    {
        if(player1 == currentPlayer)
        {
            currentPlayer = player2;
        }
        else
        {
            currentPlayer = player1;
        }
        currentPlayer.changePlayer();
        alternatePlayerLabel();
    }

    protected User getCurrentPlayer()
    {
        return currentPlayer;
    }

    protected void setupPlayerLabel()
    {
        playerLabel = new Label();
        alternatePlayerLabel();
        playerLabel.setStyle("-fx-font-size: 3em;");
        diceLayout.getChildren().add(playerLabel);
    }

    protected void alternatePlayerLabel()
    {
        if(getCurrentPlayer().getPlayerFlag())
        {
            playerLabel.setText("Current player: Black");
        }
        else
        {
            playerLabel.setText("Current player: White");
        }
    }

    protected void createMoveHistory() {
        rightPane.setAlignment(Pos.CENTER);

        Label movesTitle = new Label();
        movesTitle.setText("Moves History:");
        movesTitle.setStyle("-fx-font-size: 2em;");

        moves = new ListView<>();
        moves.setStyle("-fx-font-size: 1.5em;");

        rightPane.setPadding(new Insets(10, 10, 10, 10));
        rightPane.getChildren().addAll(movesTitle, moves);
    }

    protected void addToMoveHistory(int x0, int y0, int x1, int y1)
    {

        int indexOffset = 1;
        String[] mapping = {"A", "B", "C", "D", "E", "F", "G", "H"};
        int guiX0 = chessboard.modelGUIAdaptionX(x0, y0);
        int guiY0 = chessboard.modelGUIAdaptionY(x0, y0) + indexOffset;
        int guiX1 = chessboard.modelGUIAdaptionX(x1, y1);
        int guiY1 = chessboard.modelGUIAdaptionY(x1, y1) + indexOffset;

        moves.getItems().add(0,chessboard.getMovedPiece()+": "+mapping[guiX0]+String.valueOf(guiY0) + " --> " + mapping[guiX1]+String.valueOf(guiY1));
    }

    protected Color randomColorTheme() {
        Color[] colorPalette = {Color.BROWN, Color.CORAL, Color.CORNFLOWERBLUE, Color.DARKKHAKI};
        int randomNumber = (int) Math.round(Math.random()*(colorPalette.length-1));
        return colorPalette[randomNumber];
    }

    protected void createSideMenu() {
        sideMenuPane = new FlowPane();
        sideMenuPane.setAlignment(Pos.CENTER);
        sideMenuPane.setPadding(new Insets(10,10,10,10));
        sideMenuPane.setBackground(new Background(new BackgroundFill(colorTheme, CornerRadii.EMPTY, Insets.EMPTY)));

        diceButton = rollDiceButton();
        finishTurn = finishTurnButton();
        reset = resetButton();
        sideMenuPane.getChildren().addAll(diceButton, finishTurn, reset);
    }

    protected GameButton rollDiceButton()
    {
        GameButton diceButton = new GameButton("Roll Dice");
        diceButton.setDisable(false);
        diceButton.setOnAction(e ->
        {
            rollDiceUserAction();
        });
        return diceButton;
    }

    protected void rollDiceUserAction()
    {
        chessboard.setClickable(true);
        System.out.println("is this clickable:"+chessboard.getIsClickable());
        currentPlayer.rollDice();
        diceRoll = currentPlayer.getDiceRoll();
        if(chessboard.getAllowFutureMoves()){
            predictMoves(true);
        }

        diceDisplay();

        diceButton.setDisable(true);
        finishTurn.setDisable(false);
    }

    protected void diceDisplay()
    {
        if(currentPlayer.getPlayerFlag())
        {
            currentDice = diceImage.getBDice().get(diceRoll);
        } else
        {
            currentDice = diceImage.getWDice().get(diceRoll);
        }
        diceLayout.getChildren().add(currentDice);
    }

    protected GameButton finishTurnButton()
    {
        GameButton finishTurn = new GameButton("Finish Turn");
        finishTurn.setDisable(true);
        finishTurn.setOnAction(e ->
        {
            userFinishTurn();
        });
        return finishTurn;
    }

    protected void predictMoves(boolean predict){
        if(predict) {
            chessboard.setPossibleMoves(currentModel.getAllPossibleMoves());
        } else {
            chessboard.removePossibleMoves();
        }
    }

    protected void userFinishTurn()
    {
        chessboard.setClickable(false);
        Move currentMove = chessboard.getCurrentMove();
        currentPlayer.setMove(currentMove);
        currentPlayer.executePlay();

        chessboard.boardReversion(currentModel);
        addToMoveHistory(currentMove.getX0(), currentMove.getY0(), currentMove.getX1(), currentMove.getY1());

        alternatePlayer();

        diceLayout.getChildren().remove(currentDice);
        diceLayout.getChildren().remove(currentDice);

        if(chessboard.getAllowFutureMoves()){
            predictMoves(false);
        }

        diceButton.setDisable(false);
        finishTurn.setDisable(true);

        determineWinningMove();
    }

    protected void determineWinningMove()
    {
        Boolean currentLeader = currentModel.getCurrentLeader();
        if(currentLeader != null)
        {
            resetUser();
            currentModel.resetLeader();
            AlertWindow win = new AlertWindow();
            win.display(handler, currentModel, gameStage, "Winner", currentLeader);
        }
    }



    protected GameButton resetButton()
    {
        GameButton resetButton = new GameButton("Reset");
        resetButton.setFocusTraversable(false);
        resetButton.setOnAction(e ->
        {
            resetUser();
            resetDice();
            reset();
        });
        return resetButton;
    }

    protected void resetUser()
    {
        determineStartingPlayer();
        alternatePlayerLabel();
    }

    protected void resetDice()
    {
        diceLayout.getChildren().remove(currentDice);
        diceButton.setDisable(false);
    }

    protected void reset()
    {
        moves.getItems().clear();
        currentModel.resetBoard();
        chessboard.boardReversion(currentModel);
    }

    public Scene getScene() {
        return scene;
    }
}
