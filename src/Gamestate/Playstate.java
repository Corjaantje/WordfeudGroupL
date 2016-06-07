package Gamestate;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.border.Border;

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

	private int lastTurn;

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
		lastTurn = gsm.getUser().getMaxTurnNumber();
		if (!isCreated) {
			this.setLayout(new BorderLayout());
			playField = new PlayField(db_c, gsm);
			letterBox = new LetterBox(playField.getX(), playField.getFieldWidth(), db_c, gsm, gsm.getUser().getUsername());
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
			this.createButton();
			isCreated = true;
			this.setBackground(Color.black);
		} else {
			this.reloadPlaystate();
		}
	}
	
	private void createButton(){
		JButton button = new JButton("Ververs");
		button.setBorder(BorderFactory.createLineBorder((new Color(0, 255, 0)), 3));
		button.setPreferredSize(new Dimension(100, 10));
		button.setSize(button.getPreferredSize());
		button .setMinimumSize(button.getPreferredSize());
		button.setMaximumSize(button.getPreferredSize());
		Image image = null;
		try {
			image = ImageIO.read(this.getClass().getClassLoader().getResource("resources/refresh.png"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		image = image.getScaledInstance(100,100, Image.SCALE_DEFAULT);
		ImageIcon icon = new ImageIcon(image);
		button.setIcon(icon);
		button.setBackground(Color.black);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				reloadPlaystate();
			}
		});
		this.add(button, BorderLayout.WEST);
	}

	public void reloadPlaystate() {
		indicatorIsPlaced = false;
		boolean turnNumberChanged = false;
		if (gsm.getUser().getPlayerTurn().equals(gsm.getUser().getUsername())) {
			gsm.getUser().setTurnNumber(gsm.getUser().getMaxTurnNumber());
		} else {
			gsm.getUser().setTurnNumber(gsm.getUser().getMaxTurnNumber() - 1);
			turnNumberChanged = true;
		}
		
		if (turnNumberChanged) {
			gsm.getUser().setTurnNumber(gsm.getUser().getMaxTurnNumber());
		}
		letterBox.reloadLetterBox();
		playField.reloadPlayfield();
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
			// check if a letter is selected
			if (moveLetter != null) {
				// check if the letter is on a valid position
				if (moveLetter.getRightLocation()) {
					for (Tile tile : playField.getTiles()) {
						// check only the empty tiles
						if (tile.getIsEmpty()) {
							// check which tile is selected
							if (x > tile.getX() && x < (tile.getX() + tile.getWidth())) {
								if (y > tile.getY() && y < (tile.getY() + tile.getHeight())) {
									// check if a letterbox letter is already
									// placed on that tile
									boolean tileIsFilled = false;
									for (Tile filledTile : filledTiles) {
										if (filledTile.getX() == tile.getX()) {
											if (filledTile.getY() == tile.getY()) {
												tileIsFilled = true;
											}
										}
									}
									if (!tileIsFilled) {
										// checks if the letter is already
										// selected
										if (moveLetter.getX() == tile.getX() && moveLetter.getY() == tile.getY()) {
											return;
										}
										// give the letter the right information
										filledTiles.add(tile);
										indicatorIsPlaced = true;
										moveLetter.setBordX(tile.getBordX());
										moveLetter.setBordY(tile.getBordY());
										moveLetter.calculateRoute(tile.getX(), tile.getY());
										moveLetter.setWantedSize(tile.getWidth(), tile.getHeight());
										// check if the letter is a joker
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
								// reset the letter if its moved into the
								// letterbox
								moveLetter.reset();
								// remove the first added tile
								if (!filledTiles.isEmpty()) {
									filledTiles.remove(0);
								}
							}
						}
					}
				}
			}
		} catch (

		Exception e2)

		{
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
					if (gsm.getUser().userCanPlay()) {

						if (button.getText().equals("Resetten")) {
							this.resetLetterBoxLetters();
						} else if (button.getText().equals("Schudden")) {
							letterBox.shuffleLetters();
						} else if (button.getText().equals(" Spelen")) {
							playstateController.doPlay();
							turnIndicator.resetTurnIndicator();
						} else if (button.getText().equals("Swappen")) {
							swapFrame.setVisible(true);
							this.reloadPlaystate();
						} else if (button.getText().equals(" Passen")) {
							if (playstateController.doPass()) {
								letterBox.replacePlacedLetters(new ArrayList<Letter>());
								this.reloadPlaystate();
							}
						} else if (button.getText().equals("Opgeven")) {
							playstateController.doResign();
							this.reloadPlaystate();
						}
					} else {
						if (button.getText().equals("Resetten")) {
							this.resetLetterBoxLetters();
						} else if (button.getText().equals("Schudden")) {
							letterBox.shuffleLetters();
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
			playstateController.setScoreTrackingVariables();
			int score = playstateController.getScore();
			// check if the word is on a wrong location
			if (score == -1) {
				indicatorIsPlaced = false;
				System.out.println("The letters are not placed correctly! score = " + score);
				turnIndicator.resetTurnIndicator();
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
				turnIndicator.setToPoint(new Point((int) wordLetters.get(0).getX(), (int) wordLetters.get(0).getY()));
				turnIndicator.setScore(score);
			}
			indicatorIsPlaced = false;
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

}