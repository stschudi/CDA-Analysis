package us.tschudi.cda;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Main {
	public static final byte GENDER_MALE = 1;
	public static final byte GENDER_FEMALE = 0;
	public static final byte GENDER_UNKNOWN = 2;

	public static final byte TEAM_AFF = 1;
	public static final byte TEAM_NEG = 0;
	public static final byte TEAM_BYE = 2;
	
	public static final byte POLITICAL_LEFT = 0;
	public static final byte POLITICAL_RIGHT = 1;
	public static final byte POLITICAL_NEUTRAL = 2;
	
	private static Data data;
	
	public static void main(String[] args) throws IOException, JSONException, ParserConfigurationException, TransformerException {
		data = new Data();
		String s = "";
		displayMenu();
		s = readString("Please select a menu option:");
		while(isOption(s) == -1){
			System.out.println("That is not a valid selection\n");
			s = readString("Please select a menu option:");
		}
		switch(isOption(s)){
		case 0:
			String[][] files = {
					{"all","debaters_comprehensive"},{"09-10","debaters_season_2009-10"},{"10-11","debaters_season_2010-11"},
					{"11-12","debaters_season_2011-12"},{"12-13","debaters_season_2012-13"},{"13-14","debaters_season_2013-14"}
			};
			for(String[] file:files){
				data = new Data();
				getInputDataFromSeason(file[0]);
				System.out.println("\nAll Data Imported...");
				updateRankings();
				System.out.println("\nAll Rankings Calculated...\n");
				predictGenders();
				System.out.println();
				storeToXML(file[1]);
				storeDataToXML(file[1]);
				System.out.println();
			}
			return;
		case 1:
			getAllInputData();
			break;
		case 2:
			getSomeInputData();
			break;
		}
		System.out.println("\nAll Data Imported...");
		updateRankings();
		System.out.println("\nAll Rankings Calculated...\n");
		predictGenders();
		System.out.println();
		String filename = readString("Filename:");
		storeToXML(filename);
		storeDataToXML(filename);
		System.out.println();
		System.out.println("Closing...");
	}
	/**
	 * An array containing the options and their shortened keys.
	 */
	private static String[][] menuOptions = {
		{"Generate all XML Files:", "G"},
		{"Input, Predict, & Store ALL:", "A"},
		{"Input, Predict, & Store Some:", "S"}
	};
	/**
	 * Displays the menu based on the <code>menuOptions</code> array.
	 */
	private static void displayMenu() {
		String format = "%-35s%1s";
		String s = "";
		for(int i=0;i<menuOptions.length;i++){
			s+=String.format(format, menuOptions[i][0],menuOptions[i][1])+"\n";
		}
		System.out.println(s);
	}
	/**
	 * Checks if the string corresponds to a key in the <code>menuOptions</code> array, and outputs its index.
	 * 
	 * @param s
	 * The string to check.
	 * 
	 * @return
	 * The index of the option in the array. -1 if the key does not exist.
	 */
	private static int isOption(String s){
		for(int i=0;i<menuOptions.length;i++){
			if(s.toUpperCase().equals(menuOptions[i][1])){
				return i;
			}
		}
		return -1;
	}
	/**
	 * An array of all the names of the debate result files and their corresponding seasons.
	 */
	private static String[][] debates = {
		{"2009-10-24 Varsity.txt","09-10"},{"2009-11-14 Varsity.txt","09-10"},{"2009-12-12 Varsity.txt","09-10"},{"2010-1-9 Varsity.txt","09-10"},
		{"2010-3-6 Varsity.txt","09-10"},{"2010-3-27 Varsity Finals.txt","09-10"},{"2010-10-16 Varsity.txt","10-11"},{"2010-11-13 Varsity.txt","10-11"},
		{"2010-12-11 Varsity.txt","10-11"},{"2011-2-12 Varsity.txt","10-11"},{"2011-3-5 Varsity.txt","10-11"},{"2011-3-26 Varsity Finals.txt","10-11"},
		{"2011-10-15 Varsity.txt","11-12"},{"2011-11-12 Varsity.txt","11-12"},{"2011-12-10 Varsity.txt","11-12"},{"2012-1-7 Varsity.txt","11-12"},
		{"2012-2-4 Varsity.txt","11-12"},{"2012-3-3 Varsity.txt","11-12"},{"2012-3-24 Varsity Finals.txt","11-12"},{"2012-10-13 Varsity.txt","12-13"},
		{"2012-11-17 Varsity.txt","12-13"},{"2013-1-12 Varsity.txt","12-13"},{"2013-2-2 Varsity.txt","12-13"},{"2013-2-16 Varsity.txt","12-13"},
		{"2013-3-2 Varsity.txt","12-13"},{"2013-3-23 Varsity Finals.txt","12-13"},{"2013-10-12 Varsity.txt","13-14"},{"2013-11-16 Varsity.txt","13-14"},
		{"2014-1-11 Varsity.txt","13-14"},{"2014-2-1 Varsity.txt","13-14"},{"2014-3-1 Varsity.txt","13-14"},{"2014-3-22 Varsity Finals.txt","13-14"}
	};
	/**
	 * Iterates through the filenames in the <code>debates</code> array and asks which input data the user wants to import.
	 * 
	 * @throws IOException
	 */
	private static void getSomeInputData() throws IOException{
		for(String[] file:debates){
			if(readString("Get data from \""+file[0]+"\" (Season: "+file[1]+"? (Y/N)").equalsIgnoreCase("Y")){
				getInputData(file[0]);
			}
		}
	}
	/**
	 * Gets input data from a specific season, or from all if "all" is passed.
	 * 
	 * @param season
	 * The season to get from, or "all".
	 * 
	 * @throws IOException
	 */
	private static void getInputDataFromSeason(String season) throws IOException{
		if(season.equalsIgnoreCase("All")){
			getAllInputData();
			return;
		}
		for(String[] file:debates){
			if(file[1].equalsIgnoreCase(season)){
				getInputData(file[0]);
			}
		}
	}
	/**
	 * Gets all input data from the <code>debates</code> array.
	 * 
	 * @throws IOException
	 */
	private static void getAllInputData() throws IOException{
		for(String[] file:debates){
			getInputData(file[0]);
		}
	}
	/**
	 * The reader for the input files.
	 */
	private static Scanner fileReader;
	/**
	 * Gets all input data from the raw files given a filename.
	 * 
	 * @param filename
	 * The filename of the input data.
	 * 
	 * @throws IOException
	 */
	private static void getInputData(String filename) throws IOException{
		fileReader = new Scanner(new File(filename));
		
		String debateName = fileReader.nextLine();
		int time = Integer.parseInt(fileReader.nextLine());
		String topic = fileReader.nextLine();
		String ppoa = fileReader.nextLine();
		byte politicalPreferenceOfAff;
		if(ppoa.equalsIgnoreCase("Left")){
			politicalPreferenceOfAff = POLITICAL_LEFT;
		} else if(ppoa.equalsIgnoreCase("Right")) {
			politicalPreferenceOfAff = POLITICAL_RIGHT;
		} else {
			politicalPreferenceOfAff = POLITICAL_NEUTRAL;
		}
		Tournament tournament = new Tournament(debateName, time, topic, politicalPreferenceOfAff);
		data.addTournament(tournament);
		
		while(fileReader.hasNextLine()){
			fileReader.nextLine();
			String school = fileReader.nextLine();
			String d1 = fileReader.nextLine();
			String d2 = fileReader.nextLine();
			Debater debater1, debater2;
			if((debater1 = data.getDebaterByName(d1)) == null){
				debater1 = new Debater(d1,school,(byte)2);
				data.addDebater(debater1);
			} else {
				debater1.setSchool(school);
			}
			if((debater2 = data.getDebaterByName(d2)) == null){
				debater2 = new Debater(d2,school,(byte)2);
				data.addDebater(debater2);
			} else {
				debater2.setSchool(school);
			}
			DebateRound[] debateRounds = new DebateRound[3];
			boolean prevBye = false;
			String buffer = "";
			for(int i=0;i<debateRounds.length;i++){
				if(prevBye){
					prevBye = false;
				} else {
					buffer = fileReader.nextLine();
				}
				String[] winner = buffer.split(" ");
				byte side;
				if(winner[1].equals("AFF")){
					side = Main.TEAM_AFF;
				} else if(winner[1].equals("Neg")){
					side = Main.TEAM_NEG;
				} else {
					debateRounds[i] = new DebateRound(null,(byte)2);
					fileReader.nextLine();
					fileReader.nextLine();
					buffer = fileReader.nextLine();
					Pattern p1 = Pattern.compile("[WL] [NABF][eFyf][gFet]");
					Pattern p2 = Pattern.compile("[0123]-[0123].*");
					Matcher m1 = p1.matcher(buffer);
					Matcher m2 = p2.matcher(buffer);
					prevBye = true;
					if(!(m1.matches() || m2.matches())){
						buffer = fileReader.nextLine();
						m1 = p1.matcher(buffer);
						m2 = p2.matcher(buffer);
						if(!(m1.matches() || m2.matches())){
							buffer = fileReader.nextLine();
						}
					}
					continue;
				}
				String oppID = fileReader.nextLine();
				String judge = fileReader.nextLine();
				String[] score1 = fileReader.nextLine().split(" ");
				String[] score2 = fileReader.nextLine().split(" ");
				
				Round r = null;
				Judge j;
				if((j = data.getJudgeByName(judge)) == null || (r = data.getRoundByTournamentJudgeNumber(tournament, j, i+1)) == null){
					if(j == null){
						j = new Judge(judge);
						data.addJudge(j);
					}
					Team aff = new Team(), neg = new Team();
					if(side == 1){
						aff.setDebaters(new Debater[]{debater1, debater2});
						neg.setID(oppID);
					} else {
						aff.setID(oppID);
						neg.setDebaters(new Debater[]{debater1, debater2});
					}
					double[] scores;
					double[] ranks;
					byte win;
					if(side == Main.TEAM_AFF){
						scores = new double[]{Double.parseDouble(score1[0]),Double.parseDouble(score2[0]),0,0};
						ranks = new double[]{Double.parseDouble(score1[1]),Double.parseDouble(score2[1]),0,0};
						win = (byte)(winner[0].equals("L")?Main.TEAM_NEG:Main.TEAM_AFF);
					} else {
						scores = new double[]{0,0,Double.parseDouble(score1[0]),Double.parseDouble(score2[0])};
						ranks = new double[]{0,0,Double.parseDouble(score1[1]),Double.parseDouble(score2[1])};
						win = (byte)(winner[0].equals("L")?Main.TEAM_AFF:Main.TEAM_NEG);
					}
					r = new Round(tournament, i+1, scores, ranks, win, j, aff, neg);
					data.addRound(r);
					j.addRound(r);
				} else {
					Team aff = r.getAff(), neg = r.getNeg();
					if(side == Main.TEAM_NEG){
						r.getScores()[2] = Double.parseDouble(score1[0]);
						r.getScores()[3] = Double.parseDouble(score2[0]);
						r.getRanks()[2] = Double.parseDouble(score1[1]);
						r.getRanks()[3] = Double.parseDouble(score2[1]);
						aff.setID(oppID);
						neg.setDebaters(new Debater[]{debater1, debater2});
					} else {
						r.getScores()[0] = Double.parseDouble(score1[0]);
						r.getScores()[1] = Double.parseDouble(score2[0]);
						r.getRanks()[0] = Double.parseDouble(score1[1]);
						r.getRanks()[1] = Double.parseDouble(score2[1]);
						aff.setDebaters(new Debater[]{debater1, debater2});
						neg.setID(oppID);
					}
				}
				debateRounds[i] = new DebateRound(r, side);
			}
			Debate debate = new Debate(tournament, debateRounds);
			debater1.addDebate(debate);
			debater2.addDebate(debate);
			if(!prevBye)fileReader.nextLine();
			fileReader.nextLine();
			String str = fileReader.nextLine();
			if(str.equals("1st Seed") || str.equals("2nd Seed")) fileReader.nextLine();
			fileReader.nextLine();
		}
		fileReader.close();
//		System.out.println("Import of \""+debateName+"\" Complete...");
	}
	/**
	 * Iterates through all the debaters and calculates their rankings. This is done breadth wise, so it must be done after the
	 * input data is collected, not concurrently.
	 */
	private static void updateRankings() {
		Tournament[] tournaments = data.getTournaments().toArray(new Tournament[0]);
		for(Debater d:data.getDebaters()){
				d.clearRankings();
		}
		for(Tournament t:tournaments){
			for(Debater d:data.getDebatersThatWentTo(t)){
				d.updateRanking(t);
			}
		}
	}
	/**
	 * Stores all the data to an XML file with the specified filename.
	 * 
	 * @param filename
	 * The filename to output the data to.
	 * 
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 */
	private static void storeToXML(String filename) throws ParserConfigurationException, TransformerException {
		Tournament[] tournaments = data.getTournaments().toArray(new Tournament[0]);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.newDocument();
		Element rootElement = doc.createElement("debate");
		doc.appendChild(rootElement);
		for(Tournament t:tournaments){
			rootElement.appendChild(tournamentToXML(doc, t));
		}
		for(Debater d:data.getDebaters()){
			rootElement.appendChild(debaterToXML(doc, d));
		}
		for(Judge j:data.getJudges()){
			rootElement.appendChild(judgeToXML(doc, j));
		}
		for(School s:data.getSchoolsThatWentTo(tournaments)){
			rootElement.appendChild(schoolToXML(doc, s));
		}
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		DOMSource source = new DOMSource(doc);
		StreamResult file = new StreamResult(new File(filename+".xml"));
		transformer.transform(source, file);
		
		System.out.println("\nXML Stored...");
	}
	/**
	 * Stores data points for each debater to create graphs.
	 *  
	 * @param filename
	 * The filename to output the data to.
	 *  
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 */
	private static void storeDataToXML(String filename) throws IOException, ParserConfigurationException, TransformerException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.newDocument();
		Element rootElement = doc.createElement("debate");
		doc.appendChild(rootElement);
		for(Debater d:data.getDebaters()){
			rootElement.appendChild(debaterDataToXML(doc, d));
		}
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		DOMSource source = new DOMSource(doc);
		StreamResult file = new StreamResult(new File(filename+"_data.xml"));
		transformer.transform(source, file);
		
		System.out.println("\nData XML Stored...");
	}
	/**
	 * Creates an XML node out of the tournament.
	 * 
	 * @param doc
	 * The document to create the node in.
	 * 
	 * @param t
	 * The tournament to turn into a node.
	 * 
	 * @return
	 * A XML node containing data concerning the tournament.
	 */
	private static Node tournamentToXML(Document doc, Tournament t) {
		Element school = doc.createElement("tournament");
		Element node;
		String[][] nodes = {
				{"name",t.getName()},
				{"time_since_2000-1-1",""+t.getTime()},
				{"topic",t.getTopic()},
				{"political_position_aff",t.getPoliticalPreferenceOfAffString()}
		};
		for(String[] sA:nodes){
			node = doc.createElement(sA[0]);
			node.appendChild(doc.createTextNode(sA[1]));
			school.appendChild(node);
		}
		return school;
	}
	/**
	 * Creates an XML node out of the school.
	 * 
	 * @param doc
	 * The document to create the node in.
	 * 
	 * @param s
	 * The school to turn into a node.
	 * 
	 * @return
	 * A XML node containing data concerning the school.
	 */
	private static Node schoolToXML(Document doc, School s) {
		Element school = doc.createElement("school");
		Element node;
		String[][] nodes = {
				{"name",s.getName()},
				{"num_debates",""+s.getNumDebates()},
				{"total_score",""+s.getTotalScore()},
				{"total_rank",""+s.getTotalRank()},
				{"total_wins",""+s.getTotalWins()},
				{"average_score",""+s.getAverageScore()},
				{"average_rank",""+s.getAverageRank()},
				{"win_ratio",""+s.getWinRatio()},
				{"rounds_aff",""+s.getAffRounds()},
				{"rounds_neg",""+s.getNegRounds()},
				{"wins_aff",""+s.getAffWins()},
				{"wins_neg",""+s.getNegWins()},
				{"preffered_side",s.getPreferredSide()},
				{"better_side",s.getBetterSide()},
				{"ratio_aff",""+s.getAffWinRatio()},
				{"ratio_neg",""+s.getNegWinRatio()}
		};
		for(String[] sA:nodes){
			node = doc.createElement(sA[0]);
			node.appendChild(doc.createTextNode(sA[1]));
			school.appendChild(node);
		}
		return school;
	}
	/**
	 * Creates an XML node out of the debater.
	 * 
	 * @param doc
	 * The document to create the node in.
	 * 
	 * @param d
	 * The debater to turn into a node.
	 * 
	 * @return
	 * A XML node containing data concerning the debater.
	 */
	private static Node debaterToXML(Document doc, Debater d) {
		Element debater = doc.createElement("debater");
		Element node;
		String[][] nodes = {
				{"name",d.getName()},
				{"school",d.getSchool()},
				{"gender",d.getGenderString()},
				{"score_elo",""+d.getEloScore()},
				{"score_glicko",""+d.getGlickoScore()},
				{"score_glicko_rd",""+d.getGlickoRD()},
				{"score_elo_team",""+d.getTeamEloScore()},
				{"score_glicko_team",""+d.getTeamGlickoScore()},
				{"score_glicko_rd_team",""+d.getTeamGlickoRD()},
				{"num_debates",""+d.getNumDebates()},
				{"total_score",""+d.getTotalScore()},
				{"total_rank",""+d.getTotalRank()},
				{"total_wins",""+d.getTotalWins()},
				{"average_score",""+d.getAverageScore()},
				{"average_rank",""+d.getAverageRank()},
				{"win_ratio",""+d.getWinRatio()},
				{"rounds_aff",""+d.getAffRounds()},
				{"rounds_neg",""+d.getNegRounds()},
				{"wins_aff",""+d.getAffWins()},
				{"wins_neg",""+d.getNegWins()},
				{"preffered_side",d.getPreferredSide()},
				{"better_side",d.getBetterSide()},
				{"ratio_aff",""+d.getAffWinRatio()},
				{"ratio_neg",""+d.getNegWinRatio()},
				{"partner_common",d.getMostCommonPartner()},
				{"opponent_common",d.getMostCommonOpponent()},
				{"opponent_feared",d.getMostDefeatedOpponent()},
				{"opponent_fearedby",d.getMostDefeatedByOpponent()}
		};
		for(String[] sA:nodes){
			node = doc.createElement(sA[0]);
			node.appendChild(doc.createTextNode(sA[1]));
			debater.appendChild(node);
		}
		return debater;
	}
	/**
	 * Creates an XML node out of the debater's elo vs time data.
	 * 
	 * @param doc
	 * The document to create the node in.
	 * 
	 * @param d
	 * The debater to turn into a data node.
	 * 
	 * @return
	 * A XML node containing data concerning elo vs time of the debater.
	 */
	private static Node debaterDataToXML(Document doc, Debater d) {
		Element debater = doc.createElement("debater_data");
		debater.setAttribute("Name", d.getName().replace('\'', '_'));
		ArrayList<Ranking> rankings = d.getRankings();
		Element point;
		for(Ranking r:rankings){
			String[][] nodes = {
					{"time",""+r.getTournament().getTime()},
					{"score_elo",""+r.getEloScore()},
					{"score_glicko",""+r.getGlickoScore()},
					{"score_glicko_RD",""+r.getGlickoRD()},
					{"score_elo_team",""+r.getTeamEloScore()},
					{"score_glicko_team",""+r.getTeamGlickoScore()},
					{"score_glicko_RD_team",""+r.getTeamGlickoRD()}
			};
			Element node;
			point = doc.createElement("data_point");
			for(String[] sA:nodes){
				node = doc.createElement(sA[0]);
				node.appendChild(doc.createTextNode(sA[1]));
				point.appendChild(node);
			}
			debater.appendChild(point);
		}
		return debater;
	}
	/**
	 * Creates an XML node out of the judge.
	 * 
	 * @param doc
	 * The document to create the node in.
	 * 
	 * @param j
	 * The judge to turn into a node.
	 * 
	 * @return
	 * A XML node containing data concerning the judge.
	 */
	private static Node judgeToXML(Document doc, Judge j) {
		Element judge = doc.createElement("judge");
		Element node;
		String[][] nodes = {
				{"name",j.getName()},
				{"num_rounds",""+j.getNumRounds()},
				{"num_debaters",""+j.getNumDebaters()},
				{"average_score",""+j.getAverageScore()},				
				{"side_preference_score",j.getSideScorePreference()},
				{"aff_avg_score",""+j.getAffAvgScore()},
				{"neg_avg_score",""+j.getNegAvgScore()},
				{"side_preference_wins",j.getSideWinPreference()},
				{"aff_win_rate",""+j.getAffWinRate()},
				{"neg_win_rate",""+j.getNegWinRate()},
				{"gender_preference_score",j.getGenderScorePreference()},
				{"male_num_debates",""+j.getNumMaleDebaters()},
				{"female_num_debates",""+j.getNumFemaleDebaters()},
				{"male_avg_score",""+j.getMaleAvgScore()},
				{"female_avg_score",""+j.getFemaleAvgScore()},
				{"political_preference_score",j.getPoliticalScorePreference()},
				{"left_avg_score",""+j.getLeftWingAvgScore()},
				{"right_avg_score",""+j.getRightWingAvgScore()},
				{"political_preference_wins",j.getPoliticalWinPreference()},
				{"left_win_rate",""+j.getLeftWingWinRate()},
				{"right_win_rate",""+j.getRightWingWinRate()},
				{"repeat_num_debates",""+j.getNumRepeatDebaters()},
				{"repeat_avg_score",""+j.getRepeatAvgScore()},
				{"repeat_win_rate",""+j.getRepeatWinRate()}
		};
		for(String[] sA:nodes){
			node = doc.createElement(sA[0]);
			node.appendChild(doc.createTextNode(sA[1]));
			judge.appendChild(node);
		}
		return judge;
	}
	/**
	 * Predicts the genders of all the debaters using the Genderize API.
	 * 
	 * @throws IOException
	 * @throws JSONException
	 */
	private static void predictGenders() throws IOException, JSONException{
		ArrayList<Debater> debaters = data.getDebaters();
		TreeMap<String, Byte> genderMap = getGenders();
		for(Debater d:debaters){
			String name = null;
			if(genderMap.containsKey(name = d.getName().split(" ")[0])){
				d.setGender(genderMap.get(name));
			} else { 
				JSONObject json = sendGet("http://api.genderize.io/?name="+(d.getName().split(" ")[0]));
				genderMap.put(name, predictGender(json));
				System.out.println("Adding "+name+" to system...");
			}
			saveGenders(genderMap);
		}
		System.out.println("Debaters updated... Genders predicted... Database saved...");
	}
	/**
	 * Loads the previously saved genders to a <code>TreeMap</code>.
	 * 
	 * @return
	 * A <code>TreeMap</code> containing all the previous genders.
	 * 
	 * @throws FileNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private static TreeMap<String, Byte> getGenders() throws FileNotFoundException {
		TreeMap<String, Byte> genderMap = null;
		try {
			FileInputStream file = new FileInputStream("names.obj");
			ObjectInputStream fin  = new ObjectInputStream(file);
			try{
				genderMap = (TreeMap<String, Byte>) fin.readObject();
			} catch(EOFException e){
				if(genderMap == null){
					genderMap = new TreeMap<>();
				}
			}
			file.close();
		} catch(IOException e){
			genderMap = new TreeMap<String, Byte>();
		} catch(ClassNotFoundException e){
			e.printStackTrace();
		}
		return genderMap;
	}
	/**
	 * Returns the byte notation of gender given the JSON object.
	 * 
	 * @param json
	 * The JSON object to analyze.
	 * 
	 * @return
	 * The byte notation of the gender.
	 * 
	 * @throws JSONException
	 */
	private static byte predictGender(JSONObject json) throws JSONException {
		if(json == null){
			return GENDER_UNKNOWN;
		}
		if(json.get("gender")==JSONObject.NULL){
			return GENDER_UNKNOWN;
		}
		String gender;
		if(json.getDouble("probability") < .7){
			return GENDER_UNKNOWN;
		}
		if((gender = json.getString("gender")).equals("male")){
			return GENDER_MALE;
		} else if(gender.equals("female")){
			return GENDER_FEMALE;
		}
		return GENDER_UNKNOWN;
	}
	/**
	 * Sends a request to genderize.io asking about the gender of a name.
	 * 
	 * @param url
	 * The constructed URL to ask about.
	 * 
	 * @return
	 * A JSON Object containing the result.
	 * 
	 * @throws IOException
	 * @throws JSONException
	 */
	private static JSONObject sendGet(String url) throws IOException, JSONException {
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", "Mozilla/5.0");
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		if(response.toString() == null) return null;
		return new JSONObject(response.toString());
	}
	/**
	 * Saves the current <code>TreeMap</code> to a object file.
	 * 
	 * @param genderMap
	 * The <code>TreeMap</code> to save.
	 */
	private static void saveGenders(TreeMap<String, Byte> genderMap) {
		try {
			FileOutputStream file = new FileOutputStream("names.obj");
			ObjectOutputStream fout = new ObjectOutputStream(file);
			fout.writeObject(genderMap);
			fout.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * The input reader.
	 */
	private static BufferedReader reader;
	/**
	 * Prints the query and reads the input string.
	 * 
	 * @param query
	 * The query to print.
	 * 
	 * @return
	 * The input string.
	 * 
	 * @throws IOException
	 */
	private static String readString(String query) throws IOException {
		if(reader == null) reader = new BufferedReader(new InputStreamReader(System.in));
		System.out.print(query+" ");
		return reader.readLine();
	}
	/**
	 * An array containing the critical values for a 2 tailed t test where p = .05.
	 */
	private static double[] ttestCrits = {0,12.71,4.303,3.182,2.776,2.571,2.447,2.365,2.306,2.262,2.228,2.201,2.179,2.160,
			2.145,2.131,2.120,2.110,2.101,2.093,2.086,2.080,2.074,2.069,2.064,2.060,2.056,2.052,2.048,2.045,2.042};
	/**
	 * Gets the critical value for a 2 tailed t test where p = .05 based on the degrees of freedom.
	 * 
	 * @param df
	 * The degrees of freedom of the t test.
	 * 
	 * @return
	 * The critical value.
	 */
	public static double ttest(int df) {
		if(df <= 30){
			return ttestCrits[df];
		} else if (df <= 40){
			return 2.021 + .002 * (df - 30);
		} else if (df <= 60){
			return 2.000 + .001 * (df - 40);
		} else if (df <= 80){
			return 1.990 + .0005 * (df - 60);
		} else if (df <= 100){
			return 1.986;
		} else if (df <= 1000){
			return 1.962;
		}
		return 1.960;
	}
}
