package GameObjects;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import Gamestate.GamestateManager;
import Main.GUI;
import controller.DatabaseController;

public class CompetitionFrame extends JFrame {

	private JPanel panel;

	private DatabaseController db_c;

	private boolean isCreated = false;

	private JTable table;

	private DefaultTableModel model;

	private GamestateManager gsm;

	private int competitionNumber;

	public CompetitionFrame(DatabaseController db_c, GamestateManager gsm) {
		this.db_c = db_c;
		this.gsm = gsm;
		this.setLocation((int) (GUI.WIDTH / 3), (int) GUI.HEIGHT / 3);
		this.setResizable(false);
		this.setTitle("Wordfeud Competities");
		panel = new JPanel(new BorderLayout());
		panel.setPreferredSize(new Dimension((int) (GUI.WIDTH / 3), (int) (GUI.HEIGHT / 2)));
		this.setContentPane(panel);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage("Resources/wordfeudLogo.png"));
		this.pack();
	}

	public void loadCompetitionFrame(int competitionNumber) {
		if (!isCreated) {
			this.createButton();
			String[] header = { "Deelnemer" };
			model = new DefaultTableModel(header, 0);
			table = new JTable(model);
			table.setEnabled(false);
			JScrollPane scrollPane = new JScrollPane(table);
			scrollPane.setVisible(true);
			add(scrollPane);
			isCreated = true;
		}
		this.loadTable(competitionNumber);
		setVisible(true);
		this.competitionNumber = competitionNumber;
	}

	private void loadTable(int competitionNumber) {
		if (model.getRowCount() > 0) {
			for (int i = model.getRowCount() - 1; i > -1; i--) {
				model.removeRow(i);
			}
		}
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

	private void createButton() {
		JButton button = new JButton("Bekijk alle spellen");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				gsm.getUser().setCompetitionNumber(competitionNumber);
				gsm.setGamestate(gsm.spectatorGameOverviewState);
				setVisible(false);
			}
		});
		this.add(button,BorderLayout.SOUTH);
	}

}
