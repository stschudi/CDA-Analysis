package us.tschudi.cda;

public class Tournament {
	private String name;
	private int time;
	private String topic;
	private byte polticalPreferenceOfAff;
	public Tournament (String n, int t, String top, byte p){
		name = n;
		time = t;
		topic = top;
		polticalPreferenceOfAff = p;
	}
	public String getName() {
		return name;
	}
	public int getTime() {
		return time;
	}
	public String getTopic() {
		return topic;
	}
	public byte getPoliticalPreferenceOfAff() {
		return polticalPreferenceOfAff;
	}
	public String getPoliticalPreferenceOfAffString() {
		switch(polticalPreferenceOfAff){
		case Main.POLITICAL_LEFT:
			return "Left";
		case Main.POLITICAL_RIGHT:
			return "Right";
		case Main.POLITICAL_NEUTRAL:
			return "Neutral";
		}
		return "";
	}
	public String toString(){
		return "Name: "+name+", Time: "+time;
	}
	public boolean equals(Object o){
		if(!(o instanceof Tournament)) return false;
		Tournament t = (Tournament) o;
		return t.getTime()==time && t.getName().equals(name);
	}
}
