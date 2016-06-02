package Gamestate;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import GameObjects.CompetitionFrame;
import Main.GUI;
import controller.CompetitionController;
import controller.DatabaseController;

public class SpectatorCompetitionState extends Gamestate {

	private JPanel competitionPanel;

	private CompetitionFrame competitionFrame;

	private CompetitionController competitionController;

	private JButton newCompetitionButton;

	private boolean isCreated;

	private ArrayList<JButton> competitions;

	public SpectatorCompetitionState(GamestateManager gsm, DatabaseController db_c) {
		super(gsm, db_c);

	}

	@Override
	public void draw(Graphics2D g) {
		// TODO Auto-generated method stub
		g.setColor(Color.WHITE);
		g.setFont(new Font("Verdana", Font.BOLD, 40));
		g.drawString("Competities", (int) (GUI.WIDTH / 2.25), 75);
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	@Override
	public void create() {
		if (!isCreated) {
			competitionController = new CompetitionController(gsm);
			this.setLayout(new GridBagLayout());
			competitionFrame = new CompetitionFrame(gsm, competitionController);
			this.createCompetitionPanel();
			if (gsm.getUser().checkRole("player")) {
				this.createButton();
			}
			isCreated = true;
		} else {
			this.loadCompetitions();
		}

	}

	private void loadCompetitions() {
		competitionPanel.removeAll();
		competitions = competitionController.updateCompetitions(competitions, competitionFrame);
		for (JButton button : competitions) {
			competitionPanel.add(button);
		}
		this.validate();
	}

	private void createCompetitionPanel() {
		competitions = new ArrayList<JButton>();
		competitionPanel = new JPanel(new GridLayout(10, 10));
		competitionPanel.setBackground(Color.gray);
		competitionPanel.setPreferredSize(new Dimension((int) GUI.WIDTH / 2, (int) ((int) GUI.HEIGHT / 1.5)));
		this.loadCompetitions();
		this.add(competitionPanel, new GridBagConstraints());
	}

	private void createButton() {
		newCompetitionButton = new JButton("Nieuwe Competitie");
		newCompetitionButton.setPreferredSize(new Dimension(150, 250));
		newCompetitionButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				String result = JOptionPane.showInputDialog("Geef een competitie naam !");
				competitionController.addCompetition(result);
				loadCompetitions();
			}
		});
		this.add(newCompetitionButton);
	}

}