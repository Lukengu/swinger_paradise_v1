package swingersparadise.app.solutions.novatech.pro.swingersparadise;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pusher.pushnotifications.PushNotifications;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.fragments.Profiles;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.fragments.Matches;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.fragments.MyProfile;


public class Content extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private TextView display_name;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private DatabaseReference users_db;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maincontent);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        fab.hide();



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        display_name = headerView.findViewById(R.id.display_name);
       // profile_image = headerView.findViewById(R.id.profile_image);

        MenuItem default_menu =  sharedPreferences.contains("selected_menu") ?
                navigationView.getMenu().findItem(sharedPreferences.getInt("selected_menu", R.id.nav_discover))  :
                  navigationView.getMenu().findItem(R.id.nav_discover) ;

     default_menu = navigationView.getMenu().findItem(R.id.nav_discover);
        //loadFragment("discover", null);
        onNavigationItemSelected(default_menu);

        mAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){
                    startActivity(new Intent(Content.this, Login.class));
                    finish();
                }
            }
        };

        setProfile();
    }

    private void setProfile() {
         currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {

            users_db = FirebaseDatabase.getInstance().getReference().child("users");
          //  DatabaseReference user_profile = users_db.child(currentUser.getUid());
            Log.d("UUID", currentUser.getUid());
          //  Toast.makeText(this,  currentUser.getUid(), Toast.LENGTH_LONG).show();
            users_db.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if (dataSnapshot.exists() && dataSnapshot.getKey().equals(currentUser.getUid())) {

                     //   Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();


                        Map<String, String> data = new HashMap<>();
                        data.put("display_name", dataSnapshot.hasChild("display_name") ? dataSnapshot.child("display_name").getValue().toString() : "");
                        data.put("drinking", dataSnapshot.hasChild("drinking") ? dataSnapshot.child("drinking").getValue().toString() : "");
                        data.put("age", dataSnapshot.hasChild("age") ? dataSnapshot.child("age").getValue().toString() : "");
                        data.put("body_part", dataSnapshot.hasChild("body_part") ? dataSnapshot.child("body_part").getValue().toString() : "");
                        data.put("build", dataSnapshot.hasChild("build") ? dataSnapshot.child("build").getValue().toString() : "");
                        data.put("country", dataSnapshot.hasChild("country") ? dataSnapshot.child("country").getValue().toString() : "");
                        data.put("about_me", dataSnapshot.hasChild("about_me") ? dataSnapshot.child("about_me").getValue().toString() : "");
                        data.put("gender", dataSnapshot.hasChild("gender") ? dataSnapshot.child("gender").getValue().toString() : "");
                        data.put("hair_color", dataSnapshot.hasChild("hair_color") ? dataSnapshot.child("hair_color").getValue().toString() : "");
                        data.put("marital_status", dataSnapshot.hasChild("marital_status") ? dataSnapshot.child("marital_status").getValue().toString() : "");
                        data.put("name", dataSnapshot.hasChild("name") ? dataSnapshot.child("name").getValue().toString() : "");
                        data.put("referred_by", dataSnapshot.hasChild("referred_by") ? dataSnapshot.child("referred_by").getValue().toString() : "");
                        data.put("sexual_prefs", dataSnapshot.hasChild("sexual_prefs") ? dataSnapshot.child("sexual_prefs").getValue().toString() : "");
                        data.put("ethnicity", dataSnapshot.hasChild("ethnicity") ? dataSnapshot.child("ethnicity").getValue().toString() : "");
                        data.put("smoking", dataSnapshot.hasChild("smoking") ? dataSnapshot.child("smoking").getValue().toString() : "");


                     /*   Iterator iterator = data.entrySet().iterator();
                        System.out.println("---------------------------------------------------");
                        while(iterator.hasNext()) {
                            Map.Entry<String, Object> entry = (  Map.Entry) iterator.next();
                            System.out.println(entry.getKey()+"---"+entry.getValue().toString());

                        }*/

                        JSONObject jsonObject = new JSONObject(data);
                        display_name.setText("Welcome "+ jsonObject.optString("display_name"));

                        PushNotifications.start(Content.this, getString(R.string.pusher_instance_id));
                        PushNotifications.subscribe(currentUser.getUid());


                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            startActivity(new Intent(Content.this, Login.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.content, menu);
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
            editor.remove("selected_menu").commit();


            switch (id) {
                case R.id.nav_profile:
                    editor.putInt("selected_menu", R.id.nav_profile).commit();
                    loadFragment("profile", null);
                    break;
                case R.id.nav_discover:
                    editor.putInt("selected_menu", R.id.nav_discover).commit();
                    loadFragment("discover", null);
                    break;
                case R.id.nav_matches:
                    editor.putInt("selected_menu", R.id.nav_matches).commit();
                    loadFragment("matches", null);
                    break;
                case R.id.nav_logout:
                    mAuth.signOut();
                    startActivity(new Intent(Content.this, Login.class));
                    break;
            }

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;


    }
    public void loadFragment(String name, @Nullable Bundle bundle) {

        Fragment f = null;
        //String tag = "";

        if( "discover".equals(name)){

            // insert detail fragment into detail container
            f = new Profiles();
            setTitle("Discover");

        }
        if( "profile".equals(name)){

            // insert detail fragment into detail container
            f = new MyProfile();
            setTitle("Profile");

        }
        if( "matches".equals(name)){

            // insert detail fragment into detail container
            f = new Matches();
            setTitle("Matches");

        }


        if(bundle != null)
            f.setArguments(bundle);


        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.flContent, f)
                .commit();


    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(authStateListener);
    }
}
