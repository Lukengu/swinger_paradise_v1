package swingersparadise.app.solutions.novatech.pro.swingersparadise.main.adapters;

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
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;

import swingersparadise.app.solutions.novatech.pro.swingersparadise.AppConfig;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.ProfileView;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.R;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.entities.Card;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.fragments.Cards;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.utils.Country;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.utils.GenderConverter;

public class ProfileItemAdapter extends ArrayAdapter<Card> {

    private Context context;
    private  List<Card> profiles;
    private  int resource;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference user_db;
    private DatabaseReference albums_db;
    private SharedPreferences spref;
    private SharedPreferences.Editor editor;
  //  private Fragment parent;
    private String key ="";
    public static final String PREFERENCE= "tinder_card";
    private Cards pfragment;





    public ProfileItemAdapter(@NonNull Context context, int resource, @NonNull List<Card> profiles, Cards pfragment) {
        super(context, resource, profiles);
        this.context =context;
        this.resource = resource;
        this.profiles = profiles;
        this.pfragment = pfragment;

        firebaseAuth = FirebaseAuth.getInstance();
        user_db = FirebaseDatabase.getInstance().getReference().child("users");
        albums_db  = FirebaseDatabase.getInstance().getReference().child("albums");
        spref = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        editor = spref.edit();

    }


    public int getCount() {
        return profiles.size();
    }

