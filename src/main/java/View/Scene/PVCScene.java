package View.Scene;


import User.Agent.Agent;
import User.Agent.ExpectiMiniMax.ExpectiMiniMax;
import User.Agent.ExpectiMiniMax.ExpectiMiniMaxAlphaBetaPruning;
import User.Agent.ExpectiMiniMax.ExpectiMiniMaxAlphaBetaPruningDynamicMultiThreaded;
import User.Agent.ExpectiMiniMax.ExpectiMiniMaxAlphaBetaPruningLimitedMultiThreaded;
import User.Agent.MCTSAgent;

import View.Main.Handler;
import View.gui.Buttons.GameButton;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import Model.Model;

import User.Agent.BaselineAgent;
import Model.Move;

public class PVCScene extends SceneTemplate
{
	public PVCScene(Handler handler, Model currentModel, Stage gameStage, String agent)
	{
		super(handler, currentModel, gameStage);
		player2 = agentSelection(agent, true);
		determineStartingPlayer();
	}

	public Agent agentSelection(String type, boolean playerFlag)
	{
		switch(type)
		{
			case "Baseline": return new BaselineAgent(currentModel, playerFlag);
			case "ExpextiMiniMax": return new ExpectiMiniMax(currentModel, playerFlag);
			case "ExpextiMiniMaxAlphaBetaPruning": return new ExpectiMiniMaxAlphaBetaPruning(currentModel, playerFlag);
			case "ExpectiMiniMaxAlphaBetaPruningLimitedMultiThreaded": return new ExpectiMiniMaxAlphaBetaPruningLimitedMultiThreaded(currentModel, playerFlag);
			case "ExpectiMiniMaxAlphaBetaPruningDynamicMultiThreaded": return new ExpectiMiniMaxAlphaBetaPruningDynamicMultiThreaded(currentModel, playerFlag);
			case "MCTSAgent": return new MCTSAgent(currentModel, playerFlag);
			default:
				System.out.println("No agent selected");
				return null;
		}
	}

	@Override
	protected void createSideMenu() {
		sideMenuPane = new FlowPane();
		sideMenuPane.setAlignment(Pos.CENTER);
		sideMenuPane.setPadding(new Insets(10,10,10,10));
		sideMenuPane.setBackground(new Background(new BackgroundFill(colorTheme, CornerRadii.EMPTY, Insets.EMPTY)));

		diceButton = this.rollDiceButton();
		finishTurn = this.finishTurnButton();
		reset = resetButton();
		sideMenuPane.getChildren().addAll(diceButton, finishTurn, reset);
	}

	@Override
	protected GameButton rollDiceButton()
	{
		GameButton diceButton = new GameButton("Roll Dice");
		diceButton.setDisable(false);
		diceButton.setOnAction(e ->
		{
			this.rollDiceUserAction();
		});
		return diceButton;
	}

	@Override
	protected void rollDiceUserAction()
	{
		chessboard.setClickable(true);
		diceLayout.getChildren().remove(currentDice);
		currentPlayer.rollDice();
		diceRoll = currentPlayer.getDiceRoll();

		diceDisplay();

		diceButton.setDisable(true);
		finishTurn.setDisable(false);
	}

	protected void rollDiceAgentAction()
	{
		currentPlayer.rollDice();
		diceRoll = currentPlayer.getDiceRoll();

		diceDisplay();

		diceButton.setDisable(true);
		finishTurn.setDisable(true);
	}

	@Override
	public GameButton finishTurnButton()
	{
		chessboard.setClickable(false);
		GameButton finishTurn = new GameButton("Finish Turn");
		finishTurn.setDisable(true);
		finishTurn.setOnAction(e ->
		{
			userFinishTurn();
			AITurn();
		});
		return finishTurn;
	}

	//TODO Translate model move to board move to add to move history
	public void agentFinishTurn()
	{
		player2.executePlay();
		Move currentMove = player2.getMove();

		// Add piece type to move history
		chessboard.setMovedPiece(currentModel, currentMove);
		addToMoveHistory(currentMove.getX0(), currentMove.getY0(), currentMove.getX1(), currentMove.getY1());

		chessboard.boardReversion(currentModel);

		alternatePlayer();

		diceButton.setDisable(false);
		finishTurn.setDisable(true);

		determineWinningMove();
	}

	public void AITurn()
	{
		if (currentPlayer == player2)
		{
			rollDiceAgentAction();
			agentFinishTurn();
		}
	}
}
