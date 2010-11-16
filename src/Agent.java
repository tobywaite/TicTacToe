import java.util.ArrayList;
import java.util.Random;
import com.sun.tools.javac.util.Pair;

public abstract class Agent {

	protected static Random r;

	public abstract void initialize(Agent enemyAgent);
	
	public abstract int pickMove(Game game);
	
	public abstract ArrayList<Pair<Game, Double>> getSuccessorStates(Game game);
	
}