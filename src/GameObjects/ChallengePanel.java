package GameObjects;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
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
public class ChallengePanel extends JPanel implements ActionListener
{
	private GamestateManager gsm;
	private DatabaseController db_c;
	
	private JLabel mainText;
	private String[] users;
	private JComboBox<String> userCombo;
	
	private String[] languages = {"NL", "EN"};
	private JComboBox<String> langCombo;
	
	private JButton submit;
	
	public ChallengePanel(GamestateManager gsmPar, DatabaseController db_cPar)
	{
		this.gsm = gsmPar;
		this.db_c = db_cPar;
		submit = new JButton("Uitdagen");
		submit.addActionListener(this);
		submit.setActionCommand("challenge");
		this.setLayout(null);
		this.resetContent();
		this.validate();
		this.setBackground(new Color(24, 24, 24));
	}
	
	public void resetContent()
	{
		this.removeAll();
		mainText = new JLabel("Geef een taal & gebruikersnaam om uit te dagen");
		mainText.setForeground(Color.white);
		mainText.setBounds(0, 0, 300, 20);
		
		langCombo = new JComboBox(languages);
		langCombo.setSelectedItem(1);
		langCombo.setBounds(0, 21, (int)(GUI.WIDTH/5), 20);

		ResultSet usersResult = db_c.query("SELECT * FROM deelnemer WHERE competitie_id = "+gsm.getUser().getCompetitionNumber()+";");
		ArrayList<String> usernames = new ArrayList<String>();
		try
		{
			while(usersResult.next())
			{
				usernames.add(usersResult.getString("account_naam"));
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		users = new String[usernames.size()];
		for(int i=0; i< usernames.size(); i++)
		{
			users[i] = usernames.get(i);
		}
		userCombo = new JComboBox(users);
		userCombo.setSelectedItem(1);
		userCombo.setBounds(0, 42, (int)(GUI.WIDTH/5), 20);	
		
		submit.setBounds(0, 84, (int)(GUI.WIDTH/5), 20);
		this.add(mainText);	
		this.add(langCombo);
		this.add(userCombo);
		this.add(submit);
		this.add(Box.createRigidArea(new Dimension(0,100)), BorderLayout.AFTER_LAST_LINE);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getActionCommand().equals("challenge"))
		{
			this.submitChallenge();
		}
	}
	
	private void submitChallenge()
	{
		String input = userCombo.getSelectedItem().toString();		
		if(!input.contains("'") && !input.contains("\\")) //Valid name?
		{
			ResultSet userExist = db_c.query("SELECT * FROM account WHERE naam = '" + input + "';");
			try
			{
				if(userExist.next()) //User exists
				{
					//TODO Self-check, cannot invite yourself
					if(!input.toLowerCase().equals(gsm.getUser().getUsername().toLowerCase()))
					{
						ResultSet alreadyAGameWithinThisCompetition = db_c.query("SELECT * FROM spel WHERE toestand_type != 'finished' AND toestand_type != 'resigned' AND reaktie_type != 'rejected' AND competitie_id = "+gsm.getUser().getCompetitionNumber()+" AND account_naam_uitdager = '"+gsm.getUser().getUsername()+"' AND account_naam_tegenstander = '"+input+"';");
						if(!alreadyAGameWithinThisCompetition.next()) //Already a game running within this competition
						{
							ResultSet alreadyInviteOpen = db_c.query("SELECT * FROM spel WHERE toestand_type = 'request' AND reaktie_type = 'unknown' AND account_naam_uitdager = '" + gsm.getUser().getUsername() + "'  AND competitie_id = " + gsm.getUser().getCompetitionNumber() + " AND account_naam_tegenstander = '" + input + "'  AND account_naam_tegenstander != '" + gsm.getUser().getUsername() + "';");
							if(!alreadyInviteOpen.next()) //If query is empty, so there is no open invite
							{
								ResultSet userInCompetition = db_c.query("SELECT * FROM deelnemer WHERE account_naam = '"+input+"' AND competitie_id ="+gsm.getUser().getCompetitionNumber()+";");
								if(userInCompetition.next()) //Is the requested user in this competition?
								{
										db_c.queryUpdate("INSERT INTO spel (`competitie_id`, `toestand_type`, `account_naam_uitdager`,`reaktie_type`, `bord_naam`, `letterset_naam`,`account_naam_tegenstander`) VALUES ("+gsm.getUser().getCompetitionNumber()+", 'request', '"+gsm.getUser().getUsername()+"', 'unknown', 'standard', '"+langCombo.getSelectedItem().toString()+"', '"+input+"')");
										JOptionPane.showMessageDialog(null, "Uitnodiging verzonden!");
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
						else //No competition running
						{
							JOptionPane.showMessageDialog(null, "Je hebt al een spel openstaan met " + input + "!");
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