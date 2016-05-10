package controller;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import Gamestate.GamestateManager;
import model.User;

public class LoginController {
	
	private String username;
	private String password;	
	private GamestateManager gsm;
	private DatabaseController databaseController;
	
	public LoginController(GamestateManager gsm){
		this.gsm = gsm;
		databaseController = gsm.getDatabaseController();
		username = null;
		password = null;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void login(String username, String password){
		ResultSet rs = databaseController.query("select * from account where naam = '" + username + "'and wachtwoord = '" + password + "'");
		try{
			if(rs.next()){
				if(gsm.getUser() == null){
					System.out.println("Test database query login");
					User user = new User(username);
					gsm.setUser(user);
					gsm.setGamestate(gsm.mainMenuState);

				} else {
					JOptionPane.showMessageDialog(null, "Je bent al ingelogd.");
				}
			}else{
				JOptionPane.showMessageDialog(null, "Gebruikersnaam en wachtwoord komen niet overeen.");
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
}
