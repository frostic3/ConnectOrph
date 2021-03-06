package com.hprotcennoc.frostic3.connectorph.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.hprotcennoc.frostic3.connectorph.AddDonation;
import com.hprotcennoc.frostic3.connectorph.R;
import com.hprotcennoc.frostic3.connectorph.library.JSONParser;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserDonateFeedFragment extends ListFragment {

    // Progress Dialog
    private ProgressDialog pDialog;

    ArrayList<HashMap<String, String>> donationsList;

    // url to get all products list
    private static String url_feed_donation = "http://connectorph.byethost11.com/connectorph_php/feed_donation.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_DONATIONS = "donations";
    private static final String TAG_DONATIONID = "donationid";
    private static final String TAG_CREATED_AT = "created_at";
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_SUB_CATEGORY = "subCategory";
    private static final String TAG_DESC = "description";
    private static final String TAG_NUM_OF_ITEMS = "numberOfItems";
    private static final String TAG_ADDRESS_LINE_1 = "caddress1";
    private static final String TAG_ADDRESS_LINE_2 = "caddress2";
    private static final String TAG_STATE = "cstate";
    private static final String TAG_CITY = "ccity";
    // products JSONArray
    JSONArray donationsArray = null;

    View rootView;
    Button makeADontaion;
    String demail;

    public UserDonateFeedFragment(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_user_donate_feed, container, false);

        demail = getActivity().getIntent().getStringExtra("email");
        // Hashmap for ListView
        donationsList = new ArrayList<>();
        // Loading products in Background Thread
        new LoadAllProducts().execute();

        makeADontaion = (Button) rootView.findViewById(R.id.fragment_user_donate_feed_buttonbar);
        makeADontaion.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent AddDonationIntent = new Intent(getActivity(), AddDonation.class);
                AddDonationIntent.putExtra("email", demail);
                startActivityForResult(AddDonationIntent, 1);
            }
        });
        return rootView;
    }

    //Recreate activity on Result
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if(data.getStringExtra("choice").equals("YES"))
                getActivity().recreate();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Get listview
        //Listview lv = getListView();
        // on selecting single product
        /*lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                getActivity().recreate();
            }
        });*/
    }

    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadAllProducts extends AsyncTask<String, String, String> {

        int flag=0;
        String message;
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Loading. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<>();
            // getting JSON string from URL
            JSONObject json = JSONParser.makeHttpRequest(url_feed_donation, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("All Donations: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found
                    // Getting Array of Donations
                    donationsArray = json.getJSONArray(TAG_DONATIONS);

                    // looping through All Donations
                    for (int i = 0; i < donationsArray.length(); i++) {
                        JSONObject c = donationsArray.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.optString(TAG_DONATIONID);
                        String created_at = c.optString(TAG_CREATED_AT);
                        String category = c.optString(TAG_CATEGORY);
                        String subCategory = c.optString(TAG_SUB_CATEGORY);
                        String desc = c.optString(TAG_DESC);
                        String numOfItems = c.optString(TAG_NUM_OF_ITEMS);
                        String address1 = c.optString(TAG_ADDRESS_LINE_1);
                        String address2 = c.optString(TAG_ADDRESS_LINE_2);
                        String state = c.optString(TAG_STATE);
                        String city = c.optString(TAG_CITY);


                        // Converting timestamp into x ago format
                        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                                Long.parseLong(created_at) * 1000,
                                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_DONATIONID, id);
                        map.put(TAG_CREATED_AT, timeAgo.toString());
                        map.put(TAG_CATEGORY, category);
                        map.put(TAG_SUB_CATEGORY, subCategory);
                        map.put(TAG_DESC, desc);
                        map.put(TAG_NUM_OF_ITEMS, numOfItems);
                        map.put(TAG_ADDRESS_LINE_1, address1);
                        map.put(TAG_ADDRESS_LINE_2, address2);
                        map.put(TAG_STATE, state);
                        map.put(TAG_CITY, city);

                        // adding HashList to ArrayList
                        donationsList.add(map);
                    }
                } else {
                    // no Donations found
                    message = json.getString(TAG_MESSAGE);
                    flag=1;
                    // Launch Add New Donations Activity
                    //Intent i = new Intent(getActivity(), NewProductActivity.class);
                    // Closing all previous activities
                    //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    //startActivity(i);
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
            // dismiss the dialog after getting all products
            pDialog.dismiss();

            if(flag == 1) {
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }
            // updating UI from Background Thread
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                     ListAdapter adapter = new SimpleAdapter( getActivity(), donationsList, R.layout.list_item_user_donation_feed,
                            new String[] { TAG_DONATIONID, TAG_CREATED_AT, TAG_CATEGORY, TAG_SUB_CATEGORY, TAG_DESC, TAG_NUM_OF_ITEMS},
                            new int[] { R.id.LT_donationid, R.id.LT_timestamp, R.id.LT_category, R.id.LT_subCategory, R.id.LT_description, R.id.LT_numOfItems });
                    // updating listview
                    setListAdapter(adapter);
                }
            });

        }

    }

}
