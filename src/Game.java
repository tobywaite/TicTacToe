import java.util.ArrayList;
import java.util.Arrays;

public class Game{
	
	// these variables hold current game state information
	// Agent objects hold current players.
	private Agent ourAgent;
	private Agent opponent;
	private Agent currentAgent;
	// nextMove is the next move, X or O, that should be made.
	private int nextMove;
	// represents the game board.
	private int board[]; 
	// Current game state (inProgress, Won, Lost, Tied, Invalid)
	private int gameState;
	private int turnsElapsed;

	// Default constructor initializes a new game given two agents.
	public Game(Agent ours, Agent theirs){
		// initialize to an empty board.	
		board = new int[9];
		Arrays.fill(board, Consts.MoveEmpty);

		// initial game state parameters
		gameState = Consts.GameInProgress;
		turnsElapsed = 0;
		nextMove = Consts.MoveX; //X moves first.

		// agents participating in the game.
		ourAgent = ours;
		opponent = theirs;
		currentAgent = ourAgent;
	}
	
	// Copy Constructor.
	public Game(Game oldGame){
		// new game points to same agents as old game
		ourAgent = oldGame.ourAgent;
		opponent = oldGame.opponent;
		currentAgent = oldGame.currentAgent;
		//deep copy old game board array.
		board = Arrays.copyOf(oldGame.board, oldGame.board.length);
		// maintain oldGame's state.
		gameState = oldGame.gameState;
		turnsElapsed = oldGame.turnsElapsed;
		nextMove = oldGame.nextMove;
	}

	// constructor to specify a starting game board 
	public Game(Agent ours, Agent theirs, int[] initBoard) {
		ourAgent = ours;
		opponent = theirs;
		currentAgent = ours;
		board = initBoard;
		turnsElapsed = 9 - possibleMoves().length;
		nextMove = (turnsElapsed % 2 == 0) ? Consts.MoveX : Consts.MoveO;
		gameState = evaluateGameState();
	}

	// Plays out a given game to completion. Returns gameState to indicate which team won.
	public int playGame(){
		while (gameState == Consts.GameInProgress){
			try {
				// execute current agent's move, then update game state.
				executeMove(currentAgent.pickMove(this));
				turnsElapsed++;
				gameState = evaluateGameState(); // Check victory conditions and update the current game state.				
			} catch (InvalidMoveException e) { // if an invalid move is made, don't execute a move.
				System.out.println("Agent tried to execute an impossible move! Try again!");
				e.printStackTrace();
			}
		}
		return gameState; // at end of game, return gameState to specify which team won.
	}
	
	// Execute a move for the current team. Updates game state to
	public void executeMove(int move) throws InvalidMoveException {
		executeMove(move, nextMove);
	}
	
	private void executeMove(int move, int moveType) throws InvalidMoveException {
		if (move < 0 || move > 8 || board[move] != 0){
			String errMessage = "Team " + nextMove + " tried to move to space " + move + " but couldn't!";
			throw new InvalidMoveException(errMessage);
		}
		else{
			board[move] = moveType;
			// update current agent so the other agent takes the next move.
			nextMove = (moveType == Consts.MoveX) ? Consts.MoveO : Consts.MoveX;
			if (currentAgent == ourAgent)
				currentAgent = opponent;
			else
				currentAgent = ourAgent;
			// update game state
			gameState = evaluateGameState();
		}
	}
	
	// display the current game board.
	public void printState(){
		String[] strBoard = new String[board.length];
		// convert integer representation of game board to string representation.
		for(int i=0; i<board.length; i++){
			switch(board[i]){
				case Consts.MoveX: 		strBoard[i] = "X"; break;
				case Consts.MoveO:		strBoard[i] = "O"; break;
				case Consts.MoveEmpty: 	strBoard[i] = " "; break;
			}
		}
		
		// indicate current game state.
		System.out.print("Game State: ");
		if (gameState == Consts.GameWon) System.out.println("Won!");
		else if(gameState == Consts.GameLost) System.out.println("Lost.");
		else if(gameState == Consts.GameTied) System.out.println("Tied.");
		else System.out.println("In Progress");
		
		// print out a pretty picture!
		System.out.println(strBoard[0] + "|" + strBoard[1] + "|" + strBoard[2]);
		System.out.println(strBoard[3] + "|" + strBoard[4] + "|" + strBoard[5]);
		System.out.println(strBoard[6] + "|" + strBoard[7] + "|" + strBoard[8]);
		System.out.println();
	}

