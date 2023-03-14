package Controller;

import Model.Model;
import User.User;

public class DiceCommand implements Command
{
    private User senderReference;
    private Model receiverReference;

    public DiceCommand(Model receiverReference, User senderReference)
    {
        this.senderReference = senderReference;
        this.receiverReference = receiverReference;
    }

    public void execute()
    {
        receiverReference.updateDice(senderReference.getDiceRoll());
    }
}
