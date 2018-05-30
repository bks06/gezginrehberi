package com.example.alica.gezgin80;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import static android.content.Context.MODE_PRIVATE;

public class CustomAdapter extends BaseAdapter{


    private LayoutInflater layoutInflater;
    private ArrayList<SuggestionModel> suggestionModelsList;
    public CustomAdapter(Activity activity,ArrayList<SuggestionModel> suggestionModelsList){
        if(activity != null)
            layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.suggestionModelsList=suggestionModelsList;

    }
    public int getCount(){
        return suggestionModelsList.size();
    }

    @Override
    public Object getItem(int position) {
        return suggestionModelsList.get(getCount()-position-1);
    }

    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, final View convertView, final ViewGroup parent) {

        final SuggestionModel suggestionModel = (SuggestionModel)getItem(position);
        View satir = layoutInflater.inflate(R.layout.custom_satir,null);
        TextView header = (TextView) satir.findViewById(R.id.textViewHeader);
        TextView locations = (TextView) satir.findViewById(R.id.textViewLocations);
        TextView puan = (TextView) satir.findViewById(R.id.textViewPuan);
        final Button buttonIncele = (Button) satir.findViewById(R.id.btn_incele);
        header.setText(suggestionModel.getHeader());
        String locationNames = "1) " + suggestionModel.getLocationNames().get(0);
        for (int i = 1; i < suggestionModel.getLocationNames().size(); i++) {
            locationNames = locationNames + System.getProperty("line.separator") + (i + 1) + ") " + suggestionModel.getLocationNames().get(i);

        }
        locations.setText(locationNames);
        puan.setText("Puan: "+suggestionModel.getPoint());
        buttonIncele.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<String> locationNames = suggestionModel.getLocationNames();
                final List<String> locations = suggestionModel.getLocation();
                final Dialog dialog = new Dialog(parent.getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                /////make map clear
                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                dialog.setContentView(R.layout.dialogmap);////your custom content
                TextView dialogHeader = (TextView) dialog.findViewById(R.id.inceleHeader);
                TextView dialogExp = (TextView) dialog.findViewById(R.id.inceleExp);
                dialogHeader.setText(suggestionModel.getHeader());
                String locationExp = suggestionModel.getExp();
                int counter = 0;
                for (String location: locations) {
                    counter++;
                    locationExp = locationExp + System.getProperty("line.separator") + counter + ") " + locationNames.get(counter -  1);

                }
                dialogExp.setText(locationExp);
                MapView mMapView = (MapView) dialog.findViewById(R.id.mapView);
                MapsInitializer.initialize(parent.getContext());
                mMapView.onCreate(dialog.onSaveInstanceState());
                mMapView.onResume();
                mMapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(final GoogleMap googleMap) {
                        int counter = 0;
                        for (String location: locations) {
                            StringTokenizer st = new StringTokenizer(location, " ,");
                            LatLng posisiabsen = new LatLng(Float.parseFloat(st.nextToken()), Float.parseFloat(st.nextToken())); ////your lat lng
                            googleMap.addMarker(new MarkerOptions().position(posisiabsen).title(locationNames.get(counter)));
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(posisiabsen,15));
                            googleMap.getUiSettings().setZoomControlsEnabled(true);
                            counter++;
                        }
                    }
                });


                Button dialogButton = (Button) dialog.findViewById(R.id.btn_gotur);
