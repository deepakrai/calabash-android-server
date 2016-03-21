package sh.calaba.instrumentationbackend.actions.system;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;


import java.net.InetAddress;
import sh.calaba.instrumentationbackend.InstrumentationBackend;
import sh.calaba.instrumentationbackend.Result;
import sh.calaba.instrumentationbackend.actions.Action;

public class ConnectivityCheck implements Action {

	@Override
	public Result execute(String... args) {
		ConnectivityManager connectivityManager = (ConnectivityManager) InstrumentationBackend.solo.getCurrentActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

		try {
            InetAddress ipAddr = InetAddress.getByName("google.com");

            System.out.println("ConnectivityCheck ipAddr " + ipAddr);
            System.out.println("ConnectivityCheck networkInfo " + (networkInfo == null));
            if (!ipAddr.equals("") && networkInfo != null) {
            	System.out.println("ConnectivityCheck ConnectivityManager InetAddress true " );
                return new Result(true, "true");
            } else {
            	System.out.println("ConnectivityCheck ConnectivityManager InetAddress false " );
                return new Result(true, "false");
            }

        } catch (Exception e) {
        	System.out.println("ConnectivityCheck ConnectivityManager InetAddress Exception " );
            return new Result(true, "false");
        }
   }

	@Override
	public String key() {
		return "is_connected";
	}
}