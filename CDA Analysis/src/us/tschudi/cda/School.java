package us.tschudi.cda;

import java.util.ArrayList;

public class School {
	private String name;
	private ArrayList<Debater> debaters;
	public School(String n){
		name = n;
		debaters = new ArrayList<>();
	}
	public String getName() {
		return name;
	}
	public ArrayList<Debater> getDebaters() {
		return debaters;
	}
	public void addDebater(Debater d) {
		debaters.add(d);
	}
	public int getNumDebates() {
		int total = 0;
		for(Debater d:debaters){
			total+=d.getNumDebates();
		}
		return total;
	}
	public int getTotalRounds() {
		int total = 0;
		for(Debater d:debaters){
			total+=d.getTotalRounds();
		}
		return total;
	}
	public double getTotalScore() {
		double total = 0;
		for(Debater d:debaters){
			total+=d.getTotalScore();
		}
		return total;
	}
	public double getTotalRank() {
		double total = 0;
		for(Debater d:debaters){
			total+=d.getTotalRank();
		}
		return total;
	}
	public int getTotalWins() {
		int total = 0;
		for(Debater d:debaters){
			total+=d.getTotalWins();
		}
		return total;
	}
	public double getAverageScore(){
		if(getTotalRounds() == 0) return 0;
		return getTotalScore()/(double)(getNumDebates()*3d);
	}
	public double getAverageRank(){
		if(getTotalRounds() == 0) return 0;
		return getTotalRank()/(double)(getNumDebates()*3d);
	}
	public double getWinRatio(){
		int rounds;
		if((rounds = getTotalRounds()) == 0) return 0;
		return (double)getTotalWins()/(double)rounds;
	}
	public int getAffRounds() {
		int total = 0;
		for(Debater d:debaters){
			total+=d.getAffRounds();
		}
		return total;
	}
	public int getNegRounds() {
		int total = 0;
		for(Debater d:debaters){
			total+=d.getNegRounds();
		}
		return total;
	}
	public int getAffWins() {
		int total = 0;
		for(Debater d:debaters){
			total+=d.getAffWins();
		}
		return total;
	}
	public int getNegWins() {
		int total = 0;
		for(Debater d:debaters){
			total+=d.getNegWins();
		}
		return total;
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
}
