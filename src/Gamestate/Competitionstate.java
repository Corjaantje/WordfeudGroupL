package Gamestate;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import controller.CompetitionController;
import controller.DatabaseController;

public class Competitionstate extends Gamestate{
	
	private JPanel competitionPanel;
	private CompetitionController competitionController;
	
	private boolean isCreated = false;

	public Competitionstate(GamestateManager gsm, DatabaseController db_c) {
		super(gsm, db_c);
	}

	@Override
	public void draw(Graphics2D g) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void create() {
		if(!isCreated){
			competitionController = new CompetitionController(gsm);
			competitionPanel = new JPanel();
			competitionPanel.setBackground(Color.black);
			competitionPanel.setLocation(0,0);
			competitionPanel.setLayout(new BorderLayout());
			this.add(competitionPanel);
			
			JTextField searchCompetition = new JTextField(25);
			this.competitionPanel.add(searchCompetition,BorderLayout.PAGE_START);
			
			JButton addCompetition = new JButton("Competitie toevoegen");
			addCompetition.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent arg0) {
					String result = JOptionPane.showInputDialog("Geef een competitie naam !");
					competitionController.addCompetition(result);
					
				}
				
			});
//			this.competitionPanel.add(addCompetition,BorderLayout.LINE_END);
			
			JButton joinCompetition = new JButton("Competitie deelnemen");
			joinCompetition.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					String result = JOptionPane.showInputDialog("Geef een competitie naam !");
					competitionController.joinCompetition(result);
					
					
				}
				
			});
			this.competitionPanel.add(joinCompetition, BorderLayout.LINE_END);
			
			JLabel competitionfield = new JLabel("Competitie 1");
			competitionfield.setPreferredSize(new Dimension(1000,200));
			this.competitionPanel.add(competitionfield);
			isCreated = true;
		}else{
			
		}
	}

	
}

