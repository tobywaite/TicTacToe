/* 
 * 	Tic Tac Toe ÐÊA simple machine learning simulator.
 * 
 * This program allows two agents to play each other in a game of Tic Tac Toe. 
 * A number of naive agents have been implemented that perform a mix of random and simple rule based policies. 
 * These include RandomAgent, DefensiveAgent, AgressiveAgent, and BalancedAgent. Two "smarter" agents have been 
 * implemented that attempt to learn the best policy based on their previous results. These agents include 
 * PolicyItrAgent and ValueItrAgent which implement the Policy Iteration and Value Iteration machine learning 
 * algorithms respectively.
 * 
 * Author: 	Toby Waite
 * Contact: toby.waite@gmail.com 
 * Date: 	November 5th, 2010.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class TicTacToe {

	private int numGames;
	private Agent opponent;
	private Agent ourAgent;
	
	private int[] matchResults;
	
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

		boolean keepRunning = true;

		while(keepRunning){

			match.run();
			match.computeResults();
			match.showResults();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

			System.out.println("Enter new opponent number, or 'exit'");
			System.out.print("Input: ");
			try {
				String input = br.readLine();
				if (input == "exit"){
					keepRunning = false;
				}
				else{
					match.setOpponent(Integer.parseInt(input));
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NumberFormatException e){
				System.out.println("Bad input! Try again.");
			} catch (ParameterException e) {
				System.out.println("Invalid Opponent! Try again.");
			}
		}
	}
	
	private static void printUsage(){
		System.out.println("Usage: java TicTacToe <AgentType> <NumberOfGames> <OpponentType>");
		System.out.println("Please see README for details about parameters and usage examples.");
	}

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
	
	private void setNumGames(int n) throws ParameterException{
		if(n < 0){
			throw new ParameterException("Number of games cannot be negative");
		}
		numGames = n;
		// initialize array to store match results
		matchResults = new int[numGames];
	}	
	
	private void setAgent(int agent) throws ParameterException{
		switch(agent){
			case Consts.AgentRandom:	ourAgent = new NaiveAgent(); break;
			case Consts.AgentValItr: 	ourAgent = new ValueItrAgent(); break;
			case Consts.AgentPolItr: 	ourAgent = new PolicyItrAgent(); break;
			case Consts.AgentHuman:		ourAgent = new HumanAgent(); break;
			default: throw new ParameterException("Agent number out of range!");
		}
	}
	
	protected void run(){
		
		// only print game results if we are only running one game in the match.
		boolean printGameResults = (numGames == 1);
		
		// run numGames game instances, and record the results.
		for(int i = 0; i<numGames; i++){
			Game gameInstance = new Game(ourAgent, opponent);
			matchResults[i] = gameInstance.playGame();
						
			if(printGameResults)
				gameInstance.printState();
		}		
	}
	
	private void computeResults() {
		for(int i = 0; i< matchResults.length; i++){
			if 		(matchResults[i] == Consts.GameWon) 	wins++;
			else if (matchResults[i] == Consts.GameLost) 	losses++;
			else if (matchResults[i] == Consts.GameTied) 	ties++;
		}
	}

	private void showResults(){
		System.out.println("Won: " + wins + ", " + (wins/(double)(wins + losses + ties)) + "%");
		System.out.println("Drawn: " + ties + ", " + (ties/(double)(wins + losses + ties)) + "%");
		System.out.println("Lost: " + losses + ", " + (losses/(double)(wins + losses + ties)) + "%");
	}
}