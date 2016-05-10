package Gamestate;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;

import Main.Drawable;
import Main.GUI;
import controller.DatabaseController;

@SuppressWarnings("serial")
public abstract class Gamestate extends JPanel implements Drawable {

	protected GamestateManager gsm;
	protected DatabaseController db_c;

	public Gamestate(GamestateManager gsm,DatabaseController db_c) {
		this.gsm = gsm;
		this.db_c = db_c;
		this.setPreferredSize(new Dimension((int) GUI.WIDTH, (int) GUI.HEIGHT));
		this.setBackground(new Color(24, 24, 24));
	}

	public abstract void create();

}
