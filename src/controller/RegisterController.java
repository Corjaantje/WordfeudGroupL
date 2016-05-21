package controller;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import Gamestate.GamestateManager;

public class RegisterController {
	private GamestateManager gsm;
	private DatabaseController databaseController;
	
	public RegisterController(GamestateManager gsm){
		this.gsm = gsm;
		databaseController = gsm.getDatabaseController();
	}

	public void register(String username, String password) {
		ResultSet rs = databaseController.query("select * from account where naam = '" + username + "'");
		try{
			if(rs.next()){
				JOptionPane.showMessageDialog(null, "Deze gebruiksnaam is al in gebruik.");
			}else{
				if((username.length() >= 5) && (username.length() <= 25)){
					if(username.matches("[A-za-z0-9]+")){
						if((password.length() >= 5) && (password.length() <= 25)){
							if(password.matches("[A-za-z0-9]+")){
								databaseController.queryUpdate("INSERT INTO account(naam, wachtwoord) VALUES ('" + username + "', '" + password + "')");
								databaseController.queryUpdate("INSERT INTO accountrol (account_naam, rol_type) VALUES ('" + username + "', 'player')");
								JOptionPane.showMessageDialog(null, "Account is met succes aangemaakt.");
							}else{
								JOptionPane.showMessageDialog(null, "Wachtwoord kan alleen uit letters en cijfers bestaan.");
							}
						}else{
							JOptionPane.showMessageDialog(null, "Wachtwoord moet minimaal 5 karakters zijn en maximaal 25 karakters.");
						}
					}else{
						JOptionPane.showMessageDialog(null, "Gebruiksnaam kan alleen uit letters en cijfers bestaan.");
					}
				}else{
					JOptionPane.showMessageDialog(null, "Gebruiksnaam moet minimaal 5 karakters zijn en maximaal 25 karakters.");
				}
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
}
