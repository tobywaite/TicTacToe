/* 
 * Tic Tac Toe ÐÊA simple machine learning simulator.
 * 
 * Class: BalancedAgent
 * 
 * BalancedAgent is a Tic Tac Toe agent that follows a probabilistic rule-based policy. 
 *  - 20% of the time, the agent will choose a move from the set of remaining moves at random.
 *  - 80% of the time, the agent will attempt to make an "aggressive" move. An aggressive
 *     move is a move that would cause the agent to win the game. If no such move can be 
 *     made, the agent attempts to make a "defensive" move. A defensive move is one that would
 *     prevent an opponent from winning. If neither of these is possible, it moves randomly.
 * 
 * Author: 	Toby Waite
 * Contact: toby.waite@gmail.com 
 * Updated: November 16th, 2010.
 */

import java.util.ArrayList;

public class BalancedAgent extends RandomAgent {
	
	// Pick a move based on the current game. This follows the rules described in the class description to select
	//  an aggressive move, a defensive move, or a random move.
	public int pickMove(Game game){
	
		// Check to see if a random move exists.
		int move = pickAggressiveMove(game);
		if (move != Consts.NoMove){
			return selectMoveOrRandom(move, 5, game); // select random with 1:5 odds. 4:5 odds to select defensive move.
		}
		// If no aggressive move was found, check to see if a defensive move exists.
		move = pickDefensiveMove(game);
		if (move != Consts.NoMove){
			return selectMoveOrRandom(move, 5, game); // select random with 1:5 odds. 4:5 odds to select defensive move.
		}
		return pickRandomMove(game); // if no winning or blocking move can be executed, move randomly.
	}

	// For a given game, returns a set of games that includes the result of all possible moves by this agent. Each
	//  of those moves is encapsulated in a Pair with the probability of that move being selected. The chance of
	//  a move being selected is calculated based on the rules described above.
	public ArrayList<TransitionPair> getSuccessorStates(Game game) { 
		
		Integer[] moves = game.possibleMoves();
		int numMoves = moves.length;
		Double totalProbability = 1.0;
		ArrayList<TransitionPair> sStates = new ArrayList<TransitionPair>(moves.length);		

		// check for aggressive move
		int balMove = pickAggressiveMove(game);
		
		if (balMove != Consts.NoMove){
			totalProbability -= 0.8;
			// if aggressive move exists, its chance of being picked is 80% + the chance of it being picked randomly.
			sStates.add(new TransitionPair(game.simulateMove(balMove), 0.8 + totalProbability/numMoves)); 
		}
		else{
			// check for defensive move.
			balMove = pickDefensiveMove(game);
			if (balMove != Consts.NoMove){
				totalProbability -= 0.8;
				// if aggressive move exists, its chance of being picked is 80% + the chance of it being picked randomly.
				sStates.add(new TransitionPair(game.simulateMove(balMove), 0.8 + totalProbability/numMoves)); 
			}
		}
		// distribute remaining probability over remaining moves.
		for (int i = 0; i<moves.length; i++){
			if(moves[i] != balMove){
				sStates.add(new TransitionPair(game.simulateMove(moves[i]), totalProbability/numMoves));
			}
		}
		return sStates;
	}
}