package GameObjects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import Gamestate.GamestateManager;
import Main.Drawable;

@SuppressWarnings("serial")
public class ButtonPanel implements Drawable {

	private ArrayList<Button> buttons;
	
	private boolean buttonIsPushed;

	private String[] buttonText = { "Play","Reset", "Pass", "Shuffle", "Swap", "Resign" };

	private int x;
	private int y;

	private int width;
	private int height;

	public ButtonPanel(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.height = height;
		this.width = width;
		createButtons();
	}

	@Override
	public void draw(Graphics2D g) {
		g.setColor(Color.black);
		g.fillRect(x-2, y, width, height+2);
		drawButtons(g);
	}

	@Override
	public void update() {

	}

	private void createButtons() {
		buttons = new ArrayList<Button>();
		int buttonWidth = width / buttonText.length;
		int buttonX = x;
		for (int i = 0; i < buttonText.length; i++) {
			buttons.add(new Button(buttonX, y, buttonWidth, height, buttonText[i]));
			buttonX += buttonWidth;
		}
	}

	private void drawButtons(Graphics2D g) {
		for (Button button : buttons) {
			button.draw(g);
		}
	}

	public void mousePressed(MouseEvent e) {
		for (int i = 0; i < buttons.size(); i++) {
			if (!this.getButtonsAreSelected()&&e.getY() > buttons.get(i).getY() && e.getX() > buttons.get(i).getX()) {
				if (!this.getButtonsAreSelected()&&e.getY() < buttons.get(i).getY() + buttons.get(i).getHeight()
						&& e.getX() < buttons.get(i).getX() + (buttons.get(i).getHeight()*1.8)) {
					buttons.get(i).setIsSelected(true);
				}
			}
		}
	}
	
	public void mouseReleased(MouseEvent e){
		for (Button button:buttons) {
			if (button.getIsSelected()) {
				button.setIsSelected(false);
			}
		}
	}
	
	public int getEndY(){
		return y+height;
	}
	
	public ArrayList<Button> getButtons(){
		return buttons;
	}
	
	public boolean getButtonsAreSelected(){
		boolean buttonsAreSelected = false;
		for (Button button : buttons) {
			if (button.getIsSelected()) {
				buttonsAreSelected = true;
			}
		}
		return buttonsAreSelected;
	}
}
