/* 
 * Tic Tac Toe ÐÊA simple machine learning simulator.
 * 
 * Class: HumanAgent
 * 
 * HumanAgent is a Tic Tac Toe agent that accepts user input from the command line to specify
 *  moves on a game board.
 * 
 * Author: 	Toby Waite
 * Contact: toby.waite@gmail.com 
 * Updated: November 16th, 2010.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import com.sun.tools.javac.util.Pair;

public class HumanAgent extends Agent {

	public void initialize(Agent enemyAgent){
		// Do nothing. Human Agents don't need initialization.
	}
	
	// Prompt the user to pick a move.
	public int pickMove(Game game) {
		// display the game board
		game.printState();
		// show user possible moves.
		System.out.println("You are playing 'O'.");
		System.out.println("Where would you like to move? Select an empty space based on the following diagram:");
		System.out.println("0|1|2\n" +
				           "3|4|5\n" +
				           "6|7|8");
		System.out.print("My move:");
		
		// read in user input. If bad input is specified, allow user to try again. 
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
	//  The Machine Learning algorithms implemented are model based algorithms, so something must
	//  be assumed even though, in this case, it is clearly false.
	public ArrayList<Pair<Game, Double>> getSuccessorStates(Game game) {
		
		Integer[] moves = game.possibleMoves();		
		ArrayList<Pair<Game, Double>> sStates = new ArrayList<Pair<Game, Double>>(moves.length);
		
		// return all possible moves, indicating equal probability for each move.
		for (int i = 0; i<moves.length; i++){
			Pair<Game, Double> state = new Pair<Game, Double>(game.simulateMove(moves[i]), 1.0/moves.length);
			sStates.add(state);
		}
		return sStates;
	}
}