package controller;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import GameObjects.Letter;
import GameObjects.LetterBox;
import GameObjects.PlayField;
import Gamestate.GamestateManager;

public class PlaystateController
{
	private GamestateManager gsm;
	private DatabaseController databaseController;
	private PlayField playField;
	private LetterBox letterBox;
	
	public PlaystateController(GamestateManager gsm, PlayField playField, LetterBox letterBox){
		this.gsm = gsm;
		this.playField = playField;
		this.letterBox = letterBox;
		databaseController = gsm.getDatabaseController();
	}
	
	private ArrayList<Letter> getPlacedLetters()
	{
		ArrayList<Letter> allPlacedLetters = new ArrayList<>();
		for (Letter letter : letterBox.getLetters()) {

			if (letter.isOnPlayField()) 
			{
				//testMessage
				System.out.println("Letter " + letter.getLetterChar() + " is placed on coordinates x: " + letter.getCorrectedXInt() + ", y: " + letter.getCorrectedYInt());
				//add to the array
				allPlacedLetters.add(letter);
			}
		}
		return allPlacedLetters;
	}
	
	private boolean isLetterAttached(Letter letter) {
		// METHOD: boolean isLetterAttached(Letter letter)
		
		// get the necessary objects and values
		int letterX = letter.getCorrectedXInt();
		int letterY = letter.getCorrectedYInt();
		ArrayList<Letter> allPlayedLetters = playField.getPlayedLetters();
		//	loop through all played letters
		for (Letter playedLetter : allPlayedLetters)
		{
			// check if there's a playedLetter at any of these coordinates of letter 
			//(x+=1; y= y) 
			if (letterX == playedLetter.getCorrectedXInt()+1 && letterY == playedLetter.getCorrectedYInt())
			{
				return true;
			}
			//(x-=1;y=y)  
			if (letterX == playedLetter.getCorrectedXInt()-1 && letterY == playedLetter.getCorrectedYInt())
			{
				return true;
			}
			//(x=x;y+=1)
			if (letterX == playedLetter.getCorrectedXInt() && letterY == playedLetter.getCorrectedYInt()+1)
			{
				return true;
			}
			//(x=x;y-=1)
			if (letterX == playedLetter.getCorrectedXInt() && letterY == playedLetter.getCorrectedYInt()-1)
			{
				return true;
			}
		}
		//IF THIS IS NOT TRUE CHECK IF THE LETTER IS ON THE STARTSTAR
		// method requires ArrayList<Letter> as input so put the letter in an arrayList
		ArrayList<Letter> letterArrayList = new ArrayList<>();
		// if it's not on the startstar isOnStartStar() will return false and so will this method.
		return isOnStartStar(letterArrayList);
	}
	
	private boolean isOnStartStar(ArrayList<Letter> letterArrayList) 
	{
		//TODO
		//loop through all letters in arrayList
		for (Letter letter : letterArrayList)
		{
			// check if the letter is on the startstar
			// if yes
				//return true;
		}
		return false;
	}
	
	private int getWordOrientation(ArrayList<Letter> wordArrayList)
	{
		// Determine the word orientation
		// 0 = Horizontal (all y values the same)
		// 1 = Vertical (all x values the same)
		// -1 = Invalid (invalid placement(neither horizontal or vertical))
		boolean horizontal = true;
		boolean vertical = true;
		// get the first letter
		
		// compare every letter with all others in double loop
		loop: {
			for (int i = 0; i < wordArrayList.size(); i++)
			{
				for (int j = i+1; j < wordArrayList.size(); j++) 
				{
					// compare wordArrayList.get(i) and wordArrayList.get(j)
					// if x aren't the same the word is not vertical
					if (wordArrayList.get(i).getCorrectedXInt() != wordArrayList.get(j).getCorrectedXInt()) 
					{
						vertical = false;
					}
					// if y aren't the same the word is not horizontal
					if (wordArrayList.get(i).getCorrectedYInt() != wordArrayList.get(j).getCorrectedYInt())
					{
						horizontal = false;
					}
					
					// if vertical and horizontal are both false already, abort the loop
					// TODO is this faster than not breaking the loop?
					if (horizontal == false && vertical == false)
					{
						break loop;
					}
				}
			}
		}
		
		// determine orientation; vertical, horizontal or invalid
		// if horizontal and vertical are both false or both true the word is invalid
		if (horizontal == true && vertical == false) 
		{
			return 0;
		} else if(vertical == true && horizontal == false) 
		{
			return 1;
		} else // it is invalid
		{
			return -1;
		}
	}
	
	private boolean isWordAttached(ArrayList<Letter> wordArrayList) {
		boolean attached = false;
		// for all letters check if they are attached to an already existing letter (using method isLetterAttached())
		for (Letter letter : wordArrayList)
		{
			if (isLetterAttached(letter)) {
				attached = true;
			}
		}
		return attached;
	}
	private Letter getLowestXLetter(ArrayList<Letter> wordArrayList) {
		Letter lowestXLetter = null;
		for (Letter letter : wordArrayList)
		{
			
			if (lowestXLetter == null)
			{
				lowestXLetter = letter;
			}
			// if the new letter has a lower x make that letter the new lowestXLetter
			if (letter.getCorrectedXInt() < lowestXLetter.getCorrectedXInt()) {
				lowestXLetter = letter;
			}
		}
		return lowestXLetter;
	}
	
