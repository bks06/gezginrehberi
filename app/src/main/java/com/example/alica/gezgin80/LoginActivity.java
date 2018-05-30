package com.example.alica.gezgin80;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {
    public static String userKey;
    public static int userPoint;
    FirebaseDatabase database;
    CallbackManager callbackManager = CallbackManager.Factory.create();
    TextView txtEmail,txtBirthday,txt,txtFriends;
    ProgressDialog mDialog;
    ImageView foto;
    String username,useremail;
    int iterator = 0;
    JSONObject response, profile_pic_data, profile_pic_url;
    boolean isLoggedIn;

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);
        callbackManager.onActivityResult(requestCode, responseCode, intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        isLoggedIn = accessToken != null && !accessToken.isExpired();
        Log.e("statusssss", String.valueOf(isLoggedIn));
        if(isLoggedIn) {
        final ImageView userImage = (ImageView) findViewById(R.id.avatar);
            GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            Log.e("response",response.toString());
                            getData(object);
                            try {
                                profile_pic_data = new JSONObject(object.get("picture").toString());
                                profile_pic_url =new JSONObject(profile_pic_data.getString("data"));
                                Picasso.with(LoginActivity.this).load(profile_pic_url.getString("url")).into(userImage);
                                txtEmail.setText(object.get("email").toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            addUserIfNotExists(useremail, object);
                        }});
            Bundle parameters = new Bundle();
            parameters.putString("fields","id,name,email,picture.width(100).height(100)");
            request.setParameters(parameters);
            request.executeAsync();//ok ?
        }

        keyhash();
        //txtBirthday = findViewById(R.id.txtBirthday);
        //txtEmail=findViewById(R.id.txtEmail);
        //txtFriends=findViewById(R.id.txtFriends);
        //foto=findViewById(R.id.imageViewfoto);

        LoginButton loginWithFacebookButton= findViewById(R.id.login_button);
        loginWithFacebookButton.setReadPermissions(Arrays.asList("public_profile","email","user_birthday","user_friends"));

        loginWithFacebookButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                mDialog = new ProgressDialog(LoginActivity.this);
                mDialog.setMessage("Retrieving data...");
                mDialog.show();

                //String accesstoken = loginResult.getAccessToken().getToken();
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("response",response.toString());
                        getData(object);
                        mDialog.dismiss();
                        addUserIfNotExists(useremail, object);

                        // yeni kullanıcı eklendi
                        // check 0 olarak eklendi
                        // databaseden emailine göre bakıp checki 0 ise başka bir pop up yapıp ok tuşunu almamız lazım...!!!!




                        // İNTENT BURAYA YAZILACAK... OBJECT GÖNDERİLECEK ... MAİNDE OBJEYİ ALIP KURACAZ
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields","id,name,email,picture.width(100).height(100)");
                request.setParameters(parameters);
                request.executeAsync();
                //newUser();

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }
    /*

     if(AccessToken.getCurrentAccessToken()!=null){
        txtEmail.setText(AccessToken.getCurrentAccessToken().getUserId());
    }
*/
    private void getData(JSONObject object) {

        try {
            URL profile_picture = new URL("https://graph.facebook.com/"+object.getString("id") + "/picture?width=100&height=100");

            //Picasso.with(this).load(profile_picture.toString()).into(foto);
            username= object.getString("name");
            useremail= object.getString("email");
            //txtEmail.setText(object.getString("email"));
            //txtBirthday.setText(object.getString("first_name"+" "+"last_name"));
           // txtFriends.setText("Friends: " + object.getJSONObject("friends").getJSONObject("summary").getString("total_count"));

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void keyhash(){
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.alica.gezgin80",//Projenin paket ismini yazıyoruz
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("KeyHash:", e.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.d("KeyHash:", e.toString());
        }

    }

    private void addUser(String name, String email){
        database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef= database.getReference("users");
        DatabaseReference idRef=myRef.push();
        int puan =20;
        int check=0;
        UserModel user = new UserModel(name,email,puan,check);
        idRef.child("email").setValue(email);
        idRef.child("name").setValue(name);
        idRef.child("point").setValue(puan);
        idRef.child("check").setValue(check);
    }

    private void addUserIfNotExists(final String useremail, final Object object){
        database =FirebaseDatabase.getInstance();
        final boolean[] status = {false};
        final String uname = username;
        final DatabaseReference dbRef = database.getReference("users");
        Log.e("GET EMAIL LIST", useremail);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    if(ds.child("email").getValue().toString().equals(useremail)){
                        userKey = ds.getKey();
                        userPoint = Integer.parseInt(ds.child("point").getValue().toString());
                        int userCheck = Integer.parseInt(ds.child("check").getValue().toString());
                        if(userCheck == 0){
                            LayoutInflater li = LayoutInflater.from(getApplicationContext());
                            final View promptsView = li.inflate(R.layout.useragreementlayout, null);

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                    LoginActivity.this);

                            alertDialogBuilder.setView(promptsView);
                            alertDialogBuilder
                                    .setCancelable(false)
                                    .setPositiveButton("KABUL ET", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                            DatabaseReference dbRef = firebaseDatabase.getReference("users");
                                            DatabaseReference puanRef = dbRef.child(userKey).child("check");
                                            puanRef.setValue(1);
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            intent.putExtra("jsondata",object.toString());
                                            startActivity(intent);
                                            finish();

                                        }
                                    })
                                    .setNegativeButton("REDDET", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alertDialog = alertDialogBuilder.create();

                            // show it
                            alertDialog.show();

                        }
                        else {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("jsondata",object.toString());
                            startActivity(intent);
                            finish();

                        }






                        return;
                    }
                }
                addUser(uname,useremail);
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                intent.putExtra("jsondata",object.toString());
                startActivity(intent);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}


