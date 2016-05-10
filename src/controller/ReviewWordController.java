package controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JList;
import javax.swing.JOptionPane;

import Gamestate.GamestateManager;

public class ReviewWordController
{
	private GamestateManager gsm;
	private DatabaseController databaseController;

	public ReviewWordController(GamestateManager gsm)
	{
		this.gsm = gsm;
		databaseController = gsm.getDatabaseController();

	}

	public void denyWord(String word, String letterset)
	{
		if (word != null){
			databaseController.queryUpdate("UPDATE woordenboek SET status = 'denied' WHERE woord = '" + word + "' AND letterset_code = '" + letterset + "'");
		} else {
			JOptionPane.showMessageDialog(null, "Er is geen woord geselecteerd.");
		}
		
	}

	public void acceptWord(String word, String letterset)
	{
		if (word != null){
			databaseController.queryUpdate("UPDATE woordenboek SET status = 'accepted' WHERE woord = '" + word + "' AND letterset_code = '" + letterset + "'");
		} else {
			JOptionPane.showMessageDialog(null, "Er is geen woord geselecteerd.");
		}
	}

	public JList generatePendingWordsList(String letterset) {
		JList addedWordList = new JList<>(getPendingWords(letterset).toArray());
		
		return addedWordList;
	}
	
	private ArrayList<String> getPendingWords(String letterset) {
		ArrayList<String> allPendingWords = new ArrayList<>();
		ResultSet rSet = databaseController.query("select woord, letterset_code, status from woordenboek where letterset_code = '" + letterset +"'");
		
		try {
			while(rSet.next()) {
				if (rSet.getString("status").equals("pending")) {
					allPendingWords.add(rSet.getString("woord"));
				}
			}
			
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		return allPendingWords;
	}
}
