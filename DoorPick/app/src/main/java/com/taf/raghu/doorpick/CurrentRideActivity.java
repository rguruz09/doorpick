package com.taf.raghu.doorpick;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.taf.raghu.doorpick.R.id.map;

public class CurrentRideActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
        LocationListener, GoogleMap.OnMyLocationButtonClickListener, ActivityCompat.OnRequestPermissionsResultCallback{

    private GoogleMap mMap;
    private LocationManager locationManager;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;
    private GoogleApiClient client;
    private String src;
    private String dest;
    private int sts = 0;
    private static String sid;
    private static String email;

    private static SharedPreferences pref;

    private static Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            src = extras.getString("src");
            sid = extras.getString("sid");
        }



        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        setContentView(R.layout.activity_current_ride);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.ridemap);
        mapFragment.getMapAsync(this);

        btnConfirm = (Button) findViewById(R.id.btnConfirmingPick);

        pref = getApplicationContext().getSharedPreferences("MyPref", 1);
        email = pref.getString("email",null);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSts();
            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, this);

    }

    public void updateSts(){

        if(sts == 0){
            //sts = 1;

            String url = getString(R.string.url)+"updateScheduleStatus";

            HashMap<String,String> input = new HashMap<String,String>();
            input.put("schid",sid);
            input.put("sstatus","confirmed");
            input.put("email",email);
            new updateStatusTask(input).execute(url);

            btnConfirm.setText("Begin Ride");

        }else if(sts == 1){
            //sts = 2;
            String url = getString(R.string.url)+"updateScheduleStatus";

            HashMap<String,String> input = new HashMap<String,String>();
            input.put("schid",sid);
            input.put("sstatus","inprogress");
            input.put("email",email);
            new updateStatusTask(input).execute(url);

            btnConfirm.setText("End Ride");
        }else {

            String url = getString(R.string.url)+"updateScheduleStatus";

            HashMap<String,String> input = new HashMap<String,String>();
            input.put("schid",sid);
            input.put("sstatus","completed");
            input.put("email",email);
            new updateStatusTask(input).execute(url);

            btnConfirm.setText("End Ride");

        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();
        Location cur = getMyLocation();

        Double l;
        Double lo;

        l = Double.valueOf(src.split(",")[0]);
        lo = Double.valueOf(src.split(",")[1]);
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(l, lo))
                .draggable(true));
        l = cur.getLatitude();
        lo = cur.getLongitude();
        dest = l.toString()+","+lo.toString();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(cur.getLatitude(), cur.getLongitude()), 13));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(cur.getLatitude(), cur.getLongitude()))      // Sets the center of the map to location user
                .zoom(14)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        new GetDirection(src, dest).execute();

    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    android.Manifest.permission.ACCESS_FINE_LOCATION, true);
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
                android.Manifest.permission.ACCESS_FINE_LOCATION)) {
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
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    @Override
    public boolean onMarkerClick(final Marker marker) {


        return true;
    }



    class GetDirection extends AsyncTask<String, String, String> {

        List<LatLng> pontos;
        private ProgressDialog dialog;
        private String origin;
        private String destination;

        public GetDirection(String org, String des){
            origin = org;
            destination = des;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new ProgressDialog(CurrentRideActivity.this);
            dialog.setMessage("Drawing the route, please wait!");
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.show();

        }

        protected String doInBackground(String... args) {
            String stringUrl = "http://maps.googleapis.com/maps/api/directions/json?origin=" + origin+ "&destination=" + destination+ "&sensor=false";
            StringBuilder response = new StringBuilder();
            try {
                URL url = new URL(stringUrl);
                HttpURLConnection httpconn = (HttpURLConnection) url
                        .openConnection();
                if (httpconn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader input = new BufferedReader(
                            new InputStreamReader(httpconn.getInputStream()),
                            8192);
                    String strLine = null;

                    while ((strLine = input.readLine()) != null) {
                        response.append(strLine);
                    }
                    input.close();
                }

                String jsonOutput = response.toString();

                JSONObject jsonObject = new JSONObject(jsonOutput);

                // routesArray contains ALL routes
                JSONArray routesArray = jsonObject.getJSONArray("routes");
                // Grab the first route
                JSONObject route = routesArray.getJSONObject(0);

                JSONObject poly = route.getJSONObject("overview_polyline");
                String polyline = poly.getString("points");
                pontos = decodePoly(polyline);

            } catch (Exception e) {

            }

            return null;

        }

        protected void onPostExecute(String file_url) {
            for (int i = 0; i < pontos.size() - 1; i++) {
                LatLng src = pontos.get(i);
                LatLng dest = pontos.get(i + 1);
                try{
                    //here is where it will draw the polyline in your map
                    Polyline line = mMap.addPolyline(new PolylineOptions()
                            .add(new LatLng(src.latitude, src.longitude),
                                    new LatLng(dest.latitude,                dest.longitude))
                            .width(10).color(Color.RED).geodesic(true));
                }catch(NullPointerException e){
                    Log.e("Error", "NullPointerException onPostExecute: " + e.toString());
                }catch (Exception e2) {
                    Log.e("Error", "Exception onPostExecute: " + e2.toString());
                }

            }
           dialog.dismiss();

        }
    }

    public class updateStatusTask  extends AsyncTask<String, Void, JSONObject> {

        String charset = "UTF-8";
        String requestURL;
        HashMap<String,String> formFields;
        String status = "";
        JSONObject httpStatus ;
        private ProgressDialog dialog;


        private Context mContext;
        //private TaskCompletedStatus mCallback;


        public updateStatusTask( HashMap<String,String> formFields){
            this.formFields = formFields;
            //  this.mCallback = (TaskCompletedStatus) context;
            httpStatus = new JSONObject();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new ProgressDialog(CurrentRideActivity.this);
            dialog.setMessage("Updating..!");
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.show();

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
            dialog.dismiss();
            handleResponse(result);
        }
    }


    private void handleResponse(JSONObject result){

        switch (sts){
            case 0:
                sts =1;

                break;
            case 1:
                sts =2;

                break;
            case 2:
                sts = 3;
                Intent intent = new Intent(this, DriverMapActivity.class);
                startActivity(intent);
                break;
        }
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

}
