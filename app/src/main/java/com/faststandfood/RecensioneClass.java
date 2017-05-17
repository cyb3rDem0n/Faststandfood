package com.faststandfood;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import static com.faststandfood.R.id.seekBar;

/**
 * Created by rekrux on 26/03/2017.
 */

public class RecensioneClass extends AppCompatActivity{

    ProgressDialog prog_dialog;
    Button insert_data;
    SeekBar seekbar_stelle;

    String item_id_chiosco;
    String item_id_utente;
    String item_info;
    String item_stelle;

    EditText et_id_chiosco;
    EditText et_id_utente;
    EditText et_testo_info;
    EditText et_stelle;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recensione_layout);

        et_id_chiosco = (EditText)findViewById(R.id.stand_id_text);
        et_id_utente = (EditText) findViewById(R.id.user_id_text);
        et_stelle = (EditText)findViewById(R.id.editText_stelle);
        et_testo_info = (EditText) findViewById(R.id.editText_rec);
        seekbar_stelle = (SeekBar) findViewById(seekBar);
        insert_data = (Button) findViewById(R.id.button_insert_rec);
    }



    public void insert(View v) {
//        prog_dialog.show();

        item_id_chiosco = "88";
                //et_id_chiosco.getText().toString();
        item_id_utente = "1111";
                //et_id_utente.getText().toString();
        item_info = "TESTO";
                //et_testo_info.getText().toString();
        item_stelle = "4";
                //et_stelle.getText().toString();

        //Toast.makeText(getApplicationContext(),item_id_chiosco+item_info, Toast.LENGTH_LONG ).show();


        String url = "http://faststandfood.altervista.org/insert_data.php";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        prog_dialog.dismiss();
                        et_id_chiosco.setText(item_id_chiosco);
                        et_id_utente.setText(item_id_utente);
                        et_testo_info.setText(item_info);
                        et_stelle.setText(item_stelle);

                        Toast.makeText(getApplicationContext(),
                                "Data Inserted Successfully",
                                Toast.LENGTH_SHORT).show();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                prog_dialog.dismiss();
                Toast.makeText(getApplicationContext(),
                        "failed to insert", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("item_id_chiosco", item_id_chiosco);
                params.put("item_id_utente", item_id_utente);
                params.put("item_info", item_info);
                params.put("item_stelle", item_stelle);
                return params;
            }
        };

        // Adding request to request queue
        MyApplication.getInstance().addToReqQueue(postRequest);
    }
}
