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
	
	protected int pickDefensiveMove(Game game) {
		// determine opponent move type.
		int oppMoveType = (team == Consts.TeamX) ? Consts.MoveO : Consts.MoveX;
		
		// check for blocking moves at each spot. If one is found, return that move.
		for(int move=0; move<Consts.NumSquares; move++){
			if(game.simulateMove(move, oppMoveType).evaluateGameState() == Consts.GameLost)
				return move;
		}
		return Consts.NoMove; // if no blocking moves are found, return NoMove to indicate that no moves were found.
	}
	
	protected int pickAggressiveMove(Game game) {
		// determine our move type.
		int ourMoveType = (team == Consts.TeamX) ? Consts.MoveX : Consts.MoveO;
		
		// Check for winning moves at each spot. If one is found, return that move.
		for(int move=0; move<Consts.NumSquares; move++){
			game.printState();
			if(game.simulateMove(move, ourMoveType).evaluateGameState() == Consts.GameWon)
				return move;
		}
		return Consts.NoMove; // if no winning moves are found, return NoMove to indicate that no moves were found.
	}
		
	protected int selectMoveOrRandom(int move, int odds, Game game){
		// pick a random move with a 1 in odds chance, otherwise return move.
		if (r.nextInt(odds)==0)
			return pickRandomMove(game);
		else
			return move;
	}
	
	public void reportAction(Game currentGame, Game oldGame) {
		// We don't care about the last move, so do nothing.
	}
}
