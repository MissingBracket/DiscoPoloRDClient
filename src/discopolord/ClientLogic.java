package discopolord;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.omg.PortableInterceptor.SUCCESSFUL;

import com.google.protobuf.ByteString;

import communication.UDPListener;
import communication.UDPTransmitter;
import discopolord.security.DHClient;
import gui.GUI;
import misc.Log;
import protocol.Succ;
import protocol.Succ.Message.LoginData;
import protocol.Succ.Message.MessageType;
import protocol.Succ.Message.Status;

public class ClientLogic extends Thread{
	//	Network
	private Socket socket;
	private int freePort = 10000;
	private String addr;
	private int port;
	//	TCP Streams for Server connection
	private DataOutputStream outgoingStream;
	private BufferedReader ingoingStream;
	//	Security / encryption related variables
	private Cipher aesCipher,aesDeCipher;
	private String clientSecret;
	
	private AlgorithmParameters servIV;
	private UDPListener listener;
	private UDPTransmitter transmitter;
	private boolean connectionSecured = false;
	//	Constructor
	public ClientLogic(String addr, int port) throws UnknownHostException, IOException, NoSuchAlgorithmException, NoSuchPaddingException {
		this.socket=new Socket(addr, port);
		this.addr=addr;
		this.port=port;
		aesCipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
		aesDeCipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
	}
	//	Main thread logic
	public void connectTo(String ID, int port) {
		Log.info("Begin calling: " + ID);
		sendMessage(Succ.Message.newBuilder()
				.setMessageType(MessageType.CL_INV)
				.addAddresses(Succ.Message.UserAddress.newBuilder()
						.setPort(port)
						.setUserIdentifier(ID)
						.build())
				.build());
	}
	public void denyCall() {
		sendMessage(Succ.Message.newBuilder()
				.setMessageType(MessageType.CL_DEN)
				.build());
				
	}
	public void registerUser(String nick, String id, String email, String password) {
		//logic.registerUser(nick, email, ID, new String(password));
		Log.info("Registration form sending");
		sendMessage(Succ.Message.newBuilder()
				.setMessageType(MessageType.REGISTER)
				.setRegistrationData(Succ.Message.RegistrationData.newBuilder()
						.setUsername(nick)
						.setEmail(email)
						.setIdentifier(id)
						.setPassword(password)
						.build())
				.build());
		Succ.Message response = getMessage();
		if(response.getMessageType() == MessageType.REGISTRATION_SUCC) {
			GUI.GenericDialogStatus(true, "No i super! Witaj w DPRD!");
		}else {
			GUI.GenericDialogStatus(false, "E-mail lub ID zajête :(");
		}
	}
	
	public void run() {
		while(true) {
			Succ.Message response = getMessage();
			switch(response.getMessageType()) {
			case CL_INV:
				GUI.receivingCall(
						response.getAddressesList().get(0).getPort(), 
						response.getAddressesList().get(0).getIp(),
						response.getAddressesList().get(0).getUserIdentifier());
				break;
			case CL_DEN:
				Log.info("Failed shite");
				
				GUI.incomingCallEventHandler(false, 0, "null");
				break;				
			case ADR:
				Log.info("Beginning call");
				GUI.incomingCallEventHandler(true, 
						response.getAddressesList().get(0).getPort(),
						response.getAddressesList().get(0).getIp());
				break;
			case DISC:
				//	Someone else wants to disconnect so false
				// 	endConversation(false);
				Log.info("Received disconnect signal");
				GUI.disconnectedWith();
				break;
			case C_UPD:
				Log.info("Received status update prompt");
				if(response.getUsersList().isEmpty())
					GUI.GenericDialogStatus(false, "Nie mo¿na dodaæ u¿ytkownika");
				else {
					GUI.GenericDialogStatus(true, "Teraz mo¿ecie porozmawiaæ");
					GUI.updateContactStatus(
							response.getUsersList().get(0).getUsername(),
							response.getUsersList().get(0).getIdentifier(), 
							response.getUsersList().get(0).getStatus().toString());
				}
					
				break;
			
			default:
				break;
			}
		}
	}
	public void endConvo() {
		sendMessage(Succ.Message.newBuilder().setMessageType(MessageType.DISC).build());
	}
	public void beginConversation(int port, String addr) {
		
		Log.info("Picked Up: " + addr +  " / " + port);
		listener = 	new UDPListener(addr, GUI.freePort);
		transmitter = new UDPTransmitter(addr, port);
		
		listener.start();
		transmitter.start();
	}
	
