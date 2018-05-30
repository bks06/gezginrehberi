package com.example.alica.gezgin80;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    JSONObject response, profile_pic_data, profile_pic_url;
    TextView user_name, user_email;
    ImageView user_picture;
    NavigationView navigation_view;
    public int logcheck=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {//logini nerde aciyon, login intentini, programi login ile mi baslatiyorsun
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       /* if(logcheck == 0){
            Intent loginintent = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(loginintent);
        }*/
        Intent intent = getIntent();
        int logvalue = intent.getIntExtra("logcheck",0);
        String JsonData = intent.getStringExtra("jsondata");
       // if(logvalue == 1){
       //     logcheck = 1;





        setNavigationHeader();
        setUserProfile(JsonData);

        SuggestionsFragment top10fragment=new SuggestionsFragment();
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container,top10fragment);
        ft.commit();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //navigation_view = (NavigationView) findViewById(R.id.nav_view);
        navigation_view.setNavigationItemSelectedListener(this);
        // navigation_view.removeHeaderView(findViewById(R.id.nav_view));

      //  }
        ListView asd = (ListView) findViewById(R.id.fragment_suggestions_listView);


    }

    private void setNavigationHeader() {
         navigation_view = (NavigationView) findViewById(R.id.nav_view);
         View header = LayoutInflater.from(this).inflate(R.layout.nav_header_home,null);
         navigation_view.addHeaderView(header);
         user_name = (TextView) header.findViewById(R.id.username);
         user_picture=(ImageView)header.findViewById(R.id.profile_pic);
         user_email= (TextView)header.findViewById(R.id.useremail);

    }

    private void setUserProfile(String object){
        try{
            response = new JSONObject(object);
            user_email.setText(response.get("email").toString());
            user_name.setText(response.get("name").toString());
            profile_pic_data = new JSONObject(response.get("picture").toString());
            profile_pic_url =new JSONObject(profile_pic_data.getString("data"));
            Picasso.with(this).load(profile_pic_url.getString("url")).into(user_picture);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



    @Override
    public void onBackPressed() {//drawer_layout hangisi
        finish();
    }//iki sorunu da dene

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_TopSuggestion) {
            SuggestionsFragment top10fragment=new SuggestionsFragment();
            FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container,top10fragment);
            ft.commit();
            // Handle the camera action
        } else if (id == R.id.nav_NewestSuggestion) {

            NewestSuggestionsFragment newestfragment=new NewestSuggestionsFragment();
            FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container,newestfragment);
            ft.commit();

        } else if (id == R.id.nav_GezginMetre) {
            GezginMetreFragment gezginMetreFragment=new GezginMetreFragment();
            FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container,gezginMetreFragment);
            ft.commit();

        } else if (id == R.id.nav_AddSuggestion) {
            /*AddSuggestionFragment addSuggestionFragment=new AddSuggestionFragment();
            FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container,addSuggestionFragment);
            ft.commit();*/
            Intent s = new Intent(MainActivity.this,MapsActivity.class);
            startActivity(s);

        } else if (id == R.id.nav_About) {
            AboutFragment aboutFragment=new AboutFragment();
            FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container,aboutFragment);
            ft.commit();

        } else if (id == R.id.nav_Logout) {

            LoginManager.getInstance().logOut();
            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();//np np yalniz firebase yapin yanlismis, aa bosver cocuk kim, kizlar? xd +1 su siralar yogunum ztn
            //sen kendi ugrastigini belli et, muhtemelen seni gecirir oburlerini birakir :/ hayirlisi, yag yap :D  kim soker :D:D:D:D
            // kesin gecirir o lol, o adam cok iyi ses gitti

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
