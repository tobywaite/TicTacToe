import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class HumanAgent extends Agent {

	@Override
	int pickMove(Game game) {
		game.printState();
		String teamStr = (team == Consts.TeamX) ? "X" : "O";
		System.out.println("You are playing " + teamStr + ".");
		System.out.println("Where would you like to move? Select an empty space based on the following diagram:");
		System.out.println("0|1|2\n" +
				           "3|4|5\n" +
				           "6|7|8");
		System.out.print("My move:");
		
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		int move = Consts.NoMove;
		try{
			move = Integer.parseInt(br.readLine());
		} catch(IOException e){
			e.printStackTrace();
			System.exit(1);
		} catch(NumberFormatException e){
			System.out.println("Invalid move selection! You must enter an integer!");
			return pickMove(game);
		}
		return move;
		
	}

	@Override
	void reportAction(Game game) {
		// TODO Auto-generated method stub

	}
}