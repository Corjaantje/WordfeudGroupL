package GameObjects;

import java.awt.Dimension;

import javax.swing.JFrame;

import Main.GUI;

public class ChatConfigFrame extends JFrame
{
	private ChatConfig config; 
	
	public ChatConfigFrame(ChatOutput textarea)
	{
		this.setPreferredSize(new Dimension((int) (GUI.WIDTH/3), (int) (GUI.HEIGHT/5.75)));;
		this.setLocation((int)(GUI.WIDTH/3), (int)GUI.HEIGHT/3);
		this.setUndecorated(true);
		this.setResizable(false);
		this.setTitle("Chat Configuratie");
		this.addConfigPanel(textarea);
		this.pack();
	}
	
	private void addConfigPanel(ChatOutput textarea)
	{
		config = new ChatConfig(textarea);
		this.add(config);
	}
}
