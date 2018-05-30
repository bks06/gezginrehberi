package com.example.alica.gezgin80;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewestSuggestionsFragment extends Fragment {
    ArrayList<SuggestionModel> suggestionModels;

    ListView listView3;
    FirebaseDatabase firebaseDatabase;
    public NewestSuggestionsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_suggestions, container, false);
        suggestionModels = new ArrayList<SuggestionModel>();
        listView3 = (ListView) view.findViewById(R.id.fragment_suggestions_listView);

        firebaseDatabase =FirebaseDatabase.getInstance();
        final DatabaseReference dbRef = firebaseDatabase.getReference("Suggestions");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    String header = ds.child("header").getValue().toString();
                    String exp = ds.child("exp").getValue().toString();
                    List<String> locationNames = new ArrayList<>();
                    List<String> locations = new ArrayList<>();
                    try
                    {
                        for (int i = 0; i < 5; i++)
                        {
                            locationNames.add(ds.child("location"+i).getValue().toString());
                            locations.add(ds.child("latitude"+i).getValue().toString() + "," + ds.child("longitude"+i).getValue().toString());
                        }
                    }
                    catch (Exception e) {
                    }
                    int puan = Integer.parseInt(ds.child("point").getValue().toString());
                    suggestionModels.add(new SuggestionModel(ds.getKey(), header, exp, locationNames, locations,puan));
                }

                CustomAdapter adapter=new CustomAdapter(getActivity(),suggestionModels);
                if(listView3 != null)
                    listView3.setAdapter(adapter);
                dbRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

}
