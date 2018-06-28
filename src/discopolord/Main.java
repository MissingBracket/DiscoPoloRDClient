package discopolord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.crypto.NoSuchPaddingException;

import gui.GUI;
import misc.Log;

public class Main {	
	
	public static String getServerAddress() {
		List<String> contents;
		try {
			contents = Files.readAllLines(Paths.get("./serverAddress.txt"));
			return contents.get(0);
		} catch (IOException e) {
			Log.failure("Could not read server address: " + e.getMessage());;
			return "localhost";
		}
		
	}
	
	public static void main(String[] args) {
		String addr = getServerAddress();
				//"192.168.43.248";
				//"127.0.0.1";
				//"150.254.145.182";
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