package com.faststandfood;

        import android.*;
        import android.Manifest;
        import android.app.ProgressDialog;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.location.Location;
        import android.os.Build;
        import android.os.Bundle;
        import android.support.annotation.IntegerRes;
        import android.support.annotation.NonNull;
        import android.support.annotation.Nullable;
        import android.support.v4.app.ActivityCompat;
        import android.support.v4.content.ContextCompat;
        import android.support.v7.app.AppCompatActivity;
        import android.util.Log;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.ListAdapter;
        import android.widget.ListView;
        import android.widget.SimpleAdapter;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.android.volley.Request;
        import com.android.volley.Response;
        import com.android.volley.VolleyError;
        import com.android.volley.toolbox.JsonObjectRequest;
        import com.google.android.gms.common.ConnectionResult;
        import com.google.android.gms.common.api.GoogleApiClient;
        import com.google.android.gms.location.LocationListener;
        import com.google.android.gms.location.LocationRequest;
        import com.google.android.gms.location.LocationServices;
        import com.google.android.gms.maps.CameraUpdateFactory;
        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.OnMapReadyCallback;
        import com.google.android.gms.maps.SupportMapFragment;
        import com.google.android.gms.maps.UiSettings;
        import com.google.android.gms.maps.model.LatLng;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.util.ArrayList;
        import java.util.Collection;
        import java.util.Collections;
        import java.util.Comparator;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.LinkedList;
        import java.util.List;
        import java.util.ListIterator;

/**
 * Created by rekrux on 03/04/2017.
 */

public class NearFoodActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,OnMapReadyCallback,LocationListener {

    LocationRequest myLocationRequest;
    Location myLastLocation;
    GoogleApiClient mGoogleApiClient;


    ListView listview;
    ArrayList<HashMap<String, String>> Item_List;

    ListAdapter adapter;

    public ProgressDialog pDialog;

    String url_cibo = "http://faststandfood.altervista.org/read_cibo.php";
    String url_chiosco = "http://faststandfood.altervista.org/read_data.php";

    String val_ciboScelto;

    //nodi json tab_chioschi
    public static final String ITEM_ID_STAND = "id";
    public static final String ITEM_LAT = "lat";
    public static final String ITEM_LONG = "long";
    //public static final String ITEM_STARS = "valutazione";
// JSON Node names
    public static final String ITEM_ID = "id";
    public static final String ITEM_NAME = "nome";
    public static final String ITEM_REG = "reg";
    public static final String ITEM_CITTA = "citta";
    public static final String ITEM_TEL = "telefono";
    public static final String ITEM_VALUTAZ = "valutazione";

    // nodi json tab_cibi
    public static final String ITEM_VAL_TIPO = "val_tipo";
    public static final String ITEM_ID_STAND_CIBO = "id_chiosco";
    //public static final String ITEM_INFO_CIBO = "info_cibo";


    TextView nearStand;
    String attach;
    GPSTracker gps = null;

