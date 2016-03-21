package sh.calaba.instrumentationbackend.actions.media;

import sh.calaba.instrumentationbackend.InstrumentationBackend;
import sh.calaba.instrumentationbackend.Result;
import sh.calaba.instrumentationbackend.actions.Action;
import sh.calaba.instrumentationbackend.actions.webview.CalabashChromeClient.WebFuture;

import android.content.Context;
import android.media.AudioManager;

public class IsMusicActive implements Action {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Result execute(String... args) {
		AudioManager myAudioManager;		
		myAudioManager = (AudioManager) InstrumentationBackend.solo.getCurrentActivity().getSystemService(Context.AUDIO_SERVICE);

		return new Result(true, String.valueOf(myAudioManager.isMusicActive()));
	}

	@Override
	public String key() {
		return "is_music_active";
	}
}
