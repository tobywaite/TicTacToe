import java.util.Random;

public class Game{
	
	private Agent ourAgent;
	private Agent opponent;
	private Agent currentAgent;
	
	private int board[]; 
	private int gameState;
	private int turnsElapsed;
	
	public Game() {
		board = new int[] 	{Consts.MoveEmpty,Consts.MoveEmpty,Consts.MoveEmpty,
				 Consts.MoveEmpty,Consts.MoveEmpty,Consts.MoveEmpty,
				 Consts.MoveEmpty,Consts.MoveEmpty,Consts.MoveEmpty};

		gameState = Consts.GameInProgress;
		turnsElapsed = 0;
	}
	
	public Game(Agent ours, Agent theirs){
		// initialize to an empty board.
		
		board = new int[] 	{Consts.MoveEmpty,Consts.MoveEmpty,Consts.MoveEmpty,
				 Consts.MoveEmpty,Consts.MoveEmpty,Consts.MoveEmpty,
				 Consts.MoveEmpty,Consts.MoveEmpty,Consts.MoveEmpty};

		gameState = Consts.GameInProgress;
		turnsElapsed = 0;

		Random r = new Random();
		
		ourAgent = ours;
		opponent = theirs;
				
		if (r.nextInt(2) == 0){
			ourAgent.setTeamX();
			opponent.setTeamO();
			currentAgent = ourAgent;
		}
		else{
			ourAgent.setTeamO();
			opponent.setTeamX();
			currentAgent = opponent;
		}
	}
	
	private Game(Game oldGame){
		ourAgent = oldGame.ourAgent;
		opponent = oldGame.opponent;
		currentAgent = oldGame.currentAgent;
		
		board = new int[oldGame.board.length];
		
		// deep copy board array
		for(int i=0; i<board.length; i++){
			board[i] = oldGame.board[i];
		}

		gameState = oldGame.gameState;
		turnsElapsed = oldGame.turnsElapsed;
	}

	public int playGame(){
		
		while (gameState == Consts.GameInProgress){
			try {
				
				Game previousTurn = new Game(this);
				
				int move = currentAgent.pickMove(this);
				executeMove(move, (currentAgent.getTeam() == Consts.TeamX) ? Consts.MoveX : Consts.MoveO); // execute move picked by agent. If invalid, this will throw InvalidMoveException.
				turnsElapsed++;
				gameState = evaluateGameState(); // Check victory conditions and update the current game state.
			
				currentAgent.reportAction(this, previousTurn);
				
				// update currentAgent so the other agent takes the next move.
				if (currentAgent == ourAgent)
					currentAgent = opponent;
				else
					currentAgent = ourAgent;
				
			} catch (InvalidMoveException e) {
				System.out.println("Agent tried to execute an impossible move! Try again!");
				e.printStackTrace();
			}
		}
		
		return gameState;
	}
	
	public void executeMove(int move, int moveType) throws InvalidMoveException{
		if (move < 0 || move > 8 || board[move] != 0){
			String errMessage = "Team " + moveType + " tried to move to space " + move + " but couldn't!";
			throw new InvalidMoveException(errMessage);
		}
		else{
			board[move] = moveType;
		}
	}
	
	public void printState(){
		
		String[] strBoard = new String[board.length];
		
		for(int i=0; i<board.length; i++){
			switch(board[i]){
				case Consts.MoveX: 		strBoard[i] = "X"; break;
				case Consts.MoveO:		strBoard[i] = "O"; break;
				case Consts.MoveEmpty: 	strBoard[i] = " "; break;
			}
		}
		
		System.out.print("Game State: ");
		
		if (gameState == Consts.GameWon) System.out.println("Won!");
		else if(gameState == Consts.GameLost) System.out.println("Lost.");
		else if(gameState == Consts.GameTied) System.out.println("Tied.");
		else System.out.println("In Progress");
		
		System.out.println(strBoard[0] + "|" + strBoard[1] + "|" + strBoard[2]);
		System.out.println(strBoard[3] + "|" + strBoard[4] + "|" + strBoard[5]);
		System.out.println(strBoard[6] + "|" + strBoard[7] + "|" + strBoard[8]);
		System.out.println();
	}

	public int getSquare(int square){
		return board[square];
	}
	
	// This checks for see if the game has concluded, either by one agent winning or a tie. 
	// The return value indicates whether our agent won, lost, or tied.
	public int evaluateGameState() {
		
		if(gameState == Consts.GameInvalid)
			return Consts.GameInvalid;
		
		/* There are eight columns/rows/diagonals to check. Technically we only need to check the game states
		   that were affected by the last move, however the code is cleaner if we simply check all
		   of the combinations. As this is not an expensive operation, this was the implementation used. */
				
		boolean gameOver =  checkVictory(0,1,2) || checkVictory(3,4,5) || checkVictory(6,7,8) || // check all rows
							checkVictory(0,3,6) || checkVictory(1,4,7) || checkVictory(2,5,8) || // check all columns
							checkVictory(0,4,8) || checkVictory(6,4,2);							 // check both diagonals
		
		if (gameOver)
			return (currentAgent == ourAgent) ? Consts.GameWon : Consts.GameLost;
		else if (turnsElapsed == Consts.NumSquares) // game is a tie if there is no winner after 9 turns.
			return Consts.GameTied;
		else
			return Consts.GameInProgress;
	}
	
	private boolean checkVictory(int space1, int space2, int space3){
		if(board[space1]==board[space2] && board[space1]==board[space3] && board[space1]!=0)
			return true;
		return false;
	}

	public int getTurnsElapsed() {
		return turnsElapsed;
	}

	public Game simulateMove(int move, int moveType) {

		Game tempGame = new Game(this);
		
		try {
			tempGame.executeMove(move, moveType);
		} catch (InvalidMoveException e) {
			tempGame.gameState = Consts.GameInvalid;
		}
		return tempGame;
	}

	public int[] getBoard() {
		return board;
	}
}