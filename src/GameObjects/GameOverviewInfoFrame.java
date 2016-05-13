package GameObjects;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import Gamestate.GamestateManager;
import Main.GUI;
import controller.DatabaseController;

public class GameOverviewInfoFrame extends JFrame {

	private GamestateManager gsm;
	private DatabaseController db_c;

	private JPanel panel;
	
	private int gameNumber;

	private boolean isCreated = false;

	public GameOverviewInfoFrame(GamestateManager gsm, DatabaseController db_c) {
		this.gsm = gsm;
		this.db_c = db_c;
		this.setResizable(false);
		this.setTitle("Wordfeud Competities");
		panel = new JPanel(new BorderLayout());
		panel.setPreferredSize(new Dimension((int) (GUI.WIDTH / 3), (int) (GUI.HEIGHT / 3)));
		this.setContentPane(panel);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage("Resources/wordfeudLogo.png"));
		this.pack();
		this.setLocationRelativeTo(null);
	}

	public void loadFrame(int gameNumber) {
		this.setVisible(true);
		if (isCreated) {
			this.removeAll();
		}
		this.createLabel();
		this.createButton();
		if(!isCreated){
			isCreated = true;
		}
	}

	private void createLabel() {
		Font font = new Font("Serif", Font.BOLD, 20);

		Box box = Box.createVerticalBox();
		JLabel turn = new JLabel("De beurt is aan: " + gsm.getUser().getPlayerTurn());
		turn.setFont(font);
		JLabel score = new JLabel(gsm.getUser().getUsername() + " - " + gsm.getUser().getUserScore() + " ~ VS ~ "
				+ gsm.getUser().getOpponentName() + " - " + gsm.getUser().getOpponentScore());
		score.setFont(font);
		box.add(score);
		box.add(new JLabel(" "));
		box.add(turn);

		this.add(box, BorderLayout.NORTH);
	}

	private void createButton() {
		JButton button = new JButton("Ga naar spel");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				int option = JOptionPane.showConfirmDialog(null,
						"Weet u zeker dat u naar het spel: " + button.getText() + " wilt gaan?", "Wordfeud",
						JOptionPane.YES_NO_OPTION);
				if (option == JOptionPane.OK_OPTION) {
					ResultSet rs = db_c
							.query("SELECT max(id) AS id FROM beurt WHERE spel_id = " + gameNumber);
					try {
						while (rs.next()) {
							gsm.getUser().setTurnNumber(rs.getInt("id"));
						}
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					gsm.setGamestate(gsm.playState);
					setVisible(false);
				}
			}
		});
		this.add(button, BorderLayout.SOUTH);
	}

}
