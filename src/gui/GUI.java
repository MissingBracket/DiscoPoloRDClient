package gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import discopolord.ClientLogic;

public class GUI {
	private boolean isLogged = false;
	private JFrame loggingWindow;
	private JFrame registerWindow;
	private JFrame mainWindow;
	private JTabbedPane tabbedPane;
	private double version;
	private ClientLogic logic;
	private SoundHandler guiSounds;
	
	
	public GUI(double d, ClientLogic cl) {
		version = d;
		buildLoginWindows();

	    /*this.logic = cl;
	    	cl.start();*/
		guiSounds = new SoundHandler();
		guiSounds.registerSound("startup", "startup.wav");
		
		
		
		
		mainWindow = new JFrame("DiscoPoloRD v: " + version);
		mainWindow.setSize(450, 500);
		mainWindow.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		mainWindow.setLayout(new FlowLayout());
		JLabel logo = new JLabel(new ImageIcon("./src/media/logo.png"));
		logo.setPreferredSize(new Dimension(400, 200));
		mainWindow.add(logo);
		
		
		mainWindow.add(new JButton("Call"));
		
		mainWindow.add(new JButton("Shite"));
		
		tabbedPane = new JTabbedPane();
		JComponent panel1 = makeContactsTable();
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
		        "asdasdasdasdasdsa");
		//panel4.setPreferredSize(new Dimension(410, 50));
		panel4.add(new TextArea("Projekt Komunikatora G³osowego\nWykonanie:\n\tDariusz Krajewski - klient\n\tKrystian Minta - server"));
		tabbedPane.addTab("O projekcie", null, panel4,
		                      "Does nothing at all");
		tabbedPane.setMnemonicAt(3, KeyEvent.VK_4);
		mainWindow.add(tabbedPane);
		
		//mainWindow.setContentPane(pane);
		
		if(!isLogged)
			loggingWindow.setVisible(true);
		else
			mainWindow.setVisible(true);
	}
	
	public void buildLoginWindows() {
		this.loggingWindow = new JFrame("Sign in to DiscoPoloRD");
		loggingWindow.setSize(400, 400);
		JTextField loginBox = new JTextField();
		JPasswordField passwordBox = new JPasswordField();
		JLabel 	l =  new JLabel("E-mail:"), 
				p = new JLabel("Password:");
		loginBox.setPreferredSize(new Dimension(350, 25));
		passwordBox.setPreferredSize(new Dimension(350, 25));
		
		JButton login = new JButton("Login");
		loggingWindow.setLayout(new FlowLayout());
		
		login.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				login(loginBox.getText(), passwordBox.getPassword());
				isLogged = true;
				loggingWindow.setVisible(false);
				loggingWindow.dispose();
				mainWindow.setVisible(true);
				guiSounds.prepareSound("startup");
				guiSounds.start();
			}
		});
		
		
		JButton register = new JButton("Register");
		register.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				loggingWindow.dispose();
				loggingWindow.setVisible(false);
				registerWindow = new JFrame("Sign up with DiscoPoloRD");
				registerWindow.setPreferredSize(new Dimension(400, 450));
				JButton accept = new JButton("Register"),
						signin = new JButton("Login");
				JTextField registerNickBox = new JTextField();
				JTextField registerEmailBox = new JTextField();
				
				JPasswordField registerPassBox = new JPasswordField();
				JPasswordField registerPassBoxCheck = new JPasswordField();
				JLabel 	r_l =  new JLabel("E-mail:"), 
						r_p = new JLabel("Password:"),
						r_pCheck = new JLabel("Repeat Password:");
				registerNickBox.setPreferredSize(new Dimension(350, 25));
				registerEmailBox.setPreferredSize(new Dimension(350, 25));
				registerPassBox.setPreferredSize(new Dimension(350, 25));
				registerPassBoxCheck.setPreferredSize(new Dimension(350, 25));
				
				accept.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						if(registerPassBox.getPassword().equals(registerPassBoxCheck.getPassword())) {
							register(
									registerNickBox.getText(),
									registerEmailBox.getText(),
									registerPassBox.getPassword());
						}
						registerWindow.setVisible(false);
						registerWindow.dispose();
						mainWindow.setVisible(true);
						guiSounds.prepareSound("startup");
						guiSounds.start();
					}
				});
				signin.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						registerWindow.dispose();
						registerWindow.setVisible(false);
						buildLoginWindows();
						loggingWindow.setVisible(true);
					}
				});
				
				JLabel logo = new JLabel(new ImageIcon("./src/media/logo.png"));
				logo.setPreferredSize(new Dimension(400, 200));
				registerWindow.add(logo);
				registerWindow.setLayout(new FlowLayout());
				registerWindow.add(r_l);
				registerWindow.add(registerEmailBox);				
				registerWindow.add(r_p);
				registerWindow.add(registerPassBox);
				registerWindow.add(r_pCheck);
				registerWindow.add(registerPassBoxCheck);				
				registerWindow.add(accept);
				registerWindow.add(signin);
				registerWindow.pack();
				registerWindow.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				registerWindow.setVisible(true);
			}
		});
		//frame.add(new JLabel(new ImageIcon("Path/To/Your/Image.png")));
		
		JLabel logo = new JLabel(new ImageIcon("./src/media/logo.png"));
		logo.setPreferredSize(new Dimension(400, 200));
		loggingWindow.add(logo);
		
		loggingWindow.add(l);
		loggingWindow.add(loginBox);
		loggingWindow.add(p);
		loggingWindow.add(passwordBox);
		
		loggingWindow.add(login);
		loggingWindow.add(register);
		
		loggingWindow.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
	}
	
	protected JComponent makeTextPanel(String text) {
	    JPanel panel = new JPanel(false);
	    //JLabel filler = new JLabel(text);
	    //filler.setHorizontalAlignment(JLabel.CENTER);
	    panel.setLayout(new GridLayout(1, 1));
	    //panel.add(filler);
	    return panel;
	}
	private JComponent makeContactsTable() {
		JPanel panel = new JPanel(false);
		String[] columnNames = {"First Name",
	            "Last Name",
	            "Sport",
	            "# of Years",
	            "Vegetarian"};
		Object[][] data = {
			    {"Kathy", "Smith",
			     "Snowboarding", new Integer(5), new Boolean(false)},
			    {"John", "Doe",
			     "Rowing", new Integer(3), new Boolean(true)},
			    {"Sue", "Black",
			     "Knitting", new Integer(2), new Boolean(false)},
			    {"Jane", "White",
			     "Speed reading", new Integer(20), new Boolean(true)},
			    {"Joe", "Brown",
			     "Pool", new Integer(10), new Boolean(false)}
			};
		JTable table = new JTable(data, columnNames);
		panel.add(table);
		return panel;	
	}
	
	public void register(String nick, String email, char[] password) {
		
	}
	public void login(String nick, char[] password) {
		
	}
}
