/*
 * RandomAgent Ð A naive Tic Tac Toe agent.
 * 
 * This is an agent that plays TicTacToe. It is used in conjunction with the 
 * Tic Tac Toe simulator in TicTacToe.java
 * 
 * When asked to make a move, this agent simply selects one of the remaining 
 * squares on the grid at random and moves there.
 * 
 * Author: 	Toby Waite
 * Contact: toby.waite@gmail.com
 * Date:	November 5th, 2010
 */

import java.util.Random;

public abstract class RandomAgent extends Agent {
	
	public RandomAgent(){
		r = new Random();
	}
	
	public void initialize(Agent opponent){
		// Do nothing. Random Agents need no initialization.
	}
	
	// A random agent does not change its behavior based on its 
	// previous actions, so this method does nothing.
	void reportAction(Game game) {}
	
	public abstract int pickMove(Game game);

	protected int pickRandomMove(Game game){
		// set selector to a random value between 0 and the number of empty squares to select one of the empty squares.
		int selector = r.nextInt(Consts.NumSquares-game.getTurnsElapsed());   
		int selectedMove = 0;
		
		// iterate over empty squares. Return the empty square that corresponds to selector.
		for (selectedMove = 0; selectedMove<Consts.NumSquares; selectedMove++){	
			if (game.getSquare(selectedMove) == Consts.MoveEmpty){
				if (selector == 0){
					break;
				}
				else
					selector--;
			}
		}
		return selectedMove;
	}
	
	// A defensive move is a move that would prevent an opponent from winning. This detects a defensive move
	//  on the current game board, or returns Consts.NoMove if one cannot be found. 
	protected int pickDefensiveMove(Game game) {
		int moveType = (game.getNextMove() == Consts.MoveX)? Consts.MoveO : Consts.MoveX;
		// check for blocking moves at each spot. If one is found, return that move.
		for(int move=0; move<Consts.NumSquares; move++){
			if(game.simulateMove(move, moveType).evaluateGameState() == Consts.GameLost)
				return move;
		}
		return Consts.NoMove; // if no blocking moves are found, return NoMove to indicate that no moves were found.
	}
	
	// An aggressive move is a move that would allow the agent to win the game. This detects an aggressive move
	//  on the current game board, or returns Consts.NoMove if one cannot be found. 
	protected int pickAggressiveMove(Game game) {
		int moveType = (game.getNextMove() == Consts.MoveO)? Consts.MoveO : Consts.MoveX;
		// Check for winning moves at each spot. If one is found, return that move.
		for(int move=0; move<Consts.NumSquares; move++){
			if(game.simulateMove(move, moveType).evaluateGameState() == Consts.GameWon)
				return move;
		}
		return Consts.NoMove; // if no winning moves are found, return NoMove to indicate that no moves were found.
	}
		
	// Allows an agent to select a move with a certain probability. The input 'move' is selected with a probability
	//  of one-in-'odds', and a random move is selected otherwise.
	protected int selectMoveOrRandom(int move, int odds, Game game){
		// pick a random move with a 1 in odds chance, otherwise return move.
		if (r.nextInt(odds)==0)
			return pickRandomMove(game);
		else
			return move;
	}
}