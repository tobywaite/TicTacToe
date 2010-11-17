/* 
 * Tic Tac Toe Ð A simple machine learning simulator.
 * 
 * Class: NaiveAgent
 * 
 * NaiveAgent is a Tic Tac Toe agent that randomly selects a move each turn.
 * 
 * Author: 	Toby Waite
 * Contact: toby.waite@gmail.com 
 * Updated: November 16th, 2010.
 */

import java.util.ArrayList;
import com.sun.tools.javac.util.Pair;

public class NaiveAgent extends RandomAgent {

	// Simply pick a random move from remaining open squares.
	public int pickMove(Game game) {
		return pickRandomMove(game);
	}

	// For a given game, returns a set of games that includes the result of all possible moves by this agent. Each
	//  of those moves is encapsulated in a Pair with the probability of that move being selected. The chance of
	//  each move being selected is equal for the NaiveAgent.
	public ArrayList<Pair<Game, Double>> getSuccessorStates(Game game) {
		
		Integer[] moves = game.possibleMoves();
		ArrayList<Pair<Game, Double>> sStates = new ArrayList<Pair<Game, Double>>(moves.length);
		
		// Distribute probability evenly over all possible moves.
		for (int i = 0; i<moves.length; i++){
			Pair<Game, Double> state = new Pair<Game, Double>(game.simulateMove(moves[i]), 1.0/moves.length);
			sStates.add(state);
		}
		return sStates;
	}
}