	public void searchContact(String contact) {
		sendMessage(Succ.Message.newBuilder()
				.setMessageType(MessageType.C_UPD)
				.addUsers(Succ.Message.UserStatus.newBuilder()
						.setIdentifier(contact)
						.setStatus(Status.FRIEND)
						.build())
				.build());
		Log.info("Would search " + contact);
	}
	
	public void acceptConversation(int port) {
		sendMessage(Succ.Message.newBuilder()
				.setMessageType(MessageType.CL_ACC)
				.addAddresses(Succ.Message.UserAddress.newBuilder().setPort(port)
						.build()).build());
		
		
	}
	//	boolean describes WHO wants to disconnect.
	//	This user or someone else
	public void endConversation(boolean userHangedUp) {
		Log.info("Exiting listener and transmitter");
		if(userHangedUp)
			sendMessage(Succ.Message.newBuilder().setMessageType(MessageType.DISC).build());
		Log.info("Closing listener and transmitter");
		if(!(listener == null || transmitter == null)) {
			listener.close();
			transmitter.close();	
		}
		
	}
	
	public void removeContact(String id) {
		sendMessage(Succ.Message.newBuilder().setMessageType(MessageType.C_UPD)
				.addUsers(Succ.Message.UserStatus.newBuilder()
						.setIdentifier(id)
						.setStatus(Status.DELETED)
						.build())
				.build());
	}
	
	public boolean connectToServer(String email, String password) {
		Succ.Message response;
		int streamStatus= 1;
		initialiseStreams();
		Log.success("Running Client");
		if(streamStatus > 0) {
			//	Connecting to server - implementation of SUCC connection
			try {
				//	DIFFIE-HELLMAN KEY NEGOTIATION
				if(email == null || password == null)
				{
					if(!connectionSecured) {
					response  = Succ.Message.parseDelimitedFrom(socket.getInputStream());
					
					DHClient dhClient = new DHClient();
					byte [] publicKeys = dhClient.getPublicKey(response.getDH().toByteArray());
					
					
					clientSecret = dhClient.getSecret();
					Log.info("Received key: " + clientSecret);
					
					initialiseEncrypter();
					
							Succ.Message.newBuilder()
							.setMessageType(MessageType.DHN)
							.setDH(ByteString.copyFrom(publicKeys))
							.setEPS(ByteString.copyFrom(aesCipher.getParameters().getEncoded()))
							.build().writeDelimitedTo(outgoingStream);	
						
					
					response = Succ.Message.parseDelimitedFrom(socket.getInputStream());
	
					servIV = AlgorithmParameters.getInstance("AES");
					
					if(response.getMessageType().equals(MessageType.EP)) {
						Log.success("Received IV: " + response.getEPS().toByteArray().length);
						servIV.init(response.getEPS().toByteArray());
					}
					initialiseDecrypter();
					connectionSecured = true;
					}
					return true;
				}
				//	LOGIN
				Log.info("Sending Login request");
				
				sendEncrypted(Succ.Message.newBuilder()
				.setMessageType(MessageType.LOGIN)
						.setLoginData(LoginData.newBuilder()
								.setEmail(email)//DegbugConstants.testmail
								.setPassword(password))//DegbugConstants.testpass
						.build());
				
						
						//.writeDelimitedTo(outgoingStream);
				
				Log.info("Login req sent");
				
				response = getMessage(); 
						
						//Succ.Message.parseDelimitedFrom(socket.getInputStream());
				
				if(response != null && !response.getMessageType().equals(MessageType.AUTH)) {
					throw new IOException("User not authorised");
				}
				//	REQUEST CONTACTS LIST
				Log.info("Sending cList req");
				sendMessage(Succ.Message.newBuilder()
						.setMessageType(MessageType.C_REQ)
						.build());
				
					
					//.writeDelimitedTo(outgoingStream);
				Log.info("Sent request for contacts");
				response = getMessage(); 
						
						//Succ.Message.parseDelimitedFrom(socket.getInputStream());
				List<Succ.Message.UserStatus> contacts = null;
				//Succ.Message d = Succ.Message.parseDelimitedFrom(socket.getInputStream());
				
				
				if(response != null && response.getMessageType().equals(MessageType.C_LIST)) {
					Log.info("User has contacts");
					//contacts = response.getUsersList();
					GUI.initialiseContacts(response.getUsersList());
					//GUI.makeContactsTable();
				}					
				else throw new IOException("Failed to get contacts list");
				/*if(!contacts.isEmpty()) {
					for(Succ.Message.UserStatus a : contacts) {
						Log.info("Received Contact: ");
						Log.info(a.getIdentifier());
					}
				}*/
				return true;
				//Log.success("Completed one session");
			} catch (Exception e) {
				Log.failure("Could not reach Server: " + e.getMessage() + " / " + e.getClass() + " / " + e.getLocalizedMessage());
				e.printStackTrace();
				return false;
			}			
		}
		else
			Log.failure("Could not initialise communication streams");
		return false;
	}
	
