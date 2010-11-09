import java.util.Random;

public abstract class Agent {

	protected int team;
	protected static Random r;

	public void setTeamX() {
		team = Consts.TeamX;
	}

	public void setTeamO() {
		team = Consts.TeamO;		
	}
	
	public int getTeam() {
		return team;
	}
	
	abstract int pickMove(Game game);

	public abstract void reportAction(Game currentGame, Game lastTurn);

}