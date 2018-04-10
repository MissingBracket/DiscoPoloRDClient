package discopolord;

import java.io.IOException;

import communication.hardware.MicHandler;
import communication.hardware.SpeakersHandler;

public class Main {	
	
	public static void main(String[] args) {
		String addr = "127.0.0.1";//"150.254.145.182";
		int port = 6969; // TO be read from file
		
			try {
				new ClientLogic(addr, port).start();
			} catch (IOException e) {
				System.out.println("Could not connect to server with given:\n[ADDR@"+addr+"]\n[PORT@"+port+"]\n");
				new MicHandler().start();
				new SpeakersHandler().start();
				
			}
	}
	

}