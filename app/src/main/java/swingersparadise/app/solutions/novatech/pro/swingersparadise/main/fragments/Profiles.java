package swingersparadise.app.solutions.novatech.pro.swingersparadise.main.fragments;

import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.twitter.sdk.android.tweetui.internal.AspectRatioFrameLayout;

import org.json.JSONObject;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import swingersparadise.app.solutions.novatech.pro.swingersparadise.R;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.adapters.AlbumAdapter;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.entities.Card;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.view.TinderCard;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.utils.MeasureUtils;

public class Profiles extends Fragment implements PropertyChangeListener{

    private SwipePlaceHolderView mSwipeView;
    RecyclerView mRecyclerView;
    List<Card> cards = new ArrayList<>();


   // private ImageButton rejectBtn,acceptBtn;
    FirebaseUser user;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.discover,
                container, false);

        mSwipeView = view.findViewById(R.id.swipeView);
        mRecyclerView = view.findViewById(R.id.recycle_view);




        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);




        Point windowSize = MeasureUtils.getDisplaySize(getActivity().getWindowManager());
        int bottomMargin = MeasureUtils.dpToPx(80);


        user = FirebaseAuth.getInstance().getCurrentUser();
        mSwipeView.getBuilder()
                .setDisplayViewCount(6)
                .setSwipeDecor(new SwipeDecor()
                        .setViewWidth(windowSize.x)
                        .setViewHeight(windowSize.y - bottomMargin)
                        .setViewGravity(Gravity.TOP)
                        .setPaddingTop(0)
                        .setRelativeScale(0.01f)
                        .setSwipeInMsgLayoutId(R.layout.swipe_in_message)
                        .setSwipeOutMsgLayoutId(R.layout.swipe_out_message));

        //mSwipeView.onViewAdded();







        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        CardInfos();

    }

    private void CardInfos() {


        DatabaseReference  users_db  =    FirebaseDatabase.getInstance().getReference().child("users");
        users_db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists() && !dataSnapshot.getKey().equals(user.getUid()) ){

                    if(!dataSnapshot.hasChild("connections")) {
                        Map<String, String> data = new HashMap<>();


                        data.put("uuid", dataSnapshot.getKey());
                        data.put("display_name", dataSnapshot.hasChild("display_name") ? dataSnapshot.child("display_name").getValue().toString() : "");
                        data.put("drinking", dataSnapshot.hasChild("drinking") ? dataSnapshot.child("drinking").getValue().toString() : "");
                        data.put("age", dataSnapshot.hasChild("display_name") ? dataSnapshot.child("display_name").getValue().toString() : "");
                        data.put("body_part", dataSnapshot.hasChild("display_name") ? dataSnapshot.child("display_name").getValue().toString() : "");
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
                            mSwipeView.addView( new TinderCard(getActivity(), card, mSwipeView, Profiles.this));

                            //  getActivity().setTitle(card.getDisplay_name());
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    } else {
                        DatabaseReference  ref  =    FirebaseDatabase.getInstance().getReference().child("users").child("connections");

                        final List<String> connections  = new ArrayList<>();
                        ref.child("yep").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot entrySnapshot : dataSnapshot.getChildren()) {
                                        for (DataSnapshot propertySnapshot : entrySnapshot.getChildren()) {
                                            //  Toast.makeText(mContext, propertySnapshot.getValue(String.class), Toast.LENGTH_LONG).show();
                                            connections.add(propertySnapshot.getKey());

                                        }
                                    }
                                    if(!connections.contains(user.getUid())){
                                        Map<String, String> data = new HashMap<>();
                                        data.put("uuid", dataSnapshot.getKey());
                                        data.put("display_name", dataSnapshot.hasChild("display_name") ? dataSnapshot.child("display_name").getValue().toString() : "");
                                        data.put("drinking", dataSnapshot.hasChild("drinking") ? dataSnapshot.child("drinking").getValue().toString() : "");
                                        data.put("age", dataSnapshot.hasChild("display_name") ? dataSnapshot.child("display_name").getValue().toString() : "");
                                        data.put("body_part", dataSnapshot.hasChild("display_name") ? dataSnapshot.child("display_name").getValue().toString() : "");
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
                                            mSwipeView.addView( new TinderCard(getActivity(), card, mSwipeView, Profiles.this));
                                            //  getActivity().setTitle(card.getDisplay_name());
                                        } catch (IllegalAccessException e) {
                                            e.printStackTrace();
                                        }
                                    }




                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });



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


    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        Card c = (Card) propertyChangeEvent.getNewValue();
       // getActivity().setTitle(c.getDisplay_name());
       //mSwipeView.get

        int index = cards.indexOf(c);
        getActivity().setTitle(cards.get(index + 1 ).getDisplay_name());
    }
}
