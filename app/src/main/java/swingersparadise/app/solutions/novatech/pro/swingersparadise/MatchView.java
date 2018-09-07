package swingersparadise.app.solutions.novatech.pro.swingersparadise;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mindorks.placeholderview.SwipePlaceHolderView;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.beans.PropertyChangeSupport;
import java.io.UnsupportedEncodingException;

import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.adapters.AlbumAdapter;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.entities.Card;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.utils.GenderConverter;



public class MatchView extends AppCompatActivity implements View.OnClickListener {

    private Card mCard;



    private FirebaseAuth firebaseAuth;
    private DatabaseReference user_db;
    private DatabaseReference albums_db;
    private SharedPreferences spref;
    private SharedPreferences.Editor editor;
    private Fragment parent;
    public static final String PREFERENCE= "tinder_card";
    private ImageView profileImageView;
    private ImageView gender;
    private RecyclerView mRecyclerView;

    private ConstraintLayout container;
    private ImageView view_profile;
    private ImageView friend_request;
    private ToggleButton favorite_btn;
    private TextView online_status, indicators;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.match_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCard = (Card) getIntent().getExtras().getSerializable("card");

        setTitle(mCard.getDisplay_name());




        gender = findViewById(R.id.gender);
        mRecyclerView = findViewById(R.id.recycle_view);

        container = findViewById(R.id.container);
        profileImageView = findViewById(R.id.profile_image);
        friend_request = findViewById(R.id.friend_request);
        favorite_btn = findViewById(R.id.favorite_btn);
        view_profile = findViewById(R.id.view_profile);

