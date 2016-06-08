package Gamestate;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import GameObjects.GameOverviewInfoFrame;
import Main.GUI;
import controller.DatabaseController;

public class GameOverviewState extends Gamestate {

	private int x;
	private int y;

	private String description;

	private JPanel unfinishedGamesPanel;
	private JPanel finishedGamesPanel;

	private boolean isCreated = false;
	
	private GameOverviewInfoFrame frame;
	
	public GameOverviewState(GamestateManager gsm, DatabaseController db_c) {
		super(gsm, db_c);
		frame = new GameOverviewInfoFrame(gsm, db_c);
		
	}

	@Override
	public void draw(Graphics2D g) {
		if (isCreated) {
			g.setColor(Color.WHITE);
			g.setFont(new Font("Verdana", Font.BOLD, 28));
			g.drawString(description, x, y);
		}
	}

	@Override
	public void update() {

	}

	@Override
	public void create() {
		description = gsm.getUser().getCompetitionDescription();
		if (!isCreated) {
			this.setLayout(new BorderLayout());
			y = 100;
			unfinishedGamesPanel = new JPanel(new GridLayout(10, 10));
			this.createUnfinishedGamesPanel();
			finishedGamesPanel = new JPanel(new GridLayout(10, 10));
			this.createFinishedGamesPanel();
			x = (int) (unfinishedGamesPanel.getPreferredSize().getWidth() + 100/(description.length())*2);
			isCreated = true;
		} else {
			this.removeAll();
			unfinishedGamesPanel.removeAll();
			this.createUnfinishedGamesPanel();
			finishedGamesPanel.removeAll();
			this.createFinishedGamesPanel();
		}
	}

	private void createUnfinishedGamesPanel() {
		unfinishedGamesPanel.setPreferredSize(new Dimension((int) (GUI.WIDTH / 3), (int) (GUI.HEIGHT / 1.5)));
		JLabel label = new JLabel("Bezige spellen");
		label.setFont(new Font("Comic Sans", Font.BOLD, 30));
		label.setForeground(Color.BLACK);
		unfinishedGamesPanel.add(label);
		unfinishedGamesPanel.setBackground(Color.GRAY);
		unfinishedGamesPanel.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 0), 4));
		String query = "SELECT * FROM spel WHERE competitie_id = " + gsm.getUser().getCompetitionNumber()
				+ " AND toestand_type = 'playing' AND (account_naam_uitdager = '" + gsm.getUser().getUsername() + "' OR account_naam_tegenstander = '" + gsm.getUser().getUsername() +"');";
		this.executeQuery(query, unfinishedGamesPanel);
		this.add(unfinishedGamesPanel, BorderLayout.WEST);
	}

	private void createFinishedGamesPanel() {
		finishedGamesPanel.setPreferredSize(new Dimension((int) (GUI.WIDTH / 3), (int) (GUI.HEIGHT / 1.5)));
		JLabel label = new JLabel("Geëindigde spellen");
		label.setFont(new Font("Comic Sans", Font.BOLD, 30));
		label.setForeground(Color.BLACK);
		finishedGamesPanel.add(label);
		finishedGamesPanel.setBackground(Color.GRAY);
		finishedGamesPanel.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 0), 4));;
		finishedGamesPanel.add(label);
		String query = "SELECT * FROM spel WHERE competitie_id = " + gsm.getUser().getCompetitionNumber()
				+ " AND (toestand_type = 'finished' OR toestand_type = 'resigned') AND (account_naam_uitdager = '" + gsm.getUser().getUsername() + "' OR account_naam_tegenstander = '" + gsm.getUser().getUsername() +"');";
		executeQuery(query, finishedGamesPanel);
		this.add(finishedGamesPanel, BorderLayout.EAST);
	}

	private void executeQuery(String query, JPanel panel) {
		ResultSet rs = db_c.query(query);
		try {
			while (rs.next()) {
				int r = (int) (Math.random() * 100);
				int g = (int) (Math.random() * 100);
				int b = (int) (Math.random() * 100);
				String challenger = rs.getString("account_naam_uitdager");
				String opponent = rs.getString("account_naam_tegenstander");

				JButton button = new JButton(challenger + " - VS - " + opponent);
				button.setBackground(new Color(r, g, b));
				button.setForeground(Color.white);
				int gameNumber = rs.getInt("id");
				button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						gsm.getUser().setGameNumber(gameNumber);
						if (!gsm.getUser().getPlayerTurn().equals(gsm.getUser().getUsername())) {
							gsm.getUser().setTurnNumber(gsm.getUser().getMaxTurnNumber()-1);
						} else {
							gsm.getUser().setTurnNumber(gsm.getUser().getMaxTurnNumber());
						}
						frame.loadFrame(gameNumber);
						}
					
				});
				panel.add(button);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
