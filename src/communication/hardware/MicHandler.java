package communication.hardware;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import misc.Log;

public class MicHandler extends Thread{
	//	Get system audio in
	private TargetDataLine systemMicrophone;
	//	Specify sound encoding
	private AudioFormat soundFormat;
	//	Buffering variables
	private int bufferSize = 512;
	private byte[] buffer;
	private boolean lineInUse=true;	
	
	private boolean bufferUsed = false;
	
	public MicHandler() {
		buffer = new byte[512];
		setup();
		if(lineInUse)System.out.println("Mic running");
	}
	public synchronized void disengage() {
		Log.info("Closing microphone line");
		this.lineInUse=false;
		notify();
		systemMicrophone.stop();
		systemMicrophone.close();
	}
	
	public void failsafe() {
		this.lineInUse=false;
	}
	
	private void setup()  {
		soundFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 8000.00f, 16, 2, 4, 8000.0f, true);
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, soundFormat);
		try {
		systemMicrophone = (TargetDataLine)AudioSystem.getLine(info);
		if(systemMicrophone.isOpen())
			systemMicrophone.close();
		systemMicrophone.open();
		systemMicrophone.start();
		}catch(LineUnavailableException exc) {
			System.out.println("Failed to load system microphone line");
			exc.printStackTrace();
			lineInUse=false;
		}
	}
	public synchronized byte[] getbuffer() {
		notify();
		while(bufferUsed) {
			try {
				wait();
			}catch(InterruptedException e) {
				System.out.println("That's enough");
			}
		}
		bufferUsed = true;
		return this.buffer;
	}
	public synchronized void getMicrophoneStream() {
		while(!bufferUsed) {
			try {
				wait();
			} catch (InterruptedException e) {
				System.out.println("Can we stop those try / catch please?");
			}
		}			
		systemMicrophone.read(buffer, 0, bufferSize);
		bufferUsed = false;
		notify();
	}
	
	public void run() {		
		if(!lineInUse) {
			System.out.println("Can't begin recording");
		}else
		while(lineInUse) {
			getMicrophoneStream();
		}
		/*systemMicrophone.stop();
		systemMicrophone.close();*/
		System.out.println("Mic disengaged");
	}
}
