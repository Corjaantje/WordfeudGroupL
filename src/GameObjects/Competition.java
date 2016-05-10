package GameObjects;

import java.util.ArrayList;

import model.User;

public class Competition {
	
	private String competitionName;
	private ArrayList<User> users;
	private User eigenaar;
	
	public Competition(String competitionName, User eigenaar){
		users = new ArrayList<User>();
		setCompetitionName(competitionName);
		this.eigenaar = eigenaar;
	}
	
	public void addUser(User user){
		users.add(user);
	}
	
	public void setCompetitionName(String competitionName){
		if(competitionName.length() < 5 || competitionName.length() > 25 ){
			System.out.println("To short for a competition name.");
		}
		else{
			this.competitionName = competitionName;
		}
	}
	
	public void seeCurrentUsers(){
		for(int i = 0; i<users.size(); i++){
			if(users.get(i) == eigenaar){
				System.out.println(users.get(i).getUsername() + " Eigenaar");
			}
			else{
				users.get(i).getUsername();	
			}
			
		}
		
	}
	
}
