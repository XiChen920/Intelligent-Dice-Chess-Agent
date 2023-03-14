package View.Scene;

import Model.Model;
import View.Main.Handler;
import javafx.stage.Stage;

public class PVPScene extends SceneTemplate
{
	public PVPScene(Handler handler, Model currentModel, Stage gameStage)
	{
		super(handler, currentModel, gameStage);
		chessboard.setAllowFutureMoves(true);
	}
}