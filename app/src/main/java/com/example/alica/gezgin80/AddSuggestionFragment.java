package com.example.alica.gezgin80;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddSuggestionFragment extends Fragment {

    EditText head,exp,point;
    Button kaydet;
    FirebaseDatabase database;
    public AddSuggestionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView= inflater.inflate(R.layout.fragment_add_suggestion, container, false);
            SharedPreferences sharedPref = ((MapsActivity)getActivity()).getSharedPreferences("Settings", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.clear();
            editor.commit();
            head= (EditText)rootView.findViewById(R.id.editTextHead);
            exp= (EditText)rootView.findViewById(R.id.editTextExp);
            point= (EditText)rootView.findViewById(R.id.editTextPoint);
            kaydet = (Button)rootView.findViewById(R.id.buttonEkle);
            database = FirebaseDatabase.getInstance();
            final DatabaseReference dbRef=database.getReference("Suggestions");
            kaydet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference idRef=dbRef.push();
                    String s_head,s_exp;
                    int s_point;
                    s_head = head.getText().toString();
                    s_exp = exp.getText().toString();
                    s_point = Integer.parseInt(point.getText().toString());
                    if(!s_exp.equals("") && !s_head.equals("") && s_point != 0){
                        idRef.child("header").setValue(s_head);
                        SharedPreferences sharedPref = ((MapsActivity)getActivity()).getSharedPreferences("Settings", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        int counter = sharedPref.getInt("Counter", 0);
                        for (int i = 0; i < counter; i++)
                        {
                            String latName = "latitude" + counter;
                            String lonName = "longitude" + counter;
                            float latitude = sharedPref.getFloat(latName, 0);
                            float longitude = sharedPref.getFloat(lonName, 0);
                            idRef.child(latName).setValue(latitude);
                            idRef.child(lonName).setValue(longitude);
                        }
                        idRef.child("point").setValue(s_point);
                        head.setText("");
                        exp.setText("");
                        point.setText("");
                        editor.clear();
                        editor.commit();
                    }
                    else{
                        Toast.makeText(rootView.getContext(),"Lütfen Tüm Alanları Doldurunuz",Toast.LENGTH_SHORT).show();
                    }
                }
            });


        return rootView;
    }

}
