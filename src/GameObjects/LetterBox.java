package GameObjects;

import java.awt.Font;
import java.awt.Graphics2D;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import Gamestate.GamestateManager;
import Main.Drawable;
import controller.DatabaseController;

public class LetterBox implements Drawable {

	private DatabaseController db_c;

	private GamestateManager gsm;

	private ArrayList<Letter> letters;

	private ArrayList<Tile> tiles;
	private int tileDiameter;
	private int startX;
	private int startY;

	private boolean isCreated = false;

	private String player;

	public LetterBox(int x, int fieldWidth, DatabaseController db_c, GamestateManager gsm, String player) {
		this.db_c = db_c;
		this.gsm = gsm;
		tileDiameter = fieldWidth / 7;
		startY = fieldWidth + 5;
		startX = x;
		this.player = player;
		this.createTiles();
		this.createLetters();
		isCreated = true;
	}

	private void createTiles() {
		tiles = new ArrayList<Tile>();
		for (int i = 0; i < 7; i++) {
			tiles.add(new Tile(startX, startY, tileDiameter, tileDiameter, "--"));
			startX += tileDiameter;
		}
	}

	private void createLetters() {
		letters = new ArrayList<Letter>();
		// letterset_code, spel_id en beurt_id moeten naar de juiste waarde
		// worden gezet
		int turn = 0;
		int game = gsm.getUser().getGameNumber();
		if (player.equals(gsm.getUser().getPlayerTurn())) {
			turn = gsm.getUser().getTurnNumber()-2;//strange!!
		} else {
			turn = gsm.getUser().getTurnNumber()-3;//strange!!
		}
		String query = "SELECT *  FROM letterbakjeletter AS lb  INNER JOIN letter AS l  ON l.id = lb.letter_id  INNER JOIN lettertype AS lt  ON lt.karakter = l.lettertype_karakter  INNER JOIN beurt AS b ON b.id = lb.beurt_id WHERE lb.spel_id = "
				+ game + " AND lb.beurt_id = " + turn + " AND letterset_code = 'NL';";
		ResultSet rs = db_c.query(query);
		try {
			int i = 0;
			while (rs.next()) {
				if (i == 7) {
					break;
				}
				System.out.println(i);
				double x = tiles.get(i).getX();
				double y = tiles.get(i).getY();
				int width = tiles.get(i).getWidth();
				int height = tiles.get(i).getHeight();
				String letterSign = rs.getString("karakter");
				int score = rs.getInt("waarde");
				Letter letter = new Letter(x, y, width, height, letterSign, score);
				letter.setLetterID(rs.getInt("letter_id"));
				letters.add(letter);
				tiles.get(i).setLetter(letter);
				i++;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Something went wrong in the LetterBox: " + e.getMessage());
		}
		db_c.closeConnection();
	}

	public void replacePlacedLetters(ArrayList<Letter> placedLetters)
	{
		int numberOfPlacedLetters = placedLetters.size();
		
		// get all unused letters (not on field not in player's hands)
		// ( letter is not in gelegdeletter and letter is not in a letterbakje letter with last two turns
		String query = "SELECT * FROM letter WHERE NOT id = ANY( SELECT letter_id FROM gelegdeletter WHERE beurt_id <= " + gsm.getUser().getTurnNumber() + " AND spel_id = " + gsm.getUser().getGameNumber() + ") AND NOT id = ANY(";
		// randomly take numberOfPlacedLetters unused letters
	}
	
	@Override
	public void draw(Graphics2D g) {
		if (isCreated) {
			drawTiles(g);
		}
	}

	@Override
	public void update() {
		if (isCreated) {
			updateLetters();
		}
	}

	private void updateLetters() {
		for (Letter letter : letters) {
			letter.update();
		}
	}

	private void drawTiles(Graphics2D g) {
		for (Tile tile : tiles) {
			tile.draw(g);
		}
		for (Letter letter : letters) {
			letter.draw(g);
		}
	}

	public int getEndY() {
		return startY + tileDiameter;
	}

	public ArrayList<Letter> getLetters() {
		return letters;
	}

	public ArrayList<Tile> getTiles() {
		return tiles;
	}

	public void shuffleLetters() {
		letters.clear();
		long seed = System.nanoTime();
		Collections.shuffle(tiles, new Random(seed));
		createLetters();
	}

	public void reloadLetterBox() {
		isCreated = false;
		letters.clear();
		this.createLetters();
		isCreated = true;
	}
}