	// return the current value of a requested square on the game board.
	public int getSquare(int square){
		return board[square];
	}
	
	// This checks for see if the game has concluded, either by one agent winning or a tie. 
	// The return value indicates whether our agent won, lost, or tied.
	public int evaluateGameState() {
		
		if(gameState == Consts.GameInvalid)
			return Consts.GameInvalid;
		
		// check each row, column, and diagonal for three matching moves. If such a set is found, the game has ended.
		boolean won = checkMatch(0,1,2,Consts.MoveX) || checkMatch(3,4,5,Consts.MoveX) || checkMatch(6,7,8,Consts.MoveX) || // check all rows
					  checkMatch(0,3,6,Consts.MoveX) || checkMatch(1,4,7,Consts.MoveX) || checkMatch(2,5,8,Consts.MoveX) || // check all columns
					  checkMatch(0,4,8,Consts.MoveX) || checkMatch(6,4,2,Consts.MoveX);							 // check both diagonals

		boolean lost = checkMatch(0,1,2,Consts.MoveO) || checkMatch(3,4,5,Consts.MoveO) || checkMatch(6,7,8,Consts.MoveO) || // check all rows
		  			   checkMatch(0,3,6,Consts.MoveO) || checkMatch(1,4,7,Consts.MoveO) || checkMatch(2,5,8,Consts.MoveO) || // check all columns
		  			   checkMatch(0,4,8,Consts.MoveO) || checkMatch(6,4,2,Consts.MoveO);							 // check both diagonals
		
		// return a value corresponding to resulting game state.
		if (won)
			return Consts.GameWon;
		else if (lost)
			return Consts.GameLost;
		else if (turnsElapsed == Consts.NumSquares) // game is a tie if there is no winner after 9 turns.
			return Consts.GameTied;
		else
			return Consts.GameInProgress;
	}

	// Checks a set of three squares to see if they match (indicating a victory for a player)
	private boolean checkMatch(int space1, int space2, int space3, int moveType){
		if(board[space1]==moveType && board[space2]==moveType && board[space3]==moveType)
			return true;
		return false;
	}

	// return number of moves that have been played thus far in the game
	public int getTurnsElapsed() {
		return turnsElapsed;
	}

	// simulate a given move. Returns a copy of the game instance without modifying the existing game instance.
	public Game simulateMove(int move) {
		return simulateMove(move, nextMove);
	}
	
	public Game simulateMove(int move, int moveType) {
		// Copy the current game to a new Game instance.
		Game tempGame = new Game(this);
		// Execute the move on the new game state
		try {
			tempGame.executeMove(move, moveType);
			tempGame.turnsElapsed++;
		} catch (InvalidMoveException e) {
			// if an invalid move was attempted, indicate so in the new game's gameState.
			tempGame.gameState = Consts.GameInvalid;
		}
		return tempGame;
	}

	// return the current game state.
	public int[] getBoard() {
		return board;
	}
	
	// Returns an array containing all allowed moves (corresponding to empty spaces).
	public Integer[] possibleMoves() {
		ArrayList<Integer> allowedMoves = new ArrayList<Integer>();
		for(int i=0; i<board.length; i++){
			if (board[i] == Consts.MoveEmpty)
				allowedMoves.add(i);
		} 
		return allowedMoves.toArray(new Integer[0]);
	}

	// Set the game state to a specified value
	public void setGameState(int inState) {
		gameState = inState;
	}

	
	// Get the value of the next move.
	public int getNextMove(){
		return nextMove;
	}
}