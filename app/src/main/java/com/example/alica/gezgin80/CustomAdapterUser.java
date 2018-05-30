package com.example.alica.gezgin80;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by alica on 8.05.2018.
 */

public class CustomAdapterUser extends BaseAdapter {
    LayoutInflater layoutInflater;
    ArrayList<UserModel> userModels;
    public CustomAdapterUser(Activity activity, ArrayList<UserModel> userModels){
        layoutInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.userModels=userModels;

    }
    @Override
    public int getCount() {
        return userModels.size();
    }

    @Override
    public Object getItem(int position) {
        return userModels.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UserModel userModel = userModels.get(position);
        View userSatir = layoutInflater.inflate(R.layout.custom_satir_user,null);
        TextView head = (TextView) userSatir.findViewById(R.id.textViewUserName);
        TextView point = (TextView) userSatir.findViewById(R.id.textViewUserPoint);
        head.setText(userModel.getName());
        point.setText("Puanı: "+Integer.toString(userModel.getPuan()));
        return userSatir;
    }
}