    double latitude;
    double longitude;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private GoogleMap map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fast_stand);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        listview = (ListView) findViewById(R.id.response_list);
        Item_List = new ArrayList<>();

        pDialog = new ProgressDialog(this, R.style.AppTheme);

        pDialog.setMessage("Aspetta...");
        pDialog.setCancelable(false);

        val_ciboScelto = getIntent().getStringExtra("scelta");

        gps = new GPSTracker(NearFoodActivity.this);

        // Check if GPS enabled
        if (gps.canGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();


            // \n is for new line
            Toast.makeText(getApplicationContext(),
                    "Your Location is - \nLat: " + latitude +
                            "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        } else {
            // Can't get location.
            // GPS or network is not enabled.
            // Ask user to enable GPS/network in settings.
            gps.showSettingsAlert();
        }
        fastSearch();
    }

    public void onLocationChanged(Location location) {

        myLastLocation = location;
        if (mGoogleApiClient != null) {
            //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (LocationListener) this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        map.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                map.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            map.setMyLocationEnabled(true);
        }

        UiSettings myUiSettings = googleMap.getUiSettings();
        myUiSettings.setZoomControlsEnabled(true);
        myUiSettings.setCompassEnabled(true);

        map.animateCamera(CameraUpdateFactory.zoomTo(11));

        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(11), 2000, null);

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

    private void startLocationUpdates() {

        Log.i("TAG", "StartLocationUpdates");

        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission
                    (getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                        myLocationRequest, this);

            }
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    myLocationRequest, this);
        }

    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    // cerco, a seconda della scelta del cibo, il chisco più vicino che
    // vende quel cibo
    private void fastSearch() {
        showpDialog();
        JsonObjectRequest jreq = new JsonObjectRequest(Request.Method.GET, url_cibo,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int success = response.getInt("success");

                            if (success == 1) {
                                JSONArray ja = response.getJSONArray("cibi");

                                //String id_stand, tipo_cibo;

                                for (int i = 0; i < ja.length(); i++) {

                                    JSONObject jobj = ja.getJSONObject(i);

                                    if (jobj.getString(ITEM_VAL_TIPO).equals(val_ciboScelto)) {
                                        attach = (jobj.getString(ITEM_ID_STAND_CIBO) + "-");
                                        //nearStand.append(attach);

                                        fastSearch_phase2(attach);
                                        //id_stand = "ID STAND = "+ jobj.getString(ITEM_ID_STAND_CIBO);
                                        //tipo_cibo = "TIPO CIBO = "+ jobj.getString(ITEM_INFO_CIBO);

                                        //nearStand.append(id_stand + " " + tipo_cibo+"\n");

                                    }
                                }

                                hidepDialog();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hidepDialog();
            }
        });

        // Adding request to request queue
        MyApplication.getInstance().addToReqQueue(jreq);

    }
    ///////////////////////////////////////////////////

    private void fastSearch_phase2(final String string) {

        JsonObjectRequest jreq = new JsonObjectRequest(Request.Method.GET, url_chiosco,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int success = response.getInt("success");

                            if (success == 1) {
                                JSONArray ja = response.getJSONArray("chioschi");

                                String lat = null;
                                String lon = null;

                                String s = null;

                                double lat_s = 0;
                                double long_s = 0;

                                Integer distance = 0;

                                ArrayList<Integer> distance_list = new ArrayList<Integer>();

                                String[] id_list = string.split("\\-", -1);

                                for (int i = 0; i < ja.length(); i++) {

                                    JSONObject jobj = ja.getJSONObject(i);

                                    HashMap<String, String> item = new HashMap<>();
                                    item.put(ITEM_ID, jobj.getString(ITEM_ID));
                                    item.put(ITEM_NAME, jobj.getString(ITEM_NAME));
                                    item.put(ITEM_LAT, jobj.getString(ITEM_LAT));
                                    item.put(ITEM_LONG, jobj.getString(ITEM_LONG));
                                    item.put(ITEM_REG, jobj.getString(ITEM_REG));
                                    item.put(ITEM_CITTA, jobj.getString(ITEM_CITTA));
                                    item.put(ITEM_TEL, jobj.getString(ITEM_TEL));
                                    item.put(ITEM_VALUTAZ, jobj.getString(ITEM_VALUTAZ));

                                    Item_List.add(item);

                                    for (String anId_list : id_list) {
                                        if (jobj.getString(ITEM_ID_STAND).equals(anId_list)) {
                                            lat = "Latitudine " + jobj.getString(ITEM_LAT);
                                            lon = "Longitudine " + jobj.getString(ITEM_LONG);

                                            lat_s = Double.parseDouble(jobj.getString(ITEM_LAT));
                                            long_s = Double.parseDouble(jobj.getString(ITEM_LONG));

                                            LatLng c_stand = new LatLng(lat_s, long_s);
                                            LatLng mypos = new LatLng(latitude, longitude);
                                            distance = (int) PositionActivity.distanceTo(mypos, c_stand);

                                            distance_list.add(distance);

                                            {
                                                Collections.sort(distance_list);

                                                for (Integer d : distance_list)
                                                    if (d < 3) {


                                                String[] from = {ITEM_NAME, ITEM_REG, ITEM_CITTA, ITEM_VALUTAZ};
                                                int[] to = {R.id.item_name, R.id.item_reg, R.id.item_citta, R.id.item_valutaz
                                                };

                                                adapter = new SimpleAdapter(
                                                        getApplicationContext(), Item_List,
                                                        R.layout.list_items, from, to);
                                            }

                                                //nearStand.append(" VICINO \n");
                                            }listview.setAdapter(adapter);
                                            listview.setOnItemClickListener(new ListitemClickListener());
                                        }

                                        //s = "CHIOSCO : " + lat + " - " + lon + "\n";
                                    }
                                }
                                //nearStand.append("UTENTE " + latitude + " - " + longitude + "\n\n");


                                // occorre passare un array di coordinate non una sola coordinata
                                //Intent intent = new Intent(getBaseContext(), PositionActivity.class);
                                //intent.putExtra("LAT",lat);
                                //intent.putExtra("LONG",lon);
                                //startActivity(intent);
                                //lat +  " - " + lon + "\n\n" +


                                hidepDialog();

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hidepDialog();
            }
        });

        // Adding request to request queue
        MyApplication.getInstance().addToReqQueue(jreq);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    class ListitemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            Intent modify_intent = new Intent(NearFoodActivity.this, StandProfile.class);
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
