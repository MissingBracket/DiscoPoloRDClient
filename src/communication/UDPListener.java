package communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;

import communication.hardware.SpeakersHandler;

public class UDPListener extends Thread{
	
	private DatagramSocket listeningSocket;
	private int bufferSize = 512; // default buffer size
	
	byte inBuffer[];
	//	Buffer for sound packets
	List<byte[]> packetBuffer;
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
		speakers = new SpeakersHandler();
		packetBuffer = new LinkedList<>();
	}
	
	public void run() {
		speakers.start();
		DatagramPacket incomingData = new DatagramPacket(inBuffer, bufferSize);
		try {
			listeningSocket = new DatagramSocket(port);
			listeningSocket.setSoTimeout(3000);
			
		} catch (SocketException e1) {
			logError("Failed to initialise Listening Socket");
			e1.printStackTrace();
			isConnected=false;
		}
		while(isConnected) {
			try {
				incomingData = new DatagramPacket(inBuffer, bufferSize);
				listeningSocket.receive(incomingData);
				
				packetBuffer.add(incomingData.getData());
				
				if(!packetBuffer.isEmpty()) 
					speakers.readSound(packetBuffer.remove(0));					
				
				
				logDebug("Received: data");
			} catch (IOException e) {
				logError("No data was received due to: " + e.getMessage());
			}
		}
		listeningSocket.close();
	}
	
	public void logError(String a) {
		System.out.println("[L_ERR]@"+addr+":"+port+"\n:>"+a);
	}
	public void logDebug(String a) {
		System.out.println("[L_DBG]@"+addr+":"+port+"\n:>"+a);
	}
}
