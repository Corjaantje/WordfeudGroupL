package Gamestate;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import controller.LoginController;
import Main.GUI;
import controller.DatabaseController;

@SuppressWarnings("serial")

public class Loginstate extends Gamestate implements ActionListener, KeyListener{

	private JPanel loginPanel;
	private Image bgImage;
	private LoginController loginController;

	private JTextField user;
	private JPasswordField password;
	
	private JButton switchToRegister;
	

	public Loginstate(GamestateManager gsm,DatabaseController db_c) {
		super(gsm,db_c);
		loginController = new LoginController(gsm);
		this.loginPanel = new JPanel();
		this.loginPanel.setBackground(Color.gray);
		this.loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.PAGE_AXIS));
		this.loginPanel.setBorder(BorderFactory.createLineBorder(Color.gray, 5, true));
		this.add(loginPanel);

		user = new JTextField(10);
		user.setText("allrights");			//For Testing Only!
		password = new JPasswordField(10);
		password.setText("12345");			//For Testing Only!

		password.addKeyListener(this); //Hit enter while in password to activate the login
		
		JButton login = new JButton("Login");	
		login.setBackground(Color.lightGray);
		login.setActionCommand("login");
		login.addActionListener(this);
		
		Box userBox = Box.createHorizontalBox();
		userBox.setBorder(BorderFactory.createLineBorder(Color.darkGray, 4, true));
		userBox.add(new JLabel("Gebruikernm:"));
		userBox.add(user);
		

		Box passwordBox = Box.createHorizontalBox();
		passwordBox.setBorder(BorderFactory.createLineBorder(Color.darkGray, 4, true));
		passwordBox.add(new JLabel("Wachtwoord:"));
		passwordBox.add(password);
		
		
		switchToRegister = new JButton();
		switchToRegister.setText("Nieuw Account?");	
		switchToRegister.setBackground(Color.lightGray);
		switchToRegister.setActionCommand("switchRegister");
		switchToRegister.addActionListener(this);
		
		this.loginPanel.add(userBox);
		this.loginPanel.add(passwordBox);
		this.loginPanel.add(Box.createRigidArea(new Dimension(0,5)));
		this.loginPanel.add(login);
		this.loginPanel.add(Box.createRigidArea(new Dimension(0,10)));
		this.loginPanel.add(switchToRegister);
	
		this.bgImage = getToolkit().getImage("Resources/WordfeudLogin.png");
	}

	@Override
	public void draw(Graphics2D g) {

		int width = getWidth() / 1;
		int height = (int) (getHeight() / 1.1);
		int x = (int) (GUI.WIDTH / 2 - (width/2));
		int y = getWidth() / 18;
		
		g.drawImage(bgImage, x, y, width, height, null);
	}

	@Override
	public void update() {

	}

	// intialize gamestate at start
	@Override
	public void create() {

	}

	@SuppressWarnings("static-access")
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if("login".equals(e.getActionCommand()))
		{
			this.excecuteLogin();
		}
		else if("switchRegister".equals(e.getActionCommand()))
		{
			gsm.setGamestate(gsm.registerState);
		}
	}
	
	private void excecuteLogin()
	{
		String usernameField = user.getText();
		@SuppressWarnings("deprecation")
		String passwordField = password.getText();
		loginController.login(usernameField, passwordField);	
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			this.excecuteLogin();
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{}

	@Override
	public void keyTyped(KeyEvent e)
	{}
}