	private Letter getHighestXLetter(ArrayList<Letter> wordArrayList) {
		Letter highestXLetter = null;
		for (Letter letter : wordArrayList)
		{
			if (highestXLetter == null)
			{
				highestXLetter = letter;
			}
			// if the new letter had a higher x make that letter the new highestXLetter
			if (letter.getCorrectedXInt() > highestXLetter.getCorrectedXInt()) {
				highestXLetter = letter;
			}
		}
		return highestXLetter;
	}
	
	private Letter getFirstHorizontalWordLetter(Letter firstLetterInWord) {
		
		Letter currentLetter = null;
		Letter newLowestLetter = firstLetterInWord;
		boolean firstLetterFound = false;
		
		while(!firstLetterFound)
		{
			currentLetter = newLowestLetter;
			for (Letter letter : playField.getPlayedLetters())
			{
				// if there's a letter at x-=1 take that letter and break out of this loop
				if ((letter.getCorrectedXInt() == currentLetter.getCorrectedXInt()-1) && letter.getCorrectedYInt() == currentLetter.getCorrectedYInt())
				{
					
					newLowestLetter = letter;
					break;
				}
				
			}
			if (currentLetter.equals(newLowestLetter))
			{
				firstLetterFound = true;
			}
		}
		return newLowestLetter;
		
	}
	private boolean isHorizontalWordPlacedWithoutGaps(ArrayList<Letter> wordArrayList) {
		// first check if the letters are all attached (in a line to either each other or a letter in the playfield
		// find the played letter with the lowest x and the played letter with the highest x
		Letter lowestXLetter = getLowestXLetter(wordArrayList);
		Letter highestXLetter = getHighestXLetter(wordArrayList);
		
		// get the amount of letters after the first letter (= highestx - lowestx)
		int amountOfLetters = highestXLetter.getCorrectedXInt() - lowestXLetter.getCorrectedXInt();
		
		// grab the first letter
		Letter currentLetter = null;
		Letter nextLetter = lowestXLetter;
		for (int i = 0; i < amountOfLetters; i++)
		{
			currentLetter = nextLetter;
			
			//check if the next letter is in the wordArrayList
			for (Letter letter : wordArrayList)
			{
				// if it contains the next letter
				if ((letter.getCorrectedXInt() == currentLetter.getCorrectedXInt()+1) && letter.getCorrectedYInt() == currentLetter.getCorrectedYInt())
				{
					nextLetter = letter;
					break;
				}
			}
			// if it's not check if the next letter is already on the playfield
			if (currentLetter.equals(nextLetter))
			{
				
				for (Letter letter : playField.getPlayedLetters())
				{
					if ((letter.getCorrectedXInt() == currentLetter.getCorrectedXInt()+1) && letter.getCorrectedYInt() == currentLetter.getCorrectedYInt())
					{
						nextLetter = letter;
						break;
					}
				}
			}
			
			// if the next letter is still the same as the current letter it means
			// there isn't a letter at x+1 which means the letters have been placed incorrectly
			if (currentLetter.equals(nextLetter)) 
			{
				return false;
				
			}
		}
		return true;
	}
	
	private String getHorizontalWord(Letter firstLetter, ArrayList<Letter> wordArrayList )
	{
		
		
		// make a string variable and append the value of the first letter
		// keep checking if there's a letter at x+1, if yes append it to the word
		String horizontalWord = "" + firstLetter.getLetterChar();
		
		// grab the first letter
				Letter currentLetter = null;
				Letter nextLetter = firstLetter;
				boolean firstLetterFound = false;
				
				while(!firstLetterFound)
				{
					currentLetter = nextLetter;
					
					//check if the next letter is in the wordArrayList
					for (Letter letter : wordArrayList)
					{
						// if it contains the next letter
						if ((letter.getCorrectedXInt() == currentLetter.getCorrectedXInt()+1) && letter.getCorrectedYInt() == currentLetter.getCorrectedYInt())
						{
							
							nextLetter = letter;
							horizontalWord += nextLetter.getLetterChar();
							break;
						}
					}
					// if it's not check if the next letter is already on the playfield
					if (currentLetter.equals(nextLetter))
					{
						
						for (Letter letter : playField.getPlayedLetters())
						{
							if ((letter.getCorrectedXInt() == currentLetter.getCorrectedXInt()+1) && letter.getCorrectedYInt() == currentLetter.getCorrectedYInt())
							{
								nextLetter = letter;
								horizontalWord += nextLetter.getLetterChar();
								break;
							}
						}
					}
					
					// if the next letter is still the same as the current letter it means
					// there isn't a letter at x+1 which means the letters have been placed incorrectly
					if (currentLetter.equals(nextLetter))
					{
						firstLetterFound = true;
					}
				}
		
		return horizontalWord;
	}
	