        online_status = findViewById(R.id.online_status);
        indicators = findViewById(R.id.indicators);




        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        firebaseAuth = FirebaseAuth.getInstance();
        user_db = FirebaseDatabase.getInstance().getReference().child("users");
        albums_db  = FirebaseDatabase.getInstance().getReference().child("albums");
        spref = getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        editor = spref.edit();
        //Glide.with(mContext).load(mProfile.getImageUrl()).into(profileImageView);
        StorageReference ref = FirebaseStorage.getInstance().getReference().child("profiles/" + mCard.getUuid());


        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(final Uri uri) {

                Glide.with(MatchView.this)
                        .load(uri.toString())
                        .into(profileImageView);
                // changes.firePropertyChange("mCard",null, mCard);


            }

        });

        if(!TextUtils.isEmpty(mCard.getGender()))
            gender.setImageDrawable( getResources().getDrawable(GenderConverter.convert(mCard)));

        view_profile.setOnClickListener(this);
        view_profile.setOnClickListener(this);
        friend_request.setOnClickListener(this);
        favorite_btn.setOnClickListener(this);

        RefreshList();
        checkFavorite();
        checkFriendship();
        UserIsOnline();
    }

    private void UserIsOnline() {
        final DatabaseReference myConnectionsRef = FirebaseDatabase.getInstance().getReference("users/"+mCard.getUuid()+"/online_presence");
        myConnectionsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean online = dataSnapshot.exists();

                indicators.setEnabled(online);
                if(online){
                    online_status.setText("Online");
                    online_status.setTextColor(getResources().getColor(R.color.user_online));

                }  else {
                    online_status.setText("Offline");
                    online_status.setTextColor(getResources().getColor(R.color.colorChocolateSombre));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void RefreshList() {
        AlbumAdapter albumAdapter = new AlbumAdapter(this, mCard);
        mRecyclerView.setAdapter(albumAdapter);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setHasFixedSize(true);
    }
    private void checkFavorite(){
        user_db.child(mCard.getUuid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getKey().equals("favorites")){

                    DatabaseReference ref = user_db.child(mCard.getUuid()).child("favorites");
                    ref.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            //Toast.makeText(mContext, dataSnapshot.getKey(), Toast.LENGTH_LONG).show();
                            if(dataSnapshot.getKey().equals(firebaseAuth.getCurrentUser().getUid())){
                                //Toast.makeText(mContext, dataSnapshot.getValue().toString(), Toast.LENGTH_LONG).show();
                                favorite_btn.setChecked(true);


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

    }
    private void checkFriendship(){

        user_db.child(mCard.getUuid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getKey().equals("friends")){

                    DatabaseReference ref  = user_db.child(mCard.getUuid()).child("friends");
                    ref.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            if(dataSnapshot.getKey().equals(firebaseAuth.getCurrentUser().getUid())) {
                                String friendship = dataSnapshot.getValue().toString();
                                if(friendship.equals("false")) {

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    } else {
                                        friend_request.setImageDrawable(MatchView.this.getResources().getDrawable(R.drawable.ic_person_pin_red_24dp));
                                    }
                                }
                                if(dataSnapshot.getValue().toString().equals("true")) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        friend_request.setImageDrawable(MatchView.this.getDrawable(R.drawable.ic_person_pin_green_24dp));
                                    } else {
                                        friend_request.setImageDrawable(MatchView.this.getResources().getDrawable(R.drawable.ic_person_pin_green_24dp));
                                    }
                                }
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




    }

    @Override
    public void onClick(android.view.View view) {
        String key ="";
        switch(view.getId()){


            case R.id.view_profile:
                //Toast.makeText(mContext, "clicked", Toast.LENGTH_LONG).show();
                Bundle bundle  = new Bundle();
                bundle.putSerializable("card", mCard);
                startActivity(new Intent(MatchView.this, ProfileView.class).putExtras(bundle));
                break;
            case R.id.friend_request:
                key =  "FR-"+firebaseAuth.getCurrentUser().getUid()+"."+mCard.getUuid();
                // editor.remove(key).commit();
                if(spref.contains(key)) {
                    Snackbar snackbar =  Snackbar.make(container, "Your have already  a friend request to  "+mCard.getDisplay_name()+"", Snackbar.LENGTH_LONG);
                    android.view.View sbView = snackbar.getView();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        sbView.setBackground(MatchView.this.getDrawable(R.drawable.snackbar_error));
                    } else {
                        sbView.setBackground(MatchView.this.getResources().getDrawable(R.drawable.snackbar_error));
                    }
                    snackbar.show();
                } else {
                    final ProgressDialog dialog = new ProgressDialog(MatchView.this);
                    dialog.setCancelable(false);
                    dialog.setIndeterminate(true);
                    dialog.setMessage("Please Wait...");

                    //send notification
                    String notification_message = "{\"interests\":[\""+mCard.getUuid()+"\"],\"fcm\":{\"notification\":{\"title\":\"Friend Request\",\"body\":\"You have receive a friend request From"+mCard.getDisplay_name()+"\"}}}";
                    AsyncHttpClient client = new AsyncHttpClient();
                    client.addHeader("content-type", "application/json");
                    client.addHeader("authorization", "Bearer "+ AppConfig.auth_key);
                    try {
                        StringEntity entity = new StringEntity(notification_message);
                        client.post(MatchView.this,AppConfig.pusher_notification_enpoint,null,entity,"application/json",new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                Log.i("Request", AppConfig.pusher_notification_enpoint);
                                Log.i("Response", response.toString());
                                user_db.child(mCard.getUuid()).child("friends").child(firebaseAuth.getCurrentUser().getUid()).setValue("false");
                                Snackbar.make(container, "Friend request sent to "+mCard.getDisplay_name()+"", Snackbar.LENGTH_LONG).show();



                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    friend_request.setImageDrawable(MatchView.this.getDrawable(R.drawable.ic_person_pin_red_24dp));
                                } else {
                                    friend_request.setImageDrawable(MatchView.this.getResources().getDrawable(R.drawable.ic_person_pin_red_24dp));
                                    friend_request.setImageDrawable(MatchView.this.getResources().getDrawable(R.drawable.ic_person_pin_red_24dp));
                                }
                                dialog.dismiss();

                            }
                            @Override
                            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                                Log.e("Response", t.getMessage());
                                // mCallBack.onFailure(new ClientException(t));

                            }
                        });
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    editor.putBoolean(key, true).commit();

                }

                break;
            case R.id.favorite_btn:
                if(favorite_btn.isChecked()) {
                    user_db.child(mCard.getUuid()).child("favorites").child(firebaseAuth.getCurrentUser().getUid()).setValue("true");
                } else {
                    user_db.child(mCard.getUuid()).child("favorites").child(firebaseAuth.getCurrentUser().getUid()).removeValue();
                }
                break;


        }
    }
}
