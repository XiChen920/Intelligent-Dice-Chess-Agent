package User.Agent.MCTS;

import Model.CurrentModel;
import Model.Model;
import Model.Move;
import User.Agent.Agent;
import User.Agent.BaselineAgent;
import User.Agent.EvaluationFunction.EvaluationFunction;
import User.Agent.EvaluationFunction.MLPEvaluation;
import User.Agent.EvaluationFunction.SimplifiedEvaluationFunctionWithGamePhaseDetection;
import User.Agent.MCTS.TreeStructure.LinkedTree;
import User.Agent.MCTS.TreeStructure.TreeNode;
import Model.PieceInterface;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.List;
import java.util.Random;

/**
 * Monte Carlo Tree Search Algorithm implementation
 * All functionality of a MCTS tree defined for the game of dice chess, for a specified starting state.
 */
public class MCTS implements MonteCarloTreeSearch
{
    private LinkedTree<Model> tree = new LinkedTree<>();
    private final double selectionCoefficient = 0.4;
    private final double winValue = 1;
    private final double lossValue = 0;

    private double currentTime;
    private static double resourceLimit = 4000;
    private MctsResult result;

    private double lowScoreBoundary = -300; // if state score lower than this, quit continue play out this node
    private double highScoreBoundary = 300; // if state score higher than this, quit continue play out this node
    private EvaluationFunction evaluation;
    private static String evaluationFunction = "SimplifiedEvaluation";

    private static TreeNode<Model>  reuseNode;//for reusing tree


    /**
     * MCTS constructor
     * Generates a new MCTS tree, using a starting chess board state as the root.
     */
    public MCTS() {
        Model model = new CurrentModel();
        tree.addRoot(model);
        initializeRoot();
        setEvaluationFunction(evaluationFunction);
    }

    /**
     * MCTS constructor
     * @param currentModel The currentModel to be initialized as the root of the MCTS tree
     */
    public MCTS(Model currentModel)
    {

        //initialize
        tree.addRoot(currentModel);
        initializeRoot();
        setEvaluationFunction(evaluationFunction);

        if(reuseNode!=null && reuseNode.hasChild()){

            //get last move from opponent
            Move lastOpponentMove = currentModel.getPreviousMove();
            //System.out.print("last opponent move: "+Move.toString(lastOpponentMove));


            //opponent move from reuse tree
            List<TreeNode<Model>> expandedOpponentMoves = reuseNode.getChildren();

            /*
            //print opponent move from reuse subtree
            System.out.println("subtree opponent move:");
            for(TreeNode<Model>reuseMove:reuseMoves){
                System.out.println(Move.toString(reuseMove.getMove()));
            }
             */

            for(TreeNode<Model>reuseMove:expandedOpponentMoves){
                if(reuseMove.getMove().equals(lastOpponentMove)){//if the opposite agent's move is already in subtree
                    //System.out.println("reuse");

                    List<TreeNode<Model>> reuseNodes = reuseMove.getChildren();//the move potentially to be reused

                    for(TreeNode<Model>reuseNode:reuseNodes){

                        //if this move is valid in current state
                        if(tree.getRoot().getAllPossibleChildren().contains(reuseNode.getMove())){
                            tree.getRoot().getAllPossibleChildren().remove(reuseNode.getMove());
                            tree.getRoot().addChild(reuseNode);
                            //System.out.println("!reuse :"+Move.toString((reuseNode.getMove())));
                        }
                    }
                    break;
                }
            }
        }
    }

    //empty the subtree to reuse
    public static void clearSubTree(){
        reuseNode = null;
    }

    /**
     * Method to initialize the root of the MCTS tree with defined starting Model
     */
    public void initializeRoot()
    {
        Model currentState = tree.getRoot().getState();
        Agent randomAgent = new BaselineAgent(currentState, currentState.getPlayer());
        randomAgent.rollDice();
        tree.getRoot().setAllPossibleChildren(currentState.getAllPossibleMoves());
    }

    /**
     * Class serves to uniquely identify the end result of an MCTS run, and its properties.
     */
    private class MctsResult
    {
        private Model resultingModel;
        private Move usedMove;

        public MctsResult(Model resultingModel, Move usedMove)
        {
            this.resultingModel = resultingModel;
            this.usedMove = usedMove;
        }

