package swingersparadise.app.solutions.novatech.pro.swingersparadise.main.view;


import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeInState;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;
import com.mindorks.placeholderview.annotations.swipe.SwipeOutState;

import swingersparadise.app.solutions.novatech.pro.swingersparadise.R;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.entities.Card;

@Layout(R.layout.item)
public class TinderCard {

    @View(R.id.profile_image)
    private ImageView profileImageView;


    private Card mCard;
    private Context mContext;
    private SwipePlaceHolderView mSwipeView;

    private FirebaseAuth firebaseAuth;
  //  private DatabaseReference user_db;


    public TinderCard(Context context, Card card, SwipePlaceHolderView swipeView) {
        mContext = context;
        mCard = card;
        mSwipeView = swipeView;
    }

    @Resolve
    private void onResolved() {

        firebaseAuth = FirebaseAuth.getInstance();
        //user_db = FirebaseDatabase.getInstance().getReference().child("users");

        //Glide.with(mContext).load(mProfile.getImageUrl()).into(profileImageView);
        StorageReference ref = FirebaseStorage.getInstance().getReference().child("profiles/" + mCard.getUuid());


        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(final Uri uri) {

                Glide.with(mContext)
                        .load(uri.toString())
                        .into(profileImageView);

            }
        });
    }


    @SwipeOut
    private void onSwipedOut () {
        Log.d("EVENT", "onSwipedOut");
        //  user_db.child(mCard.getOpposite_gender()).child(mCard.getUuid()).child("Connections").child("Nope").child(firebaseAuth.getCurrentUser().getUid()).setValue("true");
        mSwipeView.addView(this);
    }

    @SwipeCancelState
    private void onSwipeCancelState () {
        Log.d("EVENT", "onSwipeCancelState");
    }

    @SwipeIn
    private void onSwipeIn () {
        Log.d("EVENT", "onSwipedIn");
        // user_db.child(mCard.getOpposite_gender()).child(mCard.getUuid()).child("Connections").child("Yep").child(firebaseAuth.getCurrentUser().getUid()).setValue("true");

    }

    @SwipeInState
    private void onSwipeInState () {
        Log.d("EVENT", "onSwipeInState");
    }

    @SwipeOutState
    private void onSwipeOutState () {
        Log.d("EVENT", "onSwipeOutState");
    }

}