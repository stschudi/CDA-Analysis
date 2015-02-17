package us.tschudi.cda;

public class Round {
	private Tournament tournament;
	private int number;
	private double[] scores; //aff 1, 2, neg 1, 2
	private double[] ranks;
	private byte winner; //1 aff, 0 neg
	private Judge judge;
	private Team aff;
	private Team neg;
	
	public Round(Tournament t, int num, double[] s, double[] r, byte w, Judge j, Team a, Team n){
		tournament = t;
		number = num;
		scores = s;
		ranks = r;
		winner = w;
		judge = j;
		aff = a;
		neg = n;
	}
	public boolean equals(Object o){
		if(!(o instanceof Round)) return false;
		Round r = (Round) o;
		for(int i=0;i<scores.length;i++){
			if(scores[i] != r.getScores()[i] || ranks[i] != r.getRanks()[i]) return false;
		}
		return tournament.equals(r.getTournament()) && number == r.getNumber() && winner == r.getWinner() 
				&& judge.equals(r.getJudge()) && aff.equals(r.getAff()) && neg.equals(r.getNeg());
	}
	public Tournament getTournament() {
		return tournament;
	}
	public int getNumber() {
		return number;
	}
	public byte getWinner() {
		return winner;
	}
	public Team getAff() {
		return aff;
	}
	public Team getNeg() {
		return neg;
	}
	public double[] getScores() {
		return scores;
	}
	public double[] getRanks() {
		return ranks;
	}
	public Judge getJudge() {
		return judge;
	}
	public String toString(){
		return "Name: \""+tournament+"\" Round: \""+number+"\" Judge: ("+judge+")";
	}
}
