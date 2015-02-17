package us.tschudi.cda;

public class DebateRound {
	private Round round;
	private byte side;
	
	public DebateRound(Round r, byte s){
		round = r;
		side = s;
	}
	public Round getRound() {
		return round;
	}
	public byte getSide() {
		return side;
	}
	public Team getTeam(){
		if(side == Main.TEAM_BYE) return null;
		return side==Main.TEAM_AFF?round.getAff():round.getNeg();
	}
	public Team getOpponent() {
		if(side == Main.TEAM_BYE) return null;
		return side==Main.TEAM_NEG?round.getAff():round.getNeg();
	}
	public boolean isWon(){
		return side == round.getWinner();
	}
	public String toString(){
		return "{"+round.getTournament().getName()+"}: Team: "+getTeam()+" Opponent: "+getOpponent()+" Won? "+isWon();
	}
}
