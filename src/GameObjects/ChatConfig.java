package GameObjects;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ChatConfig extends JPanel implements ActionListener
{	
	private ChatOutput textPane;
	private String[] fonts = { "Verdana", "Times New Roman", "Cambria"};
	private JComboBox<String> fontList;
	
	private String[] fontsTypes = { "Plain", "Bold", "Cursive" };
	private JComboBox<Integer> fontTypesList;
	
	private String[] fontsSize = { "10", "11", "12", "13", "14"};
	private JComboBox<Integer> fontSizeList;
	
	private JLabel labelFontName;
	private JLabel labelFontType;
	private JLabel labelFontSize;
	
	private String fontName = fonts[0];
	private int fontType = Font.PLAIN;
	private int fontSize = 10;
	
	
	public ChatConfig(ChatOutput textarea)
	{
		textPane = textarea;
		fontList = new JComboBox<>(fonts);
		fontList.setSelectedItem(1);
		fontList.addActionListener(this);
		
		fontTypesList = new JComboBox(fontsTypes);
		fontTypesList.setSelectedItem(1);
		fontTypesList.addActionListener(this);
		
		fontSizeList = new JComboBox(fontsSize);
		fontSizeList.setSelectedItem(1);
		fontSizeList.addActionListener(this);
		
		labelFontName = new JLabel("Letter");
		labelFontType = new JLabel("Type");
		labelFontSize = new JLabel("Grootte");
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
		this.addButtons();
	}
	
	private void addButtons()
	{
		this.add(labelFontName);
		this.add(fontList);
		this.add(labelFontType);
		this.add(fontTypesList);
		this.add(labelFontSize);
		this.add(fontSizeList);
		
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		JComboBox<String> combo = (JComboBox<String>) e.getSource();
		String selectedFont = (String) combo.getSelectedItem();
		if(selectedFont.equals("Verdana"))
		{
			this.fontName = "Verdana";
		}
		else if(selectedFont.equals("Times New Roman"))
		{
			this.fontName = "Times New Roman";
		}
		else if(selectedFont.equals("Cambria"))
		{
			this.fontName = "Cambria";
		}
		else if(selectedFont.equals("Plain"))
		{
			this.fontType = Font.PLAIN;
		}
		else if(selectedFont.equals("Bold"))
		{
			this.fontType = Font.BOLD;
		}
		else if(selectedFont.equals("Cursive"))
		{
			this.fontType = Font.ITALIC;
		}
		else if(selectedFont.equals("10"))
		{
			this.fontSize = 10;
		}
		else if(selectedFont.equals("11"))
		{
			this.fontSize = 11;
		}
		else if(selectedFont.equals("12"))
		{
			this.fontSize = 12;
		}
		else if(selectedFont.equals("13"))
		{
			this.fontSize = 13;
		}
		else if(selectedFont.equals("14"))
		{
			this.fontSize = 14;
		}
		textPane.chatOutput.setFont(new Font(fontName, fontType, fontSize));
			
	}
}
