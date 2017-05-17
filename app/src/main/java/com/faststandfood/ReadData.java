package com.faststandfood;

import android.*;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ReadData extends AppCompatActivity {

    String url = "http://faststandfood.altervista.org/read_data.php";
    ArrayList<HashMap<String, String>> Item_List;
    ProgressDialog PD;
    ListAdapter adapter;

    ListView listview = null;
    //RatingBar rate_bar;
    LayoutInflater inflater;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private GoogleMap map;


    // JSON Node names
    public static final String ITEM_ID = "id";
    public static final String ITEM_NAME = "nome";
    public static final String ITEM_LAT = "lat";
    public static final String ITEM_LONG = "long";
    public static final String ITEM_REG = "reg";
    public static final String ITEM_CITTA = "citta";
    public static final String ITEM_TEL = "telefono";
    public static final String ITEM_VALUTAZ = "valutazione";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        setContentView(R.layout.activity_read_db);

       // inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       // View list = inflater.inflate(R.layout.list_items, null);
        //rate_bar = (RatingBar)list.findViewById(R.id.item_valutaz);

        listview = (ListView) findViewById(R.id.response_list);
        Item_List = new ArrayList<HashMap<String, String>>();

        ReadDataFromDB();
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }


    private void ReadDataFromDB() {

        PD = new ProgressDialog(this);
        PD.setMessage("Loading.....");
        PD.show();

        JsonObjectRequest jreq = new JsonObjectRequest(Method.GET, url,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int success = response.getInt("success");

                            if (success == 1) {
                                JSONArray ja = response.getJSONArray("chioschi");

                                for (int i = 0; i < ja.length(); i++) {

                                    JSONObject jobj = ja.getJSONObject(i);

                                        HashMap<String, String> item = new HashMap<String, String>();
                                    item.put(ITEM_ID, jobj.getString(ITEM_ID));
                                    item.put(ITEM_NAME, jobj.getString(ITEM_NAME));
                                    item.put(ITEM_LAT, jobj.getString(ITEM_LAT));
                                    item.put(ITEM_LONG, jobj.getString(ITEM_LONG));
                                    item.put(ITEM_REG, jobj.getString(ITEM_REG));
                                    item.put(ITEM_CITTA, jobj.getString(ITEM_CITTA));
                                    item.put(ITEM_TEL, jobj.getString(ITEM_TEL));
                                    item.put(ITEM_VALUTAZ, jobj.getString(ITEM_VALUTAZ));

                                    Item_List.add(item);

                                } // for loop ends

                                String[] from = {ITEM_NAME, ITEM_REG, ITEM_CITTA, ITEM_VALUTAZ
                                };
                                int[] to = {R.id.item_name, R.id.item_reg, R.id.item_citta, R.id.item_valutaz
                                };

                                adapter = new SimpleAdapter(
                                        getApplicationContext(), Item_List,
                                        R.layout.list_items, from, to);

                                listview.setAdapter(adapter);
                                listview.setOnItemClickListener(new ListitemClickListener());
                                //setValutazione();
                                PD.dismiss();

                            } // if ends

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                PD.dismiss();
            }
        });

        // Adding request to request queue
        MyApplication.getInstance().addToReqQueue(jreq);

    }

    private void setValutazione() {
        JsonObjectRequest jreq_ = new JsonObjectRequest(Method.GET, url,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int success = response.getInt("success");

                            if (success == 1) {
                                JSONArray ja = response.getJSONArray("chioschi");

                                for (int i = 0; i < ja.length(); i++) {

                                    JSONObject jobj = ja.getJSONObject(i);

                                    if (jobj.getString(ITEM_VALUTAZ).equals("4"))
                                        Toast.makeText(getApplicationContext(),jobj.getString(ITEM_VALUTAZ), Toast.LENGTH_LONG).show();
                                    //rate_bar.setRating(4); //non funziona
                                    /*
                                    if(stars == 1){
                                        rate_bar_1.setVisibility(View.VISIBLE);
                                        rate_bar_2.setVisibility(View.GONE);
                                        rate_bar_3.setVisibility(View.GONE);
                                        rate_bar_4.setVisibility(View.GONE);
                                        rate_bar_5.setVisibility(View.GONE);
                                    }else
                                    if(stars == 2) {
                                        rate_bar_1.setVisibility(View.GONE);
                                        rate_bar_2.setVisibility(View.VISIBLE);
                                        rate_bar_3.setVisibility(View.GONE);
                                        rate_bar_4.setVisibility(View.GONE);
                                        rate_bar_5.setVisibility(View.GONE);
                                    }else
                                    if(stars == 3) {
                                        rate_bar_1.setVisibility(View.GONE);
                                        rate_bar_2.setVisibility(View.GONE);
                                        rate_bar_3.setVisibility(View.VISIBLE);
                                        rate_bar_4.setVisibility(View.GONE);
                                        rate_bar_5.setVisibility(View.GONE);
                                    }else
                                    if(stars == 4) {
                                        rate_bar_1.setVisibility(View.GONE);
                                        rate_bar_2.setVisibility(View.GONE);
                                        rate_bar_3.setVisibility(View.GONE);
                                        rate_bar_4.setVisibility(View.VISIBLE);
                                        rate_bar_5.setVisibility(View.GONE);
                                    }else
                                    if(stars == 5) {
                                        rate_bar_1.setVisibility(View.GONE);
                                        rate_bar_2.setVisibility(View.GONE);
                                        rate_bar_3.setVisibility(View.GONE);
                                        rate_bar_4.setVisibility(View.GONE);
                                        rate_bar_5.setVisibility(View.VISIBLE);
                                    }
                                    */
                                } // for loop ends
                                PD.dismiss();

                            } // if ends

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                PD.dismiss();
            }
        });

        // Adding request to request queue
        MyApplication.getInstance().addToReqQueue(jreq_);

    }
    class ListitemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            Intent modify_intent = new Intent(ReadData.this, StandProfile.class);
            modify_intent.putExtra("OBJ_STAND", Item_List.get(position).get(ITEM_NAME));
            modify_intent.putExtra("OBJ_CITTA", Item_List.get(position).get(ITEM_CITTA));
            modify_intent.putExtra("OBJ_REG", Item_List.get(position).get(ITEM_REG));
            modify_intent.putExtra("OBJ_CELL", Item_List.get(position).get(ITEM_TEL));
            modify_intent.putExtra("OBJ_LAT", Item_List.get(position).get(ITEM_LAT));
            modify_intent.putExtra("OBJ_LONG", Item_List.get(position).get(ITEM_LONG));
            modify_intent.putExtra("OBJ_VAL", Item_List.get(position).get(ITEM_VALUTAZ));

            startActivity(modify_intent);

        }
    }
}