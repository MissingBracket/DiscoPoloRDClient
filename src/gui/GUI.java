package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;

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

import com.sun.javafx.scene.control.ControlAcceleratorSupport;

import discopolord.ClientLogic;
import misc.Log;
import protocol.Succ;

public class GUI {
	private boolean isLogged = false;
	private static boolean isCalling = false;
	private static boolean isInCall = false;
	private JFrame loggingWindow;
	private JFrame registerWindow;
	private JFrame mainWindow;
	private static JFrame callingNotifier;
	private static JTextField info;
	private JTabbedPane tabbedPane;
	private double version;
	private static ClientLogic logic;
	private static SoundHandler guiSounds;

	private static JTable contactsTable;
	private static String incAddr;
	private static int incPort;
	public static boolean incomingCall;
	//	Should be found by system
	private static int freePort = 10000;
	public GUI(double d, ClientLogic cl) {
		version = d;
		this.logic = cl;
		buildLoginWindows();
		//SetupMainWindow();
	    

		guiSounds = new SoundHandler();
		guiSounds.registerSound("startup", "startup.wav");		
		guiSounds.registerSound("dialing", "thomas.wav");		
		if(!isLogged)
			loggingWindow.setVisible(true);
		else
			mainWindow.setVisible(true);
		
	}
	
	public static void callAccepted(int port, String addr, String who) {
		JFrame callFrame = new JFrame("Speaking with " + who);
		if(!isInCall)
		{	
			callFrame.setSize(450, 700);
			callFrame.getContentPane().setBackground(Color.DARK_GRAY);
			callFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			callFrame.setLayout(new FlowLayout());
			JLabel logo = new JLabel(new ImageIcon("./src/media/logo.png"));
			logo.setPreferredSize(new Dimension(400, 200));
			callFrame.add(logo);
			callFrame.setVisible(true);			
			
			isInCall=true;
		}
		else {
			callFrame.setVisible(true);
			isInCall=false;
		}
	}
	public static void initialiseContacts(List<Succ.Message.UserStatus> users) {
		Log.info("Creating contacts");
		String [] columns = {"Uzytkownik", "ID", "Status"};
		Object [][] data = new Object[users.size()][3];
	
		for(int i =0; i < users.size(); i++) {
			Log.info("Adding user: " + users.get(i).getUsername());
			data[i][0] = users.get(i).getUsername();
			data[i][1] = users.get(i).getIdentifier();
			data[i][2] = users.get(i).getStatus();
		}
		Log.info("Prepared contacts table");
		contactsTable = new JTable(data, columns);
	}
	public static void incomingCallEventHandler(boolean pickedUp) {
		if(pickedUp) {
			if(isCalling) {
				info.setText("Connected! Remember to listen");
			}
		}else {
			callingNotifier.dispose();
			callingNotifier.setVisible(false);
			isCalling=pickedUp;
			guiSounds.stopPlaying("dialing");
		}
	}
	
