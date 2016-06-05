package controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.w3c.dom.css.Counter;

import GameObjects.Letter;
import GameObjects.LetterBox;
import GameObjects.PlayField;
import GameObjects.Tile;
import Gamestate.GamestateManager;
import Gamestate.Playstate;

public class PlaystateController {
	private GamestateManager gsm;
	private DatabaseController databaseController;
	private PlayField playField;
	private LetterBox letterBox;
	private Playstate playstate;

	// Variables for Mathijs' scoretracking
	private int score;
	private ArrayList<Letter> mainWord;
	// either "vertical" or "horizontal"
	private String mainWordOrientation;

	public PlaystateController(GamestateManager gsm, PlayField playField, LetterBox letterBox, Playstate playstate) {
		this.gsm = gsm;
		this.playField = playField;
		this.letterBox = letterBox;
		this.playstate = playstate;
		databaseController = gsm.getDatabaseController();
	}

	private ArrayList<Letter> getPlacedLetters() {
		ArrayList<Letter> allPlacedLetters = new ArrayList<>();
		for (Letter letter : letterBox.getLetters()) {

			if (letter.isOnPlayField()) {
				// testMessage
				System.out.println("Letter " + letter.getLetterChar() + " is placed on coordinates x: "
						+ letter.getBordX() + ", y: " + letter.getBordY());
				// add to the array
				allPlacedLetters.add(letter);
			}
		}
		return allPlacedLetters;
	}

	private boolean isLetterAttached(Letter letter) {
		// METHOD: boolean isLetterAttached(Letter letter)

		// get the necessary objects and values
		int letterX = letter.getBordX();
		int letterY = letter.getBordY();
		ArrayList<Letter> allPlayedLetters = playField.getPlayedLetters();
		// loop through all played letters
		for (Letter playedLetter : allPlayedLetters) {
			// check if there's a playedLetter at any of these coordinates of
			// letter
			// (x+=1; y= y)
			if (letterX == playedLetter.getBordX() + 1 && letterY == playedLetter.getBordY()) {
				return true;
			}
			// (x-=1;y=y)
			if (letterX == playedLetter.getBordX() - 1 && letterY == playedLetter.getBordY()) {
				return true;
			}
			// (x=x;y+=1)
			if (letterX == playedLetter.getBordX() && letterY == playedLetter.getBordY() + 1) {
				return true;
			}
			// (x=x;y-=1)
			if (letterX == playedLetter.getBordX() && letterY == playedLetter.getBordY() - 1) {
				return true;
			}
		}
		// IF THIS IS NOT TRUE CHECK IF THE LETTER IS ON THE STARTSTAR
		// method requires ArrayList<Letter> as input so put the letter in an
		// arrayList
		ArrayList<Letter> letterArrayList = new ArrayList<>();
		letterArrayList.add(letter);
		// if it's not on the startstar isOnStartStar() will return false and so
		// will this method.
		return isOnStartStar(letterArrayList);
	}

