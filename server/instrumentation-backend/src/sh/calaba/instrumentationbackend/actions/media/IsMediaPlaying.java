package sh.calaba.instrumentationbackend.actions.media;

import sh.calaba.instrumentationbackend.InstrumentationBackend;
import sh.calaba.instrumentationbackend.Result;
import sh.calaba.instrumentationbackend.actions.Action;
import sh.calaba.instrumentationbackend.actions.webview.CalabashChromeClient.WebFuture;

import android.media.MediaPlayer;

public class IsMediaPlaying implements Action {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Result execute(String... args) {
		MediaPlayer player = new MediaPlayer();
		System.out.println("player.isPlaying() " + player.isPlaying() );

		return new Result(true, String.valueOf(player.isPlaying()));
	}

	@Override
	public String key() {
		return "is_media_playing";
	}
}
