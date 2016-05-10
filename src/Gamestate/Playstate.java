package Gamestate;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

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
import Main.GUI;
import controller.DatabaseController;
import controller.PlaystateController;

@SuppressWarnings("serial")
public class Playstate extends Gamestate implements MouseListener {

	private Letter moveLetter;

	private PlayField playField;

	private LetterBox letterBox;

	private ButtonPanel buttonPanel;

	private InfoPanel infoPanel;

	private Chat chatArea;

	private ArrayList<Tile> filledTiles;

	private SwapFrame swapFrame;

	private boolean isCreated = false;

	private PlaystateController playstateController;
	
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
		}
	}

	@Override
	public void update() {
		if (isCreated) {
			letterBox.update();
		}
	}

	@Override
	public void create() {
		if (!isCreated) {
			this.setLayout(new BorderLayout());
			playField = new PlayField(db_c, gsm);
			letterBox = new LetterBox(playField.getX(), playField.getFieldWidth(), db_c, gsm,"marijntje42");
			buttonPanel = new ButtonPanel(playField.getX(), letterBox.getEndY(), playField.getFieldWidth(), 50);
			int height = (int) (GUI.HEIGHT - buttonPanel.getEndY());
			infoPanel = new InfoPanel(playField.getX(), buttonPanel.getEndY(), playField.getFieldWidth(), height, db_c,
					gsm);
			this.addMouseListener(this);

			swapFrame = new SwapFrame(letterBox, db_c, gsm);
			chatArea = new Chat(db_c, gsm);
			this.add(chatArea, BorderLayout.EAST);
			filledTiles = new ArrayList<Tile>();
			playstateController = new PlaystateController(gsm, playField, letterBox);
			isCreated = true;
		} else {
			playField.reloadPlayfield();
			letterBox.reloadLetterBox();
			infoPanel.reloadInfoPanel();
			swapFrame.reloadSwapFrame();
			filledTiles.clear();
		}
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
									if (!tileIsFilled) {
										filledTiles.add(tile);
										moveLetter.calculateRoute(tile.getX(), tile.getY());
										moveLetter.setWantedSize(tile.getWidth(), tile.getHeight());
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
					if (button.getText().equals("Reset")) {
						this.resetLetterBoxLetters();
					} else if (button.getText().equals("Shuffle")) {
						letterBox.shuffleLetters();
					} else if (button.getText().equals("Play")) {
						this.checkCorrectPlacedLetters();
						playstateController.doPlay();
					} else if (button.getText().equals("Swap")) {
						swapFrame.setVisible(true);
					} else if (button.getText().equals("Pass")) {
						this.doPass();
					} else if (button.getText().equals("Resign")) {
						this.doResign();
					}
				}
			}
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
			db_c.closeConnection();
			// TODO set end of game
		}
	}

	private void resetLetterBoxLetters() {
		for (Letter letter : letterBox.getLetters()) {
			letter.reset();
		}
		filledTiles.clear();
		moveLetter = null;
	}

	private void checkCorrectPlacedLetters2() {
		int counter = 0;
		boolean isWrongTurn = false;
		for (Letter letter : letterBox.getLetters()) {
			if (!letter.isOnStartPosition()) {
				counter++;
			}
		}
		if (counter == 0) {
			isWrongTurn = true;
		}
		if (isWrongTurn) {
			JOptionPane.showMessageDialog(this, "U letter(s) staan op een ongeldige locatie!",
					"Letter(s) op een ongeldige locatie!", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void checkCorrectPlacedLetters() {
		for (Letter letter : letterBox.getLetters()) {

			if (!letter.isOnStartPosition()) {
				ArrayList<Letter> lettersOnX = new ArrayList<Letter>();
				ArrayList<Letter> lettersOnY = new ArrayList<Letter>();
				lettersOnX.add(letter);
				lettersOnY.add(letter);
				// checks for every letter if there are any already played
				// letters on both axis(x and y)
				for (Letter letter2 : playField.getPlayedLetters()) {
					if (letter.getX() - 5 < letter2.getX() && letter.getX() + 5 > letter2.getX()) {
						System.out.println("De y is gelijk");
						lettersOnY.add(letter2);
					} else if (letter.getY() - 5 < letter2.getY() && letter.getY() + 5 > letter2.getY()) {
						System.out.println("De x is gelijk");
						lettersOnX.add(letter2);
					} else {

					}
				}
				if (lettersOnX.isEmpty() && lettersOnY.isEmpty()) {
					System.out.println("Both axis are empty");
				} else if (!lettersOnX.isEmpty() && !lettersOnY.isEmpty()) {
					System.out.println("Letters on x ax are: ");
					for (Letter letterOnX : lettersOnX) {
						System.out.println(letterOnX.getLetterChar());
					}
					System.out.println("Letters on y ax are: ");
					Collections.reverse(lettersOnY);
					for (Letter letterOnY : lettersOnY) {
						System.out.println(letterOnY.getLetterChar());
					}
				} else if (!lettersOnY.isEmpty() && lettersOnX.isEmpty()) {
					System.out.println("Letters on y ax are: ");
					Collections.reverse(lettersOnY);
					for (Letter letterOnY : lettersOnY) {
						System.out.println(letterOnY.getLetterChar());
					}
				} else {
					System.out.println("Letters on x ax are: ");
					for (Letter letterOnX : lettersOnX) {
						System.out.println(letterOnX.getLetterChar());
					}
				}
			}
		}
	}

}