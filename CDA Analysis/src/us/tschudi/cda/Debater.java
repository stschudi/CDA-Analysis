package us.tschudi.cda;

import java.util.ArrayList;

public class Debater {
	private String name, school;
	private byte gender;
	private ArrayList<Debate> debates;
	private ArrayList<Ranking> rankings;
	
	public Debater(String n, String s, byte g){
		name = n;
		school = s;
		gender = g;
		debates = new ArrayList<>();
		rankings = new ArrayList<>();
	}
	public byte getGender() {
		return gender;
	}
	public void setGender(byte gender) {
		this.gender = gender;
	}
	public void setSchool(String school) {
		this.school = school;
	}
	public String getName() {
		return name;
	}
	public String getSchool() {
		return school;
	}
	public int getNumDebates(){
		return getDebates().size();
	}
	public boolean wentToDebate(Tournament tournament){
		for(Debate d:debates){
			if(d.getTournament().equals(tournament)) return true;
		}
		return false;
	}
	public Debate getDebateByEventName(Tournament tournament){
		for(Debate d:debates){
			if(d.getTournament().equals(tournament)){
				return d;
			}
		}
		return null;
	}
	public ArrayList<Debate> getDebates() {
		return debates;
	}
	public void addDebate(Debate d){
		debates.add(d);
	}
	public void updateRanking(Tournament tournament){
		Debate d = getDebateByEventName(tournament);
		Ranking last = getRankingOfTourneyBefore(tournament);
		Debater[] opponents = d.getOpponents();
		Ranking[] oppRankings = new Ranking[6];
		for(int i=0;i<opponents.length;i++){
			if(opponents[i] == null) continue;
			oppRankings[i] = opponents[i].getRankingOfTourneyBefore(tournament);
		}
		rankings.add(last.calculateNewScore(d.getTournament(), oppRankings, d.getResults(), d.getPartner(this).getRankingOfTourneyBefore(tournament)));
	}
	public Ranking getRankingOfTourneyBefore(Tournament t) {
		Ranking lastTourney = new Ranking();
		for(Ranking r:rankings){
			if(r.getTournament().getTime() < t.getTime()){
				lastTourney = r;
			} else {
				break;
			}
		}
		return lastTourney;
	}
	public ArrayList<Ranking> getRankings() {
		return rankings;
	}
	public Ranking getNewestRanking() {
		if(rankings.size() == 0){
			return new Ranking();
		}
		return rankings.get(rankings.size() - 1);
	}
	public void clearRankings(){
		rankings.clear();
	}
	public double getEloScore(){
		return getNewestRanking().getEloScore();
	}
	public double getGlickoScore(){
		return getNewestRanking().getGlickoScore();
	}
	public double getGlickoRD(){
		return getNewestRanking().getGlickoRD();
	}
	public double getTeamEloScore(){
		return getNewestRanking().getTeamEloScore();
	}
	public double getTeamGlickoScore(){
		return getNewestRanking().getTeamGlickoScore();
	}
	public double getTeamGlickoRD(){
		return getNewestRanking().getTeamGlickoRD();
	}
	public boolean equals(Object o){
		if(!(o instanceof Debater)) return false;
		Debater d = (Debater) o;
		return name.equals(d.getName()) && school.equals(d.getSchool());
	}
	public String toString(){
		return "Name: \""+name+"\" School: \""+school+"\"";
	}
	public String getGenderString() {
		if(gender == 1){
			return "Male";
		} else if(gender == 0){
			return "Female";
		}
		return "Unknown";
	}
	public double getTotalScore(){
		double score = 0;
		for(Debate d:getDebates()){
			boolean bye = false;
			double roundScore = 0;
			for(DebateRound dR:d.getDebateRounds()){
				if(dR.getSide() == Main.TEAM_AFF){
					Round r = dR.getRound();
					byte dID = 0;
					Team t = r.getAff();
					if(t.getDebaters()[1].getName().equals(name)){
						dID ++;
					}
					roundScore += r.getScores()[dID];
				} else if(dR.getSide() == Main.TEAM_NEG) {
					Round r = dR.getRound();
					byte dID = 2;
					Team t = r.getNeg();
					if(t.getDebaters()[1].getName().equals(name)){
						dID ++;
					}
					roundScore += r.getScores()[dID];
				} else {
					bye = true;
				}
			}
			score += bye?(roundScore*3d/2d):roundScore;
		}
		return score;
	}
	public double getTotalRank(){
		double rank = 0;
		for(Debate d:getDebates()){
			boolean bye = false;
			double roundRank = 0;
			for(DebateRound dR:d.getDebateRounds()){
				if(dR.getSide() == Main.TEAM_AFF){
					Round r = dR.getRound();
					byte dID = 0;
					Team t = r.getAff();
					if(t.getDebaters()[1].getName().equals(name)){
						dID ++;
					}
					roundRank += r.getRanks()[dID];
				} else if(dR.getSide() == Main.TEAM_NEG) {
					Round r = dR.getRound();
					byte dID = 2;
					Team t = r.getNeg();
					if(t.getDebaters()[1].getName().equals(name)){
						dID ++;
					}
					roundRank += r.getRanks()[dID];
				} else {
					bye = true;
				}
				rank += bye?(roundRank*3d/2d):roundRank;
			}
		}
		return rank;
	}
	public double getAverageScore(){
		if(getNumDebates() == 0) return 0;
		return getTotalScore()/((double)getNumDebates()*3d);
	}
	public double getAverageRank(){
		if(getNumDebates() == 0) return 0;
		return getTotalRank()/((double)getNumDebates()*3d);
	}
	public int getTotalRounds(){
		int rounds = 0;
		for(Debate d:getDebates()){
			for(DebateRound dR:d.getDebateRounds()){
				if(dR.getSide() != Main.TEAM_BYE){
					rounds++;
				}
			}
		}
		return rounds;
	}
	public int getTotalWins(){
		int wins = 0;
		for(Debate d:getDebates()){
			for(DebateRound dR:d.getDebateRounds()){
				if(dR.getSide() != Main.TEAM_BYE && dR.isWon()){
					wins++;
				}
			}
		}
		return wins;
	}
	public double getWinRatio(){
		int rounds;
		if((rounds = getTotalRounds()) == 0) return 0;
		return (double)getTotalWins()/(double)rounds;
	}
	public int getAffWins(){
		int wins = 0;
		for(Debate d:getDebates()){
			for(DebateRound dR:d.getDebateRounds()){
				if(dR.getSide() == Main.TEAM_AFF && dR.getRound().getWinner() == Main.TEAM_AFF){
					wins++;
				}
			}
		}
		return wins;
	}
	public int getAffRounds(){
		int rounds = 0;
		for(Debate d:getDebates()){
			for(DebateRound dR:d.getDebateRounds()){
				if(dR.getSide() == Main.TEAM_AFF){
					rounds++;
				}
			}
		}
		return rounds;
	}
	public int getNegWins(){
		int wins = 0;
		for(Debate d:getDebates()){
			for(DebateRound dR:d.getDebateRounds()){
				if(dR.getSide() == Main.TEAM_NEG && dR.getRound().getWinner() == Main.TEAM_NEG){
					wins++;
				}
			}
		}
		return wins;
	}
	public int getNegRounds(){
		int rounds = 0;
		for(Debate d:getDebates()){
			for(DebateRound dR:d.getDebateRounds()){
				if(dR.getSide() == Main.TEAM_NEG){
					rounds++;
				}
			}
		}
		return rounds;
	}
	public String getPreferredSide(){
		int affRounds = getAffRounds();
		int negRounds = getNegRounds();
		if(affRounds > negRounds){
			return "Aff";
		} else if(negRounds > affRounds){
			return "Neg";
		} else {
			return "Equal";
		}
	}
	public double getAffWinRatio(){
		double affRounds = getAffRounds();
		double affWins = getAffWins();
		if(affRounds == 0){
			return 0;
		}
		return affWins/affRounds;
	}
	public double getNegWinRatio(){
		double negRounds = getNegRounds();
		double negWins = getNegWins();
		if(negRounds == 0){
			return 0;
		}
		return negWins/negRounds;
	}
	public String getBetterSide(){
		double affWinRatio = getAffWinRatio();
		double negWinRatio = getNegWinRatio();
		if(affWinRatio > negWinRatio){
			return "Aff";
		} else if(negWinRatio > affWinRatio) {
			return "Neg";
		} else {
			return "Equal";
		}
	}
	public String getMostCommonPartner(){
		class Partner{
			private Debater partner;
			private int numDebates;
			public Partner(Debater p){
				partner = p;
				numDebates = 1;
			}
			public int getDebates() {
				return numDebates;
			}
			public void incrementDebates() {
				numDebates++;
			}
			public Debater getPartner() {
				return partner;
			}
			public boolean equals(Object o){
				if(!(o instanceof Debater)) return false;
				return partner.equals(o);
			}
			public String toString(){
				return "Debater: ("+partner+") Rounds: "+numDebates;
			}
		}
		ArrayList<Partner> partners = new ArrayList<>();
		for(Debate d:getDebates()){
			DebateRound[] dRounds = d.getDebateRounds();
			DebateRound dRound = dRounds[0];
			if(dRound.getSide() == Main.TEAM_BYE){
				dRound = dRounds[1];
			}
			Debater[] t = dRound.getTeam().getDebaters();
			Debater partner = t[0].equals(this)?t[1]:t[0];
			boolean found = false;
			for(Partner p:partners){
				if(p.equals(partner)){
					p.incrementDebates();
					found = true;
					break;
				}
			}
			if(!found){
				partners.add(new Partner(partner));
			}
		}
		int max = -1;
		Partner mostCommon = null;
		boolean multiple = false;
		for(Partner p:partners){
			if(p.getDebates()>max){
				multiple = false;
				mostCommon = p;
				max = p.getDebates();
			} else if(p.getDebates() == max){
				multiple = true;
			}
		}
		if(multiple){
			return "Multiple Common Partners";
		} else {
			if(mostCommon == null) return "";
			return mostCommon.getPartner().getName();
		}
	}
	class Opponent{
		private Debater opponent;
		private int rounds;
		private int wins;
		public Opponent(Debater o, boolean won){
			opponent = o;
			rounds = 1;
			wins = won?1:0;
		}
		public int getRounds() {
			return rounds;
		}
		public void incrementRounds() {
			rounds++;
		}
		public void incrementWins() {
			wins++;
		}
		public Debater getOpponent() {
			return opponent;
		}
		public boolean equals(Object o){
			if(!(o instanceof Debater)) return false;
			return opponent.equals(o);
		}
		public double getWinRate() {
			return (double)wins/(double)rounds;
		}
		public String toString(){
			return "Debater: ("+opponent+") Rounds: "+rounds+" Wins: "+wins;
		}
	}
	public String getMostCommonOpponent(){
		ArrayList<Opponent> opponents = new ArrayList<>();
		for(Debate d:getDebates()){
			for(DebateRound dR:d.getDebateRounds()){
				if(dR.getSide() == Main.TEAM_BYE){
					continue;
				}
				Debater[] opponent = dR.getOpponent().getDebaters();
				boolean found0 = false, found1 = false;
				for(Opponent o:opponents){
					if(o.equals(opponent[0])){
						o.incrementRounds();
						if(dR.isWon()){
							o.incrementWins();
						}
						found0 = true;
					}
					if(o.equals(opponent[1])){
						o.incrementRounds();
						if(dR.isWon()){
							o.incrementWins();
						}
						found1 = true;
					}
				}
				if(!found0){
					opponents.add(new Opponent(opponent[1], dR.isWon()));
				}
				if(!found1){
					opponents.add(new Opponent(opponent[0], dR.isWon()));
				}
			}
		}
		int maxRounds = -1;
		Opponent[] mostCommon = new Opponent[2];
		int multipleRounds = 0;
		for(Opponent o:opponents){
			if(o.getRounds()>maxRounds){
				multipleRounds = 1;
				mostCommon[0] = o;
				maxRounds = o.getRounds();
			} else if(o.getRounds() == maxRounds){
				multipleRounds++;
				mostCommon[1] = o;
			}			
		}
		if(multipleRounds > 2){
			return "Multiple Common Opponents";
		} else if(multipleRounds == 0){
			return "";
		} else if(multipleRounds == 1) {
			return mostCommon[0].getOpponent().getName();
		} else {
			return mostCommon[0].getOpponent().getName()+" & "+mostCommon[1].getOpponent().getName();
		}
	}
	public String getMostDefeatedOpponent(){
		ArrayList<Opponent> opponents = new ArrayList<>();
		for(Debate d:getDebates()){
			for(DebateRound dR:d.getDebateRounds()){
				if(dR.getSide() == Main.TEAM_BYE){
					continue;
				}
				Debater[] opponent = dR.getOpponent().getDebaters();
				boolean found0 = false, found1 = false;
				for(Opponent o:opponents){
					if(o.equals(opponent[0])){
						o.incrementRounds();
						if(dR.isWon()){
							o.incrementWins();
						}
						found0 = true;
					}
					if(o.equals(opponent[1])){
						o.incrementRounds();
						if(dR.isWon()){
							o.incrementWins();
						}
						found1 = true;
					}
				}
				if(!found0){
					opponents.add(new Opponent(opponent[1], dR.isWon()));
				}
				if(!found1){
					opponents.add(new Opponent(opponent[0], dR.isWon()));
				}
			}
		}
		double maxRate = -1;
		Opponent[] mostRate = new Opponent[2];
		int multipleRate = 0;
		for(Opponent o:opponents){
			if(o.getWinRate()>maxRate){
				multipleRate = 1;
				mostRate[0] = o;
				maxRate = o.getWinRate();
			} else if(o.getWinRate() == maxRate){
				multipleRate++;
				mostRate[1] = o;
			}			
		}
		if(multipleRate > 2){
			return "Multiple Defeated Opponents";
		} else if(multipleRate == 0){
			return "";
		} else if(multipleRate == 1) {
			return mostRate[0].getOpponent().getName();
		} else {
			return mostRate[0].getOpponent().getName()+" & "+mostRate[1].getOpponent().getName();
		}
	}
	public String getMostDefeatedByOpponent(){
		ArrayList<Opponent> opponents = new ArrayList<>();
		for(Debate d:getDebates()){
			for(DebateRound dR:d.getDebateRounds()){
				if(dR.getSide() == Main.TEAM_BYE){
					continue;
				}
				Debater[] opponent = dR.getOpponent().getDebaters();
				boolean found0 = false, found1 = false;
				for(Opponent o:opponents){
					if(o.equals(opponent[0])){
						o.incrementRounds();
						if(dR.isWon()){
							o.incrementWins();
						}
						found0 = true;
					}
					if(o.equals(opponent[1])){
						o.incrementRounds();
						if(dR.isWon()){
							o.incrementWins();
						}
						found1 = true;
					}
				}
				if(!found0){
					opponents.add(new Opponent(opponent[1], dR.isWon()));
				}
				if(!found1){
					opponents.add(new Opponent(opponent[0], dR.isWon()));
				}
			}
		}
		double minRate = 1000;
		Opponent[] mostRate = new Opponent[2];
		int multipleRate = 0;
		for(Opponent o:opponents){
			if(o.getWinRate()<minRate){
				multipleRate = 1;
				mostRate[0] = o;
				minRate = o.getWinRate();
			} else if(o.getWinRate() == minRate){
				multipleRate++;
				mostRate[1] = o;
			}			
		}
		if(multipleRate > 2){
			return "Multiple Defeated By Opponents";
		} else if(multipleRate == 0){
			return "";
		} else if(multipleRate == 1) {
			return mostRate[0].getOpponent().getName();
		} else {
			return mostRate[0].getOpponent().getName()+" & "+mostRate[1].getOpponent().getName();
		}
	}
}
