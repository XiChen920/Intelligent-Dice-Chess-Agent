package View;

import Model.Model;
import View.Main.Handler;
import View.Scene.CVCScene;
import View.Scene.PVCScene;
import View.Scene.PVPScene;
import View.Scene.SceneInterface;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.Optional;

public class GameViewer extends ViewManager {
	Stage gameStage;
	Stage menuStage;
	
	SceneInterface scene;

	public GameViewer(Handler handler, byte gameMode, Model model) {
		super(handler);
		gameStage = new Stage();
		if(gameMode == 0) {
			scene = new PVPScene(handler, model, gameStage);
			scene.display();
			gameStage.setTitle("Dicechess - Player vs Player");
			gameStage.setScene(scene.getScene());
			
		} else if(gameMode == 1){
			scene = new PVCScene(handler, model, gameStage, "MCTSAgent");
			scene.display();
			gameStage.setTitle("Dicechess - Player vs AI");
			gameStage.setScene(scene.getScene());
		} else {
			scene = new CVCScene(handler, model, gameStage, "MCTSAgent", "Baseline");
			scene.display();
			gameStage.setTitle("Dicechess - AI vs AI");
			gameStage.setScene(scene.getScene());
		}
	}

	public void display(Model model) {
		//alertbox popup when closing
		gameStage.setOnCloseRequest(e -> {
			e.consume();
			alertBox(model);
		});

		//TODO Location of screen size setting, if not fixed review this code
		gameStage.getIcons().add(handler.getLoadImages().getIcons().get(0));
		Rectangle2D screenBoundary = Screen.getPrimary().getVisualBounds();
		gameStage.setWidth(screenBoundary.getWidth());
		gameStage.setHeight(screenBoundary.getHeight());
		gameStage.setResizable(true);
		gameStage.centerOnScreen();
		gameStage.setMaximized(true);
		gameStage.hide();
		gameStage.show();
	}


	/**
	 * Method which creates a popup message when trying to close the game window
	 */
	private void alertBox(Model model) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.initOwner(gameStage);
		alert.setTitle("Hold up!");
		alert.setHeaderText("Giving up already?");
		alert.setContentText("Choose you destiny:");

		ButtonType alertButton1 = new ButtonType("Restart");
		ButtonType alertButton2 = new ButtonType("Main Menu");
		ButtonType alertButton3 = new ButtonType("Exit");
		ButtonType alertButton4 = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

		alert.getButtonTypes().setAll(alertButton1, alertButton2, alertButton3, alertButton4);
		
		Optional<ButtonType> result = alert.showAndWait();

		//restart
		if (result.get() == alertButton1){
			System.out.println("Restart this game");
		}
		//main menu
		else if (result.get() == alertButton2) {
			model.resetBoard();
		    MainMenuViewer mainMenuViewer = new MainMenuViewer(handler, model);
		    mainMenuViewer.returnToMainMenu(gameStage);

		//exit
		} else if (result.get() == alertButton3) {
			alert.close();
			gameStage.close();
		//cancel
		} else {
		    alert.close();
		    gameStage.show();
		}
	}

	/**
	 * Takes in the previous window, closes it, and then opens the menu stage again
	 * @param menuStage	takes in the previous window and closes it
	 */
	public void createNewGame(Stage menuStage, Model model) {
		this.menuStage = menuStage;
		this.menuStage.hide();
		display(model);
	}

	public Stage getGameStage() {
		return gameStage;
	}
}

