package Gamestate;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import model.User;
import controller.AdminDBController;
import controller.DatabaseController;
import controller.InfoUserController;

public class InfoUserState extends Gamestate{
	private InfoUserController infoC;
	private AdminDBController adbc;
	
	private User currentUser;
	
	private JFrame infoFrame;
	private JPanel userInfo;
	private JLabel usernameLabel;
	private JLabel passwordLabel;
	private JLabel roleLabel;
	private JLabel adminLabel;
	private JLabel moderatorLabel;
	private JLabel spectatorLabel;
	private JLabel playerLabel;
	private JTextField changePassword;
	private JButton changePasswordButton;
	
	private JLabel lineToSeperate = new JLabel("=======================================");

	public InfoUserState(GamestateManager gsm, DatabaseController db_c) {
		super(gsm, db_c);
		infoC = new InfoUserController(gsm);
		adbc = new AdminDBController(gsm);
		makeInfoFrame();
	}
	
	public void makeInfoFrame(){
		infoFrame = new JFrame();
		infoFrame.setTitle("Gegevens gebruiker");
		infoFrame.setPreferredSize(new Dimension(300, 220));
		infoFrame.setLayout(new BorderLayout());
		
		userInfo = new JPanel();
		userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.Y_AXIS));
		
		String currentUsername = gsm.getUser().getUsername();
		
		usernameLabel = new JLabel("Gebruikersnaam: " + currentUsername);
		passwordLabel = new JLabel("Wachtwoord: " + infoC.getPassword(currentUsername));
		
		userInfo.add(usernameLabel);
		userInfo.add(passwordLabel);
		
		changePassword = new JTextField();
		changePassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, changePassword.getMinimumSize().height));
		changePasswordButton = new JButton("Wijzig wachtwoord");
		changePasswordButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				String password = changePassword.getText();
				int passwordLength = password.length();
				if(((passwordLength >= 5) && (passwordLength <= 25)) && password.matches("[A-za-z0-9]+")){
					infoC.setPassword(currentUsername, password);
					passwordLabel.setText("Wachtwoord: " + password);
				}else{
					JOptionPane.showMessageDialog(null, "Wachtwoord kan alleen uit letters en cijfers bestaan en moet tussen de 5 en 25 karakters lang zijn");
				}
				passwordLabel.setText("Wachtwoord: " + password);
			}
		});
		userInfo.add(changePassword);
		userInfo.add(changePasswordButton);
		
		userInfo.add(lineToSeperate);		
		roleLabel = new JLabel("Rollen: ");
		adminLabel = new JLabel("Je bent geen administrator");
		moderatorLabel = new JLabel("Je bent geen moderator");
		spectatorLabel = new JLabel("Je bent geen observer");
		playerLabel = new JLabel("Je bent geen speler");
		
		userInfo.add(roleLabel);
		
		if(adbc.checkUserIsRole(currentUsername, "administrator")){
			adminLabel.setText("Je bent een administrator");
		}if(adbc.checkUserIsRole(currentUsername, "moderator")){
			moderatorLabel.setText("Je bent een moderator");
		}if(adbc.checkUserIsRole(currentUsername, "observer")){
			spectatorLabel.setText("Je bent een observer");
		}if(adbc.checkUserIsRole(currentUsername, "player")){
			playerLabel.setText("Je bent een speler");
		}
		
		userInfo.add(adminLabel);
		userInfo.add(moderatorLabel);
		userInfo.add(spectatorLabel);
		userInfo.add(playerLabel);
		
		infoFrame.add(userInfo);
		infoFrame.pack();
		infoFrame.setLocationRelativeTo(null);
		infoFrame.setVisible(true);
	}

	@Override
	public void draw(Graphics2D g) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void create() {
		currentUser = gsm.getUser();
		this.makeInfoFrame();
	}
	
}
