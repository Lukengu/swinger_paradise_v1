package swingersparadise.app.solutions.novatech.pro.swingersparadise.main.view;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeInState;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;
import com.mindorks.placeholderview.annotations.swipe.SwipeOutState;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONObject;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.UnsupportedEncodingException;

import swingersparadise.app.solutions.novatech.pro.swingersparadise.AppConfig;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.ProfileView;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.R;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.adapters.AlbumAdapter;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.entities.Card;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.utils.GenderConverter;


@Layout(R.layout.item)
public class TinderCard implements android.view.View.OnClickListener {

    @View(R.id.profile_image)
    private ImageView profileImageView;

    @View(R.id.gender)
    private ImageView gender;

    @View(R.id.recycle_view)
    private RecyclerView mRecyclerView;

    @View(R.id.connect_no)
    private ImageView connect_no;


    @View(R.id.connect_yes)
    private ImageView connect_yes;

    @View(R.id.unlock_action)
    private ImageView unlock_action;

    @View(R.id.container)
    private ConstraintLayout container;

    @View(R.id.back_to_profile)
    private ImageView back_to_profile;

    @View(R.id.view_profile)
    private ImageView view_profile;

    @View(R.id.friend_request)
    private ImageView friend_request;

    @View(R.id.favorite_btn)
    private ToggleButton favorite_btn;




   // @View(R.id.swiperefresh)
    //private android.support.v4.widget.SwipeRefreshLayout swiperefresh;








    private Card mCard;
    private Context mContext;
    private SwipePlaceHolderView mSwipeView;

    private PropertyChangeSupport changes = new PropertyChangeSupport(this);

    private FirebaseAuth firebaseAuth;
    private DatabaseReference user_db;
    private DatabaseReference albums_db;
    private SharedPreferences spref;
    private SharedPreferences.Editor editor;

    public static final String PREFERENCE= "tinder_card";


    public TinderCard(Context context, Card card, SwipePlaceHolderView swipeView) {
        mContext = context;
        mCard = card;
        mSwipeView = swipeView;




    }

