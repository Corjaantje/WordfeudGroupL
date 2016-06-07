package Main;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

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
	
	public boolean menuCreated = false;
	//end
	public GUI() {
		super();
		create();
		startGame();
	}

	private void create() {
		this.setUndecorated(true);
		Image cursorImage = null;
		try {
			cursorImage = ImageIO.read(this.getClass().getClassLoader().getResource("resources/cursor.png"));
		} catch (IOException e) {
			System.out.println("Something went wrong at the image loader: "+e.getMessage());
		}
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
	
		JMenuItem exit = new JMenuItem("Afsluiten");
		exit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				ImageIcon icon = new ImageIcon(this.getClass().getClassLoader().getResource("resources/wordfeudLogo.png"));
				int option = JOptionPane.showConfirmDialog(null, "De applicatie zal nu worden afgelosten",
						"Bent u zeker van deze keuze?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
						icon);
				if (option == JOptionPane.OK_OPTION) {
					System.exit(0);
				}
			}
		});		
		
		menu.add(exit);
		bar.add(menu);
		this.setJMenuBar(bar);
		menuCreated=true;
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