	private boolean isOnStartStar(ArrayList<Letter> letterArrayList) {
		// get the coordinates of the startstar
		String getStartStarQuery = "SELECT x, y FROM tegel LEFT JOIN bord ON tegel.bord_naam = bord.naam LEFT JOIN spel ON bord.naam = spel.bord_naam WHERE spel.id = "
				+ gsm.getUser().getGameNumber() + " AND tegel.tegeltype_soort = '*'";
		int x = -1;
		int y = -1;
		try {
			ResultSet rSet = databaseController.query(getStartStarQuery);
			if (rSet.next()) {
				x = rSet.getInt("x");
				y = rSet.getInt("y");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (x != -1 && y != -1) {
			// loop through all letters in arrayList
			for (Letter letter : letterArrayList) {
				// check if the letter is on the startstar
				if (letter.getBordX() == x && letter.getBordY() == y) {
					// if it is
					return true;
				}
			}
		} else {
			System.err.println("Something went wrong with the isOnStartStar method");
		}

		return false;
	}

	private int getWordOrientation(ArrayList<Letter> wordArrayList) {
		// Determine the word orientation
		// 0 = Horizontal (all y values the same)
		// 1 = Vertical (all x values the same)
		// -1 = Invalid (invalid placement(neither horizontal or vertical))
		boolean horizontal = true;
		boolean vertical = true;
		// get the first letter

		// compare every letter with all others in double loop
		loop: {
			for (int i = 0; i < wordArrayList.size(); i++) {
				for (int j = i + 1; j < wordArrayList.size(); j++) {
					// compare wordArrayList.get(i) and wordArrayList.get(j)
					// if x aren't the same the word is not vertical
					if (wordArrayList.get(i).getBordX() != wordArrayList.get(j).getBordX()) {
						vertical = false;
					}
					// if y aren't the same the word is not horizontal
					if (wordArrayList.get(i).getBordY() != wordArrayList.get(j).getBordY()) {
						horizontal = false;
					}

					// if vertical and horizontal are both false already, abort
					// the loop
					// TODO is this faster than not breaking the loop?
					if (horizontal == false && vertical == false) {
						break loop;
					}
				}
			}
		}

		// determine orientation; vertical, horizontal or invalid
		// if horizontal and vertical are both false or both true the word is
		// invalid
		if (horizontal == true && vertical == false) {
			return 0;
		} else if (vertical == true && horizontal == false) {
			return 1;
		} else // it is invalid
		{
			return -1;
		}
	}

	private boolean isWordAttached(ArrayList<Letter> wordArrayList) {
		boolean attached = false;
		// for all letters check if they are attached to an already existing
		// letter (using method isLetterAttached())
		for (Letter letter : wordArrayList) {
			if (isLetterAttached(letter)) {
				attached = true;
			}
		}
		return attached;
	}

	private Letter getLowestXLetter(ArrayList<Letter> wordArrayList) {
		Letter lowestXLetter = null;
		for (Letter letter : wordArrayList) {

			if (lowestXLetter == null) {
				lowestXLetter = letter;
			}
			// if the new letter has a lower x make that letter the new
			// lowestXLetter
			if (letter.getBordX() < lowestXLetter.getBordX()) {
				lowestXLetter = letter;
			}
		}
		return lowestXLetter;
	}

	private Letter getHighestXLetter(ArrayList<Letter> wordArrayList) {
		Letter highestXLetter = null;
		for (Letter letter : wordArrayList) {
			if (highestXLetter == null) {
				highestXLetter = letter;
			}
			// if the new letter had a higher x make that letter the new
			// highestXLetter
			if (letter.getBordX() > highestXLetter.getBordX()) {
				highestXLetter = letter;
			}
		}
		return highestXLetter;
	}

	private Letter getLowestYLetter(ArrayList<Letter> wordArrayList) {
		Letter lowestYLetter = null;
		for (Letter letter : wordArrayList) {

			if (lowestYLetter == null) {
				lowestYLetter = letter;
			}
			// if the new letter has a lower x make that letter the new
			// lowestXLetter
			if (letter.getBordY() < lowestYLetter.getBordY()) {
				lowestYLetter = letter;
			}
		}
		return lowestYLetter;
	}

	private Letter getHighestYLetter(ArrayList<Letter> wordArrayList) {
		Letter highestYLetter = null;
		for (Letter letter : wordArrayList) {
			if (highestYLetter == null) {
				highestYLetter = letter;
			}
			// if the new letter had a higher x make that letter the new
			// highestXLetter
			if (letter.getBordY() > highestYLetter.getBordY()) {
				highestYLetter = letter;
			}
		}
		return highestYLetter;
	}

	private Letter getFirstHorizontalWordLetter(Letter firstLetterInWord) {

		Letter currentLetter = null;
		Letter newLowestLetter = firstLetterInWord;
		boolean firstLetterFound = false;

		while (!firstLetterFound) {
			currentLetter = newLowestLetter;
			for (Letter letter : playField.getPlayedLetters()) {
				// if there's a letter at x-=1 take that letter and break out of
				// this loop
				if ((letter.getBordX() == currentLetter.getBordX() - 1)
						&& letter.getBordY() == currentLetter.getBordY()) {

					newLowestLetter = letter;
					break;
				}

			}
			if (currentLetter.equals(newLowestLetter)) {
				firstLetterFound = true;
			}
		}
		return newLowestLetter;

	}

	private boolean isHorizontalWordPlacedWithoutGaps(ArrayList<Letter> wordArrayList) {
		// first check if the letters are all attached (in a line to either each
		// other or a letter in the playfield
		// find the played letter with the lowest x and the played letter with
		// the highest x
		Letter lowestXLetter = getLowestXLetter(wordArrayList);
		Letter highestXLetter = getHighestXLetter(wordArrayList);

		// get the amount of letters after the first letter (= highestx -
		// lowestx)
		int amountOfLetters = highestXLetter.getBordX() - lowestXLetter.getBordX();

		// grab the first letter
		Letter currentLetter = null;
		Letter nextLetter = lowestXLetter;
		for (int i = 0; i < amountOfLetters; i++) {
			currentLetter = nextLetter;

			// check if the next letter is in the wordArrayList
			for (Letter letter : wordArrayList) {
				// if it contains the next letter
				if ((letter.getBordX() == currentLetter.getBordX() + 1)
						&& letter.getBordY() == currentLetter.getBordY()) {
					nextLetter = letter;
					break;
				}
			}
			// if it's not check if the next letter is already on the playfield
			if (currentLetter.equals(nextLetter)) {

				for (Letter letter : playField.getPlayedLetters()) {
					if ((letter.getBordX() == currentLetter.getBordX() + 1)
							&& letter.getBordY() == currentLetter.getBordY()) {
						nextLetter = letter;
						break;
					}
				}
			}

			// if the next letter is still the same as the current letter it
			// means
			// there isn't a letter at x+1 which means the letters have been
			// placed incorrectly
			if (currentLetter.equals(nextLetter)) {
				return false;

			}
		}
		return true;
	}

	private boolean isVerticalWordPlacedWithoutGaps(ArrayList<Letter> wordArrayList) {
		// first check if the letters are all attached (in a line to either each
		// other or a letter in the playfield
		// find the played letter with the lowest Y and the played letter with
		// the highest Y
		Letter lowestYLetter = getLowestYLetter(wordArrayList);
		Letter highestYLetter = getHighestYLetter(wordArrayList);

		// get the amount of letters after the first letter (= highestx -
		// lowestx)
		int amountOfLetters = highestYLetter.getBordY() - lowestYLetter.getBordY();

		// grab the first letter
		Letter currentLetter = null;
		Letter nextLetter = lowestYLetter;
		for (int i = 0; i < amountOfLetters; i++) {
			currentLetter = nextLetter;

			// check if the next letter is in the wordArrayList
			for (Letter letter : wordArrayList) {
				// if it contains the next letter
				if ((letter.getBordY() == currentLetter.getBordY() + 1)
						&& letter.getBordX() == currentLetter.getBordX()) {
					nextLetter = letter;
					break;
				}
			}
			// if it's not check if the next letter is already on the playfield
			if (currentLetter.equals(nextLetter)) {

				for (Letter letter : playField.getPlayedLetters()) {
					if ((letter.getBordY() == currentLetter.getBordY() + 1)
							&& letter.getBordX() == currentLetter.getBordX()) {
						nextLetter = letter;
						break;
					}
				}
			}

			// if the next letter is still the same as the current letter it
			// means
			// there isn't a letter at y+1 which means the letters have been
			// placed incorrectly
			if (currentLetter.equals(nextLetter)) {
				return false;

			}
		}
		return true;
	}

	private ArrayList<Letter> getHorizontalWord(Letter anyLetter, ArrayList<Letter> wordArrayList) {
		// make an arrayList
		ArrayList<Letter> horizontalWord = new ArrayList<>();
		// find the first letter in the word and add it to the arrayList
		Letter firstLetter = getFirstHorizontalWordLetter(anyLetter);
		horizontalWord.add(firstLetter);

		// keep checking if there's a letter at x+1, if yes add it to the
		// arrayList

		Letter currentLetter = null;
		Letter nextLetter = firstLetter;
		boolean lastLetterFound = false;

		while (!lastLetterFound) {
			currentLetter = nextLetter;

			// check if the next letter is in the wordArrayList
			for (Letter letter : wordArrayList) {
				// if it contains the next letter
				if ((letter.getBordX() == currentLetter.getBordX() + 1)
						&& letter.getBordY() == currentLetter.getBordY()) {

					nextLetter = letter;
					horizontalWord.add(nextLetter);
					break;
				}
			}
			// if it's not check if the next letter is already on the playfield
			if (currentLetter.equals(nextLetter)) {

				for (Letter letter : playField.getPlayedLetters()) {
					if ((letter.getBordX() == currentLetter.getBordX() + 1)
							&& letter.getBordY() == currentLetter.getBordY()) {
						nextLetter = letter;
						horizontalWord.add(nextLetter);
						break;
					}
				}
			}

			// if the next letter is still the same as the current letter it
			// means
			// there isn't a letter at x+1 which means the letters have been
			// placed incorrectly
			if (currentLetter.equals(nextLetter)) {
				lastLetterFound = true;
			}
		}

		return horizontalWord;
	}

	private Letter getFirstVerticalWordLetter(Letter firstLetterInWord) {

		Letter currentLetter = null;
		Letter newLowestLetter = firstLetterInWord;
		boolean firstLetterFound = false;

		while (!firstLetterFound) {
			currentLetter = newLowestLetter;
			for (Letter letter : playField.getPlayedLetters()) {
				// if there's a letter at y-=1 take that letter and break out of
				// this loop
				if ((letter.getBordY() == currentLetter.getBordY() - 1)
						&& letter.getBordX() == currentLetter.getBordX()) {

					newLowestLetter = letter;
					break;
				}

			}
			if (currentLetter.equals(newLowestLetter)) {
				firstLetterFound = true;
			}
		}
		return newLowestLetter;

	}

	private ArrayList<Letter> getVerticalWord(Letter anyLetter, ArrayList<Letter> wordArrayList) {
		// make an arrayList
		ArrayList<Letter> verticalWord = new ArrayList<>();
		// find the first letter in the word and add it to the arrayList
		Letter firstLetter = getFirstVerticalWordLetter(anyLetter);
		verticalWord.add(firstLetter);

		// keep checking if there's a letter at y+1, if yes add it to the
		// arrayList

		Letter currentLetter = null;
		Letter nextLetter = firstLetter;
		boolean lastLetterFound = false;

		while (!lastLetterFound) {
			currentLetter = nextLetter;

			// check if the next letter is in the wordArrayList
			for (Letter letter : wordArrayList) {
				// if it contains the next letter
				if ((letter.getBordY() == currentLetter.getBordY() + 1)
						&& letter.getBordX() == currentLetter.getBordX()) {

					nextLetter = letter;
					verticalWord.add(nextLetter);
					break;
				}
			}
			// if it's not check if the next letter is already on the playfield
			if (currentLetter.equals(nextLetter)) {

				for (Letter letter : playField.getPlayedLetters()) {
					if ((letter.getBordY() == currentLetter.getBordY() + 1)
							&& letter.getBordX() == currentLetter.getBordX()) {
						nextLetter = letter;
						verticalWord.add(nextLetter);
						break;
					}
				}
			}

			// if the next letter is still the same as the current letter it
			// means
			// there isn't a letter at x+1 which means the letters have been
			// placed incorrectly
			if (currentLetter.equals(nextLetter)) {
				lastLetterFound = true;
			}
		}

		return verticalWord;
	}

	private String getConvertedWordArrayListToString(ArrayList<Letter> foundWordArraylist) {
		String foundWordString = "";
		for (Letter letter : foundWordArraylist) {
			foundWordString += letter.getLetterChar();
		}
		return foundWordString;
	}

	private boolean isInDictionary(ArrayList<Letter> foundWordArraylist) {
		String foundWord = getConvertedWordArrayListToString(foundWordArraylist);

		// get the game's letterset_code
		String letterSetCode = new String();
		String getLetterSetCodeQuery = "SELECT letterset_naam FROM spel WHERE id =" + gsm.getUser().getGameNumber();
		try {
			ResultSet rSet = databaseController.query(getLetterSetCodeQuery);
			if (rSet.next()) {
				letterSetCode = rSet.getString("letterset_naam");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		ResultSet rs = databaseController.query("select woord from woordenboek where woord = '" + foundWord
				+ "' and letterset_code = '" + letterSetCode + "' and status = 'accepted'");
		try {
			if (rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	// return the value of the word in points (int)
	private int getWordValue(ArrayList<Letter> foundWordArraylist, ArrayList<Letter> wordArraylist) {
		int wordMultiplier = 1;
		int score = 0;

		// Letters already on the board only give their letter value and their
		// tile score multiplier doesn't apply.
		// get these letters and add their score to the score
		for (Letter letter : foundWordArraylist) {
			// loop through all letters on the playfield
			for (Letter playfieldLetter : playField.getPlayedLetters()) {
				// if letter x and y match that of a letter already on the
				// playfield
				if (letter.getBordX() == playfieldLetter.getBordX()
						&& letter.getBordY() == playfieldLetter.getBordY()) {
					// take their score and add to the totalscore
					score += letter.getScore();
					break;
				}

			}

		}
		// Letters from the hand do apply score multipliers.

		for (Letter letter : wordArraylist) {
			// check if letter is in foundWordArraylist too
			for (Letter letter2 : foundWordArraylist) {
				if (letter.equals(letter2)) {
					for (Tile tile : playField.getTiles()) {
						// if the tile and letter have the same x and y
						if (letter.getBordX() == tile.getBordX() && letter.getBordY() == tile.getBordY()) {
							int letterMultiplier = 1;
							// get the score from the tile the letter is placed
							// on and apply the multiplier
							String tileScore = tile.getScore();
							switch (tileScore) {
							case "--":
								break;
							case "DL":
								letterMultiplier *= 2;
								break;
							case "TL":
								letterMultiplier *= 3;
								break;
							case "DW":
								wordMultiplier *= 2;
								break;
							case "TW":
								wordMultiplier *= 3;
								break;
							default:
								break;
							}
							// add the letter score
							score += letter.getScore() * letterMultiplier;
						}
					}
				}
			}
		}

		int totalScore = score * wordMultiplier;

		return totalScore;
	}

	private void updateDatabase(int points, ArrayList<Letter> wordArrayList) {
		// TODO get turnNumber from player?
		// get number of last turn
		int lastTurnNumber = -1;
		try {

			ResultSet rSet = databaseController
					.query("SELECT MAX(id) FROM beurt WHERE spel_id =" + gsm.getUser().getGameNumber());
			if (rSet.next()) {
				lastTurnNumber = rSet.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (lastTurnNumber != -1) {
			// insert the turn into the database
			// update beurt
			String beurtUpdateQuery = "INSERT INTO beurt (`id`, `spel_id`,`account_naam`,`score`,aktie_type) VALUES("
					+ (lastTurnNumber + 1) + "," + gsm.getUser().getGameNumber() + ", '" + gsm.getUser().getUsername()
					+ "'," + points + ", 'word')";
			databaseController.queryUpdate(beurtUpdateQuery);

			// update gelegde letter
			String tegelBordNaam = "";
			try {
				ResultSet rSet = databaseController
						.query("SELECT bord_naam FROM spel WHERE id =" + gsm.getUser().getGameNumber());
				if (rSet.next()) {
					tegelBordNaam = rSet.getString(1);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			for (Letter letter : wordArrayList) {

				String blancoLetterCharacter = "NULL";
				// if letter is joker/jester/wild card change
				// blancoLetterCharacter to the character it became.

				if (letter.getIsJoker()) {
					blancoLetterCharacter = "'" + letter.getLetterChar() + "'";
				}
				// mathijs v
				boolean isGood = false;
				int counter = 0;
				while (!isGood) {
					if (databaseController.pingedBack()) {

						String gelegdeLetterUpdateQuery = ("INSERT INTO gelegdeletter (tegel_bord_naam,spel_id,beurt_id,letter_id,tegel_x,tegel_y,blancoletterkarakter) VALUES ('"
								+ tegelBordNaam + "'," + gsm.getUser().getGameNumber() + "," + (lastTurnNumber + 1)
								+ "," + letter.getLetterID() + "," + letter.getBordX() + "," + letter.getBordY() + ","
								+ blancoLetterCharacter + ")");
						databaseController.queryUpdate(gelegdeLetterUpdateQuery);
						ResultSet rs = databaseController.query("SELECT * FROM gelegdeletter WHERE spel_id = "
								+ gsm.getUser().getGameNumber() + " AND beurt_id = " + (lastTurnNumber + 1));
						ArrayList<Integer> identification = new ArrayList<Integer>();
						try {
							while (rs.next()) {
								identification.add(rs.getInt("letter_id"));
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
						if (identification.contains(letter.getLetterID())) {
							System.out.println("char: "+letter.getLetterID()+" is added to the database.");
							isGood = true;
						}
					} else if (counter == 50) {
						JOptionPane.showMessageDialog(null, "Er ging iets fout bij het inserten van de letters in de database");
						gsm.setGamestate(GamestateManager.gameOverviewState);
						return;
					} else if (!databaseController.pingedBack()) {
						databaseController = new DatabaseController();
					}

				}

				// mathijs^
			}

			// update letterbakjeletter
			letterBox.replacePlacedLetters(wordArrayList);
			// TODO END if the player hand and deck are empty the action_type is
			// "end"
			// TODO not sure if letterbakjeletter will need to be updated if
			// action_type is "end"

			// TODO remove this part (already done in letterBox replaceplaced
			// letters
			// get the letters that are in the letterBox at the end of the turn,
			// loop through them
			// and insert them into the database
			// for (Letter letter : letterBox.getLetters()) {
			// String letterBakjeLetterUpdateQuery = "INSERT INTO
			// letterbakjeletter (`spel_id`,`beurt_id`,`Letter_id`) VALUES ("
			// + gsm.getUser().getGameNumber() + "," + (lastTurnNumber + 1) +
			// "," + letter.getLetterID() + ")";
			// databaseController.queryUpdate(letterBakjeLetterUpdateQuery);
			// }

		} else {
			System.err.println("Something's gone wrong with the lastTurnNumber in the PlaystateController");
		}
	}

	private void submitValidWord(int points, ArrayList<Letter> wordArrayList) {
		// show the score
		JOptionPane.showMessageDialog(null, "Je hebt met deze zet " + points + " punten behaald.");
		// update the database
		updateDatabase(points, wordArrayList);
		// reload the playstate
		playstate.reloadPlaystate();
	}

	private void wordIsInvalid(String wrongWordsString) {
		if (wrongWordsString.endsWith(", ")) {
			wrongWordsString = wrongWordsString.substring(0, wrongWordsString.length() - 2);
		}
		JOptionPane.showMessageDialog(null, wrongWordsString);
	}

	public void doPlay() {
		String wrongWordsString = "Dit woord kan niet geplaatst worden omdat de volgende woorden niet in het woordenboek staan: ";
		ArrayList<Letter> wordArrayList = getPlacedLetters();
		// get number of letters placed down (woordArrayList.size())
		int wordSize = wordArrayList.size();
		// if number == 0 give a popup message that there's no letters placed
		// down
		if (wordSize == 0) {
			JOptionPane.showMessageDialog(null, "Er zijn geen letters geplaatst.");
		}
		// if number == 1
		else if (wordSize == 1) {
			// if isLetterAttached returns true
			if (isLetterAttached(wordArrayList.get(0))) {
				System.out.println("Letter is attached to a letter.");
				int points = 0;
				boolean placementIsValid = true;
				// get the horizontal word
				ArrayList<Letter> horizontalWordArraylist = getHorizontalWord(wordArrayList.get(0), wordArrayList);
				// it can only be a word if it's bigger than one letter
				if (horizontalWordArraylist.size() > 1) {
					// check if the word is in the dictionary
					if (isInDictionary(horizontalWordArraylist)) {
						System.out.println("Word " + getConvertedWordArrayListToString(horizontalWordArraylist)
								+ " is in the dictionary!");
						// if it is add the value of the word to the total
						// score.
						int horizontalWordValue = getWordValue(horizontalWordArraylist, wordArrayList);
						points += horizontalWordValue;
						System.out.println("The word " + getConvertedWordArrayListToString(horizontalWordArraylist)
								+ " is worth " + horizontalWordValue + " points.");
					} else {
						// if it's not placement isn't valid
						placementIsValid = false;
						// add word to list of wrong words
						wrongWordsString += getConvertedWordArrayListToString(horizontalWordArraylist) + ", ";
						System.out.println(wrongWordsString);
					}
				}
				// get the vertical word
				ArrayList<Letter> verticalWordArraylist = getVerticalWord(wordArrayList.get(0), wordArrayList);
				// it can only be a word if it's bigger than one letter
				if (verticalWordArraylist.size() > 1) {
					// check if the word is in the dictionary
					if (isInDictionary(verticalWordArraylist)) {
						System.out.println("Word " + getConvertedWordArrayListToString(verticalWordArraylist)
								+ " is in the dictionary!");
						// if it is add the value of the word to the total
						// score.
						int verticalWordValue = getWordValue(verticalWordArraylist, wordArrayList);
						points += verticalWordValue;
						System.out.println("The word " + getConvertedWordArrayListToString(verticalWordArraylist)
								+ " is worth " + verticalWordValue + " points.");
					} else {
						// if it's not placement isn't valid
						placementIsValid = false;
						// add word to list of wrong words
						wrongWordsString += getConvertedWordArrayListToString(verticalWordArraylist) + ", ";
						System.out.println(wrongWordsString);
					}
				}

				if (placementIsValid) {
					submitValidWord(points, wordArrayList);
				} else {
					wordIsInvalid(wrongWordsString);
				}
			}
		}

		// if number > 1
		else if (wordSize > 1) {
			// determine word orientation
			int wordOrientation = getWordOrientation(wordArrayList);
			// if horizontal
			if (wordOrientation == 0) {
				System.out.println("Word orientation is horizontal");
				// Check if there's no gaps in the placement (no empty tiles
				// between letters)
				if (isHorizontalWordPlacedWithoutGaps(wordArrayList)) {
					System.out.println("Horizontal word is placed without gaps");
					if (isWordAttached(wordArrayList)) {
						System.out.println("Word is attached to existing letter.");
						boolean placementIsValid = true;
						int points = 0;
						// get the first letter of the horizontal word
						Letter firstLetterInWordArrayList = getLowestXLetter(wordArrayList);
						Letter firstLetterOnGameBoard = getFirstHorizontalWordLetter(firstLetterInWordArrayList);

						ArrayList<Letter> horizontalWordArraylist = getHorizontalWord(firstLetterOnGameBoard,
								wordArrayList);
						String horizontalWordString = getConvertedWordArrayListToString(horizontalWordArraylist);

						System.out.println(
								firstLetterOnGameBoard.getLetterChar() + " is the first letter of the horizontal word");
						System.out.println(horizontalWordString + " Is the horizontal word");

						// check if the horizontal word is in the dictionary
						if (isInDictionary(horizontalWordArraylist)) {
							System.out.println("Word " + getConvertedWordArrayListToString(horizontalWordArraylist)
									+ " is in the dictionary!");
							// if it is add the value of the word to the total
							// score.
							int horizontalWordValue = getWordValue(horizontalWordArraylist, wordArrayList);
							points += horizontalWordValue;
							System.out.println("The word " + horizontalWordString + " is worth " + horizontalWordValue
									+ " points.");

							// if entire hand is placed add 40 points to the
							// score
							if (letterBox.getTiles().size() == wordArrayList.size()) {
								points += 40;
							}

							// Now get all vertical words.
							// for every letter that's been placed down, see if
							// it forms a vertical word that is bigger than 1
							// character.
							for (Letter letter : wordArrayList) {
								ArrayList<Letter> verticalWordArraylist = getVerticalWord(letter, wordArrayList);
								String verticalWordString = getConvertedWordArrayListToString(verticalWordArraylist);
								// it can only be a word if it's bigger than one
								// letter
								if (verticalWordArraylist.size() > 1) {
									// check if the vertical word is in the
									// dictionary
									if (isInDictionary(verticalWordArraylist)) {
										System.out.println(
												"Word " + getConvertedWordArrayListToString(verticalWordArraylist)
														+ " is in the dictionary!");
										// if it is add the value of the word to
										// the total score.
										int verticalWordValue = getWordValue(verticalWordArraylist, wordArrayList);
										points += verticalWordValue;
										System.out.println("The word " + verticalWordString + " is worth "
												+ verticalWordValue + " points.");
									} else {
										// if it's not placement isn't valid
										placementIsValid = false;
										// add word to list of wrong words
										wrongWordsString += getConvertedWordArrayListToString(verticalWordArraylist)
												+ ", ";
										System.out.println(wrongWordsString);
									}
								}
							}
						} else {
							// if it's not placement isn't valid
							placementIsValid = false;
							// add word to list of wrong words
							wrongWordsString += getConvertedWordArrayListToString(horizontalWordArraylist) + ", ";
							System.out.println(wrongWordsString);
						}

						if (placementIsValid) {
							submitValidWord(points, wordArrayList);
						} else {
							wordIsInvalid(wrongWordsString);
						}
					}
				}
			}

			// if vertical
			else if (wordOrientation == 1) {
				System.out.println("Word orientation is vertical");
				// Check if there's no gaps in the placement (no empty tiles
				// between letters)
				if (isVerticalWordPlacedWithoutGaps(wordArrayList)) {
					System.out.println("Vertical word is placed without gaps");
					if (isWordAttached(wordArrayList)) {
						System.out.println("Word is attached to existing letter.");
						boolean placementIsValid = true;
						int points = 0;
						// get the first letter of the vertical word
						Letter firstLetterInWordArrayList = getLowestYLetter(wordArrayList);
						Letter firstLetterOnGameBoard = getFirstVerticalWordLetter(firstLetterInWordArrayList);

						ArrayList<Letter> verticalWordArraylist = getVerticalWord(firstLetterOnGameBoard,
								wordArrayList);
						String verticalWordString = getConvertedWordArrayListToString(verticalWordArraylist);

						System.out.println(
								firstLetterOnGameBoard.getLetterChar() + " is the first letter of the vertical word");
						System.out.println(verticalWordString + " Is the vertical word");

						// check if the vertical word is in the dictionary
						if (isInDictionary(verticalWordArraylist)) {
							System.out.println("Word " + getConvertedWordArrayListToString(verticalWordArraylist)
									+ " is in the dictionary!");
							// if it is add the value of the word to the total
							// score.
							int verticalWordValue = getWordValue(verticalWordArraylist, wordArrayList);
							points += verticalWordValue;
							System.out.println(
									"The word " + verticalWordString + " is worth " + verticalWordValue + " points.");

							// if entire hand is placed add 40 points to the
							// score
							if (letterBox.getTiles().size() == wordArrayList.size()) {
								points += 40;
							}

							// Now get all horizontal words.
							// for every letter that's been placed down, see if
							// it forms a horizontal word that is bigger than 1
							// character.
							for (Letter letter : wordArrayList) {
								ArrayList<Letter> horizontalWordArraylist = getHorizontalWord(letter, wordArrayList);
								String horizontalWordString = getConvertedWordArrayListToString(
										horizontalWordArraylist);
								// it can only be a word if it's bigger than one
								// letter
								if (horizontalWordArraylist.size() > 1) {
									// check if the horizontal word is in the
									// dictionary
									if (isInDictionary(horizontalWordArraylist)) {
										System.out.println(
												"Word " + getConvertedWordArrayListToString(horizontalWordArraylist)
														+ " is in the dictionary!");
										// if it is add the value of the word to
										// the total score.
										int horizontalWordValue = getWordValue(horizontalWordArraylist, wordArrayList);
										points += horizontalWordValue;
										System.out.println("The word " + horizontalWordString + " is worth "
												+ horizontalWordValue + " points.");
									} else {
										// if it's not placement isn't valid
										placementIsValid = false;
										// add word to list of wrong words
										wrongWordsString += getConvertedWordArrayListToString(horizontalWordArraylist)
												+ ", ";
										System.out.println(wrongWordsString);
									}
								}
							}
						} else {
							// if it's not placement isn't valid
							placementIsValid = false;
							// add word to list of wrong words
							wrongWordsString += getConvertedWordArrayListToString(verticalWordArraylist) + ", ";
							System.out.println(wrongWordsString);
						}

						if (placementIsValid) {
							submitValidWord(points, wordArrayList);
						} else {
							wordIsInvalid(wrongWordsString);
						}
					}
				}
			}

			// else if word is invalid
			else if (wordOrientation == -1) {
				// give a popup message that the letters aren't placed correctly
				System.out.println("Word orientation is invalid");
				JOptionPane.showMessageDialog(null, "Letters zijn niet volledig horizontaal of verticaal geplaatst.");
			}
		}
	}
	// methods for Mathijs' score indicator: (getTotalScore() and getMainWord())
	// to get the orientation of the mainWord use the existing method
	// getWordOrientation()

	// TODO REMOVE THIS METHOD
	// get the score; if the letter placement is in any way invalid return -1
	@Deprecated
	public int getTotalScore() {
		// TODO en de score; misschien door variabelen te setten en daar getters
		// voor maken.
		int points = 0;

		ArrayList<Letter> wordArrayList = getPlacedLetters();
		// get number of letters placed down (woordArrayList.size())
		int wordSize = wordArrayList.size();
		// if number == 0 return -1
		if (wordSize == 0) {
			return -1;
		}
		// if number == 1
		else if (wordSize == 1) {
			// if isLetterAttached returns true
			if (isLetterAttached(wordArrayList.get(0))) {
				// get the horizontal word
				ArrayList<Letter> horizontalWordArraylist = getHorizontalWord(wordArrayList.get(0), wordArrayList);
				// it can only be a word if it's bigger than one letter
				if (horizontalWordArraylist.size() > 1) {
					int horizontalWordValue = getWordValue(horizontalWordArraylist, wordArrayList);
					points += horizontalWordValue;
				}
				// get the vertical word
				ArrayList<Letter> verticalWordArraylist = getVerticalWord(wordArrayList.get(0), wordArrayList);
				// it can only be a word if it's bigger than one letter
				if (verticalWordArraylist.size() > 1) {
					int verticalWordValue = getWordValue(verticalWordArraylist, wordArrayList);
					points += verticalWordValue;
				}
			}
		}

		// if number > 1
		else if (wordSize > 1) {
			// determine word orientation
			int wordOrientation = getWordOrientation(wordArrayList);
			// if horizontal
			if (wordOrientation == 0) {
				// Check if there's no gaps in the placement (no empty tiles
				// between letters)
				if (isHorizontalWordPlacedWithoutGaps(wordArrayList)) {
					if (isWordAttached(wordArrayList)) {
						// get the first letter of the horizontal word
						Letter firstLetterInWordArrayList = getLowestXLetter(wordArrayList);
						Letter firstLetterOnGameBoard = getFirstHorizontalWordLetter(firstLetterInWordArrayList);

						ArrayList<Letter> horizontalWordArraylist = getHorizontalWord(firstLetterOnGameBoard,
								wordArrayList);
						@SuppressWarnings("unused")
						String horizontalWordString = getConvertedWordArrayListToString(horizontalWordArraylist);

						int horizontalWordValue = getWordValue(horizontalWordArraylist, wordArrayList);
						points += horizontalWordValue;

						// if entire hand is placed add 40 points to the score
						if (letterBox.getTiles().size() == wordArrayList.size()) {
							points += 40;
						}

						// Now get all vertical words.
						// for every letter that's been placed down, see if it
						// forms a vertical word that is bigger than 1
						// character.
						for (Letter letter : wordArrayList) {
							ArrayList<Letter> verticalWordArraylist = getVerticalWord(letter, wordArrayList);
							@SuppressWarnings("unused")
							String verticalWordString = getConvertedWordArrayListToString(verticalWordArraylist);
							// it can only be a word if it's bigger than one
							// letter
							if (verticalWordArraylist.size() > 1) {
								int verticalWordValue = getWordValue(verticalWordArraylist, wordArrayList);
								points += verticalWordValue;
							}
						}
					} else {
						return -1;
					}
				} else {
					return -1;
				}
			}

			// if vertical
			else if (wordOrientation == 1) {
				// Check if there's no gaps in the placement (no empty tiles
				// between letters)
				if (isVerticalWordPlacedWithoutGaps(wordArrayList)) {
					if (isWordAttached(wordArrayList)) {
						// get the first letter of the vertical word
						Letter firstLetterInWordArrayList = getLowestYLetter(wordArrayList);
						Letter firstLetterOnGameBoard = getFirstVerticalWordLetter(firstLetterInWordArrayList);

						ArrayList<Letter> verticalWordArraylist = getVerticalWord(firstLetterOnGameBoard,
								wordArrayList);
						@SuppressWarnings("unused")
						String verticalWordString = getConvertedWordArrayListToString(verticalWordArraylist);

						int verticalWordValue = getWordValue(verticalWordArraylist, wordArrayList);
						points += verticalWordValue;

						// if entire hand is placed add 40 points to the score
						if (letterBox.getTiles().size() == wordArrayList.size()) {
							points += 40;
						}

						// Now get all horizontal words.
						// for every letter that's been placed down, see if it
						// forms a horizontal word that is bigger than 1
						// character.
						for (Letter letter : wordArrayList) {
							ArrayList<Letter> horizontalWordArraylist = getHorizontalWord(letter, wordArrayList);
							@SuppressWarnings("unused")
							String horizontalWordString = getConvertedWordArrayListToString(horizontalWordArraylist);
							// it can only be a word if it's bigger than one
							// letter
							if (horizontalWordArraylist.size() > 1) {
								int horizontalWordValue = getWordValue(horizontalWordArraylist, wordArrayList);
								points += horizontalWordValue;
							}
						}
					} else {
						return -1;
					}
				} else {
					return -1;
				}
			}

			// else if wordOrientation is invalid
			else if (wordOrientation == -1) {
				return -1;
			}
		}
		return points;
	}

	// set all the necessary variables for displaying the score (score,
	// mainWord, mainWordOrientation)
	public void setScoreTrackingVariables() {
		int score = 0;
		ArrayList<Letter> mainWord = new ArrayList<>();
		String mainWordOrientation = new String();

		ArrayList<Letter> wordArrayList = getPlacedLetters();
		// get number of letters placed down (woordArrayList.size())
		int wordSize = wordArrayList.size();
		// if number == 0 return -1
		if (wordSize == 0) {
			score = -1;
		}
		// if number == 1
		else if (wordSize == 1) {
			// if isLetterAttached returns true
			if (isLetterAttached(wordArrayList.get(0))) {
				// get the horizontal word
				ArrayList<Letter> horizontalWordArraylist = getHorizontalWord(wordArrayList.get(0), wordArrayList);
				// it can only be a word if it's bigger than one letter
				if (horizontalWordArraylist.size() > 1) {
					int horizontalWordValue = getWordValue(horizontalWordArraylist, wordArrayList);
					score += horizontalWordValue;
				}
				// get the vertical word
				ArrayList<Letter> verticalWordArraylist = getVerticalWord(wordArrayList.get(0), wordArrayList);
				// it can only be a word if it's bigger than one letter
				if (verticalWordArraylist.size() > 1) {
					int verticalWordValue = getWordValue(verticalWordArraylist, wordArrayList);
					score += verticalWordValue;
				}

				// the biggest word will be the mainWord; if they are the same
				// size the horizontal word will be the main word.
				if (horizontalWordArraylist.size() >= verticalWordArraylist.size()) {
					mainWord = horizontalWordArraylist;
					mainWordOrientation = "horizontal";
				} else {
					mainWord = verticalWordArraylist;
					mainWordOrientation = "vertical";
				}
			}
		}

		// if number > 1
		else if (wordSize > 1) {
			// determine word orientation
			int wordOrientation = getWordOrientation(wordArrayList);
			// if horizontal
			if (wordOrientation == 0) {
				mainWordOrientation = "horizontal";
				// Check if there's no gaps in the placement (no empty tiles
				// between letters)
				if (isHorizontalWordPlacedWithoutGaps(wordArrayList)) {
					if (isWordAttached(wordArrayList)) {
						// get the first letter of the horizontal word
						Letter firstLetterInWordArrayList = getLowestXLetter(wordArrayList);
						Letter firstLetterOnGameBoard = getFirstHorizontalWordLetter(firstLetterInWordArrayList);

						ArrayList<Letter> horizontalWordArraylist = getHorizontalWord(firstLetterOnGameBoard,
								wordArrayList);
						mainWord = horizontalWordArraylist;

						int horizontalWordValue = getWordValue(horizontalWordArraylist, wordArrayList);
						score += horizontalWordValue;

						// if entire hand is placed add 40 points to the score
						if (letterBox.getTiles().size() == wordArrayList.size()) {
							score += 40;
						}

						// Now get all vertical words.
						// for every letter that's been placed down, see if it
						// forms a vertical word that is bigger than 1
						// character.
						for (Letter letter : wordArrayList) {
							ArrayList<Letter> verticalWordArraylist = getVerticalWord(letter, wordArrayList);
							// it can only be a word if it's bigger than one
							// letter
							if (verticalWordArraylist.size() > 1) {
								int verticalWordValue = getWordValue(verticalWordArraylist, wordArrayList);
								score += verticalWordValue;
							}
						}
					} else {
						score = -1;
					}
				} else {
					score = -1;
				}
			}

			// if vertical
			else if (wordOrientation == 1) {
				mainWordOrientation = "vertical";
				// Check if there's no gaps in the placement (no empty tiles
				// between letters)
				if (isVerticalWordPlacedWithoutGaps(wordArrayList)) {
					if (isWordAttached(wordArrayList)) {
						// get the first letter of the vertical word
						Letter firstLetterInWordArrayList = getLowestYLetter(wordArrayList);
						Letter firstLetterOnGameBoard = getFirstVerticalWordLetter(firstLetterInWordArrayList);

						ArrayList<Letter> verticalWordArraylist = getVerticalWord(firstLetterOnGameBoard,
								wordArrayList);
						mainWord = verticalWordArraylist;

						int verticalWordValue = getWordValue(verticalWordArraylist, wordArrayList);
						score += verticalWordValue;

						// if entire hand is placed add 40 points to the score
						if (letterBox.getTiles().size() == wordArrayList.size()) {
							score += 40;
						}

						// Now get all horizontal words.
						// for every letter that's been placed down, see if it
						// forms a horizontal word that is bigger than 1
						// character.
						for (Letter letter : wordArrayList) {
							ArrayList<Letter> horizontalWordArraylist = getHorizontalWord(letter, wordArrayList);
							// it can only be a word if it's bigger than one
							// letter
							if (horizontalWordArraylist.size() > 1) {
								int horizontalWordValue = getWordValue(horizontalWordArraylist, wordArrayList);
								score += horizontalWordValue;
							}
						}
					} else {
						score = -1;
					}
				} else {
					score = -1;
				}
			}

			// else if wordOrientation is invalid
			else if (wordOrientation == -1) {
				score = -1;
			}
		}
		this.score = score;
		this.mainWord = mainWord;
		this.mainWordOrientation = mainWordOrientation;
	}

	// (score tracking variable 1); get the score
	public int getScore() {
		return score;
	}

	// (score tracking variable 2);get the word
	public ArrayList<Letter> getMainWord() {
		return mainWord;
	}

	// (score tracking variable 3);get the word orientation
	public String getMainWordOrientation() {
		return mainWordOrientation;
	}

	public boolean doPass() {
		int option = JOptionPane.showConfirmDialog(null, "Weet je zeker dat je deze beurt wilt passen?", "Wordfeud",
				JOptionPane.YES_NO_OPTION);
		if (option == JOptionPane.YES_OPTION) {
			int turnNumber = gsm.getUser().getTurnNumber();
			int game = gsm.getUser().getGameNumber();
			String username = gsm.getUser().getUsername();
			databaseController.queryUpdate("INSERT INTO beurt VALUES (" + (turnNumber + 1) + ", " + game + ",'"
					+ username + "'," + 0 + ", 'pass');");
			ResultSet passRS = databaseController
					.query("SELECT * FROM beurt WHERE id = (" + turnNumber + ") OR id = (" + turnNumber + " - 1);");
			int counter = 1;
			try {
				while (passRS.next()) {
					if (passRS.getString("aktie_type").equals("pass")) {
						counter++;
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if (counter == 3) {
				gsm.getUser().setTurnNumber(turnNumber - 1);
				ArrayList<Letter> opponentLetters = letterBox.getLetters();
				gsm.getUser().setTurnNumber(turnNumber + 1);
				ArrayList<Letter> userLetters = letterBox.getLetters();
				int userLetterPoints = 0;
				for (Letter letter : userLetters) {
					userLetterPoints += letter.getScore();
				}
				int opponentLetterPoints = 0;
				for (Letter letter : opponentLetters) {
					opponentLetterPoints += letter.getScore();
				}
				int userScore = 0;
				int opponentScore = 0;
				userScore -= opponentLetterPoints;
				opponentScore += userLetterPoints;
				opponentScore -= opponentLetterPoints;
				this.doEndGame(userScore, opponentScore, turnNumber, game, databaseController);
			} else {
				JOptionPane.showMessageDialog(null, "Het aantal achtereenvolgende pass beurten is nu: " + counter);
			}
			return true;
		}
		return false;
	}

	public void doResign() {
		int option = JOptionPane.showConfirmDialog(null, "Weet je zeker dat je wil opgeven?", "Wordfeud",
				JOptionPane.YES_NO_OPTION);
		if (option == JOptionPane.YES_OPTION) {
			int turn = gsm.getUser().getTurnNumber();
			int game = gsm.getUser().getGameNumber();
			String username = gsm.getUser().getUsername();
			databaseController.queryUpdate("INSERT INTO beurt VALUES (" + (turn + 1) + ", " + game + ",'" + username
					+ "'," + 0 + ", 'resign');");
			this.doEndGame((-gsm.getUser().getUserScore()), 0, (turn + 1), game, databaseController);
		}
	}

	private void doEndGame(int userScore, int opponentScore, int turn, int game,
			DatabaseController databaseController) {
		boolean isGood = false;
		int counter = 0;
		turn += 1;
		while (!isGood) {
			counter++;
			if (databaseController.pingedBack()) {
				String userEndQuery = "INSERT INTO beurt (id, spel_id, account_naam, score, aktie_type) VALUES (" + turn
						+ ", " + game + ",'" + gsm.getUser().getOpponentName() + "'," + opponentScore + ", 'end');";
				databaseController.queryUpdate(userEndQuery);
				ResultSet rs = databaseController.query("SELECT * FROM beurt WHERE spel_id = " + game);
				ArrayList<Integer> turns = new ArrayList<Integer>();
				try {
					while (rs.next()) {
						turns.add(rs.getInt("id"));
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (turns.contains(turn)) {
					isGood = true;
				}
			} else if (counter == 50) {
				JOptionPane.showMessageDialog(null, "Er ging iets fout bij het inserten van de userscore");
				gsm.setGamestate(GamestateManager.gameOverviewState);
				return;
			} else if (!databaseController.pingedBack()) {
				databaseController = new DatabaseController();
			}

		}
		counter = 0;
		isGood = false;
		turn += 1;
		while (!isGood) {
			if (databaseController.pingedBack()) {
				counter++;
				String opponentEndQuery = "INSERT INTO beurt (id, spel_id, account_naam, score, aktie_type) VALUES ("
						+ turn + ", " + game + ",'" + gsm.getUser().getChallengerName() + "'," + userScore
						+ ", 'end');";
				databaseController.queryUpdate(opponentEndQuery);
				ResultSet rs = databaseController.query("SELECT * FROM beurt WHERE spel_id = " + game);
				ArrayList<Integer> turns = new ArrayList<Integer>();
				try {
					while (rs.next()) {
						turns.add(rs.getInt("id"));
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (turns.contains(turn)) {
					isGood = true;
				}
			} else if (counter == 50) {
				JOptionPane.showMessageDialog(null, "Er ging iets fout bij het inserten van de userscore");
				gsm.setGamestate(GamestateManager.gameOverviewState);
				return;
			} else if (!databaseController.pingedBack()) {
				databaseController = new DatabaseController();
			}
		}
		JOptionPane.showMessageDialog(null, "Het spel is geeindigd!\n" + gsm.getUser().getChallengerName() + " heeft "
				+ gsm.getUser().getUserScore() + " punten.\n" + gsm.getUser().getOpponentName() + " heeft "
				+ gsm.getUser().getOpponentScore() + " punten.\n" + gsm.getUser().getWinner() + " is de winnaar!");
		databaseController.queryUpdate("UPDATE spel SET toestand_type = 'finished' WHERE id = " + game);
		gsm.setGamestate(GamestateManager.gameOverviewState);
	}
}