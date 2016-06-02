package Gamestate;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import GameObjects.AdditionalGameInfo;
import GameObjects.InfoPanel;
import GameObjects.LetterBox;
import GameObjects.PlayField;
import Main.GUI;
import controller.DatabaseController;

public class SpectatorState extends Gamestate {

	private PlayField playField;

	private LetterBox letterBox1;

	private LetterBox letterBox2;

	private InfoPanel infoPanel;
	
	private AdditionalGameInfo additionalGameInfo;

	private boolean isCreated = false;

	public SpectatorState(GamestateManager gsm, DatabaseController db_c) {
		super(gsm, db_c);
		this.setLayout(new BorderLayout());
	}

	@Override
	public void create() {
		if (!isCreated) {
			playField = new PlayField(db_c, gsm);
			letterBox1 = new LetterBox(0, playField.getX(), db_c, gsm, gsm.getUser().getChallengerName());
			letterBox2 = new LetterBox(playField.getX() + playField.getFieldWidth() - 5,
					(int) (GUI.WIDTH - (playField.getX() + playField.getFieldWidth())), db_c, gsm,
					gsm.getUser().getOpponentName());
			infoPanel = new InfoPanel(playField.getX(), playField.getFieldWidth() + 5, playField.getFieldWidth(), 75,
					db_c, gsm);
			additionalGameInfo = new AdditionalGameInfo(playField.getX(), playField.getFieldWidth() + 90, playField.getFieldWidth(), 50, db_c, gsm);
			this.createButtons();
			isCreated = true;
		} else {
			this.reloadState();
		}
	}

	@Override
	public void draw(Graphics2D g) {
		if (isCreated) {
			playField.draw(g);
			letterBox1.draw(g);
			letterBox2.draw(g);
			infoPanel.draw(g);
			additionalGameInfo.draw(g);
			g.setColor(Color.red);
			g.setFont(new Font("Arial", Font.ITALIC, 30));
			g.drawString(gsm.getUser().getChallengerName(), playField.getX() / 3, letterBox1.getEndY() / 3);
			g.drawString(gsm.getUser().getOpponentName(),
					playField.getX() + playField.getFieldWidth() + (playField.getFieldWidth() / 3),
					letterBox2.getEndY() / 3);
		}
	}

	@Override
	public void update() {

	}

	private void reloadState() {
		playField.reloadPlayfield();
		letterBox1.reloadLetterBox();
		letterBox2.reloadLetterBox();
		infoPanel.reloadInfoPanel();
		additionalGameInfo.reloadAdditionalInfo();
	}

	private void goToNext() {
		int turnNumber = gsm.getUser().getTurnNumber();
		int maxTurnNumber = gsm.getUser().getMaxTurnNumber();
		if (turnNumber <= maxTurnNumber) {
			gsm.getUser().setTurnNumber(turnNumber + 1);
			playField.moveForward();
			letterBox1.reloadLetterBox();
			letterBox2.reloadLetterBox();
			infoPanel.reloadInfoPanel();
		} else {
			JOptionPane.showMessageDialog(this, "U heeft de laatste beurt bereikt. Beurt: "+turnNumber+" - Max: "+maxTurnNumber);
		}

	}

	private void goToPrevious() {
		if (gsm.getUser().getTurnNumber()-1 > 2) {
			gsm.getUser().setTurnNumber(gsm.getUser().getTurnNumber() - 1);
			playField.moveBackwards();
			letterBox1.reloadLetterBox();
			letterBox2.reloadLetterBox();
			infoPanel.reloadInfoPanel();
		} else {
			JOptionPane.showMessageDialog(this, "U heeft de eerste beurt bereikt!");
		}

	}

	private void createButtons() {
		JButton previous = new JButton();
		previous.setBackground(new Color(26, 142, 76));
		Image previousImage = Toolkit.getDefaultToolkit().getImage("Resources/LeftPointer.png");
		previousImage = previousImage.getScaledInstance((int) (GUI.WIDTH / 5), 50, Image.SCALE_DEFAULT);
		ImageIcon leftIcon = new ImageIcon(previousImage);
		previous.setIcon(leftIcon);
		previous.setPreferredSize(new Dimension((int) (GUI.WIDTH / 2), 50));
		previous.setMinimumSize(previous.getPreferredSize());
		previous.setMaximumSize(previous.getPreferredSize());
		previous.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int turnNumber = gsm.getUser().getTurnNumber();
				System.out.println(turnNumber);
				if (turnNumber > 0) {
					gsm.getUser().setTurnNumber(turnNumber-1);
					reloadState();
				}else {
					JOptionPane.showMessageDialog(null, "U heeft de eerste beurt bereikt!");
				}
			}
		});

		JButton next = new JButton();
		next.setBackground(new Color(26, 142, 76));
		Image nextImage = Toolkit.getDefaultToolkit().getImage("Resources/RightPointer.png");
		nextImage = nextImage.getScaledInstance((int) (GUI.WIDTH / 5), 50, Image.SCALE_DEFAULT);
		ImageIcon rightIcon = new ImageIcon(nextImage);
		next.setIcon(rightIcon);
		next.setPreferredSize(new Dimension((int) (GUI.WIDTH / 2), 50));
		next.setMinimumSize(next.getPreferredSize());
		next.setMaximumSize(next.getPreferredSize());
		next.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int turnNumber = gsm.getUser().getTurnNumber();
				int maxTurnNumber = gsm.getUser().getMaxTurnNumber();
				if (turnNumber <= maxTurnNumber) {
					gsm.getUser().setTurnNumber(turnNumber + 1);
					reloadState();
				}else {
					JOptionPane.showMessageDialog(null, "U heeft de laatste beurt bereikt. Beurt: "+turnNumber+" - Max: "+maxTurnNumber);
				}
			}
		});

		Box box = Box.createHorizontalBox();
		box.add(previous, Box.LEFT_ALIGNMENT);
		box.add(next, Box.RIGHT_ALIGNMENT);
		this.add(box, BorderLayout.SOUTH);
	}

}