    public @Nullable
    Card getItem(int position) {
        return profiles.get(position);
    }
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, final ViewGroup parent) {

        // Get the data item for this position
        final  View view;
        final Card profile = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {

            convertView =  LayoutInflater.from(context).inflate(resource, parent, false);
            view =convertView;
        } else {
            view = convertView;
        }
        // Lookup view for data population
        //
        final ImageView profileImageView = view.findViewById(R.id.profile_image);
        ImageView gender = view.findViewById(R.id.gender);
        RecyclerView mRecyclerView = view.findViewById(R.id.recycle_view);
        final ImageView connect_no = view.findViewById(R.id.connect_no);
        ImageView connect_yes = view.findViewById(R.id.connect_yes);
        ImageView unlock_action= view.findViewById(R.id.unlock_action);
        final  ConstraintLayout container  = view.findViewById(R.id.container);
        ImageView back_to_profile  = view.findViewById(R.id.back_to_profile);

        ImageView view_profile = view.findViewById(R.id.view_profile);
        final ImageView friend_request  = view.findViewById(R.id.friend_request);
        final ToggleButton favorite_btn  = view.findViewById(R.id.favorite_btn);

        StorageReference ref = FirebaseStorage.getInstance().getReference().child("profiles/" + profile.getUuid());

        AlbumAdapter albumAdapter = new AlbumAdapter(context, profile);
        mRecyclerView.setAdapter(albumAdapter);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(context, 3);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        gender.setImageDrawable( context.getResources().getDrawable(GenderConverter.convert(profile)));




        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(final Uri uri) {

                Glide.with(context)
                        .load(uri.toString())
                        .into(profileImageView);
                // changes.firePropertyChange("mCard",null, mCard);
                //cards.add(mCard);

            }

        });


        unlock_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                key =  "UP-"+firebaseAuth.getCurrentUser().getUid()+"."+profile.getUuid();
                if(spref.contains(key)) {
                    Snackbar snackbar =  Snackbar.make(container, "Your request to unlock has been already sent ", Snackbar.LENGTH_LONG);
                    android.view.View sbView = snackbar.getView();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        sbView.setBackground(context.getDrawable(R.drawable.snackbar_error));
                    } else {
                        sbView.setBackground(context.getResources().getDrawable(R.drawable.snackbar_error));
                    }
                    snackbar.show();
                } else {
                    final ProgressDialog dialog = new ProgressDialog(context);
                    dialog.setCancelable(false);
                    dialog.setIndeterminate(true);
                    dialog.setMessage("Please Wait...");

                    String notification_message = "{\"interests\":[\""+profile.getUuid()+"\"],\"fcm\":{\"notification\":{\"title\":\"Unlock Request\",\"body\":\"You have receive a request From "+profile.getDisplay_name()+" to unlock your private album\"}}}";
                    AsyncHttpClient client = new AsyncHttpClient();
                    client.addHeader("content-type", "application/json");
                    client.addHeader("authorization", "Bearer "+ AppConfig.auth_key);
                    try {
                        StringEntity entity = new StringEntity(notification_message);
                        client.post(context,AppConfig.pusher_notification_enpoint,null,entity,"application/json",new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                Log.i("Request", AppConfig.pusher_notification_enpoint);
                                Log.i("Response", response.toString());
                                dialog.dismiss();
                                albums_db.child(profile.getUuid()).child("unlock").child(firebaseAuth.getCurrentUser().getUid()).setValue("false");
                                editor.putBoolean(key, true).commit();
                                Snackbar.make(container, "Your request to unlock  sent to "+profile.getDisplay_name()+"", Snackbar.LENGTH_LONG).show();

                            }
                            @Override
                            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                                Log.e("Response", t.getMessage());
                                // mCallBack.onFailure(new ClientException(t));
                                AlertDialog alertDialog =  new AlertDialog.Builder(context).create();
                                alertDialog.setTitle("");
                                alertDialog.setIcon(R.drawable.ic_error_outline_black_24dp);
                                alertDialog.setMessage(t.getMessage());
                                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });


                                alertDialog.show();

                                dialog.dismiss();

                            }
                        });
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }


                }

            }
        });

        connect_no.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                pfragment.left();

            }
        });

        connect_yes.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                pfragment.right();

            }
        });

        view_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle  = new Bundle();
                bundle.putSerializable("card", profile);
                context.startActivity(new Intent(context, ProfileView.class).putExtras(bundle));
            }
        });

        back_to_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog =  new AlertDialog.Builder(context).create();
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
            }
        });

        friend_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                key = "FR-" + firebaseAuth.getCurrentUser().getUid() + "." + profile.getUuid();
                // editor.remove(key).commit();
                if (spref.contains(key)) {
                    Snackbar snackbar = Snackbar.make(container, "Your have already  a friend request to  " + profile.getDisplay_name() + "", Snackbar.LENGTH_LONG);
                    android.view.View sbView = snackbar.getView();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        sbView.setBackground(context.getDrawable(R.drawable.snackbar_error));
                    } else {
                        sbView.setBackground(connect_no.getResources().getDrawable(R.drawable.snackbar_error));
                    }
                    snackbar.show();
                } else {

                    //send notification
                    String notification_message = "{\"interests\":[\"" + profile.getUuid() + "\"],\"fcm\":{\"notification\":{\"title\":\"Friend Request\",\"body\":\"You have receive a friend request From" + profile.getDisplay_name() + "\"}}}";
                    AsyncHttpClient client = new AsyncHttpClient();
                    client.addHeader("content-type", "application/json");
                    client.addHeader("authorization", "Bearer " + AppConfig.auth_key);
                    try {
                        final ProgressDialog dialog = new ProgressDialog(context);
                        dialog.setCancelable(false);
                        dialog.setIndeterminate(true);
                        dialog.setMessage("Please Wait...");

                        StringEntity entity = new StringEntity(notification_message);
                        client.post(context, AppConfig.pusher_notification_enpoint, null, entity, "application/json", new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                Log.i("Request", AppConfig.pusher_notification_enpoint);
                                Log.i("Response", response.toString());
                                user_db.child(profile.getUuid()).child("friends").child(firebaseAuth.getCurrentUser().getUid()).setValue("false");
                                Snackbar.make(container, "Friend request sent to " + profile.getDisplay_name() + "", Snackbar.LENGTH_LONG).show();


                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    friend_request.setImageDrawable(context.getDrawable(R.drawable.ic_person_pin_red_24dp));
                                } else {
                                    friend_request.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_person_pin_red_24dp));
                                }

                                dialog.dismiss();

                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                                Log.e("Response", t.getMessage());
                                // mCallBack.onFailure(new ClientException(t));

                                AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                                alertDialog.setTitle("");
                                alertDialog.setIcon(R.drawable.ic_error_outline_black_24dp);
                                alertDialog.setMessage(t.getMessage());
                                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });


                                alertDialog.show();

                                dialog.dismiss();

                            }
                        });
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    editor.putBoolean(key, true).commit();

                }
            }
        });

        favorite_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(favorite_btn.isChecked()) {
                    user_db.child(profile.getUuid()).child("favorites").child(firebaseAuth.getCurrentUser().getUid()).setValue("true");
                } else {
                    user_db.child(profile.getUuid()).child("favorites").child(firebaseAuth.getCurrentUser().getUid()).removeValue();
                }
            }
        });

        user_db.child(profile.getUuid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getKey().equals("favorites")){

                    DatabaseReference ref = user_db.child(profile.getUuid()).child("favorites");
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

        user_db.child(profile.getUuid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getKey().equals("friends")){

                    DatabaseReference ref  = user_db.child(profile.getUuid()).child("friends");
                    ref.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            if(dataSnapshot.getKey().equals(firebaseAuth.getCurrentUser().getUid())) {
                                String friendship = dataSnapshot.getValue().toString();
                                if(friendship.equals("false")) {

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        friend_request.setImageDrawable(context.getDrawable(R.drawable.ic_person_pin_red_24dp));
                                    } else {
                                        friend_request.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_person_pin_red_24dp));
                                    }
                                }
                                if(dataSnapshot.getValue().toString().equals("true")) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        friend_request.setImageDrawable(context.getDrawable(R.drawable.ic_person_pin_green_24dp));
                                    } else {
                                        friend_request.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_person_pin_green_24dp));
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




        user_db.child(profile.getUuid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getKey().equals("friends")){

                    DatabaseReference ref  = user_db.child(profile.getUuid()).child("friends");
                    ref.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            if(dataSnapshot.getKey().equals(firebaseAuth.getCurrentUser().getUid())) {
                                String friendship = dataSnapshot.getValue().toString();
                                if(friendship.equals("false")) {

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        friend_request.setImageDrawable(context.getDrawable(R.drawable.ic_person_pin_red_24dp));
                                    } else {
                                        friend_request.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_person_pin_red_24dp));
                                    }
                                }
                                if(dataSnapshot.getValue().toString().equals("true")) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        friend_request.setImageDrawable(context.getDrawable(R.drawable.ic_person_pin_green_24dp));
                                    } else {
                                        friend_request.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_person_pin_green_24dp));
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



        user_db.child(profile.getUuid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getKey().equals("favorites")){

                    DatabaseReference ref = user_db.child(profile.getUuid()).child("favorites");
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



        // Return the completed view to render on screen

        return convertView;

    }



}
