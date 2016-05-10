package GameObjects;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

import Main.GUI;

public class ChatOutput extends JPanel
{
	public JTextArea chatOutput = new JTextArea();
	private JScrollPane scrollPane = new JScrollPane(chatOutput);

	public ChatOutput()
	{
		this.setLayout(new BorderLayout());
		DefaultCaret caret = (DefaultCaret)chatOutput.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		this.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
		this.addChatOutput();
	}
	
	private void addChatOutput()
	{
		scrollPane.setPreferredSize(new Dimension((int) (GUI.WIDTH/5), (int) (GUI.HEIGHT/1.10)));
		chatOutput.setBorder(new EmptyBorder(10, 10, 10, 10));
		chatOutput.setBackground(Color.BLACK);
		chatOutput.setForeground(Color.WHITE);
		chatOutput.setEditable(false);
		chatOutput.setLineWrap(true);
		chatOutput.setWrapStyleWord(true);
		this.chatOutput.setFont(new Font("Verdana", Font.PLAIN, 10));
		this.add(scrollPane, BorderLayout.NORTH);
	}
	
	public void addLine(String User, String Text)
	{
		chatOutput.append("<" + User + "> ");
		chatOutput.append(Text + "\n");
	}
	
	public void ClearOutput()
	{
		chatOutput.setText("");
	}
}
