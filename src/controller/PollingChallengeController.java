package controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import Gamestate.GamestateManager;

public class PollingChallengeController implements Runnable
{
	private GamestateManager gsm;
	private DatabaseController db_c;
	
	public PollingChallengeController(GamestateManager gsm)
	{
		this.gsm = gsm;	
		this.db_c = gsm.getDatabaseController();
	}
	
	public void run()
	{
		while(true)//Always running
		{
			try{
				if(gsm.getUser() != null) //User logged in
				{
					ResultSet openRequests = db_c.query("SELECT * FROM spel WHERE toestand_type = 'request' AND reaktie_type = 'accepted' AND account_naam_uitdager = '"+gsm.getUser().getUsername()+"';");
					while(openRequests.next()) //Request that's open
					{
						int gameID = openRequests.getInt("id");
						String yourName = openRequests.getString("account_naam_uitdager");
						String challengedName = openRequests.getString("account_naam_tegenstander");
						db_c.queryUpdate("UPDATE spel SET toestand_type = 'playing' WHERE id = " + gameID + ";");
						db_c.queryUpdate("INSERT INTO letter(`id`,`spel_id`,`LetterType_LetterSet_code`,`LetterType_karakter`)VALUES(001,"+ gameID +", 'NL', 'A'),(002,"+ gameID +", 'NL', 'A'),(004,"+ gameID +", 'NL', 'A'),(005,"+ gameID +", 'NL', 'A'),(006,"+ gameID +", 'NL', 'A'),(007,"+ gameID +", 'NL', 'A'),(008,"+ gameID +", 'NL', 'B'),(009,"+ gameID +", 'NL', 'B'),(010,"+ gameID +", 'NL', 'C'),(011,"+ gameID +", 'NL', 'C'),(012,"+ gameID +", 'NL', 'D'),(013,"+ gameID +", 'NL', 'D'),(014,"+ gameID +", 'NL', 'D'),(015,"+ gameID +", 'NL', 'D'),(016,"+ gameID +", 'NL', 'D'),(017,"+ gameID +", 'NL', 'E'),(018,"+ gameID +", 'NL', 'E'),(019,"+ gameID +", 'NL', 'E'),(020,"+ gameID +", 'NL', 'E'),(021,"+ gameID +", 'NL', 'E'),(022,"+ gameID +", 'NL', 'E'),(023,"+ gameID +", 'NL', 'E'),(024,"+ gameID +", 'NL', 'E'),(025,"+ gameID +", 'NL', 'E'),(026,"+ gameID +", 'NL', 'E'),(027,"+ gameID +", 'NL', 'E'),(028,"+ gameID +", 'NL', 'E'),(029,"+ gameID +", 'NL', 'E'),(030,"+ gameID +", 'NL', 'E'),(031,"+ gameID +", 'NL', 'E'),(032,"+ gameID +", 'NL', 'E'),(033,"+ gameID +", 'NL', 'E'),(034,"+ gameID +", 'NL', 'E'),(035,"+ gameID +", 'NL', 'F'),(036,"+ gameID +", 'NL', 'F'),(037,"+ gameID +", 'NL', 'G'),(038,"+ gameID +", 'NL', 'G'),(039,"+ gameID +", 'NL', 'G'),(040,"+ gameID +", 'NL', 'H'),(041,"+ gameID +", 'NL', 'H'),(042,"+ gameID +", 'NL', 'I'),(043,"+ gameID +", 'NL', 'I'),(044,"+ gameID +", 'NL', 'I'),(045,"+ gameID +", 'NL', 'I'),(046,"+ gameID +", 'NL', 'J'),(047,"+ gameID +", 'NL', 'J'),(048,"+ gameID +", 'NL', 'K'),(049,"+ gameID +", 'NL', 'K'),(050,"+ gameID +", 'NL', 'K'),(051,"+ gameID +", 'NL', 'L'),(052,"+ gameID +", 'NL', 'L'),(053,"+ gameID +", 'NL', 'L'),(054,"+ gameID +", 'NL', 'M'),(055,"+ gameID +", 'NL', 'M'),(056,"+ gameID +", 'NL', 'M'),(057,"+ gameID +", 'NL', 'N'),(058,"+ gameID +", 'NL', 'N'),(059,"+ gameID +", 'NL', 'N'),(060,"+ gameID +", 'NL', 'N'),(061,"+ gameID +", 'NL', 'N'),(062,"+ gameID +", 'NL', 'N'),(063,"+ gameID +", 'NL', 'N'),(064,"+ gameID +", 'NL', 'N'),(065,"+ gameID +", 'NL', 'N'),(066,"+ gameID +", 'NL', 'N'),(067,"+ gameID +", 'NL', 'N'),(068,"+ gameID +", 'NL', 'O'),(069,"+ gameID +", 'NL', 'O'),(070,"+ gameID +", 'NL', 'O'),(071,"+ gameID +", 'NL', 'O'),(072,"+ gameID +", 'NL', 'O'),(073,"+ gameID +", 'NL', 'O'),(074,"+ gameID +", 'NL', 'P'),(075,"+ gameID +", 'NL', 'P'),(076,"+ gameID +", 'NL', 'Q'),(077,"+ gameID +", 'NL', 'R'),(078,"+ gameID +", 'NL', 'R'),(079,"+ gameID +", 'NL', 'R'),(080,"+ gameID +", 'NL', 'R'),(081,"+ gameID +", 'NL', 'R'),(082,"+ gameID +", 'NL', 'S'),(083,"+ gameID +", 'NL', 'S'),(084,"+ gameID +", 'NL', 'S'),(085,"+ gameID +", 'NL', 'S'),(086,"+ gameID +", 'NL', 'S'),(087,"+ gameID +", 'NL', 'T'),(088,"+ gameID +", 'NL', 'T'),(089,"+ gameID +", 'NL', 'T'),(090,"+ gameID +", 'NL', 'T'),(091,"+ gameID +", 'NL', 'T'),(092,"+ gameID +", 'NL', 'U'),(093,"+ gameID +", 'NL', 'U'),(094,"+ gameID +", 'NL', 'U'),(095,"+ gameID +", 'NL', 'V'),(096,"+ gameID +", 'NL', 'V'),(097,"+ gameID +", 'NL', 'W'),(098,"+ gameID +", 'NL', 'W'),(099,"+ gameID +", 'NL', 'X'),(100,"+ gameID +", 'NL', 'Y'),(101,"+ gameID +", 'NL',  'Z'),(102,"+gameID +", 'NL', 'Z'),(103,"+gameID +", 'NL', '?'),(104,"+gameID +", 'NL', '?');");
						
						//Setting the turn info
						db_c.queryUpdate("INSERT INTO beurt (id, spel_id, account_naam, score, aktie_type) VALUES (1, "+gameID+", '"+yourName+"', 0, 'begin'),(2, "+gameID+", '"+challengedName+"', 0, 'begin')");
					
						// fill letterbakjeletter for turn 1 and 2
						fillLetterBoxes(gameID);
					}
				}
				Thread.sleep(4000);
			}
			catch(NullPointerException NPE){NPE.printStackTrace();}
			catch(InterruptedException IPE){IPE.printStackTrace();}
			catch(SQLException SQL){SQL.printStackTrace();}
		}
	}
	
	private void fillLetterBoxes(int gameID)
	{
		ArrayList<Integer> randomLetterList = new ArrayList<Integer>();
        for (int i = 1; i < 115; i++) 
        {
        	randomLetterList.add(new Integer(i));
        }
        Collections.shuffle(randomLetterList);
        for (int i = 0; i < 7; i++) 
        {
            db_c.queryUpdate("INSERT INTO letterbakjeletter (spel_id,letter_id,beurt_id) VALUES (" + gameID + "," + randomLetterList.get(i) + ",1);");
        }
        for (int i = 7; i < 13; i++)
        {
        	db_c.queryUpdate("INSERT INTO letterbakjeletter (spel_id,letter_id,beurt_id) VALUES (" + gameID + "," + randomLetterList.get(i) + ",2);");
        }
	}
}