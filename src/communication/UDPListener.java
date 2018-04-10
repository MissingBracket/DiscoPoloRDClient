package communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import communication.hardware.SpeakersHandler;

public class UDPListener extends Thread{
	
	private DatagramSocket listeningSocket;
	private int bufferSize = 1024; // default buffer size
	
	byte inBuffer[];
	//	Iteration controller
	private boolean isConnected = true;
	//	Connection information
	private int port;
	private String addr;
	
	private SpeakersHandler speakers;
	
	public UDPListener(String addr, int port) {
		this.addr = addr;
		this.port = port;
		inBuffer = new byte[bufferSize];
	}
	
	public void run() {
		
		DatagramPacket incomingData = new DatagramPacket(inBuffer, bufferSize);
		String data = "";
		try {
			listeningSocket = new DatagramSocket(port);
		} catch (SocketException e1) {
			logError("Failed to initialise Listening Socket");
			e1.printStackTrace();
			isConnected=false;
		}
		while(isConnected) {
			try {
				listeningSocket.receive(incomingData);
				data = new String(incomingData.getData());
				logDebug("Received:"+data);
			} catch (IOException e) {
				logError("No data was received");
				isConnected=false;
			}finally {
				listeningSocket.close();		
			}
		}
	}
	
	public void logError(String a) {
		System.out.println("[ERR]@"+addr+":"+port+"\n:>"+a);
	}
	public void logDebug(String a) {
		System.out.println("[DBG]@"+addr+":"+port+"\n:>"+a);
	}
}
