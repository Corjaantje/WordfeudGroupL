package Gamestate;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import model.User;
import GameObjects.NotificationFrame;
import Main.GUI;
import controller.AdminDBController;
import controller.DatabaseController;

@SuppressWarnings("serial")
public class MainMenuState extends Gamestate implements ActionListener
{
	private GamestateManager gsm;
	private DatabaseController db_c;
	
	private User currentUser;
	
	private Image bgImage;
	
	private JLabel welcomeText;
	private JLabel adminText;
	private JLabel moderatorText;
	private JLabel playerText;
	private JLabel spectatorText;
	
	private JButton goToLogout;
	private JButton goToCompetitionState;
	private JButton openAdminSetRole;
	private JButton openModeratorNewWord;
	private JButton showPersonalInfo;
	private JButton playerNewWord;
	private JButton playerNotifications;
	private JButton spectatorCompetition;
	
	private boolean mainMenuCreated;
	
	public  MainMenuState(GamestateManager gsmanager,DatabaseController db_controller)
	{
		super(gsmanager,db_controller);
		this.gsm = gsmanager;
		this.db_c = db_controller;
	}
	
	private void welcomeUser()
	{
		welcomeText = new JLabel();
		welcomeText.setForeground(Color.WHITE);
		
		if(currentUser.getUsername().length() <= 9)
		{
			System.out.println(currentUser.getUsername().length());
			welcomeText.setFont(new Font("Verdana", Font.BOLD, 32));
		}
		else
		{
			welcomeText.setFont(new Font("Verdana", Font.BOLD, 24));
		}
		welcomeText.setText("Welkom " + currentUser.getUsername() + "!");
		welcomeText.setAlignmentX(CENTER_ALIGNMENT);
		this.add(Box.createRigidArea(new Dimension(0,25)));
		this.add(welcomeText);
	}
	
	private void addGeneralButtons()
	{
		goToLogout = new JButton();
		showPersonalInfo = new JButton();
		
		
		showPersonalInfo.setText("Gebruiker Gegevens");
		showPersonalInfo.setAlignmentX(CENTER_ALIGNMENT);
		showPersonalInfo.setActionCommand("userinfo");
		showPersonalInfo.addActionListener(this);
		
		goToLogout.setText("Uitloggen");
		goToLogout.setAlignmentX(CENTER_ALIGNMENT);
		goToLogout.setActionCommand("logout");
		goToLogout.addActionListener(this);
		
		this.add(Box.createRigidArea(new Dimension(0,10)));
		this.add(showPersonalInfo);
		this.add(Box.createRigidArea(new Dimension(0,25)));
		this.add(goToLogout);
		this.add(Box.createRigidArea(new Dimension(0,10)));
	}
	
	private void addAdministratorButtons()
	{
		if(currentUser.checkRole("administrator"))
		{
			adminText = new JLabel();
			openAdminSetRole = new JButton();
			
			adminText.setForeground(Color.WHITE);
			adminText.setFont(new Font("Verdana", Font.BOLD, 24));
			adminText.setAlignmentX(CENTER_ALIGNMENT);
			adminText.setText("Administrator instellingen");
		
			openAdminSetRole.setText("Gebruiker Overzicht (Geadvanceerd)");
			openAdminSetRole.setAlignmentX(CENTER_ALIGNMENT);
			openAdminSetRole.setActionCommand("adminrole");
			openAdminSetRole.addActionListener(this);		
			
			this.add(adminText);
			this.add(Box.createRigidArea(new Dimension(0,25)));
			this.add(openAdminSetRole);
			this.add(Box.createRigidArea(new Dimension(0,10)));
		}
	}
	
