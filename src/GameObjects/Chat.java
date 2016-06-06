package GameObjects;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

//Maintained by Corne



import javax.swing.JOptionPane;
import javax.swing.JPanel;

import controller.DatabaseController;
import Gamestate.GamestateManager;
import Main.GUI;

@SuppressWarnings("serial")
public class Chat extends JPanel implements ActionListener, KeyListener
{
	private DatabaseController database;
	private GamestateManager gamestateMananger;
	private ChatInput input = new ChatInput();
	private ChatOutput output = new ChatOutput();
	private ChatConfigFrame config = new ChatConfigFrame(this.output);;

	private boolean filledChat = false;
	private String lastTimeMessage;
	private int savedGameNumber;

	Timer timer = new Timer();
	TimerTask task = new TimerTask()
	{
		public void run()
		{
			fillChatOutput();
		}
	};

	public Chat(DatabaseController dbc, GamestateManager gsm)
	{
		database = dbc;
		gamestateMananger = gsm;
		this.setLayout(new BorderLayout());
		this.setPreferredSize(new Dimension((int) GUI.WIDTH / 4,(int) GUI.HEIGHT));
		this.setBackground(new Color(24, 24, 24));

		this.add(output, BorderLayout.NORTH);
		this.add(input, BorderLayout.SOUTH);

		this.input.sendButton.addActionListener(this);
		this.input.sendButton.setActionCommand("send");

		this.input.configButton.addActionListener(this);
		this.input.configButton.setActionCommand("config");
		this.input.chatInput.addKeyListener(this);
		timer.scheduleAtFixedRate(task, 1000, 1000);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if ("send".equals(e.getActionCommand()))
		{
			this.sendText();
		} else if ("config".equals(e.getActionCommand()))
		{
			config.setVisible(true);
		}
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			this.sendText();
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0)
	{
	}

	@Override
	public void keyTyped(KeyEvent arg0)
	{
	}

	private void sendText()
	{
		String userName = "BLANKNAME";
		if (gamestateMananger.getUser().getUsername() != null)
		{
			userName = gamestateMananger.getUser().getUsername();
		}
		String message = input.chatInput.getText();
		if(!message.contains("\\"))
		{
			if(!message.contains("'"))
			{
				DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
				Date date = new Date();
		
				try
				{
					database.queryUpdate("INSERT INTO chatregel VALUES ('" + userName + "', " + this.savedGameNumber + ", " + dateFormat.format(date) + ", '" + message + "')");
				} catch (Exception e)
				{
					e.printStackTrace();
				}
				this.input.chatInput.setText("");
				this.fillChatOutput();
			}
			else
			{
				JOptionPane.showMessageDialog(null, "Het leesteken 'Apostrof' is niet toegestaan!");	
			}
		}
		else
		{
			JOptionPane.showMessageDialog(null, "Het leesteken 'backslash' is niet toegestaan!");
		}
	}

	private void fillChatOutput()
	{
		try
		{
			if (gamestateMananger.getUser() != null)
			{
				savedGameNumber = gamestateMananger.getUser().getGameNumber();
				ResultSet resultFull = database.query("SELECT * FROM chatregel WHERE spel_id = " + this.savedGameNumber + " ORDER BY tijdstip");

				if (!filledChat) // Chat already filled with previous messages?
				{				
					output.addLine("Console", "Ingelogd als "+ gamestateMananger.getUser().getUsername()+ ". Huidig spelnummer "+ this.savedGameNumber);
					lastTimeMessage = "00000000";
					while (resultFull.next())
					{
						String user = this.properCapsNames(resultFull.getString("account_naam"));
						String message = resultFull.getString("bericht");
						lastTimeMessage = resultFull.getString("tijdstip");
						output.addLine(user, message);
					}
					if (!resultFull.next())
					{
						filledChat = true;
					}
				}
			 else	// Chat has been filled
			{
				ResultSet resultPartial;
				resultPartial = database.query("SELECT * FROM chatregel WHERE spel_id = " + this.savedGameNumber + " ORDER BY tijdstip DESC LIMIT 1");
				if (resultPartial.next())
				{
					String time = resultPartial.getString("tijdstip");
					if (!lastTimeMessage.equals(time))
					{
						String user = this.properCapsNames(resultPartial.getString("account_naam"));
						String message = resultPartial.getString("bericht");
						lastTimeMessage = resultPartial.getString("tijdstip");
						output.addLine(user, message);
					}
				}
			}
		}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private String properCapsNames(String oldUser)
	{
		String finalName = "";
		for(int i=0; i < oldUser.length(); i++)
		{
			char newChar = oldUser.charAt(i);
			finalName = finalName + newChar;
			if(i==0)
			{
				finalName = finalName.toUpperCase();
			}
		}
		return finalName;
	}
	
	public void reloadChat()
	{
		this.filledChat = false;
		lastTimeMessage = "";
		this.output.chatOutput.setText("");
	}

}
