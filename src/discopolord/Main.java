package discopolord;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;

import gui.GUI;
import misc.Log;

public class Main {	
	
	public static void main(String[] args) {
		String addr = /*"127.0.0.1";*/"150.254.145.182";
		int port = 1337; // TO be read from file
		
			try {
				ClientLogic CL = new ClientLogic(addr, port);
				new GUI(0.045, CL);	
				/*
				 * GUI related stuff
				 * */
			} catch (IOException e) {
				//	Display in popup window
				System.out.println("Could not connect to server with given:\n[ADDR@"+addr+"]\n[PORT@"+port+"]\n");
					
				/*	
				new UDPListener(addr, port).start();
				new UDPTransmitter(addr, port).start();*/
				
				/*DiffieHellman dh = new DiffieHellman();
				dh.generateKeys();	
				SoundHandler handler = new SoundHandler();
				handler.registerSound("startup", "thomas.wav");
				handler.prepareSound("startup");
				handler.start();*/
				Log.info("Out of program");
			} catch (NoSuchAlgorithmException e) {
				Log.failure("ExceptionNotThrownException thrown");
			} catch (NoSuchPaddingException e) {
				Log.failure("That's proven");
			}
	}
	

}