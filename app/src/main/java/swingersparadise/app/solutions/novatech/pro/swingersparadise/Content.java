package swingersparadise.app.solutions.novatech.pro.swingersparadise;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.pusher.pushnotifications.PushNotifications;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.entities.Card;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.fragments.Albums;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.fragments.Cards;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.fragments.Chat;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.fragments.Friends;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.fragments.Kamasutra;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.fragments.Profiles;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.fragments.Matches;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.fragments.MyProfile;


public class Content extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DialogInterface.OnKeyListener {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private TextView display_name;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private DatabaseReference users_db;
    private JSONObject myprofile;
    private BottomNavigationView bottom_navigation;
    private NavigationView navigationView;
    private Card card;


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

        //  bottom_navigation = findViewById(R.id.bottom_navigation);


        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        display_name = headerView.findViewById(R.id.display_name);
        // profile_image = headerView.findViewById(R.id.profile_image);

        mAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(Content.this, Login.class));
                    finish();
                }
            }
        };


        currentUser = mAuth.getCurrentUser();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myConnectionsRef = database.getReference("users/" + currentUser.getUid() + "/online_presence");


//
        mAuth = FirebaseAuth.getInstance();

        final DatabaseReference lastOnlineRef = FirebaseDatabase.getInstance().getReference("/users/" + mAuth.getUid() + "/lastOnline");

        final DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");

        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean connected = dataSnapshot.getValue(Boolean.class);
                if (connected) {
                    DatabaseReference con = myConnectionsRef.push();
                    con.onDisconnect().removeValue();
                    lastOnlineRef.onDisconnect().setValue(ServerValue.TIMESTAMP);
                    con.setValue(Boolean.TRUE);

                } else {

                    myConnectionsRef.onDisconnect().removeValue();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        MenuItem default_menu = sharedPreferences.contains("selected_menu") ?
                navigationView.getMenu().findItem(sharedPreferences.getInt("selected_menu", R.id.nav_discover)) :
                navigationView.getMenu().findItem(R.id.nav_discover);

        //default_menu = navigationView.getMenu().findItem(R.id.nav_discover);
        //loadFragment("discover", null);
        onNavigationItemSelected(default_menu);


    }

    private void setProfile() {


        if (currentUser != null) {

            users_db = FirebaseDatabase.getInstance().getReference().child("users");
            //  DatabaseReference user_profile = users_db.child(currentUser.getUid());
            Log.d("UUID", currentUser.getUid());
            //  Toast.makeText(this,  currentUser.getUid(), Toast.LENGTH_LONG).show();

            users_db.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        if (userSnapshot.getKey().equals(currentUser.getUid())) {
                                    card = userSnapshot.getValue(Card.class);
                            display_name.setText(card.getDisplay_name());
                            editor.putString("display_name", card.getDisplay_name()).commit();
                            if ((TextUtils.isEmpty(card.getDisplay_name()) || TextUtils.isEmpty(card.getGender()) ||
                                    TextUtils.isEmpty(card.getMarital_status()) ) && ! navigationView.getMenu().equals(navigationView.getMenu().findItem(R.id.nav_profile)) ) {

                                AlertDialog aDialog = new AlertDialog.Builder(Content.this).setMessage("Your profile is not completed please complete before you continue").setTitle("Icomplete Profile")
                                        .setNeutralButton("Close", new DialogInterface.OnClickListener() {
                                            public void onClick(final DialogInterface dialog,
                                                                final int which) {
                                                //Prevent to finish activity, if user clicks about.

                                                onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_profile));


                                            }
                                        }).create();
                                aDialog.setIcon(R.drawable.ic_error_black_24dp);
                                aDialog.setOnKeyListener(Content.this);
                                aDialog.show();

                            }
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            PushNotifications.start(Content.this, getString(R.string.pusher_instance_id));
            PushNotifications.subscribe(currentUser.getUid());

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

    //External Acces hack
    public void selectAlbum(){
        onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_album));
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = 0;
        try {
            id = item.getItemId();
            editor.remove("selected_menu").commit();
        } catch (NullPointerException e) {
            onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_profile));
        }
        if(id != R.id.nav_profile){
            setProfile();
        }


        navigationView.setCheckedItem(item);


        switch (id) {
            case R.id.nav_profile:
                Bundle bundle = new Bundle();
                editor.putInt("selected_menu", R.id.nav_profile).commit();
                loadFragment("profile", null);

                break;
            case R.id.nav_discover:
                //   bottom_navigation.setVisibility(View.INVISIBLE);
                editor.putInt("selected_menu", R.id.nav_discover).commit();
                loadFragment("discover", null);
                break;
            case R.id.nav_matches:
                editor.putInt("selected_menu", R.id.nav_matches).commit();
                loadFragment("matches", null);
                break;
            case R.id.nav_friends:
                editor.putInt("selected_menu", R.id.nav_friends).commit();
                loadFragment("friends", null);
                break;
            case R.id.nav_messenger:
                editor.putInt("selected_menu", R.id.nav_messenger).commit();
                loadFragment("chats", null);
                break;
            case R.id.nav_album:
                editor.putInt("selected_menu", R.id.nav_album).commit();
                loadFragment("albums", null);
                break;
            case R.id.nav_gallery:
                editor.putInt("selected_menu", R.id.nav_gallery).commit();
                loadFragment("kamasutra", null);
                break;




            case R.id.nav_logout:
                mAuth.signOut();
                final DatabaseReference myConnectionsRef = FirebaseDatabase.getInstance().getReference("users/" + currentUser.getUid() + "/online_presence");
                final DatabaseReference lastOnlineRef = FirebaseDatabase.getInstance().getReference("/users/" + currentUser.getUid() + "/lastOnline");

                lastOnlineRef.setValue(ServerValue.TIMESTAMP).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            myConnectionsRef.removeValue();
                            mAuth.signOut();
                            PreferenceManager.getDefaultSharedPreferences(Content.this).edit().remove("display_name").commit();
                            startActivity(new Intent(Content.this, Login.class));
                        }

                    }
                });

                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;


    }

    public void loadFragment(String name, @Nullable Bundle bundle) {

        Fragment f = null;
        //String tag = "";

        if ("discover".equals(name)) {

            // insert detail fragment into detail container
            f = new Cards();
            setTitle("Discover");

        }
        if ("profile".equals(name)) {

            // insert detail fragment into detail container
            f = new MyProfile();
            setTitle("My Profile");

        }
        if ("matches".equals(name)) {

            // insert detail fragment into detail container
            f = new Matches();
            setTitle("Matches");

        }

        if ("friends".equals(name)) {

            // insert detail fragment into detail container
            f = new Friends();
            setTitle("Friends");

        }
        if ("chats".equals(name)) {

            // insert detail fragment into detail container
            f = new Chat();
            setTitle("Chats");

        }
        if ("albums".equals(name)) {

            // insert detail fragment into detail container
            f = new Albums();
            setTitle("Manage My photos");

        }
        if ("kamasutra".equals(name)) {

            // insert detail fragment into detail container
            f = new Kamasutra();
            setTitle("Sex Positions");

        }


        if (bundle != null)
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
    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            //disable the back button
        }
        return true;
    }

}
