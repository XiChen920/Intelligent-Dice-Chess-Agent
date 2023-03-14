package View;

import Model.Model;
import View.Main.Handler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class AlertWindow {
    public void display(String title, String message) {
        Stage window = new Stage();
        window.setResizable(false);
        window.centerOnScreen();
        try {
            window.getIcons().add(new Image(new FileInputStream("src/main/java/Resources/error.png")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(400);

        Label label = new Label();
        label.setText(message);
        label.setStyle("-fx-font-size: 2em;");
        Button close = new Button();
        close.setStyle("-fx-font-size: 1.5em;");
        close.setText("Close");
        close.setOnAction(e -> window.close());

        VBox layout = new VBox(30);
        layout.setPadding(new Insets(10,10,10,10));
        layout.getChildren().addAll(label, close);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        window.setScene(scene);

        window.show();
    }

    public void display(Handler handler, Model model, Stage currentStage, String title, boolean player) {
        Stage window = new Stage();
        window.setResizable(false);
        window.centerOnScreen();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(500);

        Label label = new Label();
        label.setStyle("-fx-font-size: 2em;");

        if(player){
            label.setText("Black won! Last but certainly not least...");
        } else {
            label.setText("White won! First in, last out...");
        }

        Button close = new Button();
        close.setStyle("-fx-font-size: 1.5em;");
        close.setText("Main Menu");
        close.setOnAction(e -> {
            model.resetBoard();
            MainMenuViewer mainMenuViewer = new MainMenuViewer(handler, model);
            mainMenuViewer.returnToMainMenu(currentStage);
            window.close();
        });

        VBox layout = new VBox(30);
        layout.setPadding(new Insets(10,10,10,10));
        layout.getChildren().addAll(label, close);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.show();
    }
}
