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
	public NotificationFrame(GamestateManager gsm)
	{
		this.setPreferredSize(new Dimension((int) (GUI.WIDTH/2.8), (int) (GUI.HEIGHT/1.5)));;
		this.setLocation((int)(GUI.WIDTH/3.1), (int)(GUI.HEIGHT/6));
		this.setResizable(false);
		this.setTitle("Meldingen");
		this.setLayout(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane(new NotificationPanel(gsm, this));
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		this.add(scrollPane);
		this.pack();
	}
}
