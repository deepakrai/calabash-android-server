package sh.calaba.instrumentationbackend.actions.location;


import android.os.Build;
import sh.calaba.instrumentationbackend.InstrumentationBackend;
import sh.calaba.instrumentationbackend.Result;
import sh.calaba.instrumentationbackend.actions.Action;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.provider.Settings;
import sh.calaba.org.codehaus.jackson.map.util.Provider;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class FakeGPSLocation implements Action {
	
	static LocationProviderThread t;

    @Override
    public Result execute(String... args) {
        final double latitude = Double.parseDouble(args[0]);
        final double longitude = Double.parseDouble(args[1]);

        Context context = InstrumentationBackend.instrumentation.getTargetContext();

        try {
            if (Build.VERSION.SDK_INT <= 22) {
                if (Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION) != 1) {
                    return Result.failedResult("Allow mock location is not enabled.");
                }
            }
        } catch (Settings.SettingNotFoundException e) {
            return Result.failedResult(e.getMessage());
        }

        if (Build.VERSION.SDK_INT <= 22) {
            if (context.checkCallingOrSelfPermission(Manifest.permission.ACCESS_MOCK_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return Result.failedResult("The application does not have access mock location permission. Add the permission '" + Manifest.permission.ACCESS_MOCK_LOCATION + "' to your manifest");
            }
        }

        if (t != null) {
        	t.finish();
        }
    	
    	t = new LocationProviderThread(latitude, longitude);
    	
    	t.start();
    	
        return Result.successResult();
    }
    
   
    private class LocationProviderThread extends Thread {
    	private final double latitude;
		private final double longitude;
		
		boolean finish = false;

		LocationProviderThread(double latitude, double longitude) {
			this.latitude = latitude;
			this.longitude = longitude;
    	}
    	
    	@Override
		public void run() {
    		LocationManager locationManager = (LocationManager) InstrumentationBackend.instrumentation.getTargetContext().getSystemService(Context.LOCATION_SERVICE);

            final List<String> providerNames = locationManager.getProviders(true);
            List<String> activeProviderNames = new ArrayList<String>();

            for (String providerName : providerNames) {
                try {
                    locationManager.addTestProvider(providerName, false, false, false, false, false, false, false, Criteria.POWER_LOW, Criteria.ACCURACY_FINE);
                    locationManager.setTestProviderEnabled(providerName, true);
                    activeProviderNames.add(providerName);
                } catch (IllegalArgumentException e) {
                    // Ignored
                }
            }

    		while(!finish) {
				System.out.println("Mocking location to: (" + latitude + ", " + longitude + ")");

                for (String providerName : activeProviderNames) {
                    if (locationManager.getProvider(providerName) != null) {
                        System.out.println("Active provider: " + providerName);
                        setLocation(locationManager, providerName, latitude, longitude);
                    }
                }

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}
    	
    	private void setLocation(LocationManager locationManager, String locationProvider, double latitude, double longitude) {

    		Location location = new Location(locationProvider);
    		location.setLatitude(latitude);
    		location.setLongitude(longitude);
    		location.setAccuracy(1);
    		location.setTime(System.currentTimeMillis());

            try {
                Method makeComplete = Location.class.getMethod("makeComplete");
                if (makeComplete != null) {
                    makeComplete.invoke(location);
                }
            } catch (Exception e) {
                //Method only available in Jelly Bean
            }

            locationManager.setTestProviderLocation(locationProvider, location);
    	}

    	public void finish() {
    		finish = true;
    	}
    }

    @Override
    public String key() {
        return "set_gps_coordinates";
    }

    /**
     * Adds new LocationTestProvider matching actual provider on device.
     * 
     * @param currentProvider
     * @param providerType
     */
    private void addTestProvider(LocationProvider currentProvider, String providerType) {
    LocationManager locationManager = (LocationManager) InstrumentationBackend.instrumentation.getTargetContext().getSystemService(Context.LOCATION_SERVICE);
    
    locationManager.addTestProvider(providerType, 
                    currentProvider.requiresNetwork(), 
                    currentProvider.requiresSatellite(), 
                    currentProvider.requiresCell(), 
                    currentProvider.hasMonetaryCost(), 
                    currentProvider.supportsAltitude(), 
                    currentProvider.supportsSpeed(), 
                    currentProvider.supportsBearing(), 
                    currentProvider.getPowerRequirement(), 
                    currentProvider.getAccuracy());
    }


}
