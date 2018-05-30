package com.example.alica.gezgin80;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class SuggestionsFragment extends Fragment {
    ArrayList<SuggestionModel> suggestionModels;
    ArrayList<SuggestionModel> sıralı;
    Map<Integer,SuggestionModel> sorted = new TreeMap<Integer, SuggestionModel>(new Comparator<Integer>() {
        @Override
        public int compare(Integer o1, Integer o2) {
            return o2.compareTo(o1);
        }
    });

    ListView fragment_suggestions_listView;
    FirebaseDatabase firebaseDatabase;

    public SuggestionsFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_suggestions, container, false);
        suggestionModels = new ArrayList<SuggestionModel>();
        fragment_suggestions_listView = (ListView) view.findViewById(R.id.fragment_suggestions_listView);

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
                    int  puan = Integer.parseInt(ds.child("point").getValue().toString());
                    suggestionModels.add(new SuggestionModel(ds.getKey(), header, exp, locationNames, locations, puan));
                }
                //sorted suan da sıralı tutuyor
                ArrayList<SuggestionModel> sirali = new ArrayList<SuggestionModel>(suggestionModels);
                Collections.sort(sirali, new Comparator<SuggestionModel>() {
                    @Override
                    public int compare(SuggestionModel o1, SuggestionModel o2) {
                        return o1.getPoint() - o2.getPoint();
                    }
                });


                    // for ile 10 tanesini başka bir listeye geçirip yapabilirsin:D
                CustomAdapter adapter=new CustomAdapter(getActivity(),sirali);
                if(fragment_suggestions_listView != null)
                    fragment_suggestions_listView.setAdapter(adapter);



                dbRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return view;
    }
}