	public byte[] getEncrypted(byte[] toEncrypt) throws IllegalBlockSizeException, BadPaddingException {
		return aesCipher.doFinal(toEncrypt);	
	}
	
	public byte[] getDecrypted(byte[] input, int size) throws IllegalBlockSizeException, BadPaddingException {
		return aesDeCipher.doFinal(input, 0, size);
	}
	
	public void sendEncrypted(Succ.Message sm) throws IllegalBlockSizeException, BadPaddingException, IOException {
		outgoingStream.write(getEncrypted(sm.toByteArray()));
	}
	public Succ.Message getMessage(){
		//initialiseDecrypter(serverIV);
		Log.info("Receiving");
		byte[] buffer = new byte[256];
		try {
			int received  = socket.getInputStream().read(buffer);			
			Succ.Message message = Succ.Message.parseFrom(getDecrypted(buffer, received));
			return message;
		} catch (IOException | IllegalBlockSizeException | BadPaddingException e) {
			Log.failure("Could not read from incoming stream " + e.getMessage());
			return null;
		}
	}
	public void sendMessage(Succ.Message mess) {
		//initialiseEncrypter();
		byte[] toSend;
		try {
			toSend = getEncrypted(mess.toByteArray());
			socket.getOutputStream().write(toSend);
		} catch (IllegalBlockSizeException | BadPaddingException | IOException e) {
			Log.failure("Could not send encrypted message");
		}
		
	}
	
	public void initialiseEncrypter() {
		try {
			aesCipher.init(
					Cipher.ENCRYPT_MODE, 
					new SecretKeySpec(
							clientSecret.getBytes("UTF-8"),0,16,"AES"));
			Log.success("Encrypter ok");
		} catch (InvalidKeyException | UnsupportedEncodingException e) {
			Log.failure("Leave me be, exceptions");
		}
	}
	
	public void initialiseDecrypter() {
		try {
			aesDeCipher.init(
					Cipher.DECRYPT_MODE, 
					new SecretKeySpec(
							clientSecret.getBytes("UTF-8"),0,16,"AES"),
					servIV);
		} catch (InvalidKeyException | UnsupportedEncodingException e) {
			Log.failure("Leave me be, exceptions");
		} catch (InvalidAlgorithmParameterException e) {
			Log.failure("This is becoming ridiculous "+e.getMessage());
		}
	}
	
	//	Send packet through socket
	public void sendPacket(String packet) throws IOException {
		outgoingStream.writeBytes(packet);
	}
	//	Attempt to receive incoming transmission
	public String receivePacket() throws IOException {
		return ingoingStream.readLine();
		//return "Success";
	}
	//	Unnecessarily verbose stream initialisation
	public int initialiseStreams() {
		
		try {
			outgoingStream = new DataOutputStream(
					socket.getOutputStream());
			ingoingStream = new BufferedReader(
					new InputStreamReader(
							socket.getInputStream()));
			
		} catch (IOException e) {
			Log.failure("Failed to initialise outgoing and ingoing stream");
			return -1;
		}
		return 1;
	}
	public static class DegbugConstants{
		public static String testmail = "krowa@krowa.pl";
		public static String testpass = "krowa";
		
		public static String email = "rowan@kinson.com";
		public static String pass = "sup3rS3cr3tPassword";
		
	}
	
}
