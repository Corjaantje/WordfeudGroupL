package Gamestate;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import GameObjects.ChallengeFrame;
import GameObjects.NotificationFrame;
import Main.GUI;
import model.User;
import controller.DatabaseController;

@SuppressWarnings("serial")
public class GamestateManager extends JPanel implements ActionListener{
	
	private DatabaseController db_c;
	private GUI gui;
	// keeps track of all gamestates
	private ArrayList<Gamestate> gamestates;
	private ArrayList<Integer> usedGamestates = new ArrayList<Integer>();
	
	// different types of gamestates
	public final static int loginState = 0;
	public final static int playState = 1;
	public final static int registerState = 2;
	public final static int playerNewWordState = 3;
	public final static int moderatorReviewWordState = 4;
	public final static int spectatorState = 5;
	public final static int gameOverviewState = 6;
	public final static int competitionState = 7;
	public final static int adminState = 8;
	public final static int rankingState = 9;
	public final static int mainMenuState = 10;
	public final static int spectatorCompetitionState = 11;
	public final static int spectatorGameOverviewState = 12;


	// displays the current state of the game
	private int currentState;
	
	private User user;
	
	private boolean invitePlayerInMenu;
	private boolean invitePlayerInitialized;
	public boolean notifyCreated;
	private JMenu challenge;
	private JMenuItem notify;
	private JMenuItem mainMenu;
	private ChallengeFrame challengeFrame;
	public NotificationFrame Notify;
	
	private boolean returnInitialized = false;
	private JMenuItem backButton;
	private boolean returnVisible = false;

	public GamestateManager(GUI gui) {
		this.gui = gui;
		create();
	}


	private void create() {
		db_c = new DatabaseController();
		this.setBackground(new Color(24, 24, 24));
		this.currentState = -1;
		this.gamestates = new ArrayList<Gamestate>();
		this.gamestates.add(new Loginstate(this,db_c));
		this.gamestates.add(new Playstate(this,db_c));
		this.gamestates.add(new Registerstate(this,db_c));
		this.gamestates.add(new NewWordstate(this, db_c));
		this.gamestates.add(new ReviewWordstate(this, db_c));
		this.gamestates.add(new SpectatorState(this,db_c));
		this.gamestates.add(new GameOverviewState(this, db_c));
		this.gamestates.add(new Competitionstate(this, db_c));
		this.gamestates.add(new AdminState(this, db_c));
		this.gamestates.add(new RankingState(this, db_c));
		this.gamestates.add(new MainMenuState(this, db_c));
		this.gamestates.add(new SpectatorCompetitionState(this, db_c));
		this.gamestates.add(new SpectatorGameOverviewState(this, db_c));
		// state you want to start with
		this.setGamestate(loginState, true);
		/*
		 * add all gamestates to the array list
		 */	
	}
	
	public void setGamestate(int gamestate) {
		setGamestate(gamestate, true);
	}

	public void setGamestate(int gamestate, boolean addToUsedGameStates) {
	
		if (currentState != -1) {
			this.remove(gamestates.get(currentState));
		}	
		if(addToUsedGameStates){
			usedGamestates.add(currentState);
		}
		
		this.currentState = gamestate;
		//TODO array loop gamestates, betere manier vinden
		this.add(gamestates.get(currentState));
		
		this.gamestates.get(currentState).create();
		
		//Return button
		if(gamestate != mainMenuState)
		{
			if(!returnInitialized)
			{
				System.out.println("init");
				backButton = new JMenuItem();
				backButton.setText("Terug");
				backButton.addActionListener(this);
				backButton.setActionCommand("return");
				returnInitialized = true;
				returnVisible=false;
			}
			if(!returnVisible && gui.menuCreated)
			{
				gui.bar.add(backButton);
				returnVisible = true;
				gui.repaint();
				gui.pack();
				this.validate();
			}
		}
		if(gamestate == mainMenuState || gamestate == loginState)
		{
			if(returnVisible)
			{
				gui.bar.remove(backButton);
				gui.repaint();
				gui.pack();
				this.validate();
				returnVisible=false;
			}
		}
		//Request game invite 
		if(gamestate == gameOverviewState)
		{
			if(!invitePlayerInitialized)
			{
				challenge = new JMenu("Uitdagen");
				mainMenu = new JMenuItem("Nieuwe speler uitdagen");	
				notify = new JMenuItem("Uitdaging Overzicht");
				invitePlayerInitialized = true;
				
				mainMenu.addActionListener(this);
				mainMenu.setActionCommand("invitePlayer");
				notify.addActionListener(this);
				notify.setActionCommand("notify");
				
				challengeFrame = new ChallengeFrame(this, db_c);
			}
			if(user.checkRole("player"))
			{
				if(!invitePlayerInMenu)
				{		
					gui.bar.remove(backButton);
					gui.bar.add(challenge);
					gui.bar.add(backButton);
					challenge.add(mainMenu);
					challenge.add(notify);		
					gui.repaint();
					gui.pack();
					this.validate();
					invitePlayerInMenu = true;
				}
			}
		}
		else
		{
			if(invitePlayerInMenu)
			{
				gui.bar.remove(this.challenge);
				invitePlayerInMenu = false;
			}
		}
		//End of game request
		
		this.validate();
	}

	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		this.gamestates.get(currentState).draw(g2d);
	}

	public void update() {
		this.gamestates.get(currentState).update();
	}
	
	//Set the user and add the necessary menus with the gui.createRoleMenus method.
	public void setUser(User user){
		this.user = user;
	}


	public User getUser()
	{
		return user;
	}
	
	public User getUser(String username){
		return user;
	}
	
	public DatabaseController getDatabaseController() {
		return db_c;
	}	
	
	public int getCurrentState()
	{
		return currentState;
	}
	
	public void goToLastState(){
		if(usedGamestates.size() == 1){
			return;
		}
		this.setGamestate(usedGamestates.get(usedGamestates.size() - 1), false);
											
		if (usedGamestates.get(usedGamestates.size() - 1)==loginState){
			this.setUser(null);
		}
		this.usedGamestates.remove(usedGamestates.size() - 1);
		this.validate();
	}


	@Override
	public void actionPerformed(ActionEvent e)
	{
		if("invitePlayer".equals(e.getActionCommand()))
		{
			challengeFrame.setVisible(true);
		}
		if("notify".equals(e.getActionCommand()))
		{
			if(!notifyCreated)
			{
				Notify = new NotificationFrame(this, db_c);
				Notify.setVisible(true);
				notifyCreated = true;
			}
			else
			{
				Notify.setVisible(true);
			}
		}
		if("return".equals(e.getActionCommand()))
		{
			goToLastState();
		}

	}
}
