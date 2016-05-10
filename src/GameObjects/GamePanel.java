package GameObjects;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import Gamestate.GamestateManager;
import Main.Drawable;
import controller.DatabaseController;

public class GamePanel extends JPanel {

	private DatabaseController db_c;


	private GamestateManager gsm;

	private int x;
	private int y;

	private ArrayList<JButton> buttons;

	public GamePanel(DatabaseController db_c, GamestateManager gsm) {
		this.db_c = db_c;
		this.gsm = gsm;
		x = 100;
		y = 100;
		this.setLocation(x, y);
		this.setPreferredSize(new Dimension(300, 500));
		this.setBackground(Color.gray);
		this.setLayout(new GridLayout(10, 10));
		buttons = new ArrayList<JButton>();
		this.createButtons();
	}

	private void createButtons() {
		String query = "SELECT * FROM spel WHERE competitie_id = "+gsm.getUser().getCompetitionNumber();
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
				button.setActionCommand(rs.getString("id"));
				button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						gsm.getUser().setSpelID(Integer.parseInt(e.getActionCommand()));
						ResultSet rs = db_c.query("SELECT max(id) AS id FROM beurt WHERE spel_id = "+e.getActionCommand());
						try {
							while(rs.next()){
								gsm.getUser().setTurnNumber(rs.getInt("id"));
							}
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						gsm.setGamestate(gsm.playState);
					}
				});
				buttons.add(button);
				this.add(button);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	public void gamePanelReload() {
		for (JButton button : buttons) {
			this.remove(button);
		}
		buttons.clear();
		this.createButtons();
	}

}
