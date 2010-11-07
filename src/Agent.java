public abstract class Agent {

	protected int team;
	
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

	abstract void reportAction(Game game);

}