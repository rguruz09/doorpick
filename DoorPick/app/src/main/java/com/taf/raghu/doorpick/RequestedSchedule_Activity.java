package com.taf.raghu.doorpick;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RequestedSchedule_Activity extends AppCompatActivity {

    private static EditText pEmail;
    private static EditText pName;
    private static EditText pDesc;
    public static EditText pDate;
    public static EditText pTime;
    private static EditText pLoc;
    private static EditText pdestLoc;
    private static Button button;

    Double slat;
    Double slong;
    Double dlat;
    Double dlong;
    private static String pScid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requested_schedule_);

        pEmail = (EditText) findViewById(R.id.input_user_email);
        pName = (EditText) findViewById(R.id.input_name);
        pDesc = (EditText) findViewById(R.id.input_details);
        pDate = (EditText) findViewById(R.id.input_date);
        pTime = (EditText) findViewById(R.id.input_time);
        pLoc = (EditText) findViewById(R.id.input_loc);
        pdestLoc = (EditText) findViewById(R.id.input_droploc);
        button = (Button) findViewById(R.id.btn_schedule);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            pEmail.setText(extras.getString("pEmail"));
            pName.setText(extras.getString("pName"));
            pDesc.setText(extras.getString("pDesc"));
            pDate.setText(extras.getString("pDate"));
            pTime.setText(extras.getString("pTime"));
            pLoc.setText(extras.getString("pLoc"));
            pdestLoc.setText(extras.getString("pdestLoc"));


            slat = Double.parseDouble(extras.getString("picLat"));
            slong = Double.parseDouble(extras.getString("picLong"));
            dlat = Double.parseDouble(extras.getString("dropLat"));
            dlong = Double.parseDouble(extras.getString("dropLong"));
            pScid = extras.getString("pScid",null);

        }

        pEmail.setClickable(false);
        pEmail.setFocusable(false);

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

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmPickUp();
            }
        });
    }

    public void confirmPickUp(){

        Intent intent = new Intent(this, CurrentRideActivity.class);
        intent.putExtra("src",slat.toString()+","+slong);
        intent.putExtra("sid",pScid);
        startActivity(intent);
    }

}
