package GameObjects;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

import controller.DatabaseController;
import Gamestate.GamestateManager;
import Main.GUI;

@SuppressWarnings("serial")
public class ChallengeFrame extends JFrame
{
	public ChallengeFrame(GamestateManager gsm, DatabaseController db_c)
	{
		this.setPreferredSize(new Dimension((int) (GUI.WIDTH/4.9), (int) (GUI.HEIGHT/4)));;
		this.setLocation((int)(GUI.WIDTH/2.49), (int)(GUI.HEIGHT/6));
		this.setResizable(false);
		this.setTitle("Speler Uitdagen");
		this.setLayout(new BorderLayout());
		ChallengePanel Challenge = new ChallengePanel(gsm, db_c);
		this.add(Challenge);
		this.pack();
	}
}
