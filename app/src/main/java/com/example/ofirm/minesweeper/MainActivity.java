package com.example.ofirm.minesweeper;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    private Button EasyBtn;
    private Button MediumBtn;
    private Button HardBtn;
    private Button HighScore;


    ///////--------///////
    private static final String[] INITIAL_PERMS = {Manifest.permission.ACCESS_FINE_LOCATION};
    private static final String[] LOCATION_PERMS = {Manifest.permission.ACCESS_FINE_LOCATION};
    private static final int INITIAL_REQUEST = 1337;
    private static final int LOCATION_REQUEST = INITIAL_REQUEST + 1;
    ///////--------///////
    protected Location mCurrentLocation;
    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    protected Boolean mRequestingLocationUpdates;
    private LocationManager locationManager;
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    protected static final String TAG = "location-updates-sample";
    ///////--------////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("onCreate"," ");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        EasyBtn = (Button) findViewById(R.id.EasyButton);
        MediumBtn = (Button) findViewById(R.id.MediumButton);
        HardBtn = (Button) findViewById(R.id.HardButton);
        HighScore = (Button) findViewById(R.id.HighScoreButton);

        mRequestingLocationUpdates = false;
        locationManager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);


        EasyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClicked(getString(R.string.Easy));
            }
        });
        MediumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClicked(getString(R.string.Medium));
            }
        });
        HardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClicked(getString(R.string.Hard));
            }
        });
        HighScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClicked(getString(R.string.HighScore));
            }
        });

//        DataBase db = new DataBase(this);
//        db.deleteAllDatabase(db.getAllScoresByLevel(4));

        updateValuesFromBundle(savedInstanceState);
        buildGoogleApiClient();

    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        Log.i(TAG, "Updating values from bundle");
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(REQUESTING_LOCATION_UPDATES_KEY);
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            updateUI();
        }
    }


    /**
     * Updates the latitude, the longitude.
     */
    private void updateUI() {

        //boolean gpsPerm = canAccessLocation();
        //boolean gpsOn = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // mCurrentLocation.toString();

        if (canAccessLocation() && gpsON() && mCurrentLocation != null) {
            Log.d(" *******", mCurrentLocation.getLatitude() + " ");
            Log.d(" *******", mCurrentLocation.getLongitude() + " ");
        }
        needPermissionOrGpsOn();

    }


    public boolean gpsON() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void needPermissionOrGpsOn() {
        if (!gpsON())
            popUpDialogSetLocationOn();
        if (!canAccessLocation()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                requestPermissions(LOCATION_PERMS, LOCATION_REQUEST);
        }
    }


    // TODO: 17/11/2015 ------ we need to replace the string with values or const! (Easy,Medium,Hard)
    private void buttonClicked(String level) {
        Intent i = new Intent(MainActivity.this, GameActivity.class);
        Intent j = new Intent(MainActivity.this, PagerActivity.class);
        if(mCurrentLocation!=null){
            switch (level) {
                case "Easy":
                    i.putExtra("level", R.string.Easy);
                    sendLocation(i);
                    startActivity(i);
                    break;
                case "Medium":
                    i.putExtra("level", R.string.Medium);
                    sendLocation(i);
                    startActivity(i);
                    break;
                case "Hard":
                    i.putExtra("level", R.string.Hard);
                    sendLocation(i);
                    startActivity(i);
                    break;
                case "High score":
                    j.putExtra("level", R.string.Hard);
                    startActivity(j);
                    break;
            }
        }else{
            //// TODO: 03/01/2016
            if (mCurrentLocation == null && gpsON() && canAccessLocation()) {
                mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                updateUI();
            }
        }


            needPermissionOrGpsOn();
        Log.d("MainActivity", level + " button clicked");
    }

    private void sendLocation(Intent i) {
        i.putExtra("latitude", mCurrentLocation.getLatitude());
        i.putExtra("longitude", mCurrentLocation.getLongitude());
    }


    ///////////CODE THE SETTINGS HERE!!!!/////////
    //////// onOptionsItemSelected() evry option in the 2 dot settings!!///////
    // TODO: 17/11/2015 CODE THE SETTINGS HERE!!!!!
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getMenuInflater().inflate(R.menu.menu_main_23, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_main, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.location:
                if (canAccessLocation()) {
                    doLocationThing();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(LOCATION_PERMS, LOCATION_REQUEST);
                    }
                }
                return true;
            case R.id.action_settings:
                Log.d("im in the setting", "yap");
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                return true;
        }

        return (super.onOptionsItemSelected(item));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch(requestCode) {
            case LOCATION_REQUEST:
                if (!canAccessLocation()) {
                    bzzzt();
                }
                break;
//            //// TODO: 29/12/2015 if we add more setting tabs... csse :....
        }
    }

    private boolean canAccessLocation() {
        return(hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }
    private boolean hasPermission(String perm) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return(PackageManager.PERMISSION_GRANTED==checkSelfPermission(perm));
        }
        return true;
    }
    private void doLocationThing() {
        Toast.makeText(this, R.string.toast_location, Toast.LENGTH_SHORT).show();
    }
    private void bzzzt() {
        Toast.makeText(this, R.string.toast_bzzzt, Toast.LENGTH_SHORT).show();
    }

    public void popUpDialogSetLocationOn(){
        AlertDialog.Builder gpsOnBuilder = new AlertDialog.Builder(MainActivity.this);
        gpsOnBuilder.setTitle("Your Gps Provider is disabled please Enable it");
        gpsOnBuilder.setPositiveButton("ON",new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface arg0, int arg1) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });

        gpsOnBuilder.show();
    }

    public void isSdk23(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!canAccessLocation()) {
                requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
            }
        }
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("onStart", " ");
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("onResume", " ");
//        Log.d("mReqationUpdates",mRequestingLocationUpdates+" ");
//        Log.d("isConnected()",mGoogleApiClient.isConnected()+" ");
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
        if (mCurrentLocation != null) {
            Log.d("^^^^^^^^^^^^^^^^^", " ");
            Log.d("the lat: " + mCurrentLocation.getLatitude(), " ");
            Log.d("the lon: " + mCurrentLocation.getLongitude(), " ");
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
        Log.d("onPause"," ");

    }

    @Override
    protected void onDestroy() {
        Log.d("onDestroy"," ");
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
        Log.d("onStop", " ");

    }


    /**
     *  Implements ConnectionCallbacks
     **/
    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Connected to GoogleApiClient");
        if (mCurrentLocation == null && gpsON() && canAccessLocation()) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            updateUI();
        }

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    /**
     *  Implements OnConnectionFailedListener
     **/
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    /**
     *  Implements LocationListener
     **/
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        updateUI();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        super.onSaveInstanceState(savedInstanceState);
    }




}