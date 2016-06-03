package GameObjects;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import Main.GUI;

public class ChatInput extends JPanel
{	
	public JTextField chatInput = new JTextField();
	public JButton sendButton = new JButton();
	public JButton configButton = new JButton();
	
	public ChatInput()
	{
		this.setPreferredSize(new Dimension((int) (GUI.WIDTH/20), (int) (GUI.HEIGHT/14)));;
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createLineBorder((new Color(0, 255, 0)), 3));
		this.addChatInput();
		this.addConfigButton();
		this.addSendButton();
	}
	
	private void addChatInput()
	{
		chatInput.setPreferredSize(new Dimension((int) (GUI.WIDTH/5.5), (int) (GUI.HEIGHT/300)));
		chatInput.setBackground(Color.BLACK);
		chatInput.setForeground(Color.WHITE);
		this.add(chatInput, BorderLayout.LINE_START);
	}

	private void addSendButton()
	{
		sendButton.setPreferredSize(new Dimension((int) (GUI.WIDTH/16), (int) (GUI.HEIGHT/300)));
		sendButton.setHorizontalTextPosition(SwingConstants.LEFT);
		sendButton.setText("Verzend");
		sendButton.setContentAreaFilled(false);
		
		this.add(sendButton, BorderLayout.LINE_END);
	}
	
	private void addConfigButton()
	{
		configButton.setPreferredSize(new Dimension((int) (GUI.WIDTH/100), (int) (GUI.HEIGHT/40)));
		configButton.setHorizontalTextPosition(SwingConstants.LEFT);
		configButton.setText("Chat Configuration");
		configButton.setContentAreaFilled(false);
		
		this.add(configButton, BorderLayout.BEFORE_FIRST_LINE);
	}
}
