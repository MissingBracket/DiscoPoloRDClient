package communication.hardware;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class SpeakersHandler extends Thread{
	//	Get system audio in
	private SourceDataLine speakers=null;
	//	Specify sound encoding
	private AudioFormat soundFormat;
	//	Buffering variables
	private int bufferSize = 512;
	private byte[] buffer;
	//	Sound playback variables	
	private boolean shouldPlay=true;
	private boolean hasSound=true;
	
	public SpeakersHandler() {
		this.buffer = new byte[bufferSize];
		setup();
	}
	
	public void writeBuffer(byte[] soundBytes) {
		this.buffer = soundBytes;
		this.hasSound=true;
	}
	
	public void run() {
		System.out.println("Running speakers");
		if(shouldPlay) {System.out.println("Ready to go");}
		while(shouldPlay) {			
			if(this.hasSound) {
				speakers.write(this.buffer, 0, bufferSize);
				//this.hasSound=false;
				}
		}
		speakers.drain();
		speakers.stop();
		speakers.close();		
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
