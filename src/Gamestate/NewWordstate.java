package Gamestate;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.LayoutManager;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.mysql.fabric.xmlrpc.base.Array;

import Main.GUI;
import controller.DatabaseController;
import controller.NewWordController;

public class NewWordstate extends Gamestate
{
	private JPanel newWordPanel, addedWordsPanel;
	private Image bgImage;
	private NewWordController newWordController;
	
	private JScrollPane addedWordsScrollPane;
	
	public NewWordstate(GamestateManager gsm, DatabaseController db_c)
	{
		super(gsm, db_c);
		newWordController = new NewWordController(gsm);
		this.newWordPanel = new JPanel();
		this.newWordPanel.setBackground(Color.gray);
		this.newWordPanel.setLocation(0, 0);
		this.newWordPanel.setLayout(new BoxLayout(newWordPanel, BoxLayout.PAGE_AXIS));
		this.newWordPanel.setBorder(BorderFactory.createLineBorder(Color.gray, 5, true));
		this.add(newWordPanel);
		
		this.bgImage = getToolkit().getImage("Resources/wordfeudLogo.png");
		
		JLabel newWordLabel = new JLabel("Nieuw woord:");
		newWordPanel.add(newWordLabel);
		JTextField newWordTextField = new JTextField(10);
		newWordPanel.add(newWordTextField);
		JLabel lettersetLabel = new JLabel("Letterset:");
		newWordPanel.add(lettersetLabel);
		
		JComboBox<String> lettersetComboBox = new JComboBox<>();
		newWordController.fillLetterSetComboBox(lettersetComboBox);
		newWordPanel.add(lettersetComboBox);
		
		JButton newWordButton = new JButton("Voeg toe");
		newWordPanel.add(newWordButton);
		
		newWordButton.addActionListener(e -> newWordController.addNewWord(newWordTextField.getText().toLowerCase(), (String)lettersetComboBox.getSelectedItem()));
	
		this.addedWordsPanel = new JPanel();
		addedWordsPanel.setLayout(new BoxLayout(addedWordsPanel, BoxLayout.PAGE_AXIS));
		this.add(addedWordsPanel);

		JButton showRefreshAddedWordsButton = new JButton("Laat toegevoegde woorden zien/vernieuw");
		addedWordsPanel.add(showRefreshAddedWordsButton);
		showRefreshAddedWordsButton.addActionListener(e -> createAddedWordList());
		
		JLabel addedWordsLabel = new JLabel("Toegevoegde woorden:");
		addedWordsPanel.add(addedWordsLabel);
		
	}
	
	private void createAddedWordList() {
		JList addedWordsList = newWordController.generateAddedWordsList();
		
		if (addedWordsScrollPane != null){
			addedWordsPanel.remove(addedWordsScrollPane);
		}
		addedWordsScrollPane = new JScrollPane(addedWordsList);
		addedWordsScrollPane.setPreferredSize((new Dimension(150, 180)));
		addedWordsPanel.add(addedWordsScrollPane);
		
		addedWordsPanel.revalidate();
		addedWordsPanel.repaint();
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