	public void doPlay() {
		// INSTEAD OF THIS IF THE WORD IS NOT ATTACHED TO ANY LETTERS, CHECK IF ONE OF THE LETTERS IS ON THE STARTSTAR
		
		/*//check if it's the first turn (query the database)
		//TODO gsm.getUser().getGameNumber() vervangen met verbeterde code???
		ResultSet turnResultSet = db_c.query("select max(id) from beurt where spel_id = '" + gsm.getUser().getGameNumber() +"'");
		int turn = 0;
		try {
			turn = turnResultSet.getInt(0);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(true turn == 1) {
			// check if one of the letters is on the startstar/startpoint
			
				// check if the word is in the dictionary(with ~game.getletterset) (query database)
					// place the letters and insert them into the database (maybe make a private method for this that also
					// gives the user points etc
				
			
		} else {
			*/
		
		
		
		
		ArrayList<Letter> wordArrayList = getPlacedLetters();
			// get number of letters placed down (woordArrayList.size())
		int wordSize = wordArrayList.size();
				// if number == 0 give a popup message that there's no letters placed down
			if (wordSize == 0) {
				JOptionPane.showMessageDialog(null, "Er zijn geen letters geplaatst.");
			}
				// if number == 1 
			else if (wordSize == 1) {
				// if isLetterAttached returns true
				if (isLetterAttached(wordArrayList.get(0))) {
					System.out.println("Letter is attached to a letter.");
					//TODO
					// String getHorizontalWord(Letter letterInWord)
						// checkIfWordInDictionary() for that word
					// String getVerticalWord(Letter letterInWord)
						// checkIfWodInDictionary() for that word
				}
				
			}
			// if number > 1	
			else if (wordSize > 1)	
			{	
				// determine word orientation
				int wordOrientation = getWordOrientation(wordArrayList);
				// if horizontal
				if (wordOrientation == 0) 
				{
					System.out.println("Word orientation is horizontal");
					// Check if there's no gaps in the placement (no empty tiles between letters)
					if (isHorizontalWordPlacedWithoutGaps(wordArrayList)) 
					{
						System.out.println("Horizontal word is placed without gaps");
						if (isWordAttached(wordArrayList))
						{
							System.out.println("Word is attached to existing letter.");
							
							// get the first letter of the horizontal word
							Letter firstLetterInWordArrayList = getLowestXLetter(wordArrayList);
							Letter firstLetterOnGameBoard = getFirstHorizontalWordLetter(firstLetterInWordArrayList);
							String horizontalWord = getHorizontalWord(firstLetterOnGameBoard, wordArrayList);
							System.out.println(firstLetterOnGameBoard.getLetterChar() + " is the first letter of the horizontal word");
							System.out.println(horizontalWord + " Is the horizontal word");
						}
					}
				}
				
				
					
					// if x!= 1
						// METHOD: String getHorizontalWord(Letter letterInWord)
						// find the first letter (in a loop)
						// check if there's a letter at x-1 and y= the same
						// if true
							// take that letter , keep doing this until you find the first letter
								// then take make a string variable and append the value of the first letter
								// keep checking if there's a letter at x+1, if yes append it to the word
						// METHOD: checkIfWordInDictionary(String word, String game.getLetterSet())
							// check if the word is in the dictionary
							// if it is add the points to the player in the game
							// else add it to the wrong words string
				// Loop through every placed letter and perform the method getVerticalWord(Letter letterinWord)
					// checkIfWordInDictionary() for all the words
				
				// if vertical
				else if (wordOrientation == 1) 
				{
					System.out.println("Word orientation is vertical");
				}
			
				// METHOD: boolean checkIfVerticalWordIsAttached(ArrayList<Letter> word)
				// for all letters check if there's a letter at x+=1 and x-=1 where y=y
					// if so return true
				// for the first letter check if there's a letter at y-=1 where x=x;
					// if so return true
				// for the last letter check if there's a letter at y+=1 where x=x;
					// if so return true
				// for all the letters check if the letter is on the startstar
					// if so return true
				// get the letter with the lowest y value
					// if y!= 1
						// METHOD: String getVerticalWord(Letter letterInWord)
						// find the first letter (in a loop)
						// check if there's a letter at y-1 and x= the same
						// if true
							// take that letter , keep doing this until you find the first letter
								// then take make a string variable and append the value of the first letter
								// keep checking if there's a letter at x+1, if yes append it to the word
						// checkIfWordInDictionary() for the word that the method returns
				// Loop through every placed letter and perform the method getHorizontalWord(Letter letterinWord)
					// checkIfWordInDictionary() for all the words
				
				// else if word is invalid
				else if (wordOrientation == -1){
					// give a popup message that the letters aren't placed correctly
					System.out.println("Word orientation is invalid");
					JOptionPane.showMessageDialog(null, "Letters zijn niet volledig horizontaal of verticaal geplaatst.");
				}
			
				
			}
				
					
				
						
							
		//}
	}
}
// Test marc pull request new private repo