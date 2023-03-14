package User.Agent.MCTS.TreeStructure;

public class LinkedTree<E> implements Tree<E> {
    private TreeNode<E> root ;

    public LinkedTree (){
        root = new LinkedTreeNode<E>(null,null);
    }

    public LinkedTree (TreeNode<E> rootNode){
        rootNode.setParentEmpty();
        root = rootNode;
    }

    public TreeNode<E> getRoot() {
        return(root);
    }

    public void addRoot(E currentState) {
        root.setState(currentState);
    }

    public boolean hasRoot()
    {
        if(getRoot() != null)
            return true;
        else
            return false;
    }
}