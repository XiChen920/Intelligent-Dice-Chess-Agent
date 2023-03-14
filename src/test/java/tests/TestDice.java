package tests;

import Model.CurrentModel;
import Model.Model;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

import User.Dice;

public class TestDice
{
    @Test
    void diceRollBounds()
    {
        Model currentModel = new CurrentModel();
        Dice testRoll = new Dice(currentModel);
        int rollValue = testRoll.getDiceRoll();
        System.out.println("Role value: " + rollValue);
        assertTrue(inRange(rollValue));
    }

    public boolean inRange(int diceRollValue)
    {
        if (diceRollValue >= 1 && diceRollValue<= 6)
        {
            return true;
        }
        return false;
    }
}