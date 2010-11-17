/* 
 * Tic Tac Toe ÐÊA simple machine learning simulator.
 * 
 * Class: TransitionPair
 * 
 * This is a container class to hold information about successor states, used in the machine learning algorithms. 
 * Each TransitionPair consists of a Game and a Double. The Double is the probability of the Game being chosen as 
 * the next move for an agent.
 * 
 * Author: 	Toby Waite
 * Contact: toby.waite@gmail.com 
 * Updated: November 16th, 2010.
 */

public class TransitionPair {
	
	public Game game;
	public Double probability;
	
	public TransitionPair(Game g, Double p){
		game = g;
		probability = p;
	}
}