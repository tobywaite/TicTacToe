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

public class RandomAgent extends Agent {

	private static Random r;
	
	public RandomAgent(){
		r = new Random();
	}
	
	// A random agent does not change its behavior based on its 
	// previous actions, so this method does nothing.
	void reportAction(Game game) {}
	
	public int pickMove(Game game){
		return pickRandomMove(game);
	}

	protected int pickRandomMove(Game game){	
		int selector = r.nextInt(Consts.NumSquares-game.getTurnsElapsed()); // 
		int selectedMove = 0;
		
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
		int oppMoveType;
		if (team == Consts.TeamX)
			oppMoveType = Consts.MoveO;
		else
			oppMoveType = Consts.MoveX;
		
		// check for blocking moves at each spot.
		// Square 0 -- top left
		if (      game.getSquare(0) == Consts.MoveEmpty && 									 // check this square
				((game.getSquare(1) == oppMoveType && game.getSquare(2) == oppMoveType)   || // check row
				 (game.getSquare(3) == oppMoveType && game.getSquare(6) == oppMoveType)   || // check column
				 (game.getSquare(4) == oppMoveType && game.getSquare(8) == oppMoveType)))    // check diagonal
			return 0;
		// Square 1 -- top center
		else if  (game.getSquare(1) == Consts.MoveEmpty && 									 // check this square
				((game.getSquare(0) == oppMoveType && game.getSquare(2) == oppMoveType)   || // check row
				 (game.getSquare(4) == oppMoveType && game.getSquare(7) == oppMoveType)))    // check column
			return 1;
		// Square 2 -- top right
		else if  (game.getSquare(2) == Consts.MoveEmpty && 									 // check this square
				((game.getSquare(0) == oppMoveType && game.getSquare(1) == oppMoveType)   || // check row
				 (game.getSquare(5) == oppMoveType && game.getSquare(8) == oppMoveType)   || // check column
				 (game.getSquare(4) == oppMoveType && game.getSquare(6) == oppMoveType)))    // check diagonal
			return 2;
		// Square 3 -- middle left
		else if  (game.getSquare(3) == Consts.MoveEmpty && 									 // check this square
				((game.getSquare(4) == oppMoveType && game.getSquare(5) == oppMoveType)   || // check row
				 (game.getSquare(0) == oppMoveType && game.getSquare(6) == oppMoveType)))    // check column
			return 3;
		// Square 4 -- center square
		else if  (game.getSquare(4) == Consts.MoveEmpty && 									 // check this square
				((game.getSquare(3) == oppMoveType && game.getSquare(5) == oppMoveType)   || // check row
				 (game.getSquare(1) == oppMoveType && game.getSquare(7) == oppMoveType)   || // check column
				 (game.getSquare(0) == oppMoveType && game.getSquare(8) == oppMoveType)   || // check diagonal
				 (game.getSquare(2) == oppMoveType && game.getSquare(6) == oppMoveType)))    // check other diagonal
			return 4;
		// Square 5 -- middle right
		else if  (game.getSquare(5) == Consts.MoveEmpty && 									 // check this square
				((game.getSquare(3) == oppMoveType && game.getSquare(4) == oppMoveType)   || // check row
				 (game.getSquare(2) == oppMoveType && game.getSquare(8) == oppMoveType)))    // check column
			return 5;
		// Square 6 -- bottom left
		else if  (game.getSquare(6) == Consts.MoveEmpty && 									 // check this square
				((game.getSquare(7) == oppMoveType && game.getSquare(8) == oppMoveType)   || // check row
				 (game.getSquare(0) == oppMoveType && game.getSquare(3) == oppMoveType)   || // check column
				 (game.getSquare(2) == oppMoveType && game.getSquare(4) == oppMoveType)))    // check diagonal
			return 2;
		// Square 7 -- bottom center
		else if  (game.getSquare(7) == Consts.MoveEmpty && 									 // check this square
				((game.getSquare(6) == oppMoveType && game.getSquare(8) == oppMoveType)   || // check row
				 (game.getSquare(1) == oppMoveType && game.getSquare(4) == oppMoveType)))    // check column
			return 7;
		// Square 8 -- bottom right
		else if  (game.getSquare(8) == Consts.MoveEmpty && 									 // check this square
				((game.getSquare(7) == oppMoveType && game.getSquare(8) == oppMoveType)   || // check row
				 (game.getSquare(0) == oppMoveType && game.getSquare(3) == oppMoveType)   || // check column
				 (game.getSquare(2) == oppMoveType && game.getSquare(4) == oppMoveType)))    // check diagonal
			return 8;
		else
			return Consts.NoMove; // if no blocking moves are found, return -1 to indicate that no moves were found.
	}
	
