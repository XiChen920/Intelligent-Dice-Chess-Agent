package User.Agent.MCTS.TreeStructure;

public interface Tree<E> {
    TreeNode<E> getRoot();
    void addRoot(E element);
    boolean hasRoot();
}