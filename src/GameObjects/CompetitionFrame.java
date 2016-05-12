package GameObjects;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import Gamestate.GamestateManager;
import Main.GUI;
import controller.DatabaseController;

public class CompetitionFrame extends JFrame {

	private JPanel panel;

	private JButton seeRankingButton;

	private DatabaseController db_c;

	private boolean isCreated = false;

	private JTable table;

	private DefaultTableModel model;

	private GamestateManager gsm;

	private int competitionNumber;

	private boolean isOnRanking;

	public CompetitionFrame(DatabaseController db_c, GamestateManager gsm) {
		this.db_c = db_c;
		this.gsm = gsm;
		this.setResizable(false);
		this.setTitle("Wordfeud Competities");
		panel = new JPanel(new BorderLayout());
		isOnRanking = false;
		panel.setPreferredSize(new Dimension((int) (GUI.WIDTH / 1.5), (int) (GUI.HEIGHT / 2)));
		this.setContentPane(panel);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage("Resources/wordfeudLogo.png"));
		this.pack();
		this.setLocationRelativeTo(null);
	}

	public void loadCompetitionFrame(int competitionNumber) {
		if (!isCreated) {
			this.createButtons();
			String[] header = { "Deelnemer" };
			model = new DefaultTableModel(header, 0);
			table = new JTable(model);
			table.setEnabled(false);
			JScrollPane scrollPane = new JScrollPane(table);
			scrollPane.setVisible(true);
			this.add(scrollPane);
			isCreated = true;
		}
		this.loadTable(competitionNumber);
		setVisible(true);
		this.competitionNumber = competitionNumber;
	}

	private void loadRankingTable() {
		String[] header = { "Positie", "Speler", "Score", "Gespeeld", "Gewonnen", "Verloren","Gelijk" };
		DefaultTableModel rankingModel = new DefaultTableModel(header, 0);
		String query = "SELECT * FROM competitiestand WHERE competitie_id = " + competitionNumber
				+ " ORDER BY gemidddelde_score DESC";
		ResultSet rs = db_c.query(query);
		try {
			int counter = 1;
			while (rs.next()) {
				rankingModel.addRow(new Object[] { counter, rs.getString("account_naam"),
						rs.getString("gemidddelde_score"), rs.getString("aantal_gespeelde_spellen"),
						rs.getString("aantal_gewonnen_spellen"), rs.getString("aantal_verloren_spellen"),rs.getString("aantal_gelijke_spellen") });
				counter++;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db_c.closeConnection();
		table.setModel(rankingModel);
		seeRankingButton.setText("Bekijk Ranking");
	}

	private void loadTable(int competitionNumber) {
		if (model.getRowCount() > 0) {
			for (int i = model.getRowCount() - 1; i > -1; i--) {
				model.removeRow(i);
			}
		}
		table.setModel(model);
		String query = "SELECT * FROM deelnemer WHERE competitie_id = " + competitionNumber;
		ResultSet rs = db_c.query(query);
		try {
			while (rs.next()) {
				model.addRow(new Object[] { rs.getString("account_naam") });
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db_c.closeConnection();
	}

	private void createButtons() {
		JPanel panel = new JPanel();
		if (gsm.getUser().checkRole("observer")) {
			JButton goToGameButton = new JButton("Observeer spellen");
			goToGameButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					gsm.getUser().setCompetitionNumber(competitionNumber);
					gsm.setGamestate(gsm.spectatorGameOverviewState);
					setVisible(false);
				}
			});
			panel.add(goToGameButton);
		}
		if (gsm.getUser().checkRole("player")) {
			JButton playButton = new JButton("Speel mee!");
			playButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					boolean userIsPlayer = false;
					for (int i = 0; i < model.getRowCount(); i++) {
						if (model.getValueAt(i, 0).equals(gsm.getUser().getUsername())) {
							userIsPlayer = true;
						}
					}
					if (userIsPlayer) {
						gsm.getUser().setCompetitionNumber(competitionNumber);
						gsm.setGamestate(gsm.gameOverviewState);
						setVisible(false);
					} else {
						int option = JOptionPane.showConfirmDialog(null,
								"U bent nog geen deelnemer. Wilt u een deelnemen aan deze competitie?", "Wordfeud",
								JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
						if (option == JOptionPane.OK_OPTION) {
							String query = "INSERT INTO deelnemer VALUES ('" + gsm.getUser().getUsername() + "', "
									+ competitionNumber + ")";
							db_c.queryUpdate(query);
						}
					}

				}
			});
			panel.add(playButton);
		}
		seeRankingButton = new JButton("Bekijk Ranking");
		seeRankingButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (isOnRanking) {
					loadCompetitionFrame(competitionNumber);
					seeRankingButton.setText("Bekijk Ranking");
					isOnRanking = false;
				} else {
					loadRankingTable();
					seeRankingButton.setText("Bekijk Deelnemers");
					isOnRanking = true;
				}
			}
		});
		panel.setLayout(new GridLayout(1, 2));
		panel.add(seeRankingButton);
		this.add(panel, BorderLayout.SOUTH);
	}

}
