import java.util.ArrayList;

import com.sun.tools.javac.util.Pair;

public class BalancedAgent extends RandomAgent {
	
	private static final int noMoves = -1;
	
	public int pickMove(Game game){
	
		int move = pickAggressiveMove(game);
		if (move != noMoves){
			return selectMoveOrRandom(move, 5, game); // select random with 1:5 odds. 4:5 odds to select defensive move.
		}
		move = pickDefensiveMove(game);
		if (move != noMoves){
			return selectMoveOrRandom(move, 5, game); // select random with 1:5 odds. 4:5 odds to select defensive move.
		}
		return pickRandomMove(game); // if no winning or blocking move can be executed, move randomly.
	}

	public ArrayList<Pair<Game, Double>> getSuccessorStates(Game game) { 
		
		Integer[] moves = game.possibleMoves();
		int numMoves = moves.length;
		Double totalProbability = 1.0;
		ArrayList<Pair<Game, Double>> sStates = new ArrayList<Pair<Game, Double>>(moves.length);
		int moveType = (this.team == Consts.TeamX) ? Consts.MoveX : Consts.MoveO;
		
		// check for aggressive move
		int balMove = pickAggressiveMove(game);
		
		if (balMove != Consts.NoMove){
			// if found, assign high probability to that move.
			sStates.add(new Pair<Game, Double>(game.simulateMove(balMove, moveType), 0.8)); // if aggressive move exists, 80% chance of picking it.
			numMoves--;
			totalProbability -= 0.8;
		}
		else{
			// check for defensive move.
			balMove = pickDefensiveMove(game);
			if (balMove != Consts.NoMove){
				sStates.add(new Pair<Game, Double>(game.simulateMove(balMove, moveType), 0.8)); // if aggressive move exists, 80% chance of picking it.
				numMoves--;
				totalProbability -= 0.8;
			}
		}
		// distribute remaining probability over remaining moves.
		for (int i = 0; i<moves.length; i++){
			if(moves[i] != balMove){
				sStates.add(new Pair<Game, Double>(game.simulateMove(moves[i], moveType), totalProbability/numMoves));
			}
		}
		return sStates;
	}
}
