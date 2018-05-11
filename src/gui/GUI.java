package gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

public class GUI {
JFrame mainWindow;
JTabbedPane tabbedPane;
private double version;
protected JComponent makeTextPanel(String text) {
    JPanel panel = new JPanel(false);
    JLabel filler = new JLabel(text);
    filler.setHorizontalAlignment(JLabel.CENTER);
    panel.setLayout(new GridLayout(1, 1));
    panel.add(filler);
    return panel;
}
	public GUI(double d) {
		version = d;
		mainWindow = new JFrame("DiscoPoloRD v: " + version);
		mainWindow.setSize(500, 500);
		mainWindow.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		mainWindow.setLayout(new FlowLayout());
		
		mainWindow.add(new JButton("Call"));
		
		
		mainWindow.add(new JButton("Shite"));
		
		tabbedPane = new JTabbedPane();
		JComponent panel1 = makeTextPanel("Panel #1");
		tabbedPane.addTab("Kontakty", null, panel1,
		                  "To be Filled");
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

		JComponent panel2 = makeTextPanel("Panel #2");
		tabbedPane.addTab("Szukaj", null, panel2,
		                  "To be filled");
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

		JComponent panel3 = makeTextPanel("Panel #3");
		tabbedPane.addTab("Ustawienia", null, panel3,
		                  "To be Filled");
		tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);

		JComponent panel4 = makeTextPanel(
		        "");
		panel4.setPreferredSize(new Dimension(410, 50));
//		panel4.add(new TextArea("Projekt Komunikatora G³osowego\nWykonanie:\n\tDariusz Krajewski - klient\n\tKrystian Minta - server"));
		tabbedPane.addTab("O projekcie", null, panel4,
		                      "Does nothing at all");
		tabbedPane.setMnemonicAt(3, KeyEvent.VK_4);
		mainWindow.add(tabbedPane);
		//mainWindow.setContentPane(pane);
		mainWindow.setVisible(true);
	}
	
	
}
