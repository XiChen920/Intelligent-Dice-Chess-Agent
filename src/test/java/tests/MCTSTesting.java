package tests;

import Model.CurrentModel;
import Model.Model;
import Model.Move;
import User.Agent.MCTS.MCTS;
import User.Agent.MCTS.TreeStructure.LinkedTreeNode;
import User.Agent.MCTS.TreeStructure.TreeNode;
import Model.PieceInterface;
import Model.BoardState;
import Model.MoveHuman;


import org.junit.jupiter.api.Test;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MCTSTesting
{
    private MCTS MCTSTest = new MCTS();
    private CurrentModel currentState = new CurrentModel();

    @Test
    void testChildPossibilities()
    {
        boolean validChildren = 0 < MCTSTest.accessTree().getRoot().getAllPossibleChildren().size();
        assertTrue(validChildren);
    }

    @Test
    void expansionTest()
    {
        MCTSTest.expansion(MCTSTest.accessTree().getRoot());
        int childrenSize = MCTSTest.accessTree().getRoot().getChildren().size();
        assertEquals(childrenSize, 1);
    }

    public  void selectionTreeSetup()
    {
        //Root
        MCTSTest.accessTree().getRoot().setValue(2.5);
        MCTSTest.accessTree().getRoot().setVisitCount(5.0);
        
        //Child 1
        TreeNode child1 = new LinkedTreeNode(currentState, MCTSTest.accessTree().getRoot());
        child1.setValue(0.5);
        child1.setVisitCount(2);
        
        //Child 2
        TreeNode child2 = new LinkedTreeNode(currentState, MCTSTest.accessTree().getRoot());
        child2.setValue(1.0);
        child2.setVisitCount(3.0);

        //Child 3
        TreeNode child3 = new LinkedTreeNode(currentState, MCTSTest.accessTree().getRoot());
        child3.setValue(1.5);
        child3.setVisitCount(1.0);
        
        //Setup tree
        MCTSTest.accessTree().getRoot().addChild(child1);
        MCTSTest.accessTree().getRoot().addChild(child2);
        MCTSTest.accessTree().getRoot().addChild(child3);
    }

    public double roundDouble(double value)
    {
        double step_1 = (double) Math.round(value*100);
        return step_1/100;
    }

    @Test
    void testChildValues()
    {
        selectionTreeSetup();
        TreeNode root = MCTSTest.accessTree().getRoot();
        List<TreeNode> children =  MCTSTest.accessTree().getRoot().getChildren();

        double child_1_value = MCTSTest.UCTFunction(root, children.get(0));
        assertEquals(roundDouble(child_1_value), 0.61);

        double child_2_value = MCTSTest.UCTFunction(root, children.get(1));
        assertEquals(roundDouble(child_2_value), 0.63);

        double child_3_value = MCTSTest.UCTFunction(root, children.get(2));
        assertEquals(roundDouble(child_3_value), 2.01);
    }

    void selectionSetup()
    {
        while(!MCTSTest.accessTree().getRoot().fullyExpanded())
        {
            MCTSTest.expansion(MCTSTest.accessTree().getRoot());
        }

        //Root
        MCTSTest.accessTree().getRoot().setValue(2.5);
        MCTSTest.accessTree().getRoot().setVisitCount(5.0);

        //Children
        List<TreeNode> children = MCTSTest.accessTree().getRoot().getChildren();
        for(TreeNode child: children)
        {
            child.setValue(1.0);
            child.setVisitCount(3);
        }

        children.get(0).setValue(1.5);
        children.get(0).setVisitCount(1);
    }

    @Test
    void testSelection()
    {
        selectionSetup();
        assertTrue(MCTSTest.accessTree().getRoot().fullyExpanded());

        TreeNode selected = MCTSTest.selection();
        List<TreeNode> children = MCTSTest.accessTree().getRoot().getChildren();

        assertEquals(selected, children.get(0));
    }

    //Test will include an alternation of players from false to true
    @Test
    void singlePlayoutTest()
    {
        selectionTreeSetup();
        MCTSTest.singlePlayout(MCTSTest.accessTree().getRoot());
        List<TreeNode> children =  MCTSTest.accessTree().getRoot().getChildren();
        TreeNode addedChild = children.get(3);

        CurrentModel childState = (CurrentModel) addedChild.getState();

        assertNotEquals(currentState, childState);
        assertNotEquals(childState.getBoardState().getPreviousMove(), null);
        assertEquals(childState.getPlayer(), true);
        assertNotEquals(childState.getCurrentDiceRoll(), 0);
    }

    @Test
    void fullPlayoutTest()
    {
        selectionTreeSetup();
        TreeNode winner = MCTSTest.playout(MCTSTest.accessTree().getRoot());
        CurrentModel winnerState = (CurrentModel)winner.getState();
        assertNotEquals(winnerState.getCurrentLeader(), null);
    }

    @Test
    void backPropTest()
    {
        TreeNode currentNode = MCTSTest.accessTree().getRoot();
        TreeNode leafNode = MCTSTest.playout(currentNode);

        CurrentModel winnerState = (CurrentModel)leafNode.getState();
        Boolean winner = winnerState.getCurrentLeader();
        System.out.println("Winner: " + winner);

        MCTSTest.backprop(leafNode, currentNode);

        if(!winner)
        {
            assertEquals(MCTSTest.accessTree().getRoot().getValue(), 1);
        }
        else
        {
            assertEquals(MCTSTest.accessTree().getRoot().getValue(), 0);
        }
    }

    @Test
    void testMCTSAction()
    {
        MCTSTest.MCTSAction();
        Move bestMove = MCTSTest.getResultMove();
        TreeNode rootNode = MCTSTest.accessTree().getRoot();
        Model rootState = (CurrentModel)rootNode.getState();

        assertFalse(rootState.getPlayer());
        assertTrue(rootState.getCurrentDiceRoll() != 0);

        List<Move> possibleMoves = rootState.getAllPossibleMoves();
        boolean flag = false;
        for(Move possibleMove: possibleMoves)
        {
            if(bestMove.equals(possibleMove))
            {
                flag = true;
            }
        }
        assertTrue(flag);
    }


    @Test
    void testGreedySelection()
    {
        BoardState testBoard = new BoardState(new String[][]{
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","b5","","w5","","","b1",""},
                {"","","","b3","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""}});

        CurrentModel currentState = new CurrentModel(testBoard,false);
        MCTS MCTSTest = new MCTS(currentState);

        // expand tree
        while(!MCTSTest.accessTree().getRoot().fullyExpanded())
        {
            MCTSTest.expansion(MCTSTest.accessTree().getRoot());
        }
        assertTrue(MCTSTest.accessTree().getRoot().fullyExpanded());

        //test whether the selected move is the best selected by greedy
        List<TreeNode> children = MCTSTest.accessTree().getRoot().getChildren();
        TreeNode selected = MCTSTest.GreedySelection(MCTSTest.accessTree().getRoot(),children);
        Move selectedMove = selected.getMove();

        //take the most valuable piece b5
        Move bestMove = new MoveHuman((byte) 3, (byte) 3, (byte) 3, (byte) 1);

        assertEquals(selectedMove, bestMove);
    }

    //test greedy selection when there's 2 equally most valuable move
    // should randomly choose one of them
    @Test
    void testGreedySelectionEquallyValuable()
    {
        BoardState testBoard = new BoardState(new String[][]{
                {"","","","","","","",""},
                {"","","b1","b1","b1","","",""},
                {"","","b1","w6","b1","","",""},
                {"","","b5","w5","b1","","b1",""},
                {"","","b1","b3","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""}});

        CurrentModel currentState = new CurrentModel(testBoard,false);
        MCTS MCTSTest = new MCTS(currentState);

        // expand tree
        while(!MCTSTest.accessTree().getRoot().fullyExpanded())
        {
            MCTSTest.expansion(MCTSTest.accessTree().getRoot());
        }
        assertTrue(MCTSTest.accessTree().getRoot().fullyExpanded());

        //test whether the selected move is the best selected by greedy
        List<TreeNode> children = MCTSTest.accessTree().getRoot().getChildren();
        TreeNode selected = MCTSTest.GreedySelection(MCTSTest.accessTree().getRoot(),children);
        Move selectedMove = selected.getMove();

        //take the most valuable piece b5
        Move bestMove1 = new MoveHuman((byte) 3, (byte) 3, (byte) 3, (byte)2);
        Move bestMove2 = new MoveHuman((byte) 2, (byte) 3, (byte) 3, (byte)2);
        //System.out.println(selectedMove.getX0());
        //System.out.println(selectedMove.getY0());
        //System.out.println(selectedMove.getX1());
        //System.out.println(selectedMove.getY1());

        assertTrue(selectedMove.equals(bestMove1)||selectedMove.equals(bestMove2));
    }



}
