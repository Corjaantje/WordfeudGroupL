package Gamestate;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JOptionPane;

import GameObjects.Button;
import GameObjects.ButtonPanel;
import GameObjects.Chat;
import GameObjects.InfoPanel;
import GameObjects.Letter;
import GameObjects.LetterBox;
import GameObjects.PlayField;
import GameObjects.SwapFrame;
import GameObjects.Tile;
import GameObjects.TurnIndicator;
import Main.GUI;
import controller.DatabaseController;
import controller.PlaystateController;

@SuppressWarnings("serial")
public class Playstate extends Gamestate implements MouseListener {

	private Letter moveLetter;

	private TurnIndicator turnIndicator;

	private PlayField playField;

	private LetterBox letterBox;

	private ButtonPanel buttonPanel;

	private InfoPanel infoPanel;

	private Chat chatArea;

	private ArrayList<Tile> filledTiles;

	private SwapFrame swapFrame;

	private boolean isCreated = false;

	private PlaystateController playstateController;

	private boolean indicatorIsPlaced = false;

	public Playstate(GamestateManager gsm, DatabaseController db_c) {
		super(gsm, db_c);
	}

	@Override
	public void draw(Graphics2D g) {
		if (isCreated) {
			playField.draw(g);
			letterBox.draw(g);
			buttonPanel.draw(g);
			infoPanel.draw(g);
			turnIndicator.draw(g);
		}
	}

	@Override
	public void update() {
		if (isCreated) {
			letterBox.update();
			this.placeIndicator();
		}
	}

	@Override
	public void create() {
		if (!isCreated) {
			this.setLayout(new BorderLayout());
			playField = new PlayField(db_c, gsm);
			letterBox = new LetterBox(playField.getX(), playField.getFieldWidth(), db_c, gsm, "marijntje42");
			buttonPanel = new ButtonPanel(playField.getX(), letterBox.getEndY(), playField.getFieldWidth(), 50);
			int height = (int) (GUI.HEIGHT - buttonPanel.getEndY());
			infoPanel = new InfoPanel(playField.getX(), buttonPanel.getEndY(), playField.getFieldWidth(), height, db_c,
					gsm);
			this.addMouseListener(this);

			swapFrame = new SwapFrame(letterBox, db_c, gsm);
			chatArea = new Chat(db_c, gsm);
			this.add(chatArea, BorderLayout.EAST);
			filledTiles = new ArrayList<Tile>();
			playstateController = new PlaystateController(gsm, playField, letterBox, this);
			turnIndicator = new TurnIndicator(gsm, playField.getTiles().get(0).getWidth());
			isCreated = true;
		} else {
			this.reloadPlaystate();
		}
	}

