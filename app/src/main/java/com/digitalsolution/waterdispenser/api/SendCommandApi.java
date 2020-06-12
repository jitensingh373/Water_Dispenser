package com.digitalsolution.waterdispenser.api;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;

public class SendCommandApi extends AsyncTask<String, Void, String> {

    private Context mContext;
    private Handler mHandler;
    private String response;
    private String url;
    private HashMap<String, String> loginResponse;
    private ProgressDialog progressBar;


    public SendCommandApi(Context context, String url) {
        this.mContext = context;
        //this.mHandler = handler;
        this.url = url;
       }

    @Override
    protected void onPreExecute() {
        progressBar = new ProgressDialog(mContext);
        progressBar.setMessage("Please wait..");
        progressBar.show();
    }

    @Override
    protected String doInBackground(String... params) {
        return sendLoginRequest();
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        Log.d("RES:",response);
        Toast.makeText(mContext,response,Toast.LENGTH_LONG).show();
        if(progressBar != null && progressBar.isShowing()){
            progressBar.dismiss();
        }
    }

    private String sendLoginRequest() {
        String COUNTRY_URL = "http://" + url;
        response = HttpRequest.sendGetRequestJSON(mContext, COUNTRY_URL, "".getBytes());
        return validateResponse(response);
    }

    private String validateResponse(String response) {
        HashMap<String, String> loginResponse = new HashMap<>();
        Log.d("CountryAPIRes",response);
        return response;
    }
}
