package us.tschudi.cda;

import java.text.DecimalFormat;

public class Ranking {
	private Tournament tournament;
	private double eloScore;
	private double glickoScore, glickoRD;
	private double teamEloScore;
	private double teamGlickoScore, teamGlickoRD;
	private byte depth;
	private static final double GLICKO_C_SQUARED = Math.sqrt((350*350 - 50*50)/365);
	private static final double GLICKO_Q = Math.log(10)/400;
	
	public Ranking(){
		tournament = new Tournament("Null Tourney", 0, "Null", (byte) 1);
		depth = 0;
		eloScore = 1500;
		glickoScore = 1500;
		glickoRD = 350;
		teamEloScore = 1500;
		teamGlickoScore = 1500;
		teamGlickoRD = 350;
	}
	public Ranking(Tournament t, double eS, double gS, double gRD, double tES, double tGS, double tGRD, byte d){
		tournament = t;
		eloScore = eS;
		glickoScore = gS;
		glickoRD = gRD;
		teamEloScore = tES;
		teamGlickoScore = tGS;
		teamGlickoRD = tGRD;
		depth = d;
	}
	public Ranking calculateNewScore(Tournament t, Ranking[] oppRankings, boolean[] results, Ranking partner){
		double summation = 0;
		for(int i=0;i<6;i++){
			if(oppRankings[i] == null) continue;
			summation += (results[i/2]?1:0) - eElo(oppRankings[i]);
		}
		double eS = eloScore + kElo(eloScore)*(summation);
		double time = t.getTime() - tournament.getTime();
		double gRD = Math.min(350, Math.sqrt(sq(glickoRD) + GLICKO_C_SQUARED * time));
		summation = 0;
		for(int i=0;i<6;i++){
			if(oppRankings[i] == null) continue;
			summation += gGlicko(oppRankings[i]) * ((results[i/2]?1:0) - eGlicko(oppRankings[i]));
		}
		double dsq;
		double gS = glickoScore + GLICKO_Q * summation / ((1 / sq(gRD)) + (dsq = dminus2(oppRankings)));
		gRD = Math.pow((1/gRD) + dsq, -.5);
		
		//Team scores
		double teamsummation = 0;
		for(int i=0;i<3;i++){
			if(oppRankings[2 * i] == null) continue;
			teamsummation += (results[i]?1:0) - eElo(partner, oppRankings[2 * i],oppRankings[2 * i + 1]);
		}
		double tES = teamEloScore + tkElo(teamEloScore)*(teamsummation);
		double tGRD = Math.min(350, Math.sqrt(sq(teamGlickoRD) + GLICKO_C_SQUARED * time));
		teamsummation = 0;
		for(int i=0;i<3;i++){
			if(oppRankings[2 * i] == null) continue;
			teamsummation += gGlicko(oppRankings[2 * i], oppRankings[2 * i + 1])
					* ((results[i]?1:0) - eGlicko(partner, oppRankings[2 * i], oppRankings[2 * i + 1]));
		}
		double tdsq;
		double tGS = teamGlickoScore + GLICKO_Q * teamsummation / ((1 / sq(tGRD)) + (tdsq = dminus2(partner, oppRankings)));
		tGRD = Math.pow((1/tGRD) + tdsq, -.5);
		
		
		
		return new Ranking(t, eS, gS, gRD, tES, tGS, tGRD, (byte) (depth + 1));
	}
	private double kElo(double eS) {
		if(eS < 2100) return 32;
		if(eS < 2400) return 24;
		return 16;
	}
	private double tkElo(double eS) {
		if(eS < 2100) return 64;
		if(eS < 2400) return 48;
		return 32;
	}
	private double eElo(Ranking r){
		return 1 / (1 + Math.pow(10, (r.getEloScore() - eloScore) / 400));
	}
	private double eElo(Ranking p, Ranking o1, Ranking o2) {
		double teamScore = (teamEloScore + p.getTeamEloScore()) / 2;
		double oppScore = (o1.getTeamEloScore() + o2.getTeamEloScore()) / 2;
		return 1 / (1 + Math.pow(10, (oppScore - teamScore) / 400));
	}
	private double gGlicko(Ranking r){
		return 1 / Math.sqrt(1 + 3 * sq(GLICKO_Q) * sq(r.getGlickoRD()) / sq(Math.PI));
	}
	private double gGlicko(Ranking r1, Ranking r2){
		double teamRD = (r1.getTeamGlickoRD() + r2.getTeamGlickoRD()) / 2;
		return 1 / Math.sqrt(1 + 3 * sq(GLICKO_Q) * sq(teamRD) / sq(Math.PI));
	}
	private double eGlicko(Ranking r){
		return 1 / (1 + Math.pow(10, (gGlicko(r) * (glickoScore - r.getGlickoScore()) / -400 )));
	}
	private double eGlicko(Ranking p, Ranking o1, Ranking o2){
		double teamScore = (teamGlickoScore + p.getTeamGlickoScore()) / 2;
		double oppScore = (o1.getTeamGlickoScore() + o2.getTeamGlickoScore()) / 2;
		return 1 / (1 + Math.pow(10, (gGlicko(o1, o2) * (teamScore - oppScore) / -400 )));
	}
	private double dminus2(Ranking[] oppRankings) {
		double summation = 0;
		for(int i=0;i<6;i++){
			if(oppRankings[i] == null) continue;
			summation += sq(gGlicko(oppRankings[i]))*eGlicko(oppRankings[i])*(1 - eGlicko(oppRankings[i]));
		}
		return (sq(GLICKO_Q) * summation);
	}
	private double dminus2(Ranking p, Ranking[] oppRankings) {
		double summation = 0;
		for(int i=0;i<3;i++){
			if(oppRankings[2 * i] == null) continue;
			summation += sq(gGlicko(oppRankings[2 * i], oppRankings[2 * i + 1]))
					*eGlicko(p, oppRankings[2 * i], oppRankings[2 * i + 1])
					*(1 - eGlicko(p, oppRankings[2 * i], oppRankings[2 * i + 1]));
		}
		return (sq(GLICKO_Q) * summation);
	}
	private double sq(double d){
		return d*d;
	}
	public Tournament getTournament() {
		return tournament;
	}
	public double getEloScore() {
		return eloScore;
	}
	public double getGlickoScore() {
		return glickoScore;
	}
	public double getGlickoRD() {
		return glickoRD;
	}
	public double getTeamEloScore() {
		return teamEloScore;
	}
	public double getTeamGlickoScore() {
		return teamGlickoScore;
	}
	public double getTeamGlickoRD() {
		return teamGlickoRD;
	}
	public byte getDepth() {
		return depth;
	}
	public String toString(){
        DecimalFormat df = new DecimalFormat("#.##");
		return "Rankings (Solo): Elo: "+df.format(eloScore)+" Glicko: "+df.format(glickoScore)+" +- "+glickoRD+"\n"+
		"Rankings (Team): Elo: "+df.format(teamEloScore)+" Glicko: "+df.format(teamGlickoScore)+" +- "+teamGlickoRD;
	}
}
