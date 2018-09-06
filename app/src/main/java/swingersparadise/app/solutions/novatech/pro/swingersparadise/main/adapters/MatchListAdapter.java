package swingersparadise.app.solutions.novatech.pro.swingersparadise.main.adapters;

import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import swingersparadise.app.solutions.novatech.pro.swingersparadise.MatchView;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.R;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.entities.Card;


public class MatchListAdapter extends RecyclerView.Adapter<MatchListAdapter.MatchViewHolder> {

    Context c;
    ArrayList<Card> cards;
    SwipeRefreshLayout swiper;

    public MatchListAdapter(Context c, ArrayList<Card> cards, SwipeRefreshLayout swiper) {
        this.c = c;
        this.cards = cards;
        this.swiper = swiper;
    }

    public void addCard(Card card){
        cards.add(card);
        notifyDataSetChanged();
    }

    @Override
    public MatchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.match_card,parent,false);
        MatchListAdapter.MatchViewHolder holder=new MatchViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(final MatchListAdapter.MatchViewHolder holder, int position) {

        //holder.img.setImageResource(movies.get(position).getImage());

        StorageReference ref = FirebaseStorage.getInstance().getReference().child("profiles/" + cards.get(position).getUuid());


        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(final Uri uri) {

                Glide.with(c)
                        .load(uri.toString())
                        .into(holder.img);
                // changes.firePropertyChange("mCard",null, mCard);


            }

        });

        holder.display_name.setText(cards.get(position).getDisplay_name());

        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
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



                /*MatchListAdapter.this.notifyDataSetChanged();
                swiper.setRefreshing(false);*/
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
                                                         MatchListAdapter.this.notifyDataSetChanged();
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


        },3000);
    }

    class MatchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView img;
        public TextView display_name;


        public MatchViewHolder(View itemView) {
            super(itemView);
            img =  itemView.findViewById(R.id.profile_image);
            display_name  =  itemView.findViewById(R.id.display_name);
            itemView.setOnClickListener(this);
        }



        @Override
        public void onClick(View v) {
            Bundle b = new Bundle();
            b.putSerializable("card",  cards.get(getAdapterPosition()));
            c.startActivity(new Intent(c, MatchView.class).putExtras(b));
        }
    }
}
