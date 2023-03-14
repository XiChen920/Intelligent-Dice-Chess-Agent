package View.gui.Buttons;

import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;

public class MenuButton extends Button {
	
	private String BUTTON_STYLE = "-fx-background-color: \r\n"
			+ "        #090a0c,\r\n"
			+ "        linear-gradient(#38424b 0%, #1f2429 20%, #191d22 100%),\r\n"
			+ "        linear-gradient(#20262b, #191d22),\r\n"
			+ "        radial-gradient(center 50% 0%, radius 100%, rgba(114,131,148,0.9), rgba(255,255,255,0));\r\n"
			+ "    -fx-background-radius: 5,4,3,5;\r\n"
			+ "    -fx-background-insets: 0,1,2,0;\r\n"
			+ "    -fx-text-fill: white;\r\n"
			+ "    -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );\r\n"
			+ "    -fx-font-family: \"Arial\";\r\n"
			+ "    -fx-text-fill: linear-gradient(white, #d0d0d0);\r\n"
			+ "    -fx-font-size: 12px;\r\n"
			+ "    -fx-padding: 10 20 10 20;\r\n";
	public MenuButton() {

	}
	
	public MenuButton(String caption) {
		this.setText(caption);
		this.setFont(Font.font("Lucida Sans Unicode", FontPosture.ITALIC, 20));
		this.setMaxWidth(200.0);
		
		//standard style
		this.setStyle(BUTTON_STYLE);

	}
}
