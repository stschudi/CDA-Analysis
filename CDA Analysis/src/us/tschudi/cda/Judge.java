package us.tschudi.cda;

import java.util.ArrayList;
import java.util.Arrays;

public class Judge {
	private String name;
	private ArrayList<Round> rounds;
	
	public Judge(String n){
		name = n;
		rounds = new ArrayList<Round>();
	}
	public void addRound(Round r){
		rounds.add(r);
	}
	public String getName() {
		return name;
	}
	public ArrayList<Round> getRounds() {
		return rounds;
	}
	public boolean equals(Object o){
		if(!(o instanceof Judge)) return false;
		Judge j = (Judge) o;
		return name.equals(j.getName());
	}
	public String toString(){
		return "Name: \""+name+"\"";
	}
	public boolean wentToDebate(Tournament tournament) {
		for(Round r:rounds){
			if(r.getTournament().equals(tournament)) return true;
		}
		return false;
	}
	public int getNumRounds(){
		return rounds.size();
	}
	public int getNumDebaters(){
		return rounds.size()*4;
	}
	public double getMaleAvgScore(){
		int maleDebaters = 0;
		double maleScore = 0;
		for(Round r:rounds){
			Debater[] aff = r.getAff().getDebaters();
			Debater[] neg = r.getNeg().getDebaters();
			if(aff[0].getGender() == Main.GENDER_MALE){
				maleDebaters ++;
				maleScore += r.getScores()[0];
			}
			if(aff[1].getGender() == Main.GENDER_MALE){
				maleDebaters ++;
				maleScore += r.getScores()[1];
			}
			if(neg[0].getGender() == Main.GENDER_MALE){
				maleDebaters ++;
				maleScore += r.getScores()[2];
			}
			if(neg[1].getGender() == Main.GENDER_MALE){
				maleDebaters ++;
				maleScore += r.getScores()[3];
			}
		}
		return maleScore / (double)maleDebaters;
	}
	public double getFemaleAvgScore(){
		int femaleDebaters = 0;
		double femaleScore = 0;
		for(Round r:rounds){
			Debater[] aff = r.getAff().getDebaters();
			Debater[] neg = r.getNeg().getDebaters();
			if(aff[0].getGender() == Main.GENDER_FEMALE){
				femaleDebaters ++;
				femaleScore += r.getScores()[0];
			}
			if(aff[1].getGender() == Main.GENDER_FEMALE){
				femaleDebaters ++;
				femaleScore += r.getScores()[1];
			}
			if(neg[0].getGender() == Main.GENDER_FEMALE){
				femaleDebaters ++;
				femaleScore += r.getScores()[2];
			}
			if(neg[1].getGender() == Main.GENDER_FEMALE){
				femaleDebaters ++;
				femaleScore += r.getScores()[3];
			}
		}
		return femaleScore / (double)femaleDebaters;
	}
	public int getNumMaleDebaters(){
		int maleDebaters = 0;
		for(Round r:rounds){
			Debater[] aff = r.getAff().getDebaters();
			Debater[] neg = r.getNeg().getDebaters();
			if(aff[0].getGender() == Main.GENDER_MALE){
				maleDebaters ++;
			}
			if(aff[1].getGender() == Main.GENDER_MALE){
				maleDebaters ++;
			}
			if(neg[0].getGender() == Main.GENDER_MALE){
				maleDebaters ++;
			}
			if(neg[1].getGender() == Main.GENDER_MALE){
				maleDebaters ++;
			}
		}
		return maleDebaters;
	}
	public int getNumFemaleDebaters(){
		int femaleDebaters = 0;
		for(Round r:rounds){
			Debater[] aff = r.getAff().getDebaters();
			Debater[] neg = r.getNeg().getDebaters();
			if(aff[0].getGender() == Main.GENDER_FEMALE){
				femaleDebaters ++;
			}
			if(aff[1].getGender() == Main.GENDER_FEMALE){
				femaleDebaters ++;
			}
			if(neg[0].getGender() == Main.GENDER_FEMALE){
				femaleDebaters ++;
			}
			if(neg[1].getGender() == Main.GENDER_FEMALE){
				femaleDebaters ++;
			}
		}
		return femaleDebaters;
	}
	public double getAffWinRate(){
		int affWins = 0;
		for(Round r:rounds){
			if(r.getWinner()==Main.TEAM_AFF){
				affWins++;
			}
		}
		return affWins / (double)rounds.size();
	}
	public double getNegWinRate(){
		int negWins = 0;
		for(Round r:rounds){
			if(r.getWinner()==Main.TEAM_NEG){
				negWins++;
			}
		}
		return negWins / (double)rounds.size();
	}
	public double getAffAvgScore(){
		double affScore = 0;
		for(Round r:rounds){
			affScore += r.getScores()[0]+r.getScores()[1];
		}
		return affScore / (double)(rounds.size() * 2);
	}
	public double getNegAvgScore(){
		double negScore = 0;
		for(Round r:rounds){
			negScore += r.getScores()[2]+r.getScores()[3];
		}
		return negScore / (double)(rounds.size() * 2);
	}
	public int getNumRepeatDebaters(){
		ArrayList<Debater> repeats = new ArrayList<>();
		int repeatDebaters = 0;
		for(Round r:rounds){
			Debater[] aff = r.getAff().getDebaters();
			Debater[] neg = r.getNeg().getDebaters();
			boolean[] add = new boolean[4];
			Arrays.fill(add, true);
			for(Debater d:repeats){
				if(aff[0].equals(d)){
					add[0] = false;
					repeatDebaters++;
				}
				if(aff[1].equals(d)){
					add[1] = false;
					repeatDebaters++;
				}
				if(neg[0].equals(d)){
					add[2] = false;
					repeatDebaters++;
				}
				if(neg[1].equals(d)){
					add[3] = false;
					repeatDebaters++;
				}
			}
			if(add[0]) repeats.add(aff[0]);
			if(add[1]) repeats.add(aff[1]);
			if(add[2]) repeats.add(neg[0]);
			if(add[3]) repeats.add(neg[1]);
		}
		return repeatDebaters;
	}
	public double getRepeatWinRate(){
		ArrayList<Debater> repeats = new ArrayList<>();
		int repeatDebaters = 0;
		int repeatWins = 0;
		for(Round r:rounds){
			Debater[] aff = r.getAff().getDebaters();
			Debater[] neg = r.getNeg().getDebaters();
			boolean[] add = new boolean[4];
			Arrays.fill(add, true);
			for(Debater d:repeats){
				if(aff[0].equals(d)){
					add[0] = false;
					repeatDebaters++;
					if(r.getWinner() == Main.TEAM_AFF) repeatWins++;
				}
				if(aff[1].equals(d)){
					add[1] = false;
					repeatDebaters++;
					if(r.getWinner() == Main.TEAM_AFF) repeatWins++;
				}
				if(neg[0].equals(d)){
					add[2] = false;
					repeatDebaters++;
					if(r.getWinner() == Main.TEAM_NEG) repeatWins++;
				}
				if(neg[1].equals(d)){
					add[3] = false;
					repeatDebaters++;
					if(r.getWinner() == Main.TEAM_NEG) repeatWins++;
				}
			}
			if(add[0]) repeats.add(aff[0]);
			if(add[1]) repeats.add(aff[1]);
			if(add[2]) repeats.add(neg[0]);
			if(add[3]) repeats.add(neg[1]);
		}
		return repeatWins / (double)repeatDebaters;
	}
	public double getRepeatAvgScore(){
		ArrayList<Debater> repeats = new ArrayList<>();
		int repeatDebaters = 0;
		double repeatScore = 0;
		for(Round r:rounds){
			Debater[] aff = r.getAff().getDebaters();
			Debater[] neg = r.getNeg().getDebaters();
			boolean[] add = new boolean[4];
			Arrays.fill(add, true);
			for(Debater d:repeats){
				if(aff[0].equals(d)){
					add[0] = false;
					repeatDebaters++;
					repeatScore += r.getScores()[0];
				}
				if(aff[1].equals(d)){
					add[1] = false;
					repeatDebaters++;
					repeatScore += r.getScores()[1];
				}
				if(neg[0].equals(d)){
					add[2] = false;
					repeatDebaters++;
					repeatScore += r.getScores()[2];
				}
				if(neg[1].equals(d)){
					add[3] = false;
					repeatDebaters++;
					repeatScore += r.getScores()[3];
				}
			}
			if(add[0]) repeats.add(aff[0]);
			if(add[1]) repeats.add(aff[1]);
			if(add[2]) repeats.add(neg[0]);
			if(add[3]) repeats.add(neg[1]);
		}
		return repeatScore / (double)repeatDebaters;
	}
	public double getRightWingAvgScore(){
		int rwDebaters = 0;
		double rwScore = 0;
		for(Round r:rounds){
			Debater[] aff = r.getAff().getDebaters();
			Debater[] neg = r.getNeg().getDebaters();
			if(r.getTournament().getPoliticalPreferenceOfAff()==Main.POLITICAL_LEFT){
				if(neg[0].getGender() == Main.GENDER_MALE){
					rwDebaters ++;
					rwScore += r.getScores()[2];
				}
				if(neg[1].getGender() == Main.GENDER_MALE){
					rwDebaters ++;
					rwScore += r.getScores()[3];
				}
			} else if(r.getTournament().getPoliticalPreferenceOfAff()==Main.POLITICAL_RIGHT){
				if(aff[0].getGender() == Main.GENDER_MALE){
					rwDebaters ++;
					rwScore += r.getScores()[0];
				}
				if(aff[1].getGender() == Main.GENDER_MALE){
					rwDebaters ++;
					rwScore += r.getScores()[1];
				}
			}
		}
		return rwScore / (double)rwDebaters;
	}
	public double getLeftWingAvgScore(){
		int lwDebaters = 0;
		double lwScore = 0;
		for(Round r:rounds){
			Debater[] aff = r.getAff().getDebaters();
			Debater[] neg = r.getNeg().getDebaters();
			if(r.getTournament().getPoliticalPreferenceOfAff()==Main.POLITICAL_LEFT){
				if(aff[0].getGender() == Main.GENDER_MALE){
					lwDebaters ++;
					lwScore += r.getScores()[0];
				}
				if(aff[1].getGender() == Main.GENDER_MALE){
					lwDebaters ++;
					lwScore += r.getScores()[1];
				}
			} else if(r.getTournament().getPoliticalPreferenceOfAff()==Main.POLITICAL_RIGHT){
				if(neg[0].getGender() == Main.GENDER_MALE){
					lwDebaters ++;
					lwScore += r.getScores()[2];
				}
				if(neg[1].getGender() == Main.GENDER_MALE){
					lwDebaters ++;
					lwScore += r.getScores()[3];
				}
			}
		}
		return lwScore / (double)lwDebaters;
	}
	public double getRightWingWinRate(){
		int rwWins = 0;
		int politicalRounds = 0;
		for(Round r:rounds){
			if(r.getTournament().getPoliticalPreferenceOfAff() == Main.POLITICAL_RIGHT){
				if(r.getWinner()==Main.TEAM_AFF){
					rwWins++;
				}
				politicalRounds++;
			} else if(r.getTournament().getPoliticalPreferenceOfAff() == Main.POLITICAL_LEFT){
				if(r.getWinner()==Main.TEAM_NEG){
					rwWins++;
				}
				politicalRounds++;
			}
		}
		return (double)rwWins / (double)politicalRounds;
	}
	public double getLeftWingWinRate(){
		int lwWins = 0;
		int politicalRounds = 0;
		for(Round r:rounds){
			if(r.getTournament().getPoliticalPreferenceOfAff() == Main.POLITICAL_LEFT){
				if(r.getWinner()==Main.TEAM_AFF){
					lwWins++;
				}
				politicalRounds++;
			} else if(r.getTournament().getPoliticalPreferenceOfAff() == Main.POLITICAL_RIGHT){
				if(r.getWinner()==Main.TEAM_NEG){
					lwWins++;
				}
				politicalRounds++;
			}
		}
		return (double)lwWins / (double)politicalRounds;
	}
	public double getAverageScore() {
		double score = 0;
		for(Round r:rounds){
			double[] scores = r.getScores();
			score+=scores[0]+scores[1]+scores[2]+scores[3];
		}
		return score / ((double) rounds.size() * 4.0) ;
	}
	public String getSideWinPreference() {
		double numRounds;
		if((numRounds = getNumRounds()) < 20) return "Not enough data";
		double z = (getAffWinRate()  - .5) / Math.sqrt(.25/numRounds); 
		if(z > 1.960) return "Prefers Aff";
		if(z < -1.960) return "Prefers Neg";
		return "No Preference";
	}
	public String getSideScorePreference() {
		if(getNumRounds() < 5) return "Not enough data";
		double avgAff = getAffAvgScore(), avgNeg = getNegAvgScore();
		double varianceAff = 0, varianceNeg = 0;
		for(Round r:rounds){
			varianceAff += Math.pow(r.getScores()[0]-avgAff,2);
			varianceAff += Math.pow(r.getScores()[1]-avgAff,2);
			varianceNeg += Math.pow(r.getScores()[2]-avgNeg,2);
			varianceNeg += Math.pow(r.getScores()[3]-avgNeg,2);
		}
		varianceAff /= (2*rounds.size());
		varianceNeg /= (2*rounds.size());
		
		double t = (avgAff - avgNeg) / Math.sqrt((varianceAff + varianceNeg) / (2 * rounds.size()));
		int df = 4*rounds.size() - 2;
		double crit = Main.ttest(df);
		if(t > crit) return "Prefers Aff";
		if(t < -1 * crit) return "Prefers Neg";
		return "No Preference";
	}
	public String getGenderScorePreference() {
		if(getNumRounds() < 5) return "Not enough data";
		double avgMale = getMaleAvgScore(), avgFemale = getFemaleAvgScore();
		double varianceMale = 0, varianceFemale = 0;
		double nMale = 0, nFemale = 0;
		for(Round r:rounds){
			Debater[] aff = r.getAff().getDebaters();
			Debater[] neg = r.getNeg().getDebaters();
			if(aff[0].getGender() == Main.GENDER_MALE){
				nMale ++;
				varianceMale += Math.pow(r.getScores()[0]-avgMale,2);
			} else if(aff[0].getGender() == Main.GENDER_FEMALE) {
				nFemale ++;
				varianceFemale += Math.pow(r.getScores()[0]-avgFemale,2);
			}
			if(aff[1].getGender() == Main.GENDER_MALE){
				nMale ++;
				varianceMale += Math.pow(r.getScores()[1]-avgMale,2);
			} else if(aff[1].getGender() == Main.GENDER_FEMALE) {
				nFemale ++;
				varianceFemale += Math.pow(r.getScores()[1]-avgFemale,2);
			}
			if(neg[0].getGender() == Main.GENDER_MALE){
				nMale ++;
				varianceMale += Math.pow(r.getScores()[2]-avgMale,2);
			} else if(neg[0].getGender() == Main.GENDER_FEMALE) {
				nFemale ++;
				varianceFemale += Math.pow(r.getScores()[2]-avgFemale,2);
			}
			if(neg[1].getGender() == Main.GENDER_MALE){
				nMale ++;
				varianceMale += Math.pow(r.getScores()[3]-avgMale,2);
			} else if(neg[1].getGender() == Main.GENDER_FEMALE) {
				nFemale ++;
				varianceFemale += Math.pow(r.getScores()[3]-avgFemale,2);
			}
		}
		if(nMale == 0 && nFemale == 0) return "Not enough data";
		varianceMale /= nMale;
		varianceFemale /= nFemale;
		double df = nMale + nFemale - 2;
		double t = (avgMale - avgFemale) / Math.sqrt(((varianceMale * (nMale - 1) + varianceFemale * (nFemale - 1)) / df) * ((1 / nMale) + (1 / nFemale)));
		double crit = Main.ttest((int) df);
		if(t > crit) return "Prefers Male";
		if(t < -1 * crit) return "Prefers Female";
		return "No Preference";
	}
	public String getPoliticalWinPreference() {
		double numRounds;
		if((numRounds = getNumRounds()) < 20) return "Not enough data";
		double z = (getLeftWingWinRate()  - .5) / Math.sqrt(.25/numRounds); 
		if(z > 1.645) return "Prefers Left Wing";
		if(z < -1.645) return "Prefers Right Wing";
		return "No Preference";
	}
	public String getPoliticalScorePreference() {
		if(getNumRounds() < 5) return "Not enough data";
		double avgRW = getRightWingAvgScore(), avgLW = getLeftWingAvgScore();
		double varianceRW = 0, varianceLW = 0;
		double numRounds = 0;
		for(Round r:rounds){
			if(r.getTournament().getPoliticalPreferenceOfAff() == Main.POLITICAL_LEFT){
				varianceLW += Math.pow(r.getScores()[0]-avgLW,2);
				varianceLW += Math.pow(r.getScores()[1]-avgLW,2);
				varianceRW += Math.pow(r.getScores()[2]-avgRW,2);
				varianceRW += Math.pow(r.getScores()[3]-avgRW,2);
				numRounds += 2;
			} else if(r.getTournament().getPoliticalPreferenceOfAff() == Main.POLITICAL_RIGHT){
				varianceRW += Math.pow(r.getScores()[0]-avgRW,2);
				varianceRW += Math.pow(r.getScores()[1]-avgRW,2);
				varianceLW += Math.pow(r.getScores()[2]-avgLW,2);
				varianceLW += Math.pow(r.getScores()[3]-avgLW,2);
				numRounds += 2;
			}
		}
		if(numRounds < 2) return "Not enough data";
		varianceLW /= numRounds;
		varianceRW /= numRounds;
		double t = (avgLW - avgRW) / Math.sqrt((varianceLW + varianceRW) / numRounds);
		int df = (int) (2 * numRounds - 2);
		double crit = Main.ttest(df);
		if(t > crit) return "Prefers Left Wing";
		if(t < -1 * crit) return "Prefers Right Wing";
		return "No Preference";
	}
}
