/* 
 * Tic Tac Toe ÐÊA simple machine learning simulator.
 * 
 * Class: ValueItrAgent
 * 
 * This agent uses machine learning to "learn" how to win Tic Tac Toe. The algorithm used is Value Iteration.
 * The agent receives a reward for reaching each game state. Reaching a winning state returns a large positive reward,
 * reaching a losing state receives a large negative reward, a tying game returns a small positive reward, and any
 * other state returns a small negative reward. Each state is initialized to a random value, and the value of each 
 * state is iteratively updated based on the reward for reaching that state and the expected value of subsequent
 * states. Over a number of iterations, the value table converges to an optimal set of values. 
 * Once the value table has converged to its final value, the optimal policy for the agent can be executed by selecting
 * the move out of all possible moves that has the largest expected value.
 * 
 *  This algorithm is an offline, model based algorithm. The optimal policy of the agent is calculated when the agent
 *  is initialized, and relies on have the game and opposing agent be fully observable. By observing the game board and
 *  being able to predict the transition probability of an opponents moves, this agent can learn a policy that will 
 *  maximize its expected reward.
 *  
 *  More information about value iteration can be found here: http://webdocs.cs.ualberta.ca/~sutton/book/ebook/node44.html
 *  
 *  Note: This agent does not play a "perfect" game of tic tac toe. Rather, it attempts to maximize its expected reward.
 *   This results in the agent winning frequently against the "Random" agents, but occasionally losing because of the
 *   random nature of the opponents.
 * 
 * Author: 	Toby Waite
 * Contact: toby.waite@gmail.com 
 * Updated: November 16th, 2010.
 */

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import com.sun.tools.javac.util.Pair;

public class ValueItrAgent extends Agent {

	// A hashtable is used to store the state values for tic tac toe. This allows us to store only values of reachable
	//  tic tac toe states (5478 states), rather than all 19,683 (3^9) possible configurations of the 3x3 grid.
	private Hashtable<Integer, Double> stateValues;
	private Agent opponent;

	public ValueItrAgent(){
		r = new Random();
		stateValues = new Hashtable<Integer, Double>();
	}
	
	// Initialize the Agent. This involves fully exploring the state space, and then iteratively updating the value table.
	public void initialize(Agent enemyAgent){
		// current opponent required for Machine Learning model.
		opponent = enemyAgent;
		// initialize the state value table.
		initStateSpace();
		// teach the value iteration agent an optimal policy given the current opponent.
		trainAgent();
	}
	
	// initialize the value table. This requires fully expanding the game state space to add each reachable state to the table.
	private void initStateSpace() {
		Game newGame = new Game(this, opponent);	
		expandStateSpace(newGame); // begin recursing down the game tree. 
	}

	// recursively expand the game tree, exploring all possible moves.
	private void expandStateSpace(Game game) {
		int state = game.evaluateGameState();
		// if key exists, we've expanded this subtree already. Returning now avoids unnecessary traversals.
		if(stateValues.containsKey(genStateKey(game.getBoard()))){
			return; 
		}
		// if the game has already ended, we are at a terminal game state. 
		// Add it to the table, but don't recurse.
		else if(state != Consts.GameInProgress){
			stateValues.put(genStateKey(game.getBoard()), Consts.InitialValue);
		}
		else{
			stateValues.put(genStateKey(game.getBoard()), Consts.InitialValue);
			// recurse down the game tree to all remaining moves.
			for (int nextMove : game.possibleMoves()){
				expandStateSpace(game.simulateMove(nextMove));
			}
		}
	}

	// This implements the value iteration algorithm (described above) to teach the agent an optimal policy.
	private void trainAgent() {
				
		Double deltaValue = 1.0;
		Double maxDelta = 0.0;
		
		// iterate until the maximum change in value from the last iteration is zero. 
		while(deltaValue > maxDelta){

			deltaValue = 0.0;

			Enumeration<Integer> keys = stateValues.keys();			
			Integer key;
			Double oldValue, newValue;

			// iterate over all states in the stateValue table.
			while(keys.hasMoreElements()){
				key = keys.nextElement();
				oldValue = stateValues.get(key);
				updateValue(key); // Calcluate the new value for the current key
				newValue = stateValues.get(key);
				deltaValue = Math.max(Math.abs(oldValue-newValue), deltaValue); // track largest change in value
			}	
		}
	}
	
