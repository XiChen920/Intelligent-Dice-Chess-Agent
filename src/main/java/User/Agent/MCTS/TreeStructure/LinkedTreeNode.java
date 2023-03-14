package User.Agent.MCTS.TreeStructure;

import Model.CurrentModel;
import Model.Move;

import java.util.ArrayList;
import java.util.List;

public class LinkedTreeNode<E> implements TreeNode<E> {
    private E currentState;
    private List<TreeNode<E>> children;
    private TreeNode<E> parent;
    private double visitCount = 0;
    private double value = 0;
    private List<Move> allPossibleChildren;
    private Move moveUsed;

    public LinkedTreeNode(E currentState, TreeNode<E> parent)
    {
        this.currentState = currentState;
        this.parent = parent;
        this.children = new ArrayList<TreeNode<E>>();
    }

    public void setAllPossibleChildren(List<Move> allChildren)
    {
        allPossibleChildren = allChildren;
    }

    public List<Move> getAllPossibleChildren()
    {
        return allPossibleChildren;
    }

    public E getState() {
        return(currentState);
    }

    public boolean fullyExpanded()
    {
        if(allPossibleChildren == null || allPossibleChildren.size() > 0)
        {
            return false;
        }
        return true;
    }

    public void setMove(Move moveUsed)
    {
        this.moveUsed = moveUsed;
    }

    public Move getMove()
    {
        return moveUsed;
    }

    public void setState(E state) {
        currentState = state;
    }

    public TreeNode<E> getParent() {
        return(parent);
    }

    public List<TreeNode<E>> getChildren() {
        return(children);
    }

    public TreeNode<E> addChild(E nextState) {
        TreeNode child = new LinkedTreeNode<E>(nextState,this);
        children.add(child);
        return child;
    }

    public void addChild(TreeNode childNode)
    {
        children.add(childNode);
    }

    public boolean hasChild(){
        return(!(children.isEmpty()));
    }

    public void setVisitCount(double i) {
        visitCount= i;
    }

    public void increaseVisitCount(){
        visitCount++;
    }

    public double getVisitCount(){
        return(visitCount);
    }

    public double getValue(){
        return value;
    }

    public void setValue(double value)
    {
        this.value = value;
    }

    public void incrementValue(double amount)
    {
        this.value += amount;
    }

    public void delete() {
        currentState = null;
        children.clear();
        parent = null;
        value = 0;
        visitCount = 0;
    }

    public void clearChildren()
    {
        children.clear();
    }

    @Override
    public void setParentEmpty() {
        parent = null;
    }
}