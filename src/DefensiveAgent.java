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
}
