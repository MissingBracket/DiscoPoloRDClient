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
import java.util.regex.Pattern;

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
	private static Object [][] data;
	private static JTable contactsTable;
	private static JComponent contactsPanel;
	private static JTextField loggedUserData;
	private static String incAddr;
	private static int incPort;
	public static boolean incomingCall;
	private static String currentUser, currentID;
	
	//	Should be found by system
	public static int freePort = 10000;
	
	
	public GUI(double d, ClientLogic cl) {
		version = d;
		this.logic = cl;
		logic.connectToServer(null, null);
		buildLoginWindows();
		
		guiSounds = new SoundHandler();
		guiSounds.registerSound("startup", "startup.wav");		
		guiSounds.registerSound("dialing", "thomas.wav");		
		
		if(!isLogged)
			loggingWindow.setVisible(true);
		else
			mainWindow.setVisible(true);
		
	}
	
	public static void callAccepted(int port, String addr, String who) {
		JFrame callFrame = new JFrame("Rozmawiasz z " + who);
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
	//	Called when other side hangs up
	public static void disconnectedWith() {
		//	Someone else disconnected so false
		logic.endConversation(false);
		callingNotifier.dispose();
		//if(!isInCall)
			guiSounds.stopPlaying("dialing");
		isCalling=false;
	}
	//	Called when hanging up
	public static void wantsToDisconnect() {
		//	This user wants to disconnect
		logic.endConversation(true);
	}
	public static boolean isContact(String id) {
		for(Object [] line : data) {
			if(((String)(line[1])).equals(id))
				return true;
		}
		return false;
	}
	public static void setLoggedUserData(String user, String id) {
		Log.info("Setting hello message");
		currentUser = user;
		currentID = id;
		loggedUserData = new JTextField("Witaj " + currentUser  + " @ " + currentID);
		
	}
	public static void addContactToTable(String name, String id, String status) {
		Object [][] temp;
		if(data.length == 0) {
			Log.info("Adding new 0 user: " + name + " " + id + " " + status);
			temp = new Object[1][3];
			temp[0][0] = name;
			temp[0][1] = id;
			temp[0][2] = status;
		}
		else {
			temp  = new Object[data.length + 1][3];
			for(int i =0; i < data.length; i++) {
				Log.info("Rewriting user: " + data[i][0]);
				temp[i][0] = data[i][0];
				temp[i][1] = data[i][1];
				temp[i][2] = data[i][2];
			}
			Log.info("Adding new user: " + name + " " + id + " " + status);
			temp[data.length][0] = name; 
			temp[data.length][1] = id;
			temp[data.length][2] = status;
					
		}
		
		data = temp;
		contactsPanel.remove(contactsTable);
		contactsTable = new JTable(data, new String[] {"Uzytkownik", "ID", "Status"});
		contactsPanel.add(contactsTable);
		//contactsTable.invalidate();
		contactsTable.repaint();
		contactsPanel.repaint();
		//contactsTable.revalidate();
		//contactsTable.
		//contactsTable.setVisible(true);
	}
	
	public static void initialiseContacts(List<Succ.Message.UserStatus> users) {
		Log.info("Creating contacts");
		String [] columns = {"Uzytkownik", "ID", "Status"};
		data = new Object[users.size()][3];
	
		for(int i =0; i < users.size(); i++) {
			Log.info("Adding user: " + users.get(i).getUsername());
			data[i][0] = users.get(i).getUsername();
			data[i][1] = users.get(i).getIdentifier();
			data[i][2] = users.get(i).getStatus();
		}
		Log.info("Prepared contacts table");
		contactsTable = new JTable(data, columns);
	}
	public static void incomingCallEventHandler(boolean pickedUp, int port, String ip) {
		if(pickedUp) {
			isInCall=true;
			if(isCalling) {
				info.setText("Po³¹czono! Nadawaj!");
				guiSounds.stopPlaying("dialing");
			}
			else {
				guiSounds.stopPlaying("dialing");
			}
			logic.beginConversation(port, ip);
		
		}else {
			callingNotifier.dispose();
			callingNotifier.setVisible(false);
			isCalling=pickedUp;
			guiSounds.stopPlaying("dialing");
		}
	}
	
	public static void receivingCall(int port, String addr, String who) {
		callingNotifier = new JFrame("Dzwoni do Ciebie " + who);
		callingNotifier.setSize(400, 300);
		callingNotifier.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		callingNotifier.setLayout(new FlowLayout());
		JLabel logo = new JLabel(new ImageIcon("./src/media/logo.png"));
		logo.setPreferredSize(new Dimension(400, 200));
		callingNotifier.add(logo);		
		JButton acc = new JButton("Odbierz");
		JButton den = new JButton("Odrzuæ");
		guiSounds.prepareSound("dialing");
		guiSounds.playSound("dialing");
		acc.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				logic.acceptConversation(freePort);
				den.setText("Zakoñcz");				
			}
		});
		
		den.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!isInCall) {
					Log.info("Would reject");
					logic.denyCall();
					guiSounds.stopPlaying("dialing");
				}				
				else {
					Log.info("Hanging up");
					logic.endConversation(true);
				}
				callingNotifier.dispose();
				callingNotifier.setVisible(false);
			}
		});
		
		callingNotifier.add(acc);
		callingNotifier.add(den);
		incomingCall = true;
		callingNotifier.setVisible(true);
		incAddr=addr;
		incPort=port;
	}
	
	public void SetupMainWindow(String username) {

		mainWindow = new JFrame("DiscoPoloRD v: " + username);
		mainWindow.setSize(450, 700);
		mainWindow.getContentPane().setBackground(Color.DARK_GRAY);
		mainWindow.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		mainWindow.setLayout(new FlowLayout());
		JLabel logo = new JLabel(new ImageIcon("./src/media/logo.png"));
		logo.setPreferredSize(new Dimension(400, 200));
		mainWindow.add(logo);	
		//loggedUserData = new JTextField();
		loggedUserData.setEditable(false);
		loggedUserData.setPreferredSize(new Dimension(200, 20));
		mainWindow.add(loggedUserData);
		mainWindow.add(getCallButton());
		
		//mainWindow.add(new JButton("Shite"));
		
		tabbedPane = new JTabbedPane();
		contactsPanel = makeContactsTable();
		
		tabbedPane.addTab("Kontakty", null, contactsPanel,
		                  "Przegl¹daj kontakty");
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

		JComponent panel2 = makeSearchWindow(); 
				//makeTextPanel("Panel #2");
		tabbedPane.addTab("Dodaj", null, panel2,
		                  "Dodaj kontakt");
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

		JComponent panel3 = makeTextPanel("Panel #3");
		tabbedPane.addTab("Ustawienia", null, panel3,
		                  "NIE_ZAIMPLEMENTOWANO");
		tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);

		JComponent panel4 = makeTextPanel(
		        "asdasdasdasdasdsa");
		
		panel4.setLayout(new GridLayout(2, 1));
		panel4.add(new JLabel(new ImageIcon("./src/media/polbud.png")));
		panel4.add(new TextArea("Projekt Komunikatora G³osowego\nWykonanie:\n\tDariusz Krajewski - klient\n\tKrystian Minta - server"));
		tabbedPane.addTab("O projekcie", null, panel4,
		                      "Informacje o programie");
		tabbedPane.setMnemonicAt(3, KeyEvent.VK_4);
		mainWindow.add(tabbedPane);
	}
	
	public void showCallingDialog() {
		JFrame callDialog = new JFrame("Ktoœ dzwoni");
		callDialog.setPreferredSize(new Dimension(400, 400));
		callDialog.setLayout(new FlowLayout());		
		JButton acc = new JButton("Odbierz");		
		
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
		 JButton callButton = new JButton("Zadzwoñ");
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
				callingNotifier = new JFrame("Dzwoniê do: " + contactsTable.getValueAt(
						contactsTable.getSelectedRow(),
						1).toString());
				callingNotifier.setLayout(new FlowLayout());
				
				callingNotifier.setPreferredSize(new Dimension(400, 300));
				info = new JTextField("No ju¿, Odbieraj!");
				info.setEditable(false);
				JLabel logo = new JLabel(new ImageIcon("./src/media/logo.png"));
				//logo.setPreferredSize(new Dimension(400, 300));
				callingNotifier.add(logo);
				callingNotifier.add(info);
				//	BEGIN
				JButton hangUp = new JButton("Zakoñcz");
					hangUp.addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							guiSounds.stopPlaying("dialing");
							logic.endConversation(true);
							callingNotifier.dispose();
							callingNotifier.setVisible(false);
						}
					});
				
				callingNotifier.add(hangUp);
				//	END
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
					logic.endConversation(true);
					callingNotifier.setVisible(false);
				}
			}
		});
		 
		 return callButton;
	 }
	
	
	public void buildLoginWindows() {
		this.loggingWindow = new JFrame("Zaloguj siê do DiscoPoloRD");
		loggingWindow.setSize(400, 400);
		loggingWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JTextField loginBox = new JTextField();
		JPasswordField passwordBox = new JPasswordField();
		JLabel 	l =  new JLabel("E-mail:"), 
				p = new JLabel("Has³o:");
		loginBox.setPreferredSize(new Dimension(350, 25));
		passwordBox.setPreferredSize(new Dimension(350, 25));
		
		JButton login = new JButton("Loguj");
		loggingWindow.setLayout(new FlowLayout());
		
		login.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				isLogged = logic.connectToServer(loginBox.getText(), new String(passwordBox.getPassword()));
				Log.success("Logged is: " + isLogged);
				
				if(!isLogged) {					
					GenericDialogStatus(false, "Logowanie nie uda³o siê");			
				}
				else {
					loggingWindow.setVisible(false);
					loggingWindow.dispose();
					SetupMainWindow(loginBox.getText());
					mainWindow.setVisible(true);
					guiSounds.prepareSound("startup");
					guiSounds.playSound("startup");
					logic.start();	
					mainWindow.setName("DiscoPoloRD: "+loginBox.getText());
					mainWindow.repaint();
				}
				
			}
		});
		
		JButton register = new JButton("Rejestruj");
		register.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				loggingWindow.dispose();
				loggingWindow.setVisible(false);
				registerWindow = new JFrame("Zapisz siê do DiscoPoloRD");
				registerWindow.setPreferredSize(new Dimension(400, 550));
				JButton accept = new JButton("Rejestruj"),
						signin = new JButton("Loguj");
				JTextField registerNickBox = new JTextField(),
							registerEmailBox = new JTextField(),
							registerIDBox = new JTextField();
				
				JPasswordField registerPassBox = new JPasswordField();
				JPasswordField registerPassBoxCheck = new JPasswordField();
				JLabel 	r_l =  new JLabel("E-mail:"),
						r_n = new JLabel("Pseudonim:"),
						r_id = new JLabel("Unikatowy ID:"),
						r_p = new JLabel("Has³o:"),
						r_pCheck = new JLabel("Powtórz Has³o:");
				registerNickBox.setPreferredSize(new Dimension(350, 25));
				registerIDBox.setPreferredSize(new Dimension(350, 25));
				registerEmailBox.setPreferredSize(new Dimension(350, 25));
				registerPassBox.setPreferredSize(new Dimension(350, 25));
				registerPassBoxCheck.setPreferredSize(new Dimension(350, 25));
				
				accept.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						
						Pattern VALID_EMAIL_ADDRESS_REGEX = 
							    Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
						if(new String(registerPassBox.getPassword()).equals(
							new String(registerPassBoxCheck.getPassword())) &&
								new String(registerPassBox.getPassword()).length() > 7 &&
								registerNickBox.getText().length() > 5 &&
								registerIDBox.getText().length() > 5 &&
								VALID_EMAIL_ADDRESS_REGEX.matcher(registerEmailBox.getText()).find() == true) {
							
							Log.success("Registration data is in order");
							Log.info("Sending request to register");
							register(
									registerNickBox.getText(),
									registerIDBox.getText(),
									registerEmailBox.getText(),
									registerPassBox.getPassword());
						}
						else
							GenericDialogStatus(false, "Sprawdz dane rejestracji");
						/*registerWindow.setVisible(false);
						registerWindow.dispose();
						mainWindow.setVisible(true);
						guiSounds.prepareSound("startup");
						guiSounds.playSound("startup");*/
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
	public JComponent makeSearchWindow() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		panel.add(new JLabel("Identyfikator"));
		JTextField searchbox = new JTextField();
		searchbox.setPreferredSize(new Dimension(150, 25));
		//loginBox.setPreferredSize(new Dimension(350, 25));
		panel.add(searchbox);
		JButton remove = new JButton("Zerwij");
		remove.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				removeContact(searchbox.getText());
				
			}
		});
		
		
		JButton search = new JButton("Poczuj przyjazn!");
		search.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Log.info("Searching for: " + searchbox.getText());
				logic.searchContact(searchbox.getText());
			}
		});
		
		panel.add(search);
		panel.add(remove);
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
	
	public static void GenericDialogStatus(boolean status, String statusMessage) {
		JFrame dialogWindow = new JFrame("Status rejestracji");
		dialogWindow.setLayout(new FlowLayout());
		dialogWindow.setPreferredSize(new Dimension(400, 300));
	
		JLabel logo = new JLabel(new ImageIcon("./src/media/logo.png"));
		
		//logo.setPreferredSize(new Dimension(400, 300));
		dialogWindow.add(logo);
		JTextField message = new JTextField();
		JButton ok = new JButton();
		message.setEditable(false);
		if(status) {
			message.setText(statusMessage);
			ok.setText("ok :)");
		}
		else {
			message.setText(statusMessage);
			ok.setText("ok :(");
		}
		
		dialogWindow.add(message);
		ok.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dialogWindow.dispose();
				dialogWindow.setVisible(false);
			}
		});
		dialogWindow.add(ok);
		dialogWindow.pack();
		dialogWindow.setVisible(true);
		
	}
	
	public void register(String nick, String ID, String email, char[] password) {
		logic.registerUser(nick, ID, email, new String(password));
	}
	
	public static void removeContact(String id) {
		if(!isContact(id))
			return;
		logic.removeContact(id);
		Object [][]temp = new Object[data.length -1][3];
		int currentRow = 0;
		for(Object[] line : data) {
			if(!((String)line[1]).equals(id)) {
				temp[currentRow][0] = line[0];
				temp[currentRow][1] = line[1];
				temp[currentRow][2] = line[2];
				currentRow++;
			}
		}
		contactsPanel.remove(contactsTable);
		contactsTable = new JTable(data, new String[] {"Uzytkownik", "ID", "Status"});
		contactsPanel.add(contactsTable);
		contactsTable.repaint();
		contactsPanel.repaint();
	}
	
	public static void updateContactStatus(String name, String id, String status) {
		if(isContact(id)) {
			Log.info("Updating " + id + "with " + status);
			for(Object [] line : data) {
				if(((String)(line[1])).equals(id)) {
					line[2] = status;
				}
			}
			contactsTable.repaint();	
		}else {
			addContactToTable(name, id, status);
			contactsTable.repaint();
			contactsTable.invalidate();
			contactsTable.revalidate();
			contactsPanel.revalidate();
			contactsPanel.repaint();
			
		}		
	}
}