	private void addModeratorButtons()
	{
		if(currentUser.checkRole("moderator"))
		{	
			moderatorText = new JLabel();
			openModeratorNewWord = new JButton();
			
			moderatorText.setForeground(Color.WHITE);
			moderatorText.setFont(new Font("Verdana", Font.BOLD, 24));
			moderatorText.setAlignmentX(CENTER_ALIGNMENT);
			moderatorText.setText("Moderator Instellingen");
			
			openModeratorNewWord.setText("Check nieuwe woorden");
			openModeratorNewWord.setAlignmentX(CENTER_ALIGNMENT);
			openModeratorNewWord.setActionCommand("moderatorNew");
			openModeratorNewWord.addActionListener(this);	
			
			this.add(moderatorText);
			this.add(Box.createRigidArea(new Dimension(0,25)));
			this.add(openModeratorNewWord);
			this.add(Box.createRigidArea(new Dimension(0,10)));
		}
	}
	
	private void addPlayerButtons()
	{
		if(currentUser.checkRole("player"))
		{
			playerText = new JLabel();
			playerNewWord = new JButton();
			playerNotifications = new JButton();
			goToCompetitionState = new JButton();
			
			playerText.setForeground(Color.WHITE);
			playerText.setFont(new Font("Verdana", Font.BOLD, 24));
			playerText.setAlignmentX(CENTER_ALIGNMENT);
			playerText.setText("Speler Opties");
			
			goToCompetitionState.setText("Competitie Overzicht");
			goToCompetitionState.setAlignmentX(CENTER_ALIGNMENT);
			goToCompetitionState.setActionCommand("competition");
			goToCompetitionState.addActionListener(this);
			
			playerNewWord.setText("Woord aanvraag");
			playerNewWord.setAlignmentX(CENTER_ALIGNMENT);
			playerNewWord.setActionCommand("wordNew");
			playerNewWord.addActionListener(this);	
			
			playerNotifications.setText("Spel aanvraag meldingen");
			playerNotifications.setAlignmentX(CENTER_ALIGNMENT);
			playerNotifications.setActionCommand("notifications");
			playerNotifications.addActionListener(this);
			
			this.add(playerText);
			this.add(Box.createRigidArea(new Dimension(0,25)));
			this.add(goToCompetitionState);	
			this.add(Box.createRigidArea(new Dimension(0,25)));
			this.add(playerNewWord);
			this.add(Box.createRigidArea(new Dimension(0,25)));
			this.add(playerNotifications);
			this.add(Box.createRigidArea(new Dimension(0,10)));
		}
	}
	
	private void addSpectatorButtons()
	{
		if(currentUser.checkRole("observer"))
		{
			spectatorText = new JLabel();
			spectatorCompetition = new JButton();
			
			spectatorText.setForeground(Color.WHITE);
			spectatorText.setFont(new Font("Verdana", Font.BOLD, 24));
			spectatorText.setAlignmentX(CENTER_ALIGNMENT);
			spectatorText.setText("Toeschouwer Opties");
			
			spectatorCompetition.setText("Competitie Overzicht");
			spectatorCompetition.setAlignmentX(CENTER_ALIGNMENT);
			spectatorCompetition.setActionCommand("spectator");
			spectatorCompetition.addActionListener(this);
			
			this.add(spectatorText);
			this.add(Box.createRigidArea(new Dimension(0,25)));
			this.add(spectatorCompetition);
			this.add(Box.createRigidArea(new Dimension(0,10)));
		}
	}
	
	@SuppressWarnings("unused")
	@Override
	public void draw(Graphics2D g)
	{
		int width = getWidth() / 1;
		int height = (int) (getHeight() / 0.95);
		int x = (int) (GUI.WIDTH / 2 - (width/2));
		int y = getWidth()/21;
		
		g.drawImage(bgImage, -3, 0, width, height, null);
	}

	@Override
	public void update()
	{		
	}

	@Override
	public void create()
	{		
		if(!mainMenuCreated)
		{
			currentUser = gsm.getUser();
			this.createMenu();
		}
		else if(!currentUser.equals(gsm.getUser()))
		{
			currentUser = gsm.getUser();
			this.createMenu();
		}
		try {
			this.bgImage = ImageIO.read(this.getClass().getClassLoader().getResource(("resources/MainMenu.png")));
		} catch (IOException e) {
			System.out.println("Something went wrong at the image loader: "+e.getMessage());
		}
	}
	
