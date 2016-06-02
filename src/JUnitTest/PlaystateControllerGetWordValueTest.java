package JUnitTest;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;


import org.junit.Before;
import org.junit.Test;

import GameObjects.Letter;
import Gamestate.GamestateManager;
import Gamestate.Playstate;
import Main.GUI;
import controller.PlaystateController;
import model.User;
/*
 * To perform this JUnit test you have to make the following changes to existing code:
 * 		In class PlaystateController:
 * 			getHorizontalWord(), getVerticalWord() and getWordValue() must be made public (from private)
 * 		In class GamestateManager: add this method
 * 			public Gamestate getGamestate(int gamestateNumber)
 *			{
 *				return gamestates.get(gamestateNumber);
 *			}
 *		In class Playstate: add this method
 *			public PlaystateController getPlaystateController()
 *			{
 *				return playstateController;
 *			}
 *		In class GUI: comment out the game loop
 */
public class PlaystateControllerGetWordValueTest
{
	PlaystateController playstateController;
	
	@Before
	public void setUp() throws Exception
	{
		GUI gui = new GUI();
		GamestateManager gsm = new GamestateManager(gui);
		User user = new User("marijntje42", gsm.getDatabaseController());
		gsm.setUser(user);
		gsm.setGamestate(GamestateManager.playState);
		// added getGameState method in GamestateManager
		Playstate playstate = (Playstate) gsm.getGamestate(GamestateManager.playState);
		// added getPlaystateController method in Playstate
		playstateController = playstate.getPlaystateController();
	}

	@Test
	public void testCaseOne() throws Exception
	{
		// x, y, width and height are graphic aspects and not necessary for this test.
		Letter dLetter = new Letter(0.0, 0.0, new Integer(0), new Integer(0), "D", 2);
		dLetter.setBordX(6);
		dLetter.setBordY(6);
		ArrayList<Letter> wordArrayList = new ArrayList<>();
		wordArrayList.add(dLetter);
		
		ArrayList<Letter> foundWordArraylist = playstateController.getHorizontalWord(dLetter, wordArrayList);
		int wordValue = playstateController.getWordValue(foundWordArraylist, wordArrayList);
		assertEquals(9, wordValue);
	}
	
	@Test
	public void testCaseTwo() throws Exception
	{
		Letter aLetter = new Letter(0.0, 0.0, new Integer(0), new Integer(0), "A", 1);
		aLetter.setBordX(1);
		aLetter.setBordY(8);
		Letter bLetter = new Letter(0.0, 0.0, new Integer(0), new Integer(0), "B", 4);
		bLetter.setBordX(2);
		bLetter.setBordY(8);
		Letter cLetter = new Letter(0.0, 0.0, new Integer(0), new Integer(0), "C", 5);
		cLetter.setBordX(3);
		cLetter.setBordY(8);
		Letter dLetter = new Letter(0.0, 0.0, new Integer(0), new Integer(0), "D", 2);
		dLetter.setBordX(4);
		dLetter.setBordY(8);
		Letter eLetter = new Letter(0.0, 0.0, new Integer(0), new Integer(0), "E", 1);
		eLetter.setBordX(5);
		eLetter.setBordY(8);
		Letter fLetter = new Letter(0.0, 0.0, new Integer(0), new Integer(0), "F", 4);
		fLetter.setBordX(6);
		fLetter.setBordY(8);
		
		ArrayList<Letter> wordArrayList = new ArrayList<>();
		wordArrayList.add(aLetter);
		wordArrayList.add(bLetter);
		wordArrayList.add(cLetter);
		wordArrayList.add(dLetter);
		wordArrayList.add(eLetter);
		wordArrayList.add(fLetter);
		
		ArrayList<Letter> foundWordArraylist = playstateController.getHorizontalWord(aLetter, wordArrayList);
		int wordValue = playstateController.getWordValue(foundWordArraylist, wordArrayList);
		assertEquals(48, wordValue);
	}
	
	@Test
	public void testCaseThree() throws Exception
	{
		Letter aLetter = new Letter(0.0, 0.0, new Integer(0), new Integer(0), "A", 1);
		aLetter.setBordX(15);
		aLetter.setBordY(9);
		Letter bLetter = new Letter(0.0, 0.0, new Integer(0), new Integer(0), "B", 4);
		bLetter.setBordX(15);
		bLetter.setBordY(10);
		Letter cLetter = new Letter(0.0, 0.0, new Integer(0), new Integer(0), "C", 5);
		cLetter.setBordX(15);
		cLetter.setBordY(11);
		
		ArrayList<Letter> wordArrayList = new ArrayList<>();
		wordArrayList.add(aLetter);
		wordArrayList.add(bLetter);
		wordArrayList.add(cLetter);
		
		ArrayList<Letter> foundFirstWordArraylist = playstateController.getVerticalWord(aLetter, wordArrayList);
		int wordValueFirstWord = playstateController.getWordValue(foundFirstWordArraylist, wordArrayList);
		assertEquals(42, wordValueFirstWord);
		
		ArrayList<Letter> foundSecondWordArraylist = playstateController.getHorizontalWord(aLetter, wordArrayList);
		int wordValueSecondWord = playstateController.getWordValue(foundSecondWordArraylist, wordArrayList);
		assertEquals(4, wordValueSecondWord);
		
		int totalWordValue = wordValueFirstWord + wordValueSecondWord;
		assertEquals(46, totalWordValue);
	}
}
