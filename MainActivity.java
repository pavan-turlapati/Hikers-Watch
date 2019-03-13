package com.nocomp.hikerswatch;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("MissingPermission")

    public void updateLocation (Location location)
    {
        Log.i("Location", location.toString());

        TextView latTextView = (TextView) findViewById(R.id.latTextView);

        TextView longTextView = (TextView) findViewById(R.id.longTextView);

        TextView addTextView = (TextView) findViewById(R.id.addTextView);

        TextView accTextView = (TextView) findViewById(R.id.accTextView);

        TextView altTextView = (TextView) findViewById(R.id.altTextView);

        latTextView.setText("Latitude : "+location.getLatitude());

        longTextView.setText("Longitude :"+location.getLongitude());

        altTextView.setText("Altitude : "+location.getAltitude());

        accTextView.setText("Accuracy :"+location.getAccuracy());

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {

            String address = "Could not find address..";
            List<Address> listAddress = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);

            if(listAddress != null && listAddress.size() > 0)
            {
                Log.i("Address Info :",listAddress.get(0).toString());

                address="Address:\n";

                if(listAddress.get(0).getSubThoroughfare()!= null)
                {
                    address+= listAddress.get(0).getSubThoroughfare()+ " ";
                }
                if(listAddress.get(0).getThoroughfare()!= null)
                {
                    address+= listAddress.get(0).getThoroughfare()+ "\n";
                }
                if(listAddress.get(0).getLocality()!= null)
                {
                    address+= listAddress.get(0).getLocality()+ "\n";
                }
                if(listAddress.get(0).getPostalCode()!= null)
                {
                    address+= listAddress.get(0).getPostalCode()+ "\n";
                }
                if(listAddress.get(0).getCountryName()!= null)
                {
                    address+= listAddress.get(0).getCountryName()+ "\n";
                }


            }

            addTextView.setText(address);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            startListening();
        }
    }

    public void startListening()
    {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,(1000*60),1,locationListener);

            Location location  = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(location != null) {
                updateLocation(location);
            }
        }
    }

    LocationManager locationManager;

    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                updateLocation(location);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if(Build.VERSION.SDK_INT<23)
        {
            startListening();
        }
        else
        {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                //ask for permission

                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
            else
            {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,(1000*60),1,locationListener);

                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if(lastKnownLocation != null) {

                    updateLocation(lastKnownLocation);
                }
            }
        }


    }
}
