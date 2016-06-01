package GameObjects;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import controller.DatabaseController;
import Gamestate.GamestateManager;
import Main.GUI;

public class NotificationFrame extends JFrame
{
	private NotificationPanel notifyPanel;
	
	public NotificationFrame(GamestateManager gsm, DatabaseController dbc)
	{
		this.setPreferredSize(new Dimension((int) (GUI.WIDTH/2.8), (int) (GUI.HEIGHT/1.5)));;
		this.setLocation((int)(GUI.WIDTH/3.1), (int)(GUI.HEIGHT/6));
		this.setResizable(false);
		this.setTitle("Meldingen");
		this.setLayout(new BorderLayout());
		notifyPanel = new NotificationPanel(gsm, this, dbc);
		JScrollPane scrollPane = new JScrollPane(notifyPanel);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		this.add(scrollPane);
		this.pack();
	}
	
	public void updateNotifications()
	{
		notifyPanel.removeAll();
		notifyPanel.updateNotifications();
	}
}
