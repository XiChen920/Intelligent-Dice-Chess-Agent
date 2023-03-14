package tests;

import User.Agent.MCTS.TreeStructure.LinkedTree;
import User.Agent.MCTS.TreeStructure.Tree;
import User.Agent.MCTS.TreeStructure.TreeNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestTreeStructure {

    //TODO Break down test into multiple subtests each testing for a given attribute. Will allow for identification of errors if they occur
    @Test
    void testTreeRootValue() {
        Tree t = new LinkedTree<>();
        t.addRoot(2);
        int value = 2;
        Object value1 = t.getRoot().getState();

        assertTrue(t.hasRoot());
        assertEquals((int) value, (int) value1);

        t.getRoot().setValue(1000);
        assertEquals(t.getRoot().getValue(),1000);
    }
    @Test
    void testTreeChildValue() {

        Tree t = new LinkedTree<Integer>();
        t.addRoot(2);
        TreeNode root = t.getRoot();

        root.addChild(3);
        TreeNode child1 = (TreeNode) (root.getChildren()).get(0);
        int value = 3;
        Object value1 = (Object) child1.getState();

        assertEquals((int) value, (int) value1);

        child1.setValue(1000);
        assertEquals(child1.getValue(),1000);
    }

    @Test
    void testTreeVisitCount(){

        Tree t = new LinkedTree<Integer>();
        t.addRoot(2);
        TreeNode root = t.getRoot();

        assertEquals(root.getVisitCount(),0);

        root.increaseVisitCount();
        assertEquals(root.getVisitCount(),1);

        root.setVisitCount(1000);
        assertEquals(root.getVisitCount(),1000);

    }
}