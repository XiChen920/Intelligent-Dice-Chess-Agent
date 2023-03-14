package View.Scene;

import Model.Model;
import View.AlertWindow;
import View.Main.Handler;
import View.gui.Buttons.GameButton;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.animation.Timeline;

import Model.Move;
import javafx.util.Duration;

public class CVCScene extends PVCScene
{
    private Timeline timeline;

    public CVCScene(Handler handler, Model currentModel, Stage gameStage, String agent_1, String agent_2)
    {
        super(handler, currentModel, gameStage, agent_2);
        player1 = agentSelection(agent_1, false);
        determineStartingPlayer();
    }

    public void agentPlayOff()
    {
        timeline = new Timeline(new KeyFrame(Duration.seconds(5), ev -> {
            AITurn();
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    @Override
    public void display(){
        root = new BorderPane();

        colorTheme = randomColorTheme();
        this.createSideMenu();
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

    @Override
    protected void createSideMenu() {
        sideMenuPane = new FlowPane();
        sideMenuPane.setAlignment(Pos.CENTER);
        sideMenuPane.setPadding(new Insets(10,10,10,10));
        sideMenuPane.setBackground(new Background(new BackgroundFill(colorTheme, CornerRadii.EMPTY, Insets.EMPTY)));

        diceButton = rollDiceButton();
        diceButton.setDisable(true);
        finishTurn = finishTurnButton();
        reset = this.resetButton();
        sideMenuPane.getChildren().addAll(diceButton, finishTurn, reset);
    }

    @Override
    protected GameButton resetButton()
    {
        GameButton resetButton = new GameButton("Start Game");
        resetButton.setOnAction(e ->
        {
            resetButton.setDisable(true);
            agentPlayOff();
        });
        return resetButton;
    }

    @Override
    protected void rollDiceAgentAction()
    {
        diceLayout.getChildren().remove(currentDice);

        currentPlayer.rollDice();
        diceRoll = currentPlayer.getDiceRoll();

        diceDisplay();

        diceButton.setDisable(true);
        finishTurn.setDisable(true);
    }

    @Override
    public void agentFinishTurn()
    {
        currentPlayer.executePlay();
        Move currentMove = currentPlayer.getMove();

        // Add piece type to move history
        chessboard.setMovedPiece(currentModel, currentMove);
        addToMoveHistory(currentMove.getX0(), currentMove.getY0(), currentMove.getX1(), currentMove.getY1());

        chessboard.boardReversion(currentModel);
        alternatePlayer();

        determineWinningMove();
    }

    @Override
    //Overidden to include timeline sequence stopping conditions
    protected void determineWinningMove()
    {
        Boolean currentLeader = currentModel.getCurrentLeader();
        if(currentLeader != null)
        {
            timeline.stop();
            currentModel.resetLeader();
            AlertWindow win = new AlertWindow();
            win.display(handler, currentModel, gameStage, "Winner", currentLeader);
        }
    }

    public void AITurn()
    {
        rollDiceAgentAction();
        System.out.println("Current player: " + currentPlayer.getPlayerFlag() + " dice roll " + diceRoll);
        agentFinishTurn();
    }
}
