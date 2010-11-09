import java.util.ArrayList;
import java.util.Random;

public class ValueItrAgent extends Agent {

	double[] stateValues;
	
	/*
	 *  Each of the 9 cells in a tic tac toe board can hold 3 values. Therefore, The overall statespace 
	 *  of the tic tac toe board is 3^9, or 19,683. However, many of these states are unreachable in an 
	 *  actual game Ð a configuration consisting of all "X"s, for example. Taking this into account, 
	 *  there are 5478 reachable states. However, the tic tac toe board is symmetrical along four axes 
	 *  (horizontal, vertical, and both diagonals). Therefore, for each state there could be as many as
	 *  seven additional rotated and reflected states which are essentially identical. Taking this into 
	 *  account reduces the final state space to 765 unique, reachable states. 
	 *  
	 *   In this implementation, however, we have chosen to represent the state space without accounting 
	 *   for rotations and reflections. Doing so prevents us from having to calculate when the current 
	 *   game state is represented by a reflection or rotation of its self in state-space. The 
	 *   approximately seven-fold increase storage required to  represent the state space will be 
	 *   inconsequential for this application and is more than made up for by the resulting simplicity 
	 *   of the program.  
	 */
	
	public ValueItrAgent(){
		r = new Random();
		stateValues = new double[Consts.StateSpaceSize];
		for(int i=0; i<stateValues.length; i++){
			stateValues[i] = r.nextFloat() - 0.5; // randomly initialize values to fall in [-3, 3].
		}
	}
	
	int pickMove(Game game) {
		Integer[] possibleMoves = findPossibleMoves(game);
		double maxVal = -99; // initialize to an large negative number. All possible states will have higher value than this.
		int bestMove = Consts.NoMove;  
		
		int moveType = (team == Consts.TeamX) ? Consts.MoveX : Consts.MoveO;
		
		for(int testMove=0; testMove<possibleMoves.length; testMove++){
			Game testSim = game.simulateMove(possibleMoves[testMove], moveType);
			if (testSim.evaluateGameState() != Consts.GameInvalid){
				int simHash = hashGameState(testSim);
				double moveValue = stateValues[simHash];
				if (moveValue > maxVal){
					bestMove = possibleMoves[testMove];
					maxVal = stateValues[simHash];
				}
			}
		}
		return bestMove;
	}

	private Integer[] findPossibleMoves(Game game) {
		ArrayList<Integer> allowedMoves = new ArrayList<Integer>();
		for(int i=0; i<game.getBoard().length; i++){
			if (game.getBoard()[i] == Consts.MoveEmpty)
				allowedMoves.add(i);
		} 
		return allowedMoves.toArray(new Integer[0]);
	}

	public void reportAction(Game currentGame, Game lastTurn) {

		int lastState = hashGameState(lastTurn);
		int currentState = hashGameState(currentGame);
		
		// update value table. based on the previous state & action, and the reward for current state. 
		stateValues[lastState] = reward(currentGame.evaluateGameState()) + Consts.DiscountFactor*stateValues[currentState];
	}

	private int reward(int gameState) {
		int reward;
		switch (gameState){
			case Consts.GameInProgress:	reward = Consts.RewardInProgress; 	break;
			case Consts.GameWon:		reward = Consts.RewardWon; 			break;
			case Consts.GameLost:		reward = Consts.RewardLost;			break;
			case Consts.GameTied:		reward = Consts.RewardTied; 		break;
			default:					reward = Consts.RewardOther; 		break;
		}
		return reward;
	}

	private int hashGameState(Game game){
		int[] board = game.getBoard();
		int hashVal = 0;
		for(int i=0; i<board.length; i++){
			hashVal += board[i]+(Math.pow(3,i));
		}
		return hashVal;
	}
}
