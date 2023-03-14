package User;

import Model.Model;
import Model.Rules;

import java.util.List;

public class Dice
{
    private int diceValue;

    public Dice(Model model)
    {
        rollDice(model);
    }

    /**
     * generate a random dice value, only the value that can cause piece legal move will be generated
     * i.e. the dice value is generated from movable piece type randomly
     *
     * @param model the model to play on
     */
    public void rollDice(Model model)
    {
        List options = model.getMovablePieceTypes();
        diceValue = (byte) options.get((int)(Math.random()*options.size()));
    }

    public int getDiceRoll()
    {
        return diceValue;
    }
}