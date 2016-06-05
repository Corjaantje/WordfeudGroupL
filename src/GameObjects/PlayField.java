package GameObjects;

import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import Gamestate.GamestateManager;
import Main.Drawable;
import controller.DatabaseController;

@SuppressWarnings("serial")
public class PlayField implements Drawable {

	private int x;
	private int y;
	
	private DatabaseController db_c;
	
	private GamestateManager gsm;
	
	private int fieldWidth;

	private ArrayList<Tile> tiles;
	
	private ArrayList<Letter> playedLetters;
	
	private boolean isCreated = false;

	public PlayField(DatabaseController db_c,GamestateManager gsm) {
		this.db_c = db_c;
		this.gsm = gsm;
		this.createTiles();
		this.createPlayedLetters();
		isCreated = true;
	}

	@Override
	public void draw(Graphics2D g) {
		if(isCreated){
			this.drawTiles(g);
		}
	}

	@Override
	public void update() {
		if(isCreated){
			this.updateTiles();
		}
	}
	
	private void updateTiles(){
		for (Tile tile : tiles) {
			tile.update();
		}
	}

	private void drawTiles(Graphics2D g) {
		for (Tile tile : tiles) {
			tile.draw(g);
		}
		for (Letter letter : playedLetters) {
			letter.draw(g);
		}
	}
	
	public int getX(){
		return x;
	}
	
	public int getFieldWidth(){
		return fieldWidth;
	}
	
	public ArrayList<Tile> getTiles(){
		return tiles;
	}
	
	public ArrayList<Letter> getPlayedLetters(){
		return playedLetters;
	}
	
	private void createPlayedLetters(){
		playedLetters = new ArrayList<Letter>();
		int size = tiles.get(0).getHeight();
		int space = 2;
		int turn = gsm.getUser().getTurnNumber();
		int game = gsm.getUser().getGameNumber();
		ResultSet rs = db_c.query("SELECT * FROM gelegdeletter AS gl INNER JOIN letter AS l ON gl.letter_id = l.id INNER JOIN lettertype AS lt ON lt.karakter = l.lettertype_karakter WHERE gl.spel_id = "+game+" AND l.spel_id = "+game+" AND letterset_code = 'NL' AND gl.beurt_id <= "+turn+";");
		try {
			while (rs.next()) {
				int letterX = rs.getInt("tegel_x");
				int letterY = rs.getInt("tegel_y");
				String letterType = rs.getString("lettertype_karakter");
				if (letterType.equals("?")) {
					letterType = rs.getString("blancoletterkarakter");
				}
				int score = rs.getInt("waarde");
				Letter letter = new Letter(x + (letterX * (size + space))-36, y + (letterY * (size + space))-25, size, size,letterType,score);
				letter.setLetterID(rs.getInt("letter_id"));
				letter.setBordX(letterX);
				letter.setBordY(letterY);
				letter.setPlayfieldX(x);
				
				playedLetters.add(letter);
				
				for (Tile tile:tiles) {
					if (tile.getX() == x + (letterX * (size + space))-36) {
						if (tile.getY() == y + (letterY * (size + space))-25) {
							tile.setLetter(letter);
						}
					}
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Something went wrong with placing the letters: " +e.getMessage());
		}
		db_c.closeConnection();
	}
	
	private void createTiles(){
		tiles = new ArrayList<Tile>();
		int size = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 22.5);
		int space = 2;
		x = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2)-(15 * (size + space)) / 2;
		y = 0;
		fieldWidth = (size * 16);
		ResultSet rs = db_c.query("SELECT * FROM tegel ORDER BY x,y ASC");
		try {
			while (rs.next()) {
				int j = Integer.parseInt(rs.getString(1));
				int i = Integer.parseInt(rs.getString(2));
				String score = rs.getString(4);
				Tile tile = new Tile(x + (j * (size + space))-36, y + (i * (size + space))-25, size, size, score);
				tile.setBordX(j);
				tile.setBordY(i);
				tiles.add(tile);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Something went wrong on the PlayField: "+e.getMessage());
		}
		db_c.closeConnection();
	}

	public void reloadPlayfield(){
		isCreated = false;
		playedLetters.clear();
		this.createPlayedLetters();
		isCreated = true;
	}
	
	public void moveForward(){
		int size = tiles.get(0).getHeight();
		int space = 2;
		String query = "SELECT * FROM gelegdeletter AS gl INNER JOIN letter AS l ON gl.letter_id = l.id INNER JOIN lettertype AS lt ON lt.karakter = l.lettertype_karakter WHERE gl.spel_id = "+gsm.getUser().getGameNumber()+" AND letterset_code = 'NL' AND gl.beurt_id = "+gsm.getUser().getTurnNumber();
		ResultSet rs = db_c.query(query);
		try {
			while(rs.next()){
				int letterX = rs.getInt("tegel_x");
				int letterY = rs.getInt("tegel_y");
				String letterType = rs.getString("lettertype_karakter");
				if (letterType.equals("?")) {
					letterType = rs.getString("blancoletterkarakter");
				}
				int score = rs.getInt("waarde");
				Letter letter = new Letter(x + (letterX * (size + space))-36, y + (letterY * (size + space))-25, size, size,letterType,score);
				letter.setBordX(letterX);
				letter.setBordY(letterY);
				letter.setPlayfieldX(x);
				
				playedLetters.add(letter);
				
				for (Tile tile:tiles) {
					if (tile.getX() == x + (letterX * (size + space))-36) {
						if (tile.getY() == y + (letterY * (size + space))-25) {
							tile.setLetter(letter);
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void moveBackwards(){
		String query = "SELECT * FROM gelegdeletter AS gl INNER JOIN letter AS l ON gl.letter_id = l.id INNER JOIN lettertype AS lt ON lt.karakter = l.lettertype_karakter WHERE gl.spel_id = "+gsm.getUser().getGameNumber()+" AND letterset_code = 'NL' AND gl.beurt_id = "+gsm.getUser().getTurnNumber();
		ResultSet rs = db_c.query(query);
		ArrayList<Letter> removedLetters = new ArrayList<Letter>();
		try {
			while(rs.next()){
				int letterNumber = rs.getInt("letter_id");
				for (Letter letter : playedLetters) {
					if (letter.getLetterID() == letterNumber) {
						removedLetters.add(letter);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		for (Letter letter : removedLetters) {
			playedLetters.remove(letter);
		}
	}
}
