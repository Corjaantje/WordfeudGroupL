package Gamestate;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import Main.GUI;
import controller.DatabaseController;

public class SpectatorGameOverviewState extends Gamestate {

	private int x;
	private int y;

	private String description;

	private JPanel unfinishedGamesPanel;
	private JPanel finishedGamesPanel;

	private boolean isCreated = false;

	public SpectatorGameOverviewState(GamestateManager gsm, DatabaseController db_c) {
		super(gsm, db_c);
	}

	@Override
	public void draw(Graphics2D g) {
		if (isCreated) {
			g.setColor(Color.lightGray);
			g.setFont(new Font("Comic Sans mt", Font.ITALIC, 50));
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
			x = (int)(GUI.WIDTH / 2.5);
			y = 100;
			unfinishedGamesPanel = new JPanel(new GridLayout(10, 10));
			this.createUnfinishedGamesPanel();
			finishedGamesPanel = new JPanel(new GridLayout(10, 10));
			this.createFinishedGamesPanel();
			isCreated = true;
		} else {
			this.remove(unfinishedGamesPanel);
			unfinishedGamesPanel.removeAll();
			this.createUnfinishedGamesPanel();
			this.remove(unfinishedGamesPanel);
			finishedGamesPanel.removeAll();
			this.createUnfinishedGamesPanel();
		}
	}
	
	private void createUnfinishedGamesPanel(){
		unfinishedGamesPanel.setPreferredSize(new Dimension((int)(GUI.WIDTH/3), (int)(GUI.HEIGHT / 1.5)));
		unfinishedGamesPanel.add(new JLabel("Bezige spellen"));
		String query = "SELECT * FROM spel WHERE competitie_id = " + gsm.getUser().getCompetitionNumber()+" AND toestand_type = 'playing';";
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
						// TODO Auto-generated method stub
						gsm.getUser().setGameNumber(gameNumber);
						gsm.setGamestate(gsm.spectatorState);
					}
				});
				unfinishedGamesPanel.add(button);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.add(unfinishedGamesPanel,BorderLayout.WEST);
	}

	private void createFinishedGamesPanel(){
		finishedGamesPanel.setPreferredSize(new Dimension((int)(GUI.WIDTH/3), (int)(GUI.HEIGHT / 1.5)));
		finishedGamesPanel.add(new JLabel("Geëindigde spellen"));
		String query = "SELECT * FROM spel WHERE competitie_id = " + gsm.getUser().getCompetitionNumber()+" AND toestand_type = 'finished';";
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
						// TODO Auto-generated method stub
						gsm.getUser().setGameNumber(gameNumber);
						gsm.setGamestate(gsm.spectatorState);
					}
				});
				finishedGamesPanel.add(button);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.add(finishedGamesPanel,BorderLayout.EAST);
	}
}
