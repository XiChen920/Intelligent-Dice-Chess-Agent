package User.Agent.MCTS;

import User.Agent.MCTS.TreeStructure.TreeNode;

import java.util.List;

public interface MonteCarloTreeSearch
{
    TreeNode selection();
    TreeNode expansion(TreeNode currentNode);
    TreeNode playout(TreeNode playoutStart);
    void backprop(TreeNode leafNode, TreeNode expandedNode);

    TreeNode UCTSelection(TreeNode parentNode, List<TreeNode> children);
    double UCTFunction(TreeNode parentNode, TreeNode childNode);
}