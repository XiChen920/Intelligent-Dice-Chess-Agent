package View.gui.Menu;

import View.Main.Handler;
import View.gui.Buttons.MenuButton;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class MainMenu {

	private VBox mainMenuLayout;
	ArrayList<MenuButton> menuButtons;
	private final int width = 300, height = 300;
	private Handler handler;

	public MainMenu(Handler handler) {
		this.handler = handler;
		menuButtons = new ArrayList<>();
		createButtonMenu();
	}
	
	private void createButtonMenu() {
		mainMenuLayout = new VBox(4);
		mainMenuLayout.setPadding(new Insets(10,10,10,10));
		mainMenuLayout.setSpacing(20.0);
		mainMenuLayout.setAlignment(Pos.CENTER);
		menuButtons.add(new MenuButton("New Game"));
		menuButtons.add(new MenuButton("Exit"));
		mainMenuLayout.getChildren().addAll(menuButtons.get(0), menuButtons.get(1));
	}
	
	public ArrayList<MenuButton> getMenuButtons(){
		return menuButtons;
	}

	public VBox getMainMenuLayout() {
		return mainMenuLayout;
	}


}
