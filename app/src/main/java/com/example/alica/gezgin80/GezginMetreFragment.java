package com.example.alica.gezgin80;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * A simple {@link Fragment} subclass.
 */
public class GezginMetreFragment extends Fragment {
    ArrayList<UserModel> userModelList ;

    ListView listView2;
    FirebaseDatabase firebaseDatabase;

    public GezginMetreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_gezgin_metre, container, false);
        userModelList = new ArrayList<UserModel>();
        listView2 = (ListView) view.findViewById(R.id.listView);
        firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference dbRef = firebaseDatabase.getReference("users");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    String name = ds.child("name").getValue().toString();
                    int Puan= Integer.parseInt(ds.child("point").getValue().toString());

                    userModelList.add(new UserModel(name,"dad",Puan,0));
                }
                ArrayList<UserModel> sorted = new ArrayList<UserModel>(userModelList);
                Collections.sort(sorted, new Comparator<UserModel>() {
                    @Override
                    public int compare(UserModel o1, UserModel o2) {
                        return o2.getPuan()-o1.getPuan();
                    }
                });
                CustomAdapterUser customAdapterUser = new CustomAdapterUser(getActivity(),sorted);
                listView2.setAdapter(customAdapterUser);
                dbRef.removeEventListener(this);



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Inflate the layout for this fragment
        return view;
    }

}
