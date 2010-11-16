import java.util.ArrayList;

import com.sun.tools.javac.util.Pair;

public class NaiveAgent extends RandomAgent {

	// Simply pick a random move from remaining open squares.
	public int pickMove(Game game) {
		return pickRandomMove(game);
	}

	public ArrayList<Pair<Game, Double>> getSuccessorStates(Game game) {
		
		Integer[] moves = game.possibleMoves();
		
		ArrayList<Pair<Game, Double>> sStates = new ArrayList<Pair<Game, Double>>(moves.length);
		
		for (int i = 0; i<moves.length; i++){
			Pair<Game, Double> state = new Pair<Game, Double>(game.simulateMove(moves[i]), 1.0/moves.length);
			sStates.add(state);
		}
		
		return sStates;
	}
}
