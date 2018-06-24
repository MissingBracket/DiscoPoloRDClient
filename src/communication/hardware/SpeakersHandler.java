package communication.hardware;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import misc.Log;

public class SpeakersHandler extends Thread{
	//	Get system audio in
	private SourceDataLine speakers=null;
	//	Specify sound encoding
	private AudioFormat soundFormat;
	//	Buffering variables
	private int bufferSize = 512;
	private byte[] buffer;
	//	Sound playback variables	
	private boolean shouldPlay = true;
	private boolean bufferPlayed = true;
	
	public SpeakersHandler() {
		this.buffer = new byte[bufferSize];
		setup();
	}
	
	public void writeBuffer(byte[] soundBytes) {
		this.buffer = soundBytes;
		this.bufferPlayed=true;
	}
	public void disengage() {
		Log.info("Stopping speakers line");
		this.shouldPlay=false;
	}
	public synchronized void playSound() {
		while(bufferPlayed) {
			try {
				wait();
			}catch(InterruptedException exc ) {
				
			}
		}
		speakers.write(this.buffer, 0, buffer.length);
		bufferPlayed=true;
		notify();
	}
	public synchronized void readSound(byte[] sound) {
		notify();
		while(!bufferPlayed) {
			try {
				wait();
			}catch(Exception ex) {
				
			}
		}
		System.out.println("writing: " + this.buffer);
		this.buffer=sound;
		bufferPlayed=false;
	}
	
	public void run() {
		System.out.println("Running speakers");
		if(shouldPlay) {System.out.println("Ready to go");}
		while(shouldPlay) {
				playSound();
		}
		speakers.drain();
		speakers.stop();
		speakers.close();
		Log.info("Speakers disengaged");
	}
	
	private void setup()  {
		soundFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 8000.00f, 16, 2, 4, 8000.0f, true);
		DataLine.Info speakerInfo = new DataLine.Info(SourceDataLine.class, soundFormat);
		try {
			speakers = (SourceDataLine)AudioSystem.getLine(speakerInfo);
			speakers.open();
			speakers.start();
		}catch(LineUnavailableException exc) {
			System.out.println("Failed to load system speakers line");
			shouldPlay=false;			
		}
		System.out.println("Exiting speaker setup");
	}
}
