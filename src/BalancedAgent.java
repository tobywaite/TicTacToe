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

}
