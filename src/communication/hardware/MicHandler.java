package communication.hardware;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class MicHandler extends Thread{
	//	Get system audio in
	private TargetDataLine systemMicrophone;
	//	Specify sound encoding
	private AudioFormat soundFormat;
	//	Buffering variables
	private int bufferSize = 512;
	private byte[] buffer;
	private boolean lineInUse=true;	
	
	public MicHandler() {
		buffer = new byte[512];
		setup();
		if(lineInUse)System.out.println("Mic running");
	}
	
	public void failsafe() {
		this.lineInUse=false;
	}
	
	private void setup()  {
		soundFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 8000.00f, 16, 2, 4, 8000.0f, true);
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, soundFormat);
		try {
		systemMicrophone = (TargetDataLine)AudioSystem.getLine(info);
		
		systemMicrophone.open();
		systemMicrophone.start();
		}catch(LineUnavailableException exc) {
			System.out.println("Failed to load system microphone line");
			lineInUse=false;
		}
	}
	public byte[] getbuffer() {
		return this.buffer;
	}
	
	public void run() {
		
		if(!lineInUse) {
			System.out.println("Can't begin recording");
		}else
		while(lineInUse) {
			systemMicrophone.read(buffer, 0, bufferSize);
		}
		systemMicrophone.stop();
		systemMicrophone.close();
		System.out.println("Mic disengaged");
	}
}
