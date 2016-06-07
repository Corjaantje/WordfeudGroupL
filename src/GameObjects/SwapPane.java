package GameObjects;

import java.awt.Checkbox;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import Gamestate.GamestateManager;
import Gamestate.Playstate;
import Main.GUI;
import controller.DatabaseController;

public class SwapPane extends JPanel {

	private LetterBox letterBox;

	private JButton changeButton;

	private DatabaseController db_c;

	private GamestateManager gsm;

	private ArrayList<Checkbox> checkboxs;

	private boolean isCreated = false;

	private Box box;

	private DefaultTableModel model;

	private JTable table;

	private Playstate playState;
	
	public SwapPane(LetterBox letterBox, DatabaseController db_c, GamestateManager gsm, Playstate playState) {
		this.db_c = db_c;
		this.gsm = gsm;
		this.playState = playState;
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setPreferredSize(new Dimension((int) (GUI.WIDTH / 3), (int) (GUI.HEIGHT / 2)));
		this.letterBox = letterBox;
		this.createCheckBoxsBar();
		this.createButton();
		this.add(box);
		this.createTabel();
		isCreated = true;
	}

	private void createCheckBoxsBar() {

		checkboxs = new ArrayList<Checkbox>();
		for (int i = 0; i < letterBox.getLetters().size(); i++) {
			checkboxs.add(new Checkbox(letterBox.getLetters().get(i).getLetterChar(), true));
		}

		box = Box.createHorizontalBox();
		for (Checkbox checkbox : checkboxs) {
			box.add(checkbox);
		}
	}

