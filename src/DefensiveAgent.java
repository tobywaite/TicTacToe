/* 
 * Tic Tac Toe ÐÊA simple machine learning simulator.
 * 
 * Class: DefensiveAgent
 * 
 * DefensiveAgent is a Tic Tac Toe agent that follows a probabilistic rule-based policy. 
 *  - 50% of the time, the agent will choose a move from the set of remaining moves at random.
 *  - 50% of the time, the agent will attempt to make a "defensive" move. A defensive
 *     move is a move that would prevent the opposing agent from winning the game. 
 *     If no such move can be made, the agent moves randomly.
 * 
 * Author: 	Toby Waite
 * Contact: toby.waite@gmail.com 
 * Updated: November 16th, 2010.
 */

import java.util.ArrayList;
import com.sun.tools.javac.util.Pair;

public class DefensiveAgent extends RandomAgent {
	
	public int pickMove(Game game){
	
		// With 50% probability, move randomly. 
		// Otherwise, move defensively to block other agent from winning.
		// If opponent does not have a winning move, move randomly.
		int move = pickDefensiveMove(game);
		if (move != Consts.NoMove)
			//move randomly with 50% of the time and move defensively 50% of the time.
			return selectMoveOrRandom(move, 2, game); 
		else
			return pickRandomMove(game);
	}

	// For a given game, returns a set of games that includes the result of all possible moves by this agent. Each
	//  of those moves is encapsulated in a Pair with the probability of that move being selected. The chance of
	//  a move being selected is calculated based on the rules described above.
	public ArrayList<Pair<Game, Double>> getSuccessorStates(Game game) { 
		
		Integer[] moves = game.possibleMoves();
		int numMoves = moves.length;
		Double totalProbability = 1.0;
		ArrayList<Pair<Game, Double>> sStates = new ArrayList<Pair<Game, Double>>(moves.length);
		
		// if a defensive move exists, the probability of selecting it is 50% + the chance of selecting it randomly.
		int defMove = pickDefensiveMove(game);		
		if (defMove != Consts.NoMove){
			sStates.add(new Pair<Game, Double>(game.simulateMove(defMove), 0.5 + 0.5/numMoves)); // if defensive move exists, 50% chance of picking it.
			totalProbability -= 0.5;
		}		
		// distribute the remaining probability randomly over the remaining states.
		for (int i = 0; i<moves.length; i++){
			if(moves[i] != defMove){
				sStates.add(new Pair<Game, Double>(game.simulateMove(moves[i]), totalProbability/numMoves));
			}
		}
		return sStates;
	}
}