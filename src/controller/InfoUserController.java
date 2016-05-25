package controller;

import java.sql.ResultSet;
import java.sql.SQLException;

import Gamestate.GamestateManager;

public class InfoUserController {
	private final String errorNotificationNotFoundUsername = "Not found username for this user"; 
	private GamestateManager gsm;
	
	public InfoUserController(GamestateManager gsm){
		this.gsm = gsm;
	}
	
	public String getUsername(String username){
		ResultSet rs = gsm.getDatabaseController().query("select * from account where naam = '" + username + "'");
		try {
			if(rs.next()){
				return username;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return errorNotificationNotFoundUsername;
	}
	
	public void setPassword(String username, String password){
		ResultSet rs = gsm.getDatabaseController().query("select * from account where naam = '" + username + "'");
		try {
			if(rs.next()){
				gsm.getDatabaseController().queryUpdate("update account set wachtwoord = '" + password + "' where naam = '" + username + "'");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		return errorNotificationNotFoundUsername;
	}
}
