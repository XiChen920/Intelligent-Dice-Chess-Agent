package View;

import Model.Model;
import View.Main.Handler;
import View.Scene.GameSelectScene;
import View.Scene.MainMenuScene;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class MainMenuViewer extends ViewManager implements EventHandler<ActionEvent>{
	
	VBox mainMenuLayout;
	Stage mainMenuStage;
	Stage gameStage;

	private Model model;
	
	MainMenuScene mainMenuScene;
	GameSelectScene gameSelectScene;
	String mainMenuTitle = "Main Menu";
	String gameSelectTitle = "Main Menu - Choose Gamemode";
	
	public MainMenuViewer(Handler handler, Model model) {
		super(handler);
		this.model = model;
		width = 300;
		height = 300;
		mainMenuScene = new MainMenuScene(handler, width, height);
		gameSelectScene = new GameSelectScene(handler);
		setButtonsOnAction();
		
	}
	
	public void display() {
		mainMenuStage = new Stage();
		mainMenuStage.setTitle(mainMenuTitle);
		mainMenuScene.display();
		mainMenuStage.getIcons().add(handler.getLoadImages().getIcons().get(0));
		mainMenuStage.setMaximized(false);
		mainMenuStage.setResizable(false);
		mainMenuStage.centerOnScreen();
		mainMenuStage.setScene(mainMenuScene.getScene());
		mainMenuStage.show();
	}
	
	public void returnToMainMenu(Stage gameStage) {
		this.gameStage = gameStage;
		this.gameStage.hide();
		display();
	}

	public Stage getMainMenuStage() {
		return mainMenuStage;
	}
	
	//set action for all buttons
	private void setButtonsOnAction() {
		for(int i = 0; i<=1; i++) {
			mainMenuScene.getMenuButtons().get(i).setOnAction(this);
		}
		gameSelectScene.getPrevious().setOnAction(this);
		gameSelectScene.getPvpButton().setOnAction(this);
		gameSelectScene.getPvcbutton().setOnAction(this);
		gameSelectScene.getCvcbutton().setOnAction(this);
	}

	@Override
	public void handle(ActionEvent e) {
		//new game button
		if(e.getSource().equals(mainMenuScene.getMenuButtons().get(0))) {
			gameSelectScene.display();
			mainMenuStage.setScene(gameSelectScene.getScene());
			mainMenuStage.setTitle(gameSelectTitle);
			mainMenuStage.centerOnScreen();
		}
		//exit button
		if(e.getSource().equals(mainMenuScene.getMenuButtons().get(1))) {
			System.out.println("goodbye!");
			mainMenuStage.close();
		}
		//Previous button
		if(e.getSource().equals(gameSelectScene.getPrevious())) {
			mainMenuScene.display();
			mainMenuStage.setScene(mainMenuScene.getScene());
			mainMenuStage.setTitle(mainMenuTitle);
			mainMenuStage.centerOnScreen();
		}
		//Pvp button
		else if (e.getSource().equals(gameSelectScene.getPvpButton())) {
			GameViewer gameViewer = new GameViewer(handler, (byte) 0, model);
			gameViewer.createNewGame(mainMenuStage, model);
		}
		//Pvc button
		else if (e.getSource().equals(gameSelectScene.getPvcbutton())) {
			GameViewer gameViewer = new GameViewer(handler, (byte) 1, model);
			gameViewer.createNewGame(mainMenuStage, model);
		}
		//Cvc button
		else if(e.getSource().equals(gameSelectScene.getCvcbutton())){
			GameViewer gameViewer = new GameViewer(handler, (byte) 2, model);
			gameViewer.createNewGame(mainMenuStage, model);
		}
	}
}
