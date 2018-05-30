package com.example.alica.gezgin80;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    MarkerOptions userIndicator;
    private final static int REQUEST_lOCATION=90;
    String location;
    EditText head,exp,point;
    Button kaydet;
    FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activity_maps_mapview);
        mapFragment.getMapAsync(this);

        final Button btn_MapType=(Button) findViewById(R.id.activity_maps_btn_sat);
        btn_MapType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mMap.getMapType()==GoogleMap.MAP_TYPE_NORMAL){
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    btn_MapType.setText("NORMAL");
                }else{
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    btn_MapType.setText("UYDU");
                }

            }
        });


        Button btnGo=(Button) findViewById(R.id.activity_maps_btn_go);
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText etLocation=(EditText)findViewById(R.id.activity_maps_textview_location);
                InputMethodManager inputMethodManager =
                        (InputMethodManager) getSystemService(
                                MapsActivity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(
                        getCurrentFocus().getWindowToken(), 0);

                location = etLocation.getText().toString();
                if(location!=null && !location.equals("")){
                    List<Address> adressList = new ArrayList<>();
                    Geocoder geocoder=new Geocoder(MapsActivity.this);

                    try {
                        adressList = geocoder.getFromLocationName(location, 10);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = adressList != null && adressList.size() > 0  ? adressList.get(0) : null;
                    if (address != null){
                        LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
                        mMap.clear();
                        userIndicator = new MarkerOptions().position(latLng).title(location);
                        SharedPreferences sharedPref = getSharedPreferences("Settings", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        int counter = sharedPref.getInt("Counter", 0);
                        //editor.putInt("Counter", counter + 1);
                        editor.putFloat("latitude" + counter, (float)latLng.latitude);
                        editor.putFloat("longitude" + counter, (float)latLng.longitude);
                        editor.commit();
                        mMap.addMarker(userIndicator);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                    }
                }
            }
        });
        database = FirebaseDatabase.getInstance();
        SharedPreferences sharedPref = getSharedPreferences("Settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.commit();
        head= (EditText)findViewById(R.id.activity_maps_textview_location3);
        exp= (EditText)findViewById(R.id.activity_maps_textview_location2);
        kaydet = (Button)findViewById(R.id.activity_maps_btn_save);
        final DatabaseReference dbRef = database.getReference("Suggestions");
        Button btnSave = (Button) findViewById(R.id.activity_maps_btn_save);
        Button btnAdd = (Button) findViewById(R.id.activity_maps_btn_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  LayoutInflater li = LayoutInflater.from(getApplicationContext());
                  final View promptsView = li.inflate(R.layout.prompts, null);

                  AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                          MapsActivity.this);

                  alertDialogBuilder.setView(promptsView);
                  alertDialogBuilder
                          .setCancelable(false)
                          .setPositiveButton("Konum Ekle",
                                  new DialogInterface.OnClickListener() {
                                      public void onClick(DialogInterface dialog,int id) {
                                          EditText input = (EditText) promptsView
                                                  .findViewById(R.id.editTextDialogUserInput);
                                          String locationName = input.getText().toString();
                                          SharedPreferences sharedPref = getSharedPreferences("Settings", MODE_PRIVATE);
                                          SharedPreferences.Editor editor = sharedPref.edit();
                                          int counter = sharedPref.getInt("Counter", 0);
                                          editor.putString("location" + counter, locationName);
                                          editor.putInt("Counter", counter + 1);
                                          editor.commit();
                                          Toast.makeText(getApplicationContext(), locationName + " Konum eklendi.", Toast.LENGTH_SHORT).show();

                                      }
                                  })
                          .setNegativeButton("Iptal",
                                  new DialogInterface.OnClickListener() {
                                      public void onClick(DialogInterface dialog,int id) {
                                          dialog.cancel();
                                      }
                                  });

                  AlertDialog alertDialog = alertDialogBuilder.create();

                  // show it
                  alertDialog.show();
              }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference idRef=dbRef.push();
                String s_head,s_exp;
                int s_point;
                s_head = head.getText().toString();
                s_exp = exp.getText().toString();
                s_point = 30;
                if(!s_exp.equals("") && !s_head.equals("") && s_point != 0){
                    idRef.child("header").setValue(s_head);
                    idRef.child("exp").setValue(s_exp);
                    SharedPreferences sharedPref = getSharedPreferences("Settings", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    int counter = sharedPref.getInt("Counter", 0);
                    for (int i = 0; i < counter; i++)
                    {
                        String locationName = "location" + i;
                        String latName = "latitude" + i;
                        String lonName = "longitude" + i;
                        String location = sharedPref.getString(locationName, "");
                        float latitude = sharedPref.getFloat(latName, 0);
                        float longitude = sharedPref.getFloat(lonName, 0);
                        idRef.child(locationName).setValue(location);
                        idRef.child(latName).setValue(latitude);
                        idRef.child(lonName).setValue(longitude);
                    }
                    idRef.child("point").setValue(s_point);
                    head.setText("");
                    exp.setText("");
                    editor.clear();
                    editor.commit();
                    Toast.makeText(getApplicationContext(), "Konum kaydedildi.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Intent intent = getIntent();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            mMap.setMyLocationEnabled(true);
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_lOCATION);
            }
        }
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                mMap.clear();
                userIndicator = new MarkerOptions().position(point).title("Konum ");
                SharedPreferences sharedPref = getSharedPreferences("Settings", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                int counter = sharedPref.getInt("Counter", 0);
                //editor.putInt("Counter", counter + 1);
                editor.putFloat("latitude" + counter, (float)point.latitude);
                editor.putFloat("longitude" + counter, (float)point.longitude);
                editor.commit();
                mMap.addMarker(userIndicator);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point,15));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==REQUEST_lOCATION){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
                    mMap.setMyLocationEnabled(true);
                }
            }else{
                Toast.makeText(getApplicationContext(),"Kullanıcı konum iznini vermedi",Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
