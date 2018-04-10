package discopolord;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import communication.UDPListener;
import communication.UDPTransmitter;

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
		
		int streamStatus= 1;initialiseStreams();
		logSuccess("Running Client");
		if(streamStatus > 0) {
			try {
				logSuccess("received " + receivePacket());
				//	Server SUCC connection
				logInfo("Starting client logic threads");
				//	Temporary - for testing purposes
				new UDPListener(addr,port).start();
				new UDPTransmitter(addr, port).start();
				
			} catch (IOException e) {
				logError("Could not reach Server");
			}			
		}
		else
			logError("Could not initialise communication streams");	
	}
	
	// Logs given message preceded with Error either Success tag or Debug information
	public static void logError(String a) {
		System.out.println("[ERR] " + a);
	}
	public static void logSuccess(String a) {
		System.out.println("[SUCC] " + a);
	}
	public static void logInfo(String a) {
		System.out.println("[INFO] " + a);
	}
	//	Send packet through socket
	public void sendPacket(String packet) throws IOException {
		outgoingStream.writeBytes(packet);
	}
	//	Attempt to receive incoming transmission
	public String receivePacket() throws IOException {
		//return ingoingStream.readLine();
		return "Success";
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
			logError("Failed to initialise outgoing and ingoing stream");
			return -1;
		}
		return 1;
	}
	
	
}
