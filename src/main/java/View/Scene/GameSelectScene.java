package View.Scene;

import View.Main.Handler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;

public class GameSelectScene implements SceneInterface{

	//TODO Refactor so MainMenuViewer knows which button is being pressed/

	private Handler handler;
	private Button previous, pvpbutton, pvcbutton, cvcbutton;
	private Scene gameSelectScene;
	
	public GameSelectScene(Handler handler) {
		this.handler = handler;
		previous = new Button();
		pvpbutton = new Button();
		pvcbutton = new Button();
		cvcbutton = new Button();
	}

	@Override
	public void display() {
		BorderPane root = new BorderPane();
		FlowPane flowpane = new FlowPane();

		HBox hbox = new HBox(2);
		hbox.setPadding(new Insets(10,10,10,10));
		previous.setText("Previous");
		hbox.getChildren().add(previous);

		pvpbutton.setText("Player vs Player");
		pvpbutton.setMinSize(400/3, 150);
		pvpbutton.setMaxSize(400/3, 150);

		pvcbutton.setText("Player vs AI");
		pvcbutton.setMinSize(400/3, 150);
		pvcbutton.setMaxSize(400/3, 150);

		cvcbutton.setText("AI vs AI");
		cvcbutton.setMinSize(400/3, 150);
		cvcbutton.setMaxSize(400/3, 150);

		flowpane.getChildren().addAll(pvpbutton, pvcbutton, cvcbutton);
		root.setCenter(flowpane);
		root.setTop(hbox);
		gameSelectScene = new Scene(root);
	}
	
	public Scene getScene() {
		return gameSelectScene;
	}
	
	public Button getPrevious()
	{
		return previous;
	}

	public Button getPvpButton()
	{
		return pvpbutton;
	}

	public Button getPvcbutton()
	{
		return pvcbutton;
	}

	public Button getCvcbutton() {
		return cvcbutton;
	}
}
