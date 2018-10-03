package swingersparadise.app.solutions.novatech.pro.swingersparadise.main.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import swingersparadise.app.solutions.novatech.pro.swingersparadise.R;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.entities.Card;
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



    public ProfileItemAdapter(@NonNull Context context, int resource, @NonNull List<Card> profiles) {
        super(context, resource, profiles);
        this.context =context;
        this.resource = resource;
        this.profiles = profiles;
        firebaseAuth = FirebaseAuth.getInstance();
        user_db = FirebaseDatabase.getInstance().getReference().child("users");
        albums_db  = FirebaseDatabase.getInstance().getReference().child("albums");
        spref = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        editor = spref.edit();
    }

    public void addCard(Card profile){
        profiles.add(profile);
        notifyDataSetChanged();
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

    public View getView(int position, View convertView, ViewGroup parent) {

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
        ImageView connect_no = view.findViewById(R.id.connect_no);
        ImageView connect_yes = view.findViewById(R.id.connect_yes);
        ImageView unlock_action= view.findViewById(R.id.unlock_action);
        ConstraintLayout container  = view.findViewById(R.id.container);
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

  //      if(!TextUtils.isEmpty(profile.getGender()))
//            gender.setImageDrawable( context.getResources().getDrawable(GenderConverter.convert(profile)));



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



        // Return the completed view to render on screen

        return convertView;

    }
}
