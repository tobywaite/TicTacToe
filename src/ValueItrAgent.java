public class ValueItrAgent extends Agent {

	int[] value;
	
	public ValueItrAgent(){
		
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
		
		value = new int[Consts.StateSpaceSize];    
		
	}
	
	int pickMove(Game game) {
		// TODO Auto-generated method stub
		return 0;
	}

	void reportAction(Game game) {
		// TODO Auto-generated method stub
		
	}

}