// if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        // kullanıcıdan al puanı
                        LayoutInflater li = LayoutInflater.from(parent.getContext());
                        final View starView = li.inflate(R.layout.star, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                parent.getContext());
                        alertDialogBuilder.setView(starView);
                        final AlertDialog alertDialog = alertDialogBuilder.create();
                        Button  buttonpuanla = (Button) starView.findViewById(R.id.puanver);
                        buttonpuanla.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EditText input = (EditText) starView
                                        .findViewById(R.id.editTextDialogUserInput);
                                int verilenpuan = Integer.parseInt(input.getText().toString());
                                Toast.makeText(parent.getContext(), verilenpuan + " Puan Verildi.", Toast.LENGTH_SHORT).show();
                                Toast.makeText(parent.getContext(), "10 puan Gezgin Metrene eklendi." , Toast.LENGTH_SHORT).show();
                                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                DatabaseReference dbRef = firebaseDatabase.getReference("Suggestions");
                                DatabaseReference puanRef = dbRef.child(suggestionModel.getKey()).child("point");
                                puanRef.setValue(suggestionModel.getPoint() + verilenpuan);

                                dbRef = firebaseDatabase.getReference("users");
                                puanRef = dbRef.child(LoginActivity.userKey).child("point");
                                puanRef.setValue(LoginActivity.userPoint + 10);
                                alertDialog.dismiss();
                               /* LayoutInflater onerim = LayoutInflater.from(parent.getContext());
                                final View oneriView = onerim.inflate(R.layout.onerme, null,false);
                                final SuggestionModel suggestionModel1 = (SuggestionModel)getItem(1);
                                final SuggestionModel suggestionModel2 = (SuggestionModel)getItem(2);
                                TextView header1 = (TextView) oneriView.findViewById(R.id.textViewHeader1);
                                TextView locations1 = (TextView) oneriView.findViewById(R.id.textViewLocations1);
                                TextView puan1 = (TextView) oneriView.findViewById(R.id.textViewPuan1);
                                TextView header2 = (TextView) oneriView.findViewById(R.id.textViewHeader2);
                                TextView locations2 = (TextView) oneriView.findViewById(R.id.textViewLocations2);
                                TextView puan2 = (TextView) oneriView.findViewById(R.id.textViewPuan2);
                                final Button buttonIncele1 = (Button) oneriView.findViewById(R.id.btn_incele1);
                                final Button buttonIncele2 = (Button) oneriView.findViewById(R.id.btn_incele2);
                                header1.setText(suggestionModel1.getHeader());
                                header2.setText(suggestionModel2.getHeader());
                                String locationNames = "1) " + suggestionModel1.getLocationNames().get(0);
                                for (int i = 1; i < suggestionModel1.getLocationNames().size(); i++) {
                                    locationNames = locationNames + System.getProperty("line.separator") + (i + 1) + ") " + suggestionModel1.getLocationNames().get(i);

                                }
                                locations1.setText(locationNames);
                                puan1.setText("Puan: "+suggestionModel1.getPoint());
                                String locationNames2 = "1) " + suggestionModel2.getLocationNames().get(0);
                                for (int i = 1; i < suggestionModel2.getLocationNames().size(); i++) {
                                    locationNames2 = locationNames2 + System.getProperty("line.separator") + (i + 1) + ") " + suggestionModel2.getLocationNames().get(i);

                                }
                                locations2.setText(locationNames);
                                puan2.setText("Puan: "+suggestionModel2.getPoint());


                                AlertDialog.Builder alertDialogBuilder2 = new AlertDialog.Builder(
                                        parent.getContext());
                                alertDialogBuilder2.setView(oneriView);
                                alertDialogBuilder2
                                        .setCancelable(true);
                                buttonIncele1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {


                                    }
                                });
                                buttonIncele2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                });



                                AlertDialog alertDialog2 = alertDialogBuilder2.create();
                                alertDialog2.show();*/


                            }
                        });
                        alertDialogBuilder
                                .setCancelable(false);
                        alertDialog.show();



                        //////





                        String mainURL = "https://maps.google.ch/maps?&daddr=" + locations.get(0);
                        for(int i = 1; i < locations.size(); i++)
                        {
                            mainURL = mainURL + " to:" + locations.get(i);
                        }
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse(mainURL));
                        parent.getContext().startActivity(intent);
                        dialog.dismiss();

                    }
                });

                dialog.show();
                //Intent intent = new Intent(parent.getContext(),MapsActivity.class);
                //intent.putExtra("location",suggestionModel.getLocation());
                //parent.getContext().startActivity(intent);
            }
        });

        return satir;
    }
}