	private void createButton() {
		if (!checkboxs.isEmpty()) {
			Box box = Box.createHorizontalBox();
			for (Checkbox checkbox : checkboxs) {
				box.add(checkbox);
			}

			JLabel buttonLabel = new JLabel("Verwissel");
			Image image = Toolkit.getDefaultToolkit().getImage("Resources/replaceButton.png");
			ImageIcon icon = new ImageIcon(image);
			buttonLabel.setIcon(icon);
			changeButton = new JButton();
			changeButton.add(buttonLabel);

			box.add(changeButton);
			this.add(box);

			changeButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					doSwap();
				}
			});
			box.add(changeButton);
		}
	}
	
	private void doSwap()
	{
		// make an arraylist of all letters that have been selected to swap
		int counter = 0;
		ArrayList<Letter> swappedLetters = new ArrayList<>();
		for (Checkbox checkbox : checkboxs)
		{
			if (checkbox.getState())
			{
				swappedLetters.add(letterBox.getLetters().get(counter));
			}
			counter++;
		}
		// check if there are that many letters available in the pot
		int potSize = 999;
		String potQuery = "SELECT COUNT(letter_id) FROM pot WHERE spel_id = " + gsm.getUser().getGameNumber();
		ResultSet pot = db_c.query(potQuery);
		try
		{
			if (pot.next())
			{
				potSize = pot.getInt(1);
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		if (swappedLetters.size() <= potSize)
		{
			// create the turn
			String beurtUpdateQuery = "INSERT INTO beurt (`id`, `spel_id`,`account_naam`,`score`,aktie_type) VALUES(" + (gsm.getUser().getMaxTurnNumber() + 1) + "," + gsm.getUser().getGameNumber() + ", '" + gsm.getUser().getUsername() + "',0, 'swap')";
			db_c.queryUpdate(beurtUpdateQuery);
			// swap the letters
			letterBox.replacePlacedLetters(swappedLetters);
			// refresh playstate
			playState.reloadPlaystate();
			// close SwapFrame
			((JFrame) SwingUtilities.getWindowAncestor(this)).dispatchEvent(new WindowEvent(((JFrame) SwingUtilities.getWindowAncestor(this)), WindowEvent.WINDOW_CLOSING));
			JOptionPane.showMessageDialog(null, "Swap succesvol!");
		} else 
		{
			JOptionPane.showMessageDialog(null, "Er zijn niet genoeg letters in de pot voor deze swap!");
		}
		
	}
	
	@Deprecated
	private void oldDoSwap() {
		int turn = gsm.getUser().getTurnNumber();
		int game = gsm.getUser().getGameNumber();
		ArrayList<String> swappedLetters = new ArrayList<String>();
		boolean lettersAreSwapped = false;
		for (Checkbox checkbox : checkboxs) {
			int char_id = 0;
			String letterChar = checkbox.getLabel();
			ResultSet lbl = db_c
					.query("SELECT * FROM letterbakjeletter AS lb INNER JOIN letter AS l ON l.id = lb.letter_id WHERE beurt_id = "
							+ turn + " AND lb.spel_id = " + game + " AND lettertype_karakter = '" + letterChar + "';");
			try {
				while (lbl.next()) {
					char_id = lbl.getInt("letter_id");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (checkbox.getState()) {
				lettersAreSwapped = true;
				swappedLetters.add(checkbox.getLabel());
				String query = "SELECT * FROM pot WHERE spel_id = "+gsm.getUser().getGameNumber()+";";
				// TODO add this to the query above? - Marc
				// TODO + "AND NOT id = ANY( SELECT letter_id from letterbakjeletter where beurt_id =" + (turn-1) + " OR beurt_id =" + (turn-2);
				ArrayList<Integer> charNumberList = new ArrayList<Integer>();
				ResultSet rs = db_c.query(query);
				try {
					while (rs.next()) {
						charNumberList.add(rs.getInt("letter_id"));
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				int rand = (int) (Math.random() * charNumberList.size());
				db_c.queryUpdate("INSERT INTO letterbakjeletter VALUES (" + game + ", " + charNumberList.get(rand) + ","
						+ (turn + 2) + ");");
				db_c.closeConnection();
			} else {
				db_c.queryUpdate(
						"INSERT INTO letterbakjeletter VALUES (" + game + ", " + char_id + "," + (turn + 2) + ");");
				db_c.closeConnection();
			}

		}
		if (lettersAreSwapped) {

			String userName = gsm.getUser().getUsername();
			int score = 0;
			String action = "swap";
			String letters = "";
			for (String letter : swappedLetters) {

				if (!swappedLetters.iterator().hasNext()) {
					letters = letters.concat(letter);
				} else {
					letters = letters.concat(letter + ", ");
				}
			}
			JOptionPane.showMessageDialog(null, "U heeft de volgende letter(s) geswapped: " + letters,
					"U heeft geswapped!", JOptionPane.PLAIN_MESSAGE);

			db_c.queryUpdate("INSERT INTO beurt VALUES (" + turn + ", " + game + ", '" + userName + "', " + score + ", "
					+ " '" + action + "');");
			db_c.closeConnection();
		} else {
			JOptionPane.showMessageDialog(null, "U heeft geen letters geselecteerd om te swappen.",
					"Geen letters geselecteerd!", JOptionPane.ERROR_MESSAGE);
		}

	}

	private void createTabel() {
		String[] header = { "Letter", "Aantal" };
		model = new DefaultTableModel(header, 0);
		table = new JTable(model);
		table.setEnabled(false);
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setVisible(true);
		this.loadTable();
		add(scrollPane);
	}

	private void loadTable() {
		int counter = model.getRowCount() - 1;
		if (counter != 0) {
			while (counter > 0) {
				model.removeRow(counter);// ERROR
				counter--;
			}
		}
		String query = "SELECT karakter,count(karakter) AS aantal FROM pot WHERE spel_id = "+gsm.getUser().getGameNumber()+" GROUP BY karakter;";
		ResultSet rs = db_c.query(query);
		try {
			while (rs.next()) {
				model.addRow(new Object[] { rs.getString("karakter"), rs.getString("aantal") });
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db_c.closeConnection();
	}

	public void reloadSwapPane() {
		this.removeAll();
		box.removeAll();
		this.createCheckBoxsBar();
		this.createButton();
		this.createTabel();
	}
}