	private void createMenu()
	{
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));	
		this.welcomeUser();
		this.addGeneralButtons();
		this.addPlayerButtons();
		this.addSpectatorButtons();
		this.addAdministratorButtons();
		this.addModeratorButtons();
		mainMenuCreated = true;
	}

	@SuppressWarnings({ "static-access", "unused" })
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if("competition".equals(e.getActionCommand()))
		{
			gsm.setGamestate(GamestateManager.spectatorCompetitionState);
		}
		else if("userinfo".equals(e.getActionCommand()))
		{
			InfoUserState ius = new InfoUserState(gsm, db_c);
		}
		else if("logout".equals(e.getActionCommand()))
		{
			gsm.setGamestate(GamestateManager.loginState);
			gsm.setUser(null);
			this.removeAll();
		}
		else if("adminrole".equals(e.getActionCommand()))
		{
			this.makeAdminPopup();
		}
		else if("moderatorNew".equals(e.getActionCommand()))
		{
			gsm.setGamestate(GamestateManager.moderatorReviewWordState);
		}
		else if("wordNew".equals(e.getActionCommand()))
		{
			gsm.setGamestate(GamestateManager.playerNewWordState);
		}
		else if("notifications".equals(e.getActionCommand()))
		{
			if(!gsm.notifyCreated)
			{
				gsm.Notify = new NotificationFrame(gsm, db_c);
				gsm.Notify.setVisible(true);
				gsm.notifyCreated = true;
			}
			else
			{
				gsm.Notify.updateNotifications();
				gsm.Notify.setVisible(true);
			}
		}
		else if("spectator".equals(e.getActionCommand()))
		{
			gsm.setGamestate(GamestateManager.spectatorCompetitionState);
		}
	}
	
	private void makeAdminPopup()
	{
		AdminDBController db_con = new AdminDBController(gsm);
	    JFrame f = new JFrame("Administrator opties");
	    f.setPreferredSize(new Dimension(300, 230));
	    f.setResizable(true);
	    f.setLayout(new BorderLayout());
	    JTextField searchBar = new JTextField(); 
	    
	    JButton searchBarButton = new JButton("Zoek gebruiker");
	    searchBarButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//Makes a string variable of what is filled in into the search bar
				String usernameField = searchBar.getText();
				
				//Checks if the user exists in the app
				if(db_con.checkIfUserExists(usernameField)){
				    JPanel checkBoxPanel = new JPanel();
				    
				    //Prints the username and password of the searched user
				    JLabel usernameLabel = new JLabel("Gebruikersnaam: " + usernameField);
				    JLabel passwordLabel = new JLabel("Wachtwoord: " + db_con.getPassword(usernameField));
				    
				    //Makes all the checkboxes of the roles which the user possible is
				    JCheckBox adminOption = new JCheckBox();
				    JCheckBox moderatorOption = new JCheckBox();
				    JCheckBox observerOption = new JCheckBox();
				    JCheckBox playerOption = new JCheckBox();
				    
				    //Sets the text of every checkbox
				    adminOption.setText("Administrator");
				    moderatorOption.setText("Moderator");
				    observerOption.setText("Spectator");
				    playerOption.setText("Player");
				    
				    //Makes the layout for all the components in the chechBoxPanel
				    checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel, BoxLayout.Y_AXIS));
				
				    //Fills in all the roles which the user has
				    if(db_con.checkUserIsRole(usernameField, "administrator")){
				    	adminOption.setSelected(true);
				    	System.out.println("This user is admin");
				    }
				    if(db_con.checkUserIsRole(usernameField, "moderator")){
				    	moderatorOption.setSelected(true);
				    	System.out.println("This user is moderator");
				    }
				    if(db_con.checkUserIsRole(usernameField, "observer")){
				    	observerOption.setSelected(true);
				    	System.out.println("This user is observer");
				    }
				    if(db_con.checkUserIsRole(usernameField, "player")){
				    	playerOption.setSelected(true);
				    	System.out.println("This user is player");
				    }
				    
				  
				    adminOption.addItemListener(new ItemListener(){
				    	@SuppressWarnings("unused")
						@Override
				    	public void itemStateChanged(ItemEvent ie) {
				    		JCheckBox adminOption = (JCheckBox) ie.getItem();
				    		int state = ie.getStateChange();
				    		
				    		if(currentUser.getAmountOfRoles(usernameField) >= 0){
					    		if(state == ItemEvent.SELECTED){
					    			db_con.addRole("administrator", usernameField);
								}else if(state == ItemEvent.DESELECTED){
									if(currentUser.getAmountOfRoles(usernameField) > 1){
										db_con.removeRole("administrator", usernameField);
									}else{
										JOptionPane.showMessageDialog(null, "Deze user heeft minimaal een rol nodig");
									}
								}
				    		}
						}
					});
				    
					moderatorOption.addItemListener(new ItemListener(){
						@SuppressWarnings("unused")
						@Override
						public void itemStateChanged(ItemEvent ie) {
							JCheckBox moderatorOption = (JCheckBox) ie.getItem();
							int state = ie.getStateChange();
							
							if(currentUser.getAmountOfRoles(usernameField) >= 0){
								if(state == ItemEvent.SELECTED){
									db_con.addRole("moderator", usernameField);
								}else if(state == ItemEvent.DESELECTED){
									if(currentUser.getAmountOfRoles(usernameField) > 1){
										db_con.removeRole("moderator", usernameField);
									}else{
										JOptionPane.showMessageDialog(null, "Deze user heeft minimaal een rol nodig");
									}
								}
							}
						}
					});

					observerOption.addItemListener(new ItemListener(){
						@SuppressWarnings("unused")
						@Override
						public void itemStateChanged(ItemEvent ie) {
							JCheckBox observerOption = (JCheckBox) ie.getItem();
							int state = ie.getStateChange();
							
							if(currentUser.getAmountOfRoles(usernameField) >= 0){
								if(state == ItemEvent.SELECTED){
									db_con.addRole("observer", usernameField);
								}else if(state == ItemEvent.DESELECTED){
									if(currentUser.getAmountOfRoles(usernameField) > 1){
										db_con.removeRole("observer", usernameField);
									}else{
										JOptionPane.showMessageDialog(null, "Deze user heeft minimaal een rol nodig");
									}
								}
							}
						}
					});
					    
					playerOption.addItemListener(new ItemListener(){
						@SuppressWarnings("unused")
						@Override
						public void itemStateChanged(ItemEvent ie) {
							JCheckBox playerOption = (JCheckBox) ie.getItem();
							int state = ie.getStateChange();

							if(state == ItemEvent.SELECTED){
								db_con.addRole("player", usernameField);
							}else if(state == ItemEvent.DESELECTED){
								if(currentUser.getAmountOfRoles(usernameField) > 1){
									db_con.removeRole("player", usernameField);
								}else{
									JOptionPane.showMessageDialog(null, "Deze user heeft minimaal een rol nodig");
								}
							}
						}
					});
				    
				    //Adds all the labels and checkboxes
				    checkBoxPanel.add(usernameLabel);
				    checkBoxPanel.add(passwordLabel);
				    checkBoxPanel.add(adminOption);
				    checkBoxPanel.add(moderatorOption);
				    checkBoxPanel.add(observerOption);
				    checkBoxPanel.add(playerOption);
				    f.add(checkBoxPanel, BorderLayout.SOUTH);
				    
				    //Updates the frame so we will see all the information that is made above
				    SwingUtilities.updateComponentTreeUI(f);
				}
			}
	    });
	    
	    //Adds the search bar to find users and the button to actually start searching
	    f.add(searchBar, BorderLayout.NORTH);
	    f.add(searchBarButton, BorderLayout.CENTER);
	    f.pack();
	    f.setLocationRelativeTo(null);
	    f.setVisible(true);
	}
}
