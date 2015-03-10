package com.hprotcennoc.frostic3.connectorph;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OrphanageFragmentLaunch extends android.support.v4.app.Fragment {

    //DATABASE STARTS HERE
    // Progress Dialog
    private ProgressDialog pDialog;
    private static String url_login_orph = "http://192.168.0.102/connectorph_php/login_orph.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    //DATABASE CONTINUES LATER

    View view;
    Button btnlogin;
    Button btnreg;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.launch_view_orphanage, container, false);

        btnlogin = (Button) view.findViewById(R.id.lv_orphanage_login_button);
        btnreg = (Button) view.findViewById(R.id.lv_orphanage_register_button);
        //Login Listener
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // get user details from email and password in background thread
                new getOrphByEmailAndPassword().execute();
            }
        });
        //Register Listener
        btnreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent OrphRegFormIntent = new Intent(getActivity(), OrphRegForm.class);
                startActivity(OrphRegFormIntent);
            }
        });

        return view;
    }

    //DATABASE CONTINUES HERE
    /**
     * Background Async Task to getOrphByEmailAndPassword
     * */
    class getOrphByEmailAndPassword extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        int flag=0;
        String message;
        @Override
        protected void onPreExecute() {
            Log.i("OrphanageFragmentLaunch", "In onPreExecute");
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Logging in..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Sending email and password for verification
         * */
        protected String doInBackground(String... args) {
            Log.i("OrphanageFragmentLaunch","In doInBackground");

            EditText email = (EditText) view.findViewById(R.id.view_orphanage_orph_id_ET);
            EditText password = (EditText) view.findViewById(R.id.view_orphanage_password_ET);
            String demail = email.getText().toString();
            String dpassword = password.getText().toString();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("email", demail));
            params.add(new BasicNameValuePair("password", dpassword));

            Log.i("OrphanageFragmentLaunch","In doInBackground1");
            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = JSONParser.makeHttpRequest(url_login_orph,
                    "POST", params);

            Log.i("OrphanageFragmentLaunch","In doInBackground2");
            // check log cat from response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // successfully logged in
                    Intent UserProfileIntent = new Intent(getActivity(), OrphProfile.class);
                    startActivity(UserProfileIntent);

                } else {
                    // failed to login
                    message = json.getString(TAG_MESSAGE);
                    flag=1;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
            //If login error occurs, display the error
            if(flag == 1) {
                Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_LONG);
                toast.show();
            }
        }

    }
    //DATABASE ENDS HERE
}
