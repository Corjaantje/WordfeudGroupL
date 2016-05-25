package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import GameObjects.CompetitionFrame;
import Gamestate.GamestateManager;

public class CompetitionController {

	private GamestateManager gsm;
	private DatabaseController databaseController;

	public CompetitionController(GamestateManager gsm) {
		this.gsm = gsm;
		databaseController = gsm.getDatabaseController();
	}

	public void addCompetition(String description) {

		int competitieID = 0;
		ResultSet rs = databaseController.query("select max(id) from competitie");
		try {
			while (rs.next()) {
				competitieID = rs.getInt("max(id)");
				competitieID++;
			}
			if (description.length() < 5 || description.length() > 25) {
				JOptionPane.showMessageDialog(null, "Je hebt te weinig/veel karakters gebruikt probeer opnieuw.");
				return;
			} else {
				ResultSet eigenaarRS = databaseController.query("select * from competitie");
				while (eigenaarRS.next()) {
					if (eigenaarRS.getString("account_naam_eigenaar").equals(gsm.getUser().getUsername())) {
						JOptionPane.showMessageDialog(null, "Deze gebruiker is al eigenaar.");
						return;
					} else {
						if (eigenaarRS.getString("omschrijving").equals(description)) {

						}
					}

				}
			}
			databaseController.queryUpdate("INSERT INTO competitie VALUES (" + competitieID + ",'" + description + "','" + gsm.getUser().getUsername() + "' ) ");
			JOptionPane.showMessageDialog(null, "Je hebt een nieuwe competitie gemaakt!");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void joinCompetition(String competitionName) {
		ResultSet rs = databaseController
				.query("select * from competitie where omschrijving = '" + competitionName + "' ");
		if (rs.equals(competitionName)) {
			ResultSet playerRs = databaseController.query("select " + gsm.getUser().getUsername() + " from deelnemer as d join competitie as c on d.competitie_id=c.id where omschrijving = " + competitionName + "");
			if (playerRs.equals(gsm.getUser().getUsername())) {
				JOptionPane.showMessageDialog(null, "Je zit al in deze competitie !");
			} else {
				int competitieID = 0;
				try {
					competitieID = rs.getInt("id");
					databaseController.queryUpdate("INSERT INTO deelnemer VALUES '" + gsm.getUser().getUsername() + "'," + competitieID + "");
					JOptionPane.showMessageDialog(null, gsm.getUser().getUsername() + " heeft " + competitionName + " gejoined !");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			JOptionPane.showMessageDialog(null, "Deze competitie bestaat niet!");
			return;
		}
	}

	public ArrayList<JButton> updateCompetitions(ArrayList<JButton> competitions,CompetitionFrame competitionFrame) {
		if (!competitions.isEmpty()) {
			competitions.clear();
		}
		ResultSet rs = databaseController.query("SELECT * FROM competitie");
		try {
			while (rs.next()) {
				int competitionNumber = rs.getInt("id");
				String string = rs.getString("id") + ". " + rs.getString("omschrijving");
				JButton button = new JButton(string);
				button.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						competitionFrame.loadCompetitionFrame(competitionNumber);
					}
				});
				competitions.add(button);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return competitions;
	}
	
	public DefaultTableModel loadParticipantModel(DefaultTableModel participantModel,int competitionNumber){
		String query = "SELECT * FROM deelnemer WHERE competitie_id = " + competitionNumber;
		ResultSet rs = databaseController.query(query);
		try {
			while (rs.next()) {
				participantModel.addRow(new Object[] { rs.getString("account_naam") });
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return participantModel;
	}
	
	public void addUserAsParticipant(int competitionNumber){
		String query = "INSERT INTO deelnemer VALUES ('" + gsm.getUser().getUsername() + "', " + competitionNumber + ")";
		databaseController.queryUpdate(query);
	}
	
	public DefaultTableModel loadRankingModel(DefaultTableModel rankingModel,int competitionNumber){
		String query = "SELECT * FROM competitiestand WHERE competitie_id = " + competitionNumber + " ORDER BY gemidddelde_score DESC";
		ResultSet rs = databaseController.query(query);
		try {
			int counter = 1;
			while (rs.next()) {
				rankingModel.addRow(new Object[] { counter, rs.getString("account_naam"), rs.getString("gemidddelde_score"), rs.getString("aantal_gespeelde_spellen"), rs.getString("aantal_gewonnen_spellen"), rs.getString("aantal_verloren_spellen"),rs.getString("aantal_gelijke_spellen") });
				counter++;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rankingModel;
	}

	public void seeCurrentUsers(int id) {
		ResultSet rs = databaseController.query("select * from deelnemer where competitie_id =" + id + " ORDER BY  account_naam DESC");
		try {
			while (rs.next()) {
				String user = rs.getString("account_naam");
				System.out.println("Momentele gebruiker: " + user);
			}
			if (!rs.next()) {
				JOptionPane.showMessageDialog(null, "Er zijn geen users !");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}