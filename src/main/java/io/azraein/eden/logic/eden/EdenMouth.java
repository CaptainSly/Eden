package io.azraein.eden.logic.eden;

import java.io.IOException;
import java.util.List;

import io.github.jonelo.jAdapterForNativeTTS.engines.SpeechEngine;
import io.github.jonelo.jAdapterForNativeTTS.engines.SpeechEngineNative;
import io.github.jonelo.jAdapterForNativeTTS.engines.Voice;
import io.github.jonelo.jAdapterForNativeTTS.engines.VoicePreferences;
import io.github.jonelo.jAdapterForNativeTTS.engines.VoicePreferences.Gender;
import io.github.jonelo.jAdapterForNativeTTS.engines.exceptions.SpeechEngineCreationException;

public class EdenMouth {

	private SpeechEngine speechEngine;

	public EdenMouth() {
		try {
			speechEngine = SpeechEngineNative.getInstance();
			List<Voice> voices = speechEngine.getAvailableVoices();
			VoicePreferences voicePreferences = new VoicePreferences();
			voicePreferences.setLanguage("en");
			voicePreferences.setCountry("us");
			voicePreferences.setGender(Gender.MALE);
			Voice voice = speechEngine.findVoiceByPreferences(voicePreferences);

			if (voice == null) {
				voice = voices.get(0);
			}

			speechEngine.setVoice(voice.getName());
			speechEngine.setRate(10);

		} catch (SpeechEngineCreationException e) {
			e.printStackTrace();
		}

	}

	public void stopTalking() {
		speechEngine.stopTalking();
	}

	public void speak(String text) {
		try {
			speechEngine.say(text);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
