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
								.setEmail(DegbugConstants.email)
								.setPassword(DegbugConstants.pass))
						.build().writeTo(outgoingStream);
				response = Succ.Message.parseFrom(socket.getInputStream());
				if(!response.getMessageType().equals(MessageType.AUTH)) {
					throw new IOException("User not authorised");
				}
				//	REQUEST CONTACTS LIST
				Succ.Message.newBuilder()
					.setMessageType(MessageType.C_REQ)
					.build().writeTo(outgoingStream);
				
				response = Succ.Message.parseFrom(socket.getInputStream());
				List<Succ.Message.UserAddress> contacts = null;
				
				if(response.getMessageType().equals(MessageType.C_LIST))
					contacts = response.getAddressesList();
				else throw new IOException("Failed to get contacts list");
				
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
		public static String email = "rowan@kinson.com";
		public static String pass = "sup3rS3cr3tPassword";
		
	}
	
}
