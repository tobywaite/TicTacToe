import java.util.Random;

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
}
