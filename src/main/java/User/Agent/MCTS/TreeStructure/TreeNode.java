package User.Agent.MCTS.TreeStructure;


import Model.Move;

import java.util.List;

public interface TreeNode<E> {
    E getState();
    void setState(E currentState);
    TreeNode<E> getParent();
    List<TreeNode<E>> getChildren();
    TreeNode<E> addChild(E nextState);
    void addChild(TreeNode node);
    List<Move> getAllPossibleChildren();
    void setAllPossibleChildren(List<Move> allChildren);
    void delete();
    boolean hasChild();
    boolean fullyExpanded();
    void clearChildren();
    void setMove(Move moveUsed);
    Move getMove();

    void increaseVisitCount();
    void setVisitCount(double i);
    double getVisitCount();
    void setValue(double value);
    void incrementValue(double amount);
    double getValue();
    void setParentEmpty();
}