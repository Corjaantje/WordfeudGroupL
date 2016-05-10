package GameObjects;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;

import Main.Drawable;

public class Tile implements Drawable {

	private double x;
	private double y;

	private int width;
	private int height;
	
	private String score;

	private Image image;
	
	private boolean isEmpty;
	
	private Letter letter;

	public Tile(double x, double y, int width, int height, String score) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.score = score;
		this.width = width;
		this.height = height;
		this.score = score;
		this.isEmpty = true;
		if (score.equals("--")) {
			image = Toolkit.getDefaultToolkit().getImage("Resources/Scores/NONE.png");
		} else if (score.equals("TL")) {
			image = Toolkit.getDefaultToolkit().getImage("Resources/Scores/TL.png");
		} else if (score.equals("DW")) {
			image = Toolkit.getDefaultToolkit().getImage("Resources/Scores/DW.png");
		} else if (score.equals("TW")) {
			image = Toolkit.getDefaultToolkit().getImage("Resources/Scores/TW.png");
		} else if (score.equals("DL")) {
			image = Toolkit.getDefaultToolkit().getImage("Resources/Scores/DL.png");
		} else {
			image = Toolkit.getDefaultToolkit().getImage("Resources/Scores/START.png");
		}
	}

	@Override
	public void draw(Graphics2D g) {
		if (image != null) {
			g.drawImage(image, (int) x, (int) y, width, height, null);
		} 
	} 

	@Override
	public void update() {
		
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
	public String getScore(){
		return score;
	}
	
	public void setLetter(Letter letter){
		this.letter = letter;
		isEmpty = false;
	}
	
	public void removeLetter(){
		letter = null;
		isEmpty = true;
	}
	
	public Letter getLetter(){
		return letter;
	}
	
	public boolean getIsEmpty(){
		return isEmpty;
	}
}
