package model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import GameObjects.Letter;
import Gamestate.GamestateManager;
import controller.DatabaseController;

public class User {
	private ArrayList<String> roles;
	private String username;
	private String password;
	private final String errorNotificationNotFoundPassword = "Not found password for this user";
	private int spel_id = 500;
	private int turnNumber;
	private int competitionNumber;
	private int amountOfRoles;

	private GamestateManager gsm;
	private DatabaseController databaseController;

	public User(String username) {
		databaseController = new DatabaseController();
		roles = new ArrayList<>();
		this.username = username;
		turnNumber = 19;
		competitionNumber = 1;
		addRoles();
	}

	public boolean checkRole(String inputRole) {
		for (String role : roles) {
			if (role.equals(inputRole)) {
				return true;
			}
		}
		return false;
	}

	private void addRoles() {
		ResultSet rs = databaseController.query("select * from accountrol where account_naam = '" + username + "'");
		try {
			while (rs.next()) {
				roles.add(rs.getString("rol_type"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		databaseController.closeConnection();
	}

	public void setSpelID(int spelnummer) {
		this.spel_id = spelnummer;
	}

	public int getSpelID() {
		return this.spel_id;
	}

	public String getUsername() {
		return this.username;
	}

	public int getTurnNumber() {
		return turnNumber;
	}

	public int getGameNumber() {
		return 511;
	}

	public void setPassword(String passwordOfUsername) {
		ResultSet rs = databaseController
				.query("select wachtwoord from account where naam = '" + passwordOfUsername + "'");
		try {
			if (rs.next()) {
				password = rs.getString("wachtwoord");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getPassword(String passwordOfUsername) {
		ResultSet rs = databaseController
				.query("select wachtwoord from account where naam = '" + passwordOfUsername + "'");
		try {
			if (rs.next()) {
				return this.password;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return errorNotificationNotFoundPassword;
	}

	public int getUserScore() {
		 String query = "SELECT sum(score) AS totaalScore FROM beurt WHERE account_naam = 'marijntje42' AND spel_id ="+this.getGameNumber()+" AND id < "+this.getTurnNumber();
		ResultSet rs = databaseController.query(query);
		int score = -1;
		try {
			while (rs.next()) {
				score = rs.getInt("totaalScore");
			}
			databaseController.closeConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return score;
	}

	public int getOpponentScore() {
		String query = "SELECT sum(score) AS totaalScore FROM beurt WHERE account_naam = '" + this.getOpponentName()
				+ "' AND spel_id = " + this.getGameNumber() + " AND id < " + this.getTurnNumber();
		ResultSet rs = databaseController.query(query);
		int score = -1;
		try {
			while (rs.next()) {
				score = rs.getInt("totaalScore");
			}
			databaseController.closeConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return score;
	}

	public String getOpponentName() {
		String challenger = "";
		String opponent = "";
		ResultSet rs = databaseController
				.query("SELECT account_naam_uitdager  FROM spel WHERE id = " + this.getGameNumber());
		try {
			while (rs.next()) {
				challenger = rs.getString("account_naam_uitdager");
			}
			rs = databaseController
					.query("SELECT account_naam_tegenstander  FROM spel WHERE id = " + this.getGameNumber());
			while (rs.next()) {
				opponent = rs.getString("account_naam_uitdager");
			}
			databaseController.closeConnection();
		} catch (Exception e) {
			// TODO: handle exception
		}
		if (challenger.equals(this.getUsername())) {
			return opponent;
		} else {
			return challenger;
		}
	}

	public String getPlayerTurn() {
		String player = "";
		ResultSet rs = databaseController.query("SELECT account_naam FROM beurt WHERE spel_id = " + this.getGameNumber()
				+ " AND id = " + this.getTurnNumber());
		try {
			while (rs.next()) {
				player = rs.getString("account_naam");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return player;
	}

	public void setTurnNumber(int turnNumber) {
		this.turnNumber = turnNumber;
	}
	
	public int getCompetitionNumber(){
		return competitionNumber;
	}
	
	public String getCompetitionDescription(){
		String description = "";
		ResultSet rs = databaseController.query("SELECT omschrijving FROM competitie WHERE id = "+competitionNumber);
		try {
			while(rs.next()){
				description = rs.getString("omschrijving");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return description;
	}
	
	public int getAmountOfRoles(String nameUser){
		ResultSet rs = databaseController.query("select * from accountrol where account_naam = '" + nameUser + "'");
		amountOfRoles = 0;
		try {
			while(rs.next()){
				amountOfRoles = amountOfRoles + 1;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return amountOfRoles;
	}
}
