package communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;

import communication.hardware.SpeakersHandler;
import misc.Log;

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
		Log.info("New Speaker Handler");
		this.addr = addr;
		this.port = port;
		inBuffer = new byte[bufferSize];
		speakers = new SpeakersHandler();
		packetBuffer = new LinkedList<>();
	}
	public void close() {
		this.isConnected=false;
	}
	public void run() {
		speakers.start();
		DatagramPacket incomingData = new DatagramPacket(inBuffer, bufferSize);
		try {
			listeningSocket = new DatagramSocket(port);
			listeningSocket.setSoTimeout(3000);
			
		} catch (SocketException e1) {
			Log.failure("Failed to initialise Listening Socket");
			e1.printStackTrace();
			isConnected=false;
		}
		Log.info("Listening from " + port);
		while(isConnected) {
			try {
				incomingData = new DatagramPacket(inBuffer, bufferSize);
				listeningSocket.receive(incomingData);				
				packetBuffer.add(incomingData.getData());				
				if(!packetBuffer.isEmpty()) 
					speakers.readSound(packetBuffer.remove(0));
				Log.info("Received: data");
			} catch (IOException e) {
				Log.failure("No data was received due to: " + e.getMessage());
				if(!packetBuffer.isEmpty()) {
					Log.failure("Playing from Buffer");
					speakers.readSound(packetBuffer.remove(0));
				}					
			}
		}
		
		speakers.disengage();
		listeningSocket.close();
	}
}
