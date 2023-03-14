package View.Main;

import View.gui.Images.LoadImages;
import javafx.scene.control.MenuButton;

import java.util.ArrayList;

public class Handler {
	
	private ArrayList<MenuButton> menubuttons;
	private Main main;

	public Handler(Main main) {
		this.main = main;
	}
	
	public Main getMain() {
		return this.main;
	}
	
	public LoadImages getLoadImages() {
		return this.main.getLoadImages();
	}
}
