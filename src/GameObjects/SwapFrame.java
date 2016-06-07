package GameObjects;

import java.awt.Toolkit;

import javax.swing.JFrame;

import Gamestate.GamestateManager;
import Main.GUI;
import controller.DatabaseController;

public class SwapFrame extends JFrame{
	
	private SwapPane swapPane;

	public SwapFrame(LetterBox letterBox,DatabaseController db_c,GamestateManager gsm){
		this.setLocation((int)(GUI.WIDTH/3), (int)GUI.HEIGHT/3);
		this.setResizable(false);
		this.setTitle("Verwissel je letters!");
		swapPane = new SwapPane(letterBox,db_c,gsm);
		this.setContentPane(swapPane);
		this.pack();
	}
	
	public void reloadSwapFrame(){
		swapPane.reloadSwapPane();
	}
	
}
