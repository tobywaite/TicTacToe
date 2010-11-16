import java.util.ArrayList;

import com.sun.tools.javac.util.Pair;

public class DefensiveAgent extends RandomAgent {
	
	public int pickMove(Game game){
	
		// With 50% probability, move randomly. 
		// Otherwise, move defensively to block other agent from winning.
		// If opponent does not have a winning move, move randomly.
	
		int move = pickDefensiveMove(game);
		if (move != Consts.NoMove)
			return selectMoveOrRandom(move, 2, game); //move randomly with 50% of the time and move aggressively 50% of the time.
		else
			return pickRandomMove(game);
	}

	public ArrayList<Pair<Game, Double>> getSuccessorStates(Game game) { 
		
		Integer[] moves = game.possibleMoves();
		int numMoves = moves.length;
		Double totalProbability = 1.0;
		ArrayList<Pair<Game, Double>> sStates = new ArrayList<Pair<Game, Double>>(moves.length);

		
		int defMove = pickDefensiveMove(game);
		int moveType = (this.team == Consts.TeamX) ? Consts.MoveX : Consts.MoveO;
		
		if (defMove != Consts.NoMove){
			sStates.add(new Pair<Game, Double>(game.simulateMove(defMove, moveType), 0.5)); // if defensive move exists, 50% chance of picking it.
			numMoves--;
			totalProbability -= 0.5;
		}
		
		for (int i = 0; i<moves.length; i++){
			if(moves[i] != defMove){
				sStates.add(new Pair<Game, Double>(game.simulateMove(moves[i], moveType), totalProbability/numMoves));
			}
		}
		return sStates;
	}
}
