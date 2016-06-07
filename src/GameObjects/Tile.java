package GameObjects;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;

import javax.imageio.ImageIO;

import Main.Drawable;

public class Tile implements Drawable {

	private double x;
	private double y;

	private int width;
	private int height;
	
	private int bordX;
	private int bordY;

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
		String url = "";
		if (score.equals("--")) {
			url = "resources/Scores/NONE.png";
		} else if (score.equals("TL")) {
			url = "resources/Scores/TL.png";
		} else if (score.equals("DW")) {
			url = "resources/Scores/DW.png";
		} else if (score.equals("TW")) {
			url = "resources/Scores/TW.png";
		} else if (score.equals("DL")) {
			url = "resources/Scores/DL.png";
		} else if (score.equals("BOX")){
			url = "resources/LetterBox.png";
		} else {
			url = "resources/Scores/START.png";
		}
		try {
			image = ImageIO.read(this.getClass().getClassLoader().getResource(url));
		} catch (IOException e) {
			e.printStackTrace();
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
	
	public int getBordX() {
		return bordX;
	}

	public void setBordX(int bordX) {
		this.bordX = bordX;
	}

	public int getBordY() {
		return bordY;
	}

	public void setBordY(int bordY) {
		this.bordY = bordY;
	}
}
