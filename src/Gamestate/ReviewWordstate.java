package Gamestate;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import Main.GUI;
import controller.DatabaseController;
import controller.NewWordController;
import controller.ReviewWordController;

public class ReviewWordstate extends Gamestate
{

	private JPanel reviewWordsPanel, pendingWordsPanel;
	private Image bgImage;
	private ReviewWordController reviewWordController;
	private NewWordController newWordController;
	private JScrollPane pendingWordsScrollPane;
	private JButton acceptWordButton = new JButton("Accepteer");
	private JButton denyWordButton = new JButton("Wijs af");
	private JLabel pendingWordsLabel = new JLabel("Beoordeelbare woorden:");
	private JComboBox<String> lettersetComboBox;
	public ReviewWordstate(GamestateManager gsm, DatabaseController db_c)
	{
		super(gsm, db_c);
		// TODO Auto-generated constructor stub
		reviewWordController = new ReviewWordController(gsm, this);
		this.reviewWordsPanel = new JPanel();
		this.reviewWordsPanel.setBackground(Color.gray);
		this.reviewWordsPanel.setLocation(0, 0);
		this.reviewWordsPanel.setLayout(new BoxLayout(reviewWordsPanel, BoxLayout.PAGE_AXIS));
		this.reviewWordsPanel.setBorder(BorderFactory.createLineBorder(Color.gray, 5, true));
		this.add(reviewWordsPanel);
		
		this.bgImage = getToolkit().getImage("Resources/wordfeudLogo.png");
		
		
		JLabel lettersetLabel = new JLabel("Selecteer de letterset:");
		reviewWordsPanel.add(lettersetLabel);
		
		lettersetComboBox = new JComboBox<>();
		fillLetterSetComboBox(lettersetComboBox);
		reviewWordsPanel.add(lettersetComboBox);
		
		JButton reviewWordButton = new JButton("Laat woorden zien/vernieuw");
		reviewWordsPanel.add(reviewWordButton);
		reviewWordButton.addActionListener(e -> createAddedWordList());
		
		this.pendingWordsPanel = new JPanel();
		pendingWordsPanel.setLayout(new BoxLayout(pendingWordsPanel, BoxLayout.PAGE_AXIS));
		this.add(pendingWordsPanel);
		
		acceptWordButton.addActionListener(e -> reviewWordController.acceptWord((String) ((JList)pendingWordsScrollPane.getViewport().getView()).getSelectedValue(), (String) lettersetComboBox.getSelectedItem()));
		denyWordButton.addActionListener(e -> reviewWordController.denyWord((String) ((JList)pendingWordsScrollPane.getViewport().getView()).getSelectedValue(), (String) lettersetComboBox.getSelectedItem()));
		
		
	}
	
	// get the codes from all the lettersets
		private ArrayList<String> getLetterSets() {
			ArrayList<String> allLetterSets = new ArrayList<>();
			ResultSet rSet = db_c.query("select * from letterset");
			
			try {
				while(rSet.next()) {
					allLetterSets.add(rSet.getString("code"));
				}
				
			} catch(SQLException e) {
				e.printStackTrace();
			}
			return allLetterSets;
		}
	
	public void fillLetterSetComboBox(JComboBox<String> lettersetComboBox)
	{
		for (String lettersetCode : getLetterSets()) {
			lettersetComboBox.addItem(lettersetCode);
		}
	}

	public void createAddedWordList() {
		String letterset = (String) lettersetComboBox.getSelectedItem();
		JList pendingWordsList = reviewWordController.generatePendingWordsList(letterset);
		
		if (pendingWordsScrollPane != null){
			pendingWordsPanel.remove(pendingWordsScrollPane);
			pendingWordsPanel.remove(acceptWordButton);
			pendingWordsPanel.remove(denyWordButton);
			pendingWordsPanel.remove(pendingWordsLabel);
		}
		pendingWordsPanel.add(pendingWordsLabel);
		
		pendingWordsScrollPane = new JScrollPane(pendingWordsList);
		pendingWordsScrollPane.setPreferredSize((new Dimension(150, 150)));
		pendingWordsPanel.add(pendingWordsScrollPane);
		
		pendingWordsPanel.add(acceptWordButton);
		pendingWordsPanel.add(denyWordButton);
		
		pendingWordsPanel.revalidate();
		pendingWordsPanel.repaint();
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		// TODO Auto-generated method stub
		int width = getWidth() / 3;
		int height = (int) (getHeight() / 1.75);
		int x = (int) (GUI.WIDTH / 2 - (width/2));
		int y = getWidth() / 8;
		
		g.drawImage(bgImage, x, y, width, height, null);
	}

	@Override
	public void update()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void create()
	{
		// TODO Auto-generated method stub

	}

}
