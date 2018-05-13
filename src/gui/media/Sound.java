package gui.media;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Sound extends Thread{
	private String soundPath;
	private final Path wavPath;
	    
	private final CyclicBarrier barrier = new CyclicBarrier(2);
	
	public Sound(String sound) {
		this.soundPath = sound;
		wavPath = Paths.get("./src/media/"+soundPath);
	}
	public String getSoundName() {
		return soundPath;
	}
	
	public void run() {
		try {
			this.play();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void play() throws LineUnavailableException, IOException, UnsupportedAudioFileException {

        try (final AudioInputStream audioIn = AudioSystem.getAudioInputStream(wavPath.toFile());
             final Clip clip = AudioSystem.getClip()) {
            listenForEndOf(clip);
            clip.open(audioIn);
            clip.start();
            waitForSoundEnd();
            clip.stop();
        }
    }

    private void listenForEndOf(final Clip clip) {

        clip.addLineListener(event -> {
            if (event.getType() == LineEvent.Type.STOP) waitOnBarrier();
        });
    }

    private void waitOnBarrier() {

        try {

            barrier.await();
        } catch (final InterruptedException ignored) {
        } catch (final BrokenBarrierException e) {

            throw new RuntimeException(e);
        }
    }

    private void waitForSoundEnd() {

        waitOnBarrier();
    }
}
