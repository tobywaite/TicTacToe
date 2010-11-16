import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.sun.tools.javac.util.Pair;

public class HumanAgent extends Agent {

	public void initialize(Agent enemyAgent){
		// Do nothing. Human Agents don't need initialization.
	}
	
	public int pickMove(Game game) {
		game.printState();
		System.out.println("You are playing 'O'.");
		System.out.println("Where would you like to move? Select an empty space based on the following diagram:");
		System.out.println("0|1|2\n" +
				           "3|4|5\n" +
				           "6|7|8");
		System.out.print("My move:");
		
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		int move = Consts.NoMove;
		try{
			move = Integer.parseInt(br.readLine());
		} catch(IOException e){
			e.printStackTrace();
			System.exit(1);
		} catch(NumberFormatException e){
			System.out.println("Invalid move selection! You must enter an integer!");
			return pickMove(game);
		}
		return move;
		
	}

	// Assume human agent acts randomly for purposes of determining transition probability.
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