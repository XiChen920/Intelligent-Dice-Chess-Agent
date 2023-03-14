import Model.CurrentModel;
import View.Main.Main;

import javafx.application.Application;
import javafx.stage.Stage;

public class App
{
    public static void main(String[] args)
    {
        Main applicationMainFrame = new Main();
        applicationMainFrame.start(args);
    }
//TODO En Passant does not work
    //NOTE promotion works at least some times, castling I remember working as well
}