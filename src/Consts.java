public final class Consts{
	// Opponent types
	public static final int OpponentRandom = 0;
	public static final int OpponentDefensive = 1;
	public static final int OpponentAggressive = 2;
	public static final int OpponentBalanced = 3;
	public static final int OpponentHuman = 4;
	
	// Our agent types
	public static final int AgentRandom= 0;
	public static final int AgentValItr = 1;
	public static final int AgentPolItr = 2;
	public static final int AgentHuman = 3;
	
	// name possible game states
	public static final int GameInvalid = -1;
	public static final int GameInProgress = 0;
	public static final int GameWon = 1;
	public static final int GameLost = 2;
	public static final int GameTied = 3;

	// team names
	public static final int TeamX = 0;
	public static final int TeamO = 1;
	public static final int TeamNone = 2;
	
	// board location values
	public static final int MoveEmpty = 0;
	public static final int MoveX = 1;
	public static final int MoveO = 2;
	
	// general constants
	public static final int NumSquares = 9;
	public static final int NoMove = -1;
	
	// learning parameters
	public static final double DiscountFactor = 0.8;
	public static final int RewardInProgress = -1;
	public static final int RewardWon = 10;
	public static final int RewardLost = -10;
	public static final int RewardTied = 1;
	public static final int RewardOther = 0;
	public static final Double InitialValue = 0.0;
	public static final Integer InitialPolicy = 0;

}