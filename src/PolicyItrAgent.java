/* 
 * Tic Tac Toe ÐÊA simple machine learning simulator.
 * 
 * Class: PolicyItrAgent
 * 
 * This agent uses machine learning to "learn" how to win Tic Tac Toe. The algorithm used is Policy Iteration.
 * Policy iteration is a variant on value iteration. Instead of iteratively updating the value of each state, each state
 * is assigned an action. The value of the current policy is calculated, and then a new policy is chosen that is optimal
 * for the new set of values. Because this does not require the stateValue table to completely converge, but rather only 
 * requires it to converge "enough" that the optimal policy is found, this can be done in fewer iterations than 
 * value iteration. 
 * 
 * This algorithm is an offline, model based algorithm. The optimal policy of the agent is calculated when the agent
 * is initialized, and relies on have the game and opposing agent be fully observable. By observing the game board and
 * being able to predict the transition probability of an opponents moves, this agent can learn a policy that will 
 * maximize its expected reward.
 *  
 * More information about policy iteration can be found here: http://webdocs.cs.ualberta.ca/~sutton/book/ebook/node43.html
 *  
 * Note: This agent does not play a "perfect" game of tic tac toe. Rather, it attempts to maximize its expected reward.
 *  This results in the agent winning frequently against the "Random" agents, but occasionally losing because of the
 *  random nature of the opponents.
 * 
 * Author: 	Toby Waite
 * Contact: toby.waite@gmail.com 
 * Updated: November 16th, 2010.
 */

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;

public class PolicyItrAgent extends Agent {

	// A hashtable is used to store the state values and state policy for tic tac toe. This allows us to store only
	// values of reachable game states (5478 states), rather than all 19,683 (3^9) possible configurations of the 3x3 grid.
	private Hashtable<Integer, Double> stateValues; // maps a game state to a expected value for reaching that state.
	private Hashtable<Integer, Integer> statePolicy; // maps a game state to the current policy at that state.
	private Agent opponent;

	public PolicyItrAgent(){
		r = new Random();
		stateValues = new Hashtable<Integer, Double>();
		statePolicy = new Hashtable<Integer, Integer>();
	}
	
	// initialize the agent. First, we explore the state space, then we perform policy iteration over that space.
	public void initialize(Agent enemyAgent){
		// current opponent required for Machine Learning model.
		opponent = enemyAgent;
		// initialize the state value and policy tables.
		initStateSpace();
		// teach the value iteration agent an optimal policy given the current opponent.
		trainAgent();
	}
	
	// initialize the value and policy tables. This requires fully expanding the game state space.
	private void initStateSpace() {
		Game newGame = new Game(this, opponent);		
		expandStateSpace(newGame);
	}

	// recursively expand the game tree, exploring all possible moves.
	private void expandStateSpace(Game game) {
		int state = game.evaluateGameState();
		// if key exists, we've expanded this subtree already. Returning now avoids unnecessary traversals.
		if(stateValues.containsKey(genStateKey(game.getBoard()))){
			return; 
		}

		Integer[] moves = game.possibleMoves();
		int initPolicy = Consts.NoMove;
		if(moves.length != 0)
			initPolicy = moves[0];
		
		statePolicy.put(genStateKey(game.getBoard()), initPolicy);
		stateValues.put(genStateKey(game.getBoard()), Consts.InitialValue);
		
		// if the game is still in progress, recurse down the game tree.
		if(state == Consts.GameInProgress){
			for (int nextMove : game.possibleMoves())
				expandStateSpace(game.simulateMove(nextMove));
		}
	}

	// This implements the policy iteration algorithm (described above) to teach the agent an optimal policy.
	private void trainAgent() {
				
		int iterations = 0;
		
		boolean aPolicyChanged = true;
		// iterate algorithm until none of the policies in the table change between iterations.
		while(aPolicyChanged && iterations < 25){
			aPolicyChanged = false;
			iterations++;

			// Calculate the value of the current policy at each state, and update the value table.
			evaluatePolicy(); // "evaluation step"
			
			Enumeration<Integer> keys = statePolicy.keys();			
			Integer key;
			boolean thisPolicyChanged;
			// iterate over all policies, calculating the optimal policy given the current value table.
			while(keys.hasMoreElements()){
				key = keys.nextElement();
				thisPolicyChanged = updatePolicy(key);
				if(thisPolicyChanged)
					aPolicyChanged = true;				
			}
		}
	}

