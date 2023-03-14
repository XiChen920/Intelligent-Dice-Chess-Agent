package View.gui.Images;

import View.Main.Handler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class DiceImage {

    private Handler handler;
    private ArrayList<ImageView> wDice, bDice;

    public DiceImage(Handler handler){
        this.handler = handler;
        wDice = new ArrayList<>();
        bDice = new ArrayList<>();
        createDice(wDice, false);
        createDice(bDice, true);
    }

    private void createDice(ArrayList<ImageView> dice, boolean isBlack){
        ArrayList<String> paths;
        if(isBlack){
            paths = handler.getLoadImages().getBPieces();
        } else {
            paths = handler.getLoadImages().getWPieces();
        }

        for(int i = 0; i < paths.size(); i++){
            try {
                dice.add(new ImageView(new Image(new FileInputStream(paths.get(i)))));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            dice.get(i).setFitWidth(300);
            dice.get(i).setPreserveRatio(true);
        }
    }

    public ArrayList<ImageView> getBDice(){
        return bDice;
    }
    public ArrayList<ImageView> getWDice(){
        return wDice;
    }

}
