package GameObjects;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;

import Gamestate.GamestateManager;
import Main.Drawable;

public class TurnIndicator implements Drawable{
	
	private GamestateManager gsm;
	
	private double x;
	private double y;
	
	private int diameter;
	
	private int location;
	
	private int score = 10;
	
	public TurnIndicator(GamestateManager gsm,double tileWidth) {
		this.gsm = gsm;
		diameter = (int) (tileWidth / 2.5);
		location = (int) (tileWidth / 1.3);
	}

	@Override
	public void draw(Graphics2D g) {
		// TODO Auto-generated method stub
		g.setColor(Color.yellow);
		g.fillOval((int)x+location, (int)y+location, diameter, diameter);
		g.setColor(Color.BLACK);
		g.drawOval((int)x+location, (int)y+location, diameter, diameter);
		g.setFont(new Font("Arial", Font.PLAIN, 9));
		if (Integer.toString(score).length() == 1) {
			g.drawString(""+score, (int)(x+location+(diameter/2.5)), (int)(y+location+(diameter/1.2)));
		}else{
			g.drawString(""+score, (int)(x+location+(diameter/4.4)), (int)(y+location+(diameter/1.2)));
		}
		
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
	
	public void resetTurnIndicator(){
		x = -100;
		y = -100;
		score = 0;
	}
	
	public void setToPoint(Point point){
		x = point.getX();
		y = point.getY();
	}

}
