package com.faststandfood;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Created by rekrux on 14/04/2017.
 */

public class StandProfile extends AppCompatActivity implements OnMapReadyCallback {

    TextView nome;
    TextView città;
    TextView telefono;
    TextView lat_longi;
    TextView regione;
    ProgressBar valutaz;

    String string_nome = "NON TROTVATO";
    String string_cit = "NON TROTVATO";
    String string_tel = "NON TROTVATO";
    String string_reg = "NON TROTVATO";

    int string_val = 0;

    double lat_s = 0;
    double long_s = 0;

    double latitude;
    double longitude;

    GPSTracker gps = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stand_profile);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        gps = new GPSTracker(StandProfile.this);

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

        nome = (TextView) findViewById(R.id.nome_stand);
        città = (TextView) findViewById(R.id.citta_stand);
        telefono = (TextView) findViewById(R.id.telefono_stand);
        lat_longi = (TextView) findViewById(R.id.coordinate_stand);
        regione = (TextView) findViewById(R.id.regione_stand);
        valutaz = (ProgressBar) findViewById(R.id.progressBar2);

        Intent getInfo = getIntent();

        string_nome = getInfo.getStringExtra("OBJ_STAND");
        string_cit = getInfo.getStringExtra("OBJ_CITTA");
        string_reg = getInfo.getStringExtra("OBJ_REG");
        string_tel = getInfo.getStringExtra("OBJ_CELL");

        lat_s = Double.parseDouble(getInfo.getStringExtra("OBJ_LAT"));
        long_s = Double.parseDouble(getInfo.getStringExtra("OBJ_LONG"));

        string_val = Integer.parseInt(getInfo.getStringExtra("OBJ_VAL"));

        set_values(string_nome, string_cit, string_reg, string_tel);

    }

    void set_values(String n, String c, String r, String t) {
        nome.append(n);
        città.append(c);
        regione.append(r);
        telefono.append(t);

        valutaz.setProgress(string_val);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        LatLng mylatLng = new LatLng(latitude, longitude);
        LatLng latLng = new LatLng(lat_s, long_s);

        googleMap.addMarker(new MarkerOptions().position(latLng).title(string_nome));

        // setta zoom a 16
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
        // setta zoom a 15
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(13), 2000, null);

        //googleMap.moveCamera(CameraUpdateFactory.newLatLng(mylatLng));
        //googleMap.animateCamera(CameraUpdateFactory.zoomIn());
        //googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

        /*
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)// Sets the center of the map to Mountain View
                .zoom(5) // Sets the zoom
                .bearing(90)// Sets the orientation of the camera to east
                .tilt(30)// Sets the tilt of the camera to 30 degrees
                .build();// Creates a CameraPosition from the builder
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        Polyline polyline = googleMap.addPolyline(new PolylineOptions().clickable(true).add(
                new LatLng(latitude, longitude),
                new LatLng(lat_s, long_s)));
*/
    }

    public void onClick(View view){
        Intent intent = new Intent(getBaseContext(), PositionActivity.class);
        startActivity(intent);
    }
}
