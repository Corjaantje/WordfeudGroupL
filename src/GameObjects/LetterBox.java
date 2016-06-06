package GameObjects;

import java.awt.Font;
import java.awt.Graphics2D;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

import javax.swing.JOptionPane;

import Gamestate.GamestateManager;
import Main.Drawable;
import controller.DatabaseController;
import model.User;

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
			tiles.add(new Tile(startX, startY, tileDiameter, tileDiameter, "BOX"));
			startX += tileDiameter;
		}
	}

	private void createLetters() {
		letters = new ArrayList<Letter>();
		// letterset_code, spel_id en beurt_id moeten naar de juiste waarde
		// worden gezet
		int turn = 0;
		int game = gsm.getUser().getGameNumber();
		if (gsm.getUser().getUsername().equals(gsm.getUser().getPlayerTurn())) {
			turn = gsm.getUser().getTurnNumber() - 1;
			System.out.println("player is: " + gsm.getUser().getUsername() + " and turn belongs to: " + gsm.getUser().getPlayerTurn());
		} else {
			turn = gsm.getUser().getTurnNumber();
			System.out.println("player is: " + gsm.getUser().getUsername() + " and turn belongs to: " + gsm.getUser().getPlayerTurn());
		}
		String query = "SELECT *  FROM letterbakjeletter AS lb  INNER JOIN letter AS l  ON l.id = lb.letter_id  INNER JOIN lettertype AS lt  ON lt.karakter = l.lettertype_karakter  INNER JOIN beurt AS b ON b.id = lb.beurt_id WHERE l.spel_id = "
				+ game + " AND lb.beurt_id = " + turn + " AND letterset_code = 'NL' AND lb.spel_id = " + game
				+ " AND b.spel_id = " + game + ";";
		ResultSet rs = db_c.query(query);
		try {
			int i = 0;
			while (rs.next()) {
				if (i == 7) {
					break;
				}
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

	public void replacePlacedLetters(ArrayList<Letter> placedLetters) {
		int desiredNumberOfLetters = placedLetters.size();

		// TODO remove this commented code
		// get all unused letters (not on field not in player's hands)
		// ( letter is not in gelegdeletter and letter is not in a letterbakje
		// letter with last two turns
	//	String query = "SELECT * FROM letter WHERE NOT id = ANY( SELECT letter_id FROM gelegdeletter WHERE beurt_id <= "
	//			+ (gsm.getUser().getTurnNumber()+1) + " AND spel_id = " + gsm.getUser().getGameNumber() + ") "
	//			+ "AND NOT id = ANY( SELECT letter_id from letterbakjeletter where beurt_id ="
	//			+ (gsm.getUser().getTurnNumber()) + " OR beurt_id =" + (gsm.getUser().getTurnNumber() - 1) + ")";
		
		// get all letters from the pot
		String query = "SELECT * FROM pot WHERE spel_id =" + gsm.getUser().getGameNumber();
		ResultSet rSet = db_c.query(query);
		ArrayList<Integer> charNumberList = new ArrayList<Integer>();
		try {
			while (rSet.next())
			{
				charNumberList.add(rSet.getInt("letter_id"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// now randomize the order of the id's in the arraylist
		long seed = System.nanoTime();
		Collections.shuffle(charNumberList, new Random(seed));

		ArrayList<Integer> newLetters = new ArrayList<>();

		// if there's not enough letters available; change the number of letters
		// that gets returned to the maximum number that is available
		if (charNumberList.size() < desiredNumberOfLetters) {
			desiredNumberOfLetters = charNumberList.size();
		}
		// take desiredNumberOfLetters unused letters
		for (int i = 0; i < desiredNumberOfLetters; i++) {
			newLetters.add(charNumberList.get(i));
		}
		
		
		// place the unused letters in the letterbakjeletter again
		// get the unused letter_ids
		ArrayList<Letter> unusedLetters = (ArrayList<Letter>) letters.clone();
		// for every letter in the letterbox check if they have been used (if
		// they are also in the placedLetters arraylist
		for (Letter letter : letters) {
			for (Letter placedLetter : placedLetters) {
				// if they are remove them from the unusedLetters arraylist
				if (letter.equals(placedLetter)) {
					unusedLetters.remove(letter);
				}
			}
		}
		// add the unused letters
		for (Letter letter : unusedLetters) {
			String updateLetterbakjeletterQuery = "INSERT INTO letterbakjeletter (spel_id,letter_id,beurt_id) VALUES ("
					+ gsm.getUser().getGameNumber() + ", " + letter.getLetterID() + ", " + (gsm.getUser().getTurnNumber()+1)
					+ ")";
			db_c.queryUpdate(updateLetterbakjeletterQuery);
		}
		// finally replace the placed letters with the new letters
		for (Integer letter_id : newLetters) {
			String updateLetterbakjeletterQuery = "INSERT INTO letterbakjeletter (spel_id,letter_id,beurt_id) VALUES ("
					+ gsm.getUser().getGameNumber() + ", " + letter_id + ", " + (gsm.getUser().getTurnNumber()+1) + ")";
			db_c.queryUpdate(updateLetterbakjeletterQuery);
		}
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