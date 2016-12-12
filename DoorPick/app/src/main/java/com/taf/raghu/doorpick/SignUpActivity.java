package com.taf.raghu.doorpick;

import android.*;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {


    private Button signUpBtn;

    private static EditText ufname;
    private static EditText ulname;
    private static EditText email;
    private static EditText st_address;
    private static EditText city;
    private static EditText state;
    private static EditText zip;
    private static EditText mob;
    private static EditText password;
    private static EditText re_password;
    private static RadioButton user_type;
    private static String token;

    double longitude;
    double latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ufname = (EditText) findViewById(R.id.input_fname);
        ulname = (EditText) findViewById(R.id.input_lname);
        email = (EditText) findViewById(R.id.input_email);
        st_address = (EditText) findViewById(R.id.input_address1);
        city = (EditText) findViewById(R.id.input_city);
        state = (EditText) findViewById(R.id.input_state);
        zip = (EditText) findViewById(R.id.input_zip);
        mob = (EditText) findViewById(R.id.input_mobile);
        password = (EditText) findViewById(R.id.input_password);
        re_password = (EditText) findViewById(R.id.input_reEnterPassword);
        user_type = (RadioButton) findViewById(R.id.radio_customer);

        token = FirebaseInstanceId.getInstance().getToken();

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        longitude = location.getLongitude();
        latitude = location.getLatitude();


        signUpBtn = (Button) findViewById(R.id.btn_signup);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SuccessSignUp();
            }
        });
    }



    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    private void SuccessSignUp(){

        String url = getString(R.string.url)+"addUser";
        boolean cancel = false;
        View focusView = null;

        HashMap<String,String> input = new HashMap<String,String>();



        String pas1 = password.getText().toString();
        String pas2 = re_password.getText().toString();
        String sufname = ufname.getText().toString();
        String sulname = ulname.getText().toString();
        String semail = email.getText().toString();
        String sst_address = st_address.getText().toString();
        String scity = city.getText().toString();
        String sstate = state.getText().toString();
        String szip = zip.getText().toString();
        String smob = mob.getText().toString();


        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(pas1) && !isPasswordValid(pas1)) {
            password.setError(getString(R.string.error_invalid_password));
            focusView = password;
            cancel = true;
        }
        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(pas2) && !isPasswordValid(pas2)) {
            re_password.setError(getString(R.string.error_invalid_password));
            focusView = re_password;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(semail)) {
            email.setError(getString(R.string.error_field_required));
            focusView = email;
            cancel = true;
        } else if (!isEmailValid(semail)) {
            email.setError(getString(R.string.error_invalid_email));
            focusView = email;
            cancel = true;
        }

        // fname
        if (TextUtils.isEmpty(sufname)) {
            ufname.setError(getString(R.string.error_field_required));
            focusView = ufname;
            cancel = true;
        }

        //lname
        if (TextUtils.isEmpty(sulname)) {
            ulname.setError(getString(R.string.error_field_required));
            focusView = ulname;
            cancel = true;
        }

        //address
        if (TextUtils.isEmpty(sst_address)) {
            st_address.setError(getString(R.string.error_field_required));
            focusView = st_address;
            cancel = true;
        }

        //City
        if (TextUtils.isEmpty(scity)) {
            city.setError(getString(R.string.error_field_required));
            focusView = city;
            cancel = true;
        }

        //State
        if (TextUtils.isEmpty(sstate)) {
            state.setError(getString(R.string.error_field_required));
            focusView = state;
            cancel = true;
        }

        //Mob
        if (TextUtils.isEmpty(smob)) {
            mob.setError(getString(R.string.error_field_required));
            focusView = mob;
            cancel = true;
        }

        //zip
        if (TextUtils.isEmpty(szip)) {
            zip.setError(getString(R.string.error_field_required));
            focusView = zip;
            cancel = true;
        }

        if(!pas1.equals(pas2)){
            password.setError("Password didn't match");
            focusView = password;
            cancel = true;
        }

        if(!cancel){
            input.put("ufname",sufname);
            input.put("ulname",sulname);
            input.put("email",semail);
            input.put("st_address",sst_address);
            input.put("city",scity);
            input.put("state",sstate);
            input.put("zip",szip);
            input.put("mob",smob);
            input.put("password",pas1);
            input.put("token", token);

            input.put("lat",Double.toString(latitude));
            input.put("long",Double.toString(longitude));

            if(user_type.isChecked()){
                input.put("user_type", "customer");
            }else {
                input.put("user_type", "driver");
            }
            new SignUpAsyncTask(input).execute(url);
        }
    }

    private void handleSignUp(JSONObject result){

        //if(JSONObject["s"])
        try {
            int stscode = result.getInt("statusCode");

            if(stscode == 200){
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
            }else {
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

    public class SignUpAsyncTask  extends AsyncTask<String, Void, JSONObject> {
        String charset = "UTF-8";
        String requestURL;
        HashMap<String,String> formFields;
        String status = "";
        JSONObject httpStatus ;

        private Context mContext;
        //private TaskCompletedStatus mCallback;


        public SignUpAsyncTask( HashMap<String,String> formFields){
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
            handleSignUp(result);
        }
    }
}
