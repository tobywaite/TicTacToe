
public class NaiveAgent extends RandomAgent {

	// Simply pick a random move from remaining open squares.
	public int pickMove(Game game) {
		return pickRandomMove(game);
	}
}
