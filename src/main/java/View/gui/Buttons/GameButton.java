package View.gui.Buttons;

import javafx.scene.control.Button;

public class GameButton extends Button {

	private String BUTTON_STYLE_NORMAL = "-fx-padding: 15 20 13 15;\n" +
			"    -fx-background-insets: 10, 10, 10, 10;\n" +
			"    -fx-background-radius: 8;\n" +
			"    -fx-background-color: linear-gradient(to top, #D5D5D5, #AFAFAF);\n" +
			"    -fx-effect: dropshadow( gaussian , rgba(0,0,0,0.75) , 4,0,0,1 );\n" +
			"    -fx-font-weight: bold;\n" +
			"    -fx-font-size: 20;";
	private String BUTTON_STYLE_PRESSED = "-fx-padding: 20 20 30 15;\n" +
			"    -fx-background-insets: 2 0 0 0,2 0 3 0, 2 0 4 0, 2 0 5 0;\n" +
			"    -fx-background-radius: 8;\n" +
			"    -fx-background-color: linear-gradient(to top, #407C49, #5FB86C);\n" +
			"    -fx-effect: dropshadow( gaussian , rgba(0,0,0,0.75) , 4,0,0,1 );\n" +
			"    -fx-font-weight: bold;\n" +
			"    -fx-font-size: 20;";
	public GameButton() {

	}

	public GameButton(String caption) {
		this.setText(caption);
		this.setMinSize(200, 100);
		this.setMaxSize(200, 100);
		
		//standard style
		this.setStyle(BUTTON_STYLE_NORMAL);
		this.setOnMousePressed(e -> this.setStyle(BUTTON_STYLE_PRESSED));
		this.setOnMouseReleased(e -> this.setStyle(BUTTON_STYLE_NORMAL));

	}
}
