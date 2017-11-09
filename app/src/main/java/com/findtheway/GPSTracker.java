package com.findtheway;

<<<<<<< HEAD
=======
import java.io.IOException;
import java.util.List;
import java.util.Locale;

>>>>>>> 86532df8cd6847cbc3475ede6a019b648550db4d
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
<<<<<<< HEAD
=======
import android.location.Address;
import android.location.Geocoder;
>>>>>>> 86532df8cd6847cbc3475ede6a019b648550db4d
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
<<<<<<< HEAD


/**
 * Created by Apostolis on 10/27/2016.
 */

=======
import android.util.Log;

/**
 * Create this Class from tutorial :
 * http://www.androidhive.info/2012/07/android-gps-location-manager-tutorial
 *
 * For Geocoder read this : http://stackoverflow.com/questions/472313/android-reverse-geocoding-getfromlocation
 *
 */

public class GPSTracker extends Service implements LocationListener
{
    private final Context mContext;

    //flag for GPS Status
    boolean isGPSEnabled = false;

    //flag for network status
    boolean isNetworkEnabled = false;
>>>>>>> 86532df8cd6847cbc3475ede6a019b648550db4d

public class GPSTracker  extends Service implements LocationListener {

    private final Context context;

    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;

    Location location;

    double latitude;
    double longitude;

<<<<<<< HEAD
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 100;
=======
    //The minimum distance to change updates in metters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; //10 metters

    //The minimum time beetwen updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
>>>>>>> 86532df8cd6847cbc3475ede6a019b648550db4d

    //Declaring a Location Manager
    protected LocationManager locationManager;

<<<<<<< HEAD
    public GPSTracker(Context context){
        this.context = context;
        getLocation();
    }

    public Location getLocation(){
        try {
            locationManager = (LocationManager)context.getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER);


            if(!isGPSEnabled && !isGPSEnabled) {

            } else {
                this.canGetLocation = true;

                if(isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);


                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                        }
                    }
                }
                if(isGPSEnabled) {
                    if(location == null) {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if(locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if(location != null){
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();

                            }
=======
    public GPSTracker(Context context)
    {
        this.mContext = context;
        getLocation();
    }

    public Location getLocation()
    {
        try
        {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            //getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            //getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled)
            {
                // no network provider is enabled
            }
            else
            {
                this.canGetLocation = true;

                //First get location from Network Provider
                if (isNetworkEnabled)
                {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    Log.d("Network", "Network");

                    if (locationManager != null)
                    {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        updateGPSCoordinates();
                    }
                }

                //if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled)
                {
                    if (location == null)
                    {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        Log.d("GPS Enabled", "GPS Enabled");

                        if (locationManager != null)
                        {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            updateGPSCoordinates();
>>>>>>> 86532df8cd6847cbc3475ede6a019b648550db4d
                        }

                    }

                }
            }
<<<<<<< HEAD
        } catch (Exception e) {
            e.printStackTrace();
=======
        }
        catch (Exception e)
        {
            //e.printStackTrace();
            Log.e("Error : Location", "Impossible to connect to LocationManager", e);
        }
>>>>>>> 86532df8cd6847cbc3475ede6a019b648550db4d

        }
        return location;
    }

<<<<<<< HEAD
    public void stopUsingGPS(){
        if(locationManager != null) {
=======
    public void updateGPSCoordinates()
    {
        if (location != null)
        {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     */

    public void stopUsingGPS()
    {
        if (locationManager != null)
        {
>>>>>>> 86532df8cd6847cbc3475ede6a019b648550db4d
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

<<<<<<< HEAD
    public double getLatitude() {
        if(location != null) {
=======
    /**
     * Function to get latitude
     */
    public double getLatitude()
    {
        if (location != null)
        {
>>>>>>> 86532df8cd6847cbc3475ede6a019b648550db4d
            latitude = location.getLatitude();
        }

        return latitude;
    }

<<<<<<< HEAD
    public double getLongitude(){
        if(location != null) {
=======
    /**
     * Function to get longitude
     */
    public double getLongitude()
    {
        if (location != null)
        {
>>>>>>> 86532df8cd6847cbc3475ede6a019b648550db4d
            longitude = location.getLongitude();
        }

        return longitude;
    }

<<<<<<< HEAD
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.setTitle("GPS SETTINGS");

        alertDialog.setMessage("GPS is not enabled.Go to Settings menu and enable it?");
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        }) ;
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        getLatitude();
        getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

=======
    /**
     * Function to check GPS/wifi enabled
     */
    public boolean canGetLocation()
    {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     */
    public void showSettingsAlert()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        //Setting Dialog Title
        alertDialog.setTitle("GPS Settings");

        //Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        //On Pressing Setting button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        //On pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    /**
     * Get list of address by latitude and longitude
     * @return null or List<Address>
     */
    public List<Address> getGeocoderAddress(Context context)
    {
        if (location != null)
        {
            Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);
            try
            {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                return addresses;
            }
            catch (IOException e)
            {
                //e.printStackTrace();
                Log.e("Error : Geocoder", "Impossible to connect to Geocoder", e);
            }
        }

        return null;
    }

    /**
     * Try to get AddressLine
     * @return null or addressLine
     */
    public String getAddressLine(Context context)
    {
        List<Address> addresses = getGeocoderAddress(context);
        if (addresses != null && addresses.size() > 0)
        {
            Address address = addresses.get(0);
            String addressLine = address.getAddressLine(0);

            return addressLine;
        }
        else
        {
            return null;
        }
    }

    /**
     * Try to get Locality
     * @return null or locality
     */
    public String getLocality(Context context)
    {
        List<Address> addresses = getGeocoderAddress(context);
        if (addresses != null && addresses.size() > 0)
        {
            Address address = addresses.get(0);
            String locality = address.getLocality();

            return locality;
        }
        else
        {
            return null;
        }
    }

    /**
     * Try to get Postal Code
     * @return null or postalCode
     */
    public String getPostalCode(Context context)
    {
        List<Address> addresses = getGeocoderAddress(context);
        if (addresses != null && addresses.size() > 0)
        {
            Address address = addresses.get(0);
            String postalCode = address.getPostalCode();

            return postalCode;
        }
        else
        {
            return null;
        }
    }

    /**
     * Try to get CountryName
     * @return null or postalCode
     */
    public String getCountryName(Context context)
    {
        List<Address> addresses = getGeocoderAddress(context);
        if (addresses != null && addresses.size() > 0)
        {
            Address address = addresses.get(0);
            String countryName = address.getCountryName();

            return countryName;
        }
        else
        {
            return null;
        }
    }

    @Override
    public void onLocationChanged(Location location)
    {
    }

    @Override
    public void onProviderDisabled(String provider)
    {
    }

    @Override
    public void onProviderEnabled(String provider)
    {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
>>>>>>> 86532df8cd6847cbc3475ede6a019b648550db4d
    }


    @Override
<<<<<<< HEAD
    public IBinder onBind(Intent intent) {
=======
    public IBinder onBind(Intent intent)
    {
>>>>>>> 86532df8cd6847cbc3475ede6a019b648550db4d
        return null;
    }
}