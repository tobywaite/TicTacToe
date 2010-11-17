/* 
 * Tic Tac Toe ÐÊA simple machine learning simulator.
 * 
 * Class: TicTacToe
 * 
 * This class represents a TicTacToe match between two automatic agents. The main method for the Tic Tac Toe
 * game is in this class.
 * 
 * This program allows two agents to play each other in a game of Tic Tac Toe. 
 * A number of naive agents have been implemented that perform a mix of random and simple rule based policies. 
 * These include RandomAgent, DefensiveAgent, AgressiveAgent, and BalancedAgent. Two "smarter" agents have been 
 * implemented that attempt to learn the best policy based on their previous results. These agents include 
 * PolicyItrAgent and ValueItrAgent which implement the Policy Iteration and Value Iteration machine learning 
 * algorithms respectively.
 * 
 * Please see README.txt for usage instructions and more details.
 * 
 * Author: 	Toby Waite
 * Contact: toby.waite@gmail.com 
 * Updated: November 16th, 2010.
 */

public class TicTacToe {

	// each match consists of a number of games between two agents.
	private int numGames;
	private Agent opponent;
	private Agent ourAgent;
	
	// Store gameResults for data post-processing
	private int[] gameResults;
	
	// game results
	private int wins;
	private int losses;
	private int ties;
		
	public static void main(String[] args) {

		TicTacToe match = new TicTacToe();

		// If an incorrect number of parameters is entered, print function usage.
		if(args.length !=3){
			printUsage();
			System.exit(1);
		}

		// parse input to determine game parameters and run game.
		try{
			match.setAgent(Integer.parseInt(args[0]));
			match.setNumGames(Integer.parseInt(args[1]));
			match.setOpponent(Integer.parseInt(args[2]));
		}
		catch(NumberFormatException e){
			System.out.println("All parameters must be integers!");
			printUsage();
			System.exit(1);
		}
		catch(ParameterException e){
			System.out.println("Parameter Error: " + e.getMessage());
			printUsage();
			System.exit(1);
		}

		// run the match, then show the results!
		match.run();
		match.computeResults();
		if(match.numGames > 1) // only display match statistics if more than one game is played.
			match.showMatchResults();
	}
	
	private static void printUsage(){
		System.out.println("Usage: java TicTacToe <AgentType> <NumberOfGames> <OpponentType>");
		System.out.println("Please see README for details about parameters and usage examples.");
	}

	// determines the opponent type given the run-time parameters.
	private void setOpponent(int opp) throws ParameterException{		
		switch(opp){
 			case Consts.OpponentRandom: 	opponent = new NaiveAgent(); break;
			case Consts.OpponentDefensive: 	opponent = new DefensiveAgent(); break;
			case Consts.OpponentAggressive: opponent = new AggressiveAgent(); break;
			case Consts.OpponentBalanced: 	opponent = new BalancedAgent(); break;
			case Consts.OpponentHuman:		opponent = new HumanAgent(); break;
			default: throw new ParameterException("Opponent number (" + opp + ") out of range!");
		}
	}
	// sets the number of games as specified in the run-time parameters.
	private void setNumGames(int n) throws ParameterException{
		// make sure the number of games specified is valid.
		if(n < 0){
			throw new ParameterException("Number of games cannot be negative");
		}
		numGames = n;
		// initialize array to store match results
		gameResults = new int[numGames];
	}	
	// set ourAgent based on run-time parameters.
	private void setAgent(int agent) throws ParameterException{
		switch(agent){
			case Consts.AgentRandom:	ourAgent = new NaiveAgent(); break;
			case Consts.AgentValItr: 	ourAgent = new ValueItrAgent(); break;
			case Consts.AgentPolItr: 	ourAgent = new PolicyItrAgent(); break;
			case Consts.AgentHuman:		ourAgent = new HumanAgent(); break;
			default: throw new ParameterException("Agent number out of range!");
		}
	}
	
	// run the match
	private void run(){
		
		// initialize agents
		opponent.initialize(ourAgent);
		ourAgent.initialize(opponent);
		
		// only print game results if we are only running one game in the match.
		boolean printGameResults = (numGames == 1);
		
		// run numGames game instances, and record the results.
		for(int i = 0; i<numGames; i++){
			Game gameInstance = new Game(ourAgent, opponent);
			gameResults[i] = gameInstance.playGame();
						
			if(printGameResults)
				gameInstance.printState();
		}		
	}
	
	// after a set of games in a match is played, tabulate results. 
	private void computeResults() {
		for(int i = 0; i< gameResults.length; i++){
			if 		(gameResults[i] == Consts.GameWon) 		wins++;
			else if (gameResults[i] == Consts.GameLost) 	losses++;
			else if (gameResults[i] == Consts.GameTied) 	ties++;
		}
	}

	// display results to user
	private void showMatchResults(){
		System.out.println("Won: " + wins + ", " + 100*(wins/(double)(wins + losses + ties)) + "%");
		System.out.println("Drawn: " + ties + ", " + 100*(ties/(double)(wins + losses + ties)) + "%");
		System.out.println("Lost: " + losses + ", " + 100*(losses/(double)(wins + losses + ties)) + "%");
	}
}