	// Update the value in the value table. This is the update step in the value iteration algorithm.
	private void updateValue(Integer key) {
		// Create an instance of a game that matches the current key.
		Game game = gameFromKey(key);
		// if the current game is a terminal state, assign the reward for that state as this state's value.
		if(game.evaluateGameState() != Consts.GameInProgress){
			stateValues.put(key, (double)getReward(game));
			return;
		}
		
		// initialize Value Iteration parameters.
		Double maxReward = -20.0;
		Double moveValue;
		
		int maxAction;
		
		// find the action with the best reward.
		for(int move : game.possibleMoves()){
			Game nextTurn = game.simulateMove(move);
			moveValue = (double)getReward(nextTurn);
			if(nextTurn.evaluateGameState() != Consts.GameInProgress){
				// track the best reward and the move that returns that reward.
				if(moveValue > maxReward){
					maxReward = moveValue;
					maxAction = move;
				}
				continue;
			}
			
			// ask opponent for transition probabilities to all next states for our best move.
			ArrayList<Pair<Game, Double>> successorStates = 
				opponent.getSuccessorStates(nextTurn);

			// The value of the current square is the reward for the last action plus the sum of the value 
			// over all possible successor states weighted by each state's transition probability and
			// multiplied by the discount factor.
			for(Pair<Game, Double> successor : successorStates){
				try{
					moveValue += Consts.DiscountFactor * getValue(successor.fst) * successor.snd; // snd is transition probability
				} catch(InvalidMoveException e){
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
			}
			// track the best move. 
			if(moveValue > maxReward){
				maxReward = moveValue;
				maxAction = move;
			}		
		}
		// update value table.
		stateValues.put(key, maxReward);
	}

	public void printState(int[] board){
		
		String[] strBoard = new String[board.length];
		
		for(int i=0; i<board.length; i++){
			switch(board[i]){
				case Consts.MoveX: 		strBoard[i] = "X"; break;
				case Consts.MoveO:		strBoard[i] = "O"; break;
				case Consts.MoveEmpty: 	strBoard[i] = " "; break;
			}
		}
		
		System.out.println(strBoard[0] + "|" + strBoard[1] + "|" + strBoard[2]);
		System.out.println(strBoard[3] + "|" + strBoard[4] + "|" + strBoard[5]);
		System.out.println(strBoard[6] + "|" + strBoard[7] + "|" + strBoard[8]);
		System.out.println();
	}

	public int pickMove(Game game) {
		Integer[] possibleMoves = game.possibleMoves();
		Double maxVal = -99.0; // initialize to an large negative number. All possible states will have higher value than this.
		int bestMove = Consts.NoMove;  
				
		// iterate over all possible moves and select the one with the highest value.
		for(int moveIndex=0; moveIndex<possibleMoves.length; moveIndex++){
			try{
				Game simMove = game.simulateMove(possibleMoves[moveIndex]);
				Double simValue = getValue(simMove);
				if(simValue > maxVal){
					maxVal = simValue;
					bestMove = possibleMoves[moveIndex];
				}
			}
			catch (InvalidMoveException e){
				System.out.println("Agent Picked Invalid move!");
				System.out.println(e.getMessage());
				System.exit(1);
			}
		}
		return bestMove;
	}
	
	// Given a game board, return the current value for the state of that Game. 
	private Double getValue(Game game) throws InvalidMoveException {
		int key = genStateKey(game.getBoard());
		if(stateValues.containsKey(key))
			return stateValues.get(key);
		else{
			throw new InvalidMoveException("value did not exist in stateValues table!");
		}
	} 

	/* 
	 * This describes the reward function for an action based on the resulting game state.
	 * A winning move provides a +10 reward, while a losing move provides a -10 reward.
	 * A move that causes a tie results in a +1 move, and any other move gives a -1 reward.
	 */
	private int getReward(Game game) {
		int reward;
		switch (game.evaluateGameState()){
			case Consts.GameInProgress:	reward = Consts.RewardInProgress; 	break;
			case Consts.GameWon:		reward = Consts.RewardWon; 			break;
			case Consts.GameLost:		reward = Consts.RewardLost;			break;
			case Consts.GameTied:		reward = Consts.RewardTied; 		break;
			default:					reward = Consts.RewardOther; 		break;
		}
		return reward;
	}

	// This function generates a unique integer representation of the given game board. 
	//  It interprets a Game's "board" array as the digits of a ternary (base three) number and returns 
	//  the decimal representation of that number. This is used to calculate game state hash values.
	private int genStateKey(int[] board){
		int hashVal = 0;
		for(int i=0; i<board.length; i++){
			hashVal += board[i]*(Math.pow(3,i));
		}
		return hashVal;
	}
	
	// Given a gameState hash key, create a game with equivalent state. Performs the reverse action described
	//  by the genStateKey method.
	private Game gameFromKey(Integer key) {
		// convert ternary key to game board representation. Create a new game with that board.
		int[] board = new int[9];
		int xCount = 0, oCount = 0;
		Integer remainingKey = key;
		// iterate over the game board
		for(int i=board.length-1; i>=0; i--){
			// each value corresponds to the next power of 3.
			board[i] = (int) Math.floor(remainingKey/Math.pow(3, i));
			remainingKey -= Math.pow(3, i) * board[i];
			if(board[i] == Consts.MoveX)
				xCount++;
			else if(board[i] == Consts.MoveO)
				oCount++;
		}
		
		Game game;
		if(xCount > oCount)
			game = new Game(opponent, this, board);
		else
			game = new Game(this, opponent, board);
		return game;
	}

	// Cannot predict successor states before agent has been trained.
	public ArrayList<Pair<Game, Double>> getSuccessorStates(Game game) {
		return null;
	}
}