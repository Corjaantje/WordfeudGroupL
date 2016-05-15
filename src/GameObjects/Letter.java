package GameObjects;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.IconifyAction;

import Main.Drawable;

public class Letter implements Drawable {

	private boolean rightLocation;
	private boolean isReset;

	private double headingX;
	private double headingY;

	private double headingWidth;
	private double headingHeight;

	private double vectorX;
	private double vectorY;

	private double speed;

	private double startX;
	private double startY;

	private double startWidth;
	private double startHeight;

	private double x;
	private double y;

	private double width;
	private double height;

	private String letter;
	private int score;

	private double sizer;

	private Image bgImage;

	private Font font;
	
	private int bordX;
	private int bordY;
	
	private double playfieldX;
	private int playfieldSpace;
	
	private int letter_id;

	public Letter(double x, double y, int width, int height, String letter, int score) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.letter = letter;
		this.score = score;
		startX = x;
		startY = y;
		startWidth = width;
		startHeight = height;
		this.setWantedSize(width, height);
		speed = 0.2;
		rightLocation = true;
		font = new Font("Lucida Grande", Font.BOLD, 10);
		bgImage = Toolkit.getDefaultToolkit().getImage("Resources/backgroundTile.png");
	}

	@Override
	public void draw(Graphics2D g) {
		// TODO Auto-generated method stub
		g.drawImage(bgImage, (int) x, (int) y, (int) width, (int) height, null);
		g.setColor(Color.BLACK);
		g.setFont(font);
		g.drawString(letter, (int) (x + (width / 2.2)), (int) (y + (height / 2)));
		g.drawString("" + score, (int) (x + width / 1.5), (int) (y + height / 1.2));
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		if (x <= headingX + vectorX && x >= headingX - vectorX && y <= headingY + vectorY && y >= headingY - vectorY) {
			rightLocation = true;
			x = headingX;
			y = headingY;
		} else {
			if (x <= headingX && y <= headingY) {
				x += vectorX;
				y += vectorY;
			} else if (x <= headingX && y >= headingY) {
				x += vectorX;
				y -= vectorY;
			} else if (x >= headingX && y >= headingY) {
				x -= vectorX;
				y -= vectorY;
			} else {
				x -= vectorX;
				y += vectorY;
			}
		}
		if (!isReset && width > headingWidth && height > headingHeight) {
			width -= sizer;
			height -= sizer;
		} else if (isReset && width < startWidth && height < startHeight) {
			width += sizer;
			height += sizer;
		}
	}

	public void reset() {
		if (startX != x && startY != y) {
			if (startHeight != height && startWidth != width) {
				calculateRoute(startX, startY);
			}
		}
		isReset = true;
	}

	public void calculateRoute(double headingX, double headingY) {
		this.headingX = headingX;
		this.headingY = headingY;
		double lenX = Math.max(headingX, x) - Math.min(headingX, x);
		double lenY = Math.max(headingY, y) - Math.min(headingY, y);
		double vector = Math.sqrt(lenX + lenY);
		vectorX = (lenX / vector) * speed;
		vectorY = (lenY / vector) * speed;
		rightLocation = false;
		isReset = false;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
		headingX = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
		headingY = y;
	}

	public double getWidth() {
		// TODO Auto-generated method stub
		return width;
	}

	public double getHeight() {
		// TODO Auto-generated method stub
		return height;
	}

	public void setWantedSize(int width, int height) {
		this.headingHeight = height;
		this.headingWidth = width;
		sizer = 1.1;
	}

	public boolean getRightLocation() {
		return rightLocation;
	}

	public boolean isOnStartPosition() {
		if (x - 1 < startX && x + 1 > startX) {
			if (y - 1 < startY && y + 1 > startY) {
				return true;
			}
		}
		return false;
	}
	
	public String getLetterChar(){
		return letter;
	}
	
	public int getCorrectedXInt() {
		return (int)(x/50-10);
	}
	
	public int getCorrectedYInt() {
		return (int)(y/50+1);
	}
	
	public boolean isOnPlayField() {
		if (getCorrectedYInt() <= 15) {
			return true;
		}
		return false;
	}

	//test v
	
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
	
	public void setPlayfieldX(double x){
		playfieldX = x;
		playfieldSpace = 2;
	}
	
	public void setLetterID(int id){
		letter_id = id;
	}
	
	public int getLetterID(){
		return letter_id;
	}
	
}
