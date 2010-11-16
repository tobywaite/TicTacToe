import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;

import com.sun.tools.javac.util.Pair;

public class PolicyItrAgent extends Agent {


	private Hashtable<Integer, Double> stateValues; // maps a game state to a expected value for reaching that state.
	private Hashtable<Integer, Integer> statePolicy; // maps a game state to the current policy at that state.
	private Agent opponent;

	public PolicyItrAgent(){
		r = new Random();
		stateValues = new Hashtable<Integer, Double>();
		statePolicy = new Hashtable<Integer, Integer>();
	}
	
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

	/* Teach the agent an optimal policy for playing Tic Tac Toe. 
	 
  	   This uses a policy iteration algorithm. An initial policy is chosen arbitrarily. The value of the current
  	   policy is executed for each game state, and the policy is updated to select a policy with maximum value.
	
	   Please see http://webdocs.cs.ualberta.ca/~sutton/book/ebook/node43.html for more information 
	   on the details of the Policy Iteration algorithm.
	*/	
	private void trainAgent() {
				
		int iterations = 0;
		
		boolean aPolicyChanged = true;
		while(aPolicyChanged && iterations < 25){
			aPolicyChanged = false;
			iterations++;
			
			evaluatePolicy(); // updates the value table for the current policy.
			
			Enumeration<Integer> keys = stateValues.keys();			
			Integer key;
			boolean thisPolicyChanged;
			while(keys.hasMoreElements()){
				key = keys.nextElement();
				thisPolicyChanged = updatePolicy(key);
				if(thisPolicyChanged)
					aPolicyChanged = true;				
			}
		}
	}

	private void evaluatePolicy() {
		Double deltaValue = 1.0;
		Double maxDelta = 0.0;
		
		// use value iteration for the current policy to determine the value of the current policy over all states.
		while(deltaValue > maxDelta){

			deltaValue = 0.0;

			Enumeration<Integer> keys = stateValues.keys();			
			Integer key;
			Double oldValue, newValue;
			
			while(keys.hasMoreElements()){
				key = keys.nextElement();
				oldValue = stateValues.get(key);
				updateValue(key);
				newValue = stateValues.get(key);
				deltaValue = Math.max(Math.abs(oldValue-newValue), deltaValue);
			}	
		}		
	}

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

		if(game.evaluateGameState() != Consts.GameInProgress){
			stateValues.put(key, (double)getReward(game));
			return;
		}
		
		ArrayList<Pair<Game, Double>> successorStates = opponent.getSuccessorStates(game);
		
		Double currentValue = (double)getReward(game);
		
		// sum expected value over the successor states, weighted by each states transition probability.
		for(Pair<Game, Double> successor : successorStates){
			try{
				currentValue += Consts.DiscountFactor * getValue(successor.fst) * successor.snd; // snd is transition probability
			} catch(InvalidMoveException e){
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
		// update the value under the current policy to the calculated value.
		stateValues.put(key, currentValue);
	}

	// determine the policy that currently yields the highest value.
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
			
			for(Pair<Game, Double> successor : opponent.getSuccessorStates(nextTurn)){
				try{
					moveValue += Consts.DiscountFactor * getValue(successor.fst) * successor.snd; // snd is transition probability
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
		
		statePolicy.put(key, updatedPolicy);
		
		return (currentPolicy != updatedPolicy);
	}
	
	// Given a gameState hash key, create a game with equivalent state.
	private Game gameFromKey(Integer key) {
		// convert ternary key to game board representation. Create a new game with that board.
		int[] board = new int[9];
		int xCount = 0, oCount = 0;
		Integer remainingKey = key;
		for(int i=board.length-1; i>=0; i--){
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

	/*
	 * This function generates a state key based on the current game state, as represented by the game board.
	 * Each of the nine spaces in the game board can be one of three states ÐÊblank, X or O. We therefore 
	 * treat the spaces as digits in a ternary (base three) number, and calculate the decimal equivalent of 
	 * that number. This guarantees that each game state has a different integer key.
	 * 
	 * Each state has up to eight equivalent rotations and reflections in the state space, however only one 
	 * of these is represented in our state-value hashtable. Therefore, we calculate the keys of all 
	 * equivalent states and always return the smallest key. 
	 * 
	 * This both guarantees that we can minimize our state space and increases the algorithms rate of 
	 * learning by allowing it to update the value for multiple game states simultaneously.  
	 */
	private int genStateKey(int[] board){
				
//		int maxKey = -1; // smaller than the smallest possible key.
//		int keyHash = 0;
//		// perform all possible rotations and reflections and return the smallest key.
//
//		for(int reflections = 0; reflections <= 1; reflections++){ // once without reflection, once with.
//			for(int rotations = 0; rotations <= 3; rotations++)    // once for each rotation.				
//				keyHash = ternaryToDecimal(rotateAndReflect(board, rotations, reflections));
//				if (keyHash > maxKey)
//					maxKey = keyHash;
//		}
//		return maxKey;
		
		return(ternaryToDecimal(board));
	}
	
//	private int[] rotateAndReflect(int[] board, int rotations, int reflect) {
//		int[] newBoard = new int[board.length];
//		
//		// perform reflection, if specified
//		if(reflect == 1){
//			// swap 0 and 2 across the vertical axis.
//			newBoard[0] = board[2];
//			newBoard[2] = board[0];
//			// swap 3 and 5 across the vertical axis.
//			newBoard[3] = board[5];
//			newBoard[5] = board[3];
//			// swap 6 and 8 across the vertical axis.
//			newBoard[6] = board[8];
//			newBoard[8] = board[6];
//			// center spaces stay the same.
//			newBoard[1] = board[1];
//			newBoard[4] = board[4];
//			newBoard[7] = board[7];
//			
//			return rotateAndReflect(newBoard, rotations, --reflect); // call recursively
//		}
//		// perform rotation if required.
//		else if(rotations > 0){
//			// rotate corners clockwise
//			newBoard[0] = board[6];
//			newBoard[6] = board[8];
//			newBoard[8] = board[2];
//			newBoard[2] = board[0];
//			// rotate non-corners clockwise
//			newBoard[1] = board[3];
//			newBoard[3] = board[7];
//			newBoard[7] = board[5];
//			newBoard[5] = board[1];
//			// center square stays the same
//			newBoard[4] = board[4];
//			return rotateAndReflect(newBoard, --rotations, reflect); // call recursively
//		}
//		else
//			return board; // base case.
//	}

	/* This method interprets a Game's "board" array as the digits of a ternary (base three) number and returns 
	 * the decimal representation of that number. This is used to calculate game state hash values.
	 */
	private int ternaryToDecimal(int[] gameBoard){
		int hashVal = 0;
		for(int i=0; i<gameBoard.length; i++){
			hashVal += gameBoard[i]*(Math.pow(3,i));
		}
		return hashVal;
	}

	public ArrayList<Pair<Game, Double>> getSuccessorStates(Game game) {
		return null;
	}
}