	protected int pickAggressiveMove(Game game) {
		
		// determine opponent move type.
		int ourMoveType;
		if (team == Consts.TeamX)
			ourMoveType = Consts.MoveX;
		else
			ourMoveType = Consts.MoveO;
		
		// check for blocking moves at each spot.
		// Square 0 -- top left
		if (      game.getSquare(0) == Consts.MoveEmpty && 									 // check this square
				((game.getSquare(1) == ourMoveType && game.getSquare(2) == ourMoveType)   || // check row
				 (game.getSquare(3) == ourMoveType && game.getSquare(6) == ourMoveType)   || // check column
				 (game.getSquare(4) == ourMoveType && game.getSquare(8) == ourMoveType)))    // check diagonal
			return 0;
		// Square 1 -- top center
		else if  (game.getSquare(1) == Consts.MoveEmpty && 									 // check this square
				((game.getSquare(0) == ourMoveType && game.getSquare(2) == ourMoveType)   || // check row
				 (game.getSquare(4) == ourMoveType && game.getSquare(7) == ourMoveType)))    // check column
			return 1;
		// Square 2 -- top right
		else if  (game.getSquare(2) == Consts.MoveEmpty && 									 // check this square
				((game.getSquare(0) == ourMoveType && game.getSquare(1) == ourMoveType)   || // check row
				 (game.getSquare(5) == ourMoveType && game.getSquare(8) == ourMoveType)   || // check column
				 (game.getSquare(4) == ourMoveType && game.getSquare(6) == ourMoveType)))    // check diagonal
			return 2;
		// Square 3 -- middle left
		else if  (game.getSquare(3) == Consts.MoveEmpty && 									 // check this square
				((game.getSquare(4) == ourMoveType && game.getSquare(5) == ourMoveType)   || // check row
				 (game.getSquare(0) == ourMoveType && game.getSquare(6) == ourMoveType)))    // check column
			return 3;
		// Square 4 -- center square
		else if  (game.getSquare(4) == Consts.MoveEmpty && 									 // check this square
				((game.getSquare(3) == ourMoveType && game.getSquare(5) == ourMoveType)   || // check row
				 (game.getSquare(1) == ourMoveType && game.getSquare(7) == ourMoveType)   || // check column
				 (game.getSquare(0) == ourMoveType && game.getSquare(8) == ourMoveType)   || // check diagonal
				 (game.getSquare(2) == ourMoveType && game.getSquare(6) == ourMoveType)))    // check other diagonal
			return 4;
		// Square 5 -- middle right
		else if  (game.getSquare(5) == Consts.MoveEmpty && 									 // check this square
				((game.getSquare(3) == ourMoveType && game.getSquare(4) == ourMoveType) ||   // check row
				 (game.getSquare(2) == ourMoveType && game.getSquare(8) == ourMoveType)))    // check column
			return 5;
		// Square 6 -- bottom left
		else if  (game.getSquare(6) == Consts.MoveEmpty && 									 // check this square(game.getSquare(7) == ourMoveType && game.getSquare(8) == ourMoveType) || // check row
				((game.getSquare(0) == ourMoveType && game.getSquare(3) == ourMoveType)   || // check column
				 (game.getSquare(2) == ourMoveType && game.getSquare(4) == ourMoveType)))    // check diagonal
			return 2;
		// Square 7 -- bottom center
		else if  (game.getSquare(7) == Consts.MoveEmpty && 									 // check this square
				((game.getSquare(6) == ourMoveType && game.getSquare(8) == ourMoveType)   || // check row
				 (game.getSquare(1) == ourMoveType && game.getSquare(4) == ourMoveType)))    // check column
			return 7;
		// Square 8 -- bottom right
		else if  (game.getSquare(8) == Consts.MoveEmpty && 									 // check this square
				((game.getSquare(7) == ourMoveType && game.getSquare(8) == ourMoveType)   || // check row
				 (game.getSquare(0) == ourMoveType && game.getSquare(3) == ourMoveType)   || // check column
				 (game.getSquare(2) == ourMoveType && game.getSquare(4) == ourMoveType)))    // check diagonal
			return 8;
		else
			return Consts.NoMove; // if no blocking moves are found, pick a move randomly.
	}

	protected int selectMoveOrRandom(int move, int odds, Game game){
		if (r.nextInt(odds)==0)
			return pickRandomMove(game);
		else
			return move;
	}
}