	// evaluate the current policy and calculate the current value of the policy at each state.
	private void evaluatePolicy() {
		Double deltaValue = 1.0;
		Double maxDelta = 0.0;
		
		// use value iteration for the current policy to determine the value of the current policy over all states.
		while(deltaValue > maxDelta){

			deltaValue = 0.0;

			Enumeration<Integer> keys = stateValues.keys();			
			Integer key;
			Double oldValue, newValue;
			
			// iterate over all values in the stateValue table, as in Value Iteration.
			while(keys.hasMoreElements()){
				key = keys.nextElement();
				oldValue = stateValues.get(key);
				updateValue(key); // update value
				newValue = stateValues.get(key);
				deltaValue = Math.max(Math.abs(oldValue-newValue), deltaValue); // track largest value change.
			}	
		}		
	}

	// update value based on the reward for reaching the input state, and the expected rewared for successive states.
	private void updateValue(Integer key) {		
		// Create an instance of a game that matches the current key.
		Game game = gameFromKey(key);

		// execute the current policy
		try {
			game.executeMove(statePolicy.get(key));
		} catch (InvalidMoveException e1) {
			// if the current policy executes an invalid move, return now.
			stateValues.put(key, (double)Consts.RewardOther);
			return;
		}
		// if we've reached a terminal state,  return the reward for reaching that state.
		if(game.evaluateGameState() != Consts.GameInProgress){
			stateValues.put(key, (double)getReward(game));
			return;
		}
		
		// Get all successor states.
		ArrayList<TransitionPair> successorStates = opponent.getSuccessorStates(game);
		
		Double currentValue = (double)getReward(game);
		
		// sum expected value over the successor states, weighted by each states transition probability.
		for(TransitionPair successor : successorStates){
			try{
				currentValue += Consts.DiscountFactor * getValue(successor.game) * successor.probability;
			} catch(InvalidMoveException e){
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
		// update the value under the current policy to the calculated value.
		stateValues.put(key, currentValue);
	}

	// Determine the policy that currently yields the highest value.
	private boolean updatePolicy(Integer key) {
		Game game = gameFromKey(key);
		int currentPolicy = statePolicy.get(key);
		
		int updatedPolicy = currentPolicy;
		Double currentValue = stateValues.get(key);
		// iterate over all possible moves, determine the best policy.
		for(int move : game.possibleMoves()){
			Game nextTurn = game.simulateMove(move);
			Double moveValue = (double)getReward(nextTurn);

			if(nextTurn.evaluateGameState() != Consts.GameInProgress){
				if(moveValue > currentValue){
					currentValue = moveValue;
					updatedPolicy = move;
				}
				continue;
			}
			for(TransitionPair successor : opponent.getSuccessorStates(nextTurn)){
				try{
					moveValue += Consts.DiscountFactor * getValue(successor.game) * successor.probability;
				} catch(InvalidMoveException e){
					System.out.println(e.getMessage());
					e.printStackTrace();
				}	
			}
			if(moveValue > currentValue){
				currentValue = moveValue;
				updatedPolicy = move;
			}
		}
		// Update statePolicy table
		statePolicy.put(key, updatedPolicy);
		return (currentPolicy != updatedPolicy); // true if something changed.
	}
	

	// print the current game state.
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

	// to pick a move, simply follow the action in the converged statePolicy table.
	public int pickMove(Game game) {
		return statePolicy.get(genStateKey(game.getBoard()));
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
			remainingKey -= (int) Math.pow(3, i) * board[i];
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

	// use the current policy to determine which move the agent will return. 
	public ArrayList<TransitionPair> getSuccessorStates(Game game) {
		int pickedMove = pickMove(game); // pick move based on current policy.
		Game pickedGame = game.simulateMove(pickedMove);
		ArrayList<TransitionPair> returnList = new ArrayList<TransitionPair>(1); 
		returnList.add(new TransitionPair(pickedGame, 1.0)); // will return picked move with 100% probability.
		return returnList;
	}
}