package com.example.robin.gps_moudle;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
      Button start,stop;
      TextView text;
      String lat1,lon1;
      private BroadcastReceiver receiver;
      private RequestQueue requestQueue;
    Double lat,lon;
    String url;

    @Override
    protected void onResume() {
        super.onResume();
        if(receiver == null){
            receiver    =   new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    String action = intent.getAction();
                    if(action.equals("location_update")){
                        lat = intent.getExtras().getDouble("lat");
                        lon = intent.getExtras().getDouble("lon");
                        lat1 = String.valueOf(lat);
                        lon1 = String.valueOf(lon);
                        url = "https://maps.googleapis.com/maps/api/geocode/json?latlng="+lat1+","+lon1+"&key=AIzaSyBmGgmVQl7rBK44lkiLEBgf66JAr0ZIZMc";
                        //text.setText(lat1+" "+lon1);
                    }
                    JsonObjectRequest jsObjRequest = new JsonObjectRequest
                            (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        String address = response.getJSONArray("results").getJSONObject(1).getString("formatted_address");
                                        text.setText(address);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {

                                    text.setText("its not working ");

                                }
                            });
                    requestQueue.add(jsObjRequest);


                }
            };
        }
        registerReceiver(receiver,new IntentFilter("location_update"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(receiver != null){
            unregisterReceiver(receiver);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start = findViewById(R.id.start);
        stop = findViewById(R.id.stop);
        text = findViewById(R.id.coordinates);
        requestQueue = Volley.newRequestQueue(this);
        if(!runtimepermission()){
            enable_buttons();
        }

    }

    private void enable_buttons() {
      start.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              Intent i = new Intent(getApplicationContext(),GPS_Service.class);
              startService(i);
          }
      });

      stop.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
           Intent  i = new Intent(getApplicationContext(),GPS_Service.class);
           stopService(i);
          }
      });
    }

    private boolean runtimepermission() {
        if(Build.VERSION.SDK_INT >=23 && ContextCompat.checkSelfPermission(this, Manifest.permission.
        ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,Manifest.permission
        .ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                    10);
            return true;
        }
        return  false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 10){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
            {
                enable_buttons();
            }
            else
            {
                 runtimepermission();
            }
        }
    }
}
