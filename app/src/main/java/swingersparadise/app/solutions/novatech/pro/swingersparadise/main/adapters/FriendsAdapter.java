package swingersparadise.app.solutions.novatech.pro.swingersparadise.main.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import swingersparadise.app.solutions.novatech.pro.swingersparadise.MatchView;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.R;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.entities.Card;


public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendViewHolder> {

    Context c;
    ArrayList<Card> cards;
    SwipeRefreshLayout swiper;
    public static final  int REQUEST  = 0;
    public static final int LIST = 1;

    int view ;

    public FriendsAdapter(Context c, ArrayList<Card> cards, SwipeRefreshLayout swiper, int view ) {
        this.c = c;
        this.cards = cards;
        this.swiper = swiper;
        this.view = view;
    }

    public void addCard(Card card){
        cards.add(card);
        notifyDataSetChanged();
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.match_card,parent,false);
        FriendsAdapter.FriendViewHolder holder=new FriendViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(final FriendsAdapter.FriendViewHolder holder, int position) {

        StorageReference ref = FirebaseStorage.getInstance().getReference().child("profiles/" + cards.get(position).getUuid());
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(final Uri uri) {
                Glide.with(c)
                        .load(uri.toString())
                        .into(holder.profile_image);
            }

        });

        final DatabaseReference myConnectionsRef = FirebaseDatabase.getInstance().getReference("users/"+cards.get(position).getUuid()+"/online_presence");
        myConnectionsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                holder.online_status.setEnabled(dataSnapshot.exists());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.display_name.setText(cards.get(position).getDisplay_name());

        if(view == REQUEST){
            //  refreshMatches();
            holder.btn_one.setText("ACCEPT");
            Drawable img = c.getResources().getDrawable( R.drawable.ic_check_circle_green_24dp );
            holder.btn_one.setCompoundDrawables(img, null, null, null);

            img = c.getResources().getDrawable( R.drawable.ic_cancel_red_24dp );
            holder.btn_two.setText("REJECT");
            holder.btn_one.setCompoundDrawables(img, null, null, null);

        }
        if(view == LIST){
            // refreshFavorites();
            holder.btn_one.setText("CHAT");
            Drawable img = c.getResources().getDrawable( R.drawable.ic_messenger );
            holder.btn_one.setCompoundDrawables(img, null, null, null);

            img = c.getResources().getDrawable( R.drawable.ic_people_black_24dp );
            holder.btn_two.setText("PROFILE");
            holder.btn_one.setCompoundDrawables(img, null, null, null);
        }


    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    private void refresh()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //cards.add(0,cards.get(new Random().nextInt(cards.size())));

                if(view == REQUEST){
                  //  refreshMatches();
                }
                if(view == LIST){
                   // refreshFavorites();
                }

            }


        },3000);
    }

    private void refreshFavorites(){
        final FirebaseUser user  = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {
                if(!dataSnapshot.getKey().equals(user.getUid()) && dataSnapshot.exists()
                        && dataSnapshot.hasChild("favorites")){
                    DatabaseReference favorites = ref.child(dataSnapshot.getKey()).child("favorites");
                    favorites.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot childSnapshot, @Nullable String s) {
                            if(childSnapshot.exists() && childSnapshot.getKey().equals(user.getUid())){
                                Map<String, String> data = new HashMap<>();


                                data.put("uuid", dataSnapshot.getKey());
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


                                JSONObject jsonObject = new JSONObject(data);
                                Card card = null;
                                try {
                                    card = new Card(jsonObject);
                                    cards.add(card);
                                    FriendsAdapter.this.notifyDataSetChanged();
                                    swiper.setRefreshing(false);
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
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
    private void refreshMatches(){
        cards.clear();
        final FirebaseUser user  = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {
                if(!dataSnapshot.getKey().equals(user.getUid()) && dataSnapshot.exists()
                        && dataSnapshot.hasChild("connections")){
                    DatabaseReference match = ref.child(dataSnapshot.getKey()).child("connections").child("yep");

                    match.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot childData, @Nullable String s) {
                            if(childData.getKey().equals(user.getUid())){
                                DatabaseReference my = ref.child(user.getUid()).child("connections").child("yep");
                                my.addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot my, @Nullable String s) {

                                        if(my.getKey().equals( dataSnapshot.getKey())){
                                            Map<String, String> data = new HashMap<>();


                                            data.put("uuid", dataSnapshot.getKey());
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


                                            JSONObject jsonObject = new JSONObject(data);
                                            Card card = null;
                                            try {
                                                card = new Card(jsonObject);
                                                cards.add(card);
                                                FriendsAdapter.this.notifyDataSetChanged();
                                                swiper.setRefreshing(false);
                                                //  getActivity().setTitle(card.getDisplay_name());
                                            } catch (IllegalAccessException e) {
                                                e.printStackTrace();
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

    class FriendViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView profile_image;
        public TextView display_name,online_status,age;
        public Button  btn_one, btn_two;




        public FriendViewHolder(View itemView) {
            super(itemView);
            profile_image =  itemView.findViewById(R.id.profile_image);
            display_name  =  itemView.findViewById(R.id.name);
            age  =  itemView.findViewById(R.id.age);
            online_status =  itemView.findViewById(R.id.online_status);
            btn_one =  itemView.findViewById(R.id.btn_one);
            btn_two =  itemView.findViewById(R.id.btn_two);

            btn_one.setOnClickListener(this);
            btn_two.setOnClickListener(this);
        }



        @Override
        public void onClick(View v) {

        }
    }
}
