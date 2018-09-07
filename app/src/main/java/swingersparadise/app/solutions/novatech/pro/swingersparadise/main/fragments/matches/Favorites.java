package swingersparadise.app.solutions.novatech.pro.swingersparadise.main.fragments.matches;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import swingersparadise.app.solutions.novatech.pro.swingersparadise.R;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.adapters.MatchListAdapter;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.entities.Card;


public class Favorites extends Fragment{


    RecyclerView mRecycler;
    MatchListAdapter matchListAdapter;
    SwipeRefreshLayout swiper;
    ArrayList<Card> cardList = new ArrayList<>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.matches_list,
                container, false);

        mRecycler = view.findViewById(R.id.mRecycler);
        swiper =  view.findViewById(R.id.swiper);
        matchListAdapter = new MatchListAdapter(getActivity(),cardList,swiper, MatchListAdapter.FAVORITES);

        mRecycler.setAdapter(matchListAdapter);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecycler.setLayoutManager(mGridLayoutManager);
        mRecycler.setHasFixedSize(false);

        getFavorites();

        return view;
    }

    public void  getFavorites() {
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
                                    matchListAdapter.addCard(card);
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
