package GameObjects;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import controller.DatabaseController;
import Gamestate.GamestateManager;
import Main.GUI;

@SuppressWarnings("serial")
public class ChallengePanel extends JPanel implements KeyListener
{
	private GamestateManager gsm;
	private DatabaseController db_c;
	
	private JLabel mainText;
	private JTextField searchField;
	
	public ChallengePanel(GamestateManager gsmPar, DatabaseController db_cPar)
	{
		this.gsm = gsmPar;
		this.db_c = db_cPar;
		this.setLayout(new BorderLayout());
		this.addContent();
		this.validate();
		this.setBackground(new Color(24, 24, 24));
	}
	
	private void addContent()
	{
		mainText = new JLabel("Geef een gebruikersnaam om uit te dagen");
		mainText.setForeground(Color.white);
		
		searchField = new JTextField();
		searchField.setPreferredSize(new Dimension((int)(GUI.WIDTH/5.1), (int)(GUI.HEIGHT/50)));
		searchField.addKeyListener(this);
		
		this.add(mainText, BorderLayout.NORTH);
		this.add(searchField, BorderLayout.AFTER_LINE_ENDS);
		this.add(Box.createRigidArea(new Dimension(0,120)), BorderLayout.AFTER_LAST_LINE);
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			String input = searchField.getText();
			
			if(input.matches("^[a-zA-Z0-9]*$")) //Valid name?
			{
				ResultSet userExist = db_c.query("SELECT * FROM account WHERE naam = '" + input + "';");
				try
				{
					if(userExist.next()) //User exists
					{
						ResultSet alreadyInviteOpen = db_c.query("SELECT * FROM spel WHERE toestand_type = 'request' AND account_naam_uitdager = '" + gsm.getUser().getUsername() + "'  AND competitie_id = " + gsm.getUser().getCompetitionNumber() + " AND account_naam_tegenstander = '" + input + "'  AND account_naam_tegenstander != '" + gsm.getUser().getUsername() + "';");
						if(!alreadyInviteOpen.next()) //If query is empty, so there is no open invite
						{
							ResultSet userInCompetition = db_c.query("SELECT * FROM deelnemer WHERE account_naam = '"+input+"' AND competitie_id ="+gsm.getUser().getCompetitionNumber()+";");
							if(userInCompetition.next()) //Is the requested user in this competition?
							{
								ResultSet getEmptyGameID = db_c.query("select * from spel ORDER BY id DESC;"); 
								if(getEmptyGameID.next())
								{
									int freeGameID = (getEmptyGameID.getInt("id") + 1);
									
									db_c.queryUpdate("INSERT INTO spel VALUES ("+freeGameID+","+gsm.getUser().getCompetitionNumber()+", 'request', '"+gsm.getUser().getUsername()+"', 'unknown', 'standard', 'NL', '"+input+"');");
									JOptionPane.showMessageDialog(null, "Uitnodiging verzonden!");
								}
								else
								{
									System.out.println("UNKNOWN ERROR");
								}
							}
							else
							{
								JOptionPane.showMessageDialog(null, input + " zit niet in deze competitie!");
							}
						}
						else
						{
							System.out.println(alreadyInviteOpen.getString("competitie_id"));
							JOptionPane.showMessageDialog(null, "Er staat nog een uitnodiging open met " + input + " binnen deze competitie!");
						}
					}
					else //User doesn't exist
					{
						JOptionPane.showMessageDialog(null, "Deze gebruiker bestaat niet! Probeer opnieuw");
					}
				} 
				catch (SQLException sql) //SQL error handler
				{
					sql.printStackTrace();
				}
			}
			else //Invalid characters used
			{
				JOptionPane.showMessageDialog(null, "Ongeldige karakters in naam: '" + input + "'!");
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
		// TODO Auto-generated method stub
		
	}
}