    @Resolve
    private void onResolved() {

        firebaseAuth = FirebaseAuth.getInstance();
        user_db = FirebaseDatabase.getInstance().getReference().child("users");
        albums_db  = FirebaseDatabase.getInstance().getReference().child("albums");
        spref = mContext.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        editor = spref.edit();
        //Glide.with(mContext).load(mProfile.getImageUrl()).into(profileImageView);
        StorageReference ref = FirebaseStorage.getInstance().getReference().child("profiles/" + mCard.getUuid());


        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(final Uri uri) {

                Glide.with(mContext)
                        .load(uri.toString())
                        .into(profileImageView);
               // changes.firePropertyChange("mCard",null, mCard);


            }

        });
        gender.setImageDrawable( mContext.getResources().getDrawable(GenderConverter.convert(mCard)));

        checkFavorite();
        checkFriendship();
        RefreshList();

       // swiperefresh.setRefreshing(true);


       /* swiperefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {


                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        RefreshList();
                    }
                }
        );*/



        connect_no.setOnClickListener(this);
        connect_yes.setOnClickListener(this);
        unlock_action.setOnClickListener(this);
        back_to_profile.setOnClickListener(this);
        view_profile.setOnClickListener(this);
        friend_request.setOnClickListener(this);
        favorite_btn.setOnClickListener(this);


    }




    private void RefreshList(){
        AlbumAdapter albumAdapter = new AlbumAdapter(mContext, mCard);
        mRecyclerView.setAdapter(albumAdapter);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(mContext, 3);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setHasFixedSize(true);


    }

    private void checkFavorite(){
        user_db.child(mCard.getUuid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if(dataSnapshot.exists() && dataSnapshot.hasChild("favorites")){
                    user_db.child(mCard.getUuid()).child("favorites").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            Toast.makeText(mContext, dataSnapshot.getKey(), Toast.LENGTH_LONG).show();
                            if(dataSnapshot.getKey().equals(firebaseAuth.getCurrentUser().getUid())) {

                                favorite_btn.setEnabled(true);
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

                if(dataSnapshot.hasChild("friends")){
                    user_db.child(mCard.getUuid()).child("friends").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            Toast.makeText(mContext, dataSnapshot.getKey(), Toast.LENGTH_LONG).show();
                            if(dataSnapshot.getKey().equals(firebaseAuth.getCurrentUser().getUid())){
                                String friendship = dataSnapshot.getValue().toString();
                                if(friendship.equals("false")) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        friend_request.setImageDrawable(mContext.getDrawable(R.drawable.ic_person_pin_red_24dp));
                                    } else {
                                        friend_request.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_person_pin_red_24dp));
                                    }
                                }
                                if(friendship.equals("true")) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        friend_request.setImageDrawable(mContext.getDrawable(R.drawable.ic_person_pin_green_24dp));
                                    } else {
                                        friend_request.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_person_pin_green_24dp));
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


    @SwipeOut
    private void onSwipedOut () {
        Log.d("EVENT", "onSwipedOut");
        //  user_db.child(mCard.getOpposite_gender()).child(mCard.getUuid()).child("Connections").child("Nope").child(firebaseAuth.getCurrentUser().getUid()).setValue("true");

        user_db.child(mCard.getUuid()).child("connections").child("nope").child(firebaseAuth.getCurrentUser().getUid()).setValue("true");

        mSwipeView.addView(this);
        changes.firePropertyChange("mCard",null, mCard);
    }

    @SwipeCancelState
    private void onSwipeCancelState () {
        Log.d("EVENT", "onSwipeCancelState");
    }

    @SwipeIn
    private void onSwipeIn () {
        Log.d("EVENT", "onSwipedIn");
        // user_db.child(mCard.getOpposite_gender()).child(mCard.getUuid()).child("Connections").child("Yep").child(firebaseAuth.getCurrentUser().getUid()).setValue("true");
        user_db.child(mCard.getUuid()).child("connections").child("yep").child(firebaseAuth.getCurrentUser().getUid()).setValue("true");
        changes.firePropertyChange("mCard",null, mCard);

    }




    @SwipeInState
    private void onSwipeInState () {
        Log.d("EVENT", "onSwipeInState");
    }

    @SwipeOutState
    private void onSwipeOutState () {
        Log.d("EVENT", "onSwipeOutState");
    }



    public void addPropertyChangeListener(
            PropertyChangeListener l) {
        changes.addPropertyChangeListener(l);
    }
    public void removePropertyChangeListener(
            PropertyChangeListener l) {
        changes.removePropertyChangeListener(l);
    }

    @Override
    public void onClick(android.view.View view) {
        String key ="";
        switch(view.getId()){
            case  R.id.connect_no:
                mSwipeView.doSwipe(false);
                break;
            case R.id.connect_yes:
                mSwipeView.doSwipe(true);
                break;
            case R.id.unlock_action:
                // Send notification to unlock
                 key =  "UP-"+firebaseAuth.getCurrentUser().getUid()+"."+mCard.getUuid();
                if(spref.contains(key)) {
                    Snackbar snackbar =  Snackbar.make(container, "Your request to unlock has been already sent ", Snackbar.LENGTH_LONG);
                    android.view.View sbView = snackbar.getView();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        sbView.setBackground(mContext.getDrawable(R.drawable.snackbar_error));
                    } else {
                        sbView.setBackground(mContext.getResources().getDrawable(R.drawable.snackbar_error));
                    }
                    snackbar.show();
                } else {

                    String notification_message = "{\"interests\":[\""+mCard.getUuid()+"\"],\"fcm\":{\"notification\":{\"title\":\"Unlock Request\",\"body\":\"You have receive a request From "+mCard.getDisplay_name()+" to unlock your private album\"}}}";
                    AsyncHttpClient client = new AsyncHttpClient();
                    client.addHeader("content-type", "application/json");
                    client.addHeader("authorization", "Bearer "+ AppConfig.auth_key);
                    try {
                        StringEntity entity = new StringEntity(notification_message);
                        client.post(mContext,AppConfig.pusher_notification_enpoint,null,entity,"application/json",new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                Log.i("Request", AppConfig.pusher_notification_enpoint);
                                Log.i("Response", response.toString());

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
                    albums_db.child(mCard.getUuid()).child("unlock").child(firebaseAuth.getCurrentUser().getUid()).setValue("false");
                    editor.putBoolean(key, true).commit();
                    Snackbar.make(container, "Your request to unlock  sent to "+mCard.getDisplay_name()+"", Snackbar.LENGTH_LONG).show();

                }

                break;

            case R.id.back_to_profile:
                // Send notification to unlock
                //Snackbar.make(container, "Your request to unlock "+mCard.getDisplay_name()+"'s private profile has been sent", Snackbar.LENGTH_LONG).show();
                 AlertDialog alertDialog =  new AlertDialog.Builder(mContext).create();
                alertDialog.setTitle("");
                alertDialog.setMessage("This will take you to profile you have viewed already. Do you wish to continue ?");
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alertDialog.show();
                break;
            case R.id.view_profile:
                //Toast.makeText(mContext, "clicked", Toast.LENGTH_LONG).show();
                Bundle bundle  = new Bundle();
                bundle.putSerializable("card", mCard);
                mContext.startActivity(new Intent(mContext, ProfileView.class).putExtras(bundle));
                break;
            case R.id.friend_request:
                key =  "FR-"+firebaseAuth.getCurrentUser().getUid()+"."+mCard.getUuid();
               // editor.remove(key).commit();
                if(spref.contains(key)) {
                    Snackbar snackbar =  Snackbar.make(container, "Your have already  a friend request to  "+mCard.getDisplay_name()+"", Snackbar.LENGTH_LONG);
                    android.view.View sbView = snackbar.getView();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        sbView.setBackground(mContext.getDrawable(R.drawable.snackbar_error));
                    } else {
                        sbView.setBackground(mContext.getResources().getDrawable(R.drawable.snackbar_error));
                    }
                    snackbar.show();
                } else {

                    //send notification
                    String notification_message = "{\"interests\":[\""+mCard.getUuid()+"\"],\"fcm\":{\"notification\":{\"title\":\"Friend Request\",\"body\":\"You have receive a friend request From"+mCard.getDisplay_name()+"\"}}}";
                    AsyncHttpClient client = new AsyncHttpClient();
                    client.addHeader("content-type", "application/json");
                    client.addHeader("authorization", "Bearer "+ AppConfig.auth_key);
                    try {
                        StringEntity entity = new StringEntity(notification_message);
                        client.post(mContext,AppConfig.pusher_notification_enpoint,null,entity,"application/json",new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                Log.i("Request", AppConfig.pusher_notification_enpoint);
                                Log.i("Response", response.toString());
                                user_db.child(mCard.getUuid()).child("friends").child(firebaseAuth.getCurrentUser().getUid()).setValue("false");
                                Snackbar.make(container, "Friend request sent to "+mCard.getDisplay_name()+"", Snackbar.LENGTH_LONG).show();



                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    friend_request.setImageDrawable(mContext.getDrawable(R.drawable.ic_person_pin_red_24dp));
                                } else {
                                    friend_request.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_person_pin_red_24dp));
                                }

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
                if(favorite_btn.isEnabled()) {
                    user_db.child(mCard.getUuid()).child("favorites").child(firebaseAuth.getCurrentUser().getUid()).setValue("true");
                } else {
                    user_db.child(mCard.getUuid()).child("favorites").child(firebaseAuth.getCurrentUser().getUid()).removeValue();
                }
                break;


        }
    }

}