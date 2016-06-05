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
	private int turnNumber; // Note: turnNumber is the max(turnNumber), so it's actually the number of the last turn(not the current turn)
	private int competitionNumber;
	private int gameNumber;
	private int amountOfRoles;

	private String opponent;
	private String challenger;

	private GamestateManager gsm;
	private DatabaseController databaseController;

	public User(String username, DatabaseController databaseController) {
		this.databaseController = databaseController;
		roles = new ArrayList<>();
		this.username = username;
		turnNumber = 25;
		gameNumber = 511;
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
		ResultSet rs = databaseController
				.query("select * from accountrol where account_naam = '"
						+ username + "'");
		try {
			while (rs.next()) {
				roles.add(rs.getString("rol_type"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		databaseController.closeConnection();
	}

	public String getUsername() {
		return this.username;
	}
	// Note: turnNumber is the max(turnNumber), so it's actually the number of the last turn(not the current turn)
	public int getTurnNumber() {
		return turnNumber;
	}

	public int getGameNumber() {
		return gameNumber;
	}

	public void setGameNumber(int gameNumber) {
		this.gameNumber = gameNumber;
	}

	public void setPassword(String passwordOfUsername) {
		ResultSet rs = databaseController
				.query("select wachtwoord from account where naam = '"
						+ passwordOfUsername + "'");
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
				.query("select wachtwoord from account where naam = '"
						+ passwordOfUsername + "'");
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
		String query = "SELECT sum(score) AS totaalScore FROM beurt WHERE account_naam = '"+this.getChallengerName()+"' AND spel_id ="
				+ this.getGameNumber() + " AND id <= " + this.getTurnNumber();
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
		String query = "SELECT sum(score) AS totaalScore FROM beurt WHERE account_naam = '"
				+ this.getOpponentName()
				+ "' AND spel_id = "
<<<<<<< HEAD
				+ this.getGameNumber() + " AND id < " + this.getTurnNumber();
=======
				+ this.getGameNumber() + " AND id <= " + this.getTurnNumber();
>>>>>>> refs/remotes/origin/master
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
				.query("SELECT account_naam_uitdager,account_naam_tegenstander  FROM spel WHERE id = "
						+ this.getGameNumber());
		try {
			while (rs.next()) {
				challenger = rs.getString("account_naam_uitdager");
				opponent = rs.getString("account_naam_tegenstander");
			}
			databaseController.closeConnection();
		} catch (Exception e) {
			// TODO: handle exception
		}
		if (challenger.equals(this.getUsername())) {
			this.challenger = challenger;
			this.opponent = opponent;
			return opponent;
		} else {
			this.challenger = opponent;
			this.opponent = challenger;
			return challenger;
		}
	}

	public String getChallengerName() {
		this.getOpponentName();
		return challenger;
	}

	public String getPlayerTurn() {
		String player = "";
		ResultSet rs = databaseController
				.query("SELECT account_naam FROM beurt WHERE spel_id = "
						+ this.getGameNumber() + " AND id = "
						+ this.getTurnNumber());
		try {
			while (rs.next()) {
				player = rs.getString("account_naam");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (player.equals(this.getChallengerName())) {
			return this.getOpponentName();
		}else{
			return this.getChallengerName();
		}
	}
	// Note: turnNumber is the max(turnNumber), so it's actually the number of the last turn(not the current turn)
	public void setTurnNumber(int turnNumber) {
		if (turnNumber > 1) {
			this.turnNumber = turnNumber;
		}

	}

	public int getCompetitionNumber() {
		return competitionNumber;
	}

	public String getCompetitionDescription() {
		String description = "";
		ResultSet rs = databaseController
				.query("SELECT omschrijving FROM competitie WHERE id = "
						+ competitionNumber);
		try {
			while (rs.next()) {
				description = rs.getString("omschrijving");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return description;
	}

	public void setCompetitionNumber(int competitionNumber) {
		this.competitionNumber = competitionNumber;
	}

	public int getAmountOfRoles(String nameUser) {
		ResultSet rs = databaseController
				.query("select * from accountrol where account_naam = '"
						+ nameUser + "'");
		amountOfRoles = 0;
		try {
			while (rs.next()) {
				amountOfRoles = amountOfRoles + 1;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return amountOfRoles;
	}

	public boolean userCanPlay() {
		if (this.getPlayerTurn().equals(this.getUsername())) {
			return true;
		} else {
			return false;
		}
	}
<<<<<<< HEAD

=======
	//TODO Isn't this the same as the turnNumber?
>>>>>>> refs/remotes/origin/master
	public int getMaxTurnNumber() {
		ResultSet rs = databaseController
				.query("SELECT max(id) FROM beurt WHERE spel_id = "
						+ gameNumber);
		int maxTurn = turnNumber;
		try {
			while (rs.next()) {
				maxTurn = rs.getInt("max(id)");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return maxTurn;
	}

	public String getWinner() {
		if (this.getOpponentScore() > this.getUserScore()) {
			return this.getOpponentName();
<<<<<<< HEAD
		} else {
=======
		}else if(this.getOpponentScore() == this.getUserScore()){
			return this.getOpponentName();
		}else {
>>>>>>> refs/remotes/origin/master
			return this.getChallengerName();
		}
	}
}