        public Model getResultingModel(){return resultingModel;}
        public Move getMoveUsed(){return usedMove;}
    }

    /**
     * MCTS algorithm execution, until conclusion is reached.
     * MMCTS result generated
     */
    public void MCTSAction()
    {

        result = null;

        currentTime = System.currentTimeMillis();
        double endTime = currentTime + resourceLimit;
        TreeNode currentNode = tree.getRoot();

        while(System.currentTimeMillis() < endTime)
        {
            currentNode = selection();

            if(terminalState(currentNode))
            {
                break;
            }

            TreeNode extendedNode = expansion(currentNode);
            TreeNode leafNode = playout(extendedNode);
            backprop(leafNode, extendedNode);
        }

        TreeNode<Model> resultingNode = successorSelection("maxChildSelection");
        result = new MctsResult(tree.getRoot().getState(), resultingNode.getMove());
        reuseNode = resultingNode;//record the subtree to reuse
    }


    /**
     * Nalysis of a TreeNode to determine if it represents a terminal node.
     * Terminal node identified by a winner within the state of the Node.
     * @param currentNode The node to be evaluated
     * @return True if node is terminal. False otherwise
     */
    public boolean terminalState(TreeNode<Model> currentNode)
    {
        if(currentNode.getState().getCurrentLeader() != null || currentNode.getState().draw())
        {
            return true;
        }
        return false;
    }

    /**
     * Access the Move (from root), that led to the MCTS result
     * @return Move from the root.
     */
    public Move getResultMove()
    {
        return result.getMoveUsed();
    }

    /**
     * Access dice roll used from the root node.
     * @return Integer representing the dice thrown
     */
    public byte getDiceUsed()
    {
        return result.getResultingModel().getCurrentDiceRoll();
    }

    /**
     * Successor selection strategy selection
     * maxChildSelection - Selects child from root with the maximum win rate
     * robustChildSelection - Selects child with highest visit count
     * robustMaxChildSelection - Child with highest win rate and visit count
     * @param selected String identifying selection strategy
     * @return Selected strategy
     */
    public TreeNode<Model> successorSelection(String selected)
    {
        switch (selected)
        {
            case "maxChildSelection":
                    return maxChildSelection();
            case "robustChildSelection":
                    return robustChildSelection();
            case "maxRobustChildSelection":
                    return  maxRobustChildSelection();
            default:
                System.out.println("No child selection policy");
                return null;
        }
    }

    /**
     * Selection strategy determines how the tree is traversed, until a non-fully expanded node is found
     * Selection strategy used is 90% greedy move and 10% UCT selection.
     * @return The selected tree node to expand.
     */
    @Override
    public TreeNode selection()
    {
        TreeNode startNode = tree.getRoot();
        TreeNode currentNode = startNode;
        while(currentNode.fullyExpanded())
        {
            List<TreeNode> children = currentNode.getChildren();

            if (GreedySelection(currentNode,children)!=null){//if at least a node can take opponent piece

                if(Math.random()<0.9){//90% greedy move and 10% UCT selection
                    currentNode = GreedySelection(currentNode,children);
                }
                else{
                    currentNode = UCTSelection(currentNode, children);
                }
            }
            else{
                // if no move can take opponent piece, use UCT
                currentNode = UCTSelection(currentNode, children);
            }
        }
        return currentNode;
    }

    /**
     * Original selection method (only use UCT)
     */
    public TreeNode selectionOriginal()
    {
        TreeNode startNode = tree.getRoot();
        TreeNode currentNode = startNode;
        while(currentNode.fullyExpanded())
        {
            List<TreeNode> children = currentNode.getChildren();
            currentNode = UCTSelection(currentNode, children);

        }
        return currentNode;
    }

    /**
     * The selected node, is expanded such, that based on the state, a child is added
     * @param currentNode Parent node to be expanded upon.
     * @return The childNode of the currentNode
     */
    @Override
    public TreeNode expansion(TreeNode currentNode)
    {
        Move selectedChild = selectRandomChild(currentNode);
        return nextState(currentNode, selectedChild);
    }

    /**
     * Method selects a random node based on the dice role and avaliable moves, from parent node
     * @param currentNode The parent node
     * @return The move seleced to make the next tree node from the parent node
     */
    public Move selectRandomChild(TreeNode currentNode)
    {
        List<Move> allPossibleChildren = currentNode.getAllPossibleChildren();
        Random random = new Random();
        return allPossibleChildren.remove(random.nextInt(allPossibleChildren.size()));
    }

