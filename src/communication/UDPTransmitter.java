package communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import communication.hardware.MicHandler;

public class UDPTransmitter extends Thread{
	
	private DatagramSocket transmittingSocket;
	private int bufferSize = 1024; // default buffer size
	
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
				transmittingSocket.send(new DatagramPacket(outBuffer, outBuffer.length, 
						InetAddress.getByName(addr), port));
			} catch (IOException e) {
				logError("Failed to send data");
				isConnected=false;
			}finally {
				transmittingSocket.close();	
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
