/* 
 * Tic Tac Toe ÐÊA simple machine learning simulator.
 * 
 * Class: Agent
 * 
 * This abstract class defines the structure for a TicTacToe agent. All new agents must implement these methods.
 *  
 * Author: 	Toby Waite
 * Contact: toby.waite@gmail.com 
 * Updated: November 16th, 2010.
 */

import java.util.ArrayList;
import java.util.Random;
import com.sun.tools.javac.util.Pair;

public abstract class Agent {

	protected static Random r;

	// Initialize any internal state variables needed before a game is played
	public abstract void initialize(Agent enemyAgent);
	
	// Given a current game state, pick a move.
	public abstract int pickMove(Game game);
	
	// Given a current game state, return all possible next states and the probability
	//  of transitioning to that state.
	public abstract ArrayList<TransitionPair> getSuccessorStates(Game game);
}