    /**
     * Generate a next state, based on a currentState, through a dice roll
     * @param stateClone Current state, that next state is based on.
     */
    public void populateNextState(Model stateClone)
    {
        Agent rollDiceAgent = new BaselineAgent(stateClone, stateClone.getPlayer());
        rollDiceAgent.rollDice();
    }

    /**
     * Produce the next state, with all information, incl. Moves, dice rolls, and possible children
     * @param currentNode The next state node to be populated
     * @param selectedMove Move the next state is based on.
     * @return The fully populated next state
     */
    public TreeNode nextState(TreeNode currentNode, Move selectedMove)
    {
        Model currentStateClone = getCurrentStateClone(currentNode);
        currentStateClone.updateBoardModel(selectedMove);
        alternatePlayer(currentStateClone);
        TreeNode child = currentNode.addChild(currentStateClone);
        child.setMove(selectedMove);

        if(!terminalState(child))
        {
            populateNextState(currentStateClone);
            populatePossibleMoves(child);
        }
        return child;
    }

    /**
     * Determine all the possible moves for a given state, based on the dice roll
     * @param newChildNode Given node, containing state children will be generated from
     */
    public void populatePossibleMoves(TreeNode newChildNode)
    {
        Model childState = (Model)newChildNode.getState();
        newChildNode.setAllPossibleChildren(childState.getAllPossibleMoves());
    }
    /**
     * Method provides a random game playout from the current node until a terminal node is reached
     *
     * @param currentNode The starting node from which playout is conducted
     * @return The terminal node reached by the playout
     */
    @Deprecated
    public TreeNode playoutOriginal(TreeNode currentNode)
    {
        while(determineTerminalNode(currentNode) == null)
        {
            currentNode = singlePlayout(currentNode);
        }
        return currentNode;
    }

    public void setEvaluationFunction(String function)
    {
        if(function.equals("MLPEvaluation"))
        {
            evaluation = new MLPEvaluation();
            highScoreBoundary = evaluation.getMaxEvaluation();
            lowScoreBoundary = evaluation.getMinEvaluation();
        }
        else if(function.equals("SimplifiedEvaluation"))
        {
            evaluation = new SimplifiedEvaluationFunctionWithGamePhaseDetection();
        }
        else throw new IllegalArgumentException("unknown evaluation function");
        evaluationFunction = function;
    }

    /**
     * Method provides a random game playout from the current node until a terminal node is reached
     * evaluation function is utilised to detect early finish.
     *
     * @param currentNode The starting node from which playout is conducted
     * @return The terminal node reached by the playout
     */
    @Override
    public TreeNode playout(TreeNode currentNode)
    {
        while(!terminalState(currentNode))
        {
            currentNode = singlePlayout(currentNode);
            if( getBoardScore(currentNode) <= lowScoreBoundary){// if the state score of the node is too low
                // i.e. too bad for white side (max player)

                Model currentState = (Model)currentNode.getState();
                currentState.setCurrentLeader(true);//then set the result to white lose, black win (true)
            }

            else if( getBoardScore(currentNode) >= highScoreBoundary){// state score too high
                // i.e. too bad for black side

                Model currentState = (Model)currentNode.getState();
                currentState.setCurrentLeader(false);//then set the result to white win
            }
        }
        return currentNode;
    }

    /**
     * Method to evaluate the state of a node with evaluation function
     *
     * @param currentNode the node to be evaluated
     * @return the score of current node (Determined by selected/ initialized evaluation function.
     */
    public double getBoardScore(TreeNode currentNode){
        Model currentState = (Model)currentNode.getState();
        return evaluation.evaluation(currentState);
    }

    /**
     * Single playout producing a next State from the currentNode utilizing a random move (Dice value present
     * @param currentNode Basis of which the next state is produced from a random move
     * @return Resulting treenode produced from the next state
     */
    public TreeNode singlePlayout(TreeNode currentNode)
    {
        Move randomMove = getSingleMove(currentNode);
        return nextState(currentNode, randomMove);
    }

