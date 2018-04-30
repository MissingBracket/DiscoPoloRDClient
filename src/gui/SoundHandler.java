package gui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import gui.media.Sound;
import misc.Log;

public class SoundHandler extends Thread{
	//	Sound playback related variables
	Map<String, Sound> sounds;
	Sound soundToPlay;
	
	public SoundHandler(){
		sounds = new HashMap<String, Sound>();
	}
	
	public void registerSound(String soundName, String sound) {
		sounds.put(soundName, new Sound(sound));
	}
	
	public void prepareSound(String s) {
		soundToPlay = sounds.get(s);
	}
	
	public String getCurrentSound() {
		return soundToPlay.getSoundName();
	}
	
	public void run() {
		try {
			soundToPlay.play();
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
			Log.failure("Could not play sound due to " + e.getLocalizedMessage());
		}
	}
	
}
