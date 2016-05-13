package Gamestate;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import Main.GUI;
import controller.DatabaseController;
import controller.RegisterController;

@SuppressWarnings("serial")
public class Registerstate extends Gamestate implements ActionListener{
	private JPanel registerPanel;
	private Image bgImage;
	private RegisterController registerController;
	
	private JTextField user;
	private JPasswordField password;
	private JButton switchToLogin;
	
	public Registerstate(GamestateManager gsm,DatabaseController db_c) {
		super(gsm,db_c);
		registerController = new RegisterController(gsm);
		this.registerPanel = new JPanel();
		this.registerPanel.setBackground(Color.gray);
		this.registerPanel.setLocation(0, 0);
		this.registerPanel.setLayout(new BoxLayout(registerPanel, BoxLayout.PAGE_AXIS));
		this.registerPanel.setBorder(BorderFactory.createLineBorder(Color.gray, 5, true));
		this.add(registerPanel);

		user = new JTextField(10);
		password = new JPasswordField(10);

		JButton register = new JButton("Registreren");
		register.setBackground(Color.lightGray);
		register.setActionCommand("register");
		register.addActionListener(this);

		Box userBox = Box.createHorizontalBox();
		userBox.setBorder(BorderFactory.createLineBorder(Color.darkGray, 4, true));
		userBox.add(new JLabel("Gebruikernm:"));
		userBox.add(user);
		

		Box passwordBox = Box.createHorizontalBox();
		passwordBox.setBorder(BorderFactory.createLineBorder(Color.darkGray, 4, true));
		passwordBox.add(new JLabel("Wachtwoord:"));
		passwordBox.add(password);
		
		switchToLogin = new JButton();
		switchToLogin.setText("Terug naar inloggen...");	
		switchToLogin.setBackground(Color.lightGray);
		switchToLogin.setActionCommand("switchLogin");
		switchToLogin.addActionListener(this);

		this.registerPanel.add(userBox);
		this.registerPanel.add(passwordBox);
		this.registerPanel.add(Box.createRigidArea(new Dimension(0,10)));
		this.registerPanel.add(register);
		this.registerPanel.add(Box.createRigidArea(new Dimension(0,10)));
		this.registerPanel.add(switchToLogin);
		this.bgImage = getToolkit().getImage("Resources/WordfeudRegister.png");
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

	@SuppressWarnings({ "deprecation", "static-access" })
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if("register".equals(e.getActionCommand()))
		{
			String usernameField = user.getText();
			String passwordField = password.getText();
			registerController.register(usernameField, passwordField);
		}
		else if("switchLogin".equals(e.getActionCommand()))
		{
			gsm.setGamestate(gsm.loginState);
		}
	}
}
