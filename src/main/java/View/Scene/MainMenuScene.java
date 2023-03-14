package View.Scene;

import java.util.ArrayList;

import View.Main.Handler;

import View.gui.Buttons.MenuButton;
import View.gui.Menu.MainMenu;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class MainMenuScene implements SceneInterface{
		
	private Handler handler;
	private int width, height;
	private MainMenu mm;
	private Scene mainMenuScene;
	private BorderPane root;
	
	public MainMenuScene(Handler handler, int width, int height) {
		this.handler = handler;
		this.width = width;
		this.height = height;
		mm = new MainMenu(handler);
	}
	
	@Override
	public void display() {
		root = new BorderPane();
		root.setBackground(new Background(new BackgroundFill(Color.BLANCHEDALMOND, CornerRadii.EMPTY, Insets.EMPTY)));
		mainMenuScene = new Scene(root, width, height);
		
		
		VBox mainMenuLayout = mm.getMainMenuLayout();
		root.setCenter(mainMenuLayout);
	}
	
	public Scene getScene() {
		return mainMenuScene;
	}
	
	public ArrayList<MenuButton> getMenuButtons(){
		return mm.getMenuButtons();
	}
}
