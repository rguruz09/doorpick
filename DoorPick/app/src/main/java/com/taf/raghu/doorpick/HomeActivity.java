package com.taf.raghu.doorpick;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ArrayList<Schedules> scheduls;

    private static SharedPreferences pref;
    private static SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pref = getApplicationContext().getSharedPreferences("MyPref", 1);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScheduleNew();
            }
        });

        loadSchedules();

    }

    private void loadSchedules(){

        String url = getString(R.string.url) + "getAllSchedules";

        HashMap<String, String> input = new HashMap<String, String>();
        input.put("email", pref.getString("email", null));

        new scheduleTask(input).execute(url);

    }

    private void handleResponse(JSONObject result){

        try {

            ScheduleListSington  scheduleListSington =ScheduleListSington.get(this);
            if(scheduleListSington.getOrdersListInfos() != null && scheduleListSington.getOrdersListInfos().size() >0 ){

            }else{

                ArrayList<Schedules> schedules = new ArrayList<>();

                JSONArray resArray = result.getJSONArray("data");

                for(int i = 0; i<resArray.length(); i++){
                    JSONObject jb = (JSONObject) resArray.get(i);

                    Schedules scd = new Schedules(jb.getString("schid"),jb.getString("pname"),jb.getString("pdesc"),jb.getString("ploc"),jb.getString("pdate"),
                            jb.getString("ptime"),jb.getString("pdloc"),jb.getString("sstatus"));
                    schedules.add(scd);
                }

                scheduleListSington.serScheduleList(schedules);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.mainpage_fragment,new SchedulesListFragment()).commit();


    }

    private void ScheduleNew(){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class scheduleTask  extends AsyncTask<String, Void, JSONObject> {
        String charset = "UTF-8";
        String requestURL;
        HashMap<String,String> formFields;
        String status = "";
        JSONObject httpStatus ;

        private Context mContext;
        //private TaskCompletedStatus mCallback;


        public scheduleTask( HashMap<String,String> formFields){
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
            handleResponse(result);
        }
    }

}