    /**
     * Selects a single move determined by piece type, randomly selecting a move
     * @param currentNode Node containing the current state from which the move is derived.
     * @return Single move derived
     */
    public Move getSingleMove(TreeNode currentNode)
    {
        Model currentState = (Model)currentNode.getState();
        Random randomSelection = new Random();
        List<Move> allPossibleMoves = currentState.getAllPossibleMoves();
        return allPossibleMoves.get(randomSelection.nextInt(allPossibleMoves.size()));
    }

    /**
     * Method clones the state of the specified start node
     * @param playoutStart Node contains state to be cloned.
     * @return The cloned state
     */
    public Model getCurrentStateClone(TreeNode playoutStart)
    {
        Model currentState = (Model)playoutStart.getState();
        return currentState.clone();
    }

    /**
     * Access the Model state of a given node
     * @param currentNode Node containing the required state
     * @return The state of the passed node
     */
    public Model accessNodeState(TreeNode currentNode)
    {
        Model currentState = (Model)currentNode.getState();
        return currentState;
    }

    /**
     * Method to determine if a passed node is terminal.
     * @param currentNode Node containing state to determine if its terminal.
     * @return True if the node is terminal.
     */
    public Boolean determineTerminalNode(TreeNode currentNode)
    {
        Model currentState = (Model)currentNode.getState();
        return currentState.getCurrentLeader();
    }


    /**
     * Method to access the state of the given node, in order to change the state player.
     * @param currentModel Current node cntaining the state.
     */
    public void alternatePlayer(Model currentModel)
    {
        boolean alternatePlayer = !currentModel.getPlayer();
        currentModel.updatePlayer(alternatePlayer);
    }

    /**
     * Backpropagation updates relevant values from terminal leaf node to root.
     * @param leafNode Leaf node to back propagate from.
     * @param expandedNode Node from which playout was conducted.
     * Note that playout child nodes will be removed from expandedNode after backpropagation is complete
     */
    @Override
    public void backprop(TreeNode leafNode, TreeNode expandedNode)
    {
        TreeNode currentNode = leafNode;
        Boolean winner = determineTerminalNode(leafNode);

        while(currentNode.getParent() != null)
        {
            currentNode = singleBackpropStep(currentNode, winner);
        }

        //Root update & Clean up
        updateNodeValues(currentNode, winner);
        expandedNode.clearChildren();
    }

    /**
     * Single backpropagation step
     * @param currentNode Current node to back propagate from.
     * @param winner Boolean value representing winner of the terminal node state.
     * @return The parent of the current node.
     */
    public TreeNode singleBackpropStep(TreeNode currentNode, Boolean winner)
    {
        updateNodeValues(currentNode, winner);
        return currentNode.getParent();
    }

    /**
     * Part of back propagation that aims to update values (Visit and Value count) for the current node, based on the winner of the playout.
     * @param currentNode Current node to update values on
     * @param winner True if terminal node winner is white. False if winner is Black
     */
    public void updateNodeValues(TreeNode currentNode, Boolean winner)
    {
        boolean rootFlag = accessNodeState(tree.getRoot()).getPlayer();
        boolean winRootPlayer;
        if(winner != null) winRootPlayer = winner == rootFlag;
        else winRootPlayer = false;

        if(winRootPlayer)
        {
            currentNode.incrementValue(winValue);
        }
        currentNode.increaseVisitCount();
    }

    /**
     * Upper Confidence Level selection strategy based on visit and value count
     * @param parentNode Parent node, from which a child will be selected.
     * @param children List of children of the parent node
     * @return
     */
    @Override
    public TreeNode UCTSelection(TreeNode parentNode, List<TreeNode> children)
    {
        TreeNode maxNode = children.get(0);
        double currentMax = UCTFunction(parentNode, maxNode);

        for(TreeNode child : children)
        {
            double nodeValue = UCTFunction(parentNode, child);
            if(currentMax < nodeValue)
            {
                currentMax = nodeValue;
                maxNode = child;
            }
        }
        return maxNode;
    }


    /**
     * Upper Confidence Bound (UCT) algorithm performing computations
     * @param parentNode Parent node from which a child of must be selected
     * @param childNode COmputations based on the child node.
     * @return The evaluation of the function with regard to the parent and child node.
     */
    @Override
    public double UCTFunction(TreeNode parentNode, TreeNode childNode)
    {
        return (childNode.getValue()/childNode.getVisitCount()) + selectionCoefficient * Math.sqrt(Math.log(parentNode.getVisitCount()) / childNode.getVisitCount());
    }