	public static void receivingCall(int port, String addr, String who) {
		JFrame acceptCall = new JFrame("Incoming call from " + who);
		acceptCall.setSize(450, 450);
		acceptCall.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		acceptCall.setLayout(new FlowLayout());
		JLabel logo = new JLabel(new ImageIcon("./src/media/logo.png"));
		logo.setPreferredSize(new Dimension(400, 200));
		acceptCall.add(logo);		
		JButton acc = new JButton("Pick up");
		JButton den = new JButton("Pick up");
		
		acc.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				logic.beginConversation(port, addr);
				
			}
		});
		
		den.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				logic.denyCall();
				acceptCall.dispose();
				acceptCall.setVisible(false);
			}
		});
		
		acceptCall.add(acc);
		acceptCall.add(den);
		incomingCall = true;
		acceptCall.setVisible(true);
		incAddr=addr;
		incPort=port;
	}
	
	public void SetupMainWindow() {

		mainWindow = new JFrame("DiscoPoloRD v: " + version);
		mainWindow.setSize(450, 700);
		mainWindow.getContentPane().setBackground(Color.DARK_GRAY);
		mainWindow.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		mainWindow.setLayout(new FlowLayout());
		JLabel logo = new JLabel(new ImageIcon("./src/media/logo.png"));
		logo.setPreferredSize(new Dimension(400, 200));
		mainWindow.add(logo);		
		
		mainWindow.add(getCallButton());
		
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
		
		panel4.setLayout(new GridLayout(2, 1));
		panel4.add(new JLabel(new ImageIcon("./src/media/polbud.png")));
		panel4.add(new TextArea("Projekt Komunikatora G�osowego\nWykonanie:\n\tDariusz Krajewski - klient\n\tKrystian Minta - server"));
		tabbedPane.addTab("O projekcie", null, panel4,
		                      "Does nothing at all");
		tabbedPane.setMnemonicAt(3, KeyEvent.VK_4);
		mainWindow.add(tabbedPane);
	}
	
	public void showCallingDialog() {
		JFrame callDialog = new JFrame("Incoming Call");
		callDialog.setPreferredSize(new Dimension(400, 400));
		callDialog.setLayout(new FlowLayout());		
		JButton acc = new JButton("Pick up");		
		
		acc.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				logic.beginConversation(incPort, incAddr);
			}
		});
		
		callDialog.add(acc);
		callDialog.setVisible(true);
	}
	
	public JButton getCallButton() {
		 JButton callButton = new JButton("Call");
		 callButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {				
				if(!isCalling) {
				Log.info("Begin calling: " + 
						contactsTable.getValueAt(
								contactsTable.getSelectedRow(),
								contactsTable.getSelectedColumn()).toString());
				guiSounds.prepareSound("dialing");
				Log.info(guiSounds.getCurrentSound());
				callingNotifier = new JFrame("Dialing: " + contactsTable.getValueAt(
						contactsTable.getSelectedRow(),
						1).toString());
				callingNotifier.setLayout(new FlowLayout());
				
				callingNotifier.setPreferredSize(new Dimension(400, 300));
				info = new JTextField("Come on, pickup the phone!");
				info.setEditable(false);
				JLabel logo = new JLabel(new ImageIcon("./src/media/logo.png"));
				//logo.setPreferredSize(new Dimension(400, 300));
				callingNotifier.add(logo);
				callingNotifier.add(info);
				
				callingNotifier.pack();
				
				callingNotifier.setVisible(true);
				guiSounds.playSound("dialing");
				isCalling = true;
				logic.connectTo(contactsTable.getValueAt(
						contactsTable.getSelectedRow(),
						1).toString(), 
						freePort);
				}else {
					guiSounds.stopPlaying("dialing");
					Log.info("Dialing terminated");
					isCalling = false;
					callingNotifier.dispose();
					callingNotifier.setVisible(false);
				}
			}
		});
		 
		 return callButton;
	 }
	
	
	public void buildLoginWindows() {
		this.loggingWindow = new JFrame("Sign in to DiscoPoloRD");
		loggingWindow.setSize(400, 400);
		loggingWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
				isLogged = logic.connectToServer(loginBox.getText(), new String(passwordBox.getPassword()));
				Log.success("Logged is: " + isLogged);
				loggingWindow.setVisible(false);
				loggingWindow.dispose();
				if(!isLogged) {
					buildLoginWindows();
					return;
				}
				SetupMainWindow();
				mainWindow.setVisible(true);
				guiSounds.prepareSound("startup");
				guiSounds.playSound("startup");
				logic.start();
			}
		});
		
		
		JButton register = new JButton("Register");
		register.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				loggingWindow.dispose();
				loggingWindow.setVisible(false);
				registerWindow = new JFrame("Sign up with DiscoPoloRD");
				registerWindow.setPreferredSize(new Dimension(400, 550));
				JButton accept = new JButton("Register"),
						signin = new JButton("Login");
				JTextField registerNickBox = new JTextField(),
							registerEmailBox = new JTextField(),
							registerIDBox = new JTextField();
				
				JPasswordField registerPassBox = new JPasswordField();
				JPasswordField registerPassBoxCheck = new JPasswordField();
				JLabel 	r_l =  new JLabel("E-mail:"),
						r_n = new JLabel("Nick:"),
						r_id = new JLabel("Unique ID:"),
						r_p = new JLabel("Password:"),
						r_pCheck = new JLabel("Repeat Password:");
				registerNickBox.setPreferredSize(new Dimension(350, 25));
				registerIDBox.setPreferredSize(new Dimension(350, 25));
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
						guiSounds.playSound("startup");
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
				registerWindow.add(r_n);
				registerWindow.add(registerNickBox);
				registerWindow.add(r_id);
				registerWindow.add(registerIDBox);
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
	public static JComponent makeContactsTable() {
		JPanel panel = new JPanel(false);
		/*String[] columnNames = {"First Name",
	            "Last Name",
	            "Sport",
	            "# of Years",
	            "Vegetarian"};
		Object[][] data = {
			    {"Krystian", "Minta",
			     "SooPerGo5ciu", new Integer(5), new Boolean(false)},
			    {"Mrystian", "Kinta",
			     "GramWLolaIksD", new Integer(3), new Boolean(true)},
			    {"Trystan", "Kwinta",
			     "FemaleCatSlayer", new Integer(2), new Boolean(false)},
			    {"Wyspa", "Clinta",
			     "BenTenInches", new Integer(20), new Boolean(true)},
			    {"Browar", "Pinta",
			     "BrowarTowar", new Integer(10), new Boolean(false)}
			};
		contactsTable = new JTable(data, columnNames);*/
		panel.add(contactsTable);
		return panel;
	}
	
	public void register(String nick, String email, char[] password) {
		
	}
	public void login(String nick, char[] password) {

	}
}
