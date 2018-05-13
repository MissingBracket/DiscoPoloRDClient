package discopolord;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import javax.management.RuntimeErrorException;

import misc.Log;
import protocol.Succ;
import protocol.Succ.Message.LoginData;
import protocol.Succ.Message.MessageType;

public class ClientLogic extends Thread{
	//	Network
	private Socket socket;
	
	private String addr;
	private int port;
	//	TCP Streams for Server connection
	private DataOutputStream outgoingStream;
	private BufferedReader ingoingStream;
	
	
	//	Constructor
	public ClientLogic(String addr, int port) throws UnknownHostException, IOException {
		this.socket=new Socket(addr, port);
		this.addr=addr;
		this.port=port;
	}
	//	Main thread logic
	public void run() {
		Succ.Message response;
		int streamStatus= 1;initialiseStreams();
		Log.success("Running Client");
		if(streamStatus > 0) {
			//	Connecting to server - implementation of SUCC connection
			try {
				//	DIFFIE-HELLMAN KEY NEGOTIATION
				
				//	LOGIN
				Log.info("Sending Login request");
				Succ.Message.newBuilder()
				.setMessageType(MessageType.LOGIN)
						.setLoginData(LoginData.newBuilder()
								.setEmail(DegbugConstants.testmail)
								.setPassword(DegbugConstants.testpass))
						.build().writeDelimitedTo(outgoingStream);
				
				Log.info("Login req sent");
				
				response = Succ.Message.parseDelimitedFrom(socket.getInputStream());
				
				if(response != null && !response.getMessageType().equals(MessageType.AUTH)) {
					throw new IOException("User not authorised");
				}
				//	REQUEST CONTACTS LIST
				Log.info("Sending cList req");
				Succ.Message.newBuilder()
					.setMessageType(MessageType.C_REQ)
					.build().writeDelimitedTo(outgoingStream);
				
				response = Succ.Message.parseDelimitedFrom(socket.getInputStream());
				List<Succ.Message.UserAddress> contacts = null;
				//Succ.Message d = Succ.Message.parseDelimitedFrom(socket.getInputStream());
				
				
				if(response != null && response.getMessageType().equals(MessageType.C_LIST))
					contacts = response.getAddressesList();
				else throw new IOException("Failed to get contacts list");
				if(!contacts.isEmpty()) {
					for(Succ.Message.UserAddress a : contacts) {
						Log.info(a.getIp());
					}
				}
				while(true){
					//	Connection Keeping
				}
				//Log.success("Completed one session");
			} catch (IOException e) {
				Log.failure("Could not reach Server: " + e.getMessage());
			}			
		}
		else
			Log.failure("Could not initialise communication streams");
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
