package gui;

import java.util.HashMap;
import java.util.Map;

import gui.media.Sound;

public class SoundHandler {
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
	
	public void stopPlaying(String s) {
		sounds.get(s).interrupt();
		Sound replace = new Sound(sounds.get(s).getSoundName());
		sounds.remove(s);
		sounds.put(s, replace);
	}
	public void playSound(String s) {
		sounds.get(s).start();
		
		/*try {
			soundToPlay.play();
			Log.info("finished playing?");
			
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
			Log.failure("Could not play sound due to " + e.getLocalizedMessage());
		}*/
		}
	
}
