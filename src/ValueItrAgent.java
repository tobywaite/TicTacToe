import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

public class ValueItrAgent extends Agent {

	private Hashtable<Integer, Double> stateValues;
	
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
	 *   This results in a a reduction of worst-case memory usage by a factor of over 25.7!
	 *   
	 *   Please see http://en.wikipedia.org/wiki/Tic-tac-toe for more information on the size of the 
	 *   Tic Tac Toe state space.
	 */
	
	public ValueItrAgent(){
		r = new Random();
		stateValues = new Hashtable<Integer, Double>();
	}
	
	int pickMove(Game game) {
		Integer[] possibleMoves = findPossibleMoves(game);
		Double maxVal = -99.0; // initialize to an large negative number. All possible states will have higher value than this.
		int bestMove = Consts.NoMove;  
		
		int moveType = (team == Consts.TeamX) ? Consts.MoveX : Consts.MoveO;
		
		// iterate over all possible moves and select the one with the highest value.
		for(int moveIndex=0; moveIndex<possibleMoves.length; moveIndex++){
			// simulate each possible move, test the value of that action.
			Game simMove = game.simulateMove(possibleMoves[moveIndex], moveType);
			// the value of a move is the reward for performing that action plus the discounted value of the resulting state.
			Double moveValue = reward(simMove) + Consts.DiscountFactor*getValue(simMove);   
			if (moveValue > maxVal){
				bestMove = possibleMoves[moveIndex];
				maxVal = moveValue;
			}
		}
		return bestMove;
	}
	
	/*
	 * Given a game, the current value for the state of that Game. If no value currently exists, 
	 * a random initial value between -0.5 and 0.5 is added to the stateValues table.
	 */
	private Double getValue(Game game) {
		int key = genStateKey(game);
		if(stateValues.containsKey(key))
			return stateValues.get(key);
		else{
			Double initVal = 0.0; // initial value is always 0.
			stateValues.put(key, initVal);
			return initVal;
		}
	}

	/*
	 * Given a Game, returns an array containing all allowed moves (corresponding to empty spaces).
	 */
	private Integer[] findPossibleMoves(Game game) {
		ArrayList<Integer> allowedMoves = new ArrayList<Integer>();
		for(int i=0; i<game.getBoard().length; i++){
			if (game.getBoard()[i] == Consts.MoveEmpty)
				allowedMoves.add(i);
		} 
		return allowedMoves.toArray(new Integer[0]);
	}

	/* 
	 * When receiving a action report, update the stateValues table per the Value Iteration algorithm. 
	 * This sets the new value based on the previous state & action, and the reward for current state.
	 */
	public void reportAction(Game currentGame, Game lastTurn) { 
		Double newStateVal = reward(currentGame) + Consts.DiscountFactor*stateValues.get(genStateKey(currentGame));
		stateValues.put(genStateKey(lastTurn), newStateVal);
	}

	/* 
	 * This describes the reward function for an action based on the resulting game state.
	 * A winning move provides a +10 reward, while a losing move provides a -10 reward.
	 * A move that causes a tie results in a +1 move, and any other move gives a -1 reward.
	 */
	private int reward(Game game) {
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
	private int genStateKey(Game game){
				
		int minKey = (int) Math.pow(3, 9); // larger than the largest possible key.
		int key = 0;
		
		// perform all possible rotations and reflections and return the smallest key.

		for(int reflections = 0; reflections <= 1; reflections++){ // once without reflection, once with.
			for(int rotations = 0; rotations <= 3; rotations++)    // once for each rotation.				
				key = ternaryToDecimal(rotateAndReflect(game, rotations, reflections));
				if (key < minKey)
					minKey = key;
		}
		return minKey;
	}
	
	private int[] rotateAndReflect(Game game, int rotations, int reflect) {
		int[] board = game.getBoard();
		int swap;
		
		// perform reflection, if specified
		if(reflect == 1){
			// swap 0 and 2 across the vertical axis.
			swap = board[0];
			board[0] = board[2];
			board[2] = swap;
			// swap 3 and 5 across the vertical axis.
			swap = board[3];
			board[3] = board[5];
			board[5] = swap;
			// swap 6 and 8 across the vertical axis.
			swap = board[6];
			board[6] = board[8];
			board[8] = swap;
		}
		// perform the specified number of rotations
		for(int rotateCount = 0; rotateCount < rotations; rotateCount++){
			// rotate corners clockwise
			swap = board[0];
			board[0] = board[6];
			board[6] = board[8];
			board[8] = board[2];
			board[2] = swap;
			// rotate non-corners clockwise
			swap = board[1];
			board[1] = board[3];
			board[3] = board[7];
			board[7] = board[5];
			board[5] = swap;
		}
		return board;
	}

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
}