	public void reloadPlaystate() {
		playField.reloadPlayfield();
		letterBox.reloadLetterBox();
		infoPanel.reloadInfoPanel();
		swapFrame.reloadSwapFrame();
		filledTiles.clear();
		chatArea.reloadChat();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		buttonPanel.mousePressed(e);
		this.letterPressed(e);
		this.buttonPushed(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		buttonPanel.mouseReleased(e);
		this.letterReleased(e);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	private void letterReleased(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		try {
			if (moveLetter != null) {
				if (moveLetter.getRightLocation()) {
					for (Tile tile : playField.getTiles()) {
						if (tile.getIsEmpty()) {
							if (x > tile.getX() && x < (tile.getX() + tile.getWidth())) {
								if (y > tile.getY() && y < (tile.getY() + tile.getHeight())) {
									boolean tileIsFilled = false;
									for (Tile filledTile : filledTiles) {
										if (filledTile.getX() == tile.getX()) {
											if (filledTile.getY() == tile.getY()) {
												tileIsFilled = true;
											}
										}
									}
									if (!tileIsFilled && moveLetter.getX() != tile.getX()
											&& moveLetter.getBordY() != tile.getY()) {
										filledTiles.add(tile);
										indicatorIsPlaced = true;
										moveLetter.setBordX(tile.getBordX());
										moveLetter.setBordY(tile.getBordY());
										moveLetter.calculateRoute(tile.getX(), tile.getY());
										moveLetter.setWantedSize(tile.getWidth(), tile.getHeight());
										if (moveLetter.getLetterChar().equals("?")) {
											String option = JOptionPane
													.showInputDialog("Vul hier de gewenste letter in: ");
											if (option.length() == 1 && Character.isLetter(option.charAt(0))) {
												moveLetter.setLetterChar(option.toUpperCase());
											} else {
												JOptionPane.showMessageDialog(null,
														"U heeft geen geldige letter ingevuld!", "Wordfeud",
														JOptionPane.ERROR_MESSAGE);
												moveLetter.reset();
											}
										}
									}
								}
							}
						}
					}
					for (Tile tile : letterBox.getTiles()) {
						if (x > tile.getX() && x < (tile.getX() + tile.getWidth())) {
							if (y > tile.getY() && y < (tile.getY() + tile.getHeight())) {
								moveLetter.reset();
								if (!filledTiles.isEmpty()) {
									filledTiles.remove(0);
								}
							}
						}
					}
				}
			}
		} catch (Exception e2) {
			// TODO: handle exception
			e2.printStackTrace();
		}
	}

	private void letterPressed(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		for (Letter letter : letterBox.getLetters()) {
			if (x > letter.getX() && x < (letter.getX() + letter.getWidth())) {
				if (y > letter.getY() && y < (letter.getY() + letter.getHeight())) {
					// checks if the selected letter lays on the playfield, if
					// that is correct the tile will be deleted from filledTiles
					for (Tile tile : playField.getTiles()) {
						int xa = (int) tile.getX();
						int ya = (int) tile.getY();
						if (xa + 5 > letter.getX() && xa - 5 < letter.getX()) {
							if (ya + 5 > letter.getY() && ya - 5 < letter.getY()) {
								filledTiles.remove(tile);
							}
						}
					}
					moveLetter = letter;
				}
			}
		}

	}

	private void buttonPushed(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		for (Button button : buttonPanel.getButtons()) {
			if (buttonPanel.getButtonsAreSelected() && x > button.getX() && x < (button.getX() + button.getWidth())) {
				if (buttonPanel.getButtonsAreSelected() && y > button.getY()
						&& y < (button.getY() + button.getHeight())) {
					this.playSound("ButtonClick.wav");
					if (gsm.getUser().userCanPlay()) {

						if (button.getText().equals("Reset")) {
							this.resetLetterBoxLetters();
						} else if (button.getText().equals("Shuffle")) {
							letterBox.shuffleLetters();
						} else if (button.getText().equals("Play")) {
							// this.checkCorrectPlacedLetters();
							playstateController.doPlay();
						} else if (button.getText().equals("Swap")) {
							swapFrame.setVisible(true);
						} else if (button.getText().equals("Pass")) {
							this.doPass();
						} else if (button.getText().equals("Resign")) {
							this.doResign();
						}
					} else {
						if (button.getText().equals("Reset")) {
							this.resetLetterBoxLetters();
						} else {
							JOptionPane.showMessageDialog(null, "De beurt is aan: " + gsm.getUser().getPlayerTurn(),
									"Wordfeud", JOptionPane.ERROR_MESSAGE);
						}

					}
				}
			}
		}
	}

	private void placeIndicator() {
		if (indicatorIsPlaced && moveLetter.getRightLocation()) {
			this.playSound("LetterDrop.wav");
			playstateController.setScoreTrackingVariables();
			int score = playstateController.getScore();
			// check if the word is on a wrong location
			if (score == -1) {
				indicatorIsPlaced = false;
				System.out.println("The letters are not placed correctly! score = " + score);
				return;
			}
			ArrayList<Letter> wordLetters = playstateController.getMainWord();
			if (playstateController.getMainWordOrientation().equals("horizontal")) {
				// Order the letters reversed
				Collections.sort(wordLetters, new Comparator<Letter>() {
					@Override
					public int compare(Letter a, Letter b) {
						if (a.getBordX() < b.getBordX())
							return 1;
						if (a.getBordX() > b.getBordX())
							return -1;
						return 0;
					}
				});
				System.out.println("Horizontal word ordered");
				turnIndicator.setToPoint(new Point((int) wordLetters.get(0).getX(), (int) wordLetters.get(0).getY()));
				turnIndicator.setScore(score);
			} else if (playstateController.getMainWordOrientation().equals("vertical")) {
				// Order the letters reversed
				Collections.sort(wordLetters, new Comparator<Letter>() {
					@Override
					public int compare(Letter a, Letter b) {
						if (a.getBordY() < b.getBordY())
							return 1;
						if (a.getBordY() > b.getBordY())
							return -1;
						return 0;
					}
				});
				System.out.println("Vertical word ordered");
				turnIndicator.setToPoint(new Point((int) wordLetters.get(0).getX(), (int) wordLetters.get(0).getY()));
				turnIndicator.setScore(score);
			} else {
				System.out.println("Word Orientation went wrong");
			}
			indicatorIsPlaced = false;
		}
	}

	private void doPass() {
		int option = JOptionPane.showConfirmDialog(this, "Weet je zeker dat je deze beurt wilt passen?", "Wordfeud",
				JOptionPane.YES_NO_OPTION);
		if (option == JOptionPane.YES_OPTION) {
			int turn = gsm.getUser().getTurnNumber();
			int game = gsm.getUser().getGameNumber();
			String username = gsm.getUser().getUsername();
			db_c.query("INSERT INTO beurt VALUES (" + turn + ", " + game + ",'" + username + "'," + 0 + ", 'pass');");
			db_c.closeConnection();
		}
	}

	private void doResign() {
		int option = JOptionPane.showConfirmDialog(this, "Weet je zeker dat je wil opgeven?", "Wordfeud",
				JOptionPane.YES_NO_OPTION);
		if (option == JOptionPane.YES_OPTION) {
			int turn = gsm.getUser().getTurnNumber();
			int game = gsm.getUser().getGameNumber();
			String username = gsm.getUser().getUsername();
			db_c.query("INSERT INTO beurt VALUES (" + turn + ", " + game + ",'" + username + "'," + 0 + ", 'resign');");
			db_c.closeConnection(); // TODO waarom de connection closen? - Marc
			// TODO set end of game
		}
	}

	private void resetLetterBoxLetters() {
		for (Letter letter : letterBox.getLetters()) {
			letter.setBordX(0);
			letter.setBordY(0);
			letter.reset();
			if (letter.getIsJoker()) {
				letter.setLetterChar("?");
			}
		}
		filledTiles.clear();
		turnIndicator.resetTurnIndicator();
		moveLetter = null;
	}

	private void checkCorrectPlacedLetters() {
		int counter = 0;
		boolean isWrongTurn = false;
		ArrayList<Letter> letters = new ArrayList<Letter>();
		for (Letter letter : letterBox.getLetters()) {
			if (!letter.isOnStartPosition()) {
				counter++;
				letters.add(letter);
				// check for every letter that isn't on start position on which
				// tile they are
				for (Tile tile : playField.getTiles()) {
					if (letter.getX() - 5 < tile.getX() && letter.getX() + 5 > tile.getX()) {
						if (letter.getY() - 5 < tile.getY() && letter.getY() + 5 > tile.getY()) {
							letter.setBordX(tile.getBordX());
							letter.setBordY(tile.getBordY());
						}
					}
				}
			}
		}
		// if all letters are on start position give warning
		if (counter == 0) {
			isWrongTurn = true;
		}
		if (counter == 1) {
			// only one letter is placed so dont need to check for axis

			// check if the one letter is placed right
			return;
		}
		if (isWrongTurn) {
			JOptionPane.showMessageDialog(this, "U letter(s) staan op een ongeldige locatie!",
					"Letter(s) op een ongeldige locatie!", JOptionPane.ERROR_MESSAGE);
		} else {
			this.checkForAxis(letters);
		}
	}

	private void checkForAxis(ArrayList<Letter> letters) {
		boolean isVerticalLayed = true;
		boolean isHorizontalLayed = true;
		ArrayList<Integer> letterX = new ArrayList<Integer>();
		ArrayList<Integer> letterY = new ArrayList<Integer>();
		for (Letter letter : letters) {
			letterX.add(letter.getBordX());
			letterY.add(letter.getBordY());
			System.out.println("Letter = " + letter.getLetterChar() + " X waarde = " + letter.getBordX()
					+ " Y waarde = " + letter.getBordY());
		}
		// test
		int sequenceX = letters.get(0).getBordX();
		int sequenceY = letters.get(0).getBordY();
		// checks if all horizontal letters are the same.
		for (int i = 0; i < letterX.size(); i++) {
			if (i > 0 && sequenceX != letterX.get(i)) {
				isHorizontalLayed = false;
			}
		}
		// checks if all vertical letters are the same.
		for (int i = 0; i < letterY.size(); i++) {
			if (i > 0 && sequenceY != letterY.get(i)) {
				isVerticalLayed = false;
			}
		}
		if (isHorizontalLayed && !isVerticalLayed) {
			this.checkYAxis(letters, letterX, sequenceY);
		} else if (isVerticalLayed && !isHorizontalLayed) {
			this.checkXAxis(letters, letterX, sequenceY);
		} else {
			// wrong placed letters
		}
	}

	private void checkYAxis(ArrayList<Letter> letters, ArrayList<Integer> letterY, int sequenceX) {
		// orders integer array
		Arrays.sort(letterY.toArray());
		// all loops are for checking sequence
		ArrayList<Letter> placedLetters = new ArrayList<Letter>();
		// add all letters on the right x-ax
		placedLetters.addAll(letters);
		for (Letter placedLetter : playField.getPlayedLetters()) {
			if (sequenceX == placedLetter.getBordX()) {
				placedLetters.add(placedLetter);
			}
		}
		// order all placed letters on their x position
		Collections.sort(placedLetters, new Comparator<Letter>() {
			@Override
			public int compare(Letter z1, Letter z2) {
				if (z1.getBordY() > z2.getBordY())
					return 1;
				if (z1.getBordY() < z2.getBordY())
					return -1;
				return 0;
			}
		});
		// check in which sequence the placed letters are
		Letter previousGood = new Letter(0, 0, 0, 0, "", -1);
		ArrayList<Letter> sequence = new ArrayList<Letter>();
		for (Letter letter : placedLetters) {
			for (Letter letter2 : letters) {
				if (letter.getBordY() == letter2.getBordY()) {
					sequence.add(letter);
					previousGood = letter;
					break;
				} else if (letter.getBordY() == letter2.getBordY() - 1 || letter.getBordY() == letter2.getBordY() + 1) {
					sequence.add(letter);
					previousGood = letter;
					break;
				} else if (letter.getBordY() == previousGood.getBordY() - 1
						|| letter.getBordY() == previousGood.getBordY() + 1) {
					sequence.add(letter);
					previousGood = letter;
					break;
				} else {
					System.out.println("Wrong");
				}
			}
		}
		// last sort to get the right sequence order
		Collections.sort(sequence, new Comparator<Letter>() {
			@Override
			public int compare(Letter z1, Letter z2) {
				if (z1.getBordY() > z2.getBordY())
					return 1;
				if (z1.getBordY() < z2.getBordY())
					return -1;
				return 0;
			}
		});
		String word = "";
		for (Letter letter : sequence) {
			word.concat(letter.getLetterChar());
		}
		JOptionPane.showMessageDialog(this, "U heeft '" + word + "' gespeeld");
	}

	private void checkXAxis(ArrayList<Letter> letters, ArrayList<Integer> letterX, int sequenceY) {
		// orders integer array
		Arrays.sort(letterX.toArray());
		// all loops are for checking sequence
		ArrayList<Letter> placedLetters = new ArrayList<Letter>();
		// add all letters on the right x-ax
		placedLetters.addAll(letters);
		for (Letter placedLetter : playField.getPlayedLetters()) {
			if (sequenceY == placedLetter.getBordY()) {
				placedLetters.add(placedLetter);
			}
		}
		// order all placed letters on their x position
		Collections.sort(placedLetters, new Comparator<Letter>() {
			@Override
			public int compare(Letter z1, Letter z2) {
				if (z1.getBordX() > z2.getBordX())
					return 1;
				if (z1.getBordX() < z2.getBordX())
					return -1;
				return 0;
			}
		});
		// check in which sequence the placed letters are

		// PROBLEM: only 3 letters can be selected
		Letter previousGood = new Letter(0, 0, 0, 0, "", -1);
		ArrayList<Letter> sequence = new ArrayList<Letter>();
		for (Letter letter : placedLetters) {
			for (Letter letter2 : letters) {
				if (!sequence.contains(letter)) {
					if (letter.getBordX() == letter2.getBordX()) {
						sequence.add(letter);
						previousGood = letter;
						break;
					} else if (letter.getBordX() == letter2.getBordX() - 1
							|| letter.getBordX() == letter2.getBordX() + 1) {
						sequence.add(letter);
						previousGood = letter;
						break;
					} else if (letter.getBordX() == previousGood.getBordX() - 1
							|| letter.getBordX() == previousGood.getBordX() + 1) {
						sequence.add(letter);
						previousGood = letter;
						break;
					} else {

					}
				}
			}
		}
		// last sort to get the right sequence order
		Collections.sort(sequence, new Comparator<Letter>() {
			@Override
			public int compare(Letter z1, Letter z2) {
				if (z1.getBordX() > z2.getBordX())
					return 1;
				if (z1.getBordX() < z2.getBordX())
					return -1;
				return 0;
			}
		});
		String word = "";
		for (Letter letter : sequence) {
			word = word.concat(word + letter.getLetterChar());
		}
		JOptionPane.showMessageDialog(this, "U heeft '" + word + "' gespeeld");
	}

	private void playSound(String url) {
		File f = new File("Resources/Sound/" + url);
		try {
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(f.getAbsoluteFile());
			try {
				Clip clip = AudioSystem.getClip();
				clip.open(audioInputStream);
				clip.start();
			} catch (LineUnavailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}