package Controller;

import Model.Model;
import User.User;

public class PlayerCommand implements Command
{
    private User senderReference;
    private Model receiverReference;

    public PlayerCommand(Model receiverReference, User senderReference)
    {
        this.senderReference = senderReference;
        this.receiverReference = receiverReference;
    }

    public void execute()
    {
        receiverReference.updatePlayer(senderReference.getPlayerFlag());
    }
}
