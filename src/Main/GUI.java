package Main;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Label;
import java.awt.List;
import java.awt.Point;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import controller.AdminDBController;
import model.User;
import GameObjects.Button;
import GameObjects.NotificationFrame;
import Gamestate.GamestateManager;

@SuppressWarnings("serial")
public class GUI extends JFrame {

	public static final double WIDTH = Toolkit.getDefaultToolkit().getScreenSize().getWidth() + 10;
	public static final double HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 30;

	private int FPS = 60;
	private long targetTime = 1000 / FPS;
	public JMenu menu;
	public JMenuBar bar;
	private GamestateManager gsm;
	//player menu for adding words.	
	JMenu playerMenu = new JMenu("Player Menu");
	//end
	public GUI() {
		super();
		create();
		startGame();
	}

	private void create() {
		this.setUndecorated(true);
		Image image = Toolkit.getDefaultToolkit().getImage("Resources/wordfeudlogo.png");
		this.setIconImage(image);
		Image cursorImage = Toolkit.getDefaultToolkit().getImage("Resources/cursor.png");
		Cursor cursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImage, new Point(this.getX(), this.getY()), "cursorImg");
		this.setCursor(cursor);
		this.setTitle("Wordfeud");
		this.gsm = new GamestateManager(this);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setContentPane(gsm);
		this.createMenu();
		this.pack();
	}

	private void createMenu() {
		bar = new JMenuBar();
		menu = new JMenu("Menu");
	
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				ImageIcon icon = new ImageIcon("Resources/wordfeudLogo.png");
				int option = JOptionPane.showConfirmDialog(null, "All unsaved progress wil be lost",
						"Are you sure you want to quit?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
						icon);
				if (option == JOptionPane.OK_OPTION) {
					System.exit(0);
				}
			}
		});
		
		//TODO Remove Hoofdmenu State
		JMenuItem mainMenu = new JMenuItem("Hoofdmenu");
		mainMenu.addActionListener(new ActionListener() {
			@SuppressWarnings("static-access")
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try{
					if(gsm.getUser() != null)
					{
						if(gsm.getCurrentState() != gsm.mainMenuState)
						{
						gsm.setGamestate(gsm.mainMenuState);
						}
						else
						{
							JOptionPane.showMessageDialog(null, "Je bent al in het hoofdmenu!");
						}
					}
					else
					{
						JOptionPane.showMessageDialog(null, "Je bent nog niet ingelogd!");
					}
				}
				catch(NullPointerException npe){}
			}
		});
		
		
		JMenuItem backButton = new JMenuItem();
		backButton.setText("Terug");
		backButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				gsm.goToLastState();
			}
			
		});
		
		menu.add(exit);
		menu.add(mainMenu);
		menu.add(backButton);
		bar.add(menu);
		this.setJMenuBar(bar);
	}

	private void draw() {
		this.gsm.repaint();
	}

	private void update() {
		this.gsm.update();
	}

	private void startGame() {

		long start;
		long elapsed;
		long wait;

		// game loop
		while (true) {
			start = System.nanoTime();
			this.update();
			this.draw();

			elapsed = System.nanoTime() - start;

			wait = targetTime - elapsed / 1000000;
			if (wait < 0)
				wait = 5;

			try {
				Thread.sleep(wait);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
}