package Gamestate;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import controller.DatabaseController;

public class Competitionstate extends Gamestate{
	
	JPanel competitionPanel;
	

	public Competitionstate(GamestateManager gsm, DatabaseController db_c) {
		super(gsm, db_c);
		competitionPanel = new JPanel();
		competitionPanel.setBackground(Color.black);
		competitionPanel.setLocation(0,0);
		competitionPanel.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(competitionPanel);
		
		
	}

	@Override
	public void draw(Graphics2D g) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void create() {
		// TODO Auto-generated method stub
		
	}

	
}
