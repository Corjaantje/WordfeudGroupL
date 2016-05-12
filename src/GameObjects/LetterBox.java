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
			turn = gsm.getUser().getTurnNumber();//strange!!
		} else {
			turn = gsm.getUser().getTurnNumber();//strange!!
		}
		String query = "SELECT * FROM plankje WHERE spel_id = "+game+" AND beurt_id = "+turn;
		ResultSet rs = db_c.query(query);
		String letterboxLetters = "";
		try {
			while (rs.next()) {
				letterboxLetters = rs.getString("inhoud");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Something went wrong in the LetterBox: " + e.getMessage());
		}
		db_c.closeConnection();
		letterboxLetters = letterboxLetters.replaceAll(",", "");
		for (int i = 0; i < letterboxLetters.length(); i++) {
			double x = tiles.get(i).getX();
			double y = tiles.get(i).getY();
			int width = tiles.get(i).getWidth();
			int height = tiles.get(i).getHeight();
			String letterSign = "" + letterboxLetters.charAt(i);
			int score = -1;
			Letter letter = new Letter(x, y, width, height, letterSign, score);
			letters.add(letter);
			tiles.get(i).setLetter(letter);
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
