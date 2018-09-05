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
import android.widget.Toast;

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
        final List<String> connections = new ArrayList<>();

        users_db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists() && !dataSnapshot.getKey().equals(user.getUid()) ) {
                    if (dataSnapshot.hasChild("connections")) {
                       // Toast.makeText(getActivity(), dataSnapshot.getKey(), Toast.LENGTH_LONG).show();
                        final String key = dataSnapshot.getKey();

                        DatabaseReference  ref  =    FirebaseDatabase.getInstance().getReference().child("users").child(key).child("connections").child("yep");
                        final List<String> connections  = new ArrayList<>();
                        ref.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                               if(dataSnapshot.getKey().equals(user.getUid())){
                                   Toast.makeText(getActivity(),key, Toast.LENGTH_LONG).show();
                                   connections.add(key);
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

                    Toast.makeText(getActivity(),"" +connections.indexOf(dataSnapshot.getKey()), Toast.LENGTH_LONG).show();


                    if(!connections.contains(dataSnapshot.getKey())){

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
