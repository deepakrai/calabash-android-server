package sh.calaba.instrumentationbackend.actions.media;

import sh.calaba.instrumentationbackend.InstrumentationBackend;
import sh.calaba.instrumentationbackend.Result;
import sh.calaba.instrumentationbackend.actions.Action;
import sh.calaba.instrumentationbackend.actions.webview.CalabashChromeClient.WebFuture;

import android.app.ActivityManager;
import android.content.Context;
import java.util.ArrayList;
import java.util.List;

public class Dummy implements Action {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Result execute(String... args) {
		ActivityManager am = (ActivityManager) InstrumentationBackend.solo.getCurrentActivity().getSystemService(Context.ACTIVITY_SERVICE);

		List<ActivityManager.RunningServiceInfo> rs = am.getRunningServices(500);

		for (int i=0; i<rs.size(); i++) {
			ActivityManager.RunningServiceInfo rsi = rs.get(i);
			System.out.println("Service ----> Process " + rsi.process + " with component " + rsi.service.getClassName() + " with clientPackage " + rsi.clientPackage +
					" started " + rsi.started );
		}
		
		List<ActivityManager.RunningTaskInfo> rt = am.getRunningTasks(500);

		for (int i=0; i<rt.size(); i++) {
			ActivityManager.RunningTaskInfo rti = rt.get(i);
			System.out.println("Task ----> baseActivity " + rti.baseActivity + " with description " + rti.description +
					" numActivities " + rti.numActivities +
					" numRunning " + rti.numRunning +
					" topActivity " + rti.topActivity +
					" topActivity getClassName " + rti.topActivity.getClassName() +
					" topActivity getPackageName" + rti.topActivity.getPackageName() +
					" topActivity getShortClassName " + rti.topActivity.getShortClassName());
		}

		return new Result();
	}

	@Override
	public String key() {
		return "dummy";
	}
}