    /**
     * Utilize domain knowledge to select move
     *
     * select the move that can take most valuable piece
     * if no move can take other piece, return null
     *
     * @param children List of children of the parent node
     * @param parentNode Parent node from which a child of must be selected
     * @return the node selected by greedy selection or null
     */
    public TreeNode GreedySelection(TreeNode parentNode,List<TreeNode> children)
    {
        TreeNode maxNode = children.get(0);
        double currentMax = GreedyFunction(parentNode,maxNode);

        for(TreeNode child : children)
        {
            double nodeValue = GreedyFunction(parentNode,child);
            if(currentMax < nodeValue)
            {
                currentMax = nodeValue;
                maxNode = child;
            }
        }
        if(currentMax==0){//if no child move can take over opponent piece, return null
            return null;
        }
        //System.out.println(currentMax);
        return maxNode;
    }

    /**
     * If an opponent piece is taken by the move(node), then return the piece value
     * If none is taken , return 0
     * Higher value means more valuable piece is taken
     *
     * @param child child node to evaluate
     * @param parentNode Parent node from which a child of must be selected
     * @return the score of the move, higher the better
     */
    public byte GreedyFunction(TreeNode parentNode,TreeNode child){
        //piece value
        byte pawnValue = 1;
        byte rookValue = 5;
        byte knightValue = 3;
        byte bishopValue = 3;
        byte queenValue = 10;
        byte kingValue = 100;

        //get current board
        Model currentState = (Model)parentNode.getState();
        PieceInterface[][] currentBoard = currentState.getBoardState().getBoardState();

        //get the position after move
        Move move = child.getMove();
        byte x1 = move.getX1();
        byte y1 = move.getY1();
        PieceInterface targetPosition = currentBoard[x1][y1];

        //if an opponent piece is taken by this move
        if(targetPosition!=null){
            byte pieceType = targetPosition.getTypeOfPiece();
            switch (pieceType) {
                case 1: return pawnValue;
                case 2: return rookValue;
                case 3: return knightValue;
                case 4: return bishopValue;
                case 5: return queenValue;
                case 6: return kingValue;
                default: return 0;
            }
        }
        return 0;
    }

    /**
     * Final move selection from root, based on the highest value count of the children of the root
     * @return The tree node with the highest value count.
     */
    public TreeNode<Model> maxChildSelection()
    {
        List<TreeNode<Model>> children = tree.getRoot().getChildren();
        TreeNode maximum = children.get(0);

        for(TreeNode<Model> child: children)
        {
            if(maximum.getValue() < child.getValue())
            {
                maximum = child;
            }
        }
        return  maximum;
    }

    /**
     * Final move selection from root, based on the highest visit count of the children of the root
     * @return The tree node with the highest visit count.
     */
    public TreeNode<Model> robustChildSelection()
    {
        List<TreeNode<Model>> children = tree.getRoot().getChildren();
        TreeNode maximum = children.get(0);

        for(TreeNode<Model> child: children)
        {
            if(maximum.getVisitCount() < child.getVisitCount())
            {
                maximum = child;
            }
        }
        return maximum;
    }

    /**
     * Final move selection from root, based on the highest value and visit counts of the children of the root
     * @return The tree node with the highest value and visit counts.
     */
    public TreeNode<Model> maxRobustChildSelection()
    {
        List<TreeNode<Model>> children = tree.getRoot().getChildren();
        TreeNode<Model> maximum = children.get(0);

        for(TreeNode<Model> child: children)
        {
            if(maximum.getVisitCount() < child.getVisitCount() && maximum.getValue() < child.getValue())
            {
                maximum = child;
            }
        }
        return maximum;
    }

    /**
     * Modifier to MCTS class resource limit.
     * @param newResourceLimit New resource limit MCTS must abide by
     */
    public static void alterResourceLimit(double newResourceLimit)
    {
        resourceLimit = newResourceLimit;
    }

    /**
     * Accessor method to MCTS class resource limit
     * @return The current resource limit of MCTS
     */
    public static double getResourceLimit()
    {
        return resourceLimit;
    }

    /**
     * Accessor to the established tree, on which the MCTS is active.
     * @return The LinkedTree object containing the MCTS tree.
     */
    public LinkedTree accessTree()
    {
        return tree;
    }
}
