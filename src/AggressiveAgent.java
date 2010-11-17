/* 
 * Tic Tac Toe ÐÊA simple machine learning simulator.
 * 
 * Class: AggressiveAgent
 * 
 * AggressiveAgent is a Tic Tac Toe agent that follows a probabilistic rule-based policy. 
 *  - 20% of the time, the agent will choose a move from the set of remaining moves at random.
 *  - 80% of the time, the agent will attempt to make an "aggressive" move. An aggressive
 *     move is a move that would cause the agent to win the game. If no such move can be 
 *     made, the agent moves randomly.
 * 
 * Author: 	Toby Waite
 * Contact: toby.waite@gmail.com 
 * Updated: November 16th, 2010.
 */

import java.util.ArrayList;

public class AggressiveAgent extends RandomAgent {

	public int pickMove(Game game){
	
		// With 20% probability, move randomly. 
		// Otherwise, move aggressively to win if a winning move exists.
		// If no such move exists, move randomly.
		int move = pickAggressiveMove(game);
		if(move != Consts.NoMove)
			return selectMoveOrRandom(move, 5, game); //move randomly with 1:5 odds, move aggressively 4:5 odds, if such a move exists.
		else
			return pickRandomMove(game);
	}

	// For a given game, returns a set of games that includes the result of all possible moves by this agent. Each
	//  of those moves is encapsulated in a Pair with the probability of that move being selected. The chance of
	//  a move being selected is calculated based on the rules described above.
	public ArrayList<TransitionPair> getSuccessorStates(Game game) { 

		Integer[] moves = game.possibleMoves();
		int numMoves = moves.length;
		Double totalProbability = 1.0;
		ArrayList<TransitionPair> sStates = new ArrayList<TransitionPair>(moves.length);

		int aggMove = pickAggressiveMove(game);

		// if an aggressive move exists, the probability of selecting that move will be 80% + the chance of it being randomly selected.
		if (aggMove != Consts.NoMove){
			totalProbability -= 0.8;
			sStates.add(new TransitionPair(game.simulateMove(aggMove), 0.8 + totalProbability/numMoves));
		}
		// distribute remaining probability evenly over all remaining states. 
		for (int i = 0; i<moves.length; i++){
			if(moves[i] != aggMove){
				sStates.add(new TransitionPair(game.simulateMove(moves[i]), totalProbability/numMoves));
			}
		}
		return sStates;
	}
}