package View.Main;

import Model.CurrentModel;
import Model.Model;
import View.MainMenuViewer;

import View.gui.Images.LoadImages;

import javafx.application.Application;

import javafx.stage.Stage;

public class Main extends Application{
	private int fps;
	private boolean running;
	private String title;
	private Stage menuStage;
	private MainMenuViewer mmv;
	private Handler handler;
	private LoadImages loadImages;

	private Model model;

	public Main() {
		this.title = "Dice Chess - Group 15 Project 2-1";
	}

	private void initialize() {
		handler = new Handler(this);
		loadImages = new LoadImages();
		model = new CurrentModel();
		mmv = new MainMenuViewer(handler, model);
	}
	
	//starts the application
	@Override
	public void start(Stage mainStage) throws Exception
	{
		initialize();
		
		mmv.display();
		mainStage = mmv.getMainMenuStage();
		mainStage.show();
	}

	public void start(String[] args) {
		launch(args);
	}
	
	public LoadImages getLoadImages() {
		return loadImages;
	}
}
