package discopolord;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import misc.ClientSocketType;

public class InterClientComm extends Thread{
	//	Communication Socket
	private Socket rtpSocket;
	private ClientSocketType socketType;
	//	Network activity indicator - loop condition
	private boolean socketActive = true;
	//	Network Streams
	private DataOutputStream outgoingStream = null;
	private BufferedReader ingoingStream = null; 
	
	public InterClientComm(Socket socket, ClientSocketType socketType){
		this.rtpSocket=socket;
		this.socketType=socketType;
	}
	
	public void run(){
		switch(socketType){
		case LISTENING:
			//	Receiving End Logic 
			String receivedPacket = "";
			try{
			ingoingStream = new BufferedReader(
					new InputStreamReader(
							rtpSocket.getInputStream()));
			//	Main listening loop
			while(socketActive){
				System.out.println("listening");
				receivedPacket = receivePacket();
				System.out.println("received: " + receivedPacket);
			
			}
			}catch(IOException ex){
				System.out.println("Failed to initialise listening stream");
			}
			break;
		case TRANSMITTING:
			//	Transmitting End Logic
			try{
			outgoingStream = new DataOutputStream(
					rtpSocket.getOutputStream());
			// Main transmitting loop
			while(socketActive){
				System.out.println("Sending");
				sendPacket(new String("Hello neighbour!"));
				
			}
			}catch(IOException ex){
				System.out.println("Failed to initialise transmitting stream");
			}
			break;
		}
	}
	public void constructPacket(){
		
	}
	//	Send packet through socket
	public void sendPacket(String packet) throws IOException {
		outgoingStream.writeBytes(packet);
	}
	//	Attempt to receive incoming transmission
	public String receivePacket() throws IOException {
		return ingoingStream.readLine();
	}
	
}
