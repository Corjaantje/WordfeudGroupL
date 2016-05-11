package controller;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import model.User;
import Gamestate.GamestateManager;

public class CompetitionController {
	
	private GamestateManager gsm;
	private DatabaseController databaseController;
	
	private DefaultTableModel model;
	private JTable table;
	
	public CompetitionController(GamestateManager gsm){
		this.gsm = gsm;
		databaseController = gsm.getDatabaseController();
	}
	
	public void addCompetition(String omschrijving){
		
		int competitieID = 0;
		ResultSet rs = databaseController.query("select max(id) from competitie");
		try{
			while(rs.next()){
				competitieID = rs.getInt("max(id)");
				competitieID++;
			}
				if(omschrijving.length() < 5 || omschrijving.length() > 25){
					JOptionPane.showMessageDialog(null, "Je hebt te weinig/ veel karakters gebruikt probeer opnieuw.");
					return;
				}
				else{
					ResultSet eigenaarRS =databaseController.query("select * from competitie");
					while(eigenaarRS.next()){
						if(eigenaarRS.getString("account_naam_eigenaar").equals(gsm.getUser().getUsername())){
							JOptionPane.showMessageDialog(null, "Deze gebruiker is al eigenaar.");
							return;
						}
						else{
							if(eigenaarRS.getString("omschrijving").equals(omschrijving)){
								
							}
						}
						
						
					}
				}
				databaseController.queryUpdate("INSERT INTO competitie VALUES ("+competitieID+",'" +omschrijving+"','"+gsm.getUser().getUsername()+"' ) ");
				JOptionPane.showMessageDialog(null, "Je hebt een nieuwe competitie gemaakt !");
			
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public void joinCompetition(String competitionName){
		ResultSet rs = databaseController.query("select * from competitie where omschrijving = '"+ competitionName +"' ");
		if(rs.equals(competitionName)){
			ResultSet playerRs = databaseController.query("select "+gsm.getUser().getUsername()+" from deelnemer as d join competitie as c on d.competitie_id=c.id where omschrijving = "+ competitionName+"");
			if(playerRs.equals(gsm.getUser().getUsername())){
				JOptionPane.showMessageDialog(null, "Je zit al in deze competitie !");
			}
			else{
				int competitieID = 0;
				try {
					competitieID = rs.getInt("id");
					databaseController.queryUpdate("INSERT INTO deelnemer VALUES '"+gsm.getUser().getUsername()+"',"+ competitieID+"");
					JOptionPane.showMessageDialog(null, gsm.getUser().getUsername()+ " heeft " + competitionName +" gejoined !" );
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		}
		else{
			JOptionPane.showMessageDialog(null, "Deze competitie bestaat niet !");
			return;
		}
	}
	
	public void seeCurrentUsers(int id){
		ResultSet rs = databaseController.query("select * from deelnemer where competitie_id =" + id +" ORDER BY  account_naam DESC");
		try {
			while(rs.next()){
				String user = rs.getString("account_naam");
			}
			if(!rs.next()){
				JOptionPane.showMessageDialog(null, "Er zijn geen users !");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	public JTable createTable(){
//		String[] header = {"ID","Omschrijving"};
//		model = new DefaultTableModel(header , 0);
//		table = new JTable(model);
//		
//		model.addRow(new Object[] { rs.getString("account_naam")});
//		
//	}
}