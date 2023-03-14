package Controller;

import Model.Model;
import User.User;

public class BoardCommand implements Command
{
    private User senderReference;
    private Model receiverReference;

    public BoardCommand(Model receiverReference, User senderReference)
    {
        this.senderReference = senderReference;
        this.receiverReference = receiverReference;
    }

    public void execute()
    {
        receiverReference.updateBoardModel(senderReference.getMove());
    }
}
