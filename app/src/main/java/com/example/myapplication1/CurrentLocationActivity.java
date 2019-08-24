package com.example.myapplication1;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class CurrentLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int ERROR_DIALOG_REQUEST =1234;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1235;
    int PERMISSIONS_REQUEST_ENABLE_GPS=1236;
    String TAG="MEH";
    boolean mLocationPermissionGranted;
    private UserLocation mUserLocation;
    private FirebaseFirestore mDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_location);
        mDb = FirebaseFirestore.getInstance();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        if(!isLocationEnabled(this))
//        {
//            new AlertDialog.Builder(CurrentLocationActivity.this)
//                    .setMessage("location not enabled")
//                    .setPositiveButton("open location setting", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                            CurrentLocationActivity.this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//                        }
//                    })
//                    .setNegativeButton("Cancel",null)
//                    .show();
//
//        }

        checkMapServices();

//        LatLng current = new LatLng(lat, lon);
//        mMap.addMarker(new MarkerOptions().position(current).title("Marker where you are"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
    }
    public void getLocation() throws SecurityException {//final method to display location
        Log.d(TAG,"getting location");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Log.d(TAG,"on sucess sucess");
                        // Got last known location. In some rare situations this can be null.

                        if (location != null) {
                            Log.d(TAG,"location is not null");
                            Toast.makeText(CurrentLocationActivity.this,location.getLatitude()+""+location.getLatitude(),Toast.LENGTH_LONG).show();
                            LatLng current = new LatLng(location.getLatitude(),location.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(current).title("Marker where you are"));
                            //mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current,10));

                            //mUserLocation.setLocation(location);
                            saveUserLocation();

                        }
                    }
                });


    }


    public void requestPermission() {
        Log.d(TAG,"requesting permission");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "Access required to access location", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);
                requestPermission();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            }
        } else {
            //getLocation();
            getUserDetails();

        }
    }


    public void versionCheck()
    {
        Log.d(TAG,"checking version");
        int num= android.os.Build.VERSION.SDK_INT;
        if(num>= Build.VERSION_CODES.M)
        {

            requestPermission();
        }
        else
        {
            getUserDetails();
        }
    }

    public boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                Log.d(TAG,"Checking if location is on for android better than kitkat");
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            Log.d(TAG,"Checking if location is on for android lesser than kitkat");
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }
    private boolean checkMapServices(){
        if(isServicesOK()){
            if(isMapsEnabled()){
                if(isConnectedToInternet())
                    versionCheck();

            }
        }
        return false;
    }
    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(CurrentLocationActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(CurrentLocationActivity.this, available,ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           @NonNull String permissions[],
//                                           @NonNull int[] grantResults) {
//        mLocationPermissionGranted = false;
//        if(requestCode==MY_PERMISSIONS_REQUEST_FINE_LOCATION)
//        {
//            if (grantResults.length > 0
//                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                mLocationPermissionGranted = true;
//            }
//        }
//        }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        if(requestCode==PERMISSIONS_REQUEST_ENABLE_GPS)
          {
                if(mLocationPermissionGranted){

                }
                else{
                    requestPermission();
                }
            }
        }
    public boolean isConnectedToInternet(){
        Log.d(TAG,"Checking if internet is connected");
        ConnectivityManager connectivity = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        Log.d(TAG, "Connected to Internet");
                        return true;
                    }

                }
                Log.d(TAG, "Not connected");
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("This application requires Internet to work properly, do you want to enable it?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                CurrentLocationActivity.this.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                                ComponentName cName = new ComponentName("com.android.phone", "com.android.phone.NetworkSetting");
                                intent.setComponent(cName);


                            }
                        });
                final AlertDialog alert = builder.create();
                alert.show();

                // Here I've been added intent to open up data settings

            }
        }
        else
        {
            Log.d(TAG,"Not connected");
            // Here I've been added intent to open up data settings
            CurrentLocationActivity.this.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
            ComponentName cName = new ComponentName("com.android.phone", "com.android.phone.NetworkSetting");
            intent.setComponent(cName);

        }
        return false;
    }
    private void getUserDetails(){
        if(mUserLocation == null){
            mUserLocation = new UserLocation();
             DocumentReference userRef = mDb.collection("users")
                    .document(FirebaseAuth.getInstance().getUid());

            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        Log.d(TAG, "onComplete: successfully set the user client.");

                      // Details user = task.getResult().toObject(Details.class);
                      //  Details notifPojo = userRef.toObject(Details.class);
                       // mUserLocation.setDetails(user);
                        getLocation();
                    }
                    else
                    {
                        getLocation();
                    }
                }
            });
        }
        else{
            getLocation();
        }
    }
    private void saveUserLocation(){

        if(mUserLocation != null){
            DocumentReference locationRef = mDb
                    .collection("user locations")
                    .document(FirebaseAuth.getInstance().getUid());

            locationRef.set(mUserLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Log.d(TAG, "saveUserLocation: \ninserted user location into database.");
                    }
                    else
                    {
                        Log.d(TAG,""+task.getException());
                    }
                }
            });
        }
    }
    //is my code even present??


}



