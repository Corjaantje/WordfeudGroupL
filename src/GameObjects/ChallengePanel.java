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
import javax.swing.JComboBox;
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
	
	private String[] languages = {"NL", "EN"};
	private JComboBox<String> langCombo;
	
	public ChallengePanel(GamestateManager gsmPar, DatabaseController db_cPar)
	{
		this.gsm = gsmPar;
		this.db_c = db_cPar;
		this.setLayout(null);
		this.addContent();
		this.validate();
		this.setBackground(new Color(24, 24, 24));
	}
	
	private void addContent()
	{
		mainText = new JLabel("Geef een taal & gebruikersnaam om uit te dagen");
		mainText.setForeground(Color.white);
		mainText.setBounds(0, 0, 300, 20);
		
		langCombo = new JComboBox(languages);
		langCombo.setSelectedItem(1);
		langCombo.setBounds(0, 21, (int)(GUI.WIDTH/5), 20);

		searchField = new JTextField();
		searchField.addKeyListener(this);
		searchField.setBounds(0, 42, (int)(GUI.WIDTH/5), 20);	
		
		this.add(mainText);	
		this.add(langCombo);
		this.add(searchField);
		this.add(Box.createRigidArea(new Dimension(0,100)), BorderLayout.AFTER_LAST_LINE);
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
						//TODO Self-check, cannot invite yourself
						if(!input.toLowerCase().equals(gsm.getUser().getUsername().toLowerCase()))
						{
							ResultSet alreadyInviteOpen = db_c.query("SELECT * FROM spel WHERE toestand_type = 'request' AND reaktie_type = 'unknown' AND account_naam_uitdager = '" + gsm.getUser().getUsername() + "'  AND competitie_id = " + gsm.getUser().getCompetitionNumber() + " AND account_naam_tegenstander = '" + input + "'  AND account_naam_tegenstander != '" + gsm.getUser().getUsername() + "';");
							if(!alreadyInviteOpen.next()) //If query is empty, so there is no open invite
							{
								ResultSet userInCompetition = db_c.query("SELECT * FROM deelnemer WHERE account_naam = '"+input+"' AND competitie_id ="+gsm.getUser().getCompetitionNumber()+";");
								if(userInCompetition.next()) //Is the requested user in this competition?
								{
									ResultSet getEmptyGameID = db_c.query("select * from spel ORDER BY id DESC;"); 
									if(getEmptyGameID.next())
									{
										int freeGameID = (getEmptyGameID.getInt("id") + 1);
										
										db_c.queryUpdate("INSERT INTO spel VALUES ("+freeGameID+","+gsm.getUser().getCompetitionNumber()+", 'request', '"+gsm.getUser().getUsername()+"', 'unknown', 'standard', '"+ langCombo.getSelectedItem().toString() + "', '"+input+"');");
										JOptionPane.showMessageDialog(null, "Uitnodiging verzonden!");
									}
									else
									{
										System.out.println("Er zijn geen game id's meer beschikbaar...");
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
						else //Inviting yourself
						{
							JOptionPane.showMessageDialog(null, "Je kan jezelf niet uitdagen!");
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
