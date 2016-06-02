package GameObjects;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;

import Main.Drawable;

public class Button implements Drawable {

	private int x;
	private int y;

	private int width;
	private int height;

	private String text;

	private Color buttonColor;

	private Font buttonFont;

	private Image buttonImg;
	private Image selectedButtonImg;

	private boolean isSelected;

	public Button(int x, int y, int width, int height, String text) {
		this.x = x;
		this.y = y;
		this.height = height;
		this.width = width;
		this.text = text;
		buttonColor = Color.WHITE;
		buttonFont = new Font("Verdana", Font.BOLD, 12);
		buttonImg = Toolkit.getDefaultToolkit().getImage("Resources/ButtonImg.png");
		selectedButtonImg = Toolkit.getDefaultToolkit().getImage("Resources/SelectedButtonImg.png");
	}

	@Override
	public void draw(Graphics2D g) {
		// TODO Auto-generated method stub
		if (isSelected) {
			g.drawImage(selectedButtonImg, x-5, y-2, width+15, height+5, null);
		} else {
			g.drawImage(buttonImg, x-5, y-2, width+15, height+5, null);
		}
		g.setColor(Color.WHITE);
		g.setFont(buttonFont);
		g.drawString(text, x + (width / 4)-5, y + (height / 2));
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	public void setButtonColor(Color color) {
		buttonColor = color;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public String getText() {
		return text;
	}
	
	public void setIsSelected(boolean isSelected){
		this.isSelected = isSelected;
	}
	
	public boolean getIsSelected(){
		return isSelected;
	}
}
