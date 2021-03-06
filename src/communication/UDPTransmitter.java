package communication;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import communication.hardware.MicHandler;

public class UDPTransmitter extends Thread{
	
	private DatagramSocket transmittingSocket;
	private int bufferSize = 512; // default buffer size
	
	byte outBuffer[];
	//	Iteration controller
	private boolean isConnected = true;
	//	Connection information
	private int port;
	private String addr;
	
	private MicHandler microphoneHandler;
	
	public UDPTransmitter(String addr, int port) {
		this.addr = addr;
		this.port = port;
		outBuffer = new byte[bufferSize];
		microphoneHandler = new MicHandler();
		microphoneHandler.start();
	}
	
	public void run() {
		try {
			
			transmittingSocket = new DatagramSocket();
		} catch (SocketException e) {
			logError("Failed to initialiste Transmitting Datagram Socket");
			isConnected=false;
		}
		outBuffer = "hello!".getBytes();
			while(isConnected) {			
				try {				
					outBuffer = microphoneHandler.getbuffer();
					transmittingSocket.send(new DatagramPacket(outBuffer, bufferSize, 
							InetAddress.getByName(addr), port));
					//logDebug("Sent!: " + new String(outBuffer));
				} catch (Exception e) {
					logError("Failed to send data due to: " + e.getMessage());
					isConnected=false;
				}/*finally {
					transmittingSocket.close();	
				}*/
		}
		
	}	
	
	public void logError(String a) {
		System.out.println("[T_ERR]@"+addr+":"+port+"\n:>"+a);
	}
	public void logDebug(String a) {
		System.out.println("[T_DBG]@"+addr+":"+port+"\n:>"+a);
	}
}
