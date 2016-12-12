package com.taf.raghu.doorpick;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;



/**
 * Created by Raghu on 12/9/2016.
 */

public class MultipartUtilityAsyncTask  extends AsyncTask<String, Void, JSONObject> {
    String charset = "UTF-8";
    String requestURL;
    HashMap<String,String> formFields;
    String status = "";
    JSONObject httpStatus ;

    private Context mContext;
    //private TaskCompletedStatus mCallback;


    public MultipartUtilityAsyncTask(Context context, HashMap<String,String> formFields){
        this.formFields = formFields;
        this.mContext = context;
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
    }
}
