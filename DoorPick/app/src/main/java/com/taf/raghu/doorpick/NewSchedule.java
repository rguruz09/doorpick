package com.taf.raghu.doorpick;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.SingleLineTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by raghu on 19/11/16.
 */
public class NewSchedule extends AppCompatActivity {

    private String strAddr;
    private String strCity;
    private String strState;
    private String strZip;
    String[] address;

    private EditText pName;
    private EditText pDesc;


    public static EditText pDate;
    public static EditText pTime;
    private EditText pLoc;
    private EditText pdestLoc;

    private static int callby = -1;

    private static String srclat;
    private static String srclong;
    private static String deslat;
    private static String deslong;

    private static SharedPreferences pref;
    private static SharedPreferences.Editor editor;

    Schedules scd;

    int PLACE_PICKER_REQUEST = 1;
    Button button;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_schedule);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            address = extras.getString("addr").split(",");
            srclat = extras.getString("lat");
            srclong = extras.getString("long");
        }

        pref = getApplicationContext().getSharedPreferences("MyPref", 1);

        pName = (EditText) findViewById(R.id.input_name);
        pDesc = (EditText) findViewById(R.id.input_details);
        pDate = (EditText) findViewById(R.id.input_date);
        pTime = (EditText) findViewById(R.id.input_time);
        button = (Button) findViewById(R.id.btn_schedule);
        pDate.setFocusable(false);
        pDate.setClickable(true);
        pTime.setFocusable(false);
        pTime.setClickable(true);

        pLoc = (EditText) findViewById(R.id.input_loc);
        pLoc.setFocusable(false);
        pLoc.setClickable(true);
        pLoc.setText(address[0]);

        pLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callby = 0;
                locChange();
            }
        });

        pdestLoc = (EditText) findViewById(R.id.input_droploc);
        pdestLoc.setFocusable(false);
        pdestLoc.setClickable(true);
        //pdestLoc.setText(address[0]);

        pdestLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callby = 1;
                locChange();
            }
        });


        pDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateChange();
            }
        });

        pTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeChange();
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmSchedule();
            }
        });


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }

    private void locChange() {

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (Exception e) {
            System.out.print(e.getCause());
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);

                if (callby == 0) {
                    pLoc.setText(place.getAddress().toString());
                    srclat = Double.toString(place.getLatLng().latitude);
                    srclong = Double.toString(place.getLatLng().longitude);
                } else {
                    pdestLoc.setText(place.getAddress().toString());
                    deslat = Double.toString(place.getLatLng().latitude);
                    deslong = Double.toString(place.getLatLng().longitude);
                }
                callby = -1;
            }
        }
    }

    private void dateChange() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void timeChange() {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    private void confirmSchedule() {

        String url = getString(R.string.url) + "addNewSchedule";
        boolean cancel = false;
        View focusView = null;

        HashMap<String, String> input = new HashMap<String, String>();


        String pname = pName.getText().toString();
        String pdesc = pDesc.getText().toString();
        String ploc = pLoc.getText().toString();
        String pdloc = pdestLoc.getText().toString();
        String pdate = pDate.getText().toString();
        String ptime = pTime.getText().toString();


        // Check for a valid email address.
        if (TextUtils.isEmpty(pname)) {
            pName.setError(getString(R.string.error_field_required));
            focusView = pName;
            cancel = true;
        }

        // fname
        if (TextUtils.isEmpty(pdesc)) {
            pDesc.setError(getString(R.string.error_field_required));
            focusView = pDesc;
            cancel = true;
        }

        //lname
        if (TextUtils.isEmpty(ploc)) {
            pLoc.setError(getString(R.string.error_field_required));
            focusView = pLoc;
            cancel = true;
        }

        //address
        if (TextUtils.isEmpty(pdesc)) {
            pdestLoc.setError(getString(R.string.error_field_required));
            focusView = pdestLoc;
            cancel = true;
        }


        if (!cancel) {

            String schid = UUID.randomUUID().toString();

            input.put("schid", schid);
            input.put("email", pref.getString("email", null));
            input.put("pname", pname);
            input.put("pdesc", pdesc);
            input.put("ploc", ploc);
            input.put("pdloc", pdloc);
            input.put("pdate", pdate);
            input.put("ptime", ptime);
            input.put("slat", srclat);
            input.put("slong", srclong);
            input.put("desclat", deslat);
            input.put("desclong", deslong);
            input.put("sstatus", "requested");

            scd = new Schedules(schid,pname,pdesc,ploc,pdate,
                    ptime,ploc,"requested");

            new AddScheduleTask(input).execute(url);

        }

    }

    private void handleResponse(JSONObject result) {

        try {

            int stscode = result.getInt("statusCode");
            if (stscode == 200) {


                if(scd!= null){
                    ScheduleListSington.get(this).getOrdersListInfos().add(scd);
                }

                Context context = getApplicationContext();
                CharSequence text = "Looking for drivers";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);


            } else {
                Context context = getApplicationContext();
                CharSequence text = result.getString("message");
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("NewSchedule Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    public class AddScheduleTask extends AsyncTask<String, Void, JSONObject> {
        String charset = "UTF-8";
        String requestURL;
        HashMap<String, String> formFields;
        String status = "";
        JSONObject httpStatus;


        private Context mContext;
        //private TaskCompletedStatus mCallback;


        public AddScheduleTask(HashMap<String, String> formFields) {
            this.formFields = formFields;
            //  this.mCallback = (TaskCompletedStatus) context;
            httpStatus = new JSONObject();

        }


        @Override
        protected JSONObject doInBackground(String... url) {


            requestURL = url[0];

            try {
                MultipartUtility multipart = new MultipartUtility(requestURL, charset);

                if (formFields != null) {
                    if (!(formFields.isEmpty())) {
                        for (HashMap.Entry<String, String> entry : formFields.entrySet()) {
                            String key = entry.getKey();
                            String value = entry.getValue();
                            multipart.addFormField(key, value);
                        }
                    }
                }

                String response = multipart.finishString();

                if (response != null) {
                    try {
                        httpStatus = new JSONObject(response);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            } catch (IOException ex) {
                System.err.println(ex);
            }
            Log.i("MultiAsynk", httpStatus + "");
            return httpStatus;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
            //showProgress(false);
            handleResponse(result);
        }
    }

}
