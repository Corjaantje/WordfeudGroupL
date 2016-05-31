package GameObjects;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import Gamestate.GamestateManager;
import Main.GUI;
import controller.DatabaseController;

@SuppressWarnings("serial")
public class NotificationPanel extends JPanel
{
	private GamestateManager gsm;
	private NotificationFrame fr;
	//TODO connect Databasecontroller
	private DatabaseController db_c = new DatabaseController();
	private int NotificationTotal;
	
	public NotificationPanel(GamestateManager gsm, NotificationFrame frame, DatabaseController dbc)
	{
		this.gsm = gsm;
		this.fr = frame;
		this.updateNotifications();
	}
	
	public void updateNotifications()
	{
		this.setBackground(Color.GRAY);
		this.setBorder(BorderFactory.createLineBorder(Color.black));
		this.setPreferredSize(new Dimension((int) (GUI.WIDTH/3), (int) (GUI.HEIGHT/300)));;
		this.setMaximumSize(new Dimension((int) (GUI.WIDTH/3), (int) (GUI.HEIGHT/300)));;
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		NotificationTotal = 0;
		
		Font headerFont = new Font("Verdana", Font.BOLD, 24);
		JLabel header = new JLabel("Melding Overzicht");
		header.setForeground(Color.BLACK);
		header.setAlignmentX(CENTER_ALIGNMENT);
		header.setFont(headerFont);
		this.add(header);
		this.add(Box.createRigidArea(new Dimension(0,25)));
		
		try
		{
				ResultSet notifications = db_c.query("SELECT * FROM spel WHERE reaktie_type = 'unknown' and account_naam_tegenstander = '" + gsm.getUser().getUsername() + "';");		
				
				while(notifications.next())
				{
					String spelID = notifications.getString("id");
					ResultSet competitions = db_c.query("SELECT * FROM Competitie WHERE id = " + notifications.getString("competitie_id"));
					if(competitions.next())
					{
						String competitionName = competitions.getString("omschrijving");
						
						this.addNotifications(notifications.getString("account_naam_uitdager"), competitionName, spelID);
					}
				}
		}
		catch(SQLException sql){sql.printStackTrace();};
		
		if(NotificationTotal == 0)
		{
			JLabel noNotifications = new JLabel("Geen nieuwe meldingen!");
			noNotifications.setForeground(Color.BLACK);
			noNotifications.setAlignmentX(CENTER_ALIGNMENT);
			noNotifications.setFont(headerFont);
			this.add(noNotifications);
		}
	}
	
	private void addNotifications(String challengerName, String competitionName, String gameNumber)
	{	
		String gameID = gameNumber;
		Font messageFont = new Font("Verdana", Font.BOLD, 12);
		JLabel message = new JLabel((NotificationTotal + 1) + ". Uitgedaagd door: " + challengerName + " in competitie '" + competitionName + "!");
		message.setForeground(Color.BLACK);
		message.setAlignmentX(CENTER_ALIGNMENT);
		message.setFont(messageFont);
		
		JButton accept = new JButton();
		accept.setText("Accepteren");
		accept.setSize(new Dimension(200, 20));
		accept.setAlignmentX(CENTER_ALIGNMENT);
		accept.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				db_c.queryUpdate("UPDATE spel SET reaktie_type = 'accepted' WHERE id = " + gameID + ";");	
				removeAll();
				updateNotifications();
				fr.pack();
			}
		});
		
		JButton deny = new JButton();
		deny.setText("Weigeren");
		deny.setSize(new Dimension(200, 20));
		deny.setAlignmentX(CENTER_ALIGNMENT);
		deny.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				db_c.queryUpdate("UPDATE spel SET reaktie_type = 'rejected' WHERE id = " + gameID + ";");
				removeAll();
				updateNotifications();
				fr.pack();
			}
		});
		
		this.add(message);
		this.add(accept);
		this.add(deny);
		this.add(Box.createRigidArea(new Dimension(0,20)));
		NotificationTotal++;
	}
}
