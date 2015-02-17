package us.tschudi.cda;

public class Team {
	private String ID;
	private Debater[] debaters;
	
	public void setDebaters(Debater[] debaters) {
		this.debaters = debaters;
	}
	public String getID() {
		return ID;
	}
	public void setID(String id) {
		ID = id;
	}
	public Debater[] getDebaters() {
		return debaters;
	}
	public boolean equals(Object o){
		if(!(o instanceof Team)) return false;
		Team t = (Team) o;
		return ID.equals(t.getID());
	}
	public String toString(){
		return "ID: \""+ID+"\"";
	}
}
