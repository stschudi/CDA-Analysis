package us.tschudi.cda;

import java.util.ArrayList;

public class Data {
	
	private ArrayList<Debater> debaters;
	private ArrayList<Round> rounds;
	private ArrayList<Judge> judges;
	private ArrayList<Tournament> tournaments;

	public Data(){
		debaters = new ArrayList<>();
		rounds = new ArrayList<>();
		judges = new ArrayList<>();
		tournaments = new ArrayList<>();
	}
	public Debater getDebaterByName(String name){
		for(Debater d:debaters){
			if(d.getName().equals(name)) return d;
		}
		return null;
	}
	public Judge getJudgeByName(String name){
		for(Judge j:judges){
			if(j.getName().equals(name)) return j;
		}
		return null;
	}
	public Round getRoundByTournamentJudgeNumber(Tournament t, Judge j, int number){
		for(Round r:rounds){
			if(r.getTournament().equals(t) && r.getJudge().equals(j) && r.getNumber() == number) return r;
		}
		return null;
	}
	public ArrayList<Debater> getDebaters() {
		return debaters;
	}
	public ArrayList<Debater> getDebatersThatWentTo(Tournament tournament) {
		ArrayList<Debater> subList = new ArrayList<>();
		for(Debater d:debaters){
			if(d.wentToDebate(tournament)){
				subList.add(d);
			}
		}
		return subList;
	}
	public ArrayList<Debater> getDebatersThatWentTo(Tournament[] tournaments) {
		ArrayList<Debater> subList = new ArrayList<>();
		for(Debater d:debaters){
			for(Tournament t:tournaments){
				if(d.wentToDebate(t)){
					subList.add(d);
					break;
				}
			}
		}
		return subList;
	}
	public ArrayList<Judge> getJudgesThatJudgedAt(Tournament[] tournaments) {
		ArrayList<Judge> subList = new ArrayList<>();
		for(Judge j:judges){
			for(Tournament t:tournaments){
				if(j.wentToDebate(t)){
					subList.add(j);
					break;
				}
			}
		}
		return subList;
	}
	public ArrayList<School> getSchoolsThatWentTo(Tournament[] tournaments) {
		ArrayList<School> subList = new ArrayList<>();
		for(Debater d:debaters){
			for(Tournament t:tournaments){
				if(d.wentToDebate(t)){
					boolean found = false;
					if(subList.size() > 0){
						for(School s:subList){
							if(s.getName().equalsIgnoreCase(d.getSchool())){
								s.addDebater(d);
								found = true;
								break;
							}
						}
					}
					if(!found){
						School s = new School(d.getSchool());
						s.addDebater(d);
						subList.add(s);
					}
				}
			}
		}
		return subList;
	}
	public ArrayList<Round> getRounds() {
		return rounds;
	}
	public ArrayList<Judge> getJudges() {
		return judges;
	}
	public ArrayList<Tournament> getTournaments() {
		return tournaments;
	}
	public void addRound(Round r) {
		rounds.add(r);
	}
	public void addDebater(Debater d) {
		debaters.add(d);
	}
	public void addJudge(Judge j) {
		judges.add(j);
	}
	public void addTournament(Tournament t) {
		tournaments.add(t);
	}

}
