package com.taf.raghu.doorpick;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class DriverMapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
        LocationListener, GoogleMap.OnMyLocationButtonClickListener, ActivityCompat.OnRequestPermissionsResultCallback{



    HashMap <String, Schedules> mMarkers;

    private GoogleMap mMap;
    private LocationManager locationManager;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_map);


        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.dvmap);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMarkers = new HashMap<String, Schedules>();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch(id) {
            case android.R.id.home:
                if (NavUtils.getParentActivityName(this) != null) {
                    NavUtils.navigateUpFromSameTask(this);
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();
        Location cur = getMyLocation();
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(cur.getLatitude(), cur.getLongitude()))
                .draggable(true)
                .title("Hello world"));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(cur.getLatitude(), cur.getLongitude()), 13));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(cur.getLatitude(), cur.getLongitude()))      // Sets the center of the map to location user
                .zoom(14)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                handleMarkerClick(marker);
                return true;
            }
        });
        getReqSchedules();

    }

    public void handleMarkerClick(Marker marker){

        Schedules s = mMarkers.get(marker.getId());

        Intent intent = new Intent(this, RequestedSchedule_Activity.class);
        intent.putExtra("pScid",s.getScdId());
        intent.putExtra("pEmail",s.getCustEmail());
        intent.putExtra("pName",s.getpName());
        intent.putExtra("pDesc",s.getpDesc());
        intent.putExtra("pDate",s.getpPickDate());
        intent.putExtra("pTime",s.getpPickTime());
        intent.putExtra("pLoc",s.getpPickUpLoc());
        intent.putExtra("pdestLoc",s.getpDropLoc());

        intent.putExtra("picLat",s.getSlat());
        intent.putExtra("picLong",s.getSlong());
        intent.putExtra("dropLat",s.getDlat());
        intent.putExtra("dropLong",s.getDlong());

        startActivity(intent);

    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        System.out.print("Location Changed");
//        mMap.addMarker(new MarkerOptions()
//                .position(new LatLng(location.getLatitude(), location.getLongitude()))
//                .draggable(true)
//                .title("Hello world"));
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        return false;
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    private Location getMyLocation() {
        // Get location from GPS if it's available
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //
        }
        Location myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        // Location wasn't found, check the next most accurate place for the current location
        if (myLocation == null) {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            // Finds a provider that matches the criteria
            String provider = lm.getBestProvider(criteria, true);
            // Use the provider to get the last known location
            myLocation = lm.getLastKnownLocation(provider);
        }

        return myLocation;
    }

    @Override
    public void onStart() {
        super.onStart();


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.taf.raghu.doorpick/http/host/path")
        );

        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.taf.raghu.doorpick/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    public void getReqSchedules(){

        String url = getString(R.string.url)+"getScheduleRequests";

        HashMap<String,String> input = new HashMap<String,String>();

        new getReqSchedulesTask(input).execute(url);

    }

    public void loadRequsts(JSONObject result){

        int stscode = 0;
        try {
            stscode = result.getInt("statusCode");
            if(stscode == 200){

                ArrayList<Schedules> reqSchedule = ReqScheSington.get().getOrdersListInfos();
                reqSchedule.clear();

                JSONArray resArray = result.getJSONArray("data");
                for(int i = 0; i<resArray.length(); i++){

                    JSONObject jb = (JSONObject) resArray.get(i);

                    Schedules scd = new Schedules(jb.getString("schid"),jb.getString("pname"),jb.getString("pdesc"),jb.getString("ploc"),jb.getString("pdate"),
                            jb.getString("ptime"),jb.getString("pdloc"),jb.getString("sstatus"));

                    scd.setSlat(jb.getString("slat"));
                    scd.setSlong(jb.getString("slong"));
                    scd.setDlat(jb.getString("desclat"));
                    scd.setDlong(jb.getString("desclong"));
                    scd.setCustEmail(jb.getString("email"));

                    reqSchedule.add(scd);

                    double lat = Double.parseDouble(scd.getSlat());
                    double lng = Double.parseDouble(scd.getSlong());

                    MarkerOptions mo = new MarkerOptions()
                            .position(new LatLng(lat, lng))
                            .draggable(false)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.driver))
                            .title(jb.getString("email"));

                    Marker mkr = mMap.addMarker(mo);
                    mMarkers.put(mkr.getId(), scd);


                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {


        return true;
    }

    public class getReqSchedulesTask  extends AsyncTask<String, Void, JSONObject> {

        String charset = "UTF-8";
        String requestURL;
        HashMap<String,String> formFields;
        String status = "";
        JSONObject httpStatus ;

        private Context mContext;
        //private TaskCompletedStatus mCallback;


        public getReqSchedulesTask( HashMap<String,String> formFields){
            this.formFields = formFields;
            //  this.mCallback = (TaskCompletedStatus) context;
            httpStatus = new JSONObject();
        }


        @Override
        protected JSONObject doInBackground(String... url) {


            requestURL =  url[0];

            try {
                MultipartUtility multipart = new MultipartUtility(requestURL, charset);

                if(formFields != null){
                    if(!(formFields.isEmpty())){
                        for (HashMap.Entry<String, String> entry : formFields.entrySet()) {
                            String key = entry.getKey();
                            String value = entry.getValue();
                            multipart.addFormField(key,value);
                        }
                    }
                }

                String response = multipart.finishString();

                if( response != null){
                    try {
                        httpStatus = new JSONObject(response);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            } catch (IOException ex) {
                System.err.println(ex);
            }
            Log.i("MultiAsynk",httpStatus + "");
            return httpStatus;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
            loadRequsts(result);
        }
    }


}
