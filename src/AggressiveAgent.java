import java.util.ArrayList;
import java.util.Random;
import com.sun.tools.javac.util.Pair;

public class AggressiveAgent extends RandomAgent {

	Random r;
	
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

	public ArrayList<Pair<Game, Double>> getSuccessorStates(Game game) { 
		
		Integer[] moves = game.possibleMoves();
		int numMoves = moves.length;
		Double totalProbability = 1.0;
		ArrayList<Pair<Game, Double>> sStates = new ArrayList<Pair<Game, Double>>(moves.length);

		int aggMove = pickAggressiveMove(game);
		
		if (aggMove != Consts.NoMove){
			sStates.add(new Pair<Game, Double>(game.simulateMove(aggMove), 0.8)); // if aggressive move exists, 80% chance of picking it.
			numMoves--;
			totalProbability -= 0.8;
		}
		
		for (int i = 0; i<moves.length; i++){
			if(moves[i] != aggMove){
				sStates.add(new Pair<Game, Double>(game.simulateMove(moves[i]), totalProbability/numMoves));
			}
		}
		return sStates;
	}
}