import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;

import com.sun.tools.javac.util.Pair;

public class ValueItrAgent extends Agent {

	private Hashtable<Integer, Double> stateValues;
	private Agent opponent;
	
	/*
	 *  Each of the 9 cells in a Tic Tac Toe board can hold 3 values. Therefore, The overall statespace 
	 *  of the Tic Tac Toe board is 3^9, or 19,683. However, many of these states are unreachable in an 
	 *  actual game Ð a configuration consisting of all "X"s, for example. Taking this into account, 
	 *  there are 5478 reachable states. However, the Tic Tac Toe board is symmetrical along four axes 
	 *  (horizontal, vertical, and both diagonals). Therefore, for each state there could be as many as
	 *  seven additional rotated and reflected states which are essentially identical. Taking this into 
	 *  account reduces the final state space to 765 unique, reachable states. 
	 *  
	 *   In this implementation, we use a hash table to store these values. We add (and initialize) new 
	 *   state values to the table as they are encountered. This way, only reachable states will be 
	 *   stored, reducing the state space from 19,683 to 5,478 possible states.
	 *   
	 *   When hashing a game state, we calculate an integer key based on the current game state and the
	 *   seven equivalent rotations and reflections of that game state. The lowest key of these is 
	 *   selected as the key used to represent the state. This way we can further limit the state 
	 *   space, reducing the maximum number of stored state-value pairs to only 765 states! 
	 *   
	 *   This results in a a reduction of memory usage by a factor of over 25.7!
	 *   
	 *   Please see http://en.wikipedia.org/wiki/Tic-tac-toe for more information on the size of the 
	 *   Tic Tac Toe state space.
	 */

	public ValueItrAgent(){
		r = new Random();
		stateValues = new Hashtable<Integer, Double>();
	}
	
	public void initialize(Agent enemyAgent){
		// current opponent required for Machine Learning model.
		opponent = enemyAgent;
		// initialize the state value table.
		initStateSpace();
		// teach the value iteration agent an optimal policy given the current opponent.
		trainAgent();
	}
	
	// initialize the value table. This requires fully expanding the game state space.
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

	/* Teach the agent an optimal policy for playing Tic Tac Toe. 
	 
  	   This uses a value iteration algorithm. A reward for each possible action (moving, winning, 
	   drawing, & losing). The value of each move is based on the expected future reward. All game
	   states are iterated over, propagating the ultimate state values down the game tree over 
	   multiple iterations. Because there are at most 9 turns in the game, it takes approximately 
	   10 iterations to propagate the final values to all states.
	
	   Please see http://webdocs.cs.ualberta.ca/~sutton/book/ebook/node44.html for more information 
	   on the details of the Value Iteration algorithm.
	*/	
	private void trainAgent() {
				
		Double deltaValue = 1.0;
		Double maxDelta = 0.0;
		
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
		
		if(game.evaluateGameState() != Consts.GameInProgress){
			stateValues.put(key, (double)getReward(game));
			return;
		}
		
		// initialize Value Iteration parameters.
		Double maxReward = -20.0;
		Double moveValue;
		
		int maxAction = Consts.NoMove;
		
		// find the action with the best reward.
		for(int move : game.possibleMoves()){
			Game nextTurn = game.simulateMove(move);
			moveValue = (double)getReward(nextTurn);
			if(nextTurn.evaluateGameState() != Consts.GameInProgress){
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
			if(moveValue > maxReward){
				maxReward = moveValue;
				maxAction = move;
			}		
		}
		// update value table.
		stateValues.put(key, maxReward);
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