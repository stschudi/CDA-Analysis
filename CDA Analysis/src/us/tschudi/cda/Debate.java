package us.tschudi.cda;

public class Debate {
	private Tournament tournament;
	private DebateRound[] debateRounds;
	public Debate(Tournament t, DebateRound[] r){
		debateRounds = r;
		tournament = t;
	}
	public Tournament getTournament() {
		return tournament;
	}
	public Debater getPartner(Debater d){
		Team team = debateRounds[0].getTeam();
		if(team == null){
			team = debateRounds[1].getTeam();
		}
		if(team == null){
			team = debateRounds[2].getTeam();
		}
		Debater[] teamDebaters = team.getDebaters();
		if(teamDebaters[0].equals(d)){
			return teamDebaters[1];
		}
		return teamDebaters[0];
	}
	public Debater[] getOpponents(){
		Debater[] opponents = new Debater[6];
		for(int i=0;i<3;i++){
			Team opp = debateRounds[i].getOpponent();
			if(opp == null) continue;
			Debater[] opps = opp.getDebaters();
			opponents[2 * i] = opps[0];
			opponents[2 * i + 1] = opps[1];
		}
		return opponents;
	}
	public boolean[] getResults(){
		boolean[] results = new boolean[3];
		for(int i=0;i<3;i++){
			if(debateRounds[i].getSide() == Main.TEAM_BYE) continue;
			results[i] = debateRounds[i].isWon();
		}
		return results;
	}
	public DebateRound[] getDebateRounds() {
		return debateRounds;
	}
	public String toString(){
		return "Tournament: \""+tournament+"\"";
	}
}
