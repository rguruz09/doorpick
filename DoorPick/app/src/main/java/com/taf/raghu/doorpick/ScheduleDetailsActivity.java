package com.taf.raghu.doorpick;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class ScheduleDetailsActivity extends AppCompatActivity {

    private static int position = -1;
    private static EditText pName;
    private static EditText pDesc;
    public static EditText pDate;
    public static EditText pTime;
    private static EditText pLoc;
    private static EditText pdestLoc;
    private static Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_details);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            position = (int) extras.get("position");
        }

        pName = (EditText) findViewById(R.id.input_name);
        pDesc = (EditText) findViewById(R.id.input_details);
        pDate = (EditText) findViewById(R.id.input_date);
        pTime = (EditText) findViewById(R.id.input_time);
        pLoc = (EditText) findViewById(R.id.input_loc);
        pdestLoc = (EditText) findViewById(R.id.input_droploc);
        button = (Button) findViewById(R.id.btn_schedule);


        pDesc.setClickable(false);
        pDesc.setFocusable(false);

        pName.setClickable(false);
        pName.setFocusable(false);

        pDate.setClickable(false);
        pDate.setFocusable(false);

        pTime.setClickable(false);
        pTime.setFocusable(false);

        pLoc.setClickable(false);
        pLoc.setFocusable(false);

        pdestLoc.setClickable(false);
        pdestLoc.setFocusable(false);



        if(position >= 0){
            Schedules sc = ScheduleListSington.get(this).getOrdersListInfos().get(position);
            pName.setText(sc.getpName());
            pDesc.setText(sc.getpDesc());
            pDate.setText(sc.getpPickDate());
            pTime.setText(sc.getpPickTime());
            pdestLoc.setText(sc.getpDropLoc());
            pLoc.setText(sc.getpPickUpLoc());

            if(!sc.getsStatus().equals("requested")){
                button.setVisibility(View.INVISIBLE);
            }
        }


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelSchedule();
            }
        });

    }

    public void cancelSchedule(){
        Schedules sc = ScheduleListSington.get(this).getOrdersListInfos().get(position);
        sc.setsStatus("cancelled");
        String scid = sc.getScdId();

        String url = getString(R.string.url) + "updateScheduleStatus";

        HashMap<String, String> input = new HashMap<String, String>();

        input.put("schid", scid);
        input.put("sstatus",sc.getsStatus());

        new UpdateStsTask(input).execute(url);

    }

    private void handleResponse(JSONObject result) {

        try {

            int stscode = result.getInt("statusCode");
            if (stscode == 200) {
                Context context = getApplicationContext();
                CharSequence text = "Schedule Cancelled";
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

    public class UpdateStsTask extends AsyncTask<String, Void, JSONObject> {
        String charset = "UTF-8";
        String requestURL;
        HashMap<String, String> formFields;
        String status = "";
        JSONObject httpStatus;


        private Context mContext;
        //private TaskCompletedStatus mCallback;


        public UpdateStsTask(HashMap<String, String> formFields) {
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
