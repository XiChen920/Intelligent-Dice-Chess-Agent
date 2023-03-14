package View.gui.Menu;

import javafx.scene.control.ListView;

public class MoveHistory {
    private ListView<String> moves;
    private String[] alphabet = {"A", "B", "C", "D", "E", "F", "G", "H"};

    public MoveHistory(){
        createMoveHistory();
    }

    //TODO: add the coordinates of each move
    private void createMoveHistory() {
        moves = new ListView<>();
        moves.setStyle("-fx-font-size: 1.5em;");
        for(int i = 0; i<200; i++){
            moves.getItems().add(0, "A:"+i+" -> A:"+(i+1));
        }
    }

    public void addMove(int newX, int newY){
      //  moves.getItems().add(0, alphabet."")
    }
}
