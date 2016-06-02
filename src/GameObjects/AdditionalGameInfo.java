package GameObjects;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.sql.ResultSet;
import java.sql.SQLException;

import Gamestate.GamestateManager;
import Main.Drawable;
import controller.DatabaseController;
import model.User;

public class AdditionalGameInfo implements Drawable{
	
	private int x;
	private int y;

	private int width;
	private int height;

	private DatabaseController db_c;
	private GamestateManager gsm;

	private String playerTurn;
	
	private String additionalInfo;
	
	private User user;

	public AdditionalGameInfo(int x, int y, int width, int height, DatabaseController db_c, GamestateManager gsm) {
		this.x = x;
		this.y = y;
		this.height = height;
		this.width = width;
		this.db_c = db_c;
		user = gsm.getUser();
		this.reloadAdditionalInfo();
	}

	@Override
	public void draw(Graphics2D g) {
		g.setColor(Color.gray);
		g.fillRect(x - 1, y + 2, width, height + 5);
		g.setColor(Color.BLACK);
		g.setFont(new Font("Arial", Font.BOLD, 20));
		g.drawString(additionalInfo, x+25, y+(height/2));
	}

	@Override
	public void update() {
	}
	
	private String getPlacedLetters(){
		String query = "SELECT * FROM gelegd WHERE spel_id = "+user.getGameNumber()+" AND beurt_id = "+user.getTurnNumber();
		ResultSet rs = db_c.query(query);
		String letters = "";
		try {
			while(rs.next()){
				letters = rs.getString("woorddeel");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return letters;
	}
	
	private int getTurnScore(){
		String query = "SELECT * FROM beurt WHERE spel_id = "+user.getGameNumber()+" AND id = "+user.getTurnNumber();
		ResultSet rs = db_c.query(query);
		int score = 0;
		try {
			while(rs.next()){
				score = rs.getInt("score");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return score;
	}
	
	public void reloadAdditionalInfo(){
		//user.setTurnNumber(user.getTurnNumber()-1);
		playerTurn = user.getPlayerTurn();
		user.setTurnNumber(user.getTurnNumber()-1);
		additionalInfo = playerTurn+" heeft "+this.getPlacedLetters()+" gespeeld voor "+this.getTurnScore()+" punten.";
		user.setTurnNumber(user.getTurnNumber()+1);
	}

}
