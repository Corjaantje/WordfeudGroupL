package controller;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import Gamestate.GamestateManager;

public class AdminDBController {
	private final String errorNotificationNotFoundPassword = "Not found password for this user"; 
	private GamestateManager gsm;
	
	public AdminDBController(GamestateManager gsm){
		this.gsm = gsm;
	}
	
	public void addRole(String inputRole, String username){
		gsm.getDatabaseController().queryUpdate("insert into accountrol values('" + username + "','" + inputRole + "')");
	}
	
	public void removeRole(String inputRole, String username){
		gsm.getDatabaseController().queryUpdate("delete from accountrol where account_naam = '" + username + "' and rol_type = '" + inputRole + "'");
	}
	
	public boolean checkUserIsRole(String username, String inputRole){
		ResultSet rs = gsm.getDatabaseController().query("select * from accountrol where account_naam = '" + username + "' and rol_type = '" + inputRole + "'");
		try
		{
			if(rs.next()){
				return true;
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean checkIfUserExists(String nameUser){
		ResultSet rs = gsm.getDatabaseController().query("select * from account where naam = '" + nameUser + "'");
		try{
			if(rs.next()){
				System.out.println("User exists");
				return true;
			}else{
				JOptionPane.showMessageDialog(null, "Gebruiker bestaat niet. Probeer opnieuw.");
				return false;
			}
		} catch(SQLException e){
			e.printStackTrace();
		}
		return false;
	}
	
	public String getPassword(String passwordOfUsername){
		ResultSet rs = gsm.getDatabaseController().query("select wachtwoord from account where naam = '" + passwordOfUsername + "'");
		try{
			if(rs.next()){
				return rs.getString("wachtwoord");
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		return errorNotificationNotFoundPassword;
	}
}
