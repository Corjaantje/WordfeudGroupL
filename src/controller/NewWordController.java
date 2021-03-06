package controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JOptionPane;

import Gamestate.GamestateManager;
import Gamestate.NewWordstate;

public class NewWordController
{
	private GamestateManager gsm;
	private DatabaseController databaseController;

	private JList addedWordList;
	private NewWordstate newWordstate;
	
	public NewWordController(GamestateManager gsm, NewWordstate newWordstate)
	{
		this.gsm = gsm;
		databaseController = gsm.getDatabaseController();
		this.newWordstate = newWordstate;
	}

	public void addNewWord(String word, String letterSet)
	{
		
		if(!doesWordExist(word, letterSet)) {
			//add word
			if (word.length() > 0) {
				if (word.length() <= 15){
					databaseController.queryUpdate("insert into woordenboek (woord, letterset_code, status, account_naam) values ('" + word + "', '" + letterSet + "', 'pending', '" + gsm.getUser().getUsername() +"')");
					newWordstate.createAddedWordList();
					JOptionPane.showMessageDialog(null, "" + word + " is succesvol toegevoegd aan het woordenboek met letterset " + letterSet + ".");
				} else {
					JOptionPane.showMessageDialog(null, "De maximale lengte van een woord is 15 characters.");
				}
			} else {
				JOptionPane.showMessageDialog(null, "Het woord moet minstens 1 character bevatten.");
			}
		} else {
			JOptionPane.showMessageDialog(null, "Dat woord staat al in het woordenboek met letterset " + letterSet + ".");
		}
	}
	
	// check if word already exists in the dictionary
	private boolean doesWordExist(String word, String letterSet)
	{
		ResultSet rSet = databaseController.query("select * from woordenboek where letterset_code = '" + letterSet + "'");
		try {
			while(rSet.next()) {
				if(rSet.getString("woord").toLowerCase().equals(word)) {
					return true;
				}
			}
			
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	// get the codes from all the lettersets
	private ArrayList<String> getLetterSets() {
		ArrayList<String> allLetterSets = new ArrayList<>();
		ResultSet rSet = databaseController.query("select * from letterset");
		
		try {
			while(rSet.next()) {
				allLetterSets.add(rSet.getString("code"));
			}
			
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return allLetterSets;
	}

	public void fillLetterSetComboBox(JComboBox<String> lettersetComboBox)
	{
		for (String lettersetCode : getLetterSets()) {
			lettersetComboBox.addItem(lettersetCode);
		}
		
	}	
	
	private ArrayList<String> getAddedWords() {
		ArrayList<String> allAddedWords = new ArrayList<>();
		ResultSet rSet = databaseController.query("select woord, letterset_code, status from woordenboek where account_naam = '" + gsm.getUser().getUsername() +"'");
		
		try {
			while(rSet.next()) {
				allAddedWords.add("" + rSet.getString("woord") + ", " + rSet.getString("letterset_code") + ", " + rSet.getString("status"));
				
			}
			
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		return allAddedWords;
	}
	@SuppressWarnings("rawtypes")
	public JList generateAddedWordsList() {
		addedWordList = new JList<>(getAddedWords().toArray());
		
		return addedWordList;
	}
}
